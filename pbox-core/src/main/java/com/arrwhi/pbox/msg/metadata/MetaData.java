package com.arrwhi.pbox.msg.metadata;

import org.apache.commons.codec.Charsets;

import com.google.gson.Gson;

/*
 * Contains various values which get serialized into JSON.
 */
public class MetaData {

    private static final Gson GSON;

    private String from;
    private String to;
    private String hash;

    static {
        GSON = new Gson();
    }

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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public byte[] toJsonBytes() {
        return MetaData.toJsonBytes(this);
    }

    public static MetaData fromJsonBytes(byte[] json) {
        String s = new String(json, Charsets.UTF_8);
        return GSON.fromJson(s, MetaData.class);
    }

    public static byte[] toJsonBytes(MetaData md) {
        String s = GSON.toJson(md);
        return s.getBytes(Charsets.UTF_8);
    }
}