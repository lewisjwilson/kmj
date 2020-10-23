package com.lewiswilson.kiminojisho.JSON;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Japanese {

    @SerializedName("word")
    @Expose
    private String word;
    @SerializedName("reading")
    @Expose
    private String reading;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getReading() {
        return reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

}