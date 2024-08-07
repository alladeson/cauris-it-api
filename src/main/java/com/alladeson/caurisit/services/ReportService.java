package com.alladeson.caurisit.services;

import com.alladeson.caurisit.config.AppConfig;
import com.alladeson.caurisit.models.entities.Article;
import com.alladeson.caurisit.models.entities.Client;
import com.alladeson.caurisit.models.entities.CommandeFournisseur;
import com.alladeson.caurisit.models.entities.DetailFacture;
import com.alladeson.caurisit.models.entities.Facture;
import com.alladeson.caurisit.models.entities.FactureResponseDgi;
import com.alladeson.caurisit.models.entities.Parametre;
import com.alladeson.caurisit.models.entities.ReglementFacture;
import com.alladeson.caurisit.models.entities.TypeData;
import com.alladeson.caurisit.models.reports.BilanPeriodiqueData;
import com.alladeson.caurisit.repositories.FactureRepository.BilanRecapMontant;
import com.alladeson.caurisit.models.reports.ClientData;
import com.alladeson.caurisit.models.reports.CmdFournisseurData;
import com.alladeson.caurisit.models.reports.CompanyContact;
import com.alladeson.caurisit.models.reports.ConfigReportData;
import com.alladeson.caurisit.models.reports.ConfigTableData;
import com.alladeson.caurisit.models.reports.InventaireData;
import com.alladeson.caurisit.models.reports.InventaireDetailData;
import com.alladeson.caurisit.models.reports.InvoiceData;
import com.alladeson.caurisit.models.reports.InvoiceDetailData;
import com.alladeson.caurisit.models.reports.InvoicePayement;
import com.alladeson.caurisit.models.reports.InvoiceRecapData;
import com.alladeson.caurisit.models.reports.cmdFournisseurDetailData;
import com.alladeson.caurisit.utils.Tool;
import net.sf.jasperreports.engine.JRException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * @author William ALLADE
 *
 */
@Service
public class ReportService {

	@Autowired
	private Tool tool;
	@Autowired
	private AppConfig appConfig;

	/**
	 * Instanciation et mise à jour des informations du client pour l'impression de
	 * la facture
	 * 
	 * @param client
	 * @return {@link ClientData}
	 */
	public ClientData setClientData(Client client) {
		// Instanciation de ClientData
		ClientData clientData = new ClientData();
		// Mise à jour des champs
		clientData.setName(client.getName());
		clientData.setIfu(client.getIfu());
		clientData.setAddress(client.getAddress());
		clientData.setContact(client.getContact());
		clientData.setTelephone(client.getTelephone());
		clientData.setEmail(client.getEmail());

		return clientData;
	}

	/**
	 * Instanciation des données de remplissage de la liste des lignes de la facture
	 * pour l'impression
	 * 
	 * @param facture
	 * @return {@link List<InvoiceDetailData>}
	 */
	public List<InvoiceDetailData> setInvoiceDetailData(Facture facture) {
		// Instanciation de la liste des InvoiceDetailData
		List<InvoiceDetailData> liste = new ArrayList<InvoiceDetailData>();

		// Récupération de la liste des détails de la facture
		var details = facture.getDetails();

		int i = 1;
		for (DetailFacture detail : details) {
			var invoiceDetail = new InvoiceDetailData();
			// System.out.println("Id de ligne de la facture : " + detail.getId());
			invoiceDetail.setNumero(i);
			invoiceDetail.setName(detail.getName());
			invoiceDetail.setTaxe(detail.getTaxe().getAbreviation());
			invoiceDetail.setPrix_u(detail.getPrixUnitaire().longValue());
			invoiceDetail.setQte(detail.getQuantite());
			// Formatage du montant ttc
			invoiceDetail.setMontant_ttc(
					formatNumber(detail.getMontantTtc()) + " [" + detail.getTaxe().getGroupe().name() + "]");
			liste.add(invoiceDetail);
			// Gestion de la taxe spécifique
			if (detail.getTs() != null) {
				// Récupération de la taxe spécifique
				var ts = detail.getTs();
				var invoiceDetailTs = new InvoiceDetailData();
				invoiceDetailTs.setNumero(++i);
				invoiceDetailTs.setName("TS ("
						+ ((ts.getName() != null && !ts.getName().isBlank()) ? ts.getName() : "Taxe spécifique") + ")");
				invoiceDetailTs.setTaxe(ts.getTaxe().getAbreviation());
				invoiceDetailTs.setPrix_u(Math.round(ts.getTsUnitaireTtc()));
				invoiceDetailTs.setQte(ts.getQuantite());
				invoiceDetailTs
						.setMontant_ttc(formatNumber(ts.getTsTotal()) + " [" + ts.getTaxe().getGroupe().name() + "]");
				liste.add(invoiceDetailTs);
			}
			// Gestion de la remise
			if (detail.isRemise()) {
				var discount = detail.getDiscount();
				invoiceDetail.setRemise(discount.getTaux() + "%");
				invoiceDetail.setPrix_u(discount.getOriginalPrice().longValue());
			}
			i++;
		}

		return liste;
	}

