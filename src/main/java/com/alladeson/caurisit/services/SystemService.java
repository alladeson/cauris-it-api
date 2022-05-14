package com.alladeson.caurisit.services;

import com.alladeson.caurisit.config.AppConfig;
import com.alladeson.caurisit.models.entities.*;
//import com.alladeson.caurisit.repositories.SysParameterRepository;
import com.alladeson.caurisit.security.core.AccountService;
import com.alladeson.caurisit.security.core.RoleService;
import com.alladeson.caurisit.security.entities.Account;
import com.alladeson.caurisit.security.entities.Role;
import com.alladeson.caurisit.security.entities.TypeRole;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.Collections;

@Service
public class SystemService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private RoleService roleService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private UserService userService;
	@Autowired
	private ParametreService paramService;
	@Autowired
	private AppConfig config;

	@EventListener
	public void onApplicationInitialized(ContextRefreshedEvent event) throws IOException {
		logger.info(">> APP INITIALIZED");

		var watch = new StopWatch("APP INIT DATA");
		try {
			logger.info(">> APP INIT DATA - START");

			//
			if (!userService.existsByUsername("super_admin")) {
				watch.start("Init - Users");

				/* User SA */
				var roleSuperAdmin = roleService.save(new Role(TypeRole.SUPER_ADMIN.name()));
				var account = new Account();
				account.setUsername("super_admin");
				account.setEmail(config.getEmailAdmin());
				account.setPassword(passwordEncoder.encode("super@admin"));
//                account.setEnabled(true);
//                account.setPasswordEnabled(false);
//                account.setSys(true);
				account.setRoles(Collections.singleton(roleSuperAdmin));
				account = accountService.save(account);
				var user = new User();
				user.setFirstname("Superadmin");
				user.setLastname("SA");
				user.setRole(TypeRole.SUPER_ADMIN);
				user.setAccount(account);
				userService.save(user);
			}
			/* Chargement des groupe de taxe */
			if (paramService.getAllTaxe().isEmpty()) {
				// Groupe A
				var taxe = new Taxe();
				taxe.setType(TypeData.IMPOT);
				taxe.setGroupe(TaxeGroups.A);
				taxe.setLibelle("Exonéré");
				taxe.setValeur(0);
				paramService.createTaxe(taxe);
				// Groupe B
				taxe = new Taxe();
				taxe.setType(TypeData.IMPOT);
				taxe.setGroupe(TaxeGroups.B);
				taxe.setLibelle("Taxable");
				taxe.setValeur(18);
				paramService.createTaxe(taxe);
				// Groupe C
				taxe = new Taxe();
				taxe.setType(TypeData.IMPOT);
				taxe.setGroupe(TaxeGroups.C);
				taxe.setLibelle("Exportation de produits taxables");
				taxe.setValeur(0);
				paramService.createTaxe(taxe);
				// Groupe D
				taxe = new Taxe();
				taxe.setType(TypeData.IMPOT);
				taxe.setGroupe(TaxeGroups.D);
				taxe.setLibelle("TVA régime d'exception");
				taxe.setValeur(18);
				paramService.createTaxe(taxe);
				// Groupe E
				taxe = new Taxe();
				taxe.setType(TypeData.IMPOT);
				taxe.setGroupe(TaxeGroups.E);
				taxe.setLibelle("Régime fiscal TPS");
				taxe.setValeur(0);
				paramService.createTaxe(taxe);
				// Groupe F
				taxe = new Taxe();
				taxe.setType(TypeData.IMPOT);
				taxe.setGroupe(TaxeGroups.F);
				taxe.setLibelle("Réservé");
				taxe.setValeur(0);
				paramService.createTaxe(taxe);
				// Groupe aibA
				taxe = new Taxe();
				taxe.setType(TypeData.AIB);
				taxe.setGroupe(TaxeGroups.AibA);
				taxe.setLibelle("Aib 1%");
				taxe.setValeur(1);
				paramService.createTaxe(taxe);
				// Groupe aibB
				taxe = new Taxe();
				taxe.setType(TypeData.AIB);
				taxe.setGroupe(TaxeGroups.AibB);
				taxe.setLibelle("Aib 5%");
				taxe.setValeur(5);
				paramService.createTaxe(taxe);
			}
			/* Chargment des types de facture */
			if (paramService.getAllTypeFactureVente().isEmpty()) {
				// Facture de vente FV
				var tf1 = new TypeFacture();
				tf1.setGroupe(TypeData.FV);
				tf1.setType(TypeFactureEnum.FV);
				tf1.setDescription("Facture de vente");
				tf1 = paramService.createTypeFacture(tf1);
				// Facture d'avoir FA
				var tf = new TypeFacture();
				tf.setGroupe(TypeData.FA);
				tf.setType(TypeFactureEnum.FA);
				tf.setDescription("Facture d'avoir");
				tf.setOrigine(tf1);
				paramService.createTypeFacture(tf);
				// Facture de vente à l'exportation EV
				var tf2 = new TypeFacture();
				tf2.setGroupe(TypeData.FV);
				tf2.setType(TypeFactureEnum.EV);
				tf2.setDescription("Facture de vente à l'exportation");
				tf2 = paramService.createTypeFacture(tf2);
				// Facture d'avoir à l'exportation EA
				tf = new TypeFacture();
				tf.setGroupe(TypeData.FA);
				tf.setType(TypeFactureEnum.EA);
				tf.setDescription("Facture d'avoir à l'exportation");
				tf.setOrigine(tf2);
				paramService.createTypeFacture(tf);
			}
			/* Chargement des types de paiement */
			if (paramService.getAllTypePaiement().isEmpty()) {
				// ESPECES
				var tp = new TypePaiement();
				tp.setType(TypePaiementEnum.ESPECES);
				tp.setDescription("ESPECES");
				paramService.createTypePaiement(tp);
				// CHEQUES
				tp = new TypePaiement();
				tp.setType(TypePaiementEnum.CHEQUES);
				tp.setDescription("CHEQUES");
				paramService.createTypePaiement(tp);
				// MOBILE MONEY
				tp = new TypePaiement();
				tp.setType(TypePaiementEnum.MOBILEMONEY);
				tp.setDescription("MOBILE MONEY");
				paramService.createTypePaiement(tp);
				// CARTE BANCAIRE
				tp = new TypePaiement();
				tp.setType(TypePaiementEnum.CARTEBANCAIRE);
				tp.setDescription("CARTE BANCAIRE");
				paramService.createTypePaiement(tp);
				// VIREMENT
				tp = new TypePaiement();
				tp.setType(TypePaiementEnum.VIREMENT);
				tp.setDescription("VIREMENT");
				paramService.createTypePaiement(tp);
				// CREDIT
				tp = new TypePaiement();
				tp.setType(TypePaiementEnum.CREDIT);
				tp.setDescription("CREDIT");
				paramService.createTypePaiement(tp);
				// AUTRE
				tp = new TypePaiement();
				tp.setType(TypePaiementEnum.AUTRE);
				tp.setDescription("AUTRE");
				paramService.createTypePaiement(tp);
			}

		} finally {
			logger.info(watch.prettyPrint());
			logger.info(">> APP INIT DATA - END");
		}
	}

}
