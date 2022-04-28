/**
 * 
 */
package com.alladeson.caurisit.models.reports;

import java.util.Date;

/**
 * @author allad
 *
 */
public class InvoiceData {

	// Le numéro de la facture
	private String invoice_id;
	// La date de confirmation de la facture
	private Date invoice_date;
	// Le type de la facture
	private String invoice_type;
	// Le montant total de la facture
	private Long invoice_total;
	// Le montant total de la facture en lettre
	private String invoice_total_toWord;
	// Le nom de l'agent opérateur ayant établi la facture
	private String invoice_operator;

	// Les informations de la société
	private String ste_ifu;
	private String ste_name;
	private String ste_telephone;
	private String ste_email;
	private String ste_address;
	private String ste_raisonSociale;
	private String ste_ville;
	private String ste_pays;
	private String ste_rccm;
	private String ste_logo;
	private String ste_contact;
	// Le numero de la machine e-mcef
	private String emcef_nim;

	/**
	 * @return the invoice_id
	 */
	public String getInvoice_id() {
		return invoice_id;
	}

	/**
	 * @param invoice_id the invoice_id to set
	 */
	public void setInvoice_id(String invoice_id) {
		this.invoice_id = invoice_id;
	}

	/**
	 * @return the invoice_date
	 */
	public Date getInvoice_date() {
		return invoice_date;
	}

	/**
	 * @param invoice_date the invoice_date to set
	 */
	public void setInvoice_date(Date invoice_date) {
		this.invoice_date = invoice_date;
	}

	/**
	 * @return the invoice_type
	 */
	public String getInvoice_type() {
		return invoice_type;
	}

	/**
	 * @param invoice_type the invoice_type to set
	 */
	public void setInvoice_type(String invoice_type) {
		this.invoice_type = invoice_type;
	}

	/**
	 * @return the invoice_total
	 */
	public Long getInvoice_total() {
		return invoice_total;
	}

	/**
	 * @param invoice_total the invoice_total to set
	 */
	public void setInvoice_total(Long invoice_total) {
		this.invoice_total = invoice_total;
	}

	/**
	 * @return the invoice_total_toWord
	 */
	public String getInvoice_total_toWord() {
		return invoice_total_toWord;
	}

	/**
	 * @param invoice_total_toWord the invoice_total_toWord to set
	 */
	public void setInvoice_total_toWord(String invoice_total_toWord) {
		this.invoice_total_toWord = invoice_total_toWord;
	}

	/**
	 * @return the invoice_operator
	 */
	public String getInvoice_operator() {
		return invoice_operator;
	}

	/**
	 * @param invoice_operator the invoice_operator to set
	 */
	public void setInvoice_operator(String invoice_operator) {
		this.invoice_operator = invoice_operator;
	}

	/**
	 * @return the ste_ifu
	 */
	public String getSte_ifu() {
		return ste_ifu;
	}

	/**
	 * @param ste_ifu the ste_ifu to set
	 */
	public void setSte_ifu(String ste_ifu) {
		this.ste_ifu = ste_ifu;
	}

	/**
	 * @return the ste_name
	 */
	public String getSte_name() {
		return ste_name;
	}

	/**
	 * @param ste_name the ste_name to set
	 */
	public void setSte_name(String ste_name) {
		this.ste_name = ste_name;
	}

	/**
	 * @return the ste_telephone
	 */
	public String getSte_telephone() {
		return ste_telephone;
	}

	/**
	 * @param ste_telephone the ste_telephone to set
	 */
	public void setSte_telephone(String ste_telephone) {
		this.ste_telephone = ste_telephone;
	}

	/**
	 * @return the ste_email
	 */
	public String getSte_email() {
		return ste_email;
	}

	/**
	 * @param ste_email the ste_email to set
	 */
	public void setSte_email(String ste_email) {
		this.ste_email = ste_email;
	}

	/**
	 * @return the ste_address
	 */
	public String getSte_address() {
		return ste_address;
	}

	/**
	 * @param ste_address the ste_address to set
	 */
	public void setSte_address(String ste_address) {
		this.ste_address = ste_address;
	}

	/**
	 * @return the ste_raisonSociale
	 */
	public String getSte_raisonSociale() {
		return ste_raisonSociale;
	}

	/**
	 * @param ste_raisonSociale the ste_raisonSociale to set
	 */
	public void setSte_raisonSociale(String ste_raisonSociale) {
		this.ste_raisonSociale = ste_raisonSociale;
	}

	/**
	 * @return the ste_ville
	 */
	public String getSte_ville() {
		return ste_ville;
	}

	/**
	 * @param ste_ville the ste_ville to set
	 */
	public void setSte_ville(String ste_ville) {
		this.ste_ville = ste_ville;
	}

	/**
	 * @return the ste_pays
	 */
	public String getSte_pays() {
		return ste_pays;
	}

	/**
	 * @param ste_pays the ste_pays to set
	 */
	public void setSte_pays(String ste_pays) {
		this.ste_pays = ste_pays;
	}

	/**
	 * @return the ste_rccm
	 */
	public String getSte_rccm() {
		return ste_rccm;
	}

	/**
	 * @param ste_rccm the ste_rccm to set
	 */
	public void setSte_rccm(String ste_rccm) {
		this.ste_rccm = ste_rccm;
	}

	/**
	 * @return the ste_logo
	 */
	public String getSte_logo() {
		return ste_logo;
	}

	/**
	 * @param ste_logo the ste_logo to set
	 */
	public void setSte_logo(String ste_logo) {
		this.ste_logo = ste_logo;
	}

	/**
	 * @return the ste_contact
	 */
	public String getSte_contact() {
		return ste_contact;
	}

	/**
	 * @param ste_contact the ste_contact to set
	 */
	public void setSte_contact(String ste_contact) {
		this.ste_contact = ste_contact;
	}

	/**
	 * @return the emcef_nim
	 */
	public String getEmcef_nim() {
		return emcef_nim;
	}

	/**
	 * @param emcef_nim the emcef_nim to set
	 */
	public void setEmcef_nim(String emcef_nim) {
		this.emcef_nim = emcef_nim;
	}

}