	/**
	 * @param number
	 * @return
	 */
	private String formatNumber(Double number) {
		var symbole = new DecimalFormatSymbols(Locale.ITALIAN);
		var montantTtc = new DecimalFormat("#,###.##", symbole).format(Double.valueOf(Math.round(number)));
		return montantTtc;
	}

	/**
	 * Intantiation de mise à jour des données de payement de la facture pour
	 * l'impression
	 * 
	 * @param reglement
	 * @return {@link InvoicePayement}
	 */
	public InvoicePayement setInvoicePayement(ReglementFacture reglement) {
		// Intanciation de InvoicePayement
		InvoicePayement payement = new InvoicePayement();
		// Mise à jour des champs
		payement.setType_payement(reglement.getTypePaiement().getDescription());
		payement.setMontant(reglement.getMontantRecu().longValue());
		payement.setPayer(reglement.getMontantPayer().longValue());
		payement.setRendu(reglement.getMontantRendu().longValue());
		return payement;
	}

	/**
	 * Définition de la liste des recaps de la facture
	 * 
	 * @param recapdgi
	 * @param facture
	 * @return
	 */
	public List<InvoiceRecapData> setInvoiceRecapData(FactureResponseDgi recapdgi, Facture facture) {
		// Récupération du typ de la facture, utile en cas de factures d'avoir
		var fa = facture.getType().getGroupe() == TypeData.FA;
		// Instanciation de la liste InvoiceRecapData
		List<InvoiceRecapData> recaps = new ArrayList<InvoiceRecapData>();
		// Instancition et mise à jour de recaps
		// Pour le groupe exonéré A (0%)
		if (recapdgi.getTaa() != 0) {
			InvoiceRecapData recap = new InvoiceRecapData();
			recap.setTaxe_group("A - Exonéré");
			recap.setTotal(fa ? (recapdgi.getTaa() * (-1)) : recapdgi.getTaa());
			recap.setImposable(0l);
			recap.setImpot(0l);
			// Ajout à la liste des recaps
			recaps.add(recap);
		}
		// Pour le groupe de taxation B (18%)
		if (recapdgi.getTab() != 0) {
			InvoiceRecapData recap = new InvoiceRecapData();
			recap.setTaxe_group("B - Taxable (18%)");
			recap.setTotal(fa ? (recapdgi.getTab() * (-1)) : recapdgi.getTab());
			recap.setImposable(fa ? (recapdgi.getHab() * (-1)) : recapdgi.getHab());
			recap.setImpot(fa ? (recapdgi.getVab() * (-1)) : recapdgi.getVab());
			// Ajout à la liste des recaps
			recaps.add(recap);
		}
		// Pour le groupe C (Exportation de produits taxables) 0%
		if (recapdgi.getTac() != 0) {
			InvoiceRecapData recap = new InvoiceRecapData();
			recap.setTaxe_group("C - Exportation");
			recap.setTotal(fa ? (recapdgi.getTac() * (-1)) : recapdgi.getTac());
			recap.setImposable(0l);
			recap.setImpot(0l);
			// Ajout à la liste des recaps
			recaps.add(recap);
		}
		// Pour le groupe de taxation D (18%)
		if (recapdgi.getTad() != 0) {
			InvoiceRecapData recap = new InvoiceRecapData();
			recap.setTaxe_group("D - Exception (18%)");
			recap.setTotal(fa ? (recapdgi.getTad() * (-1)) : recapdgi.getTad());
			recap.setImposable(fa ? (recapdgi.getHad() * (-1)) : recapdgi.getHad());
			recap.setImpot(fa ? (recapdgi.getVad() * (-1)) : recapdgi.getVad());
			// Ajout à la liste des recaps
			recaps.add(recap);
		}
		// Pour le groupe E (Régime fiscal TPS) 0%
		if (recapdgi.getTae() != 0) {
			InvoiceRecapData recap = new InvoiceRecapData();
			recap.setTaxe_group("E - TPS");
			recap.setTotal(fa ? (recapdgi.getTae() * (-1)) : recapdgi.getTae());
			recap.setImposable(0l);
			recap.setImpot(0l);
			// Ajout à la liste des recaps
			recaps.add(recap);
		}
		// Pour le groupe F (Réservé) 0%
		if (recapdgi.getTaf() != 0) {
			InvoiceRecapData recap = new InvoiceRecapData();
			recap.setTaxe_group("F - Réservé");
			recap.setTotal(fa ? (recapdgi.getTaf() * (-1)) : recapdgi.getTaf());
			recap.setImposable(0l);
			recap.setImpot(0l);
			// Ajout à la liste des recaps
			recaps.add(recap);
		}
		// Pour la taxe spécifique
		if (recapdgi.getTs() != 0) {
			InvoiceRecapData recap = new InvoiceRecapData();
			recap.setTaxe_group("TS");
			recap.setTotal(fa ? (recapdgi.getTs() * (-1)) : recapdgi.getTs());
			recap.setImposable(0l);
			recap.setImpot(0l);
			// Ajout à la liste des recaps
			recaps.add(recap);
		}

		// Pour le groupe Aib
		if (facture.getAib() != null) {
			InvoiceRecapData recap = new InvoiceRecapData();
			var aib = facture.getAib();
			// recap.setTaxe_group(aib.getGroupe().name() + " (" + aib.getValeur() + "%)");
			recap.setTaxe_group("AIB (" + aib.getValeur() + "%)");
			recap.setTotal(fa ? (recapdgi.getAib() * (-1)) : recapdgi.getAib());
			recap.setImposable(0l);
			recap.setImpot(0l);
			// Ajout à la liste des recaps
			recaps.add(recap);
		}

		return recaps;
	}

