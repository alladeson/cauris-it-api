package com.alladeson.caurisit.services;

import com.alladeson.caurisit.config.AppConfig;
import com.alladeson.caurisit.models.entities.Client;
import com.alladeson.caurisit.models.entities.DetailFacture;
import com.alladeson.caurisit.models.entities.Facture;
import com.alladeson.caurisit.models.entities.FactureResponseDgi;
import com.alladeson.caurisit.models.entities.Parametre;
import com.alladeson.caurisit.models.entities.ReglementFacture;
import com.alladeson.caurisit.models.entities.TypeData;
import com.alladeson.caurisit.models.reports.ClientData;
import com.alladeson.caurisit.models.reports.CompanyContact;
import com.alladeson.caurisit.models.reports.InvoiceData;
import com.alladeson.caurisit.models.reports.InvoiceDetailData;
import com.alladeson.caurisit.models.reports.InvoicePayement;
import com.alladeson.caurisit.models.reports.InvoiceRecapData;
import com.alladeson.caurisit.utils.Tool;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
	 * Instanciation de remplissage de la liste des lignes de la facture pour
	 * l'impression
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
//			System.out.println("Id de ligne de la facture : " + detail.getId());
			invoiceDetail.setNumero(i);
			invoiceDetail.setName(detail.getName());
			invoiceDetail.setPrix_u(detail.getPrixUnitaire().longValue());
			invoiceDetail.setQte(detail.getQuantite());
			invoiceDetail.setMontant_ttc(
					Math.round(detail.getMontantTtc()) + " [" + detail.getTaxe().getGroupe().name() + "]");
			liste.add(invoiceDetail);
			// Gestion de la taxe spécifique
			if (detail.getTs() != null) {
				// Récupération de la taxe spécifique
				var ts = detail.getTs();
				var invoiceDetailTs = new InvoiceDetailData();
				invoiceDetailTs.setNumero(++i);
				invoiceDetailTs.setName("TS (" + ((ts.getName() != null && !ts.getName().isBlank()) ? ts.getName() : "Taxe spécifique") + ")");
				invoiceDetailTs.setPrix_u(Math.round(ts.getTsUnitaireTtc()));
				invoiceDetailTs.setQte(ts.getQuantite());
				invoiceDetailTs.setMontant_ttc(Math.round(ts.getTsTotal()) + " ["
						+ ts.getTaxe().getGroupe().name() + "]");
				liste.add(invoiceDetailTs);
			}
			// Gestion de la remise
			if(detail.isRemise()) {
				invoiceDetail.setRemise(detail.getDiscount().getTaux() + "%");
			}
			i++;
		}

		return liste;
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
		payement.setType_payement(reglement.getTypePaiement().getType().name());
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
//			recap.setTaxe_group(aib.getGroupe().name() + " (" + aib.getValeur() + "%)");
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
	public ResponseEntity<byte[]> invoiceReport(InvoiceData invoice, HashMap<String, Object> params, String invoiceTemplate, String invoiceFileName)
			throws IOException, JRException {
		return tool.generateInvoice(Collections.singleton(invoice), params, invoiceTemplate, invoiceFileName);
	}
	
	/**
	 * Générer la facture normalisée et l'enregistrer
	 * @param invoice
	 * @param params
	 * @param invoiceTemplate
	 * @param invoiceFileName
	 * @return
	 * @throws IOException
	 * @throws JRException
	 */
	public String invoiceReportAndStoreIt(InvoiceData invoice, HashMap<String, Object> params, String invoiceTemplate, String invoiceFileName)
			throws IOException, JRException {
		return tool.generateInvoiceAndStoreIt(Collections.singleton(invoice), params, invoiceTemplate, invoiceFileName);
	}
}
