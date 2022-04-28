package com.alladeson.caurisit.models.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Facture extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2469748665214237679L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	// Date de validation de la facture
	private Date date;
	private Double montantHt;
	private Double montantTva;
	private Double montantAib;
	private Double montantTtc;
	private boolean valid;
	
	// Les taxes spécifiques
	private Double tsHt;
	private Double tsTtc;

	// Champ de confiramtion de la facture
	private boolean confirm;
	// Champ d'annulation de la facture
	private boolean cancel;

	// Observation sur la facture
	private String observation;

	// Référence de la facture après confirmation Mecef/DGI
	// Le contenu est de 24 caractères de Code MECeF/DGI de la facture originale
	private String reference;

	// Le type de la facture
	@ManyToOne
	private TypeFacture type;

	@ManyToOne
	private Taxe aib;

	@ManyToOne
	private Client client;

	@OneToMany(mappedBy = "facture")
	private List<DetailFacture> details;

	@OneToOne
	private ReglementFacture reglement;

	@ManyToOne
	private User operateur;

	// Le numero de la facture après confirmation du serveur de la dgi
	private String numero;

	// La reference de la facture d'origine en cas de facture d'avoir
	private String origineRef;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the type
	 */
	public TypeFacture getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(TypeFacture type) {
		this.type = type;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the montantHt
	 */
	public Double getMontantHt() {
		return montantHt;
	}

	/**
	 * @param montantHt the montantHt to set
	 */
	public void setMontantHt(Double montantHt) {
		this.montantHt = montantHt;
	}

	/**
	 * @return the montantTva
	 */
	public Double getMontantTva() {
		return montantTva;
	}

	/**
	 * @param montantTva the montantTva to set
	 */
	public void setMontantTva(Double montantTva) {
		this.montantTva = montantTva;
	}

	/**
	 * @return the montantAib
	 */
	public Double getMontantAib() {
		return montantAib;
	}

	/**
	 * @param montantAib the montantAib to set
	 */
	public void setMontantAib(Double montantAib) {
		this.montantAib = montantAib;
	}

	/**
	 * @return the montantTtc
	 */
	public Double getMontantTtc() {
		return montantTtc;
	}

	/**
	 * @param montantTtc the montantTtc to set
	 */
	public void setMontantTtc(Double montantTtc) {
		this.montantTtc = montantTtc;
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * @return the confirm
	 */
	public boolean isConfirm() {
		return confirm;
	}

	/**
	 * @param confirm the confirm to set
	 */
	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}

	/**
	 * @return the cancel
	 */
	public boolean isCancel() {
		return cancel;
	}

	/**
	 * @param cancel the cancel to set
	 */
	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}

	/**
	 * @return the details
	 */
	public List<DetailFacture> getDetails() {
		if (details == null)
			details = new ArrayList<DetailFacture>();
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(List<DetailFacture> details) {
		this.details = details;
	}

	/**
	 * @return the observation
	 */
	public String getObservation() {
		return observation;
	}

	/**
	 * @param observation the observation to set
	 */
	public void setObservation(String observation) {
		this.observation = observation;
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * @return the aib
	 */
	public Taxe getAib() {
		return aib;
	}

	/**
	 * @param aib the aib to set
	 */
	public void setAib(Taxe aib) {
		this.aib = aib;
	}

	/**
	 * @return the reglement
	 */
	public ReglementFacture getReglement() {
		return reglement;
	}

	/**
	 * @param reglement the reglement to set
	 */
	public void setReglement(ReglementFacture reglement) {
		this.reglement = reglement;
	}

	/**
	 * @return the operateur
	 */
	public User getOperateur() {
		return operateur;
	}

	/**
	 * @param operateur the operateur to set
	 */
	public void setOperateur(User operateur) {
		this.operateur = operateur;
	}

	/**
	 * @return the numero
	 */
	public String getNumero() {
		return numero;
	}

	/**
	 * @param numero the numero to set
	 */
	public void setNumero(String numero) {
		this.numero = numero;
	}

	/**
	 * @return the origineRef
	 */
	public String getOrigineRef() {
		return origineRef;
	}

	/**
	 * @param origineRef the origineRef to set
	 */
	public void setOrigineRef(String origineRef) {
		this.origineRef = origineRef;
	}

	/**
	 * @return the tsHt
	 */
	public Double getTsHt() {
		return tsHt;
	}

	/**
	 * @param tsHt the tsHt to set
	 */
	public void setTsHt(Double tsHt) {
		this.tsHt = tsHt;
	}

	/**
	 * @return the tsTtc
	 */
	public Double getTsTtc() {
		return tsTtc;
	}

	/**
	 * @param tsTtc the tsTtc to set
	 */
	public void setTsTtc(Double tsTtc) {
		this.tsTtc = tsTtc;
	}
}
