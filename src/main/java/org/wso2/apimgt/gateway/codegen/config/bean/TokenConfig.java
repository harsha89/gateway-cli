package org.wso2.apimgt.gateway.codegen.config.bean;

public class TokenConfig {

    private String publisherEndpoint;
    private String registrationEndpoint;
    private String tokenEndpoint;
    private String username;
    private String clientId;
    private String clientSecret;

    public String getPublisherEndpoint() {
        return publisherEndpoint;
    }

    public void setPublisherEndpoint(String publisherEndpoint) {
        this.publisherEndpoint = publisherEndpoint;
    }

    public String getRegistrationEndpoint() {
        return registrationEndpoint;
    }

    public void setRegistrationEndpoint(String registrationEndpoint) {
        this.registrationEndpoint = registrationEndpoint;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
