package io.bdx.microservices.search.insert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import io.bdx.microservices.search.insert.model.CommuneAire;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

@EnableAutoConfiguration
@ComponentScan("io.bdx.microservices")
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private static ObjectMapper mapper = new ObjectMapper();
    public static final Properties props = new Properties();
    private static Client esClient;
    private static ExecutorService executorService = Executors.newFixedThreadPool(8);

    static {
        try {
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("search-insert.properties"));
        } catch (IOException e) {
            logger.error("Impossible de charger les propriétés!", e);
        }
    }
    private static int NB_OFFERS = Integer.valueOf(props.getProperty("nboffers", "100"));

    public static boolean storeGeolocatedOffersSeparately = Boolean.valueOf(props
            .getProperty("index.geolocated.offer.separately"));


    public static void main(String[] args) throws Exception {
//        SpringApplication.run(Application.class, args);

        loadData();
    }

    public static void loadData() throws IOException {
        MongoClientURI uri = new MongoClientURI(props.getProperty("mongodb.uri"));

        MongoClient mongoClient = new MongoClient(uri);

        esClient = new TransportClient().addTransportAddress(new InetSocketTransportAddress(
                props.getProperty("es.url"),
                Integer.valueOf(props.getProperty("es.port"))));
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

    private static void saveProduct(CommuneAire communeAire) throws JsonProcessingException {
        executorService.execute(new FutureTask<IndexResponse>(new EsIndexer(communeAire, communeAire.getCi())));
    }

    private static class EsIndexer implements Callable<IndexResponse> {

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
