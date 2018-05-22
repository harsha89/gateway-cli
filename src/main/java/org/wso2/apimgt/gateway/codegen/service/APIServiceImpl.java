package org.wso2.apimgt.gateway.codegen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.wso2.apimgt.gateway.codegen.service.bean.API;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class APIServiceImpl implements APIService{

    public API getAPI(String id, String token) {
        URL url;
        HttpsURLConnection urlConn = null;
        API api = null;
        //calling token endpoint
        try {
            String urlStr = "https://localhost:9443/api/am/publisher/v0.11/apis/" + id;
            url = new URL(urlStr);
            urlConn = (HttpsURLConnection) url.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("Authorization", "Bearer " + token);
            int responseCode = urlConn.getResponseCode();
            if (responseCode == 200) {
                ObjectMapper mapper = new ObjectMapper();
                String responseStr = getResponseString(urlConn.getInputStream());
                //create ObjectMapper instance
                System.out.println(responseStr);
                System.out.println("OOOOOOOOOOOOOOOOOOOOOOOO");
                //convert json string to object
                api = mapper.readValue(responseStr, API.class);
            } else {
                throw new RuntimeException("Error occurred while getting token. Status code: " + responseCode);
            }
        } catch (Exception e) {
            String msg = "Error while creating the new token for token regeneration.";
            throw new RuntimeException(msg, e);
        } finally {
            if (urlConn != null) {
                urlConn.disconnect();
            }
        }
        return api;
    }

    private static String getResponseString(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String file = "";
            String str;
            while ((str = buffer.readLine()) != null) {
                file += str;
            }
            return file;
        }
    }
}
