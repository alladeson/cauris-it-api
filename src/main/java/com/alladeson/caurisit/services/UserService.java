package com.alladeson.caurisit.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.alladeson.caurisit.config.AppConfig;
import com.alladeson.caurisit.models.entities.*;
import com.alladeson.caurisit.models.paylaods.SignupRequest;
import com.alladeson.caurisit.repositories.UserGroupRepository;
import com.alladeson.caurisit.repositories.UserRepository;
import com.alladeson.caurisit.security.core.AccountService;
import com.alladeson.caurisit.security.core.PasswordPayload;
import com.alladeson.caurisit.security.core.PasswordResetPayload;
import com.alladeson.caurisit.security.core.RoleService;
import com.alladeson.caurisit.security.entities.Account;
import com.alladeson.caurisit.security.entities.Role;
import com.alladeson.caurisit.security.entities.TypeRole;
import com.alladeson.caurisit.utils.Tool;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

@Service
public class UserService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserRepository repository;
	@Autowired
	private UserGroupRepository groupeRepos;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AccountService accountService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private AccessService accessService;
	@Autowired
	private AuditService auditService;
	@Autowired
	private Tool tool;
	@Autowired
	private FileService fileService;
	@Autowired
	private AppConfig config;

	public List<User> getAll() {
//		return repository.findByGroupProfileIdNot(UserProfiles.SA);
		return repository.findAll();
	}

	public List<User> getAllForAdmin() {
		return repository.findAllByRoleNot(TypeRole.SUPER_ADMIN);
	}
	public User find(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
	}

	public User get(Long id) {
		return this.find(id);
	}

	public User get() {
		return this.getAuthenticated();
	}

	public User create(User user, Long groupeId) throws JsonProcessingException {
		// Check permission
		if (!accessService.canWritable(Feature.accessCtrlUser))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");
		
		UserGroup groupe = groupeRepos.findById(groupeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Groupe d'utilisateur non trouvé"));

		// vérifie la disponibilité de l'identifiant
		if (accountService.existsByUsername(user.getUsername()))
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Identifiant non disponible");

		// génère un mot de passe par défaut s'il n'est pas fourni
		if (!StringUtils.hasText(user.getDefaultPassword()))
//            user.setDefaultPassword(RandomStringUtils.randomAlphanumeric(10));
//        logger.trace("--> defaultPassword : " + user.getDefaultPassword());
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Mot de passe non défini");

		var u = new User();
		u.setDefaultPassword(user.getDefaultPassword());
		u.setPassword(passwordEncoder.encode(u.getDefaultPassword()));
		u = this.save(user, u, groupe);
		
		auditService.traceChange(Operation.USER_CREATE, null, u);

//        if (StringUtils.hasText(u.getEmail())) {
//            // envoie un mail d'activation du compte
//            this.sendMail(u, config.getMailSignupRequestTitle(), "signup-request");
//        }
		return u;
	}
	
	/**
	 * @param article
	 * @return
	 */
	private User saveUser(User user, boolean delete) {
		try {
			if (delete)
				repository.delete(user);
			else
				user = repository.save(user);
		} catch (Exception e) {
			// Contrainte d'unicité
			if (e.getCause() instanceof ConstraintViolationException) {
				// Récupération du vrai cause de l'exception
				SQLIntegrityConstraintViolationException exception = (SQLIntegrityConstraintViolationException) (e
						.getCause()).getCause();
				exception.printStackTrace();
				if (delete && exception.getMessage().contains("foreign key constraint"))
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"Cet d'utilisateur est déjà associé à d'autres données");
//				else if (exception.getMessage().contains("UniqueName"))
//					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Un autre utilisateur porte déjà le mêm nom");
//				else if (exception.getMessage().contains("ne peut être vide"))
//					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le nom du group utilisateur ne peut être vide");
				else
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, exception.getMessage());

			} else
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
		}
		return user;
	}

	public User update(Long id, User user, Long groupeId) throws JsonProcessingException {
		logger.info(">> update");
		logger.info("---> id: {} - user: {}", id, user);

		// Check permission
		if (!accessService.canWritable(Feature.accessCtrlUser))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// récupère l'utilisateur
		var user1 = repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
		
		UserGroup groupe = groupeRepos.findById(groupeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Groupe d'utilisateur non trouvé"));

		// vérifie la disponibilité de l'identifiant s'il a été modifié
		if (!user.getUsername().equals(user1.getUsername()) && accountService.existsByUsername(user.getUsername()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identifiant non disponible");
		
		String valAvant = tool.toJson(user1);

		// génère un mot de passe par défaut s'il n'est pas fourni
		if (StringUtils.hasText(user.getDefaultPassword())) {
			user1.setDefaultPassword(user.getDefaultPassword());
			user1.setPassword(passwordEncoder.encode(user1.getDefaultPassword()));
		}

		user1 = save(user, user1, groupe);
		
		String valApres = tool.toJson(user1);
		
		auditService.traceChange(Operation.USER_UPDATE, valAvant, valApres);

		return user1;
	}

	public User save(User user) {
		return repository.save(user);
	}

	private User save(User user, User user1, UserGroup groupe) {
		user1.setUsername(user.getUsername());
		user1.setEmail(user.getEmail());
		user1.setFirstname(user.getFirstname());
		user1.setLastname(user.getLastname());
		user1.setPhone(user.getPhone());
		user1.setRole(groupe.getRole());
		user1.setEmail(user.getEmail());
		user1.setGroup(groupe);

		var account = user1.getAccount();
		if (account.getRoles().isEmpty()) {
			var role = roleService.findByName(groupe.getRole().name())
					.orElseGet(() -> roleService.save(new Role(groupe.getRole().name())));
			account.setRoles(Collections.singleton(role));
//            account.setEnabled(true);
			// account.setEnabled(false);
//            account.setPasswordEnabled(false);
		}
		account = accountService.save(account);
		user1.setAccount(account);
		return saveUser(user1, false);
	}

	public boolean delete(Long id) throws JsonProcessingException {

		// Check permission
		if (!accessService.canDeletable(Feature.accessCtrlUser))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// récupère l'utilisateur
		var user = repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));

		auditService.traceChange(Operation.USER_DELETE, user, null);
		
//		repository.delete(user);
		user = saveUser(user, true);
		accountService.delete(user.getAccount());

		return true;
	}

	public User getAuthenticated() {
		Account account = accountService.getAuthenticated();
		return repository.findByAccount(account)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
	}

	public boolean existsByUsername(String username) {
		return accountService.existsByUsername(username);
	}

	public User updatePhoto(Long id, MultipartFile file) {
		var u = this.find(id);
		var filename = "photo_" + id + "." + FilenameUtils.getExtension(file.getOriginalFilename());
		fileService.store(file, filename);
		// Mise à jour de la photo
		u.setPhoto(filename);
		// Enregistrement et renvoie de l'utilisateur
		return repository.save(u);
	}

	public User login() throws JsonProcessingException {
        var u = this.getAuthenticated();

        auditService.traceChange(Operation.USER_LOGIN, u, null);
        return u;
    }

    public User logout() throws JsonProcessingException {
        var u = this.getAuthenticated();

        auditService.traceChange(Operation.USER_LOGOUT, u, null);
        return u;
    }

	public User register(SignupRequest signup, Long groupeId) {
		
		UserGroup groupe = groupeRepos.findById(groupeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Groupe d'utilisateur non trouvé"));
		
		// vérifie la disponibilité de l'identifiant
		if (accountService.existsByUsername(signup.getUsername()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identifiant non disponible");

		// vérifie la confirmation du mot de passe
		if (!signup.getPassword().equals(signup.getConfirmedPassword()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mot de passe non confirmé.");

		var u = new User();
		Account compte = u.getAccount();
		compte.setUsername(signup.getUsername());
		compte.setPassword(passwordEncoder.encode(signup.getPassword()));
		compte.setEmail(signup.getEmail());
		compte.setPhone(signup.getPhone());
		u = save(signup, u, groupe);

		// envoie un mail d'activation du compte
		this.sendMail(u, config.getMailSignupRequestTitle(), "signup-request");

		return u;
	}

//    /**
//     * Active un compte utilisateur donné.
//     *
//     * @param accountId
//     * @return
//     * @implNote Permet d'activer un compte utilisateur à partir d'un lien public envoyé par mail à l'utilisateur lors
//     * de sa création, et à la première connexion l'utilisateur est invité à changer son mot de passe.
//     */
//    public User activateAccount(String accountId) {
//        // var account = accountService.confirm(accountId);
//        var account = accountService.getById(accountId);
//        if (account.isEnabled()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");
//        var user = repository.findByAccountId(account.getId()).orElseThrow(
//                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé")
//        );
//
//        String valAvant = tool.toJson(account);
//
//        //TODO: Gérer le délai d'activation du compte
//
//        account.setEnabled(true);
//        account.setPasswordEnabled(false);
//        accountService.save(account);
//
//
//        // envoie un mail de notification
//        this.sendMail(user, config.getMailSignupSuccessTitle(), "signup-success");
//
//        String valApres = tool.toJson(account);
//        auditService.traceChange(Operation.USER_ACTIVATE, valAvant, valApres);
//
//        return user;
//    }

//    public User activateDesactivateAccount(Long userId) {
//
//        User user1 = repository.findById(userId).orElseThrow(
//                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé")
//        );
//        // var account = accountService.confirm(accountId);
//        var account = user1.getAccount();
//        //if (account.isEnabled()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");
//        var user = repository.findByAccountId(account.getId()).orElseThrow(
//                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé")
//        );
//
//        String valAvant = tool.toJson(account);
//
//        account.setEnabled(!account.isEnabled());
//        accountService.save(account);
//
//        String valApres = tool.toJson(account);
//        auditService.traceChange(Operation.USER_ACTIVATE, valAvant, valApres);
//
//        return user1;
//    }

	/**
	 * Active le mot de passe du compte utilisateur connecté.
	 *
	 * @param password
	 * @return
	 * @implNote Permet de changer le mot de passe par défaut du compte utilisateur
	 *           connecté à sa première connexion ou lorsque son mot de passe est
	 *           réinitialisé.
	 */
	public User activatePassword(PasswordPayload password) {
		try {
			logger.trace(">> activatePassword - start");
			logger.trace("--> password: {}", password);

			var user = this.getAuthenticated();
			var account = user.getAccount();
//            if (account.isPasswordEnabled()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Mot de passe déjà activé.");
			//
			if (!password.getNew().equals(password.getConfirmed())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nouveau mot de passe non confirmé.");
			}

			String valAvant = tool.toJson(account);

			// TODO: Gérer le délai d'activation du compte

			account.setPassword(passwordEncoder.encode(password.getNew()));
//            account.setPasswordEnabled(true);
			accountService.save(account);

			// envoie un mail de notification
			this.sendMail(user, config.getMailSignupSuccessTitle(), "signup-success");

			String valApres = tool.toJson(account);
			
            auditService.traceChange(Operation.USER_ACTIVATE, valAvant, valApres);

			return user;
		} finally {
			logger.trace(">> activatePassword - end");
		}
	}

	public User deactivatePassword(Long id, PasswordResetPayload reset) {
		try {
			logger.trace(">> deactivatePassword - start");
			logger.trace("--> userId: {}", id);

			// récupère l'utilisateur
			var user = repository.findById(id)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
			var account = user.getAccount();

			String valAvant = tool.toJson(user);

			// génère un mot de passe par défaut s'il n'est pas fourni
			if (!StringUtils.hasText(reset.getDefaultPassword()))
				reset.setDefaultPassword(RandomStringUtils.randomAlphanumeric(10));
			logger.trace("--> defaultPassword : " + reset.getDefaultPassword());

			user.setDefaultPassword(reset.getDefaultPassword());
			user.setPassword(passwordEncoder.encode(user.getDefaultPassword()));
			// account.setPasswordEnabled(false);
			accountService.save(account);
			
			String valApres = tool.toJson(user);
			
            auditService.traceChange(Operation.USER_DEACTIVATE, valAvant, valApres);

			return user;
		} finally {
			logger.trace(">> deactivatePassword - end");
		}
	}

	public User changePassword(PasswordPayload password) {
		var user = this.getAuthenticated();
		var account = user.getAccount();
		
		String valAvant = tool.toJson(account);

		if (!passwordEncoder.matches(password.getOld(), account.getPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ancien mot de passe incorrect.");
		}
		if (!password.getNew().equals(password.getConfirmed())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nouveau mot de passe non confirmé.");
		}

		account.setPassword(passwordEncoder.encode(password.getNew()));
		account = accountService.save(account);
		
		String valApres = tool.toJson(account);
		
        auditService.traceChange(Operation.USER_PASSWORD_CHANGE, valAvant, valApres);
        
		return user;
	}

	/**
	 * Send a mail with a link to reset the password.
	 *
	 * @param request
	 */
	public void resetPassword(PasswordResetPayload request) {

	}

	/**
	 * Reset/disable the specified user password.
	 *
	 * @param userId
	 */
	public void resetPassword(Long userId) {

	}

	private void sendMail(User u, String title, String template) {
		try {
			if (!StringUtils.hasText(u.getEmail()))
				return;

			Map<String, Object> vars = new HashMap<>();
			vars.put("account", u.getAccount());
			vars.put("user", u);
			boolean sent = tool.sendMail(config.getEmailNoReply(), config.getAppName(), new String[] { u.getEmail() },
					title, template, vars);

			// si l'envoi de mail a échoué, met le mail en attente pour une tentative
			// d'envoi auto
			if (!sent) {

			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

}
