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
 * @author William ALLADE
 *
 */
@Table(uniqueConstraints = { @UniqueConstraint(name = "UniqueLibelle", columnNames = { "libelle" }) })
@Entity
public class CategorieArticle extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6644092043791298533L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	@Column(nullable = false)
	private String libelle;
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
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
