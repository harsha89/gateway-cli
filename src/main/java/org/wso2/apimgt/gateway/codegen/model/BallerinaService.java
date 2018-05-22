/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.apimgt.gateway.codegen.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.ExternalDocs;
import io.swagger.models.Info;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;
import org.apache.commons.lang3.StringUtils;
import org.wso2.apimgt.gateway.codegen.exception.BallerinaServiceGenException;
import org.wso2.apimgt.gateway.codegen.service.bean.API;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Wrapper for {@link Swagger}.
 * <p>This class can be used to push additional context variables for handlebars</p>
 */
public class BallerinaService implements BallerinaSwaggerObject<BallerinaService, Swagger> {
    private String name;
    private API api;
    private EndpointConfig endpointConfig;
    private String srcPackage;
    private String modelPackage;
    private String qualifiedServiceName;
    private Info info = null;
    private ExternalDocs externalDocs = null;
    private List<BallerinaServer> servers = null;
    private Set<Map.Entry<String, String>> security = null;
    private List<Tag> tags = null;
    private Set<Map.Entry<String, BallerinaPath>> paths = null;

    /**
     * Build a {@link BallerinaService} object from a {@link Swagger} object.
     * All non iterable objects using handlebars library is converted into
     * supported iterable object types.
     *
     * @param swagger {@link Swagger} type object to be converted
     * @return Converted {@link BallerinaService} object
     * @throws BallerinaServiceGenException when OpenAPI to BallerinaService parsing failed
     */
    @Override
    public BallerinaService buildContext(Swagger swagger) throws BallerinaServiceGenException {
        this.info = swagger.getInfo();
        this.externalDocs = swagger.getExternalDocs();
        this.tags = swagger.getTags();
        setPaths(swagger);
        setServers(swagger);
        return this;
    }

    @Override
    public BallerinaService buildContext(Swagger definition, API api) throws BallerinaServiceGenException {
        this.name = trim(api.getName());
        this.api = api;
        String endpointConfig = api.getEndpointConfig();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(endpointConfig);
            EndpointConfig endpointConf = new EndpointConfig();
            this.qualifiedServiceName = trim(api.getName()) + "_" + replaceAllNonAlphaNumeric(api.getVersion());
            String endpointType = rootNode.path("endpoint_type").asText();
            endpointConf.setEndpointType(endpointType);

            if ("http".equalsIgnoreCase(endpointType) || "failover".equalsIgnoreCase(endpointType)) {
                JsonNode prodEndpointNode = rootNode.get("production_endpoints");
                Endpoint prod = new Endpoint();
                prod.setEndpointUrl(prodEndpointNode.get("url").asText());
                endpointConf.addProdEndpoint(prod);

                JsonNode sandEndpointNode = rootNode.get("sandbox_endpoints");
                Endpoint sandbox = new Endpoint();
                sandbox.setEndpointUrl(sandEndpointNode.get("url").asText());
                endpointConf.addSandEndpoint(sandbox);

                if ("failover".equalsIgnoreCase(endpointType)) {
                    JsonNode prodFailoverEndpointNode = rootNode.withArray("production_failovers");
                    Iterator<JsonNode> prodFailoverEndointIterator = prodFailoverEndpointNode.iterator();
                    while (prodFailoverEndointIterator.hasNext()) {
                        JsonNode node = prodFailoverEndointIterator.next();
                        Endpoint endpoint = new Endpoint();
                        endpoint.setEndpointUrl(node.get("url").asText());
                        endpointConf.addProdFailoverEndpoint(endpoint);
                    }

                    JsonNode sandFailoverEndpointNode = rootNode.withArray("sandbox_failovers");
                    Iterator<JsonNode> sandboxFailoverEndointIterator = sandFailoverEndpointNode.iterator();
                    while (sandboxFailoverEndointIterator.hasNext()) {
                        JsonNode node = sandboxFailoverEndointIterator.next();
                        Endpoint endpoint = new Endpoint();
                        endpoint.setEndpointUrl(node.get("url").asText());
                        endpointConf.addSandFailoverEndpoint(endpoint);
                    }
                }
            } else if ("load_balance".equalsIgnoreCase(endpointType)) {
                JsonNode prodEndoints = rootNode.withArray("production_endpoints");
                Iterator<JsonNode> prodEndointIterator = prodEndoints.iterator();
                while (prodEndointIterator.hasNext()) {
                    JsonNode node = prodEndointIterator.next();
                    Endpoint endpoint = new Endpoint();
                    endpoint.setEndpointUrl(node.get("url").asText());
                    endpointConf.addProdEndpoint(endpoint);
                }

                JsonNode sandboxEndpoints = rootNode.withArray("sandbox_endpoints");
                Iterator<JsonNode> sandboxEndointIterator = sandboxEndpoints.iterator();
                while (sandboxEndointIterator.hasNext()) {
                    JsonNode node = sandboxEndointIterator.next();
                    Endpoint endpoint = new Endpoint();
                    endpoint.setEndpointUrl(node.get("url").asText());
                    endpointConf.addSandEndpoint(endpoint);
                }
            }
            this.endpointConfig = endpointConf;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buildContext(definition);
    }

