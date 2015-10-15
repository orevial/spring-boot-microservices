package io.bdx.microservices.kpi.service;

import javax.inject.Inject;

import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.client.Client;
import org.springframework.stereotype.Service;

@Service
public class KpiSearchService {

	@Inject
	private Client esClient;

	public long getNbImport() throws Exception {

		CountResponse response = esClient.prepareCount("aoc_aop")
				.execute()
				.actionGet();

		return response.getCount();
	}
}
