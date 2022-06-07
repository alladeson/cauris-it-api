package com.alladeson.caurisit.models.entities;

public enum Operation {
	/** Les opérations d'authentification **/
    USER_LOGIN(1, "Connexion"),
    USER_LOGOUT(-1, "Déconnexion"),

    /** Les opérations de gestion de stock **/
    // Catégorie des articles
    CATEGORIE_CREATE(Feature.gestStockCategorie + 1, "Création catégorie article"),
    CATEGORIE_UPDATE(Feature.gestStockCategorie + 2, "Mis à jour catégorie article"),
    CATEGORIE_DELETE(-(Feature.gestStockCategorie + 1), "Suppression catégorie article"),
    // Les articles
    ARTICLE_CREATE(Feature.gestStockArticle + 1, "Création article"),
    ARTICLE_UPDATE(Feature.gestStockArticle + 2, "Mis à jour article"),
    ARTICLE_DELETE(-(Feature.gestStockArticle + 1), "Suppression article"),

    /** Les opérations de facturation **/
    // Émission des factures de vente
    FACTURATION_CREATE(Feature.facturation + 1, "Création facture de vente"),
    FACTURATION_FA_CREATE(Feature.facturation + 2, "Création facture d'avoir"),
    FACTURATION_UPDATE(Feature.facturation + 3, "Mis à jour facture"),
    FACTURATION_VALIDATE(Feature.facturation + 4, "Validation facture de vente"),
    FACTURATION_FA_VALIDATE(Feature.facturation + 5, "Validation facture d'avoir"),
    FACTURATION_DETAIL_VALIDATE(Feature.facturation + 51, "Validation détail facture"),
    FACTURATION_DELETE(-(Feature.facturation + 1), "Suppression facture"),
    FACTURATION_DETAIL_DELETE(-(Feature.facturation + 11), "Suppression détail facture"),
    // Les clients
    CLIENT_CREATE(Feature.facturationClient + 1, "Création du client"),
    CLIENT_UPDATE(Feature.facturationClient + 2, "Mis à jour du client"),
    CLIENT_DELETE(-(Feature.facturationClient + 1), "Suppression du client"),

    /** Les operations de paramètres **/
    // Les opérations liées au données du système
    SYSTEM_CREATE(Feature.parametreDonneSysteme, "Création des données du système"),
    SYSTEM_UPDATE(Feature.parametreSysteme + 2, "Mis à jour des données du système"),
    SYSTEM_LOGO_UPDATE(Feature.parametreSysteme + 21, "Mis à jour du logo"),
    SYSTEM_DELETE(-(Feature.parametreSysteme + 1), "Suppression des données du système"),

    /** Les opérations de contrôle d'accès **/
    // Les opération liées aux utilisateurs
    USER_CREATE(Feature.accessCtrlUser + 1, "Création utilisateur"),
    USER_UPDATE(Feature.accessCtrlUser + 2, "Mis à jour utilisateur"),
    USER_DELETE(-(Feature.accessCtrlUser + 1), "Suppression utilisateur"),
    USER_ACTIVATE(Feature.accessCtrlUser + 5, "Activation du compte utilisateur"),
    USER_DEACTIVATE(Feature.accessCtrlUser + 6, "Désactivation du compte utilisateur"),
    USER_PASSWORD_CHANGE(Feature.accessCtrlUser + 7, "Changement de mot de passe utilisateur"),
    // Les opérations liées au groupes d'utilisateurs
    USER_GROUP_CREATE(Feature.accessCtrlUserGroup + 1, "Création d'un groupe d'utilisateur"),
    USER_GROUP_UPDATE(Feature.accessCtrlUserGroup + 2, "Mis à jour d'un groupe d'utilisateur"),
    USER_GROUP_DELETE(-(Feature.accessCtrlUserGroup + 1), "Suppression d'un groupe d'utilisateur"),
    // Les opérations de permission : Access
    ACCESS_CREATE(Feature.accessCtrlAccess + 1, "Ajout de permission"),
    ACCESS_UPDATE(Feature.accessCtrlAccess + 2, "Mise à jour de permission"),
    ACCESS_DELETE(-(Feature.accessCtrlAccess + 1), "Retrait de permission"),
    // Les opérations liées aux fonctionnalités
    FEATURE_CREATE(Feature.accessCtrlFeatures + 1, "Création d'une fonctionnalité"),
    FEATURE_UPDATE(Feature.accessCtrlFeatures + 2, "Mis à jour d'une fonctionnalité"),
    FEATURE_DELETE(-(Feature.accessCtrlFeatures + 1), "Suppression d'une fonctionnalité"),
	// Les opérations liées aux clés unique d'activation de l'application
	SERIALKEY_CREATE(Feature.accessSerialKey + 1, "Génération d'une clé de sécurité"),
	SERIALKEY_UPDATE(Feature.accessSerialKey + 2, "Mis à jour d'une clé de sécurité"),
	SERIALKEY_ACTIVATE(Feature.accessSerialKey + 3, "Activation d'une clé de sécurité"),
	SERIALKEY_DELETE(-(Feature.accessSerialKey + 1), "Suppression d'une clé de sécurité");
    

    private final int value;
    private final String desc;

    Operation(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int value() {
        return this.value;
    }

    public String desc() {
        return this.desc;
    }

}
