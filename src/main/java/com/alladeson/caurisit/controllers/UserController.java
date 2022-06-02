package com.alladeson.caurisit.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.alladeson.caurisit.models.entities.User;
import com.alladeson.caurisit.security.core.PasswordPayload;
import com.alladeson.caurisit.security.core.PasswordResetPayload;
import com.alladeson.caurisit.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {

	@Autowired
	private UserService service;

	@GetMapping({ "parametre/users-connected", "parametre/users/connected" })
	public User get() {
		return service.get();
	}

	@GetMapping("parametre/users/super-admin")
	public List<User> getAll() {
		return service.getAll();
	}

	@GetMapping("parametre/users/admin")
	public List<User> getAllForAdmin() {
		return service.getAllForAdmin();
	}

	@GetMapping("parametre/users/{id}")
	public User get(@PathVariable(value = "id") Long id) {
		return service.get(id);
	}

	@PostMapping("parametre/users/groupe/{gId}")
	public User create(@RequestBody User user, @PathVariable(value = "gId") Long groupeId)
			throws JsonProcessingException {
		return service.create(user, groupeId);
	}

	@PutMapping("parametre/users/{id}/groupe/{gId}")
	public User update(@RequestBody User user, @PathVariable(value = "id") Long id,
			@PathVariable(value = "gId") Long groupeId) throws JsonProcessingException {
		return service.update(id, user, groupeId);
	}

	@DeleteMapping("parametre/users/{id}")
	public boolean delete(@PathVariable(value = "id") Long id) throws JsonProcessingException {
		return service.delete(id);
	}

	/**
	 * @param id
	 * @param file
	 * @return
	 * @see com.alladeson.caurisit.services.UserService#updatePhoto(java.lang.Long,
	 *      org.springframework.web.multipart.MultipartFile)
	 */
	@PutMapping("parametre/users/{id}/photo")
	public User updatePhoto(@PathVariable(value = "id") Long id, @RequestParam("file") MultipartFile file) {
		return service.updatePhoto(id, file);
	}

	/**
	 * @param password
	 * @return
	 * @see com.alladeson.caurisit.services.UserService#changePassword(com.alladeson.caurisit.security.core.PasswordPayload)
	 */
	@PutMapping("parametre/users/{id}/reset-password")
	public User changePassword(@RequestBody PasswordPayload password) {
		return service.changePassword(password);
	}

}
