package com.alladeson.caurisit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alladeson.caurisit.models.entities.DetailFacture;
import com.alladeson.caurisit.models.entities.Facture;
import com.alladeson.caurisit.models.paylaods.FacturePayload;
import com.alladeson.caurisit.services.FactureService;

import bj.impots.dgi.emcf.InfoResponseDto;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.util.List;

/**
 * @author William
 *
 */
@RestController
public class FactureController {

	@Autowired
	private FactureService service;

	/**
	 * @param id
	 * @return
	 */
	@GetMapping("/factures/{id}")
	public Facture get(@PathVariable(value = "id") Long id) {
		return service.get(id);
	}

	/**
	 * @param clientId
	 * @return
	 */
	@GetMapping("/factures/client/{id}")
	public Facture getFactureValidFalseByClient(@PathVariable(value = "id") Long clientId) {
		return service.getFactureValidFalseByClient(clientId);
	}

	/**
	 * @param clientId
	 * @return
	 */
	@GetMapping("/factures/client/{id}/list")
	public List<Facture> getFactureValidTrueByClient(@PathVariable(value = "id") Long clientId) {
		return service.getFactureValidTrueByClient(clientId);
	}

	/**
	 * @param search
	 * @return
	 */
	@GetMapping("/factures")
	public List<Facture> getAll(@RequestParam(name = "search", required = false, defaultValue = "") String search) {
		return service.getAll(search);
	}

//	/**
//	 * @param detailId
//	 * @param detail
//	 * @return
//	 */
//	@PutMapping("/factures/{fId}/detail/{dtId}")
//	public Facture updateDetailFacture(@PathVariable(value = "fId") Long factureId, @PathVariable(value = "dtId") Long detailId, @RequestBody DetailFacture detail) {
//		return service.updateDetailFacture(factureId, detailId, detail);
//	}

	/**
	 * @param id
	 * @return
	 */
	@PutMapping("/factures/{fId}/detail/{dtId}/valider")
	public Facture validerDetailFacture(@PathVariable(value = "fId") Long factureId,
			@PathVariable(value = "dtId") Long detailId) {
		return service.validerDetailFacture(factureId, detailId);
	}

	/**
	 * @param detailId
	 * @return
	 */
	@DeleteMapping("/factures/{fId}/detail/{dtId}")
	public Facture deleteDetailFacture(@PathVariable(value = "fId") Long factureId,
			@PathVariable(value = "dtId") Long detailId) {
		return service.deleteDetailFacture(factureId, detailId);
	}

	/**
	 * @param id
	 * @return
	 */
	@PutMapping("/factures/{id}/valider")
	public Facture validerFacture(@PathVariable(value = "id") Long id, @RequestBody FacturePayload payload) {
		return service.validerFacture(id, payload);
	}

	/**
	 * @return
	 */
	@DeleteMapping("/factures/{fId}")
	public boolean deleteFacture(@PathVariable(value = "fId") Long factureId) {
		return service.deleteFacture(factureId);
	}

	/**
	 * @param factureId
	 * @param detailId
	 * @return
	 * @see com.alladeson.caurisit.services.FactureService#getDetailFacture(java.lang.Long,
	 *      java.lang.Long)
	 */
	@GetMapping("/factures/{fId}/detail/{dtId}")
	public DetailFacture getDetailFacture(@PathVariable(value = "fId") Long factureId,
			@PathVariable(value = "dtId") Long detailId) {
		return service.getDetailFacture(factureId, detailId);
	}

	/**
	 * @param factureId
	 * @param articleId
	 * @return
	 * @see com.alladeson.caurisit.services.FactureService#getDetailFactureByArticle(java.lang.Long,
	 *      java.lang.Long)
	 */
	@GetMapping("/factures/{fId}/article/{articleId}")
	public DetailFacture getDetailFactureByArticle(@PathVariable(value = "fId") Long factureId,
			@PathVariable(value = "articleId") Long articleId) {
		return service.getDetailFactureByArticle(factureId, articleId);
	}

	/**
	 * @param objet
	 * @return
	 */
	@PostMapping("/factures/client/{clientId}/article/{articleId}")
	public Facture ajouterDetailFacture(@PathVariable(value = "clientId") Long clientId,
			@PathVariable(value = "articleId") Long articleId, @RequestBody DetailFacture objet) {
		return service.ajouterDetailFacture(clientId, articleId, objet);
	}

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.FactureService#getStatusInfoMcef()
	 */
	@GetMapping("/factures/emcef/api/info/status")
	public InfoResponseDto getStatusInfoMcef() {
		return service.getStatusInfoMcef();
	}

	/**
	 * @param id
	 * @return
	 * @throws IOException
	 * @throws JRException
	 * @see com.alladeson.caurisit.services.FactureService#genererFacture(java.lang.Long)
	 */
	@GetMapping("/factures/{id}/imprimer")
	public ResponseEntity<byte[]> genererFacture(@PathVariable(value = "id") Long factureId) throws IOException, JRException {
		return service.genererFacture(factureId);
	}

	/**
	 * @param typeId
	 * @param factureId
	 * @return
	 * @see com.alladeson.caurisit.services.FactureService#createFactureAvoir(java.lang.Long, java.lang.Long)
	 */
	@PostMapping("/facture-avoir/type/{tId}/facture-vente/{id}")
	public Facture setFactureAvoir(@PathVariable(value = "tId") Long typeId, @PathVariable(value = "id") Long factureId) {
		return service.createFactureAvoir(typeId, factureId);
	}

	/**
	 * @param id
	 * @return
	 * @see com.alladeson.caurisit.services.FactureService#validerFactureAvoir(java.lang.Long)
	 */
	@PutMapping("/facture-avoir/{id}/valider")
	public Facture validerFactureAvoir(@PathVariable(value = "id") Long id) {
		return service.validerFactureAvoir(id);
	}
}
