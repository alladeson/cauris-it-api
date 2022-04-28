package com.alladeson.caurisit.security.core;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alladeson.caurisit.security.entities.Role;
import com.alladeson.caurisit.security.repositories.RoleRepository;

@Service
public class RoleService {

    @Autowired
    RoleRepository repository;

    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    public Optional<Role> findByName(String name) {
        return repository.findByName(name);
    }

    public Role save(Role role) {
        return repository.save(role);
    }
}
