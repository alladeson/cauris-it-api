/**
 * 
 */
package com.alladeson.caurisit.controllers;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alladeson.caurisit.models.entities.Access;
import com.alladeson.caurisit.models.entities.Feature;
import com.alladeson.caurisit.models.entities.SerialKey;
import com.alladeson.caurisit.models.entities.UserGroup;
import com.alladeson.caurisit.services.AccessService;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author allad
 *
 */
@RestController
public class AccessController {

	@Autowired
	private AccessService accessService;

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.AccessService#findAllFeature()
	 */
	@GetMapping("access/features/admin")
	public List<Feature> getAllFeatureForAdmin() {
		return accessService.getAllFeatureForAdmin();
	}

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.AccessService#getAllFeatureForSA()
	 */
	@GetMapping("access/features/super-admin")
	public List<Feature> getAllFeatureForSA() {
		return accessService.getAllFeatureForSA();
	}

	/**
	 * @param id
	 * @return
	 * @see com.alladeson.caurisit.services.AccessService#getFeature(java.lang.Long)
	 */
	@GetMapping("access/features/{id}")
	public Feature getFeature(@PathVariable(value = "id") Long id) {
		return accessService.getFeature(id);
	}

	/**
	 * @param code
	 * @return
	 * @see com.alladeson.caurisit.services.AccessService#findFeature(java.lang.Integer)
	 */
	@GetMapping("access/features/code/{code}")
	public Feature findFeatureByCode(@PathVariable(value = "code") Integer code) {
		return accessService.findFeatureByCode(code);
	}

//	/**
//	 * @param feature
//	 * @return
//	 * @throws JsonProcessingException
//	 * @see com.alladeson.caurisit.services.AccessService#createFeature(com.alladeson.caurisit.models.entities.Feature)
//	 */
//	public Feature createFeature(Feature feature) throws JsonProcessingException {
//		return accessService.createFeature(feature);
//	}
//
//	/**
//	 * @param feature
//	 * @param featureId
//	 * @return
//	 * @throws JsonProcessingException
//	 * @see com.alladeson.caurisit.services.AccessService#putFeature(com.alladeson.caurisit.models.entities.Feature, java.lang.Long)
//	 */
//	public Feature putFeature(Feature feature, Long featureId) throws JsonProcessingException {
//		return accessService.putFeature(feature, featureId);
//	}
//
//	/**
//	 * @param featureId
//	 * @return
//	 * @throws JsonProcessingException
//	 * @see com.alladeson.caurisit.services.AccessService#deleteFeature(java.lang.Long)
//	 */
//	public String deleteFeature(Long featureId) throws JsonProcessingException {
//		return accessService.deleteFeature(featureId);
//	}

	/**
	 * @param search
	 * @return
	 * @see com.alladeson.caurisit.services.AccessService#getAllGroupe(java.lang.String)
	 */
	@GetMapping("access/user-groups/super-admin")
	public List<UserGroup> getAllGroupe(@RequestParam(name = "search", defaultValue = "") String search) {
		return accessService.getAllGroupe(search);
	}

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.AccessService#getAllGroupeNotSA()
	 */
	@GetMapping("access/user-groups/admin")
	public List<UserGroup> getAllGroupeNotSA() {
		return accessService.getAllGroupeNotSA();
	}

	/**
	 * @param id
	 * @return
	 * @see com.alladeson.caurisit.services.AccessService#getGroupe(java.lang.Long)
	 */
	@GetMapping("access/user-groups/{id}")
	public UserGroup getGroupe(@PathVariable(value = "id") Long id) {
		return accessService.getGroupe(id);
	}

	/**
	 * @param group
	 * @return
	 * @throws JsonProcessingException
	 * @see com.alladeson.caurisit.services.AccessService#createGroupe(com.alladeson.caurisit.models.entities.UserGroup)
	 */
	@PostMapping("access/user-groups")
	public UserGroup createGroupe(@RequestBody UserGroup group) throws JsonProcessingException {
		return accessService.createGroupe(group);
	}

