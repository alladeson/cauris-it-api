/**
 * 
 */
package com.alladeson.caurisit.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alladeson.caurisit.models.entities.Facture;
import com.alladeson.caurisit.models.paylaods.StatsPayload;
import com.alladeson.caurisit.repositories.FactureRepository.BilanRecapMontant;
import com.alladeson.caurisit.services.StatsService;

import net.sf.jasperreports.engine.JRException;

/**
 * @author allad
 *
 */
@RestController
public class StatsController {

	@Autowired
	private StatsService statsService;

	/**
	 * @param payload
	 * @return
	 * @see com.alladeson.caurisit.services.StatsService#getListFactureVenteByConfirmedDate(com.alladeson.caurisit.models.paylaods.StatsPayload)
	 */
	@GetMapping("/stats/bilan-periodique/facture-vente")
	public List<Facture> getListFactureVenteByConfirmedDate(@RequestBody StatsPayload payload) {
		return statsService.getListFactureVenteByConfirmedDate(payload);
	}

	/**
	 * @param payload
	 * @return
	 * @see com.alladeson.caurisit.services.StatsService#getListFactureAvoirByConfirmedDate(com.alladeson.caurisit.models.paylaods.StatsPayload)
	 */
	@GetMapping("/stats/bilan-periodique/facture-avoir")
	public List<Facture> getListFactureAvoirByConfirmedDate(@RequestBody StatsPayload payload) {
		return statsService.getListFactureAvoirByConfirmedDate(payload);
	}

	/**
	 * @param payload
	 * @return
	 * @see com.alladeson.caurisit.services.StatsService#getListFactureRecapByConfirmedDate(com.alladeson.caurisit.models.paylaods.StatsPayload)
	 */
	@GetMapping("/stats/bilan-periodique/facture-recap")
	public List<Facture> getListFactureRecapByConfirmedDate(@RequestBody StatsPayload payload) {
		return statsService.getListFactureRecapByConfirmedDate(payload);
	}

	/**
	 * @param payload
	 * @return
	 * @see com.alladeson.caurisit.services.StatsService#getBilanMonantFactureVenteByConfirmedDate(com.alladeson.caurisit.models.paylaods.StatsPayload)
	 */
	@GetMapping("/stats/bilan-periodique/facture-vente/montant")
	public BilanRecapMontant getBilanMonantFactureVenteByConfirmedDate(@RequestBody StatsPayload payload) {
		return statsService.getBilanMonantFactureVenteByConfirmedDate(payload);
	}

	/**
	 * @param payload
	 * @return
	 * @see com.alladeson.caurisit.services.StatsService#getBilanMonantFactureAvoirByConfirmedDate(com.alladeson.caurisit.models.paylaods.StatsPayload)
	 */
	@GetMapping("/stats/bilan-periodique/facture-avoir/montant")
	public BilanRecapMontant getBilanMonantFactureAvoirByConfirmedDate(@RequestBody StatsPayload payload) {
		return statsService.getBilanMonantFactureAvoirByConfirmedDate(payload);
	}

	/**
	 * @param payload
	 * @return
	 * @see com.alladeson.caurisit.services.StatsService#getBilanMonantRecapByConfirmedDate(com.alladeson.caurisit.models.paylaods.StatsPayload)
	 */
	@GetMapping("/stats/bilan-periodique/facture-recap/montant")
	public BilanRecapMontant getBilanMonantRecapByConfirmedDate(@RequestBody StatsPayload payload) {
		return statsService.getBilanMonantRecapByConfirmedDate(payload);
	}

	/**
	 * @param payload
	 * @return
	 * @throws IOException
	 * @throws JRException
	 * @see com.alladeson.caurisit.services.StatsService#genererBilanPeriodique(com.alladeson.caurisit.models.paylaods.StatsPayload)
	 */
	@GetMapping("/stats/bilan-periodique/report")
	public ResponseEntity<byte[]> genererBilanPeriodique(@RequestBody StatsPayload payload) throws IOException, JRException {
		return statsService.genererBilanPeriodique(payload);
	}

	/**
	 * @param payloads
	 * @return
	 * @see com.alladeson.caurisit.services.StatsService#bilanDashboard(java.util.List)
	 */
	@GetMapping("/stats/bilan-periodique/dashboard")
	public Map<String, List<Long>> bilanDashboard(@RequestBody List<StatsPayload> payloads) {
		return statsService.bilanDashboard(payloads);
	}	
	
}
