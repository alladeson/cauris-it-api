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
public class TypePaiement extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2171670638796745699L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	// Le type de paiement
	private TypePaiementEnum type;
	// La description du type de paiement
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
	public TypePaiementEnum getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(TypePaiementEnum type) {
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
