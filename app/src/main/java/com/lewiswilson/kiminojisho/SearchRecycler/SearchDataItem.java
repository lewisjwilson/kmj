package com.lewiswilson.kiminojisho.SearchRecycler;

public class SearchDataItem {
    private String mKanji;
    private String mKana;
    private String mEnglish;

    public SearchDataItem(String kanji, String kana, String english){
        mKanji = kanji;
        mKana = kana;
        mEnglish = english;
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
}
