package com.alladeson.caurisit.models.paylaods;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportFacture {

    private String numero;
    private String client;
    private String type;
    private String typeRef;
    private String debut;
    private String fin;
    private String duree;
    private String prixUnitaire;
    private String montant;
    //private JRBeanCollectionDataSource clientDetailFacture;

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeRef() {
        return typeRef;
    }

    public void setTypeRef(String typeRef) {
        this.typeRef = typeRef;
    }

    public String getDebut() {
        return debut;
    }

    public void setDebut(String debut) {
        this.debut = debut;
    }

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

    public String getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(String prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public String getMontant() {
        return montant;
    }

    public void setMontant(String montant) {
        this.montant = montant;
    }

    /*public JRBeanCollectionDataSource getClientDetailFacture() {
        return clientDetailFacture;
    }

    public void setClientDetailFacture(JRBeanCollectionDataSource clientDetailFacture) {
        this.clientDetailFacture = clientDetailFacture;
    }

    public Map<String, Object> getDataSources() {
        Map<String,Object> dataSources = new HashMap<>();
        dataSources.put("clientDetailFacture", clientDetailFacture);

        return dataSources;
    }*/

}
