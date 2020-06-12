package com.boa.api.response;

import java.util.Map;

public class AuthorisationResponse extends GenericResponse {
    private Map<String, Object> data;

    public AuthorisationResponse() {
    }


    public AuthorisationResponse(Map<String,Object> data) {
        this.data = data;
    }

    public Map<String,Object> getData() {
        return this.data;
    }

    public void setData(Map<String,Object> data) {
        this.data = data;
    }

    public AuthorisationResponse data(Map<String,Object> data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
            " data='" + getData() + "'" +
            "}";
    }
    
    
}