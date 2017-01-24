package com.arrwhi.pbox.json;

public interface Json {
    String toJSON(Object o);
    Object fromJSON(String s, Class clazz);
}