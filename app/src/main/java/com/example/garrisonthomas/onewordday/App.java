package com.example.garrisonthomas.onewordday;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(DailyWord.class);
        Parse.initialize(this, "ozuC7RKPh5GEdd5ewBhNAHsNiIMTdWquEvjWYuFf", "k1df0Zv0PuHlI1wBDlUVy24AqL1jYTxVpiGUJ0GQ");

    }
}
