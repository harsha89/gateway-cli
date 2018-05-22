package org.wso2.apimgt.gateway.codegen.service;

import org.wso2.apimgt.gateway.codegen.service.bean.API;

public interface APIService {

    API getAPI(String id, String token);
}
