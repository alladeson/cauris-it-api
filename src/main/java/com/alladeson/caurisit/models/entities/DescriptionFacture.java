/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author allad
 *
 */
@Entity
public class DescriptionFacture extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6955259305004834818L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	//L'objet de la facture
	@Column(length = 1000, nullable = true)
	private String objet;
	//Autres champs
	private String dossier;
	private String numeroBl;
	private String numeroPo;
	private Double montantTotal;
	private Double avance;
	private Double solde;
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the objet
	 */
	public String getObjet() {
		return objet;
	}
	/**
	 * @param objet the objet to set
	 */
	public void setObjet(String objet) {
		this.objet = objet;
	}
	/**
	 * @return the dossier
	 */
	public String getDossier() {
		return dossier;
	}
	/**
	 * @param dossier the dossier to set
	 */
	public void setDossier(String dossier) {
		this.dossier = dossier;
	}
	/**
	 * @return the numeroBl
	 */
	public String getNumeroBl() {
		return numeroBl;
	}
	/**
	 * @param numeroBl the numeroBl to set
	 */
	public void setNumeroBl(String numeroBl) {
		this.numeroBl = numeroBl;
	}
	/**
	 * @return the numeroPo
	 */
	public String getNumeroPo() {
		return numeroPo;
	}
	/**
	 * @param numeroPo the numeroPo to set
	 */
	public void setNumeroPo(String numeroPo) {
		this.numeroPo = numeroPo;
	}
	/**
	 * @return the montantTotal
	 */
	public Double getMontantTotal() {
		return montantTotal;
	}
	/**
	 * @param montantTotal the montantTotal to set
	 */
	public void setMontantTotal(Double montantTotal) {
		this.montantTotal = montantTotal;
	}
	/**
	 * @return the avance
	 */
	public Double getAvance() {
		return avance;
	}
	/**
	 * @param avance the avance to set
	 */
	public void setAvance(Double avance) {
		this.avance = avance;
	}
	/**
	 * @return the solde
	 */
	public Double getSolde() {
		return solde;
	}
	/**
	 * @param solde the solde to set
	 */
	public void setSolde(Double solde) {
		this.solde = solde;
	}
}
