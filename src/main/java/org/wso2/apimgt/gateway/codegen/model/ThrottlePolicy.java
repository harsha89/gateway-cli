/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.apimgt.gateway.codegen.model;

import org.wso2.apimgt.gateway.codegen.service.bean.policy.ApplicationThrottlePolicyDTO;
import org.wso2.apimgt.gateway.codegen.service.bean.policy.RequestCountLimitDTO;
import org.wso2.apimgt.gateway.codegen.service.bean.policy.SubscriptionThrottlePolicyDTO;
import org.wso2.apimgt.gateway.codegen.utils.GeneratorConstants;

public class ThrottlePolicy {

    private String policyType;
    private String policyKey;
    private String name;
    private int unitTime;
    private String timeUnit;
    private String srcPackage;
    private String modelPackage;
    private String funcName;
    private long count;

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnitTime() {
        return unitTime;
    }

    public void setUnitTime(int unitTime) {
        this.unitTime = unitTime;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getSrcPackage() {
        return srcPackage;
    }

    public void setSrcPackage(String srcPackage) {
        this.srcPackage = srcPackage;
    }

    public String getModelPackage() {
        return modelPackage;
    }

    public void setModelPackage(String modelPackage) {
        this.modelPackage = modelPackage;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getPolicyKey() {
        return policyKey;
    }

    public void setPolicyKey(String policyKey) {
        this.policyKey = policyKey;
    }

    public ThrottlePolicy buildContext(ApplicationThrottlePolicyDTO applicationPolicy) {
        this.policyType = GeneratorConstants.APPLICATION_POLICY_TYPE;
        this.name = applicationPolicy.getPolicyName();
        RequestCountLimitDTO requestCountLimitDTO = (RequestCountLimitDTO) applicationPolicy.getDefaultLimit();
        this.count = requestCountLimitDTO.getRequestCount();
        this.unitTime = requestCountLimitDTO.getUnitTime();
        this.timeUnit = requestCountLimitDTO.getTimeUnit();
        this.funcName = GeneratorConstants.APPLICATION_INIT_FUNC_PREFIX + applicationPolicy.getPolicyName()
                + GeneratorConstants.INIT_FUNC_SUFFIX;
        this.policyKey = GeneratorConstants.APPLICATION_KEY;
        return this;
    }

    public ThrottlePolicy buildContext(SubscriptionThrottlePolicyDTO applicationPolicy) {
        this.policyType = GeneratorConstants.SUBSCRIPTION_POLICY_TYPE;
        this.name = applicationPolicy.getPolicyName();
        RequestCountLimitDTO requestCountLimitDTO = (RequestCountLimitDTO) applicationPolicy.getDefaultLimit();
        this.count = requestCountLimitDTO.getRequestCount();
        this.unitTime = requestCountLimitDTO.getUnitTime();
        this.timeUnit = requestCountLimitDTO.getTimeUnit();
        this.funcName = GeneratorConstants.SUBSCRIPTION_INIT_FUNC_PREFIX + applicationPolicy.getPolicyName()
                + GeneratorConstants.INIT_FUNC_SUFFIX;
        this.policyKey = GeneratorConstants.SUBSCRIPTION_KEY;
        return this;
    }

    public ThrottlePolicy srcPackage(String srcPackage) {
        if (srcPackage != null) {
            this.srcPackage = srcPackage.replaceFirst("\\.", "/");
        }
        return this;
    }

    public ThrottlePolicy modelPackage(String modelPackage) {
        if (modelPackage != null) {
            this.modelPackage = modelPackage.replaceFirst("\\.", "/");
        }
        return this;
    }
}
