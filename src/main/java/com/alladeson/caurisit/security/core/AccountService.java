package com.alladeson.caurisit.security.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.alladeson.caurisit.security.entities.Account;
import com.alladeson.caurisit.security.repositories.AccountRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository repository;
//    @Autowired
//    private PasswordEncoder passwordEncoder;


    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public boolean existsByPhone(String phone) {
        return repository.existsByPhone(phone);
    }

    public Account save(Account account) {
        return repository.save(account);
    }
    
    public void delete(Account account) {
        repository.delete(account);
    }

    public Account getById(Long id) {
        Account account = repository.findById(/* UUID.fromString(id) */ id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")
        );
        return account;
    }

    public Account getByLogin(String login) {
        Account account = repository.findByUsername(login)
                .or(() -> repository.findByEmail(login))
                //.or(() -> userRepository.findByPhone(login))
                .orElseThrow(() ->
                        new UsernameNotFoundException("None account not found with username or email : " + login)
                );
        return account;
    }

    public Account getAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        Account account = this.getByLogin(login);
        return account;
    }

//    public  Account register(RegistrationPayload registration){
//
//    }
//
//    public Account changePassword(PasswordChangePayload password) {
//        Account account = this.getAuthenticated();
//
//        if (!passwordEncoder.matches(password.getOldPassword(), account.getPassword())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect old password.");
//        }
//        if (!password.getNewPassword().equals(password.getConfirmedPassword())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect confirmation.");
//        }
//
//        account.setPassword(passwordEncoder.encode(password.getNewPassword()));
//        account = repository.save(account);
//        return account;
//    }
}