    @Override
    public BallerinaService getDefaultValue() {
        return null;
    }

    /**
     * Populate path models into iterable structure.
     * This method will also add an operationId to each operation,
     * if operationId not provided in swagger definition
     *
     * @param swagger {@code OpenAPI} definition object with schema definition
     * @throws BallerinaServiceGenException when context building fails
     */
    private void setPaths(Swagger swagger) throws BallerinaServiceGenException {
        if (swagger.getPaths() == null) {
            return;
        }

        this.paths = new LinkedHashSet<>();
        Map<String, Path> pathList = swagger.getPaths();
        for (Map.Entry<String, Path> path : pathList.entrySet()) {
            BallerinaPath balPath = new BallerinaPath().buildContext(path.getValue(), this.api);
            balPath.getOperations().forEach(operation -> {
                if (operation.getValue().getOperationId() == null) {
                    String pathName = path.getKey().substring(1); // need to drop '/' prefix from the key, ex:'/path'
                    String operationId = operation.getKey() + trim(StringUtils.capitalize(pathName));
                    operation.getValue().setOperationId(operationId);
                }
            });
            paths.add(new AbstractMap.SimpleEntry<>(path.getKey(), balPath));
        }
    }

    /**
     * Extract endpoint information from OpenAPI server list.
     * If no servers were found, default {@link BallerinaServer} will be set as the server
     *
     * @param swagger <code>OpenAPI</code> definition object with server details
     * @throws BallerinaServiceGenException on failure to parse {@code Server} list
     */
    private void setServers(Swagger swagger) throws BallerinaServiceGenException {
        this.servers = new ArrayList<>();
        BallerinaServer server = new BallerinaServer().getDefaultValue();
        this.servers.add(server);
    }

    public BallerinaService srcPackage(String srcPackage) {
        if (srcPackage != null) {
            this.srcPackage = srcPackage.replaceFirst("\\.", "/");
        }
        return this;
    }

    public BallerinaService modelPackage(String modelPackage) {
        if (modelPackage != null) {
            this.modelPackage = modelPackage.replaceFirst("\\.", "/");
        }
        return this;
    }

    public String getSrcPackage() {
        return srcPackage;
    }

    public String getModelPackage() {
        return modelPackage;
    }

    public Info getInfo() {
        return info;
    }

    public List<BallerinaServer> getServers() {
        return servers;
    }

    public Set<Map.Entry<String, String>> getSecurity() {
        return security;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public Set<Map.Entry<String, BallerinaPath>> getPaths() {
        return paths;
    }


    private String trim(String key) {
        if (key == null) {
            return null;
        }
        key = key.replaceAll(" ", "_");
        key = key.replaceAll("/", "_");
        key = key.replaceAll("\\{", "_");
        key = key.replaceAll("}", "_");
        return key;
    }

    private String replaceAllNonAlphaNumeric(String value) {
        return value.replaceAll("[^a-zA-Z0-9]+","_");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EndpointConfig getEndpointConfig() {
        return endpointConfig;
    }

    public void setEndpointConfig(EndpointConfig endpointConfig) {
        this.endpointConfig = endpointConfig;
    }

    public API getApi() {
        return api;
    }

    public void setApi(API api) {
        this.api = api;
    }

    public String getQualifiedServiceName() {
        return qualifiedServiceName;
    }

    public void setQualifiedServiceName(String qualifiedServiceName) {
        this.qualifiedServiceName = qualifiedServiceName;
    }
}
