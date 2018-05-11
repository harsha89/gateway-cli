package org.wso2.apimgt.gateway.codegen.config.bean;

public class Config {
    private ClientConfig clientConfig;
    private TokenConfig tokenConfig;

    public TokenConfig getTokenConfig() {
        return tokenConfig;
    }

    public void setTokenConfig(TokenConfig tokenConfig) {
        this.tokenConfig = tokenConfig;
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
}
