package org.wso2.apimgt.gateway.codegen.service;

import org.wso2.apimgt.gateway.codegen.service.bean.APIDTO;

public interface APIService {

    APIDTO getAPI(String id, String token);
}
