/**
 * 
 */
package com.alladeson.caurisit.services;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.alladeson.caurisit.repositories.*;
import com.alladeson.caurisit.security.core.AccountService;
import com.alladeson.caurisit.security.entities.Account;
import com.alladeson.caurisit.utils.Tool;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.alladeson.caurisit.models.entities.Article;
import com.alladeson.caurisit.models.entities.Client;
import com.alladeson.caurisit.models.entities.DetailFacture;
import com.alladeson.caurisit.models.entities.Facture;
import com.alladeson.caurisit.models.entities.FactureFinalisationDgi;
import com.alladeson.caurisit.models.entities.FactureResponseDgi;
import com.alladeson.caurisit.models.entities.Feature;
import com.alladeson.caurisit.models.entities.MouvementArticle;
import com.alladeson.caurisit.models.entities.Operation;
import com.alladeson.caurisit.models.entities.Parametre;
import com.alladeson.caurisit.models.entities.ReglementFacture;
import com.alladeson.caurisit.models.entities.Remise;
import com.alladeson.caurisit.models.entities.Taxe;
import com.alladeson.caurisit.models.entities.TaxeGroups;
import com.alladeson.caurisit.models.entities.TaxeSpecifique;
import com.alladeson.caurisit.models.entities.TypeData;
import com.alladeson.caurisit.models.entities.TypeFacture;
import com.alladeson.caurisit.models.entities.TypeFactureEnum;
import com.alladeson.caurisit.models.entities.TypeSystem;
import com.alladeson.caurisit.models.entities.User;
import com.alladeson.caurisit.models.paylaods.FactureAutocomplete;
import com.alladeson.caurisit.models.paylaods.ReglementPayload;
import com.alladeson.caurisit.models.paylaods.StatsPayload;
import com.alladeson.caurisit.models.reports.ClientData;
import com.alladeson.caurisit.models.reports.InvoiceData;
import com.alladeson.caurisit.models.reports.InvoiceDetailData;
import com.alladeson.caurisit.models.reports.InvoicePayement;
import com.alladeson.caurisit.models.reports.InvoiceRecapData;

import bj.impots.dgi.*;
import bj.impots.dgi.auth.ApiKeyAuth;
import bj.impots.dgi.emcf.*;

/**
 * @author William ALLADE
 *
 */
@Service
public class FactureService {

	@Autowired
	private FactureRepository repository;
	@Autowired
	private DetailFactureRepository dfRepos;
	@Autowired
	private ClientRepository clientRepos;
	@Autowired
	private ArticleRepository articleRepos;
	@Autowired
	private TaxeRepository taxeRepos;
	@Autowired
	private TSRepository tsRepos;
	@Autowired
	private ParametreRepository paramRepos;
	@Autowired
	private FacRespRepository frRepos;
	@Autowired
	private FacFinRepository ffRepos;
	@Autowired
	private ReglementRepository reglementRepos;
	@Autowired
	private TypeFactureRepository tfRepos;
	@Autowired
	private TypePaiementRepository tpRepos;
	@Autowired
	private RemiseRepository remiseRepos;
	@Autowired
	private ReportService reportService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AccountService accountService;

	@Autowired
	private AccessService accessService;
	@Autowired
	private AuditService auditService;
	@Autowired
	private Tool tool;
	@Autowired
	private MouvementArticleRepository mvtArticleRepos;

	private static final String INVOICE_REPORT_BASE_NAME = "facture-";
	private static final String INVOICE_REPORT_TEMPLATE_FV = "facture-vente.jrxml";
	private static final String INVOICE_REPORT_TEMPLATE_FA = "facture-avoir.jrxml";
	private static final String INVOICE_REPORT_TEMPLATE_FV_REMISE = "facture-vente-remise.jrxml";
	private static final String INVOICE_REPORT_TEMPLATE_FA_REMISE = "facture-avoir-remise.jrxml";
	private static final String INVOICE_MAIL_TRANSFERT_TEMPLATE = "rapport-de-facture-validee";

	/**
	 * Récupération de l'utilisateur connecté
	 * 
	 * @return {@link User}
	 */
	private User getAuthenticated() {
		Account account = accountService.getAuthenticated();
		return userRepository.findByAccount(account)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
	}

	/**
	 * Récupère une facture dont l'identifiant est renseigné
	 * 
	 * @param id L'identifiant de la facture
	 * @return {@link Facture}
	 */
	public Facture get(Long id) {
		// Check permission
		if (!accessService.canReadable(Feature.facturationListe))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		if (id.equals((long) 0)) {
			return new Facture();
		}
		Optional<Facture> optional = repository.findById(id);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture non trouvée");
		return optional.get();
	}

	/**
	 * Récupérer une facture par sa référence
	 * 
	 * @param reference La référence de la facture
	 * @return {@link Facture}
	 */
	public Facture getFactureByReference(String reference) {
		// Check permission
		if (!accessService.canReadable(Feature.facturationListe))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Optional<Facture> optional = repository.findByReferenceAndConfirmTrue(reference);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture non trouvée");
		return optional.get();
	}

