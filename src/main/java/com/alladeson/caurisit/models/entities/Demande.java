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
 * @author allad
 *
 */
@Entity
public class Demande extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6000564961303123027L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	// La clé d'activation du contribuable
	@OneToOne
	private SerialKey serialKey;
	// Le formulaire de demande
	private String formulaire;
	// Les infos récapitulatifs de la demande DGI
	private String formulaireDgi;
	// Le rapport de configuration de la machine du contribuable
	private String configReport;
	// Le statut de traitement de la demande
	private boolean treated;
	// Le statut de validation de la demande
	private boolean valid;

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
	public SerialKey getSerialKey() {
		return serialKey;
	}

	/**
	 * @param serialKey the serialKey to set
	 */
	public void setSerialKey(SerialKey serialKey) {
		this.serialKey = serialKey;
	}

	/**
	 * @return the formulaire
	 */
	public String getFormulaire() {
		return formulaire;
	}

	/**
	 * @param formulaire the formulaire to set
	 */
	public void setFormulaire(String formulaire) {
		this.formulaire = formulaire;
	}

	/**
	 * @return the formaulaireDgi
	 */
	public String getFormulaireDgi() {
		return formulaireDgi;
	}

	/**
	 * @param formaulaireDgi the formaulaireDgi to set
	 */
	public void setFormulaireDgi(String formulaireDgi) {
		this.formulaireDgi = formulaireDgi;
	}

	/**
	 * @return the configReport
	 */
	public String getConfigReport() {
		return configReport;
	}

	/**
	 * @param configReport the configReport to set
	 */
	public void setConfigReport(String configReport) {
		this.configReport = configReport;
	}

	/**
	 * @return the treated
	 */
	public boolean isTreated() {
		return treated;
	}

	/**
	 * @param treated the treated to set
	 */
	public void setTreated(boolean treated) {
		this.treated = treated;
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
}