	/**
	 * Mise en place des données de la facture pour l'impression
	 * 
	 * @param facture
	 * @param params
	 * @return {@link InvoiceData}
	 */
	public InvoiceData setInvoiceData(Facture facture, Parametre params) {
		// Instanciation de InvoiceData
		var invoice = new InvoiceData();
		// Mise à jour des champs pour la facture
		invoice.setInvoice_id(facture.getNumero());
		invoice.setInvoice_date(Tool.formatDate(facture.getDate(), "dd/MM/yyyy HH:mm:ss"));
		invoice.setInvoice_type(facture.getType().getDescription());
		invoice.setInvoice_total(Math.round(facture.getMontantTtc()));
		invoice.setInvoice_total_toWord(Tool.convert(Math.abs(invoice.getInvoice_total().longValue())));
		invoice.setInvoice_operator(facture.getOperateur().getId() + " " + facture.getOperateur().getFullname());
		invoice.setInvoice_origine_ref(facture.getOrigineRef());
		// Mise à jour des champs pour la société
		invoice.setSte_name(params.getName());
		invoice.setSte_ifu(params.getIfu());
		invoice.setSte_address(params.getAddress());
		invoice.setSte_contact(params.getContact());
		invoice.setSte_email(params.getEmail());
		invoice.setSte_pays(params.getPays());
		invoice.setSte_rccm(params.getRcm());
		invoice.setSte_telephone(params.getTelephone());
		invoice.setSte_raisonSociale(params.getRaisonSociale());
		invoice.setSte_ville(params.getVille());
		// Mise à jour du logo de la société
		invoice.setSte_logo(appConfig.getUploadDir() + "/" + params.getLogo());
		// Mise à jour du numéro nim de la machine e-mcef
		invoice.setEmcef_nim(params.getNim());
		return invoice;
	}

