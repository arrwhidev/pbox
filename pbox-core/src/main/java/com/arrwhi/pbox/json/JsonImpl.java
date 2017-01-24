package com.arrwhi.pbox.json;

import com.google.gson.Gson;

// Encapsulating Gson. 
public class JsonImpl implements Json {

    private Gson gson;

    public JsonImpl() {
        gson = new Gson();
    }

    public String toJSON(Object o) {
        return gson.toJson(o);
    }

    public Object fromJSON(String s, Class clazz) {
        return gson.fromJson(s, clazz);
    }
}