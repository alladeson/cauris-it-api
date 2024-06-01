/**
 * 
 */
package com.alladeson.caurisit.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.alladeson.caurisit.models.entities.Facture;
import com.alladeson.caurisit.models.entities.Feature;
import com.alladeson.caurisit.models.entities.Parametre;
import com.alladeson.caurisit.models.entities.TypeData;
//import com.alladeson.caurisit.models.entities.User;
import com.alladeson.caurisit.models.paylaods.ReportResponse;
import com.alladeson.caurisit.models.paylaods.StatsPayload;
import com.alladeson.caurisit.models.reports.BilanPeriodiqueData;
import com.alladeson.caurisit.models.reports.InvoiceRecapData;
//import com.alladeson.caurisit.repositories.FacRespRepository;
import com.alladeson.caurisit.repositories.FactureRepository;
import com.alladeson.caurisit.repositories.ParametreRepository;
import com.alladeson.caurisit.repositories.FactureRepository.BilanRecapMontant;
//import com.alladeson.caurisit.repositories.ReglementRepository;
//import com.alladeson.caurisit.repositories.UserRepository;
//import com.alladeson.caurisit.security.core.AccountService;
//import com.alladeson.caurisit.security.entities.Account;
import com.alladeson.caurisit.utils.Tool;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * @author William ALLADE
 *
 */
@Service
public class StatsService {

	@Autowired
	private FactureRepository factureRepos;
	// @Autowired
	// private FacRespRepository frRepos;
	// @Autowired
	// private ReglementRepository reglementRepos;
	@Autowired
	private ParametreRepository paramRepos;
	@Autowired
	private ReportService reportService;
	// @Autowired
	// private UserRepository userRepository;
	//
	// @Autowired
	// private AccountService accountService;

	@Autowired
	private AccessService accessService;

	private static final String BILAN_PERIODIQUE_REPORT_TEMPLATE = "report/bilan-periodique.jrxml";

	// /**
	// * Récupération de l'utilisateur connecté
	// *
	// * @return {@link User}
	// */
	// private User getAuthenticated() {
	// Account account = accountService.getAuthenticated();
	// return userRepository.findByAccount(account)
	// .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
	// "Utilisateur non trouvé"));
	// }

	/**
	 * Récupérer la liste des factures de vente en fonction de la date de
	 * confirmation E-MECeF/DGI de ces dernières
	 * 
	 * @param payload L'objet contenant les dates début et fin de la recherche
	 * @return {@link List<Facture>}
	 */
	public List<Facture> getListFactureVenteByConfirmedDate(StatsPayload payload) {
		// Check permission
		if (!accessService.canReadable(Feature.statsBilanPeriodique))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		if (payload.getDebut() != null && payload.getFin() != null)
			return factureRepos.findAllByTypeGroupeAndDateNotNullAndDateBetween(TypeData.FV, payload.getDebut(),
					payload.getFin());

		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Les dates ne sont pas correctement définies");
	}

	/**
	 * Récupérer la liste des factures d'avoir en fonction de la date de
	 * confirmation E-MECeF/DGI de ces dernières
	 * 
	 * @param payload L'objet contenant les dates début et fin de la recherche
	 * @return {@link List<Facture>}
	 */
	public List<Facture> getListFactureAvoirByConfirmedDate(StatsPayload payload) {
		// Check permission
		if (!accessService.canReadable(Feature.statsBilanPeriodique))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		if (payload.getDebut() != null && payload.getFin() != null)
			return factureRepos.findAllByTypeGroupeAndDateNotNullAndDateBetween(TypeData.FA, payload.getDebut(),
					payload.getFin());

		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les dates ne sont pas correctement définies");
	}

	/**
	 * Récupérer la liste des factures de vente n'ayant pas faire objet de facture
	 * d'avoir en fonction de la date de confirmation E-MECeF/DGI de ces dernières
	 * 
	 * @param payload L'objet contenant les dates début et fin de la recherche
	 * @return {@link List<Facture>}
	 */
	public List<Facture> getListFactureRecapByConfirmedDate(StatsPayload payload) {
		// Check permission
		if (!accessService.canReadable(Feature.statsBilanPeriodique))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		if (payload.getDebut() != null && payload.getFin() != null)
			return factureRepos.findAllRecapByDateNotNullAndDateBetween(TypeData.FV.ordinal(), TypeData.FA.ordinal(),
					payload.getDebut(), payload.getFin());

		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les dates ne sont pas correctement définies");
	}

