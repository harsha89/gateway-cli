package org.wso2.apimgt.gateway.codegen.service;

import org.wso2.apimgt.gateway.codegen.service.bean.API;
import org.wso2.apimgt.gateway.codegen.service.bean.policy.ApplicationThrottlePolicyDTO;
import org.wso2.apimgt.gateway.codegen.service.bean.policy.SubscriptionThrottlePolicyDTO;

import java.util.List;

public interface APIService {

    API getAPI(String id, String accessToken);

    List<API> getApis(String labelId, String accessToken);

    List<ApplicationThrottlePolicyDTO> getApplicationPolicies(String token);

    List<SubscriptionThrottlePolicyDTO> getSubscriptionPolicies(String token);
}