	/**
	 * @param id
	 * @param group
	 * @return
	 * @throws JsonProcessingException
	 * @see com.alladeson.caurisit.services.AccessService#updateGroupe(java.lang.Long,
	 *      com.alladeson.caurisit.models.entities.UserGroup)
	 */
	@PutMapping("access/user-groups/{id}")
	public UserGroup updateGroupe(@PathVariable(value = "id") Long id, @RequestBody UserGroup group)
			throws JsonProcessingException {
		return accessService.updateGroupe(id, group);
	}

	/**
	 * @param id
	 * @return
	 * @throws JsonProcessingException
	 * @see com.alladeson.caurisit.services.AccessService#deleteGroupe(java.lang.Long)
	 */
	@DeleteMapping("access/user-groups/{id}")
	public boolean deleteGroupe(@PathVariable(value = "id") Long id) throws JsonProcessingException {
		return accessService.deleteGroupe(id);
	}

	/**
	 * @param access
	 * @param userGroupId
	 * @param featureId
	 * @return
	 * @throws JsonProcessingException
	 * @see com.alladeson.caurisit.services.AccessService#createAccess(com.alladeson.caurisit.models.entities.Access,
	 *      java.lang.Long, java.lang.Long)
	 */
	@PostMapping("access/accesses/user-group/{ugId}/feature/{fId}")
	public Access createAccess(@RequestBody Access access, @PathVariable(value = "ugId") Long userGroupId,
			@PathVariable(value = "fId") Long featureId) throws JsonProcessingException {
		return accessService.createAccess(access, userGroupId, featureId);
	}

	/**
	 * @param id
	 * @return
	 * @see com.alladeson.caurisit.services.AccessService#getAccess(java.lang.Long)
	 */
	@GetMapping("access/accesses/{id}")
	public Access getAccess(@PathVariable(value = "id") Long id) {
		return accessService.getAccess(id);
	}

//	/**
//	 * @param userGroupId
//	 * @param featureId
//	 * @return
//	 * @see com.alladeson.caurisit.services.AccessService#getAccessByUserGroupAndFeature(java.lang.Long,
//	 *      java.lang.Long)
//	 */
//	@GetMapping("access/accesses/user-group/{ugId}/feature/{fId}")
//	public Access getAccessByUserGroupAndFeature(@PathVariable(value = "ugId") Long userGroupId,
//			@PathVariable(value = "fId") Long featureId) {
//		return accessService.getAccessByUserGroupAndFeature(userGroupId, featureId);
//	}

	/**
	 * @param userGroupId
	 * @return
	 * @see com.alladeson.caurisit.services.AccessService#getAccessByUserGroup(java.lang.Long)
	 */
	@GetMapping("access/accesses/user-group/{ugId}")
	public List<Access> getAccessByUserGroup(@PathVariable(value = "ugId") Long userGroupId) {
		return accessService.getAccessByUserGroup(userGroupId);
	}

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.AccessService#getAccessByUserConnected()
	 */
	@GetMapping("access/accesses/connected-user")
	public List<Access> getAccessByUserConnected() {
		return accessService.getAccessByUserConnected();
	}

	/**
	 * @param userId
	 * @param featureId
	 * @return
	 * @see com.alladeson.caurisit.services.AccessService#getAccessByUserAndFeature(java.lang.Long,
	 *      java.lang.Long)
	 */
	@GetMapping("access/accesses/user/{uId}/feature/{fId}")
	public Access getAccessByUserAndFeature(@PathVariable(value = "uId") Long userId,
			@PathVariable(value = "fId") Long featureId) {
		return accessService.getAccessByUserAndFeature(userId, featureId);
	}

//	/**
//	 * @param search
//	 * @return
//	 * @see com.alladeson.caurisit.services.AccessService#getAllAccess(java.lang.String)
//	 */
//	@GetMapping("access/accesses")
//	public List<Access> getAllAccess(@RequestParam(name = "search", defaultValue = "") String search) {
//		return accessService.getAllAccess(search);
//	}

