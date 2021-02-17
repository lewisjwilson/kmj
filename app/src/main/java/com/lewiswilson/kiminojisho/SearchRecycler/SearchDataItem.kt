package com.lewiswilson.kiminojisho.SearchRecycler;

public class SearchDataItem {
    private String mKanji;
    private String mKana;
    private String mEnglish;
    private String mNotes;

    public SearchDataItem(String kanji, String kana, String english, String notes){
        mKanji = kanji;
        mKana = kana;
        mEnglish = english;
        mNotes = notes;
    }

    public String getKanji() {
        return mKanji;
    }

    public String getKana() {
        return mKana;
    }

    public String getEnglish() {
        return mEnglish;
    }

    public String getNotes() {
        return mNotes;
    }
}