	/**
	 * Définition des infos de contact de la société
	 * 
	 * @param params Les infos de paramètre de la socité
	 * @return {@link CompanyContact} Le contact défini
	 */
	public CompanyContact setCompanyContact(Parametre params) {
		// Instanciation de companyContact
		var contact = new CompanyContact();
		// Mise à jour des champs de contact
		contact.setAddress(params.getAddress());
		contact.setVille(params.getVille());
		contact.setContact(params.getContact());
		contact.setEmcef(params.getNim());
		contact.setTelephone(params.getTelephone());
		contact.setEmail(params.getEmail());
		// Renvoie du contact
		return contact;
	}

	/**
	 * Générer la facture normalisée
	 * 
	 * @param invoice
	 * @param params
	 * @param invoiceTemplate
	 * @param invoiceFileName
	 * @return
	 * @throws IOException
	 * @throws JRException
	 */
	public ResponseEntity<byte[]> invoiceReport(InvoiceData invoice, HashMap<String, Object> params,
			String invoiceTemplate, String invoiceFileName) throws IOException, JRException {
		return tool.generatePdf(Collections.singleton(invoice), params, invoiceTemplate, invoiceFileName);
	}

	/**
	 * Générer la facture normalisée et l'enregistrer
	 * 
	 * @param invoice
	 * @param params
	 * @param invoiceTemplate
	 * @param invoiceFileName
	 * @return Le nom du fichier de la facture
	 * @throws IOException
	 * @throws JRException
	 */
	public String invoiceReportAndStoreIt(InvoiceData invoice, HashMap<String, Object> params, String invoiceTemplate,
			String invoiceFileName) throws IOException, JRException {
		return tool.generatePdfAndStoreIt(Collections.singleton(invoice), params, invoiceTemplate, invoiceFileName);
	}

	/*** Bilan Périodique ***/

	/**
	 * Mise en place des données du bilan périodique pour l'impression
	 * 
	 * @param params
	 * @return {@link BilanPeriodiqueData}
	 */
	public BilanPeriodiqueData setBilanPeriodiqueData(Parametre params) {
		// Instanciation de InvoiceData
		var bilan = new BilanPeriodiqueData();
		// Mise à jour des champs pour la société
		bilan.setSte_name(params.getName());
		bilan.setSte_ifu(params.getIfu());
		bilan.setSte_address(params.getAddress());
		bilan.setSte_contact(params.getContact());
		bilan.setSte_email(params.getEmail());
		bilan.setSte_pays(params.getPays());
		bilan.setSte_rccm(params.getRcm());
		bilan.setSte_telephone(params.getTelephone());
		bilan.setSte_raisonSociale(params.getRaisonSociale());
		bilan.setSte_ville(params.getVille());
		// Mise à jour du logo de la société
		bilan.setSte_logo(appConfig.getUploadDir() + "/" + params.getLogo());
		// Mise à jour du numéro nim de la machine e-mcef
		bilan.setEmcef_nim(params.getNim());
		return bilan;
	}

