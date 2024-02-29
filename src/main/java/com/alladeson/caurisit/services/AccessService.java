/**
 * 
 */
package com.alladeson.caurisit.services;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.SSLException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.alladeson.caurisit.config.AppConfig;
import com.alladeson.caurisit.models.entities.Access;
import com.alladeson.caurisit.models.entities.Feature;
import com.alladeson.caurisit.models.entities.Operation;
import com.alladeson.caurisit.models.entities.Parametre;
import com.alladeson.caurisit.models.entities.User;
import com.alladeson.caurisit.models.entities.UserGroup;
import com.alladeson.caurisit.models.paylaods.JwtAuthResponsePayload;
import com.alladeson.caurisit.repositories.AccessRepository;
import com.alladeson.caurisit.repositories.FeatureRepository;
import com.alladeson.caurisit.repositories.UserGroupRepository;
import com.alladeson.caurisit.repositories.UserRepository;
import com.alladeson.caurisit.security.core.AccountService;
import com.alladeson.caurisit.security.entities.Account;
import com.alladeson.caurisit.security.entities.TypeRole;
import com.alladeson.caurisit.utils.Tool;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import net.sf.jasperreports.engine.JRException;
import reactor.netty.http.client.HttpClient;

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

	@Autowired
	private AppConfig config;

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

	/** feature Management **/

	/**
	 * Retrieve minimum features required for administrators, 
	 * While super-administrator have access to all features
	 * @return
	 */
	public List<Feature> getAllFeatureForAdmin() {
		// Check permission
		if (!this.canReadable(Feature.accessCtrlFeatures))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		var codes = new ArrayList<Integer>();
		// codes.add(Feature.parametreSysteme);
		// codes.add(Feature.gestStock);
		codes.add(Feature.parametreDonneSysteme);
		// codes.add(Feature.accessCtrl);
		// codes.add(Feature.accessCtrlAccess);
		codes.add(Feature.accessCtrlFeatures);
		codes.add(Feature.accessCtrlUser);
		codes.add(Feature.accessCtrlUserGroup);
		// codes.add(Feature.audit);
		return featureRepos.findAllByCodeNotIn(codes);
	}
	
	/**
	 * Retrieve minimum features required for other users, 
	 * @return
	 */
	public List<Feature> getAllFeatureForOthers() {
		// Check permission
		if (!this.canReadable(Feature.accessCtrlFeatures))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		var codes = new ArrayList<Integer>();
		// codes.add(Feature.gestStock);
		// codes.add(Feature.parametreSysteme);
		codes.add(Feature.parametre);
		codes.add(Feature.parametreDonneSysteme);
		codes.add(Feature.accessCtrl);
		// codes.add(Feature.accessCtrlAccess);
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
		// Feature featureToUpdateSave = optionalFeature.get();

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
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Un autre groupe utilisateur porte déjà le mêm nom");
				else if (exception.getMessage().contains("ne peut être vide"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Le nom du group utilisateur ne peut être vide");
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
		// logger.info(group.toString());

		ug.setName(group.getName());
		ug.setDescription(group.getDescription());
		ug.setRole(group.getRole());
		ug.setUpdatedAt(null);
		ug = saveUserGroupe(ug, false);

		// Permissions
		if (!ug.getAccess().isEmpty()) {
			for (var ac : ug.getAccess()) {
				// logger.info(ac.toString());
				var ft = findFeatureByCode(ac.getMenu());
				this.saveAccess(ug, ft, ac.isReadable(), ac.isWritable(), ac.isDeletable());
			}
		} else {
			// Permissions
			List<Feature> features = new ArrayList<Feature>();
			if(ug.getRole().equals(TypeRole.ADMIN))
				features = this.getAllFeatureForAdmin();
			else 
				features = this.getAllFeatureForOthers();
			// Save access for the new group
			for (Feature f : features) {
				// this.saveAccess(ug, f, ug.getRole().equals(TypeRole.ADMIN) ? true : false);
				this.saveAccess(ug, f, true);
			}
		}
		return ug;
	}

	// public UserGroup saveGroupe(UserGroup group) {
	// return groupeRepos.save(group);
	// }

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

	// public UserGroup saveGroupe(String name, TypeRole role) {
	// return saveGroupe(name, null, role);
	// }

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

	// public List<Access> getAllAccess(String search) {
	// return accessRepos.findByGroupNameOrFeatureNameContaining(search, search);
	// }

	// public List<Access> saveAllAccess(List<Access> access) {
	// return accessRepos.saveAll(access);
	// }

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

	// public Access saveAccess(Long userGroupId, Long featureId, boolean readable,
	// boolean writable, boolean deletable)
	// throws JsonProcessingException {
	// var acces = new Access();
	// acces.setReadable(readable);
	// acces.setWritable(writable);
	// acces.setDeletable(deletable);
	// return this.saveAccess(acces, userGroupId, featureId);
	// }

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

	/*** Gestion de la verification du ***/

	/**
	 * 
	 * @param serialKey
	 * @return
	 * @throws URISyntaxException
	 * @throws SSLException
	 */
	public boolean checkSecrialKey(String serialKey) throws URISyntaxException, SSLException {
		WebClient client = webClientBuilder();

		// MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();

		Map<String, String> bodyMap = loginData();

		String token = remoteLogin(client, bodyMap);

		return serialKeyChecking(serialKey, client, token);
	}

	/**
	 * @return
	 */
	public Map<String, String> loginData() {
		Map<String, String> bodyMap = new HashMap<>();
		bodyMap.put("login", config.getSaUsername());
		bodyMap.put("password", config.getSaPassword() + "G6erxOlQkKitFlZlxZgyP27V6mu");
		return bodyMap;
	}

	/**
	 * @return
	 * @throws SSLException
	 */
	public WebClient webClientBuilder() throws SSLException {
		SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
				.build();

		HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

		// WebClient client = WebClient.create();
		WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
		return client;
	}

	/**
	 * @param serialKey
	 * @param client
	 * @param token
	 * @return
	 * @throws URISyntaxException
	 */
	private boolean serialKeyChecking(String serialKey, WebClient client, String token) throws URISyntaxException {
		boolean response = client.post().uri(new URI(config.getSkChckUri() + serialKey))
				.header("Authorization", "Bearer " + token)
				// .contentType(MediaType.APP•LICATION_FORM_URLENCODED)
				// .contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				// .body(BodyInserters.fromFormData(bodyValues))
				.body(null).retrieve().bodyToMono(boolean.class).block();

		return response;
	}

	/**
	 * @param client
	 * @param bodyMap
	 * @return
	 * @throws URISyntaxException
	 */
	private String remoteLogin(WebClient client, Map<String, String> bodyMap) throws URISyntaxException {
		ResponseEntity<JwtAuthResponsePayload> response = client.post().uri(new URI(config.getSkLoginUri()))
				// .header("Authorization", "Bearer MY_SECRET_TOKEN")
				// .contentType(MediaType.APP•LICATION_FORM_URLENCODED)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				// .body(BodyInserters.fromFormData(bodyValues))
				.body(BodyInserters.fromValue(bodyMap)).retrieve().toEntity(JwtAuthResponsePayload.class).block();

		return response.getBody().getToken();
	}

	/** Envoie de mail après création du paramètre **/

	@Async
	public CompletableFuture<Boolean> sendMail(Parametre param, String template) throws IOException, JRException {
		if (!StringUtils.hasText(param.getEmail()))
			return CompletableFuture.completedFuture(false);
		//
		Map<String, Object> vars = new HashMap<>();
		vars.put("parametre", param);
		// Ajout de la pièce jointe
		File[] file = new File[1];
		file[0] = new File(config.getUploadDir() + "/" + param.getConfigReport());
		// Titre du mail
		String title = "Rapport de configuration";
		// Envoie du mail
		boolean send = tool.sendMail(config.getEmailNoReply(), config.getAppName(),
				new String[] { param.getEmail(), config.getEmailAdmin(), config.getEmailAdmin2(), config.getEmailCaurisit() }, title, template, vars, file);
		// Envoie de la reponse
		return CompletableFuture.completedFuture(send);

	}

	/** Gestion de l'envoie des données de paramètre **/
	/**
	 * 
	 * @param parametre
	 * @return
	 * @throws URISyntaxException
	 * @throws SSLException
	 */
	@Async
	public CompletableFuture<Parametre> sendParametreData(Parametre parametre, boolean update) throws URISyntaxException, SSLException {
		WebClient client = webClientBuilder();
		// Récupération des données de login
		Map<String, String> bodyMap = loginData();
		// Connexion et recupération du token
		String token = remoteLogin(client, bodyMap);
		// Envoie des données de paramètre au server distant
		Parametre params = null;
		if(!update) // En cas de la création du paramètre
			params = sendingParametre(client, this.setParametreBodyMap(parametre), token);
		// En cas de mise à jour du paramètre
		params = sendingUpdateParametre(parametre, client, this.setParametreBodyMap(parametre), token);
		// Envoie de la reponse
		return CompletableFuture.completedFuture(params);
	}

	/**
	 * Formater le body data pour l'envoie des données de paramètre
	 * 
	 * @param <T>
	 * @param parametre
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> Map<String, T> setParametreBodyMap(Parametre parametre) {
		Map<String, T> bodyMap = new HashMap<>();

		Class<?> paramClass = parametre.getClass();

		Field[] fields = paramClass.getDeclaredFields();

		// Mise à jour du tokenTmp : utile pour l'enregistrement car le token n'est par
		// reçu par JSON
		parametre.setTokenTmp(parametre.getToken());
		// Formatage du bodyMap
		for (Field field : fields) {
			String fieldName = field.getName();
			String methodName = "get" + this.capitalize(fieldName);
			try {
				Method method = paramClass.getDeclaredMethod(methodName);
				bodyMap.put(fieldName, (T) method.invoke(parametre));
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bodyMap;
	}

	/**
	 * Retourne la chaine avec la première lettre en grand caractère;
	 * 
	 * @param str
	 * @return
	 */
	private String capitalize(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}

		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * @param client
	 * @param bodyMap
	 * @return
	 * @throws URISyntaxException
	 */
	private Parametre sendingParametre(WebClient client, Map<String, String> bodyMap, String token)
			throws URISyntaxException {
		ResponseEntity<Parametre> response = client.post().uri(new URI(config.getParamSendUri()))
				.header("Authorization", "Bearer " + token)
				// .contentType(MediaType.APP•LICATION_FORM_URLENCODED)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				// .body(BodyInserters.fromFormData(bodyValues))
				.body(BodyInserters.fromValue(bodyMap)).retrieve().toEntity(Parametre.class).block();

		return response.getBody();
	}

	/**
	 * @param parametre
	 * @param client
	 * @param bodyMap
	 * @return
	 * @throws URISyntaxException
	 */
	private Parametre sendingUpdateParametre(Parametre parametre, WebClient client, Map<String, String> bodyMap,
			String token)
			throws URISyntaxException {
		ResponseEntity<Parametre> response = client.put()
				.uri(new URI(config.getParamUpdateUri().replace("__key__", parametre.getSerialKey())))
				.header("Authorization", "Bearer " + token)
				// .contentType(MediaType.APP•LICATION_FORM_URLENCODED)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				// .body(BodyInserters.fromFormData(bodyValues))
				.body(BodyInserters.fromValue(bodyMap)).retrieve().toEntity(Parametre.class).block();

		return response.getBody();
	}

	/** Gestion de l'envoie du logo **/
	/**
	 * 
	 * @param serialKey
	 * @param parametre
	 * @return
	 * @throws URISyntaxException
	 * @throws SSLException
	 */
	@Async
	public CompletableFuture<Parametre> sendParametreLogo(Parametre parametre) throws URISyntaxException, SSLException {
		WebClient client = webClientBuilder();
		// Récupération des données de login
		Map<String, String> bodyMap = loginData();
		// Connexion et recupération du token
		String token = remoteLogin(client, bodyMap);
		// Envoie du logo au server distant
		Parametre params = sendingParametreLogo(parametre, client, token);
		// Envoie de la reponse
		return CompletableFuture.completedFuture(params);
	}

	/**
	 * @param client
	 * @param bodyMap
	 * @return
	 * @throws URISyntaxException
	 */
	private Parametre sendingParametreLogo(Parametre parametre, WebClient client, String token)
			throws URISyntaxException {
		ResponseEntity<Parametre> response = client.put()
				.uri(new URI(config.getParamLogoUri().replace("__key__", parametre.getSerialKey())))
				.header("Authorization", "Bearer " + token)
				// .contentType(MediaType.APP•LICATION_FORM_URLENCODED)
				.contentType(MediaType.MULTIPART_FORM_DATA).accept(MediaType.APPLICATION_JSON)
				// .body(BodyInserters.fromFormData(bodyValues))
				.body(BodyInserters
						.fromMultipartData(this.fromFile(new File(config.getUploadDir() + "/" + parametre.getLogo()))))
				.retrieve().toEntity(Parametre.class).block();

		return response.getBody();
	}

	/**
	 * Formatage du body pour l'envoie du fichier
	 * 
	 * @param file
	 * @return
	 */
	private MultiValueMap<String, HttpEntity<?>> fromFile(File file) {
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("file", new FileSystemResource(file));
		return builder.build();
	}
	
	/**
	 * Envoie d'une facture validée par mail
	 * 
	 * @param Le nom du fichier de la facture
	 * @param template Le modèle Thymleaf du mail à envoyé
	 * @return True si le mail est envoyé, False sinon
	 * @throws IOException
	 * @throws JRException
	 */

	@Async
	public CompletableFuture<Boolean> sendMailFactureValidee(String fileName, String template) throws IOException, JRException {
		if (!StringUtils.hasText(config.getEmailFactureValidee()))
			return CompletableFuture.completedFuture(false);
		//
		Map<String, Object> vars = new HashMap<>();
		//vars.put("facture", facture); N'est pas utile
		// Ajout de la pièce jointe
		File[] file = new File[1];
		file[0] = new File(config.getUploadDir() + "/" + fileName);
		// Titre du mail
		String title = "Validation de facture";
		// Envoie du mail
		boolean send = tool.sendMail(config.getEmailNoReply(), config.getAppName(),
				new String[] { config.getEmailFactureValidee() }, title, template, vars, file);
		// Suppression du fichier de la facture que l'envoie de mail réussisse ou pas
		file[0].delete();
		// Envoie de la reponse
		return CompletableFuture.completedFuture(send);
	}
}
