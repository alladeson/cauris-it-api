/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author allad
 *
 */
@Table(uniqueConstraints = { @UniqueConstraint(name = "UniqueSerialKey", columnNames = { "serialKey" }) })
@Entity
public class SerialKey extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3038697960004628292L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	// La clé d'activation unique utilisant "SHA-256" et un UUID aléatoire
	@Column(nullable = false)
	private String serialKey;

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
	 * @return the serialKey
	 */
	public String getSerialKey() {
		return serialKey;
	}

	/**
	 * @param serialKey the serialKey to set
	 */
	public void setSerialKey(String serialKey) {
		this.serialKey = serialKey;
	}

	/**
	 * @return the status
	 */
	public boolean isStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	// Vérificateur de l'état de la clé unique (false = disponible, true = déjà
	// utilisé)
	private boolean status;

}
