/**
 *
 */
package com.alladeson.caurisit.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.alladeson.caurisit.security.entities.Account;
import com.alladeson.caurisit.security.entities.Role;
import com.alladeson.caurisit.security.entities.TypeRole;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
/**
 * @author TechDigita
 *
 */
//@Table(uniqueConstraints = { 
//		@UniqueConstraint(name = "UniqueName", columnNames = { "name" })
//		})
@Entity
public class User extends BaseEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6209084795534331740L;
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;
    @Transient
    private String defaultPassword;
    private String photo;
    @JsonIgnore
    @OneToOne
    private Account account;
    @ManyToOne
    private UserGroup group;
//    @ManyToOne
//    private Structure structure;
    
    // Gestion du role de l'utilisateur, utile pour la récupérer depuis le client
    private TypeRole role;
    
   // Paramètre du frontend de l'application Cauris-iT
    @OneToOne(cascade = { CascadeType.REMOVE, CascadeType.PERSIST })
    private FrontendLayoutSettings layout;
    
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

    public Account getAccount() {
        if (account == null) account = new Account();
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getUsername() {
        return this.getAccount().getUsername();
    }

    public void setUsername(String username) {
        this.getAccount().setUsername(username);
    }

    @JsonIgnore
    public String getPassword() {
        return this.getAccount().getPassword();
    }

    @JsonIgnore
    public void setPassword(String password) {
        this.getAccount().setPassword(password);
    }

    @Email
    //@NotBlank
    public String getEmail() {
        return this.getAccount().getEmail();
    }

    public void setEmail(String email) {
        this.getAccount().setEmail(email);
    }

    public String getPhone() {
        return this.getAccount().getPhone();
    }

    public void setPhone(String phone) {
        this.getAccount().setPhone(phone);
    }

//    public boolean isEnabled() {
//        return this.getAccount().isEnabled();
//    }

//    public boolean isSystem() {
//        return this.getAccount().isSys();
//    }
//
//    public void setSystem(boolean system) {
//        this.getAccount().setSys(system);
//    }

    @JsonIgnore
    public Collection<Role> getRoles() {
        return this.getAccount().getRoles();
    }

//    public void setRoles(Collection<Role> roles) {
//        this.getAccount().setRoles(roles);
//    }

    public String getDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * @return the lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * @param lastname the lastname to set
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFullname() {
        return firstname + " " + lastname;
    }

    /**
     * @return the group
     */
    public UserGroup getGroup() {
        if (group == null) group = new UserGroup();
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(UserGroup group) {
        this.group = group;
    }
//
//    /**
//     * @return the structure
//     */
//    public Structure getStructure() {
//        return structure;
//        //return this.getGroup().getStructure();
//    }
//
//    /**
//     * @param structure the structure to set
//     */
//    public void setStructure(Structure structure) {
//        this.structure = structure;
//        //this.getGroup().setStructure(structure);
//    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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
	
	/**
	 * Récupérer le numero de téléphone de l'utilisateur
	 * @return the phone
	 */
	public String getTelephone() {
		return this.account.getPhone();
	}

	/**
	 * @return the layout
	 */
	public FrontendLayoutSettings getLayout() {
		return layout;
	}

	/**
	 * @param layout the layout to set
	 */
	public void setLayout(FrontendLayoutSettings layout) {
		this.layout = layout;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	/**
	 * 
	 * @return true if super_admin, else false
	 */
	public boolean isSA() {
        return this.role == TypeRole.SUPER_ADMIN;
    }

	/**
	 * 
	 * @return true if admin, else false
	 */
    public boolean isAdmin() {
        return this.role == TypeRole.ADMIN;
    }
    
    


//    public boolean isActive() {
//        return this.getAccount().isPasswordEnabled();
//        //return this.getAccount().isEnabled();
//    }
}
