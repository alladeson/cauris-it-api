/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * @author alladeson
 *
 */
@Entity
public class TypeFacture extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5827981536854743886L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private TypeData groupe;
	// Le type de la facture
	private TypeFactureEnum type;

	// La description du type de la facture
	private String description;

	// Rélation inclusive : utile uniquement pour les types d'avoir, juste pour
	// savoir qu'elle type de vente leur correspondent
	@OneToOne
	private TypeFacture origine;

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
	 * @return the groupe
	 */
	public TypeData getGroupe() {
		return groupe;
	}

	/**
	 * @param groupe the groupe to set
	 */
	public void setGroupe(TypeData groupe) {
		this.groupe = groupe;
	}

	/**
	 * @return the type
	 */
	public TypeFactureEnum getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(TypeFactureEnum type) {
		this.type = type;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the origine
	 */
	public TypeFacture getOrigine() {
		return origine;
	}

	/**
	 * @param origine the origine to set
	 */
	public void setOrigine(TypeFacture origine) {
		this.origine = origine;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
