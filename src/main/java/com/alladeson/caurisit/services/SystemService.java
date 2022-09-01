package com.alladeson.caurisit.services;

import com.alladeson.caurisit.config.AppConfig;
import com.alladeson.caurisit.models.entities.*;
import com.alladeson.caurisit.repositories.FeatureRepository;
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
	private AccessService accessService;
	
	@Autowired
	private FeatureRepository featureRepos;

	@Autowired
	private AppConfig config;

	@EventListener
	public void onApplicationInitialized(ContextRefreshedEvent event) throws IOException {
		logger.info(">> APP INITIALIZED");

		var watch = new StopWatch("APP INIT DATA");
		try {
			logger.info(">> APP INIT DATA - START");

			// User-groups
			UserGroup sa = null/* , adm = null */;
			if (accessService.countGroupe() == 0) {
				sa = accessService.saveGroupe("Super Admin", "Super Administrateur", TypeRole.SUPER_ADMIN);
			}

			//
			if (!userService.existsByUsername(config.getSaUsername())) {
				watch.start("Init - Users");
				/* User SA */
				var roleSuperAdmin = roleService.save(new Role(TypeRole.SUPER_ADMIN.name()));
				var account = new Account();
				account.setUsername(config.getSaUsername());
				account.setEmail(config.getEmailAdmin());
				account.setPassword(passwordEncoder.encode(config.getSaPassword()/* + "G6erxOlQkKitFlZlxZgyP27V6mu"*/));
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
				user.setGroup(sa);
				userService.save(user);
			}
			/* Chargement des groupe de taxe */
			if (paramService.getAllTaxe().isEmpty()) {
				// Groupe A
				var taxe = new Taxe();
				taxe.setType(TypeData.IMPOT);
				taxe.setGroupe(TaxeGroups.A);
				taxe.setLibelle("A-Exonéré");
				taxe.setDescription("Exonéré");
				taxe.setAbreviation("A-EX");
				taxe.setValeur(0);
				paramService.saveTaxe(taxe);
				// Groupe B
				taxe = new Taxe();
				taxe.setType(TypeData.IMPOT);
				taxe.setGroupe(TaxeGroups.B);
				taxe.setLibelle("B-Taxable");
				taxe.setDescription("Taxable");
				taxe.setAbreviation("B-TAX");
				taxe.setValeur(18);
				paramService.saveTaxe(taxe);
				// Groupe C
				taxe = new Taxe();
				taxe.setType(TypeData.IMPOT);
				taxe.setGroupe(TaxeGroups.C);
				taxe.setLibelle("C-Exportation");
				taxe.setDescription("Exportation de produits taxables");
				taxe.setAbreviation("C-EXP");
				taxe.setValeur(0);
				paramService.saveTaxe(taxe);
				// Groupe D
				taxe = new Taxe();
				taxe.setType(TypeData.IMPOT);
				taxe.setGroupe(TaxeGroups.D);
				taxe.setLibelle("D-Exception");
				taxe.setDescription("TVA régime d'exception");
				taxe.setAbreviation("D-EXCEP");
				taxe.setValeur(18);
				paramService.saveTaxe(taxe);
				// Groupe E
				taxe = new Taxe();
				taxe.setType(TypeData.IMPOT);
				taxe.setGroupe(TaxeGroups.E);
				taxe.setLibelle("E-Régime TPS");
				taxe.setDescription("Régime fiscal TPS");
				taxe.setAbreviation("E-TPS");
				taxe.setValeur(0);
				paramService.saveTaxe(taxe);
				// Groupe F
				taxe = new Taxe();
				taxe.setType(TypeData.IMPOT);
				taxe.setGroupe(TaxeGroups.F);
				taxe.setLibelle("F-Réservé");
				taxe.setDescription("Réservé");
				taxe.setAbreviation("F-RES");
				taxe.setValeur(0);
				paramService.saveTaxe(taxe);
				// Groupe aibA
				taxe = new Taxe();
				taxe.setType(TypeData.AIB);
				taxe.setGroupe(TaxeGroups.AibA);
				taxe.setLibelle("Aib 1%");
				taxe.setValeur(1);
				paramService.saveTaxe(taxe);
				// Groupe aibB
				taxe = new Taxe();
				taxe.setType(TypeData.AIB);
				taxe.setGroupe(TaxeGroups.AibB);
				taxe.setLibelle("Aib 5%");
				taxe.setValeur(5);
				paramService.saveTaxe(taxe);
			}
			/* Chargment des types de facture */
			if (paramService.getAllTypeFactureVente().isEmpty()) {
				// Facture de vente FV
				var tf1 = new TypeFacture();
				tf1.setGroupe(TypeData.FV);
				tf1.setType(TypeFactureEnum.FV);
				tf1.setDescription("Facture de vente");
				tf1 = paramService.saveTypeFacture(tf1);
				// Facture d'avoir FA
				var tf = new TypeFacture();
				tf.setGroupe(TypeData.FA);
				tf.setType(TypeFactureEnum.FA);
				tf.setDescription("Facture d'avoir");
				tf.setOrigine(tf1);
				paramService.saveTypeFacture(tf);
				// Facture de vente à l'exportation EV
				var tf2 = new TypeFacture();
				tf2.setGroupe(TypeData.FV);
				tf2.setType(TypeFactureEnum.EV);
				tf2.setDescription("Facture de vente à l'exportation");
				tf2 = paramService.saveTypeFacture(tf2);
				// Facture d'avoir à l'exportation EA
				tf = new TypeFacture();
				tf.setGroupe(TypeData.FA);
				tf.setType(TypeFactureEnum.EA);
				tf.setDescription("Facture d'avoir à l'exportation");
				tf.setOrigine(tf2);
				paramService.saveTypeFacture(tf);
			}
			/* Chargement des types de paiement */
			if (paramService.getAllTypePaiement().isEmpty()) {
				// ESPECES
				var tp = new TypePaiement();
				tp.setType(TypePaiementEnum.ESPECES);
				tp.setDescription("ESPECES");
				paramService.saveTypePaiement(tp);
				// CHEQUES
				tp = new TypePaiement();
				tp.setType(TypePaiementEnum.CHEQUES);
				tp.setDescription("CHEQUES");
				paramService.saveTypePaiement(tp);
				// MOBILE MONEY
				tp = new TypePaiement();
				tp.setType(TypePaiementEnum.MOBILEMONEY);
				tp.setDescription("MOBILE MONEY");
				paramService.saveTypePaiement(tp);
				// CARTE BANCAIRE
				tp = new TypePaiement();
				tp.setType(TypePaiementEnum.CARTEBANCAIRE);
				tp.setDescription("CARTE BANCAIRE");
				paramService.saveTypePaiement(tp);
				// VIREMENT
				tp = new TypePaiement();
				tp.setType(TypePaiementEnum.VIREMENT);
				tp.setDescription("VIREMENT");
				paramService.saveTypePaiement(tp);
				// CREDIT
				tp = new TypePaiement();
				tp.setType(TypePaiementEnum.CREDIT);
				tp.setDescription("CREDIT");
				paramService.saveTypePaiement(tp);
				// AUTRE
				tp = new TypePaiement();
				tp.setType(TypePaiementEnum.AUTRE);
				tp.setDescription("AUTRE");
				paramService.saveTypePaiement(tp);
			}

			/* Les fonctionnalités de l'application */
			if (accessService.countFeature() == 0) {
				// Gestion de stock
				accessService.saveFeature(Feature.gestStock, "Gestion de Stock", true, false, false);
				accessService.saveFeature(Feature.gestStockCategorie, "Catégorie Articles", true, true, true);
				accessService.saveFeature(Feature.gestStockArticle, "Articles", true, true, true);
				// Emission des factures
				accessService.saveFeature(Feature.facturation, "Facture", true, false, false);
				accessService.saveFeature(Feature.facturationFV, "Facture de Vente", true, true, true);
				accessService.saveFeature(Feature.facturationFA, "Facture d'avoir", true, true, true);
				accessService.saveFeature(Feature.facturationListe, "Liste des facture", true, true, true);
				accessService.saveFeature(Feature.facturationClient, "Clients", true, true, true);
				// Les données de base et paramètre du système
				accessService.saveFeature(Feature.parametre, "Paramètres", true, false, false);
				accessService.saveFeature(Feature.parametreTaxe, "Taxe", true, true, true);
				accessService.saveFeature(Feature.parametreTypeFacture, "Type de facture", true, true, true);
				accessService.saveFeature(Feature.parametreTypePaiement, "Type de paiement", true, true, true);
				accessService.saveFeature(Feature.parametreSysteme, "Paramètre du système", true, true, true);
				accessService.saveFeature(Feature.parametreDonneSysteme, "Données du système", true, true, true);
				// Le contrôle d'accès
				accessService.saveFeature(Feature.accessCtrl, "Accès & Audit", true, false, false);
				accessService.saveFeature(Feature.accessCtrlUser, "Utilisateurs", true, true, true);
				accessService.saveFeature(Feature.accessCtrlUserGroup, "Groupes Utilisateurs", true, true, true);
				accessService.saveFeature(Feature.accessCtrlFeatures, "Les fonctionnalités", true, true, true);
				accessService.saveFeature(Feature.accessCtrlAccess, "Le contrôle d'accès", true, true, true);
				accessService.saveFeature(Feature.audit, "Audit", true, true, true);
				// Les statistiques
				accessService.saveFeature(Feature.stats, "Statistiques", true, false, false);
				accessService.saveFeature(Feature.statsBilanPeriodique, "Bilan Périodique", true, true, true);
				
				
				// Permissions
                var features = featureRepos.findAll();
                // - Super_Admin
                for (Feature f : features) {
                    accessService.saveAccess(sa, f, true);
                }
			}
		} finally {
			logger.info(watch.prettyPrint());
			logger.info(">> APP INIT DATA - END");
		}
	}

}
