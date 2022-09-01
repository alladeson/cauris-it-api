/**
 *
 */
package com.alladeson.caurisit.models.entities;

import javax.persistence.*;

/**
 * @author William ALLADE
 *
 */
@Entity
public class Access extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8056954868217096617L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private boolean readable;
	private boolean writable;
	private boolean deletable;
	@ManyToOne
	private UserGroup group;
	@ManyToOne
	private Feature feature;
	@Transient
	private Integer menu;

	public Access() {
	}

	public Access(UserGroup group, Feature feature, boolean readable, boolean writable, boolean deletable) {
		this.group = group;
		this.feature = feature;
		this.readable = readable;
		this.writable = writable;
		this.deletable = deletable;
	}

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
	 * @return the group
	 */
	public UserGroup getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(UserGroup group) {
		this.group = group;
	}

	/**
	 * @return the feature
	 */
	public Feature getFeature() {
		return feature;
	}

	/**
	 * @param feature the feature to set
	 */
	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public Integer getMenu() {
		return feature == null ? this.menu : feature.getCode();
	}

	public void setMenu(Integer menu) {
		this.menu = this.feature.getCode();
	}

	public String getMenuName() {
		return feature == null ? null : feature.getName();
	}

}
