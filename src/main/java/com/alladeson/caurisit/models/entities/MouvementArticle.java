/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

/**
 * @author William ALLADE
 *
 */
@Entity
public class MouvementArticle extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3000244193512304919L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	// La date de la création du mouvement
	private Date date;
	// Pour le type de mouvement
	private TypeData type;

	// Quand il s'agit d'un mouvememnt d'entrée, c'est l'approvisionnement
	@OneToOne
	private Approvisionnement approvisionnement;

	// Quand il s'agit d'un mouvememnt de sortie, c'est la vente
	// Il faut donc enregistrer la ligne de la facture validée
	@OneToOne
	private DetailFacture detailFacture;

	// L'article concerné par le mouvement
	@ManyToOne
	private Article article;

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
	 * @return the approvisionnement
	 */
	public Approvisionnement getApprovisionnement() {
		return approvisionnement;
	}

	/**
	 * @param approvisionnement the approvisionnement to set
	 */
	public void setApprovisionnement(Approvisionnement approvisionnement) {
		this.approvisionnement = approvisionnement;
	}

	/**
	 * @return the detailFacture
	 */
	public DetailFacture getDetailFacture() {
		return detailFacture;
	}

	/**
	 * @param detailFacture the detailFacture to set
	 */
	public void setDetailFacture(DetailFacture detailFacture) {
		this.detailFacture = detailFacture;
	}

	/**
	 * @return the article
	 */
	public Article getArticle() {
		return article;
	}

	/**
	 * @param article the article to set
	 */
	public void setArticle(Article article) {
		this.article = article;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
