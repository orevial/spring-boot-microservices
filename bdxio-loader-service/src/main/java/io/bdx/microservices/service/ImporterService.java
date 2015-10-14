package io.bdx.microservices.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import io.bdx.microservices.config.Config;
import io.bdx.microservices.model.ESAppellationDocument;
import io.bdx.microservices.model.ESCommuneDocument;
import io.bdx.microservices.model.MongoCommuneAire;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ImporterService {
    private static final Logger logger = LoggerFactory.getLogger(ImporterService.class);

    public int nbIndexedDocuments = 0;

    private ObjectMapper mapper = new ObjectMapper();
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

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

            logger.info(communesAiresCollection.getFullName() + " - Nb elements to load : " + communesAiresCollection.getCount());

            BasicDBObject sort = new BasicDBObject("ci", 1);
            DBCursor cursor = communesAiresCollection.find().sort(sort);
            nbIndexedDocuments = 0;
            initializeBulkProcessor();

            ESCommuneDocument previousCommune = null;
            ESCommuneDocument currentCommune = null;
            boolean commitPreviousCommune = false;
            while(cursor.hasNext()) {
                DBObject next = cursor.next();
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

                currentCommune.addAppellations(new ESAppellationDocument(
                        currentMongoCommuneAire.getAireGeo(),
                        currentMongoCommuneAire.getIda()
                ));

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
                .setBulkActions(200)
                .setConcurrentRequests(1)
                .build();
    }

    public int getNbIndexedDocuments() {
        return nbIndexedDocuments;
    }
}
