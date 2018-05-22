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

import io.swagger.models.ExternalDocs;
import io.swagger.models.Operation;
import io.swagger.models.parameters.Parameter;
import org.wso2.apimgt.gateway.codegen.exception.BallerinaServiceGenException;
import org.wso2.apimgt.gateway.codegen.service.bean.API;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Wraps the {@link Operation} from swagger models to provide iterable child models.
 *
 * @since 0.967.0
 */
public class BallerinaOperation implements BallerinaSwaggerObject<BallerinaOperation, Operation> {
    private List<String> tags;
    private String summary;
    private String description;
    private String resourceTier;
    private ExternalDocs externalDocs;
    private String operationId;
    private List<BallerinaParameter> parameters;
    private List<String> methods;

    // Not static since handlebars can't see static variables
    private final List<String> allMethods =
            Arrays.asList("HEAD", "OPTIONS", "PATCH", "DELETE", "POST", "PUT", "GET");

    @Override
    public BallerinaOperation buildContext(Operation operation, API api) throws BallerinaServiceGenException {
        if (operation == null) {
            return getDefaultValue();
        }

        // OperationId with spaces will cause trouble in ballerina code.
        // Replacing it with '_' so that we can identify there was a ' ' when doing bal -> swagger
        this.operationId = getTrimmedOperationId(operation.getOperationId());
        this.tags = operation.getTags();
        this.summary = operation.getSummary();
        this.description = operation.getDescription();
        this.externalDocs = operation.getExternalDocs();
        this.parameters = new ArrayList<>();
        this.methods = null;
        Map<String, Object> extension =  operation.getVendorExtensions();
        Object resourceTier = extension.get("x-throttling-tier");
        if (resourceTier != null) {
            this.resourceTier = resourceTier.toString();
        }

        if (operation.getParameters() != null) {
            for (Parameter parameter : operation.getParameters()) {
                this.parameters.add(new BallerinaParameter().buildContext(parameter, api));
            }
        }

        return this;
    }

    @Override
    public BallerinaOperation buildContext(Operation operation) throws BallerinaServiceGenException {
        return buildContext(operation, null);
    }

    /**
     * Build BallerinaOperation with user extension.
     * Complete BallerinaOperation object will not be built. Only selected
     * set of attributes are supported.
     *
     * @param xObj extension context object
     * @return BallerinaOperation built with extension details
     */
    public BallerinaOperation buildXContext(Object xObj) {
        LinkedHashMap extension = (LinkedHashMap) xObj;
        Object operationId = extension.get("operationId");
        Object tags = extension.get("tags");
        Object summary = extension.get("summary");
        Object description = extension.get("description");
        Object xMethodsObj = extension.get("x-METHODS");
        Object resourceTier = extension.get("x-throttling-tier");
        this.parameters = new ArrayList<>();

        if (operationId != null) {
            // OperationId with spaces will cause trouble in ballerina code.
            // Replacing it with '_' so that we can identify there was a ' ' when doing bal -> swagger
            this.operationId = getTrimmedOperationId(operationId.toString());
        }
        if (tags != null && tags instanceof ArrayList) {
            this.tags = (ArrayList<String>) tags;
        }
        if (summary != null) {
            this.summary = summary.toString();
        }
        if (description != null) {
            this.description = description.toString();
        }
        if (xMethodsObj != null && (xMethodsObj instanceof ArrayList)) {
            this.methods =  (ArrayList) xMethodsObj;
        }
        if (resourceTier != null) {
            this.resourceTier = resourceTier.toString();
        }

        return this;
    }

    private String getTrimmedOperationId (String operationId) {
        if (operationId == null) {
            return null;
        }

        return operationId.replaceAll(" ", "_");
    }

    @Override
    public BallerinaOperation getDefaultValue() {
        return new BallerinaOperation();
    }

    public List<String> getTags() {
        return tags;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public String getOperationId() {
        return operationId;
    }

    public List<BallerinaParameter> getParameters() {
        return parameters;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public List<String> getMethods() {
        return methods;
    }

    public List<String> getAllMethods() {
        return allMethods;
    }

    public ExternalDocs getExternalDocs() {
        return externalDocs;
    }

    public void setExternalDocs(ExternalDocs externalDocs) {
        this.externalDocs = externalDocs;
    }

    public String getResourceTier() {
        return resourceTier;
    }

    public void setResourceTier(String resourceTier) {
        this.resourceTier = resourceTier;
    }
}
