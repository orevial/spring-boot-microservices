package io.bdx.microservices.controller;

import io.bdx.microservices.service.ImporterService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("/")
public class ImportController {

    @Inject
    private ImporterService importerService;

    @RequestMapping(method = RequestMethod.GET, value = "/importData")
    public String importData() {
        try {
            importerService.loadData();
            return "Data imported...";
        } catch (Exception e) {
            return "Unable to load data : " + e.getCause().getMessage();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/nbIndexedDocuments")
    public int getNbIndexedDocuments() {
        return importerService.getNbIndexedDocuments();
    }
}