	/**
	 * Récupère une facture non validée d'un client
	 * 
	 * @param clientId L'identifiant du client
	 * @return {@link Facture}
	 */
	public Facture getFactureValidFalseByClient(Long clientId, Long typeId) {
		// Check permission
		if (!accessService.canReadable(Feature.facturationListe))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		if (clientId.equals(0l) || typeId.equals(0l)) {
			return new Facture();
		}
		// Récupération du client
		Client client = clientRepos.findById(clientId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client non trouvé"));
		// Récupération du type de la facture
		TypeFacture tf = tfRepos.findById(typeId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le type de la facture non trouvé"));
		// Recupération de la facture du
		Facture facture = repository.findByClientAndTypeAndValidFalse(client, tf);
		if (facture == null)
			// throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture non
			// trouvée");
			return new Facture();
		// Renvoie de la facture
		return facture;
	}

	/**
	 * Récupère la liste des factures validées d'un client
	 * 
	 * @param clientId L'identifiant du client
	 * @return {@link List<Facture>}
	 */
	public List<Facture> getFactureValidTrueByClient(Long clientId) {
		// Check permission
		if (!accessService.canReadable(Feature.facturationListe))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Optional<Client> clientOptional = clientRepos.findById(clientId);
		if (clientOptional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client non trouvé");

		// Recupération de la liste des facture validées (payée) pour le client
		return repository.findByClientIdAndValidTrue(clientId);

	}

	/**
	 * Récupère la liste des facture en fonction d'un thème de recherche
	 * 
	 * @param search Le thème de la recherche
	 * @return {@link List<Facture>}
	 */
	public List<Facture> getAll(String search) {
		// Check permission
		if (!accessService.canReadable(Feature.facturationListe))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// System.out.println(search);
		// return repository.findByClientNameContaining(search);
		if (search.equals("vide"))
			return new ArrayList<Facture>();
		// Sinon renvoie toutes les factures)
		return repository.findAll();
	}

	/**
	 * Récupérer FactureAutocomplete, utile lors de la création de la facture
	 * d'avoir pour récupération de vente correspondante
	 * 
	 * @param typeId Le type de la facture d'avoir
	 * @param search Le début de la référence servant de recherche
	 * @return {@link List<FactureAutocomplete>}
	 */
	public List<FactureAutocomplete> getListFactureAutocomplete(Long typeId, String search) {
		// Check permission
		if (!accessService.canReadable(Feature.facturationListe))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération du type de la facture d'avoir
		TypeFacture tf = tfRepos.findByIdAndGroupe(typeId, TypeData.FA).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le type de la facture non trouvé"));

		return repository.getFactureForAutocomplete(tf.getOrigine().getId(), typeId, search);
	}

	/**
	 * Récupération du detail de la facture par son identifiant
	 * 
	 * @param factureId
	 * @param detailId
	 * @return {@link DetailFacture}
	 */
	public DetailFacture getDetailFacture(Long factureId, Long detailId) {
		// Check permission
		if (!accessService.canReadable(Feature.facturationListe))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		if (detailId.equals((long) 0) || factureId.equals((long) 0)) {
			return new DetailFacture();
		}

		Optional<DetailFacture> optional = dfRepos.findByFactureIdAndId(factureId, detailId);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ligne Facture non trouvée");
		return optional.get();
	}

	/**
	 * Recupération du detail de la facture en fonction de l'article
	 * 
	 * @param factureId
	 * @param articleId
	 * @return
	 */
	public DetailFacture getDetailFactureByArticle(Long factureId, Long articleId) {
		// Check permission
		if (!accessService.canReadable(Feature.facturationListe))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		if (factureId.equals((long) 0) || articleId.equals((long) 0)) {
			return new DetailFacture();
		}

		// Tentative de récupération du detail pour cet article
		Optional<DetailFacture> optional = dfRepos.findByFactureIdAndArticleId(factureId, articleId);
		if (optional.isEmpty())
			// throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ligne Facture non
			// trouvée");
			return null;

		return optional.get();
	}

	/**
	 * Ajout et mise à jour d'une ligne de la facture
	 * 
	 * @param clientId
	 * @param articleId
	 * @param detailPayload
	 * @return {@link Facture}
	 */
	public Facture ajouterDetailFacture(Long clientId, Long articleId, DetailFacture detailPayload) {
		// Check permission
		if (!accessService.canWritable(Feature.facturationFV))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération du client
		Client client = clientRepos.findById(clientId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client non trouvé"));
		// Récupération du client
		Article article = articleRepos.findById(articleId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article non trouvé"));

		// Récupération du type de la facture
		TypeFacture tf = null;
		if (detailPayload.getTfId() != null) {
			tf = tfRepos.findById(detailPayload.getTfId()).orElseThrow(
					() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le type de facture non trouvé"));
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le type de facture non défini");
		}

		// Gestion audit : valeurAvant
		String valAvant = null;

		// Récupération de la facture non validée du client en fonction du type de
		// facture et du client
		Facture facture = repository.findByClientAndTypeAndValidFalse(client, tf);
		//
		if (facture == null) {
			valAvant = null;
			facture = new Facture();
			facture.setClient(client);
			facture.setType(tf);
			facture = repository.save(facture);
		} else {
			valAvant = tool.toJson(facture);
		}

		// Instanciation d'un detailFacture
		DetailFacture detail = new DetailFacture();

		// Tentative de récupération du detail pour cet article
		Optional<DetailFacture> optionalDf = dfRepos.findByFactureIdAndArticleId(facture.getId(), articleId);
		// Si le detail de la facture existe, on le récupère
		if (optionalDf.isPresent()) {
			detail = optionalDf.get();
		}
		// Si le détail de la facture n'existais pas, on met à jour le nouveau détail
		else {
			detail.setFacture(facture);
			detail.setArticle(article);
			detail = dfRepos.save(detail);
			facture.getDetails().add(detail);
		}

		// Mise à jour des champs du detail facture
		detail = setDetailFacture(detailPayload, article, detail);

		// Enregistrement du detail de la facture
		detail = dfRepos.save(detail);
		// Calcule et mise à jour des montants de la facture
		// Renvoie de la facture
		facture = calculer(facture);
		// Gestion audit : valeurApres
		String valApres = tool.toJson(facture);
		// Enregisterment de l'audit
		auditService.traceChange(valAvant == null ? Operation.FACTURATION_CREATE : Operation.FACTURATION_UPDATE,
				valAvant, valApres);
		// Renvoie de la facture
		return facture;
	}

	/**
	 * @param detailPayload
	 * @param detail
	 * @return {@link DetailFacture}
	 */
	private DetailFacture setTaxeSpecifique(DetailFacture detailPayload, DetailFacture detail) {
		// Récupération d'une probable ancienne taxe spécifique
		var ts = detail.getTs();
		// Si la taxe spécifique est fourni alors la récupérer pour l'enregistrer
		if (detailPayload.getTaxeSpecifique() != null) {
			// Mise à jour des champs de TS
			detail.setTaxeSpecifique(detailPayload.getTaxeSpecifique());
			detail.setTsTtc(
					(double) Math.round((detail.getTaxeSpecifique() * (100 + detail.getTaxe().getValeur())) / 100));
			// Gestion de la taxe spécifique
			// Si l'ancienne TS est null, instancier une nouvelle
			if (ts == null)
				ts = new TaxeSpecifique();
			// Récupération du nom de la taxe spécifique depuis l'article
			ts.setName(detailPayload.getTsName());
			// Mise à jour des autres champs
			ts.setQuantite(detail.getQuantite());
			ts.setTsTotalHt(detail.getTaxeSpecifique());
			ts.setTsUnitaire(ts.getTsTotalHt() / ts.getQuantite());
			ts.setTaxe(detail.getTaxe());
			// tsTotal = tsTotalHt * (1+taux)
			ts.setTsTotal((double) Math.round((ts.getTsTotalHt() * (100 + ts.getTaxe().getValeur())) / 100));
			// Prix Unitaire T.T.C
			ts.setTsUnitaireTtc(ts.getTsTotal() / ts.getQuantite());
			ts = tsRepos.save(ts);
			detail.setTs(ts);
		} else {
			detail.setTaxeSpecifique(null);
			detail.setTs(null);
			detail.setTsTtc(null);
			// Si l'ancienne ts n'est pas null, alors la supprimer
			if (ts != null)
				tsRepos.delete(ts);
		}
		return detail;
	}

	/**
	 * @param detailPayload
	 * @param article
	 * @param detail
	 * @return {@link DetailFacture}
	 */
	private DetailFacture setDetailFacture(DetailFacture detailPayload, Article article, DetailFacture detail) {
		// Récupération et mise à jour de la taxe
		if (detailPayload.getTaxeId() == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Taxe non précisée");
		Taxe taxe = taxeRepos.findById(detailPayload.getTaxeId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Taxe non trouvée"));
		detail.setTaxe(taxe);
		// Mise à jour des autres champs du detail
		detail.setName(article.getDesignation());
		detail.setPrixUnitaire(detailPayload.getPrixUnitaire());
		// Le prix unitaire HT = prix unitaire ttc / (1+taux)
		detail.setPrixUht((detail.getPrixUnitaire() * 100) / (100 + detail.getTaxe().getValeur()));
		detail.setQuantite(detailPayload.getQuantite());
		detail.setUnite(detailPayload.getUnite());
		detail.setMontantHt((double) Math.round(detail.getQuantite() * detail.getPrixUht()));
		detail.setMontantTva((double) Math.round((detail.getMontantHt() * detail.getTaxe().getValeur()) / 100));
		detail.setMontantTtc((double) Math.round(detail.getPrixUnitaire() * detail.getQuantite()));
		// Gestion des remise
		detail.setRemise(detailPayload.isRemise());
		// Récupération d'une probable ancien remise
		var remise = detail.getDiscount();
		if (detail.isRemise()) {
			// Si l'ancienne remise est nulle, on instancie une nouvelle
			if (remise == null)
				remise = new Remise();
			remise.setTaux(detailPayload.getTaux());
			remise.setOriginalPrice(detailPayload.getOriginalPrice());
			remise.setPriceModification(detailPayload.getPriceModification());
			detail.setDiscount(remiseRepos.save(remise));
		} else {
			detail.setDiscount(null);
			// Suppression de la probable ancienne remise si elle n'est pas null
			if (remise != null)
				remiseRepos.delete(remise);
		}

		// Mise à jour de la taxe spécifique si la taxe n'est pas du groupe C :
		// Exportation de produits taxable
		if (taxe.getGroupe() != TaxeGroups.C)
			detail = setTaxeSpecifique(detailPayload, detail);

		// Renvoie du detailFacture
		return detail;
	}

	/**
	 * Calcul des montants de la facture
	 * 
	 * @param facture
	 * @return {@link Facture}
	 */
	private Facture calculer(Facture facture) {
		// Mise à jour du montantHT
		facture.setMontantHt(repository.calcMontantHt(facture));
		// Mise à jour du montantHT
		var taxeGroupe1 = new ArrayList<TaxeGroups>();
		taxeGroupe1.add(TaxeGroups.C);
		taxeGroupe1.add(TaxeGroups.D);
		taxeGroupe1.add(TaxeGroups.E);
		taxeGroupe1.add(TaxeGroups.F);
		var taxeGroupe2 = new ArrayList<TaxeGroups>();
		taxeGroupe2.add(TaxeGroups.D);
		taxeGroupe2.add(TaxeGroups.E);
		taxeGroupe2.add(TaxeGroups.F);
		facture.setMontantHtAib(repository.calcMontantHtForAib(facture, taxeGroupe1)
				+ repository.calcMontantTsHtForAib(facture, taxeGroupe2));
		// Mise à jour du montantTva
		facture.setMontantTva(repository.calcMontantTva(facture));
		// Mise à jour du montant tsHt
		facture.setTsHt(repository.calctsHt(facture));
		// Mise à jour du montant tsTtc
		facture.setTsTtc(repository.calctsTtc(facture));
		// Mise à jour du montantTTC : somme des montantTtc des lignes de la facture
		facture.setMontantTtc(repository.calcMontantTtc(facture));
		// Ajout du montant de la taxe spécifique HT au montantHT
		if (facture.getTsHt() != null)
			facture.setMontantHt(facture.getMontantHt() + facture.getTsHt());
		// Ajout du montant de la taxe spécifique TTC au montantTtc
		if (facture.getTsTtc() != null)
			facture.setMontantTtc(facture.getMontantTtc() + facture.getTsTtc());
		// Ajout du montant aib au montantTtc de la facture
		if (facture.getAib() != null && facture.getMontantTtc() != null && facture.getMontantHtAib() != null) {
			// Mise à jour du montant aib de la facture
			facture = setFactureMontantAib(facture);
			facture.setMontantTtc((double) (Math.round(facture.getMontantTtc()) + Math.round(facture.getMontantAib())));
		}

		// Mise à jour de l'option remise de la facture
		var details = facture.getDetails();
		facture.setRemise(false);
		for (DetailFacture detail : details) {
			if (detail.isRemise()) {
				facture.setRemise(true);
				break;
			}
		}

		// Enregistrement dans la base de données
		facture = repository.save(facture);
		// Renvoie de la facture
		return facture;
	}

	/**
	 * Valide un DetailFacture
	 * 
	 * @param id L'identifiant du DetailFacture
	 * @return {@link Facture} La facture du DetailFacture
	 */
	public Facture validerDetailFacture(Long factureId, Long detailId) {
		// Check permission
		if (!accessService.canWritable(Feature.facturationListe))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération du DetailFacture à valider
		Optional<DetailFacture> optional = dfRepos.findByFactureIdAndId(factureId, detailId);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lingne de la facture non trouvée");
		DetailFacture df = optional.get();
		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(df);

		// Validation du detailFacture
		df.setValid(true);
		// Mise à jour du personnel qui met à jour (qui valide le detailFacture)
		// df.setUpdatedBy(getAuthPersonnel());
		// Enregistrement et renvoie de la facture
		df = dfRepos.save(df);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(df);

		// Enregistrement de l'audit
		auditService.traceChange(Operation.FACTURATION_DETAIL_VALIDATE, valAvant, valApres);

		// Renvoie de la facture
		return df.getFacture();
	}

	/**
	 * Supprimer une ligne de la facture et mise à jour de cette dernière
	 * 
	 * @param detailId L'identifiant du detailFacture à supprimer
	 * @return {@link Facture}
	 */
	public Facture deleteDetailFacture(Long factureId, Long detailId) {
		// Check permission
		if (!accessService.canDeletable(Feature.facturationListe))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération du detailFacture à supprimer
		Optional<DetailFacture> optional = dfRepos.findByFactureIdAndId(factureId, detailId);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ligne de la facture non trouvée");
		DetailFacture dtf = optional.get();
		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(dtf);
		// Récupération de la facture
		Facture facture = dtf.getFacture();
		// Vérification du type de la facture
		if (facture.getType().getGroupe().equals(TypeData.FA))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"Vous ne pouvez supprimer les lignes d'une facture d'avoir");
		// Vérifier si le detailFacture n'est pas encore validé
		if (dtf.isValid())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ligne de la facture déjà validée");
		// Vérification si la facture n'est pas encore valider
		if (facture.isValid())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Facture déjà validée");
		// Suppression de toutes les clés étrangers orphélines du detail facture
		// Suppression de la remise si existante
		/*
		 * Remise remise = null; if (dtf.isRemise()) { remise = dtf.getDiscount();
		 * dtf.setDiscount(null); remiseRepos.delete(remise); } // Suppression de la
		 * taxe spécifique TaxeSpecifique ts = null; if (dtf.getTs() != null) { ts =
		 * dtf.getTs(); dtf.setTs(null); tsRepos.delete(ts); }
		 */
		// Suppression du detailFacture
		dfRepos.delete(dtf);

		// Enregistrement de l'audit
		auditService.traceChange(Operation.FACTURATION_DETAIL_DELETE, valAvant, null);

		// Mise à jour des montants de la facture et renvoie de cette dernière
		return this.resetFacture(facture.getId());
	}

	/**
	 * Remettre à null les montant de la facture si cette dernière est vide, c'est à
	 * dire ne contient aucune ligne
	 * 
	 * @param factureId L'identifiant de la facture
	 * @return {@link Facture} La facture mise à jour
	 */
	private Facture resetFacture(Long factureId) {
		// Récupération de la facture à mettre à jour
		Facture facture = repository.findByIdAndValidFalse(factureId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture non trouvée"));
		// Quand la facture n'est pas vide, mettre à jour les montants
		if (!facture.getDetails().isEmpty())
			return this.calculer(facture);
		else { // Sinon, remettre à null tous les montant de la facture
			facture.setAib(null);
			facture.setMontantAib(null);
			facture.setMontantHt(null);
			facture.setMontantTtc(null);
			facture.setMontantTva(null);
			facture.setTsHt(null);
			facture.setTsTtc(null);
			return repository.save(facture);
		}
	}

	/**
	 * Valide une facture non vide
	 * 
	 * @param id L'identifiant de la facture
	 * @return {@link Facture} La facture validée
	 * @throws JRException 
	 * @throws IOException 
	 */
	public Facture validerFacture(Long id, ReglementPayload payload) {
		// Check permission
		if (!accessService.canWritable(Feature.facturationFV))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération de la facture à valider
		Optional<Facture> optional = repository.findByIdAndValidFalse(id);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture non trouvée");
		Facture facture = optional.get();
		// Vérification si la facture n'est pas vide
		if (facture.getDetails().isEmpty())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Facture vide");

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(facture);

		// Gestion de l'aib
		var aibId = payload.getAibId();
		if (aibId != null) {
			facture = setFactureAib(facture, aibId);
		} else { // Si l'aib est null, remettre à null une probable précédente aib
			// Verification d'une précédente aib et la supprimer
			if (facture.getAib() != null) {
				facture.setAib(null);
				facture.setMontantAib(null);
				// Mise à jour des montants de la facture
				facture = this.calculer(facture);
			}

		}

		// Gestion du règlement de la facture
		ReglementFacture reglement = null;
		// Suppression d'un probable précédent règlement de la facture
		// Cela permet d'éviter d'avoir des règlements orphélins dans la base de données
		if (facture.getReglement() != null) {
			reglement = facture.getReglement();
			facture.setReglement(null);
			reglementRepos.delete(reglement);
		}
		// Nouveau règlement de la facture
		reglement = setFactureReglement(payload);

		// Mise à jour du reglement de la facture
		facture.setReglement(reglement);

		// Pré-enregistrement de la facture
		facture = repository.save(facture);

		// Finalisation de la facture
		var resultat = finalisationDgi(facture);

		// Enregistrement
		facture = repository.save(resultat);

		// Validation des lignes de la facture
		if (facture.isConfirm()) {
			for (DetailFacture detail : facture.getDetails()) {
				detail.setValid(true);
				detail = dfRepos.save(detail);
				// Générer le mouvement article
				generateMvtSortieArticle(detail);
			}
		}
		// Tentatif d'impression de la facture
//		try {
//			var filename = this.printInvoiceAndStoreIt(facture);
//			facture.setFilename(filename);
//			facture = repository.save(facture);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JRException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		// Si la facture est validé, enregistrer les traces de changement
		if (facture.isConfirm()) {
			// Gestion audit : valeurApres
			String valApres = tool.toJson(facture);

			// Enregistrement de l'audit
			auditService.traceChange(Operation.FACTURATION_VALIDATE, valAvant, valApres);
			
			// Envoie du rapport de validation de la facture par mail à l'adresse email défini par le fournisseur
			sendMailFactureValidee(facture);
		}

		// Renvoie de la facture
		return facture;
	}

	/**
	 * @param detail
	 */
	private void generateMvtSortieArticle(DetailFacture detail) {
		/*** Début mise à jour de l'article ***/
		// Mise à jour de la quantité de l'article
		Article article = detail.getArticle();
		article.setStock(article.getStock() - detail.getQuantite());
		article = articleRepos.save(article);
		/*** Fin mise à jour de l'article ***/
		
		// Générer le mouvement article
		MouvementArticle mvt = new MouvementArticle();
		// Mise à jour des champs du mouvement article
		mvt.setDetailFacture(detail);;
		mvt.setArticle(article);
		mvt.setDate(new Date());
		mvt.setType(TypeData.SORTIE);
		mvt.setDescription("Sortie d'article suite à la validation d'une facture de vente");		
		// Enregistrement du mouvement de l'article
		mvtArticleRepos.save(mvt);
	}
	
	/**
	 * @param detail
	 */
	private void generateMvtRestaurationArticle(DetailFacture detail) {
		/*** Début mise à jour de l'article ***/
		// Mise à jour de la quantité de l'article
		Article article = detail.getArticle();
		article.setStock(article.getStock() + detail.getQuantite());
		article = articleRepos.save(article);
		/*** Fin mise à jour de l'article ***/
		
		// Générer le mouvement article
		MouvementArticle mvt = new MouvementArticle();
		// Mise à jour des champs du mouvement article
		mvt.setDetailFacture(detail);;
		mvt.setArticle(article);
		mvt.setDate(new Date());
		mvt.setType(TypeData.RESTAURATION);
		mvt.setDescription("Restauration du stock de l'article suite à la validation d'une facture d'avoir");		
		// Enregistrement du mouvement de l'article
		mvtArticleRepos.save(mvt);
	}

	/**
	 * Mettre à jour l'aib de la facture
	 * 
	 * @param facture
	 * @param aibId
	 * @return
	 */
	private Facture setFactureAib(Facture facture, Long aibId) {
		Taxe aib = taxeRepos.findById(aibId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aib non trouvée"));
		// Mise à jour de l'aib
		facture.setAib(aib);
		// Mise à jour des montants et renvoie de la facture
		return this.calculer(facture);
	}

	/**
	 * Mettre à jour le montant aib de la facture
	 * 
	 * @param facture La facture à mettre à jour
	 * @return {@link Facture} La facture mise à jour
	 */
	private Facture setFactureMontantAib(Facture facture) {
		/**
		 * Le montant de l'Aib est arrondi par defaut si sa partie decimale est < 5 (je
		 * veux dire le chiffre après la virgule), et par excès si la partie décimale
		 * est > 5 Si la partie décimale est égle à 0.5, ambiguïté dans le calcul, il
		 * faudrait attendre la reponse du serveur de la DGI pour prendre une décisison.
		 * 
		 * Le code qui suit resoud cette approche que nous avons constacté lors des
		 * tests sur les factures générées par le serveur de la DGI
		 */
		// Récupération du montant ht pour l'aib
		// Calcule du montant aib
		Double montantAib = (facture.getMontantHtAib() * facture.getAib().getValeur()) / 100;
		// Récupération de la partie décimale
		Double decimal = montantAib - montantAib.longValue();
		// Si La partie decimale est null ou inférieure ou égale à 0.5, prendre la
		// partie
		// entière du montant l'aib
		if (decimal.equals(0d))
			facture.setMontantAib((double) (montantAib.longValue()));
		// Sinon si le decimal est égale à 0.5, ambiguïté dans le calcul, il faudrait
		// attendre la reponse du serveur de la DGI pour prendre une décisison
		else if (decimal.equals(0.5d))
			facture.setMontantAib(montantAib);
		// Sinon, prendre la partie entière du montant de l'aib + 1
		else
			facture.setMontantAib((double) ((montantAib.longValue()) + 1l));
		// Renvoie de la facture
		return facture;
	}

	/**
	 * Définir le règlement de la facture
	 * 
	 * @param payload
	 * @return
	 */
	private ReglementFacture setFactureReglement(ReglementPayload payload) {
		// Instanciation du reglement
		ReglementFacture reglement = new ReglementFacture();
		// Mise à jour des champs
		reglement.setDescription(payload.getDescription());
		reglement.setMontantPayer(payload.getMontantPayer());
		reglement.setMontantRecu(payload.getMontantRecu());
		reglement.setMontantRendu(payload.getMontantRendu());
		// Gestion du type de payement
		if (payload.getTypePaiementId() != null) {
			var tp = tpRepos.findById(payload.getTypePaiementId()).orElseThrow(
					() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le type de payement non trouvé"));

			reglement.setTypePaiement(tp);
		}
		// Enregistrement du reglement
		reglement = reglementRepos.save(reglement);
		return reglement;
	}

	/**
	 * Supprime une facture
	 * 
	 * @param id L'identifiant de la facture
	 * @return {@link Facture} La facture validée
	 */
	public boolean deleteFacture(Long id) {
		// Check permission
		if (!accessService.canDeletable(Feature.facturationListe))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération de la facture à valider
		Optional<Facture> optional = repository.findById(id);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture non trouvée");
		Facture facture = optional.get();
		// Vérification si la facture n'est pas encore valider
		if (facture.isValid())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Facture déjà validée");
		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(facture);
		// Suppression de la facture
		repository.delete(facture);
		// Enregistrement de la trace de changement
		auditService.traceChange(Operation.FACTURATION_DELETE, valAvant, null);
		// Si tout s'est bien passé, on renvoie true
		return true;
	}

	private Facture finalisationDgi(Facture facture) {
		// Initialisation des données
		// Récupération du parametre system
		Parametre param = paramRepos.findOneParams().orElseThrow(
				() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Votre système n'est pas encore paramètré"));

		// Vérification de la validité de l'e-mecef
		if (param.getExpiration().before(new Date()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Votre machine virtuelle e-MECef est expirée");

		// Rcupération de l'utilisateur connecté
		User operateur = this.getAuthenticated();
		// Récupération du client de la facture
		Client client = facture.getClient();
		// Récupération du reglement de la facutre
		ReglementFacture reglement = facture.getReglement();
		// Récupération du type de la facture
		TypeFacture tf = facture.getType();

		// Les données de connection au serveur de la dgi
		ApiClient defaultClient = Configuration.getDefaultApiClient();

		// Configure API key authorization: Bearer
		ApiKeyAuth Bearer = (ApiKeyAuth) defaultClient.getAuthentication("Bearer");
		// YOUR API TOKEN HERE
		Bearer.setApiKey(param.getToken());

		// By default, SDK is configured with SyGMEF-test
		// Enable following line in order to configure SDK for SyGMEF-production
		if (param.getTypeSystem() == TypeSystem.Production)
			defaultClient.setBasePath("https://sygmef.impots.bj/emcf");

		// Instanciation pour gestion de la facture (côté serveur dgi)
		SfeInvoiceApi apiInvoiceInstance = new SfeInvoiceApi();
		// Instanciation pour la gestion des informations utiles
		SfeInfoApi apiInfoInstance = new SfeInfoApi();

		try {
			// INFO
			InfoResponseDto infoResponseDto = apiInfoInstance.apiInfoStatusGet();
			System.out.println(infoResponseDto);

			// var invoiceTypesDto = apiInfoInstance.apiInfoInvoiceTypesGet();
			// System.out.println(invoiceTypesDto);
			//
			// var taxGroupsDto = apiInfoInstance.apiInfoTaxGroupsGet();
			// System.out.println(taxGroupsDto);
			//
			// var paymentTypesDto = apiInfoInstance.apiInfoPaymentTypesGet();
			// System.out.println(paymentTypesDto);

			// INVOICE
			StatusResponseDto statusResponseDto = apiInvoiceInstance.apiInvoiceGet();
			System.out.println(statusResponseDto);

			InvoiceRequestDataDto invoiceRequestDataDto = new InvoiceRequestDataDto();
			// fill-in the request
			invoiceRequestDataDto.setIfu(param.getIfu());// YOUR IFU HERE

			// Gestion de l'operateur : utilisateur faisant l'opération
			OperatorDto operatorDto = new OperatorDto();
			operatorDto.setId(operateur.getId() + "");
			operatorDto.setName(operateur.getFullname());
			invoiceRequestDataDto.setOperator(operatorDto);
			// Gestion du client de la facture si existant
			if (client != null) {
				ClientDto clientDto = new ClientDto();
				clientDto.setIfu(client.getIfu());
				clientDto.setName(client.getName());
				clientDto.setContact(client.getContact());
				clientDto.setAddress(client.getAddress());
				invoiceRequestDataDto.setClient(clientDto);
			}
			// Mise à jour du mode de payement
			List<PaymentDto> payments = new ArrayList<PaymentDto>();
			PaymentDto pay = new PaymentDto();
			pay.setName(PaymentTypeEnum.fromValue(reglement.getTypePaiement().getType().name()));
			pay.setAmount((long) reglement.getMontantPayer());
			payments.add(pay);
			invoiceRequestDataDto.setPayment(payments);

			// Mise à jour de la référence de la facture en cas des facture d'avoir
			if (tf.getType() == TypeFactureEnum.FA && tf.getType() == TypeFactureEnum.EA)
				invoiceRequestDataDto.setReference(facture.getReference());

			// Mise à jour du type de la facture
			// invoiceRequestDataDto.setType(InvoiceTypeEnum.FV);
			invoiceRequestDataDto.setType(InvoiceTypeEnum.fromValue(tf.getType().name()));
			// En cas des facture d'avoir, on met la référence de la facture d'origine
			if (tf.getType() == TypeFactureEnum.FA || tf.getType() == TypeFactureEnum.EA)
				invoiceRequestDataDto.setReference(facture.getOrigineRef());

			if (facture.getAib() != null)
				// J'utilise la fonction substring pour récupérer le groupe de l'aib en un seul
				// caractère (char)
				invoiceRequestDataDto
						.setAib(AibGroupTypeEnum.fromValue((facture.getAib().getGroupe().name()).substring(3)));

			// Gestion des lignes de la facture
			// Instanciation de la liste des items
			List<ItemDto> items = new ArrayList<ItemDto>();

			// Récupération de la liste des lignes de la facture
			List<DetailFacture> details = facture.getDetails();

			for (DetailFacture detail : details) {
				var item = new ItemDto();
				item.setName(detail.getArticle().getDesignation());
				item.setPrice(Math.abs(detail.getPrixUnitaire().longValue()));
				item.setQuantity(detail.getQuantite());
				item.setTaxGroup(TaxGroupTypeEnum.fromValue(detail.getTaxe().getGroupe().name()));
				// Ajout de la taxe spécifique
				if (detail.getTaxeSpecifique() != null)
					item.setTaxSpecific(Math.abs(detail.getTaxeSpecifique().longValue()));
				// Ajout de remise
				if (detail.isRemise()) {
					var remise = detail.getDiscount();
					item.setOriginalPrice(Math.abs(remise.getOriginalPrice()));
					item.setPriceModification(remise.getPriceModification());
				}
				items.add(item);
			}

			invoiceRequestDataDto.setItems(items);
			System.out.println(invoiceRequestDataDto);
			InvoiceResponseDto invoiceResponseDto = apiInvoiceInstance.apiInvoicePost(invoiceRequestDataDto);
			System.out.println(invoiceResponseDto);

			if (invoiceResponseDto.getUid() != null) {
				// Enregistrement de la reponse du serveur
				facture = setFactureResponseDgi(invoiceResponseDto, facture);
				//
				InvoiceDetailsDto invoiceDetailsDto = apiInvoiceInstance.apiInvoiceUidGet(invoiceResponseDto.getUid());
				System.out.println(invoiceDetailsDto);

				// finalize
				SecurityElementsDto securityElementsDto = apiInvoiceInstance
						.apiInvoiceUidConfirmPut(invoiceResponseDto.getUid());
				System.out.println(securityElementsDto);

				// Enregistrement de la finalisation
				facture = setFinalisation(securityElementsDto, facture);
			}
		} catch (ApiException e) {
			System.err.println("Exception when calling SfeInvoiceApi");
			e.printStackTrace();
			if (e.getCode() == HttpStatus.UNAUTHORIZED.value())
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
						"Une autorisation est réquise pour valider la facture. Veuillez contacter le fournisseur de votre SFE pour plus d'assistance. Merci !");
			if (e.getCode() == 0)
				throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
						"Vérifier la connexion internet de votre machine svp !");

		}

		// Retour de la facture
		return facture;
	}

	private Facture setFactureResponseDgi(InvoiceResponseDto invoiceResponseDto, Facture facture) {
		// Instanciation de la FactureResponseDgi
		FactureResponseDgi factRespo = new FactureResponseDgi();
		// Mise à jour de la facture de factRespo
		factRespo.setFacture(facture);
		// Mise à jour des autres champs
		factRespo.setAib(invoiceResponseDto.getAib());
		factRespo.setErrorCode(invoiceResponseDto.getErrorCode());
		factRespo.setErrorDesc(invoiceResponseDto.getErrorDesc());
		factRespo.setHab(invoiceResponseDto.getHab());
		factRespo.setHad(invoiceResponseDto.getHad());
		factRespo.setTa(invoiceResponseDto.getTa());
		factRespo.setTaa(invoiceResponseDto.getTaa());
		factRespo.setTab(invoiceResponseDto.getTab());
		factRespo.setTac(invoiceResponseDto.getTac());
		factRespo.setTad(invoiceResponseDto.getTad());
		factRespo.setTae(invoiceResponseDto.getTae());
		factRespo.setTaf(invoiceResponseDto.getTaf());
		factRespo.setTb(invoiceResponseDto.getTb());
		factRespo.setTc(invoiceResponseDto.getTc());
		factRespo.setTd(invoiceResponseDto.getTd());
		factRespo.setTotal(invoiceResponseDto.getTotal());
		factRespo.setTs(invoiceResponseDto.getTs());
		factRespo.setUid(invoiceResponseDto.getUid());
		factRespo.setVab(invoiceResponseDto.getVab());
		factRespo.setVad(invoiceResponseDto.getVad());

		// Enregistre de la response du serveur
		frRepos.save(factRespo);
		// Gestion de conformité du montant total avec le serveur de la DGI
		Double totalDGI = (double) invoiceResponseDto.getTotal();
		Double totalFacture = facture.getMontantTtc();
		if (facture.getType().getGroupe() != TypeData.FA && !totalFacture.equals(totalDGI)) {
			System.out.println("Montant mis à jour");
			// Mise à jour du montant total de la facture
			facture.setMontantTtc(totalDGI);
			// Mise à jour du montant aib
			facture.setMontantAib((double) invoiceResponseDto.getAib());
			// Mise à jour des données de reglement de la facture
			var reglement = facture.getReglement();
			reglement.setMontantPayer(invoiceResponseDto.getTotal());
			reglement.setMontantRendu(reglement.getMontantRecu() - reglement.getMontantPayer());
			reglement = reglementRepos.save(reglement);
			// Mise à jour du règlement de la facture
			facture.setReglement(reglement);
		}
		// Retour de la facture
		return facture;
	}

	private Facture setFinalisation(SecurityElementsDto securityElementsDto, Facture facture) {
		// Instanciation de la FactureFinalisationDgi
		FactureFinalisationDgi ffdgi = new FactureFinalisationDgi();

		// Mise à jour de la facture de ffdgi
		ffdgi.setFacture(facture);
		// Mise à jour des autres champs
		ffdgi.setCodeMECeFDGI(securityElementsDto.getCodeMECeFDGI());
		ffdgi.setCounters(securityElementsDto.getCounters());
		ffdgi.setDateTime(securityElementsDto.getDateTime());
		ffdgi.setErrorCode(securityElementsDto.getErrorCode());
		ffdgi.setErrorDesc(securityElementsDto.getErrorDesc());
		ffdgi.setNim(securityElementsDto.getNim());
		ffdgi.setQrCode(securityElementsDto.getQrCode());

		// Mise à jour de la refernce de la facture
		if (ffdgi.getCodeMECeFDGI() != null) {
			facture.setConfirm(true);
			facture.setValid(true);
			facture.setReference(ffdgi.getCodeMECeFDGI().replace("-", ""));
			// Mise à jour de l'operateur de la facture
			facture.setOperateur(this.getAuthenticated());

			// Mise à jour du numero de la facture
			// Récupération du compteur
			var counter = ffdgi.getCounters();
			// Récupération du numéro du compteur
			var numero = counter.substring(counter.indexOf("/") + 1, counter.indexOf(" "));
			// Numéro de la facture
			facture.setNumero(ffdgi.getNim() + "-" + numero);

			// Mise à jour de la date de finalisation de la facture
			try {
				// Date From String
				Date date = tool.stringToDate(ffdgi.getDateTime(), "dd/MM/yyyy HH:mm:ss");
				facture.setDate(date);
				// Récupération et Mise à jour de la date de FactureResponseDgi pour cette
				// facture
				var factRespo = frRepos.findByFactureId(facture.getId());
				factRespo.setDate(date);
				frRepos.save(factRespo);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Enregistrement de la finalisation
		ffRepos.save(ffdgi);
		// Retour de la facture
		return facture;

	}

	/**
	 * Générer la facture normalisée
	 * 
	 * @param id
	 * @return {@link ResponseEntity}
	 * @throws IOException
	 * @throws JRException
	 */
	public ResponseEntity<byte[]> genererFacture(Long id, String format) throws IOException, JRException {

		// Récupération de la facture
		Optional<Facture> optional = repository.findByIdAndConfirmTrue(id);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture non trouvée");
		Facture facture = optional.get();

		return getInvoicePrint(facture, format);
	}

	/**
	 * Imprimer la facture et l'envoiyer telle quelle
	 * 
	 * @param facture La facture confirmée par l'emcef
	 * @return
	 * @throws IOException
	 * @throws JRException
	 */
	private ResponseEntity<byte[]> getInvoicePrint(Facture facture, String format) throws IOException, JRException {
		// Récupération des données du parametres
		Parametre param = paramRepos.findOneParams().orElseThrow(
				() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Votre système n'est pas encore paramètré."));
		System.out.println("format = " + format);
		// Récupération du type de la facture
		var type = facture.getType();
//		var templateDir = format.equals("A8") ? "report-A8/" : "report/";
		var templateDir = param.getFormatFacture().equals(TypeData.A8) ? "report-A8/" : "report/";
		var template = "";
		if (type.getGroupe().equals(TypeData.FV)) {
			template = templateDir + INVOICE_REPORT_TEMPLATE_FV;
			if (facture.isRemise())
				template = templateDir + INVOICE_REPORT_TEMPLATE_FV_REMISE;
		} else if (type.getGroupe().equals(TypeData.FA)) {
			template = templateDir + INVOICE_REPORT_TEMPLATE_FA;
			if (facture.isRemise())
				template = templateDir + INVOICE_REPORT_TEMPLATE_FA_REMISE;
		}

		// Les données de la facture
		InvoiceData invoice = reportService.setInvoiceData(facture, param);

		// Setting Invoice Report Params
		HashMap<String, Object> map = setInvoiceReportParams(facture, param);
		// Ajout des données de l'entête
//		map.put("entete", new JRBeanCollectionDataSource(Collections.singleton(invoice)));

		// Générer la facture
		var invoiceName = INVOICE_REPORT_BASE_NAME + facture.getNumero() + (format.equals("A8") ? "-A8" : "-A4")
				+ ".pdf";
		return reportService.invoiceReport(invoice, map, template, invoiceName);
	}

	/**
	 * Imprimer la facture et l'enregistrement en local
	 *
	 * @param facture La facture confirmée par l'emcef
	 * @return
	 * @throws IOException
	 * @throws JRException
	 */
	private String printInvoiceAndStoreIt(Facture facture, String format) throws IOException, JRException {
		// Récupération des données du parametres
		Parametre param = paramRepos.findOneParams().orElseThrow(
				() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Votre système n'est pas encore paramètré."));
		System.out.println("format = " + format);
		// Récupération du type de la facture
		var type = facture.getType();
		var templateDir = format.equals("A8") ? "report-A8/" : "report/";
		// var templateDir = param.getFormatFacture().equals(TypeData.A8) ? "report-A8/" : "report/";
		var template = "";
		if (type.getGroupe().equals(TypeData.FV)) {
			template = templateDir + INVOICE_REPORT_TEMPLATE_FV;
			if (facture.isRemise())
				template = templateDir + INVOICE_REPORT_TEMPLATE_FV_REMISE;
		} else if (type.getGroupe().equals(TypeData.FA)) {
			template = templateDir + INVOICE_REPORT_TEMPLATE_FA;
			if (facture.isRemise())
				template = templateDir + INVOICE_REPORT_TEMPLATE_FA_REMISE;
		}

		// Les données de la facture
		InvoiceData invoice = reportService.setInvoiceData(facture, param);

		// Setting Invoice Report Params
		HashMap<String, Object> map = setInvoiceReportParams(facture, param);
		// Ajout des données de l'entête
//		map.put("entete", new JRBeanCollectionDataSource(Collections.singleton(invoice)));

		// Générer la facture
		var invoiceName = INVOICE_REPORT_BASE_NAME + facture.getNumero() + (format.equals("A8") ? "-A8" : "-A4")
				+ ".pdf";
		return reportService.invoiceReportAndStoreIt(invoice, map, template, invoiceName);
	}

	/**
	 * Dénfinir les données de paramètre pour l'impression de la facture
	 * 
	 * @param facture
	 * @param param
	 * @return
	 */
	private HashMap<String, Object> setInvoiceReportParams(Facture facture, Parametre param) {
		// Récupération de FactureResponseDgi pour la facture
		FactureResponseDgi fresp = frRepos.findByFactureId(facture.getId());
		// Récupération des données de finalisation de la facture après sa confirmation
		FactureFinalisationDgi ffin = ffRepos.findByFactureId(facture.getId());

		// Les données des détails de la facture
		List<InvoiceDetailData> details = reportService.setInvoiceDetailData(facture);
		System.out.println("Nom de ligne de la facture : " + facture.getDetails().size());
		// Les données du client
		ClientData client = reportService.setClientData(facture.getClient());
		// Les données de payement
		InvoicePayement payement = reportService.setInvoicePayement(facture.getReglement());
		// Les données de récapitulatif de la facture
		List<InvoiceRecapData> recaps = reportService.setInvoiceRecapData(fresp, facture);
		// Les infos de contact de la société
		var contact = reportService.setCompanyContact(param);

		// Instanciation de la liste des paramètres
		HashMap<String, Object> map = new HashMap<>();
		// Ajout des paramètres
		map.put("detailFacture", new JRBeanCollectionDataSource(details));
		map.put("recap", new JRBeanCollectionDataSource(recaps));
		map.put("client", new JRBeanCollectionDataSource(Collections.singleton(client)));
		map.put("payement", new JRBeanCollectionDataSource(Collections.singleton(payement)));
		map.put("emcef", new JRBeanCollectionDataSource(Collections.singleton(ffin)));
		map.put("company_contact", new JRBeanCollectionDataSource(Collections.singleton(contact)));
		return map;
	}

	/**
	 * Creation de la facture d'avoir
	 * 
	 * @param typeId    L'identifiant du type de la facture d'avoir
	 * @param factureId L'identifiant de la facture de vente d'origine
	 * @return {@link Facture} La facture d'avoir créé
	 */
	public Facture createFactureAvoir(Long typeId, Long factureId) {
		// Check permission
		if (!accessService.canWritable(Feature.facturationFA))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération du type de la facture
		TypeFacture tf = tfRepos.findByIdAndGroupe(typeId, TypeData.FA).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le type de la facture non trouvé"));
		// Récupération de la facture d'origine
		Facture factureOrigine = repository.findByIdAndTypeIdAndConfirmTrue(factureId, tf.getOrigine().getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture de vente non trouvée"));
		// Tentative de récupération de la facture d'avoir associée à la facture de
		// vente
		if (repository.findByOrigineRef(factureOrigine.getReference()).isPresent())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Une facture d'avoir existe déjà pour cette facture de vente");

		// Gestion audit : valeurAvant
		String valAvant = null;

		// Création et mise à jour de la facture d'avoir
		Facture facture = setFactureAvoir(tf, factureOrigine);

		// Gestion des lignes (détails) de la facture
		// Récupération des détails d'origines
		var detailsOrigine = factureOrigine.getDetails();
		// Pour chaque détail d'origine, definition du nouveau détail
		for (DetailFacture detail : detailsOrigine) {
			DetailFacture df = setDetailFactureAvoir(facture, detail);
			// Ajout du detail à la facture
			facture.getDetails().add(df);
		}

		// Mise à jour de la référence de la facture d'origine
		facture.setOrigineRef(factureOrigine.getReference());
		// Mise à jour de la remise
		facture.setRemise(factureOrigine.isRemise());
		// Sauvegarde et renvoie de la nouvelle facture
		facture = repository.save(facture);

		// Gestion audit : valeurApres
		String valApres = tool.toJson(facture);

		// Enregistrement de la trace de changement
		auditService.traceChange(Operation.FACTURATION_FA_CREATE, valAvant, valApres);

		// Renvoie de la facture
		return facture;
	}

	/**
	 * @param tf
	 * @param factureOrigine
	 * @return {@link Facture}
	 */
	private Facture setFactureAvoir(TypeFacture tf, Facture factureOrigine) {
		// Instanciation de la facture d'avoir
		Facture facture = new Facture();
		// Mise à jour des champs de la facture
		// le type de la facture
		facture.setType(tf);
		// Le montant HT négatif
		if (factureOrigine.getMontantHt() != null)
			facture.setMontantHt((double) Math.round(factureOrigine.getMontantHt()) * (-1));
		// Le montant tva négatif
		if (factureOrigine.getMontantTva() != null)
			facture.setMontantTva((double) Math.round(factureOrigine.getMontantTva()) * (-1));
		// L'aib et son montant négatif si existants
		if (factureOrigine.getAib() != null) {
			facture.setAib(factureOrigine.getAib());
			facture.setMontantAib((double) Math.round(factureOrigine.getMontantAib()) * (-1));
		}
		// Le montant ttc négatif
		if (factureOrigine.getMontantTtc() != null)
			facture.setMontantTtc((double) Math.round(factureOrigine.getMontantTtc()) * (-1));
		// Le client d'origine de la facture
		facture.setClient(factureOrigine.getClient());
		// L'opérateur actuelle de la facture
		facture.setOperateur(this.getAuthenticated());

		// Gestion du règlement
		// Récupération de l'ancien règlement de la facture
		var reglementOrigine = factureOrigine.getReglement();
		// Instanciation du nouveau règlement
		var reglement = new ReglementFacture();
		// Mise à jour des champs du nouveau règlement
		reglement.setTypePaiement(reglementOrigine.getTypePaiement());
		reglement.setMontantRecu(reglementOrigine.getMontantRecu() * (-1));
		reglement.setMontantPayer(reglementOrigine.getMontantPayer() * (-1));
		reglement.setMontantRendu(reglementOrigine.getMontantRendu());
		reglement.setDescription(reglementOrigine.getDescription());
		facture.setReglement(reglementRepos.save(reglement));
		// Sauvegarde de la facture
		facture = repository.save(facture);
		return facture;
	}

	/**
	 * @param facture
	 * @param detail
	 * @return {@link DetailFacture}
	 */
	private DetailFacture setDetailFactureAvoir(Facture facture, DetailFacture detail) {
		// instanciation d'un nouveau détail
		var df = new DetailFacture();
		df.setName(detail.getName());
		df.setQuantite(detail.getQuantite());
		df.setUnite(detail.getUnite());
		df.setPrixUht(detail.getPrixUht() * (-1));
		df.setPrixUnitaire(detail.getPrixUnitaire() * (-1));
		if (detail.getMontantHt() != null)
			df.setMontantHt((double) Math.round(detail.getMontantHt()) * (-1));
		if (detail.getMontantTva() != null)
			df.setMontantTva((double) Math.round(detail.getMontantTva()) * (-1));
		if (detail.getMontantTtc() != null)
			df.setMontantTtc((double) Math.round(detail.getMontantTtc()) * (-1));
		// Mise à jour de la taxe spécifique
		if (detail.getTaxeSpecifique() != null) {
			df.setTaxeSpecifique((double) Math.round(detail.getTaxeSpecifique()) * (-1));
			if (detail.getTsTtc() != null)
				df.setTsTtc((double) Math.round(detail.getTsTtc()) * (-1));
			var tsOriginale = detail.getTs();
			// Instanciation et mise à jour du ts pour la ligne de la facture d'avoir
			var ts = new TaxeSpecifique();
			ts.setName(tsOriginale.getName());
			ts.setQuantite(tsOriginale.getQuantite());
			ts.setTaxe(tsOriginale.getTaxe());
			if (tsOriginale.getTsUnitaire() != null)
				ts.setTsUnitaire((double) Math.round(tsOriginale.getTsUnitaire()) * (-1));
			if (tsOriginale.getTsUnitaireTtc() != null)
				ts.setTsUnitaireTtc((double) Math.round(tsOriginale.getTsUnitaireTtc()) * (-1));
			if (tsOriginale.getTsTotalHt() != null)
				ts.setTsTotalHt((double) Math.round(tsOriginale.getTsTotalHt()) * (-1));
			if (tsOriginale.getTsTotal() != null)
				ts.setTsTotal((double) Math.round(tsOriginale.getTsTotal()) * (-1));
			ts = tsRepos.save(ts);
			// Mise à jour du ts de la ligne de la facture
			df.setTs(ts);
		}
		if (detail.isRemise()) {
			df.setRemise(detail.isRemise());
			var remise = new Remise();
			remise.setTaux(detail.getDiscount().getTaux());
			remise.setOriginalPrice(detail.getDiscount().getOriginalPrice() * (-1));
			remise.setPriceModification(detail.getDiscount().getPriceModification());
			df.setDiscount(remiseRepos.save(remise));
		}
		df.setTaxe(detail.getTaxe());
		df.setArticle(detail.getArticle());
		df.setFacture(facture);
		// Enregistrement et Renvoie du detailFacture
		return dfRepos.save(df);
	}

	/**
	 * Validation d'une facture d'avoir pour la normaliser
	 * 
	 * @param id L'identifiant de la facture à valider
	 * @return {@link Facture} La facture valider
	 */
	public Facture validerFactureAvoir(Long id) {
		// Check permission
		if (!accessService.canWritable(Feature.facturationFA))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération de la facture à valider
		Optional<Facture> optional = repository.findByIdAndValidFalse(id);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture non trouvée");
		Facture facture = optional.get();
		// Vérification si la facture n'est pas vide
		if (facture.getDetails().isEmpty())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Facture vide");

		var typeFacture = facture.getType();
		if (typeFacture.getGroupe() != TypeData.FA) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le type de la facture n'est pas valide");
		}

		// Gestion audit : valeurAvant
		String valAvant = tool.toJson(facture);

		// Finalisation de la facture
		var resultat = finalisationDgi(facture);

		// Enregistrement
		facture = repository.save(resultat);

		// Validation des lignes de la facture
		if (facture.isConfirm()) {
			for (DetailFacture detail : facture.getDetails()) {
				detail.setValid(true);
				detail = dfRepos.save(detail);
				generateMvtRestaurationArticle(detail);
			}
		}

		// Tentatif d'impression de la facture
//		try {
//			var filename = this.printInvoiceAndStoreIt(facture);
//			facture.setFilename(filename);
//			facture = repository.save(facture);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JRException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		// Si la facture est validé, enregistrer les traces de changement
		if (facture.isConfirm()) {
			// Gestion audit : valeurApres
			String valApres = tool.toJson(facture);

			// Enregistrement de l'audit
			auditService.traceChange(Operation.FACTURATION_FA_VALIDATE, valAvant, valApres);
			
			// Envoie du rapport de validation de la facture par mail à l'adresse email défini par le fournisseur
			sendMailFactureValidee(facture);
		}

		// renvoie de la facture après la finalisation
		return facture;
	}

	/** Gestion du filtre pour la liste des facture **/

	/**
	 * Récupérer la liste des facture en fonction d'un type de facture
	 * 
	 * @param typeId L'identifiant du type de la facture
	 * @return {@link List<Facture>}
	 */
	public List<Facture> getListByType(Long typeId) {
		// Check permission
		if (!accessService.canReadable(Feature.facturationListe))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		TypeFacture tf = tfRepos.findById(typeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Type de facture non trouvé"));
		return repository.findAllByType(tf);
	}

	/**
	 * Récupérer la liste des factures en fonction de la date de création de ces
	 * dernières
	 * 
	 * @param payload L'objet contenant les dates début et fin de la recherche
	 * @return {@link List<Facture>}
	 */
	public List<Facture> getListByCreatedAt(StatsPayload payload) {
		// Check permission
		if (!accessService.canReadable(Feature.facturationListe))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		if (payload.getDebutAt() != null && payload.getFinAt() != null)
			return repository.findAllByCreatedAtBetween(payload.getDebutAt(), payload.getFinAt());

		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les dates ne sont pas correctement définies");
	}

	/**
	 * Récupérer la liste des factures en fonction de la date de confirmation
	 * E-MECeF/DGI de ces dernières
	 * 
	 * @param payload L'objet contenant les dates début et fin de la recherche
	 * @return {@link List<Facture>}
	 */
	public List<Facture> getListByConfirmedDate(StatsPayload payload) {
		// Check permission
		if (!accessService.canReadable(Feature.facturationListe))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		if (payload.getDebut() != null && payload.getFin() != null)
			return repository.findAllByDateNotNullAndDateBetween(payload.getDebut(), payload.getFin());

		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les dates ne sont pas correctement définies");
	}

	/**
	 * Récupérer la liste des factures en fonction du type des factures et de la
	 * date de création de ces dernières
	 * 
	 * @param payload L'objet contenant les dates début et fin de la recherche
	 * @return {@link List<Facture>}
	 */
	public List<Facture> getListByTypeAndCreatedAt(StatsPayload payload, Long typeId) {
		// Check permission
		if (!accessService.canReadable(Feature.facturationListe))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		TypeFacture tf = tfRepos.findById(typeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Type de facture non trouvé"));

		if (payload.getDebutAt() != null && payload.getFinAt() != null)
			return repository.findAllByTypeAndCreatedAtBetween(tf, payload.getDebutAt(), payload.getFinAt());

		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les dates ne sont pas correctement définies");
	}

	/**
	 * Récupérer la liste des factures en fonction du type des factures et de la
	 * date de confirmation E-MECeF/DGI de ces dernières
	 * 
	 * @param payload L'objet contenant les dates début et fin de la recherche
	 * @return {@link List<Facture>}
	 */
	public List<Facture> getListByTypeAndConfirmedDate(StatsPayload payload, Long typeId) {
		// Check permission
		if (!accessService.canReadable(Feature.facturationListe))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		TypeFacture tf = tfRepos.findById(typeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Type de facture non trouvé"));

		if (payload.getDebut() != null && payload.getFin() != null)
			return repository.findAllByTypeAndDateNotNullAndDateBetween(tf, payload.getDebut(), payload.getFin());

		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les dates ne sont pas correctement définies");
	}
	
	/**
	 * Une méthode asynchrone pour l'envoye d'une facture par mail après que cette dernière soit validée
	 * 
	 * @param facture La facture à envoyer par mail
	 * @return True si le mail est envoyé, False sinon
	 * @throws IOException
	 * @throws JRException
	 */
	@Async
	private CompletableFuture<Boolean> sendMailFactureValidee(Facture facture) {
		// Flag de vérification de l'envoie du mail
		CompletableFuture<Boolean> mailSended = CompletableFuture.completedFuture(false);
		try {
			// Impression et enregistrement du fichier de la facture
			String fileName = printInvoiceAndStoreIt(facture, "A4");
			// Vérifier si la facture est bien imprimée, le nom du fichier deverait être renvoyé
			if (!StringUtils.hasText(fileName))
				return mailSended;
			//Ici la facture est bien imprimée, nous pouvons donc l'envoyer par mail		
			mailSended = accessService.sendMailFactureValidee(fileName, INVOICE_MAIL_TRANSFERT_TEMPLATE);
		} catch (IOException | JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mailSended;
	}

}
