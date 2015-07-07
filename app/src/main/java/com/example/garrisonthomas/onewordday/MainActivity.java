package com.example.garrisonthomas.onewordday;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        ParseObject.registerSubclass(DailyWord.class);

        Parse.initialize(this, "ozuC7RKPh5GEdd5ewBhNAHsNiIMTdWquEvjWYuFf", "k1df0Zv0PuHlI1wBDlUVy24AqL1jYTxVpiGUJ0GQ");

        final EditText word = (EditText) findViewById(R.id.enter_word);
        Button submit = (Button) findViewById(R.id.btn_submit);
        Button viewResults = (Button) findViewById(R.id.btn_view_results);
        final TextView tvResult = (TextView) findViewById(R.id.tv_result);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseObject dailyWord = new DailyWord();
                dailyWord.put("word", word.getText().toString());
                dailyWord.saveInBackground();

            }
        });

        viewResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseQuery<DailyWord> query = new ParseQuery<>("DailyWord");

                query.findInBackground(new FindCallback<DailyWord>() {
                    public void done(List<DailyWord> objects, com.parse.ParseException e) {
                        ArrayList<DailyWord> wordArray = new ArrayList<>();
                        if (e == null) {
                            for (DailyWord newWord : objects) {

                                DailyWord dWord = new DailyWord();
                                dWord.setWord(newWord.getWord());
                                wordArray.add(dWord);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Error "+e, Toast.LENGTH_SHORT).show();
                        }
                        tvResult.setText(wordArray.toString());
                    }
                });


            }




});
}
}