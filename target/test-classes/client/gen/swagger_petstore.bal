import ballerina/io;
import ballerina/mime;
import ballerina/http;

//=====================================
//============Client Config============
//=====================================
public type SwaggerPetstoreClientConfig {
    string serviceUrl,
};

//=======================================
//============Client Endpoint============
//=======================================
public type SwaggerPetstoreClientEp object {
    public {
        http:Client client;
        SwaggerPetstoreClientConfig config;
    }

    public function init(SwaggerPetstoreClientConfig config) {
        endpoint http:Client httpEp {
            url: config.serviceUrl
        };

        self.client = httpEp;
        self.config = config;
    }

    public function getCallerActions() returns (SwaggerPetstoreClient) {
        return new SwaggerPetstoreClient(self);
    }
};

//==============================
//============Client============
//==============================
public type SwaggerPetstoreClient object {
    public {
        SwaggerPetstoreClientEp clientEp;
    }

    new (clientEp) {}

    public function listPets() returns http:Response | error {
        endpoint http:Client ep = self.clientEp.client;
        http:Request request = new;

        //Create the required request as needed
        return check ep->get("/pets", request = request);
    }
    
    public function postPets() returns http:Response | error {
        endpoint http:Client ep = self.clientEp.client;
        http:Request request = new;

        //Create the required request as needed
        return check ep->post("/pets", request = request);
    }
    
    public function showPetById(string petId) returns http:Response | error {
        endpoint http:Client ep = self.clientEp.client;
        http:Request request = new;

        //Create the required request as needed
        return check ep->get("/pets/{petId}", request = request);
    }
    
    public function headgetAction() returns http:Response | error {
        endpoint http:Client ep = self.clientEp.client;
        http:Request request = new;

        //Create the required request as needed
        return check ep->head("/action", request = request);
    }
    
    public function optionsgetAction() returns http:Response | error {
        endpoint http:Client ep = self.clientEp.client;
        http:Request request = new;

        //Create the required request as needed
        return check ep->options("/action", request = request);
    }
    
    public function patchgetAction() returns http:Response | error {
        endpoint http:Client ep = self.clientEp.client;
        http:Request request = new;

        //Create the required request as needed
        return check ep->patch("/action", request = request);
    }
    
    public function deletegetAction() returns http:Response | error {
        endpoint http:Client ep = self.clientEp.client;
        http:Request request = new;

        //Create the required request as needed
        return check ep->delete("/action", request = request);
    }
    
    public function postgetAction() returns http:Response | error {
        endpoint http:Client ep = self.clientEp.client;
        http:Request request = new;

        //Create the required request as needed
        return check ep->post("/action", request = request);
    }
    
    public function putgetAction() returns http:Response | error {
        endpoint http:Client ep = self.clientEp.client;
        http:Request request = new;

        //Create the required request as needed
        return check ep->put("/action", request = request);
    }
    
    public function getgetAction() returns http:Response | error {
        endpoint http:Client ep = self.clientEp.client;
        http:Request request = new;

        //Create the required request as needed
        return check ep->get("/action", request = request);
    }
    
};
