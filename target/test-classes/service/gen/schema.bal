

public type Pet { 
    int id,
    string name,
    string tag,
    string _type,
};

public type Dog { 
    int id,
    string name,
    string tag,
    string _type,
    boolean bark,
};

public type Pets { 
    Pet[] petList,
};

public type Error { 
    int code,
    string message,
};