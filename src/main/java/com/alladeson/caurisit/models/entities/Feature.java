/**
 *
 */
package com.alladeson.caurisit.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * @author William ALLADE
 *
 */
@Entity
public class Feature extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6794940783868755513L;

	/** Déblut des constantes de codes des fonctionnalités **/
	// Gestion de stock
	public static final int gestStock = 10000;
	public static final int gestStockCategorie = 11000;
	public static final int gestStockArticle = 12000;
	public static final int gestStockApprovisionnement = 13000;
	public static final int gestStockFournisseur = 14000;
	public static final int gestStockCmdFournisseur = 15000;
	// Emission des factures
	public static final int facturation = 20000;
	public static final int facturationFV = 21000;
	public static final int facturationFA = 22000;
	public static final int facturationListe = 23000;
	public static final int facturationClient = 24000;
	// Les données de base et paramètre du système
	public static final int parametre = 30000;
	public static final int parametreTaxe = 31000;
	public static final int parametreTypeFacture = 32000;
	public static final int parametreTypePaiement = 33000;
	public static final int parametreSysteme = 34000;
	public static final int parametreDonneSysteme= 35000;
	// Le contrôle d'accès
	public static final int accessCtrl = 40000;
	public static final int accessCtrlUser = 41000;
	public static final int accessCtrlUserGroup = 42000;
	public static final int accessCtrlFeatures = 43000;
	public static final int accessCtrlAccess = 44000;
	// Les audits
	public static final int audit = 50000;
	// Les statistiques
	public static final int stats = 60000;
	public static final int statsBilanPeriodique = 61000;
	/** Fin des constantes de codes des fonctionnalités **/

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(unique = true)
	private Integer code; // Le code de la fonctionnalité
	private String name;
	private boolean readable;
	private boolean writable;
	private boolean deletable;
	@JsonIgnore
	@OneToMany(mappedBy = "feature")
	private List<Access> access;

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
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public boolean isReadable() {
		return readable;
	}

	public void setReadable(boolean readable) {
		this.readable = readable;
	}

	public boolean isWritable() {
		return writable;
	}

	public void setWritable(boolean writable) {
		this.writable = writable;
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	/**
	 * @return the access
	 */
	public List<Access> getAccess() {
		if (this.access == null)
			this.access = new ArrayList<Access>();
		return access;
	}

	/**
	 * @param access the access to set
	 */
	public void setAccess(List<Access> access) {
		this.access = access;
	}
}