	/**
	 * Définition de la liste des recaps de la facture
	 * 
	 * @param recapdgi
	 * @param facture
	 * @return
	 */
	public List<InvoiceRecapData> setBilanPeriodiqueRecapData(BilanRecapMontant recapdgi, boolean fa) {
		// Instanciation de la liste InvoiceRecapData
		List<InvoiceRecapData> recaps = new ArrayList<InvoiceRecapData>();
		// Instancition et mise à jour de recaps
		// Pour le groupe exonéré A (0%)
		if (recapdgi.getTaa() != 0) {
			InvoiceRecapData recap = new InvoiceRecapData();
			recap.setTaxe_group("A - Exonéré");
			recap.setTotal(fa ? (recapdgi.getTaa() * (-1)) : recapdgi.getTaa());
			recap.setImposable(0l);
			recap.setImpot(0l);
			// Ajout à la liste des recaps
			recaps.add(recap);
		}
		// Pour le groupe de taxation B (18%)
		if (recapdgi.getTab() != 0) {
			InvoiceRecapData recap = new InvoiceRecapData();
			recap.setTaxe_group("B - Taxable (18%)");
			recap.setTotal(fa ? (recapdgi.getTab() * (-1)) : recapdgi.getTab());
			recap.setImposable(fa ? (recapdgi.getHab() * (-1)) : recapdgi.getHab());
			recap.setImpot(fa ? (recapdgi.getVab() * (-1)) : recapdgi.getVab());
			// Ajout à la liste des recaps
			recaps.add(recap);
		}
		// Pour le groupe C (Exportation de produits taxables) 0%
		if (recapdgi.getTac() != 0) {
			InvoiceRecapData recap = new InvoiceRecapData();
			recap.setTaxe_group("C - Exportation");
			recap.setTotal(fa ? (recapdgi.getTac() * (-1)) : recapdgi.getTac());
			recap.setImposable(0l);
			recap.setImpot(0l);
			// Ajout à la liste des recaps
			recaps.add(recap);
		}
		// Pour le groupe de taxation D (18%)
		if (recapdgi.getTad() != 0) {
			InvoiceRecapData recap = new InvoiceRecapData();
			recap.setTaxe_group("D - Exception (18%)");
			recap.setTotal(fa ? (recapdgi.getTad() * (-1)) : recapdgi.getTad());
			recap.setImposable(fa ? (recapdgi.getHad() * (-1)) : recapdgi.getHad());
			recap.setImpot(fa ? (recapdgi.getVad() * (-1)) : recapdgi.getVad());
			// Ajout à la liste des recaps
			recaps.add(recap);
		}
		// Pour le groupe E (Régime fiscal TPS) 0%
		if (recapdgi.getTae() != 0) {
			InvoiceRecapData recap = new InvoiceRecapData();
			recap.setTaxe_group("E - TPS");
			recap.setTotal(fa ? (recapdgi.getTae() * (-1)) : recapdgi.getTae());
			recap.setImposable(0l);
			recap.setImpot(0l);
			// Ajout à la liste des recaps
			recaps.add(recap);
		}
		// Pour le groupe F (Réservé) 0%
		if (recapdgi.getTaf() != 0) {
			InvoiceRecapData recap = new InvoiceRecapData();
			recap.setTaxe_group("F - Réservé");
			recap.setTotal(fa ? (recapdgi.getTaf() * (-1)) : recapdgi.getTaf());
			recap.setImposable(0l);
			recap.setImpot(0l);
			// Ajout à la liste des recaps
			recaps.add(recap);
		}
		// Pour la taxe spécifique
		if (recapdgi.getTs() != 0) {
			InvoiceRecapData recap = new InvoiceRecapData();
			recap.setTaxe_group("TS");
			recap.setTotal(fa ? (recapdgi.getTs() * (-1)) : recapdgi.getTs());
			recap.setImposable(0l);
			recap.setImpot(0l);
			// Ajout à la liste des recaps
			recaps.add(recap);
		}

		// Pour le groupe Aib
		if (recapdgi.getAib() != 0) {
			InvoiceRecapData recap = new InvoiceRecapData();
			recap.setTaxe_group("AIB");
			recap.setTotal(fa ? (recapdgi.getAib() * (-1)) : recapdgi.getAib());
			recap.setImposable(0l);
			recap.setImpot(0l);
			// Ajout à la liste des recaps
			recaps.add(recap);
		}

		if (recaps.size() == 0) {
			InvoiceRecapData recap = new InvoiceRecapData();
			recap.setTaxe_group("-");
			recap.setTotal(0l);
			recap.setImposable(0l);
			recap.setImpot(0l);
			// Ajout à la liste des recaps
			recaps.add(recap);
		}
		return recaps;
	}

	/**
	 * Générer le bilan périodique
	 * 
	 * @param bilanData
	 * @param params
	 * @param bilanTemplate
	 * @param bilanFileName
	 * @return
	 * @throws IOException
	 * @throws JRException
	 */
	public ResponseEntity<byte[]> bilanPeriodiqueReport(BilanPeriodiqueData bilanData, HashMap<String, Object> params,
			String bilanTemplate, String bilanFileName) throws IOException, JRException {
		return tool.generatePdf(Collections.singleton(bilanData), params, bilanTemplate, bilanFileName);
	}

	/** Rapport de configuration **/

