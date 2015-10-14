package io.bdx.microservices.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bdx.microservices.config.Config;
import io.bdx.microservices.model.CityResult;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Inject
    private Config config;

    @Inject
    private Client esClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(method = RequestMethod.GET, value = "/city/{city}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CityResult> searchByCity(@PathVariable String city) throws Exception {
        SearchResponse response = esClient.prepareSearch("aoc_aop")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchQuery("commune", city))
                .execute()
                .actionGet();

        List<CityResult> cityResults = new ArrayList<>();
        if(response != null) {
            for(SearchHit hit : response.getHits().getHits()) {
                CityResult cityResult = objectMapper.readValue(hit.getSourceAsString(), CityResult.class);
                cityResults.add(cityResult);
            }
        }
        return cityResults;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/geo/{aireGeo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> searchByAireGeo(@PathVariable String aireGeo) throws Exception {
        SearchResponse response = esClient.prepareSearch("aoc_aop")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchPhraseQuery("aire_geo", aireGeo))
                .execute()
                .actionGet();

        List<String> communes = new ArrayList<String>();
        for(SearchHit hit : response.getHits().getHits()) {
            communes.add(hit.getSource().get("commune") + " - (" + hit.getSource().get("departement") + ")");
        }
        return communes;
    }
}