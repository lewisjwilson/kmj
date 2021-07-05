package com.lewiswilson.kiminojisho.json;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sense {

    @SerializedName("english_definitions")
    @Expose
    private List<String> englishDefinitions = null;
    @SerializedName("parts_of_speech")
    @Expose
    private List<String> partsOfSpeech = null;
    @SerializedName("links")
    @Expose
    private List<Link> links = null;
    @SerializedName("tags")
    @Expose
    private List<String> tags = null;
    @SerializedName("restrictions")
    @Expose
    private List<Object> restrictions = null;
    @SerializedName("see_also")
    @Expose
    private List<String> seeAlso = null;
    @SerializedName("antonyms")
    @Expose
    private List<Object> antonyms = null;
    @SerializedName("source")
    @Expose
    private List<Object> source = null;
    @SerializedName("info")
    @Expose
    private List<String> info = null;
    @SerializedName("sentences")
    @Expose
    private List<Object> sentences = null;

    public List<String> getEnglishDefinitions() {
        return englishDefinitions;
    }

    public void setEnglishDefinitions(List<String> englishDefinitions) {
        this.englishDefinitions = englishDefinitions;
    }

    public List<String> getPartsOfSpeech() {
        return partsOfSpeech;
    }

    public void setPartsOfSpeech(List<String> partsOfSpeech) {
        this.partsOfSpeech = partsOfSpeech;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Object> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(List<Object> restrictions) {
        this.restrictions = restrictions;
    }

    public List<String> getSeeAlso() {
        return seeAlso;
    }

    public void setSeeAlso(List<String> seeAlso) {
        this.seeAlso = seeAlso;
    }

    public List<Object> getAntonyms() {
        return antonyms;
    }

    public void setAntonyms(List<Object> antonyms) {
        this.antonyms = antonyms;
    }

    public List<Object> getSource() {
        return source;
    }

    public void setSource(List<Object> source) {
        this.source = source;
    }

    public List<String> getInfo() {
        return info;
    }

    public void setInfo(List<String> info) {
        this.info = info;
    }

    public List<Object> getSentences() {
        return sentences;
    }

    public void setSentences(List<Object> sentences) {
        this.sentences = sentences;
    }

}