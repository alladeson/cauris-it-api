/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author allad
 *
 */
@Entity
public class Taxe extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2014954712004806447L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private TypeData type;
	private TaxeGroups groupe;
	private String libelle;
	private Integer valeur;

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
	 * @return the type
	 */
	public TypeData getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(TypeData type) {
		this.type = type;
	}

	/**
	 * @return the groupe
	 */
	public TaxeGroups getGroupe() {
		return groupe;
	}

	/**
	 * @param groupe the groupe to set
	 */
	public void setGroupe(TaxeGroups groupe) {
		this.groupe = groupe;
	}

	/**
	 * @return the libelle
	 */
	public String getLibelle() {
		return libelle;
	}

	/**
	 * @param libelle the libelle to set
	 */
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	/**
	 * @return the valeur
	 */
	public Integer getValeur() {
		return valeur;
	}

	/**
	 * @param valeur the valeur to set
	 */
	public void setValeur(Integer valeur) {
		this.valeur = valeur;
	}

	/**
	 * @return the string of object
	 */
	public String getString() {
		return this.groupe + " (" + this.valeur + "%) - " + this.libelle;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
