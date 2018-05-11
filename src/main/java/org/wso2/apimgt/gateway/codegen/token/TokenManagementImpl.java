package org.wso2.apimgt.gateway.codegen.token;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;

public class TokenManagementImpl implements TokenManagement {
    @Override
    public String generateAccessToken(String username, char[] password, String clientId, char[] clientSecret) {
        return null;
    }

    @Override
    public String generateClientIdAndSecret() {
        String applicationName = "Integration_Test_App";
        URL url;
        HttpURLConnection urlConn = null;
        try {
            //Create json payload for DCR endpoint
            JsonObject json = new JsonObject();
            json.addProperty("callbackUrl", "http://test.callback.lk/");
            json.addProperty("clientName", applicationName);
            json.addProperty("tokenScope", "Production");
            json.addProperty("owner", adminUsername+ '@' + tenantDomain);
            json.addProperty("grantType", "client_credentials");
            // Calling DCR endpoint
            String dcrEndpoint = "http://127.0.0.1:9763/client-registration/v0.12/register";
            url = new URL(dcrEndpoint);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Content-Type", "application/json");
            String clientEncoded = DatatypeConverter.printBase64Binary((adminUsername+ '@' + tenantDomain + ':' + adminPassword)
                    .getBytes(StandardCharsets.UTF_8));
            urlConn.setRequestProperty("Authorization", "Basic " + clientEncoded); //temp fix
            urlConn.getOutputStream().write((json.toString()).getBytes("UTF-8"));
            int responseCode = urlConn.getResponseCode();
            if (responseCode == 200) {  //If the DCR call is success
                String responseStr = getResponseString(urlConn.getInputStream());
                JsonParser parser = new JsonParser();
                JsonObject jObj = parser.parse(responseStr).getAsJsonObject();
                consumerKey = jObj.getAsJsonPrimitive("clientId").getAsString();
                consumerSecret = jObj.getAsJsonPrimitive("clientSecret").getAsString();
            } else { //If DCR call fails
                throw new RuntimeException("DCR call failed. Status code: " + responseCode);
            }
        } catch (IOException e) {
            String errorMsg = "Can not create OAuth application  : " + applicationName;
            throw new RuntimeException(errorMsg, e);
        } finally {
            if (urlConn != null) {
                urlConn.disconnect();
            }
        }
    }

    private void applySslSettings() {
        try {
            KeyManager[] keyManagers = null;
            TrustManager[] trustManagers = null;
            HostnameVerifier hostnameVerifier = null;
            if (!verifyingSsl) {
                trustAll = new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                    @Override
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                };
                SSLContext sslContext = SSLContext.getInstance("TLS");
                trustManagers = new TrustManager[]{ trustAll };
                hostnameVerifier = new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) { return true; }
                };
            } else if (sslCaCert != null) {
                char[] password = null; // Any password will work.
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(sslCaCert);
                if (certificates.isEmpty()) {
                    throw new IllegalArgumentException("expected non-empty set of trusted certificates");
                }
                KeyStore caKeyStore = newEmptyKeyStore(password);
                int index = 0;
                for (Certificate certificate : certificates) {
                    String certificateAlias = "ca" + Integer.toString(index++);
                    caKeyStore.setCertificateEntry(certificateAlias, certificate);
                }
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(caKeyStore);
                trustManagers = trustManagerFactory.getTrustManagers();
            }

            if (keyManagers != null || trustManagers != null) {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(keyManagers, trustManagers, new SecureRandom());
                httpClient.setSslSocketFactory(sslContext.getSocketFactory());
            } else {
                httpClient.setSslSocketFactory(null);
            }
            httpClient.setHostnameVerifier(hostnameVerifier);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
}
