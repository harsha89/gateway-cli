package org.wso2.apimgt.gateway.codegen.service;

import org.wso2.apimgt.gateway.codegen.service.bean.API;

import java.util.List;

public interface APIService {

    API getAPI(String id, String accessToken);

    List<API> getApis(String labelId, String accessToken);
}
