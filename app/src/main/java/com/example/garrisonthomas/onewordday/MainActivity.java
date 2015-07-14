package com.example.garrisonthomas.onewordday;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {

    EditText word;
    Button submit, viewResults, logout;
    TextView tvResult;
    ProgressBar mainPBar;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences(getString(R.string.application_id), Context.MODE_PRIVATE);
        submit = (Button) findViewById(R.id.btn_submit);
        logout = (Button) findViewById(R.id.btn_logout);
        viewResults = (Button) findViewById(R.id.btn_view_results);
        tvResult = (TextView) findViewById(R.id.tv_result);
        mainPBar = (ProgressBar) findViewById(R.id.main_pbar);
        word = (EditText) findViewById(R.id.enter_word);

        mainPBar.setVisibility(View.INVISIBLE);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        String howMany = String.valueOf((c.getTimeInMillis()-System.currentTimeMillis())/60000);

        String savedDateTime = sp.getString("submitDate", "");
        if ("".equals(savedDateTime)) {
            //no previous datetime was saved (allow button click)
            submit.setEnabled(true);
        } else {
            String dateStringNow = DateHelper.getCurrentDate();
            //compare savedDateTime with today's datetime (dateStringNow)
            if (savedDateTime.equals(dateStringNow)) {
                //same date; disable button
                submit.setEnabled(false);
                submit.setText("You can submit again in "+howMany+" minutes");
            } else {
                //different date; allow button click
                submit.setEnabled(true);
            }
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!word.getText().toString().equals("")) {

                    ParseObject dailyWord = new DailyWord();
                    String wordString = word.getText().toString();
                    wordString = wordString.substring(0, 1).toUpperCase() + wordString.substring(1);
                    dailyWord.put("word", wordString);
                    dailyWord.saveInBackground();
                    word.setText("");

                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    String submitDate = DateHelper.getCurrentDate();

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("submitDate", submitDate);
                    editor.commit();
                    submit.setEnabled(false);
                    submit.setText(getString(R.string.btn_after_submit));

                    Toast.makeText(MainActivity.this, "Successfully submitted " + wordString,
                            Toast.LENGTH_LONG).show();

                }
            }
        });

        viewResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mainPBar.setVisibility(View.VISIBLE);

                ParseQuery<DailyWord> query = new ParseQuery<>("DailyWord");
                query.orderByDescending("createdAt");
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
                            Toast.makeText(MainActivity.this, "Error " + e, Toast.LENGTH_SHORT).show();
                        }
                        tvResult.setText(wordArray.toString());
                        mainPBar.setVisibility(View.INVISIBLE);
                    }
                });

            }

        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                startActivity(new Intent(MainActivity.this, LoginOrSignupActivity.class));
            }
        });
    }

}