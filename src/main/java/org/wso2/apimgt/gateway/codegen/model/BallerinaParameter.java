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

import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import org.wso2.apimgt.gateway.codegen.exception.BallerinaServiceGenException;
import org.wso2.apimgt.gateway.codegen.service.bean.API;

/**
 * Wraps the {@link Parameter} from swagger models for easier templating.
 *
 * @since 0.967.0
 */
public class BallerinaParameter implements BallerinaSwaggerObject<BallerinaParameter, Parameter> {
    private String name;
    private String in;
    private String description;
    private Boolean required;
    private Boolean allowEmptyValue;

    @Override
    public BallerinaParameter buildContext(Parameter parameter) throws BallerinaServiceGenException {
        return buildContext(parameter, null);
    }

    @Override
    public BallerinaParameter buildContext(Parameter parameter, API api) throws BallerinaServiceGenException {
        this.name = parameter.getName();
        this.in = parameter.getIn();
        this.description = parameter.getDescription();
        this.required = parameter.getRequired();
        this.allowEmptyValue = parameter.getAllowEmptyValue();
        return this;
    }

    @Override
    public BallerinaParameter getDefaultValue() {
        return null;
    }

    public String getName() {
        return name;
    }

    public String getIn() {
        return in;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getRequired() {
        return required;
    }

    public Boolean getAllowEmptyValue() {
        return allowEmptyValue;
    }

    public void setAllowEmptyValue(Boolean allowEmptyValue) {
        this.allowEmptyValue = allowEmptyValue;
    }
}