	/**
	 * @param search
	 * @return
	 * @see com.alladeson.caurisit.services.AccessService#getAllAccessForSa(java.lang.String)
	 */
	@GetMapping("access/accesses/super-admin")
	public List<Access> getAllAccessForSa(String search) {
		return accessService.getAllAccessForSa(search);
	}

	/**
	 * @param search
	 * @return
	 * @see com.alladeson.caurisit.services.AccessService#getAllAccessForAdmin(java.lang.String)
	 */
	@GetMapping("access/accesses/admin")
	public List<Access> getAllAccessForAdmin(String search) {
		return accessService.getAllAccessForAdmin(search);
	}

	/**
	 * @param id
	 * @return
	 * @throws JsonProcessingException
	 * @see com.alladeson.caurisit.services.AccessService#deleteAccess(java.lang.Long)
	 */
	@DeleteMapping("access/accesses/{id}")
	public boolean deleteAccess(Long id) throws JsonProcessingException {
		return accessService.deleteAccess(id);
	}

	/*** Gestion de la clé d'activation ***/

	/**
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @see com.alladeson.caurisit.services.AccessService#createSerialKey()
	 */
	@PostMapping("access/serial-key")
	public SerialKey createSerialKey() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return accessService.createSerialKey();
	}

	/**
	 * @param serialKeyId
	 * @return
	 * @see com.alladeson.caurisit.services.AccessService#getSerialKey(java.lang.Long)
	 */
	@GetMapping("access/serial-key/{keyId}")
	public SerialKey getSerialKey(@PathVariable(value = "keyId") Long serialKeyId) {
		return accessService.getSerialKey(serialKeyId);
	}

	/**
	 * @param serialKey
	 * @return
	 * @see com.alladeson.caurisit.services.AccessService#getSerialKeyByKey(java.lang.String)
	 */
	@GetMapping("access/serial-key/key/{serialKey}")
	public SerialKey getSerialKeyByKey(@PathVariable(value = "serialKey") String serialKey) {
		return accessService.getSerialKeyByKey(serialKey);
	}

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.AccessService#getAllSerialKey()
	 */
	@GetMapping("access/serial-key")
	public List<SerialKey> getAllSerialKey() {
		return accessService.getAllSerialKey();
	}

	/**
	 * @param serialKey
	 * @param serialKeyId
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @see com.alladeson.caurisit.services.AccessService#updateSerialKey(com.alladeson.caurisit.models.entities.SerialKey,
	 *      java.lang.Long)
	 */
	@PutMapping("access/serial-key/{keyId}")
	public SerialKey updateSerialKey(@PathVariable(value = "keyId") Long serialKeyId)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return accessService.updateSerialKey(serialKeyId);
	}

	/**
	 * @param serialKeyId
	 * @return
	 * @see com.alladeson.caurisit.services.AccessService#deleteSerialKey(java.lang.Long)
	 */
	@DeleteMapping("access/serial-key/{keyId}")
	public boolean deleteSerialKey(@PathVariable(value = "keyId") Long serialKeyId) {
		return accessService.deleteSerialKey(serialKeyId);
	}

	/**
	 * @param key
	 * @return
	 * @see com.alladeson.caurisit.services.AccessService#activateSerialKey(java.lang.String)
	 */
	@PostMapping("access/serial-key/key/{serialKey}")
	public boolean activateSerialKey(@PathVariable(value = "serialKey") String key) {
		return accessService.activateSerialKey(key);
	}

//	/*** Vérification de la clé d'activation ***/
//
//	/**
//	 * @param serialKey
//	 * @return
//	 * @throws URISyntaxException
//	 * @throws SSLException 
//	 * @see com.alladeson.caurisit.services.AccessService#checkSecrialKey(java.lang.String)
//	 */
//	@PostMapping("access/serial-key/check/key/{serialKey}")
//	public boolean checkSecrialKey(@PathVariable(value = "serialKey") String serialKey) throws URISyntaxException, SSLException {
//		return accessService.checkSecrialKey(serialKey);
//	}
}