	/**
	 * Instanciation et mise à jour des informations du rapport de configuration
	 * pour l'impression
	 * 
	 * @param Parametre
	 * @return {@link ConfigReportData}
	 */
	public ConfigReportData setConfigReportData(Parametre params) {
		// Instanciation
		var configData = new ConfigReportData();
		// Mise à jour des champs pour la société
		configData.setSte_name(appConfig.getAppName());
		configData.setSte_ifu(appConfig.getAppIfu());
		// configData.setSte_address(params.getAddress());
		// configData.setSte_contact(params.getContact());
		// configData.setSte_email(params.getEmail());
		// configData.setSte_pays(params.getPays());
		configData.setSte_rccm(appConfig.getAppRcm());
		// configData.setSte_telephone(params.getTelephone());
		// configData.setSte_raisonSociale(params.getRaisonSociale());
		// configData.setSte_ville(params.getVille());
		// Mise à jour du logo de la société
		configData.setSte_logo(appConfig.getAppLogo());
		// Mise à jour de la clé de sécurité
		configData.setSerialKey(params.getSerialKey());
		// Mise à jour de la date de configuration
		// Récupération de la date de creation
		configData.setDate(Tool.formatDate(params.getActivationDate(), "dd/MM/yyyy HH:mm:ss"));
		// Renvoie des donné de confgiuration
		return configData;
	}

	/**
	 * Instanciation et mise à jour des informations de la sociéteé du rapport de
	 * configuration pour l'impression
	 * 
	 * @param Parametre
	 * @return {@link List<ConfigTableData>}
	 */
	public List<ConfigTableData> setConfigCompanyData(Parametre params) {
		// Creation de liste
		List<ConfigTableData> data = new ArrayList<ConfigTableData>();
		// Instanciation
		var tableData = new ConfigTableData();
		// Mise à jour des champs pour la société
		tableData.setField("IFU");
		tableData.setContent(params.getIfu());
		data.add(tableData);

		tableData = new ConfigTableData();
		tableData.setField("Nom ou Raison sociale");
		tableData.setContent(params.getName());
		data.add(tableData);

		tableData = new ConfigTableData();
		tableData.setField("RCCM");
		tableData.setContent(params.getRcm());
		data.add(tableData);

		tableData = new ConfigTableData();
		tableData.setField("Adresse");
		tableData.setContent(params.getAddress());
		data.add(tableData);

		tableData = new ConfigTableData();
		tableData.setField("Ville");
		tableData.setContent(params.getVille());
		data.add(tableData);

		tableData = new ConfigTableData();
		tableData.setField("Nom ou Raison sociale");
		tableData.setContent(params.getPays());
		data.add(tableData);

		tableData = new ConfigTableData();
		tableData.setField("Téléphone");
		tableData.setContent(params.getTelephone());
		data.add(tableData);

		tableData = new ConfigTableData();
		tableData.setField("E-mail");
		tableData.setContent(params.getEmail());
		data.add(tableData);

		// Renvoie des donné de confgiuration
		return data;
	}

	/**
	 * Instanciation et mise à jour des informations de l'emecef du rapport de
	 * configuration pour l'impression
	 * 
	 * @param Parametre
	 * @return {@link List<ConfigTableData>}
	 */
	public List<ConfigTableData> setConfigEmecefData(Parametre params) {
		// Creation de liste
		List<ConfigTableData> data = new ArrayList<ConfigTableData>();
		// Instanciation
		var tableData = new ConfigTableData();
		// Mise à jour des champs pour la société
		tableData.setField("NIM");
		tableData.setContent(params.getNim());
		data.add(tableData);

		tableData = new ConfigTableData();
		tableData.setField("Mode");
		tableData.setContent(params.getTypeSystem().name());
		data.add(tableData);

		tableData = new ConfigTableData();
		tableData.setField("Expiration");
		tableData.setContent(Tool.formatDate(params.getExpiration(), "dd/MM/yyyy HH:mm:ss"));
		data.add(tableData);

		// Renvoie des donné de confgiuration
		return data;
	}

