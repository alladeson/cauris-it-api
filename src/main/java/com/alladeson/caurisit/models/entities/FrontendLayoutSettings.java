/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 * @author William ALLADE
 *
 */
@Entity
public class FrontendLayoutSettings /* extends BaseEntity */ {

	/**
	 * 
	 */
	/* private static final long serialVersionUID = -7240015719681583934L; */

	//
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_flsettings")
	@SequenceGenerator(name = "gen_flsettings", sequenceName = "_seq_flsettings", allocationSize = 1)
	private Long id;
	//
	// Pour la disposition des pages : Vertical ou Horizontal
	private String layout;
	// Pour le mode d'affichage des pages : Light ou Dark
	private String mode;
	// Pour la largeur des pages : FLuid (remplir l'écran) ou Boxed (cadré)
	private String width;
	// Pour la position de l'affichage : Fixed (fixe) ou Scrollable
	private String position;
	// Pour la couleur de la barre du haut : Light ou Dark
	private String topbarColor;
	// Paramètre du menu de gauche (sidebar menu) en cas de disposition vertical des
	// pages
	// Pour la dimension du sidebar (Sidebar size) : Default, Compact, Small (Icon
	// View)
	private String sidebarSize;
	// Pour la couleur du sidebar (Sidebar Color) : Light, Dark, Brand
	private String sidebarColor;

	// @JsonIgnore
	// @OneToOne
	// private User user;

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
	 * @return the layout
	 */
	public String getLayout() {
		return layout;
	}

	/**
	 * @param layout the layout to set
	 */
	public void setLayout(String layout) {
		this.layout = layout;
	}

	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * @return the width
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(String width) {
		this.width = width;
	}

	/**
	 * @return the position
	 */
	public String getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(String position) {
		this.position = position;
	}

	/**
	 * @return the topbarColor
	 */
	public String getTopbarColor() {
		return topbarColor;
	}

	/**
	 * @param topbarColor the topbarColor to set
	 */
	public void setTopbarColor(String topbarColor) {
		this.topbarColor = topbarColor;
	}

	/**
	 * @return the sidebarSize
	 */
	public String getSidebarSize() {
		return sidebarSize;
	}

	/**
	 * @param sidebarSize the sidebarSize to set
	 */
	public void setSidebarSize(String sidebarSize) {
		this.sidebarSize = sidebarSize;
	}

	/**
	 * @return the sidebarColor
	 */
	public String getSidebarColor() {
		return sidebarColor;
	}

	/**
	 * @param sidebarColor the sidebarColor to set
	 */
	public void setSidebarColor(String sidebarColor) {
		this.sidebarColor = sidebarColor;
	}

	// /**
	// * @return the user
	// */
	// public User getUser() {
	// return user;
	// }

	// /**
	// * @param user the user to set
	// */
	// public void setUser(User user) {
	// this.user = user;
	// }

	// /**
	// * @return the serialversionuid
	// */
	// public static long getSerialversionuid() {
	// return serialVersionUID;
	// }
}
