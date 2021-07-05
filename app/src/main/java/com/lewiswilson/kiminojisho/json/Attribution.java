package com.lewiswilson.kiminojisho.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attribution {

    @SerializedName("jmdict")
    @Expose
    private boolean jmdict;
    @SerializedName("jmnedict")
    @Expose
    private boolean jmnedict;
    @SerializedName("dbpedia")
    @Expose
    private boolean dbpedia;

    public boolean isJmdict() {
        return jmdict;
    }

    public void setJmdict(boolean jmdict) {
        this.jmdict = jmdict;
    }

    public boolean isJmnedict() {
        return jmnedict;
    }

    public void setJmnedict(boolean jmnedict) {
        this.jmnedict = jmnedict;
    }

    public boolean isDbpedia() {
        return dbpedia;
    }

    public void setDbpedia(boolean dbpedia) {
        this.dbpedia = dbpedia;
    }

}