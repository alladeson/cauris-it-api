/**
 * 
 */
package com.alladeson.caurisit.services;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.alladeson.caurisit.models.entities.Access;
import com.alladeson.caurisit.models.entities.Article;
import com.alladeson.caurisit.models.entities.Feature;
import com.alladeson.caurisit.models.entities.Operation;
import com.alladeson.caurisit.models.entities.User;
import com.alladeson.caurisit.models.entities.UserGroup;
import com.alladeson.caurisit.repositories.AccessRepository;
import com.alladeson.caurisit.repositories.FeatureRepository;
import com.alladeson.caurisit.repositories.UserGroupRepository;
import com.alladeson.caurisit.repositories.UserRepository;
import com.alladeson.caurisit.security.core.AccountService;
import com.alladeson.caurisit.security.entities.Account;
import com.alladeson.caurisit.security.entities.TypeRole;
import com.alladeson.caurisit.utils.Tool;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author allad
 *
 */
@Service
public class AccessService {
	@Autowired
	private AccessRepository accessRepos;
	@Autowired
	private FeatureRepository featureRepos;
	@Autowired
	private UserGroupRepository groupeRepos;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private Tool tool;
	@Autowired
	private AuditService auditService;

	@Autowired
	private AccountService accountService;

	/**
	 * Récupération de l'utilisateur connecté
	 * 
	 * @return {@link User}
	 */
	private User getAuthenticated() {
		Account account = accountService.getAuthenticated();
		return userRepository.findByAccount(account)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
	}

	/** Gestion du feature **/

	public List<Feature> getAllFeatureForAdmin() {
		// Check permission
		if (!this.canReadable(Feature.accessCtrlFeatures))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		var codes = new ArrayList<Integer>();
//		codes.add(Feature.parametreSysteme);
		codes.add(Feature.gestStock);
		codes.add(Feature.parametreDonneSysteme);
		codes.add(Feature.accessCtrl);
		codes.add(Feature.accessCtrlAccess);
		codes.add(Feature.accessCtrlFeatures);
		codes.add(Feature.accessCtrlUser);
		codes.add(Feature.accessCtrlUserGroup);
		codes.add(Feature.audit);
		return featureRepos.findAllByCodeNotIn(codes);
	}

	public List<Feature> saveAllFeature(List<Feature> features) {
		return featureRepos.saveAll(features);
	}

	public List<Feature> getAllFeatureForSA() {
		// Check permission
		if (!this.canReadable(Feature.accessCtrlFeatures))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return featureRepos.findAll();
	}

	public Feature getFeature(Long id) {
		// Check permission
		if (!this.canReadable(Feature.accessCtrlFeatures))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return featureRepos.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fonctionnalité non trouvée"));
	}

	public Feature findFeatureByCode(Integer code) {
		// Check permission
		if (!this.canReadable(Feature.accessCtrlFeatures))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return featureRepos.findByCode(code)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fonctionnalité non trouvée"));
	}

	public Feature createFeature(Feature feature) throws JsonProcessingException {

		// Check permission
		if (!this.canWritable(Feature.accessCtrlFeatures))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Feature feature1 = featureRepos.save(feature);

		Operation operation = Operation.FEATURE_CREATE;
		Feature valAvant = null;
		Feature valApres = feature1;
		auditService.traceChange(operation, valAvant, valApres);

		return feature1;

	}

