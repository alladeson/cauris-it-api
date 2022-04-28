package com.alladeson.caurisit.security.core;

import com.alladeson.caurisit.security.entities.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AccountDetails implements UserDetails {
    /**
     *
     */
    private static final long serialVersionUID = -8138269269174282199L;

    private /*UUID*/ Long id;

//    private String name;

    private String username;

    @JsonIgnore
    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public AccountDetails(Account account, Collection<? extends GrantedAuthority> authorities) {
        this.id = account.getId();
        this.username = account.getUsername();
        this.password = account.getPassword();
        this.email = account.getEmail();
        this.authorities = authorities;
    }

    public static AccountDetails create(Account account) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        /*
         * account.getRoles().stream().forEach((role)->
         * authorities.addAll(role.getPrivileges().stream().map(privilege -> new
         * SimpleGrantedAuthority(privilege.getName().toString())).collect(Collectors.
         * toList()) ) );
         */
        account.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new AccountDetails(account, authorities);
    }

    public /*UUID*/ Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AccountDetails other = (AccountDetails) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
