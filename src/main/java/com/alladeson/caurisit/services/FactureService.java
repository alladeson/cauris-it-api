/**
 * 
 */
package com.alladeson.caurisit.services;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.alladeson.caurisit.repositories.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.alladeson.caurisit.models.entities.Article;
import com.alladeson.caurisit.models.entities.Client;
import com.alladeson.caurisit.models.entities.DetailFacture;
import com.alladeson.caurisit.models.entities.Facture;
import com.alladeson.caurisit.models.entities.FactureFinalisationDgi;
import com.alladeson.caurisit.models.entities.FactureResponseDgi;
import com.alladeson.caurisit.models.entities.Parametre;
import com.alladeson.caurisit.models.entities.ReglementFacture;
import com.alladeson.caurisit.models.entities.Remise;
import com.alladeson.caurisit.models.entities.Taxe;
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
	private UserService userService;

	private static final String INVOICE_REPORT_BASE_NAME = "facture-";
	private static final String INVOICE_REPORT_TEMPLATE_FV = "report/facture-vente.jrxml";
	private static final String INVOICE_REPORT_TEMPLATE_FA = "report/facture-avoir.jrxml";
	private static final String INVOICE_REPORT_TEMPLATE_FV_REMISE = "report/facture-vente-remise.jrxml";
	private static final String INVOICE_REPORT_TEMPLATE_FA_REMISE = "report/facture-avoir-remise.jrxml";

	/**
	 * Récupère une facture dont l'identifiant est renseigné
	 * 
	 * @param id L'identifiant de la facture
	 * @return {@link Facture}
	 */
	public Facture get(Long id) {
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
		System.out.println(search);
		// return repository.findByClientNameContaining(search);
		if(search.equals("vide"))
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
		if (detailId.equals((long) 0) || factureId.equals((long) 0)) {
			return new DetailFacture();
		}

		Optional<DetailFacture> optional = dfRepos.findByFactureIdAndId(factureId, detailId);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ligne Facture non trouvée");
		return optional.get();
	}

	/**
	 * Recupération du detail de la facture en fonctionde l'article
	 * 
	 * @param factureId
	 * @param articleId
	 * @return
	 */
	public DetailFacture getDetailFactureByArticle(Long factureId, Long articleId) {
		if (factureId.equals((long) 0) || factureId.equals((long) 0)) {
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
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le type de facture non défini");
		}

		// Récupération de la facture non validée du client en fonction du type de
		// facture et du client
		Facture facture = repository.findByClientAndTypeAndValidFalse(client, tf);
		//
		if (facture == null) {
			facture = new Facture();
			facture.setClient(client);
			facture.setType(tf);
			facture = repository.save(facture);
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
		return calculer(facture);
	}

	/**
	 * @param detailPayload
	 * @param detail
	 * @return {@link DetailFacture}
	 */
	private DetailFacture setTaxeSpecifique(DetailFacture detailPayload, DetailFacture detail) {
		if (detailPayload.getTaxeSpecifique() != null) {
			detail.setTaxeSpecifique(detailPayload.getTaxeSpecifique());
			detail.setTsTtc(
					(double) Math.round((detail.getTaxeSpecifique() * (100 + detail.getTaxe().getValeur())) / 100));
			// Gestion de la taxe spécifique
			var ts = new TaxeSpecifique();
			// Récupération du nom de la taxe spécifique depuis l'article
			ts.setName(detail.getArticle().getTsName());
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
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Taxe non précisée");
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
		var ancienneRemise = detail.getDiscount();
		if (detail.isRemise()) {
			var remise = new Remise();
			remise.setTaux(detailPayload.getTaux());
			remise.setOriginalPrice(detailPayload.getOriginalPrice());
			remise.setPriceModification(detailPayload.getPriceModification());
			detail.setDiscount(remiseRepos.save(remise));
		} else {
			detail.setDiscount(null);
		}
		// Suppression de la probable ancienne remise
		if (ancienneRemise != null) {
			remiseRepos.delete(ancienneRemise);
		}

		// Mise à jour de la taxe spécifique
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
		if (facture.getAib() != null && facture.getMontantTtc() != null) {
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
		// Récupération du DetailFacture à valider
		Optional<DetailFacture> optional = dfRepos.findByFactureIdAndId(factureId, detailId);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lingne de la facture non trouvée");
		DetailFacture df = optional.get();
		// Validation du detailFacture
		df.setValid(true);
		// Mise à jour du personnel qui met à jour (qui valide le detailFacture)
		// df.setUpdatedBy(getAuthPersonnel());
		// Enregistrement et renvoie de la facture
		df = dfRepos.save(df);
		return df.getFacture();
	}

	/**
	 * Supprimer une ligne de la facture et mise à jour de cette dernière
	 * 
	 * @param detailId L'identifiant du detailFacture à supprimer
	 * @return {@link Facture}
	 */
	public Facture deleteDetailFacture(Long factureId, Long detailId) {
		// Récupération du detailFacture à supprimer
		Optional<DetailFacture> optional = dfRepos.findByFactureIdAndId(factureId, detailId);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ligne de la facture non trouvée");
		DetailFacture dtf = optional.get();
		// Récupération de la facture
		Facture facture = dtf.getFacture();
		// Vérifier si le detailFacture n'est pas encore validé
		if (dtf.isValid())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ligne de la facture déjà validée");
		// Vérification si la facture n'est pas encore valider
		if (facture.isValid())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture déjà validée");
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
		// Récupération de la facture à valider
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
	 */
	public Facture validerFacture(Long id, ReglementPayload payload) {
		// Récupération de la facture à valider
		Optional<Facture> optional = repository.findByIdAndValidFalse(id);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture non trouvée");
		Facture facture = optional.get();
		// Vérification si la facture n'est pas vide
		if (facture.getDetails().isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture vide");

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
				dfRepos.save(detail);
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

		// Renvoie de la facture
		return facture;
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
		 * Le montant de l'Aib est arrondi par defaut si sa partie decimale est <= 5 (je
		 * veux dire le chiffre après la virgule), et par excès si la partie décimale
		 * est > 5 Le code qui suit resoud cette approche que nous avons constacté lors
		 * des tests sur les factures générées par le serveur de la DGI
		 */
		// Calcule du montant aib
		Double montantAib = (facture.getMontantHt() * facture.getAib().getValeur()) / 100;
		// Récupération de la partie décimale
		Double decimal = montantAib - montantAib.longValue();
		// Si La partie decimale est null ou inférieure ou égale à 0.5, prendre la
		// partie
		// entière du montant l'aib
		if (decimal.equals(0d) || decimal <= 0.5)
			facture.setMontantAib((double) (montantAib.longValue()));
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
		// Récupération de la facture à valider
		Optional<Facture> optional = repository.findById(id);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture non trouvée");
		Facture facture = optional.get();
		// Vérification si la facture n'est pas encore valider
		if (facture.isValid())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture déjà validée");
		// Suppression de la facture
		repository.delete(facture);
		// Si tout s'est bien passé, on renvoie true
		return true;
	}

	public Facture finalisationDgi(Facture facture) {
		// Initialisation des données
		// Récupération du parametre system
		Parametre param = paramRepos.findOneParams().orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Votre système n'est pas encore paramètré."));

		// Rcupération de l'utilisateur connecté
		User operateur = userService.getAuthenticated();
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

	public Facture setFactureResponseDgi(InvoiceResponseDto invoiceResponseDto, Facture facture) {
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
		// Retour de la facture
		return facture;
	}

	public Facture setFinalisation(SecurityElementsDto securityElementsDto, Facture facture) {
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
			facture.setOperateur(userService.getAuthenticated());

			// Mise à jour du numero de la facture
			// Récupération du compteur
			var counter = ffdgi.getCounters();
			// Récupération du numéro du compteur
			var numero = counter.substring(counter.indexOf("/") + 1, counter.indexOf(" "));
			// Numéro de la facture
			facture.setNumero(ffdgi.getNim() + "-" + numero);

			// Mise à jour de la date de finalisation de la facture
			try {
				SimpleDateFormat SDFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				// Use of .parse() method to parse
				// Date From String
				String dt = ffdgi.getDateTime();
				cal.setTime(SDFormat.parse(dt));
				facture.setDate(cal.getTime());
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
	public ResponseEntity<byte[]> genererFacture(Long id) throws IOException, JRException {

		// Récupération de la facture
		Optional<Facture> optional = repository.findByIdAndConfirmTrue(id);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture non trouvée");
		Facture facture = optional.get();

		return getInvoicePrint(facture);
	}

	/**
	 * Imprimer la facture et l'envoiyer telle quelle
	 * 
	 * @param facture La facture confirmée par l'emcef
	 * @return
	 * @throws IOException
	 * @throws JRException
	 */
	private ResponseEntity<byte[]> getInvoicePrint(Facture facture) throws IOException, JRException {
		// Récupération des données du parametres
		Parametre param = paramRepos.findOneParams().orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Votre système n'est pas encore paramètré."));

		// Récupération du type de la facture
		var type = facture.getType();
		var template = "";
		if (type.getGroupe().equals(TypeData.FV)) {
			template = INVOICE_REPORT_TEMPLATE_FV;
			if (facture.isRemise())
				template = INVOICE_REPORT_TEMPLATE_FV_REMISE;
		} else if (type.getGroupe().equals(TypeData.FA)) {
			template = INVOICE_REPORT_TEMPLATE_FA;
			if (facture.isRemise())
				template = INVOICE_REPORT_TEMPLATE_FA_REMISE;
		}

		// Les données de la facture
		InvoiceData invoice = reportService.setInvoiceData(facture, param);

		// Setting Invoice Report Params
		HashMap<String, Object> map = setInvoiceReportParams(facture, param);
		// Ajout des données de l'entête
//		map.put("entete", new JRBeanCollectionDataSource(Collections.singleton(invoice)));

		// Générer la facture
		return reportService.invoiceReport(invoice, map, template,
				INVOICE_REPORT_BASE_NAME + facture.getNumero() + ".pdf");
	}

	/**
	 * Imprimer la facture et l'enregistrement en local
	 * 
	 * @param facture La facture confirmée par l'emcef
	 * @return
	 * @throws IOException
	 * @throws JRException
	 */
	private String printInvoiceAndStoreIt(Facture facture) throws IOException, JRException {
		// Récupération des données du parametres
		Parametre param = paramRepos.findOneParams().orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Votre système n'est pas encore paramètré."));

		// Récupération du type de la facture
		var type = facture.getType();
		var template = "";
		if (type.getGroupe().equals(TypeData.FV)) {
			template = INVOICE_REPORT_TEMPLATE_FV;
			if (facture.isRemise())
				template = INVOICE_REPORT_TEMPLATE_FV_REMISE;
		} else if (type.getGroupe().equals(TypeData.FA)) {
			template = INVOICE_REPORT_TEMPLATE_FA;
			if (facture.isRemise())
				template = INVOICE_REPORT_TEMPLATE_FA_REMISE;
		}

		// Les données de la facture
		InvoiceData invoice = reportService.setInvoiceData(facture, param);

		// Setting Invoice Report Params
		HashMap<String, Object> map = setInvoiceReportParams(facture, param);
		// Ajout des données de l'entête
//		map.put("entete", new JRBeanCollectionDataSource(Collections.singleton(invoice)));

		// Générer la facture
		return reportService.invoiceReportAndStoreIt(invoice, map, template,
				INVOICE_REPORT_BASE_NAME + facture.getNumero() + ".pdf");
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
		// Récupération du type de la facture
		TypeFacture tf = tfRepos.findByIdAndGroupe(typeId, TypeData.FA).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le type de la facture non trouvé"));
		// Récupération de la facture d'origine
		Facture factureOrigine = repository.findByIdAndTypeIdAndConfirmTrue(factureId, tf.getOrigine().getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture de vente non trouvée"));
		// Tentative de récupération de la facture d'avoir associée à la facture de
		// vente
		if (repository.findByOrigineRef(factureOrigine.getReference()).isPresent())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"Une facture d'avoir existe déjà pour cette facture de vente");

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
		return repository.save(facture);
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
		facture.setOperateur(userService.getAuthenticated());

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
		// Récupération de la facture à valider
		Optional<Facture> optional = repository.findByIdAndValidFalse(id);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture non trouvée");
		Facture facture = optional.get();
		// Vérification si la facture n'est pas vide
		if (facture.getDetails().isEmpty())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Facture vide");

		var typeFacture = facture.getType();
		if (typeFacture.getGroupe() != TypeData.FA) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le type de la facture n'est pas valide");
		}
		// Finalisation de la facture
		var resultat = finalisationDgi(facture);

		// Enregistrement
		facture = repository.save(resultat);

		// Validation des lignes de la facture
		if (facture.isConfirm()) {
			for (DetailFacture detail : facture.getDetails()) {
				detail.setValid(true);
				dfRepos.save(detail);
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
		if (payload.getDebutAt() != null && payload.getFinAt() != null)
			return repository.findAllByCreatedAtBetween(payload.getDebutAt(), payload.getFinAt());

		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Les dates ne sont pas correctement définies");
	}

	/**
	 * Récupérer la liste des factures en fonction de la date de confirmation
	 * E-MECeF/DGI de ces dernières
	 * 
	 * @param payload L'objet contenant les dates début et fin de la recherche
	 * @return {@link List<Facture>}
	 */
	public List<Facture> getListByConfirmedDate(StatsPayload payload) {
		if (payload.getDebut() != null && payload.getFin() != null)
			return repository.findAllByDateNotNullAndDateBetween(payload.getDebut(), payload.getFin());

		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Les dates ne sont pas correctement définies");
	}
	
	/**
	 * Récupérer la liste des factures en fonction du type des factures et de la date de création de ces dernières
	 * 
	 * @param payload L'objet contenant les dates début et fin de la recherche
	 * @return {@link List<Facture>}
	 */
	public List<Facture> getListByTypeAndCreatedAt(StatsPayload payload, Long typeId) {
		TypeFacture tf = tfRepos.findById(typeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Type de facture non trouvé"));
		
		if (payload.getDebutAt() != null && payload.getFinAt() != null)
			return repository.findAllByTypeAndCreatedAtBetween(tf, payload.getDebutAt(), payload.getFinAt());

		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Les dates ne sont pas correctement définies");
	}

	/**
	 * Récupérer la liste des factures en fonction du type des factures et de la date de confirmation E-MECeF/DGI de ces dernières
	 * 
	 * @param payload L'objet contenant les dates début et fin de la recherche
	 * @return {@link List<Facture>}
	 */
	public List<Facture> getListByTypeAndConfirmedDate(StatsPayload payload, Long typeId) {
		TypeFacture tf = tfRepos.findById(typeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Type de facture non trouvé"));
		
		if (payload.getDebut() != null && payload.getFin() != null)
			return repository.findAllByTypeAndDateNotNullAndDateBetween(tf, payload.getDebut(), payload.getFin());

		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Les dates ne sont pas correctement définies");
	}

}
