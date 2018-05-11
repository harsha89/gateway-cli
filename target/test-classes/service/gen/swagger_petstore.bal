import ballerina/log;
import ballerina/http;
import ballerina/swagger;

endpoint http:Listener ep0 { 
    host: "petstore.openapi.io",
    port: 80
};
endpoint http:Listener ep1 { 
    host: "petstore.swagger.io",
    port: 443
};

@swagger:ServiceInfo { 
    title: "Swagger Petstore",
    serviceVersion: "1.0.0",
    license: {name: "MIT", url: ""},
    tags: [
        {name: "pets", description: "Pets Tag", externalDocs: {}},
        {name: "list", description: "List Tag", externalDocs: {}}
    ],
    security: [
        {name: "petstore_auth", requirements: []},
        {name: "user_auth", requirements: []}
    ]
}
@http:ServiceConfig {
    basePath: "/v1"
}
service SwaggerPetstore bind ep0, ep1 {

    @swagger:ResourceInfo {
        summary: "List all pets",
        tags: ["pets","list"],
        description: "Show a list of pets in the system",
        parameters: [
            {
                name: "limit",
                inInfo: "query",
                description: "How many items to return at one time (max 100)",  
                allowEmptyValue: ""
            }
        ]
    }
    @http:ResourceConfig { 
        methods:["GET"],
        path:"/pets"
    }
    listPets (endpoint outboundEp, http:Request req) {
        http:Response res = listPets(req);
        outboundEp->respond(res) but { error e => log:printError("Error while responding", err = e) };
    }

    @swagger:ResourceInfo {
        summary: "Create a pet",
        tags: ["pets"]
    }
    @http:ResourceConfig { 
        methods:["POST"],
        path:"/pets"
    }
    postPets (endpoint outboundEp, http:Request req) {
        http:Response res = postPets(req);
        outboundEp->respond(res) but { error e => log:printError("Error while responding", err = e) };
    }

    @swagger:ResourceInfo {
        summary: "Info for a specific pet",
        tags: ["pets"],
        parameters: [
            {
                name: "petId",
                inInfo: "path",
                description: "The id of the pet to retrieve", 
                required: true, 
                allowEmptyValue: ""
            }
        ]
    }
    @http:ResourceConfig { 
        methods:["GET"],
        path:"/pets/{petId}"
    }
    showPetById (endpoint outboundEp, http:Request req, string petId) {
        http:Response res = showPetById(req, petId);
        outboundEp->respond(res) but { error e => log:printError("Error while responding", err = e) };
    }

    @swagger:ResourceInfo {
        summary: ""
    }
    @http:ResourceConfig { 
        methods:["HEAD", "OPTIONS", "PATCH", "DELETE", "POST", "PUT", "GET"],
        path:"/action"
    }
    getAction (endpoint outboundEp, http:Request req) {
        http:Response res = getAction(req);
        outboundEp->respond(res) but { error e => log:printError("Error while responding", err = e) };
    }

}
