/**
 * 
 */
package com.alladeson.caurisit.models.entities;

import com.alladeson.caurisit.security.entities.TypeRole;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

/**
 * @author William ALLADE
 *
 */
@Table(uniqueConstraints = { 
		@UniqueConstraint(name = "UniqueName", columnNames = { "name" })
		})
@Entity
public class UserGroup extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5936237106297268500L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(nullable = false)
	private String name;
	private String description;
	@JsonIgnore
	@OneToMany(mappedBy = "group")
	private List<User> users;
	@JsonIgnore
	@OneToMany(mappedBy = "group", orphanRemoval = true, cascade = CascadeType.PERSIST)
	private List<Access> access;
	// Le Rôle tient lieu du profil utilisateur, c'est le type de l'utilisateur
	private TypeRole role;

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
	public String getName() {
		return name;
	}

	/**
	 * @param name the libelle to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the users
	 */
	public List<User> getUsers() {
		if (this.users == null)
			return new ArrayList<User>();
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(List<User> users) {
		this.users = users;
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

	/**
	 * @return the role
	 */
	public TypeRole getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(TypeRole role) {
		this.role = role;
	}
}
