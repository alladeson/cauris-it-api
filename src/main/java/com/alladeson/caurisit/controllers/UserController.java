package com.alladeson.caurisit.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.alladeson.caurisit.models.entities.User;
import com.alladeson.caurisit.security.core.PasswordPayload;
import com.alladeson.caurisit.security.core.PasswordResetPayload;
import com.alladeson.caurisit.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService service;


    @GetMapping({"parametre/users-connected", "parametre/users/connected"})
    public User get() {
        return service.get();
    }

    @GetMapping("parametre/users")
    public List<User> getAll(@RequestParam(name = "search", required = false, defaultValue = "") String search) {
        return service.getAll(search);
    }

    @GetMapping("parametre/users/{id}")
    public User get(@PathVariable(value = "id") Long id) {
        return service.get(id);
    }

    @PostMapping("parametre/users")
    public User create(@RequestBody User user) throws JsonProcessingException {
        return service.create(user);
    }

    @PostMapping("parametre/users/connected/activate-password")
    public User activate(@RequestBody @Valid PasswordPayload payload) {
        return service.activatePassword(payload);
    }

    @PutMapping("parametre/users/{id}")
    public User update(@RequestBody User user,
                       @PathVariable(value = "id") Long id
    ) throws JsonProcessingException {
        return service.update(id, user);
    }
    
    @PutMapping("parametre/users/{id}/reset-password")
    public User deactivate(@PathVariable(value = "id") Long id, @RequestBody PasswordResetPayload reset) {
        return service.deactivatePassword(id, reset);
    }

    @DeleteMapping("parametre/users/{id}")
    public boolean delete(@PathVariable(value = "id") Long id) throws JsonProcessingException {
        return service.delete(id);
    }
}
