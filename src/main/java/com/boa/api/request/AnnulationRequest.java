package com.boa.api.request;

public class AnnulationRequest {
    private String referenceTransfert, operation, country;

    public AnnulationRequest() {
    }

    public AnnulationRequest(String referenceTransfert, String operation, String country) {
        this.referenceTransfert = referenceTransfert;
        this.operation = operation;
        this.country = country;
    }

    public String getReferenceTransfert() {
        return this.referenceTransfert;
    }

    public void setReferenceTransfert(String referenceTransfert) {
        this.referenceTransfert = referenceTransfert;
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

    public AnnulationRequest referenceTransfert(String referenceTransfert) {
        this.referenceTransfert = referenceTransfert;
        return this;
    }

    public AnnulationRequest operation(String operation) {
        this.operation = operation;
        return this;
    }

    public AnnulationRequest country(String country) {
        this.country = country;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
            " referenceTransfert='" + getReferenceTransfert() + "'" +
            ", codeOperateur='" + getOperation() + "'" +
            ", country='" + getCountry() + "'" +
            "}";
    }

}