	/** Bilan des montant **/

	/**
	 * Récupérer du bilan des montants des factures de vente en fonction de la date
	 * de confirmation E-MECeF/DGI de ces dernières
	 * 
	 * @param payload L'objet contenant les dates début et fin de la recherche
	 * @return {@link List<Facture>}
	 */
	public BilanRecapMontant getBilanMonantFactureVenteByConfirmedDate(StatsPayload payload) {
		// Check permission
		if (!accessService.canReadable(Feature.statsBilanPeriodique))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		if (payload.getDebut() != null && payload.getFin() != null)
			return factureRepos.bilanMontantByConfirmedDate(TypeData.FV, payload.getDebut(), payload.getFin());

		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les dates ne sont pas correctement définies");
	}

	/**
	 * Récupérer le bilan des montants des factures d'avoir en fonction de la date
	 * de confirmation E-MECeF/DGI de ces dernières
	 * 
	 * @param payload L'objet contenant les dates début et fin de la recherche
	 * @return {@link List<Facture>}
	 */
	public BilanRecapMontant getBilanMonantFactureAvoirByConfirmedDate(StatsPayload payload) {
		// Check permission
		if (!accessService.canReadable(Feature.statsBilanPeriodique))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		if (payload.getDebut() != null && payload.getFin() != null)
			return factureRepos.bilanMontantByConfirmedDate(TypeData.FA, payload.getDebut(), payload.getFin());

		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les dates ne sont pas correctement définies");
	}

	/**
	 * Récupérer le bilan recapitulatif des montants des factures en fonction de la
	 * date de confirmation E-MECeF/DGI de ces dernières
	 * 
	 * @param payload L'objet contenant les dates début et fin de la recherche
	 * @return {@link List<Facture>}
	 */
	public BilanRecapMontant getBilanMonantRecapByConfirmedDate(StatsPayload payload) {
		// Check permission
		if (!accessService.canReadable(Feature.statsBilanPeriodique))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		if (payload.getDebut() != null && payload.getFin() != null)
			return factureRepos.bilanMontantRecapByConfirmedDate(TypeData.FV, TypeData.FA, payload.getDebut(),
					payload.getFin());

		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les dates ne sont pas correctement définies");
	}

	/**** Impression du bilan périodique ****/

