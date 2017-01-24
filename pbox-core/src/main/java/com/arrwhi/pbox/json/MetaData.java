package com.arrwhi.pbox.json;

import org.apache.commons.codec.Charsets;

import com.google.gson.Gson;

public class MetaData {

    private String from;
    private String to;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public static MetaData fromJsonBytes(byte[] json) {
        String s = new String(json, Charsets.UTF_8);
        return new Gson().fromJson(s, MetaData.class);
    }
    
    public static byte[] toJsonBytes(MetaData md) {
        String s = new Gson().toJson(md);
        return s.getBytes(Charsets.UTF_8);
    }
}