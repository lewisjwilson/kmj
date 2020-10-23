package com.lewiswilson.kiminojisho.JSON;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("is_common")
    @Expose
    private boolean isCommon;
    @SerializedName("tags")
    @Expose
    private List<String> tags = null;
    @SerializedName("jlpt")
    @Expose
    private List<String> jlpt = null;
    @SerializedName("japanese")
    @Expose
    private List<Japanese> japanese = null;
    @SerializedName("senses")
    @Expose
    private List<Sense> senses = null;
    @SerializedName("attribution")
    @Expose
    private Attribution attribution;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public boolean isIsCommon() {
        return isCommon;
    }

    public void setIsCommon(boolean isCommon) {
        this.isCommon = isCommon;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getJlpt() {
        return jlpt;
    }

    public void setJlpt(List<String> jlpt) {
        this.jlpt = jlpt;
    }

    public List<Japanese> getJapanese() {
        return japanese;
    }

    public void setJapanese(List<Japanese> japanese) {
        this.japanese = japanese;
    }

    public List<Sense> getSenses() {
        return senses;
    }

    public void setSenses(List<Sense> senses) {
        this.senses = senses;
    }

    public Attribution getAttribution() {
        return attribution;
    }

    public void setAttribution(Attribution attribution) {
        this.attribution = attribution;
    }

}