	/**
	 * Générer le rapport de configuration
	 * 
	 * @param reportData
	 * @param params
	 * @param bilanTemplate
	 * @param bilanFileName
	 * @return
	 * @throws IOException
	 * @throws JRException
	 */
	public ResponseEntity<byte[]> generateConfigReport(ConfigReportData reportData, HashMap<String, Object> params,
			String reportTemplate, String reportFileName) throws IOException, JRException {
		return tool.generateConfigReport(Collections.singleton(reportData), params, reportTemplate, reportFileName);
	}

	/** Gestion de la commande du fournisseur **/
	/**
	 * Mise en place des données de la commande pour l'impression
	 * 
	 * @param cmdf   La commandeFournisseur à imprimer
	 * @param params Les données de paramètre de l'application
	 * @return {@link CmdFournisseurData}
	 */
	public CmdFournisseurData setCmdFournisseurData(CommandeFournisseur cmdf, Parametre params) {
		// Instanciation de InvoiceData
		CmdFournisseurData cmdfData = new CmdFournisseurData();
		// Mise à jour des champs pour la commande
		cmdfData.setNumero(cmdf.getNumero());
		cmdfData.setDateCreation(Tool.formatDate(cmdf.getDateCreation(), "dd/MM/yyyy"));
		cmdfData.setDateLivraison(Tool.formatDate(cmdf.getDateLivraison(), "dd/MM/yyyy"));
		cmdfData.setReferenceExterne(cmdf.getReferenceExterne());
		cmdfData.setNotes(cmdf.getNotes());
		cmdfData.setQteTotal(cmdf.getQteTotal());
		cmdfData.setMontantRemise(cmdf.getMontantRemise());
		cmdfData.setMontantHt(cmdf.getMontantHt());
		cmdfData.setMontantTva(cmdf.getMontantTva());
		cmdfData.setMontantTtc(cmdf.getMontantTtc());
		// Mise à jour des champs pour la société
		cmdfData.setSteName(params.getName());
		cmdfData.setSteIfu(params.getIfu());
		cmdfData.setSteAddress(params.getAddress());
		cmdfData.setSteVille(params.getVille());
		cmdfData.setSteAddressContact(params.getAddressContact());
		cmdfData.setSteEmail(params.getEmail());
		cmdfData.setSteRccm(params.getRcm());
		cmdfData.setSteTelephone(params.getTelephone());
		// Mise à jour du logo de la société
		cmdfData.setSteLogo(appConfig.getUploadDir() + "/" + params.getLogo());

		// Les données du fournisseur
		cmdfData.setFournisseurName(cmdf.getFournisseur().getName());
		cmdfData.setFournisseurAddress(cmdf.getFournisseur().getAddress());
		cmdfData.setFournisseurVille(cmdf.getFournisseur().getVille());
		cmdfData.setFournisseurAddressContact(cmdf.getFournisseur().getAddressContact());
		cmdfData.setFournisseurTelephone(cmdf.getFournisseur().getTelephone());

		// Renvoie des données
		return cmdfData;
	}

	/**
	 * Instanciation des données de remplissage de la liste des lignes de la facture
	 * pour l'impression
	 * 
	 * @param cmdf La commandeFournisseur à imprimer
	 * @return {@link List<cmdFournisseurDetailData>}
	 */
	public List<cmdFournisseurDetailData> setCmdFournisseurDetailData(CommandeFournisseur cmdf) {
		// Instanciation de la liste des cmdFournisseurDetailData
		List<cmdFournisseurDetailData> liste = new ArrayList<cmdFournisseurDetailData>();

		// Récupération de la liste des détails de la facture
		var details = cmdf.getDetails();

		// Pour récupérer le détail de l'expédition 
		// Utile pour l'ajouter à la fin de la liste
		var detailExpedition = new cmdFournisseurDetailData();

		for (var detail : details) {
			// Initialisation du detail pour le reporting
			var cmdfDetail = new cmdFournisseurDetailData();
			// Mise à jour des champs du detail
			cmdfDetail.setId(detail.getId());
			cmdfDetail.setReference(detail.getReference());
			cmdfDetail.setName(detail.getName());
			cmdfDetail.setTaxe(detail.getTaxe().getValeur());
			cmdfDetail.setPrixUht(detail.isRemise() ? detail.getDiscount().getOriginalPrice() : detail.getPrixUht());
			cmdfDetail.setQuantite(detail.getQuantite());
			cmdfDetail.setMontantHt(detail.getMontantHt());
			cmdfDetail.setRemise(detail.isRemise() ? detail.getDiscount().getTaux() : 0);
			// Si c'est un detail des données d'expédition, on le récupère car il sera
			// ajouté à la fin de la liste
			if (detail.isExpedition()) {
				detailExpedition = cmdfDetail;
			} else { // Ajouter tous les autres détails à la liste
				// Ajout du detail à la liste
				liste.add(cmdfDetail);
			}
		}
		
		// Ajout de l'expéditon à la liste en dernière postion
		// Si elle existe bien-sur
		if(detailExpedition.getId() != null) {
			liste.add(detailExpedition);		
		}
		// Retourner la liste
		return liste;
	}

