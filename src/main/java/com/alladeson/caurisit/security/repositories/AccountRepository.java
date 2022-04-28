package com.alladeson.caurisit.security.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alladeson.caurisit.security.entities.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByUsername(String string);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Optional<Account> findByUsername(String username);
    Optional<Account> findByEmail(String email);
    Optional<Account> findByPhone(String phone);

}
