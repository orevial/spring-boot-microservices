package io.bdx.microservices.service;

import java.io.IOException;

import javax.inject.Inject;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import io.bdx.microservices.config.Config;
import io.bdx.microservices.model.ESAppellationDocument;
import io.bdx.microservices.model.ESCommuneDocument;
import io.bdx.microservices.model.MongoAireProduit;
import io.bdx.microservices.model.MongoCommuneAire;

@Service
public class ImporterService {
    private static final Logger logger = LoggerFactory.getLogger(ImporterService.class);

    public int nbIndexedDocuments = 0;

    private ObjectMapper mapper = new ObjectMapper();

    @Inject
    private Config appConfig;

    @Inject
    private Client esClient;
    private BulkProcessor bulkProcessor;

    public void loadData() throws IOException {
        MongoClientURI uri = new MongoClientURI(appConfig.getMongoDbUri());
        MongoClient mongoClient = new MongoClient(uri);

        try {
            DB db = mongoClient.getDB("aoc_aop");
            DBCollection communesAiresCollection = db.getCollection("communes_aires");
            DBCollection airesProduitsCollection = db.getCollection("aires_produits");

            logger.info(communesAiresCollection.getFullName() + " - Nb elements to load : " + communesAiresCollection.getCount());

            BasicDBObject sort = new BasicDBObject("ci", 1);
            DBCursor communesCursor = communesAiresCollection.find().sort(sort);
//            nbIndexedDocuments = 0;
            initializeBulkProcessor();

            ESCommuneDocument previousCommune = null;
            ESCommuneDocument currentCommune = null;
            boolean commitPreviousCommune = false;
            while(communesCursor.hasNext()) {
                DBObject next = communesCursor.next();
                MongoCommuneAire currentMongoCommuneAire = mapper.readValue(next.toString(), MongoCommuneAire.class);

                if(currentCommune == null || !previousCommune.getCi().equals(currentMongoCommuneAire.getCi())) {
                    currentCommune = new ESCommuneDocument(
                            currentMongoCommuneAire.getArt(),
                            currentMongoCommuneAire.getCi(),
                            currentMongoCommuneAire.getDepartement(),
                            currentMongoCommuneAire.getCommune()
                    );
                    if(previousCommune != null) {
                        commitPreviousCommune = true;
                    }
                }

                try {
                    // Fetch all products from other Mongo collections
                    BasicDBObject productsSort = new BasicDBObject("produit", 1);
                    BasicDBObject productsQuery = new BasicDBObject("ida", Integer.valueOf(currentMongoCommuneAire.getIda()));
                    DBCursor productsCursor = airesProduitsCollection.find(productsQuery).sort(productsSort);

                    while(productsCursor.hasNext()) {
                        DBObject nextProduct = productsCursor.next();
                        MongoAireProduit produit = mapper.readValue(nextProduct.toString(), MongoAireProduit.class);
                        currentCommune.addAppellations(new ESAppellationDocument(
                                produit.getIda(),
                                produit.getAireGeo(),
                                produit.getProduit()
                        ));
                    }
                } catch (NumberFormatException e) {
                    logger.info("Number format exception for : {}", currentMongoCommuneAire.getIda());
                } catch (IOException e) {
                    logger.info("IO exception... {}");
                }

                if(commitPreviousCommune) {
                    commitPreviousCommune = false;
                    bulkProcessor.add(getIndexRequest(previousCommune));
                }
                previousCommune = currentCommune;
            }
            bulkProcessor.flush();
            bulkProcessor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
            esClient.close();
        }
    }

    private IndexRequest getIndexRequest(ESCommuneDocument esCommune) {
        try {
            return new IndexRequest("aoc_aop", "commune", esCommune.getCi())
                    .source(mapper.writeValueAsString(esCommune));
        } catch (JsonProcessingException e) {
            logger.error("Error while processing commune with id {}", esCommune.getCi());
        }
        return null;
    }

    private void initializeBulkProcessor() {
        bulkProcessor = BulkProcessor.builder(
                esClient,
                new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(long executionId,
                                           BulkRequest request) {
                        logger.info("Before bulk {} ", executionId);
                    }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          BulkResponse response) {

                        int newlyIndexedDocuments = response.getItems().length;
                        nbIndexedDocuments += newlyIndexedDocuments;
                        logger.info("*** TOTAL INDEXED : {} ***", nbIndexedDocuments);
                    }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          Throwable failure) {
                        logger.info("After bulk with failure...", failure);
                    }
                })
                .setBulkActions(50)
                .setConcurrentRequests(1)
                .build();
    }

    public int getNbIndexedDocuments() {
        return nbIndexedDocuments;
    }
}
