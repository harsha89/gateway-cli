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

import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import org.wso2.apimgt.gateway.codegen.exception.BallerinaServiceGenException;

import java.util.AbstractMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Wraps the {@link PathItem} from swagger models to provide an iterable object model
 * for operations.
 *
 * @since 0.967.0
 */
public class BallerinaPath implements BallerinaSwaggerObject<BallerinaPath, Path> {
    private Set<Map.Entry<String, BallerinaOperation>> operations;

    public BallerinaPath() {
        this.operations = new LinkedHashSet<>();
    }

    @Override
    public BallerinaPath buildContext(Path item, Swagger swagger) throws BallerinaServiceGenException {
        Map.Entry<String, BallerinaOperation> entry;
        BallerinaOperation operation;

        if (item.getVendorExtensions() != null && item.getVendorExtensions().size() > 0) {
            for (Map.Entry<String, Object> xEntry: item.getVendorExtensions().entrySet()) {
                resolveExtension(xEntry);
            }
            return this;
        }

        // Swagger PathItem object doesn't provide a iterable structure for operations
        // Therefore we have to manually check if each http verb exists
        if (item.getGet() != null) {
            operation = new BallerinaOperation().buildContext(item.getGet(), swagger);
            entry = new AbstractMap.SimpleEntry<>("get", operation);
            operations.add(entry);
        }
        if (item.getPut() != null) {
            operation = new BallerinaOperation().buildContext(item.getPut(), swagger);
            entry = new AbstractMap.SimpleEntry<>("put", operation);
            operations.add(entry);
        }
        if (item.getPost() != null) {
            operation = new BallerinaOperation().buildContext(item.getPost(), swagger);
            entry = new AbstractMap.SimpleEntry<>("post", operation);
            operations.add(entry);
        }
        if (item.getDelete() != null) {
            operation = new BallerinaOperation().buildContext(item.getDelete(), swagger);
            entry = new AbstractMap.SimpleEntry<>("delete", operation);
            operations.add(entry);
        }
        if (item.getOptions() != null) {
            operation = new BallerinaOperation().buildContext(item.getOptions(), swagger);
            entry = new AbstractMap.SimpleEntry<>("options", operation);
            operations.add(entry);
        }
        if (item.getHead() != null) {
            operation = new BallerinaOperation().buildContext(item.getHead(), swagger);
            entry = new AbstractMap.SimpleEntry<>("head", operation);
            operations.add(entry);
        }
        if (item.getPatch() != null) {
            operation = new BallerinaOperation().buildContext(item.getPatch(), swagger);
            entry = new AbstractMap.SimpleEntry<>("patch", operation);
            operations.add(entry);
        }

        return this;
    }

    @Override
    public BallerinaPath buildContext(Path item) throws BallerinaServiceGenException {
        return buildContext(item, null);
    }

    private void resolveExtension(Map.Entry<String, Object> xEntry) {
        // currently we only support x-MULTI extension
        if ("x-MULTI".equals(xEntry.getKey())) {
            BallerinaOperation operation = new BallerinaOperation().buildXContext(xEntry.getValue());
            Map.Entry<String, BallerinaOperation> entry = new AbstractMap.SimpleEntry<>("multi", operation);
            operations.add(entry);
        }
    }

    @Override
    public BallerinaPath getDefaultValue() {
        return null;
    }

    public Set<Map.Entry<String, BallerinaOperation>> getOperations() {
        return operations;
    }
}
