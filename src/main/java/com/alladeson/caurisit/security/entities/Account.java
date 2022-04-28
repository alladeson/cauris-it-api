package com.alladeson.caurisit.security.entities;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Account extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8295803199012737655L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(unique = true)
	private String username;
	@JsonIgnore
	private String password;
	//@Email
	@Column(unique = true)
	private String email;
	@Column(unique = true)
	private String phone;
	@ManyToMany
	@JoinTable(name = "account_role",
			joinColumns = @JoinColumn(name="account_id"),
			inverseJoinColumns = @JoinColumn(name="role_id"))
	private Collection<Role> roles;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Collection<Role> getRoles() {
		if(roles == null) roles = new ArrayList<>();
		return roles;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}
}
