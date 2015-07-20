package com.example.garrisonthomas.onewordday;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("DailyWord")
public class DailyWord extends ParseObject {

    public String getWord() {

        return getString("word");

    }

    public void setWord(String word) {

        put("word", word);

    }

    @Override
    public String toString() {
        return getString("word");
    }
}
