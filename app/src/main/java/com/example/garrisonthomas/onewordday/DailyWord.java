package com.example.garrisonthomas.onewordday;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("DailyWord")
public class DailyWord extends ParseObject {

    public String getWord() {

        return getString("word");

    }

    public int getMood() {

        return getInt("moodNumber");

    }

    public String getDate() {

        return getString("date");

    }

    public String getRelatedUsername() {

        return getString("relatedUsername");

    }

    public void setWord(String word) {

        put("word", word);

    }

    public void setMood(int mood) {

        put("moodNumber", mood);

    }

    public void setDate(String date) {

        put("date", date);

    }

    public void setRelatedUsername(String relatedUsername) {

        put("relatedUsername", relatedUsername);

    }

    @Override
    public String toString() {
        return getString("word");
    }
}
