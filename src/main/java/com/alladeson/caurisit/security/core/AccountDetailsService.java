package com.alladeson.caurisit.security.core;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.alladeson.caurisit.security.entities.Account;


@Service
public class AccountDetailsService implements UserDetailsService {

	@Autowired
	AccountService service;

	@Override
	public UserDetails loadUserByUsername(String login) {
		Account account = service.getByLogin(login);
		return AccountDetails.create(account);
	}

	public UserDetails loadUserById(Long id)  {
		Account account = service.getById(id);
		return AccountDetails.create(account);
	}

}