	/**
	 * Générer le pdf de la commande et le renvoyer
	 * 
	 * @param cmdf         La commandeFournisseur à imprimer
	 * @param reportParams Les données de paramètre pour JasperReport
	 * @param cmdfTemplate Le fichier du template de l'impression
	 * @param cmdfFileName Le nom du fichier de sortie
	 * @return {@link ResponseEntity}
	 * @throws IOException
	 * @throws JRException
	 */
	public ResponseEntity<byte[]> cmdFournisseurReport(CmdFournisseurData cmdf, HashMap<String, Object> reportParams,
			String cmdfTemplate, String cmdfFileName) throws IOException, JRException {
		return tool.generatePdf(Collections.singleton(cmdf), reportParams, cmdfTemplate, cmdfFileName);
	}
	
	/** Gestion de la fichie d'inventaire de stock **/
	/**
	 * Mise en place des données d'inventaire pour l'impression
	 * 
	 * @param params Les données de paramètre de l'application
	 * @return {@link InventaireData}
	 */
	public InventaireData setInventaireData(Parametre params) {
		// Instanciation de InvoiceData
		InventaireData inventaire = new InventaireData();
		// Mise à jour des champs pour la commande
		inventaire.setDate(Tool.formatDate(new Date(), "dd/MM/yyyy"));
		
		// Mise à jour des champs pour la société
		inventaire.setSteName(params.getName());
		inventaire.setSteIfu(params.getIfu());
		inventaire.setSteAddress(params.getAddress());
		inventaire.setSteVille(params.getVille());
		inventaire.setSteAddressContact(params.getAddressContact());
		inventaire.setSteEmail(params.getEmail());
		inventaire.setSteRccm(params.getRcm());
		inventaire.setSteTelephone(params.getTelephone());
		// Mise à jour du logo de la société
		inventaire.setSteLogo(appConfig.getUploadDir() + "/" + params.getLogo());

		// Renvoie des données
		return inventaire;
	}

	/**
	 * Instanciation des données de remplissage de la liste des lignes de l'inventaire pour l'impression
	 * 
	 * @return {@link List<cmdFournisseurDetailData>}
	 */
	public List<InventaireDetailData> setInventaireDetailData(List<Article> articles) {
		// Instanciation de la liste des inventaireDetailData
		List<InventaireDetailData> liste = new ArrayList<InventaireDetailData>();

		for (Article article : articles) {
			// Initialisation du detail pour le reporting
			var inventaireDetail = new InventaireDetailData();
			// Mise à jour des champs du detail
			inventaireDetail.setReference(article.getReference());
			inventaireDetail.setName(article.getDesignation());
			inventaireDetail.setQte(article.getStock());
			liste.add(inventaireDetail);
		}
		
		// Retourner la liste
		return liste;
	}

	/** 
	 * Générer le pdf et le renvoyer
	 * 
	 * @param collection L'ensemble des données du reporting, ex: Collections.singleton(Object)
	 * @param reportParams Les données de paramètre du reporting, ex: new JRBeanCollectionDataSource(List<Object>)
	 * @param template Le chemin vers le fichier.jrxml du reporting
	 * @param filename Le nom du fichier de sortie
	 * @return {@link ResponseEntity<byte[]>}
	 * @throws IOException
	 * @throws JRException
	 */
	public ResponseEntity<byte[]> generateReport(Collection<?> collection, HashMap<String, Object> reportParams,
			String template, String filename) throws IOException, JRException {
		return tool.generatePdf(collection, reportParams, template, filename);
	}

}
