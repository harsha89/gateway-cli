package org.wso2.apimgt.gateway.codegen.token;

public interface TokenManagement {

    String generateAccessToken(String username, char[] password, String clientId, char[] clientSecret);

    String generateClientIdAndSecret();
}