	public Feature putFeature(Feature feature, Long featureId) throws JsonProcessingException {
		// Check permission
		if (!this.canWritable(Feature.accessCtrlFeatures))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Optional<Feature> optionalFeature = featureRepos.findById(featureId);
		if (optionalFeature.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fonctionnalité non trouvée");
		Feature featureToUpdate = optionalFeature.get();
//        Feature featureToUpdateSave = optionalFeature.get();

		if (feature.getName() != null)
			featureToUpdate.setName(feature.getName());

		Feature feature1 = featureRepos.save(featureToUpdate);

		Operation operation = Operation.FEATURE_UPDATE;
		Feature valAvant = featureToUpdate;
		Feature valApres = feature1;
		auditService.traceChange(operation, valAvant, valApres);

		return feature1;
	}

	public String deleteFeature(Long featureId) throws JsonProcessingException {
		// Check permission
		if (!this.canDeletable(Feature.accessCtrlFeatures))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Optional<Feature> optionalFeature = featureRepos.findById(featureId);
		if (optionalFeature.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fonctionnalité non trouvée");
		Feature feature = optionalFeature.get();

		Operation operation = Operation.FEATURE_DELETE;
		Feature valAvant = feature;
		Feature valApres = null;
		auditService.traceChange(operation, valAvant, valApres);

		featureRepos.delete(feature);
		return "fonctionnalité supprimée";
	}

	public Feature saveFeature(Integer code, String libelle, boolean readable, boolean writable, boolean deletable) {
		Feature feature = new Feature();
		feature.setCode(code);
		feature.setName(libelle);
		feature.setReadable(readable);
		feature.setWritable(writable);
		feature.setDeletable(deletable);
		return featureRepos.save(feature);
	}

	public long countFeature() {
		return featureRepos.count();
	}

	/***** Gestion du groupe d'utilisateur *****/

	public List<UserGroup> getAllGroupe(String search) {

		// Check permission
		if (!this.canReadable(Feature.accessCtrlUserGroup))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return groupeRepos.findAll();
	}

	public List<UserGroup> getAllGroupeNotSA() {

		// Check permission
		if (!this.canReadable(Feature.accessCtrlUserGroup))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return groupeRepos.findByRoleNot(TypeRole.SUPER_ADMIN);
	}

	public UserGroup getGroupe(Long id) {
		// Check permission
		if (!this.canReadable(Feature.accessCtrlUserGroup))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return this.findGroupe(id);
	}

	public UserGroup findGroupe(Long id) {
		var ug = groupeRepos.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Groupe utilisateur non trouvé"));
		return ug;
	}

	public UserGroup createGroupe(UserGroup group) throws JsonProcessingException {

		// Check permission
		if (!this.canWritable(Feature.accessCtrlUserGroup))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Vérification de l'existence d'un groupe d'utilisateur par le nom
		if (groupeRepos.findByName(group.getName()).isPresent())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce nom de groupe n'est plus disponible");
		//
		if (group.getRole() == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rôle utilisateur manquant");
		//
		var ug = new UserGroup();
		ug = saveGroupe(group, ug);

		auditService.traceChange(Operation.USER_GROUP_CREATE, null, ug);

		return ug;
	}
	
	/**
	 * @param article
	 * @return
	 */
	private UserGroup saveUserGroupe(UserGroup groupe, boolean delete) {
		try {
			if (delete)
				groupeRepos.delete(groupe);
			else
				groupe = groupeRepos.save(groupe);
		} catch (Exception e) {
			// Contrainte d'unicité
			if (e.getCause() instanceof ConstraintViolationException) {
				// Récupération du vrai cause de l'exception
				SQLIntegrityConstraintViolationException exception = (SQLIntegrityConstraintViolationException) (e
						.getCause()).getCause();
				exception.printStackTrace();
				if (delete && exception.getMessage().contains("foreign key constraint"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Ce groupe d'utilisateur est déjà associé à d'autres données, un utilisateur par exemple");
				else if (exception.getMessage().contains("UniqueName"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Un autre groupe utilisateur porte déjà le mêm nom");
				else if (exception.getMessage().contains("ne peut être vide"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le nom du group utilisateur ne peut être vide");
				else
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, exception.getMessage());

			} else
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
		}
		return groupe;
	}

	public UserGroup updateGroupe(Long id, UserGroup group) throws JsonProcessingException {

		// Check permission
		if (!this.canWritable(Feature.accessCtrlUserGroup))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		var ug = this.findGroupe(id);
		if (group.getRole() == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rôle utilisateurs manquant");

		String valAvant = tool.toJson(ug);

		ug = saveGroupe(group, ug);

		String valApres = tool.toJson(ug);

		auditService.traceChange(Operation.USER_GROUP_UPDATE, valAvant, valApres);

		return ug;
	}

	private UserGroup saveGroupe(UserGroup group, UserGroup ug) {
//		logger.info(group.toString());

		ug.setName(group.getName());
		ug.setDescription(group.getDescription());
		ug.setRole(group.getRole());
		ug.setUpdatedAt(null);
		ug = saveUserGroupe(ug, false);

		// Permissions
		if (!ug.getAccess().isEmpty()) {
			for (var ac : ug.getAccess()) {
//			logger.info(ac.toString());
				var ft = findFeatureByCode(ac.getMenu());
				this.saveAccess(ug, ft, ac.isReadable(), ac.isWritable(), ac.isDeletable());
			}
		} else {
			// Permissions
			var features = this.getAllFeatureForAdmin();
			for (Feature f : features) {
				this.saveAccess(ug, f, ug.getRole().equals(TypeRole.ADMIN) ? true : false);
			}
		}
		return ug;
	}

//	public UserGroup saveGroupe(UserGroup group) {
//		return groupeRepos.save(group);
//	}

	public boolean deleteGroupe(Long id) throws JsonProcessingException {
		// Check permission
		if (!this.canDeletable(Feature.accessCtrlUserGroup))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		var ug = this.findGroupe(id);

		auditService.traceChange(Operation.USER_GROUP_DELETE, ug, null);

		ug = saveUserGroupe(ug, true);

		return true;
	}

	public long countGroupe() {
		return groupeRepos.count();
	}

//	public UserGroup saveGroupe(String name, TypeRole role) {
//		return saveGroupe(name, null, role);
//	}

	public UserGroup saveGroupe(String name, String desc, TypeRole role) {
		var group = new UserGroup();
		group.setName(name);
		group.setDescription(desc);
		group.setRole(role);
		return groupeRepos.save(group);
	}

	/****** Gestion des Access ********/
	public List<Access> getAllAccessForSa(String search) {
		// Check permission
		if (!this.canReadable(Feature.accessCtrlAccess))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return accessRepos.findAll();
	}

	public List<Access> getAllAccessForAdmin(String search) {
		// Check permission
		if (!this.canReadable(Feature.accessCtrlAccess))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return accessRepos.findAllByGroupRoleNot(TypeRole.SUPER_ADMIN);
	}

	public Access getAccess(Long id) {
		// Check permission
		if (!this.canReadable(Feature.accessCtrlAccess))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Optional<Access> optional = accessRepos.findById(id);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Access non trouvé");
		return optional.get();
	}

	public Access getAccessByUserGroupAndFeature(Long userGroupId, Long featureId) {
		Optional<UserGroup> optionalUserGroup = groupeRepos.findById(userGroupId);
		if (optionalUserGroup.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Groupe d'utilisateur non trouvé");
		UserGroup userGroup = optionalUserGroup.get();

		Optional<Feature> optionalFeature = featureRepos.findById(featureId);
		if (optionalFeature.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fonctionnalité non trouvée");
		Feature feature = optionalFeature.get();
		return accessRepos.findByGroupIdAndFeatureId(userGroup.getId(), feature.getId());
	}

	public List<Access> getAccessByUserGroup(Long userGroupId) {
		// Check permission
		if (!this.canReadable(Feature.accessCtrlAccess))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Optional<UserGroup> optionalUserGroup = groupeRepos.findById(userGroupId);
		if (optionalUserGroup.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Groupe d'utilisateur non trouvé");
		UserGroup userGroup = optionalUserGroup.get();

		List<Access> accessList = accessRepos.findByGroupId(userGroup.getId());
		return accessList;
	}

	public List<Access> getAccessByUserConnected() {
		// Check permission
		if (!this.canReadable(Feature.accessCtrlAccess))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		var user = this.getAuthenticated();
		List<Access> accessList = accessRepos.findByGroupId(user.getGroup().getId());
		return accessList;
	}

	public Access getAccessByUserAndFeature(Long userId, Long featureId) {
		// Check permission
		if (!this.canReadable(Feature.accessCtrlAccess))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé");
		User user = userOptional.get();

		Optional<Feature> optionalFeature = featureRepos.findById(featureId);
		if (optionalFeature.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fonctionnalité non trouvée");
		Feature feature = optionalFeature.get();

		Access access = this.getAccessByUserGroupAndFeature(user.getGroup().getId(), feature.getId());

		return access;
	}

	public boolean canReadable(Integer code) {
		User user = this.getAuthenticated();

		Optional<Feature> optionalFeature = featureRepos.findByCode(code);
		if (optionalFeature.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fonctionnalité non trouvée");
		Feature feature = optionalFeature.get();

		Access access = this.getAccessByUserGroupAndFeature(user.getGroup().getId(), feature.getId());

		return user.isSA() || user.isAdmin() || access != null && access.isReadable();
	}

	public boolean canWritable(Integer code) {
		User user = this.getAuthenticated();

		Optional<Feature> optionalFeature = featureRepos.findByCode(code);
		if (optionalFeature.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fonctionnalité non trouvée");
		Feature feature = optionalFeature.get();

		Access access = this.getAccessByUserGroupAndFeature(user.getGroup().getId(), feature.getId());

		return user.isSA() || user.isAdmin() || access != null && access.isWritable();
	}

	public boolean canDeletable(Integer code) {
		User user = this.getAuthenticated();

		Optional<Feature> optionalFeature = featureRepos.findByCode(code);
		if (optionalFeature.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fonctionnalité non trouvée");
		Feature feature = optionalFeature.get();

		Access access = this.getAccessByUserGroupAndFeature(user.getGroup().getId(), feature.getId());

		return user.isSA() || user.isAdmin() || access != null && access.isDeletable();
	}

//	public List<Access> getAllAccess(String search) {
//		return accessRepos.findByGroupNameOrFeatureNameContaining(search, search);
//	}

//	public List<Access> saveAllAccess(List<Access> access) {
//		return accessRepos.saveAll(access);
//	}

	public Access saveAccess(Access access, Long userGroupId, Long featureId) throws JsonProcessingException {
		Optional<UserGroup> optionalUserGroup = groupeRepos.findById(userGroupId);
		if (optionalUserGroup.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Groupe d'utilisateur non trouvé");
		UserGroup userGroup = optionalUserGroup.get();

		Optional<Feature> optionalFeature = featureRepos.findById(featureId);
		if (optionalFeature.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fonctionnalité non trouvée");
		Feature feature = optionalFeature.get();

		Access access1 = accessRepos.findByGroupIdAndFeatureId(userGroup.getId(), feature.getId());
		/* Access access1 = this.getByUserGroupAndFeature(userGroupId, featureId); */
		String valAvant = tool.toJson(access1);

		access = saveAccess(access, userGroup, feature);

		String valApres = tool.toJson(access);
		auditService.traceChange(Operation.ACCESS_CREATE, valAvant, valApres);
		return access;
	}

	private Access saveAccess(Access access, UserGroup userGroup, Feature feature) {
		Access access1 = accessRepos.findByGroupIdAndFeatureId(userGroup.getId(), feature.getId());

		if (access1 == null) {
			access1 = new Access();
			access1.setFeature(feature);
			access1.setGroup(userGroup);
		}
		if (feature.isReadable()) {
			access1.setReadable(access.isReadable());
		}
		if (feature.isWritable()) {
			access1.setWritable(access.isWritable());
		}
		if (feature.isDeletable()) {
			access1.setDeletable(access.isDeletable());
		}

		return accessRepos.save(access1);
	}

//	public Access saveAccess(Long userGroupId, Long featureId, boolean readable, boolean writable, boolean deletable)
//			throws JsonProcessingException {
//		var acces = new Access();
//		acces.setReadable(readable);
//		acces.setWritable(writable);
//		acces.setDeletable(deletable);
//		return this.saveAccess(acces, userGroupId, featureId);
//	}

	public Access saveAccess(UserGroup userGroup, Feature feature, boolean readable, boolean writable,
			boolean deletable) {
		var acces = new Access();
		acces.setReadable(readable);
		acces.setWritable(writable);
		acces.setDeletable(deletable);
		return this.saveAccess(acces, userGroup, feature);
	}

	public Access saveAccess(UserGroup userGroup, Feature feature, boolean all) {
		return saveAccess(userGroup, feature, all, all, all);
	}

	public Access createAccess(Access access, Long userGroupId, Long featureId) throws JsonProcessingException {
		// Check permission
		if (!this.canWritable(Feature.accessCtrlAccess))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return this.saveAccess(access, userGroupId, featureId);
	}

	public boolean deleteAccess(Long id) throws JsonProcessingException {
		// Check permission
		if (!this.canDeletable(Feature.accessCtrlAccess))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");
		
		Optional<Access> optional = accessRepos.findById(id);
		if (optional.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Access non trouvé");
		Access acs = optional.get();

		Operation operation = Operation.ACCESS_DELETE;
		Access valAvant = acs;
		Access valApres = null;
		auditService.traceChange(operation, valAvant, valApres);

		accessRepos.delete(acs);
		return true;
	}
}