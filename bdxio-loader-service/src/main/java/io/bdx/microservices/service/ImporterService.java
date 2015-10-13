package io.bdx.microservices.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import io.bdx.microservices.config.Config;
import io.bdx.microservices.model.CommuneAire;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

@Service
public class ImporterService {
    private static final Logger logger = LoggerFactory.getLogger(ImporterService.class);

    private ObjectMapper mapper = new ObjectMapper();
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Inject
    private Config appConfig;

    @Inject
    private Client esClient;

    public void loadData() throws IOException {
        MongoClientURI uri = new MongoClientURI(appConfig.getMongoDbUri());
        MongoClient mongoClient = new MongoClient(uri);

        try {
            DB db = mongoClient.getDB("aoc_aop");
            DBCollection coll = db.getCollection("communes_aires");

            logger.info(coll.getFullName() + " - Nb elements : " + coll.getCount());

            BasicDBObject sort = new BasicDBObject("ci", 1);

            DBCursor cursor = coll.find().sort(sort);

            CommuneAire previousCommuneAire = null;
            while(cursor.hasNext()) {
                DBObject next = cursor.next();
                CommuneAire currentCommuneAire = mapper.readValue(next.toString(), CommuneAire.class);

                saveProduct(currentCommuneAire);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
            esClient.close();
        }
    }

    private void saveProduct(CommuneAire communeAire) throws JsonProcessingException {
        executorService.execute(new FutureTask<IndexResponse>(new EsIndexer(communeAire, communeAire.getCi())));
    }

    private class EsIndexer implements Callable<IndexResponse> {

        private String commune;
        private final String communeId;

        public EsIndexer(CommuneAire commune, String communeId) throws JsonProcessingException {
            this.commune = mapper.writeValueAsString(commune);
            this.communeId = communeId;
        }

        @Override
        public IndexResponse call() throws Exception {
            try {
                return esClient
                        .prepareIndex("aoc_aop", "commune", communeId)
                        .setSource(commune)
                        .execute().actionGet();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Impossible d'indexer la commune " + communeId + " : " + e.getMessage());
                throw e;
            }
        }
    }
}
