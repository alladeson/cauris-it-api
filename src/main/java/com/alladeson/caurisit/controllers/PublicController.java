package com.alladeson.caurisit.controllers;

import com.alladeson.caurisit.services.FactureService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/public")
public class PublicController {
    @Autowired
    private FactureService factureService;

    @GetMapping("/reservation/{facture}/facture")
    public ResponseEntity<byte[]> genererFacture(@PathVariable(value = "facture") Long factureId) throws IOException, JRException, JRException, IOException {
        return factureService.genererFacture(factureId);
    }

}
