package com.boa.api.request;

public class AuthorisationRequest {

    private Double montant, mntFrais;
    private String referenceTransfert, numeroTransaction;
    private String compteEmetteur, disponible, valDisponible;
    private String operation, country;
    private String libelle, compteCrediteur, codAuto;

    public AuthorisationRequest() {
    }

    public AuthorisationRequest(Double montant, Double mntFrais, String referenceTransfert, String numeroTransaction, String compteEmetteur, String disponible, String valDisponible, String operation, String country, String libelle, String compteCrediteur, String codAuto) {
        this.montant = montant;
        this.mntFrais = mntFrais;
        this.referenceTransfert = referenceTransfert;
        this.numeroTransaction = numeroTransaction;
        this.compteEmetteur = compteEmetteur;
        this.disponible = disponible;
        this.valDisponible = valDisponible;
        this.operation = operation;
        this.country = country;
        this.libelle = libelle;
        this.compteCrediteur = compteCrediteur;
        this.codAuto = codAuto;
    }

    public Double getMontant() {
        return this.montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public Double getMntFrais() {
        return this.mntFrais;
    }

    public void setMntFrais(Double mntFrais) {
        this.mntFrais = mntFrais;
    }

    public String getReferenceTransfert() {
        return this.referenceTransfert;
    }

    public void setReferenceTransfert(String referenceTransfert) {
        this.referenceTransfert = referenceTransfert;
    }

    public String getNumeroTransaction() {
        return this.numeroTransaction;
    }

    public void setNumeroTransaction(String numeroTransaction) {
        this.numeroTransaction = numeroTransaction;
    }

    public String getCompteEmetteur() {
        return this.compteEmetteur;
    }

    public void setCompteEmetteur(String compteEmetteur) {
        this.compteEmetteur = compteEmetteur;
    }

    public String getDisponible() {
        return this.disponible;
    }

    public void setDisponible(String disponible) {
        this.disponible = disponible;
    }

    public String getValDisponible() {
        return this.valDisponible;
    }

    public void setValDisponible(String valDisponible) {
        this.valDisponible = valDisponible;
    }

    public String getOperation() {
        return this.operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getCompteCrediteur() {
        return this.compteCrediteur;
    }

    public void setCompteCrediteur(String compteCrediteur) {
        this.compteCrediteur = compteCrediteur;
    }

    public String getCodAuto() {
        return this.codAuto;
    }

    public void setCodAuto(String codAuto) {
        this.codAuto = codAuto;
    }

    public AuthorisationRequest montant(Double montant) {
        this.montant = montant;
        return this;
    }

    public AuthorisationRequest mntFrais(Double mntFrais) {
        this.mntFrais = mntFrais;
        return this;
    }

    public AuthorisationRequest referenceTransfert(String referenceTransfert) {
        this.referenceTransfert = referenceTransfert;
        return this;
    }

    public AuthorisationRequest numeroTransaction(String numeroTransaction) {
        this.numeroTransaction = numeroTransaction;
        return this;
    }

    public AuthorisationRequest compteEmetteur(String compteEmetteur) {
        this.compteEmetteur = compteEmetteur;
        return this;
    }

    public AuthorisationRequest disponible(String disponible) {
        this.disponible = disponible;
        return this;
    }

    public AuthorisationRequest valDisponible(String valDisponible) {
        this.valDisponible = valDisponible;
        return this;
    }

    public AuthorisationRequest operation(String operation) {
        this.operation = operation;
        return this;
    }

    public AuthorisationRequest country(String country) {
        this.country = country;
        return this;
    }

    public AuthorisationRequest libelle(String libelle) {
        this.libelle = libelle;
        return this;
    }

    public AuthorisationRequest compteCrediteur(String compteCrediteur) {
        this.compteCrediteur = compteCrediteur;
        return this;
    }

    public AuthorisationRequest codAuto(String codAuto) {
        this.codAuto = codAuto;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
            " montant='" + getMontant() + "'" +
            ", mntFrais='" + getMntFrais() + "'" +
            ", referenceTransfert='" + getReferenceTransfert() + "'" +
            ", numeroTransaction='" + getNumeroTransaction() + "'" +
            ", compteEmetteur='" + getCompteEmetteur() + "'" +
            ", disponible='" + getDisponible() + "'" +
            ", valDisponible='" + getValDisponible() + "'" +
            ", operation='" + getOperation() + "'" +
            ", country='" + getCountry() + "'" +
            ", libelle='" + getLibelle() + "'" +
            ", compteCrediteur='" + getCompteCrediteur() + "'" +
            ", codAuto='" + getCodAuto() + "'" +
            "}";
    }

}