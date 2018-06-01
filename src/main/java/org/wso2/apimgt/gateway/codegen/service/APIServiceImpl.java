package org.wso2.apimgt.gateway.codegen.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.wso2.apimgt.gateway.codegen.service.bean.APIListDTO;
import org.wso2.apimgt.gateway.codegen.service.bean.Endpoint;
import org.wso2.apimgt.gateway.codegen.service.bean.EndpointConfig;
import org.wso2.apimgt.gateway.codegen.service.bean.ext.ExtendedAPI;
import org.wso2.apimgt.gateway.codegen.service.bean.policy.ApplicationThrottlePolicyDTO;
import org.wso2.apimgt.gateway.codegen.service.bean.policy.ApplicationThrottlePolicyListDTO;
import org.wso2.apimgt.gateway.codegen.service.bean.policy.SubscriptionThrottlePolicyDTO;
import org.wso2.apimgt.gateway.codegen.service.bean.policy.SubscriptionThrottlePolicyListDTO;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class APIServiceImpl implements APIService {
    
    @Override
    public List<ExtendedAPI> getAPIs(String labelName, String accessToken) {

        URL url;
        HttpsURLConnection urlConn = null;
        APIListDTO apiListDTO = null;
        //calling token endpoint
        try {
            String urlStr =
                    "https://localhost:9443/api/am/publisher/v0.12/apis?query=labelName:" + labelName + "&expand=true";
            url = new URL(urlStr);
            urlConn = (HttpsURLConnection) url.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("Authorization", "Bearer " + accessToken);
            int responseCode = urlConn.getResponseCode();
            if (responseCode == 200) {
                ObjectMapper mapper = new ObjectMapper();
                String responseStr = getResponseString(urlConn.getInputStream());
                //convert json string to object
                apiListDTO = mapper.readValue(responseStr, APIListDTO.class);
                for (ExtendedAPI api : apiListDTO.getList()) {
                    String endpointConfig = api.getEndpointConfig();
                    api.setEndpointConfigRepresentation(getEndpointConfig(endpointConfig)); 
                }
            } else {
                throw new RuntimeException("Error occurred while getting token. Status code: " + responseCode);
            }
        } catch (Exception e) {
            String msg = "Error while getting all APIs with label " + labelName;
            throw new RuntimeException(msg, e);
        } finally {
            if (urlConn != null) {
                urlConn.disconnect();
            }
        }
        return apiListDTO.getList();
    }
    

    private List<ExtendedAPI> getSampleAPIs() {
        String sample = "{\"count\":2,\"next\":\"\",\"previous\":\"\",\"list\":[{\"id\":\"e72ba3c2-aef5-4893-ad5b-271f9d1a5814\",\"name\":\"PizzaShackAPI\",\"description\":\"This is a simple APIDetailedDTO for Pizza Shack online pizza delivery store.\",\"context\":\"/pizzashack\",\"version\":\"1.0.0\",\"provider\":\"admin\",\"apiDefinition\":\"{\\\"paths\\\":{\\\"/order\\\":{\\\"post\\\":{\\\"x-auth-type\\\":\\\"Application & Application User\\\",\\\"x-throttling-tier\\\":\\\"Unlimited\\\",\\\"description\\\":\\\"Create a new Order\\\",\\\"parameters\\\":[{\\\"schema\\\":{\\\"$ref\\\":\\\"#/definitions/Order\\\"},\\\"description\\\":\\\"Order object that needs to be added\\\",\\\"name\\\":\\\"body\\\",\\\"required\\\":true,\\\"in\\\":\\\"body\\\"}],\\\"responses\\\":{\\\"201\\\":{\\\"headers\\\":{\\\"Location\\\":{\\\"description\\\":\\\"The URL of the newly created resource.\\\",\\\"type\\\":\\\"string\\\"},\\\"Content-Type\\\":{\\\"description\\\":\\\"The content type of the body.\\\",\\\"type\\\":\\\"string\\\"}},\\\"schema\\\":{\\\"$ref\\\":\\\"#/definitions/Order\\\"},\\\"description\\\":\\\"Created. Successful response with the newly created object as entity in the body. Location header contains URL of newly created entity.\\\"},\\\"400\\\":{\\\"schema\\\":{\\\"$ref\\\":\\\"#/definitions/Error\\\"},\\\"description\\\":\\\"Bad Request. Invalid request or validation error.\\\"},\\\"415\\\":{\\\"schema\\\":{\\\"$ref\\\":\\\"#/definitions/Error\\\"},\\\"description\\\":\\\"Unsupported Media Type. The entity of the request was in a not supported format.\\\"}},\\\"security\\\":[{\\\"pizzashack_auth\\\":[\\\"write:order\\\",\\\"read:order\\\"]}]}},\\\"/menu\\\":{\\\"get\\\":{\\\"x-auth-type\\\":\\\"Application & Application User\\\",\\\"x-throttling-tier\\\":\\\"Unlimited\\\",\\\"description\\\":\\\"Return a list of available menu items\\\",\\\"parameters\\\":[],\\\"responses\\\":{\\\"200\\\":{\\\"headers\\\":{},\\\"schema\\\":{\\\"items\\\":{\\\"$ref\\\":\\\"#/definitions/MenuItem\\\"},\\\"type\\\":\\\"array\\\"},\\\"description\\\":\\\"OK. List of APIs is returned.\\\"},\\\"304\\\":{\\\"description\\\":\\\"Not Modified. Empty body because the client has already the latest version of the requested resource.\\\"},\\\"406\\\":{\\\"schema\\\":{\\\"$ref\\\":\\\"#/definitions/Error\\\"},\\\"description\\\":\\\"Not Acceptable. The requested media type is not supported\\\"}},\\\"security\\\":[{\\\"pizzashack_auth\\\":[\\\"read:menu\\\"]}]}},\\\"/order/{orderId}\\\":{\\\"put\\\":{\\\"x-auth-type\\\":\\\"Application & Application User\\\",\\\"x-throttling-tier\\\":\\\"Unlimited\\\",\\\"description\\\":\\\"Update an existing Order\\\",\\\"parameters\\\":[{\\\"description\\\":\\\"Order Id\\\",\\\"name\\\":\\\"orderId\\\",\\\"format\\\":\\\"string\\\",\\\"type\\\":\\\"string\\\",\\\"required\\\":true,\\\"in\\\":\\\"path\\\"},{\\\"schema\\\":{\\\"$ref\\\":\\\"#/definitions/Order\\\"},\\\"description\\\":\\\"Order object that needs to be added\\\",\\\"name\\\":\\\"body\\\",\\\"required\\\":true,\\\"in\\\":\\\"body\\\"}],\\\"responses\\\":{\\\"200\\\":{\\\"headers\\\":{\\\"Location\\\":{\\\"description\\\":\\\"The URL of the newly created resource.\\\",\\\"type\\\":\\\"string\\\"},\\\"Content-Type\\\":{\\\"description\\\":\\\"The content type of the body.\\\",\\\"type\\\":\\\"string\\\"}},\\\"schema\\\":{\\\"$ref\\\":\\\"#/definitions/Order\\\"},\\\"description\\\":\\\"OK. Successful response with updated Order\\\"},\\\"400\\\":{\\\"schema\\\":{\\\"$ref\\\":\\\"#/definitions/Error\\\"},\\\"description\\\":\\\"Bad Request. Invalid request or validation error\\\"},\\\"404\\\":{\\\"schema\\\":{\\\"$ref\\\":\\\"#/definitions/Error\\\"},\\\"description\\\":\\\"Not Found. The resource to be updated does not exist.\\\"},\\\"412\\\":{\\\"schema\\\":{\\\"$ref\\\":\\\"#/definitions/Error\\\"},\\\"description\\\":\\\"Precondition Failed. The request has not been performed because one of the preconditions is not met.\\\"}},\\\"security\\\":[{\\\"pizzashack_auth\\\":[\\\"write:order\\\",\\\"read:order\\\"]}]},\\\"get\\\":{\\\"x-auth-type\\\":\\\"Application & Application User\\\",\\\"x-throttling-tier\\\":\\\"Unlimited\\\",\\\"description\\\":\\\"Get details of an Order\\\",\\\"parameters\\\":[{\\\"description\\\":\\\"Order Id\\\",\\\"name\\\":\\\"orderId\\\",\\\"format\\\":\\\"string\\\",\\\"type\\\":\\\"string\\\",\\\"required\\\":true,\\\"in\\\":\\\"path\\\"}],\\\"responses\\\":{\\\"200\\\":{\\\"schema\\\":{\\\"$ref\\\":\\\"#/definitions/Order\\\"},\\\"headers\\\":{},\\\"description\\\":\\\"OK Requested Order will be returned\\\"},\\\"304\\\":{\\\"description\\\":\\\"Not Modified. Empty body because the client has already the latest version of the requested resource.\\\"},\\\"404\\\":{\\\"schema\\\":{\\\"$ref\\\":\\\"#/definitions/Error\\\"},\\\"description\\\":\\\"Not Found. Requested APIDetailedDTO does not exist.\\\"},\\\"406\\\":{\\\"schema\\\":{\\\"$ref\\\":\\\"#/definitions/Error\\\"},\\\"description\\\":\\\"Not Acceptable. The requested media type is not supported\\\"}},\\\"security\\\":[{\\\"pizzashack_auth\\\":[\\\"write:order\\\", \\\"read:order\\\"]}]},\\\"delete\\\":{\\\"x-auth-type\\\":\\\"Application & Application User\\\",\\\"x-throttling-tier\\\":\\\"Unlimited\\\",\\\"description\\\":\\\"Delete an existing Order\\\",\\\"parameters\\\":[{\\\"description\\\":\\\"Order Id\\\",\\\"name\\\":\\\"orderId\\\",\\\"format\\\":\\\"string\\\",\\\"type\\\":\\\"string\\\",\\\"required\\\":true,\\\"in\\\":\\\"path\\\"}],\\\"responses\\\":{\\\"200\\\":{\\\"description\\\":\\\"OK. Resource successfully deleted.\\\"},\\\"404\\\":{\\\"schema\\\":{\\\"$ref\\\":\\\"#/definitions/Error\\\"},\\\"description\\\":\\\"Not Found. Resource to be deleted does not exist.\\\"},\\\"412\\\":{\\\"schema\\\":{\\\"$ref\\\":\\\"#/definitions/Error\\\"},\\\"description\\\":\\\"Precondition Failed. The request has not been performed because one of the preconditions is not met.\\\"}},\\\"security\\\":[{\\\"pizzashack_auth\\\":[\\\"write:order\\\",\\\"read:order\\\"]}]}}},\\\"schemes\\\":[\\\"https\\\"],\\\"produces\\\":[\\\"application/json\\\"],\\\"swagger\\\":\\\"2.0\\\", \\\"securityDefinitions\\\":{\\\"pizzashack_auth\\\":{\\\"type\\\":\\\"oauth2\\\",\\\"authorizationUrl\\\": \\\"http://wso2.swagger.io/api/oauth/dialog\\\",\\\"flow\\\": \\\"implicit\\\", \\\"scopes\\\":{\\\"write:order\\\": \\\"modify order in your account\\\",\\\"read:order\\\":\\\"read your order\\\", \\\"read:menu\\\": \\\"read your menu\\\"}}},\\\"definitions\\\":{\\\"ErrorListItem\\\":{\\\"title\\\":\\\"Description of individual errors that may have occored during a request.\\\",\\\"properties\\\":{\\\"message\\\":{\\\"description\\\":\\\"Description about individual errors occored\\\",\\\"type\\\":\\\"string\\\"},\\\"code\\\":{\\\"format\\\":\\\"int64\\\",\\\"type\\\":\\\"integer\\\"}},\\\"required\\\":[\\\"code\\\",\\\"message\\\"]},\\\"MenuItem\\\":{\\\"title\\\":\\\"Pizza menu Item\\\",\\\"properties\\\":{\\\"price\\\":{\\\"type\\\":\\\"string\\\"},\\\"description\\\":{\\\"type\\\":\\\"string\\\"},\\\"name\\\":{\\\"type\\\":\\\"string\\\"},\\\"image\\\":{\\\"type\\\":\\\"string\\\"}},\\\"required\\\":[\\\"name\\\"]},\\\"Order\\\":{\\\"title\\\":\\\"Pizza Order\\\",\\\"properties\\\":{\\\"customerName\\\":{\\\"type\\\":\\\"string\\\"},\\\"delivered\\\":{\\\"type\\\":\\\"boolean\\\"},\\\"address\\\":{\\\"type\\\":\\\"string\\\"},\\\"pizzaType\\\":{\\\"type\\\":\\\"string\\\"},\\\"creditCardNumber\\\":{\\\"type\\\":\\\"string\\\"},\\\"quantity\\\":{\\\"type\\\":\\\"number\\\"},\\\"orderId\\\":{\\\"type\\\":\\\"string\\\"}},\\\"required\\\":[\\\"orderId\\\"]},\\\"Error\\\":{\\\"title\\\":\\\"Error object returned with 4XX HTTP status\\\",\\\"peroperties\\\":{\\\"message\\\":{\\\"description\\\":\\\"Error message.\\\",\\\"type\\\":\\\"string\\\"},\\\"error\\\":{\\\"items\\\":{\\\"$ref\\\":\\\"#/definitions/ErrorListItem\\\"},\\\"description\\\":\\\"If there are more than one error list them out. Ex. list out validation errors by each field.\\\",\\\"type\\\":\\\"array\\\"},\\\"description\\\":{\\\"description\\\":\\\"A detail description about the error message.\\\",\\\"type\\\":\\\"string\\\"},\\\"code\\\":{\\\"format\\\":\\\"int64\\\",\\\"type\\\":\\\"integer\\\"},\\\"moreInfo\\\":{\\\"description\\\":\\\"Preferably an url with more details about the error.\\\",\\\"type\\\":\\\"string\\\"}},\\\"required\\\":[\\\"code\\\",\\\"message\\\"]}},\\\"consumes\\\":[\\\"application/json\\\"],\\\"info\\\":{\\\"title\\\":\\\"PizzaShackAPI\\\",\\\"description\\\":\\\"This is a RESTFul APIDetailedDTO for Pizza Shack online pizza delivery store.\\\\n\\\",\\\"license\\\":{\\\"name\\\":\\\"Apache 2.0\\\",\\\"url\\\":\\\"http://www.apache.org/licenses/LICENSE-2.0.html\\\"},\\\"contact\\\":{\\\"email\\\":\\\"architecture@pizzashack.com\\\",\\\"name\\\":\\\"John Doe\\\",\\\"url\\\":\\\"http://www.pizzashack.com\\\"},\\\"version\\\":\\\"1.0.0\\\"}}\",\"wsdlUri\":null,\"status\":\"PUBLISHED\",\"responseCaching\":\"Disabled\",\"cacheTimeout\":300,\"destinationStatsEnabled\":null,\"isDefaultVersion\":false,\"type\":\"HTTP\",\"transport\":[\"http\",\"https\"],\"tags\":[\"pizza\"],\"tiers\":[\"Gold\"],\"apiLevelPolicy\":null,\"maxTps\":null,\"thumbnailUri\":\"/apis/e72ba3c2-aef5-4893-ad5b-271f9d1a5814/thumbnail\",\"visibility\":\"PUBLIC\",\"visibleRoles\":[],\"visibleTenants\":[],\"endpointConfig\":\"{\\\"production_endpoints\\\": {\\\"url\\\": \\\"https://localhost:9443/am/sample/pizzashack/v1/api/\\\",\\\"config\\\": null},\\\"sandbox_endpoints\\\": {\\\"url\\\": \\\"https://localhost:9443/am/sample/pizzashack/v1/api/\\\",\\\"config\\\": null},\\\"endpoint_type\\\": \\\"http\\\" }\",\"endpointSecurity\":null,\"gatewayEnvironments\":\"Production and Sandbox\",\"sequences\":[],\"subscriptionAvailability\":null,\"subscriptionAvailableTenants\":[],\"additionalProperties\":{},\"accessControl\":\"NONE\",\"accessControlRoles\":[],\"businessInformation\":{\"technicalOwnerEmail\":\"architecture@pizzashack.com\",\"businessOwnerEmail\":\"marketing@pizzashack.com\",\"businessOwner\":\"Jane Roe\",\"technicalOwner\":\"John Doe\"},\"corsConfiguration\":{\"accessControlAllowOrigins\":[\"*\"],\"accessControlAllowCredentials\":false,\"corsConfigurationEnabled\":false,\"accessControlAllowHeaders\":[\"authorization\",\"Access-Control-Allow-Origin\",\"Content-Type\",\"SOAPAction\"],\"accessControlAllowMethods\":[\"GET\",\"PUT\",\"POST\",\"DELETE\",\"PATCH\",\"OPTIONS\"]}},{\"id\":\"15b57c63-b1f5-4273-9d52-9d2671831c21\",\"name\":\"Test1\",\"description\":null,\"context\":\"/test1\",\"version\":\"1.0.0\",\"provider\":\"admin\",\"apiDefinition\":\"{\\\"swagger\\\":\\\"2.0\\\",\\\"paths\\\":{\\\"/res\\\":{\\\"get\\\":{\\\"responses\\\":{\\\"200\\\":{\\\"description\\\":\\\"\\\"}},\\\"x-auth-type\\\":\\\"Application & Application User\\\",\\\"x-throttling-tier\\\":\\\"Unlimited\\\"}}},\\\"info\\\":{\\\"title\\\":\\\"Test1\\\",\\\"version\\\":\\\"1.0.0\\\"}}\",\"wsdlUri\":null,\"status\":\"PROTOTYPED\",\"responseCaching\":\"Disabled\",\"cacheTimeout\":300,\"destinationStatsEnabled\":null,\"isDefaultVersion\":false,\"type\":\"HTTP\",\"transport\":[\"http\",\"https\"],\"tags\":[],\"tiers\":[\"Gold\"],\"apiLevelPolicy\":null,\"maxTps\":null,\"thumbnailUri\":null,\"visibility\":\"PUBLIC\",\"visibleRoles\":[],\"visibleTenants\":[],\"endpointConfig\":\"{\\\"production_endpoints\\\":{\\\"url\\\":\\\"http://www.mocky.io/v2/5a53349f30000075161ebe45\\\",\\\"config\\\":null,\\\"template_not_supported\\\":false},\\\"endpoint_type\\\":\\\"http\\\"}\",\"endpointSecurity\":null,\"gatewayEnvironments\":\"Production and Sandbox\",\"sequences\":[],\"subscriptionAvailability\":null,\"subscriptionAvailableTenants\":[],\"additionalProperties\":{},\"accessControl\":\"NONE\",\"accessControlRoles\":[],\"businessInformation\":{\"technicalOwnerEmail\":null,\"businessOwnerEmail\":null,\"businessOwner\":null,\"technicalOwner\":null},\"corsConfiguration\":{\"accessControlAllowOrigins\":[\"*\"],\"accessControlAllowCredentials\":false,\"corsConfigurationEnabled\":false,\"accessControlAllowHeaders\":[\"authorization\",\"Access-Control-Allow-Origin\",\"Content-Type\",\"SOAPAction\"],\"accessControlAllowMethods\":[\"GET\",\"PUT\",\"POST\",\"DELETE\",\"PATCH\",\"OPTIONS\"]}}],\"pagination\":{\"total\":2,\"offset\":0,\"limit\":25}}";
        ObjectMapper mapper = new ObjectMapper();
        //create ObjectMapper instance
        //convert json string to object
        try {
            APIListDTO apiList = mapper.readValue(sample, APIListDTO.class);
            String endpointConfig = "";
            for (ExtendedAPI api : apiList.getList()) {
                endpointConfig = api.getEndpointConfig();
                api.setEndpointConfigRepresentation(getEndpointConfig(endpointConfig));
            }
            return apiList.getList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ApplicationThrottlePolicyDTO> getApplicationPolicies(String token) {
        URL url;
        HttpsURLConnection urlConn = null;
        ApplicationThrottlePolicyListDTO appsList;
        List<ApplicationThrottlePolicyDTO> filteredPolicyDTOS = new ArrayList<>();
        //calling token endpoint
        try {
            String urlStr = "https://localhost:9443/api/am/admin/v0.12/throttling/policies/application";
            url = new URL(urlStr);
            urlConn = (HttpsURLConnection) url.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("Authorization", "Bearer " + token);
            int responseCode = urlConn.getResponseCode();
            if (responseCode == 200) {
                ObjectMapper mapper = new ObjectMapper();
                String responseStr = getResponseString(urlConn.getInputStream());
                System.out.println(responseStr);
                //convert json string to object
                appsList = mapper.readValue(responseStr, ApplicationThrottlePolicyListDTO.class);
                List<ApplicationThrottlePolicyDTO> policyDTOS = appsList.getList();
                for (ApplicationThrottlePolicyDTO policyDTO : policyDTOS ) {
                    if(!"Unlimited".equalsIgnoreCase(policyDTO.getPolicyName())){
                        filteredPolicyDTOS.add(policyDTO);
                    }
                }
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
        return filteredPolicyDTOS;
    }

    @Override
    public List<SubscriptionThrottlePolicyDTO> getSubscriptionPolicies(String token) {
        URL url;
        HttpsURLConnection urlConn = null;
        SubscriptionThrottlePolicyListDTO subsList;
        List<SubscriptionThrottlePolicyDTO> filteredPolicyDTOS = new ArrayList<>();
        //calling token endpoint
        try {
            String urlStr = "https://localhost:9443/api/am/admin/v0.12/throttling/policies/subscription";
            url = new URL(urlStr);
            urlConn = (HttpsURLConnection) url.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("Authorization", "Bearer " + token);
            int responseCode = urlConn.getResponseCode();
            if (responseCode == 200) {
                ObjectMapper mapper = new ObjectMapper();
                String responseStr = getResponseString(urlConn.getInputStream());
                System.out.println(responseStr);
                //convert json string to object
                subsList = mapper.readValue(responseStr, SubscriptionThrottlePolicyListDTO.class);
                List<SubscriptionThrottlePolicyDTO> policyDTOS = subsList.getList();
                for (SubscriptionThrottlePolicyDTO policyDTO : policyDTOS ) {
                    if(!"Unlimited".equalsIgnoreCase(policyDTO.getPolicyName())){
                        filteredPolicyDTOS.add(policyDTO);
                    }
                }
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
        return filteredPolicyDTOS;
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

    private EndpointConfig getEndpointConfig(String endpointConfig) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        EndpointConfig endpointConf = new EndpointConfig();
        rootNode = mapper.readTree(endpointConfig);
        String endpointType = rootNode.path("endpoint_type").asText();
        endpointConf.setEndpointType(endpointType);

        if ("http".equalsIgnoreCase(endpointType) || "failover".equalsIgnoreCase(endpointType)) {
            JsonNode prodEndpointNode = rootNode.get("production_endpoints");
            if (prodEndpointNode != null) {
                Endpoint prod = new Endpoint();
                prod.setEndpointUrl(prodEndpointNode.get("url").asText());
                endpointConf.addProdEndpoint(prod);
            }

            JsonNode sandEndpointNode = rootNode.get("sandbox_endpoints");
            if (sandEndpointNode != null) {
                Endpoint sandbox = new Endpoint();
                sandbox.setEndpointUrl(sandEndpointNode.get("url").asText());
                endpointConf.addSandEndpoint(sandbox);
            }

            if ("failover".equalsIgnoreCase(endpointType)) {
                JsonNode prodFailoverEndpointNode = rootNode.withArray("production_failovers");
                if (prodFailoverEndpointNode != null) {
                    Iterator<JsonNode> prodFailoverEndointIterator = prodFailoverEndpointNode.iterator();
                    while (prodFailoverEndointIterator.hasNext()) {
                        JsonNode node = prodFailoverEndointIterator.next();
                        Endpoint endpoint = new Endpoint();
                        endpoint.setEndpointUrl(node.get("url").asText());
                        endpointConf.addProdFailoverEndpoint(endpoint);
                    }
                }

                JsonNode sandFailoverEndpointNode = rootNode.withArray("sandbox_failovers");
                if (sandFailoverEndpointNode != null) {
                    Iterator<JsonNode> sandboxFailoverEndointIterator = sandFailoverEndpointNode.iterator();
                    while (sandboxFailoverEndointIterator.hasNext()) {
                        JsonNode node = sandboxFailoverEndointIterator.next();
                        Endpoint endpoint = new Endpoint();
                        endpoint.setEndpointUrl(node.get("url").asText());
                        endpointConf.addSandFailoverEndpoint(endpoint);
                    }
                }
            }
        } else if ("load_balance".equalsIgnoreCase(endpointType)) {
            JsonNode prodEndoints = rootNode.withArray("production_endpoints");
            if (prodEndoints != null) {
                Iterator<JsonNode> prodEndointIterator = prodEndoints.iterator();
                while (prodEndointIterator.hasNext()) {
                    JsonNode node = prodEndointIterator.next();
                    Endpoint endpoint = new Endpoint();
                    endpoint.setEndpointUrl(node.get("url").asText());
                    endpointConf.addProdEndpoint(endpoint);
                }
            }

            JsonNode sandboxEndpoints = rootNode.withArray("sandbox_endpoints");
            if (sandboxEndpoints != null) {
                Iterator<JsonNode> sandboxEndointIterator = sandboxEndpoints.iterator();
                while (sandboxEndointIterator.hasNext()) {
                    JsonNode node = sandboxEndointIterator.next();
                    Endpoint endpoint = new Endpoint();
                    endpoint.setEndpointUrl(node.get("url").asText());
                    endpointConf.addSandEndpoint(endpoint);
                }
            }
        }
        return endpointConf;
    }
}
