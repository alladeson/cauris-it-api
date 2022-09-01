package com.alladeson.caurisit.services;

import com.alladeson.caurisit.models.entities.Audit;
import com.alladeson.caurisit.models.entities.Operation;
import com.alladeson.caurisit.models.entities.User;
import com.alladeson.caurisit.repositories.AuditRepository;
import com.alladeson.caurisit.repositories.UserRepository;
import com.alladeson.caurisit.security.core.AccountService;
import com.alladeson.caurisit.security.entities.Account;
import com.alladeson.caurisit.security.entities.TypeRole;
import com.alladeson.caurisit.utils.Tool;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuditService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AuditRepository repository;

	@Autowired
	private Tool tool;

	@Autowired
	private UserRepository userRepository;

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

	public void traceChange(Operation operation, Object objetAvant, Object objetApres) throws JsonProcessingException {

		String valeurAvant = tool.toJson(objetAvant);
		String valeurApres = tool.toJson(objetApres);

		traceChange(operation, valeurAvant, valeurApres);
	}

	public void traceChange(Operation operation, String valeurAvant, String valeurApres) {
		try {
			User user = this.getAuthenticated();
			var code = operation.name();
			var desc = operation.desc();

			Audit a = new Audit();
			a.setDateHeure(LocalDateTime.now());
			a.setUser(user);
			a.setOperation(operation);
			a.setCode(code);
			a.setDescription(desc);
			a.setValeurAvant(valeurAvant);
			a.setValeurApres(valeurApres);

			repository.save(a);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	public List<Audit> getAll() {
		// Check permission
		var user = this.getAuthenticated();
		if (user.isSA())
			return repository.findAll();
		else
			return repository.findAllByUserRoleNot(TypeRole.SUPER_ADMIN);
	}

	public List<Audit> getAll(Long userId, String desc, LocalDateTime debut, LocalDateTime fin, String search) {

		// Check permission
		var user = this.getAuthenticated();
		// Si l'utilisteur n'est pas un super admin ou un admin, il ne peut récupérer
		// les audits
		if (!user.getGroup().getRole().equals(TypeRole.SUPER_ADMIN)
				&& !user.getGroup().getRole().equals(TypeRole.ADMIN))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		// Récupération des audits

		// Liste vide
		if (search.equals("vide"))
			return new ArrayList<Audit>();

		if (userId != null && StringUtils.hasText(desc) && debut != null && fin != null)
			return repository.findByUserIdAndDescriptionAndDateHeureBetweenOrderByDateHeureDesc(userId, desc, debut,
					fin);
		if (userId != null && StringUtils.hasText(desc) && debut != null)
			return repository.findByUserIdAndDescriptionAndDateHeureAfterOrderByDateHeureDesc(userId, desc, debut);
		if (userId != null && StringUtils.hasText(desc) && fin != null)
			return repository.findByUserIdAndDescriptionAndDateHeureBeforeOrderByDateHeureDesc(userId, desc, fin);
		if (userId != null && StringUtils.hasText(desc))
			return repository.findByUserIdAndDescriptionOrderByDateHeureDesc(userId, desc);
		if (userId != null && debut != null && fin != null)
			return repository.findByUserIdAndDateHeureBetweenOrderByDateHeureDesc(userId, debut, fin);
		if (userId != null && fin != null)
			return repository.findByUserIdAndDateHeureBeforeOrderByDateHeureDesc(userId, fin);
		if (userId != null && debut != null)
			return repository.findByUserIdAndDateHeureAfterOrderByDateHeureDesc(userId, debut);
		if (userId != null)
			return repository.findByUserIdOrderByDateHeureDesc(userId);
		if (StringUtils.hasText(desc) && debut != null && fin != null)
			return repository.findByDescriptionAndDateHeureBetweenOrderByDateHeureDesc(desc, debut, fin);
		if (StringUtils.hasText(desc) && fin != null)
			return repository.findByDescriptionAndDateHeureBeforeOrderByDateHeureDesc(desc, fin);
		if (StringUtils.hasText(desc) && debut != null)
			return repository.findByDescriptionAndDateHeureAfterOrderByDateHeureDesc(desc, debut);
		if (StringUtils.hasText(desc))
			return repository.findByDescriptionOrderByDateHeureDesc(desc);
		if (debut != null && fin != null)
			return repository.findByDateHeureBetweenOrderByDateHeureDesc(debut, fin);
		if (debut != null)
			return repository.findByDateHeureAfterOrderByDateHeureDesc(debut);
		if (fin != null)
			return repository.findByDateHeureBeforeOrderByDateHeureDesc(fin);

		// On récupère tout si aucune condition n'est valable
		return this.getAll();
	}

	public Audit get(Long id) {
		// Check permission
		var user = this.getAuthenticated();
		// Si l'utilisteur n'est pas un super admin ou un admin, il ne peut récupérer
		// les audits
		if (!user.getGroup().getRole().equals(TypeRole.SUPER_ADMIN)
				&& !user.getGroup().getRole().equals(TypeRole.ADMIN))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		Optional<Audit> optional = repository.findById(id);
		if (optional.isEmpty())
			return null;

		return optional.get();
	}

	public List<Audit> getForConnectedUser() {
		// Check permission
		var user = this.getAuthenticated();
		// Si l'utilisteur n'est pas un super admin ou un admin, il ne peut récupérer
		// les audits
		if (!user.getGroup().getRole().equals(TypeRole.SUPER_ADMIN)
				&& !user.getGroup().getRole().equals(TypeRole.ADMIN))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réfusé");

		return repository.findByUserIdOrderByDateHeureDesc(user.getId());
	}

}
