package io.bdx.microservices.kpi.web;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.bdx.microservices.kpi.service.KpiLoaderService;

@RestController
@RequestMapping("/kpi/")
public class KpiController {

	@Inject
	private KpiLoaderService kpiLoaderService;

	@RequestMapping(method = RequestMethod.GET, value = "/loader/nbImport")
	public Integer getNbImportedData() {
		try {
			return kpiLoaderService.getNbImport();
		} catch (Exception e) {
			throw new RuntimeException("Unable to get KPI: " + e.getMessage(), e);
		}
	}

	
	// TODO count something in ES
	
//	@RequestMapping(method = RequestMethod.GET, value = "/nbIndexedDocuments")
//	public int getNbIndexedDocuments() {
//		return importerService.getNbIndexedDocuments();
//	}

}
