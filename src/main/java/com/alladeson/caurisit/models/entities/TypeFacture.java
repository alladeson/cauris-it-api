/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
	
	// Le type de la facture
	private TypeFactureEnum type;
	// La description du type de la facture
	private String description;
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
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