	/**
	 * Générer le bilan périodique
	 * 
	 * @param id
	 * @return {@link ReportResponse}
	 * @throws IOException
	 * @throws JRException
	 */
	public ResponseEntity<byte[]> genererBilanPeriodique(StatsPayload payload) throws IOException, JRException {

		// Check permission
		if (!accessService.canReadable(Feature.statsBilanPeriodique))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération des données du parametres
		Parametre param = paramRepos.findOneParams().orElseThrow(
				() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Votre système n'est pas encore paramètré."));

		// Récupération des nombre des factures
		Integer nbFv = getListFactureVenteByConfirmedDate(payload).size();
		// // Si le nombre des factrue de vente est égale à 0, pas la peine de continuer
		// if(nbFv == 0)
		// throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucune facture
		// trouvée pour cette période");
		// // Sinon, l'exécution continue
		Integer nbFa = getListFactureAvoirByConfirmedDate(payload).size();
		Integer nbFvRecap = getListFactureRecapByConfirmedDate(payload).size();

		// Récupération du recapitulatif des montants
		BilanRecapMontant recapMontantFv = getBilanMonantFactureVenteByConfirmedDate(payload);
		BilanRecapMontant recapMontantFa = getBilanMonantFactureAvoirByConfirmedDate(payload);
		BilanRecapMontant recapMontantRecap = getBilanMonantRecapByConfirmedDate(payload);

		// Instanciation et Formatage des champs de données du rapport
		// Mise à jour des données de la société
		BilanPeriodiqueData bilanData = reportService.setBilanPeriodiqueData(param);
		// Mise à jour des champ du bilan périodique
		String dateDebut = Tool.formatDate(payload.getDebut(), "dd/MM/yyyy HH:mm:ss");
		String dateFin = Tool.formatDate(payload.getFin(), "dd/MM/yyyy HH:mm:ss");
		// Calcul du montant d'imposition
		Long montantImpot = Math.round((payload.getTauxImpot() * recapMontantRecap.getTotal())/100);
		//
		bilanData.setDateDebut(dateDebut);
		bilanData.setDateFin(dateFin);
		bilanData.setNbFv(nbFv);
		bilanData.setNbFa(nbFa);
		bilanData.setNbFvRecap(nbFvRecap);
		bilanData.setTotalFv(recapMontantFv.getTotal());
		bilanData.setTotalFa(recapMontantFa.getTotal() * (-1));
		bilanData.setTotalFvRecap(recapMontantRecap.getTotal());
		bilanData.setTauxImpot(payload.getTauxImpot());
		bilanData.setMontantImpot(montantImpot);
		/** Fin formatage des données du bilan pour l'impression **/

		// Les données de récapitulatif des montants
		// Bilan des montant de facture de vente
		List<InvoiceRecapData> recapsFv = reportService.setBilanPeriodiqueRecapData(recapMontantFv, false);
		// Bilan des montant de facture d'avoir
		List<InvoiceRecapData> recapsFa = reportService.setBilanPeriodiqueRecapData(recapMontantFa, true);
		// Bilan des montant de facture de vente n'ayant point fait objet de facture
		// d'avoir
		List<InvoiceRecapData> recapsFvRecap = reportService.setBilanPeriodiqueRecapData(recapMontantRecap, false);

		// Formatage des données de paramètre du rapport
		// Instanciation de la liste des paramètres
		HashMap<String, Object> map = new HashMap<>();
		map.put("recap_montant_fv", new JRBeanCollectionDataSource(recapsFv));
		map.put("recap_montant_fa", new JRBeanCollectionDataSource(recapsFa));
		map.put("recap_montant_recap", new JRBeanCollectionDataSource(recapsFvRecap));

		// Générer la facture
		String reportName = "bilan_du_" + ((dateDebut.replace(" ", "_")).replace(":", "")).replace("/", "") + "_au_"
				+ ((dateFin.replace(" ", "_")).replace(":", "")).replace("/", "") + ".pdf";
		return reportService.bilanPeriodiqueReport(bilanData, map, BILAN_PERIODIQUE_REPORT_TEMPLATE, reportName);
		// Formatage de données de retour
		// ReportResponse rr = new ReportResponse();
		// rr.setFileName(reportName);
		// return rr;
	}

	/**** Statistiques pour tableau de bord *****/
	/*** Bilan des mois pour un an ***/
	/**
	 * Récupération des données de statistiques du dashboard pour le bilan d'une
	 * année
	 * 
	 * @param payloads
	 * @return
	 */
	public Map<String, List<Long>> bilanDashboard(List<StatsPayload> payloads) {
		if (payloads.isEmpty())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Les données de bilan ne sont pas correctement définies");

		// Initialisation du tableau des bilan
		Map<String, List<Long>> map = new HashMap<>();

		for (StatsPayload statsPayload : payloads) {
			System.out.println("keyword : " + statsPayload.getKeyword());
			// Initalisation du bilan de ce mois (dans statsPayload.getKeyword)
			List<Long> stats = new ArrayList<Long>();
			// Récupération du bilan de ce mois
			stats.add((long) getListFactureVenteByConfirmedDate(statsPayload).size());
			stats.add((long) getListFactureAvoirByConfirmedDate(statsPayload).size());
			stats.add((long) getListFactureRecapByConfirmedDate(statsPayload).size());
			stats.add(getBilanMonantFactureVenteByConfirmedDate(statsPayload).getTotal());
			stats.add(getBilanMonantFactureAvoirByConfirmedDate(statsPayload).getTotal());
			stats.add(getBilanMonantRecapByConfirmedDate(statsPayload).getTotal());
			// Ajout du bilan de ce mois
			map.put(statsPayload.getKeyword(), stats);
		}

		return map;
	}
}
