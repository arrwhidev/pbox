package com.arrwhi.pbox.msg.metadata;

/**
 * Created by arran on 29/07/17.
 */
public class MetaDataBuilder {

    private String to, from, hash;

    public MetaDataBuilder withTo(String to) {
        this.to = to;
        return this;
    }

    public MetaDataBuilder withFrom(String from) {
        this.from = from;
        return this;
    }

    public MetaDataBuilder withHash(String hash) {
        this.hash = hash;
        return this;
    }

    public MetaData build() {
        MetaData md = new MetaData();
        md.setHash(this.hash);
        md.setTo(this.to);
        md.setFrom(this.from);
        return md;
    }
}