import ballerina/http;

public function listPets (http:Request req) returns http:Response {
    //stub code - fill as necessary
    http:Response resp = new;
    string payload = "Sample listPets Response";
    resp.setTextPayload(payload);

	return resp;
}

public function postPets (http:Request req) returns http:Response {
    //stub code - fill as necessary
    http:Response resp = new;
    string payload = "Sample postPets Response";
    resp.setTextPayload(payload);

	return resp;
}

public function showPetById (http:Request req, string petId) returns http:Response {
    //stub code - fill as necessary
    http:Response resp = new;
    string payload = "Sample showPetById Response";
    resp.setTextPayload(payload);

	return resp;
}

public function getAction (http:Request req) returns http:Response {
    //stub code - fill as necessary
    http:Response resp = new;
    string payload = "Sample getAction Response";
    resp.setTextPayload(payload);

	return resp;
}

