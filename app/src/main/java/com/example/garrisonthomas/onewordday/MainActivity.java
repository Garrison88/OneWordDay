package com.example.garrisonthomas.onewordday;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity {

    EditText word;
    Button submit, viewResults;
    public static TextView tvResult;
    public static ProgressBar mainPBar;
    SharedPreferences sp;
    ParseUser currentUser;
    Toolbar mToolbar;
    String [] moodNumber;
    String [] moodNames;
    String moodAtPosition;

    Spinner moodSpinner;

    RecyclerView resultsRecyclerView;
    RecyclerViewAdapter rvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        currentUser = ParseUser.getCurrentUser();

        sp = getSharedPreferences(getString(R.string.application_id), Context.MODE_PRIVATE);

        submit = (Button) findViewById(R.id.btn_submit);
        viewResults = (Button) findViewById(R.id.btn_view_results);

        tvResult = (TextView) findViewById(R.id.tv_result);

        word = (EditText) findViewById(R.id.et_enter_word);

        mainPBar = (ProgressBar) findViewById(R.id.main_pbar);

        word.setHint("How was your day, " + currentUser.getUsername() + "?");

        moodSpinner = (Spinner) findViewById(R.id.image_spinner);
        moodNumber = getResources().getStringArray(R.array.mood);
        moodNames = getResources().getStringArray(R.array.moodNames);

        resultsRecyclerView = (RecyclerView) findViewById(R.id.results_recycler_view);
        rvAdapter = new RecyclerViewAdapter(MainActivity.this, getData());
        resultsRecyclerView.setAdapter(rvAdapter);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        mainPBar.setVisibility(View.INVISIBLE);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.moodNames, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moodSpinner.setAdapter(adapter);
        moodSpinner.setSelection(0);
        moodSpinner.setAdapter(adapter);

        moodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                moodAtPosition = moodNumber[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        submit.setOnClickListener(new View.OnClickListener()

              {
                  @Override
                  public void onClick(View v) {

                      InputMethodManager imm = (InputMethodManager) getSystemService(
                              Context.INPUT_METHOD_SERVICE);
                      imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                      if (!isInternetAvailable()) {
                          Toast.makeText(MainActivity.this, getString(R.string.toast_no_internet), Toast.LENGTH_SHORT).show();
                          return;
                      }

                      if (!TextUtils.isEmpty(word.getText())) {

                          if (!currentUser.getBoolean("emailVerified")) {
                              try {
                                  currentUser.fetch();
                              } catch (Exception e) {
                                  e.printStackTrace();
                              }
                          }

                          if (currentUser.getBoolean("emailVerified")) {

                              if (!currentUser.getBoolean("submittedToday")) {
                                  ParseObject dailyWord = new DailyWord();
                                  String wordString = word.getText().toString();
                                  dailyWord.put("word", wordString);
                                  dailyWord.put("moodNumber", moodAtPosition);
                                  dailyWord.saveInBackground();
                                  word.setText("");

                                  String submitDate = DateHelper.getCurrentDate();

                                  SharedPreferences.Editor editor = sp.edit();
                                  editor.putString("submitDate", submitDate);
                                  editor.apply();
                                  currentUser.put("submittedToday", true);
                                  currentUser.saveInBackground();
                                  submit.setEnabled(false);
                                  submit.setText(getString(R.string.btn_after_submit));

                                  Toast.makeText(MainActivity.this, "Successfully submitted " + wordString,
                                          Toast.LENGTH_LONG).show();
                              }
                          } else {
                              Toast.makeText(MainActivity.this, "Please verify email address",
                                      Toast.LENGTH_LONG).show();
                          }
                      }
                  }
              }

        );

//    viewResults.setOnClickListener(new View.OnClickListener()
//
//    {
//        @Override
//        public void onClick (View v){
//
//        if (!isInternetAvailable()) {
//            Toast.makeText(MainActivity.this, getString(R.string.toast_no_internet), Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        mainPBar.setVisibility(View.VISIBLE);
//
//
//        final ParseQuery<DailyWord> query = new ParseQuery<>("DailyWord");
//        query.orderByDescending("createdAt");
//        query.findInBackground(new FindCallback<DailyWord>() {
//            public void done(List<DailyWord> objects, com.parse.ParseException e) {
//                ArrayList<String> wordArray = new ArrayList<>();
//                String currentDate = DateHelper.getCurrentDate();
//                Format formatter = new SimpleDateFormat("yyyy-MM-dd");
//                if (e == null) {
//                    for (DailyWord newWord : objects) {
//                        String s = formatter.format(newWord.getCreatedAt());
////                        if (s.equals(currentDate)) {
//                        DailyWord dWord = new DailyWord();
//                        dWord.setWord(newWord.getWord());
//                        wordArray.add(String.valueOf(dWord));
////                        }
//                    }
//                } else {
//                    Toast.makeText(MainActivity.this, "Error " + e, Toast.LENGTH_SHORT).show();
//                }
//
//                tvResult.setText(String.valueOf(wordArray));
//                mainPBar.setVisibility(View.INVISIBLE);
//            }
//        });
//
//    }
//
//    }
//
//    );
    }

    public static List<RecyclerViewData> getData() {

        final List<RecyclerViewData> data = new ArrayList<>();
        final ParseQuery<DailyWord> query = new ParseQuery<>("DailyWord");
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<DailyWord>() {
            public void done(List<DailyWord> objects, com.parse.ParseException e) {
                String currentDate = DateHelper.getCurrentDate();
                Format formatter = new SimpleDateFormat("yyyy-MM-dd");

                if (e == null) {
                    for (DailyWord newWord : objects) {
                        String s = formatter.format(newWord.getCreatedAt());
//                        if (s.equals(currentDate)) {
                        RecyclerViewData current = new RecyclerViewData();
                        DailyWord dWord = new DailyWord();
                        dWord.setWord(newWord.getWord());
                        dWord.setMood(newWord.getMood());
                        if (dWord.getMood() == 1) {
                            current.iconId = (R.drawable.mood_happy);
                        } else if (dWord.getMood() == 2) {
                            current.iconId = (R.drawable.mood_neutral);
                        } else {
                            current.iconId = (R.drawable.mood_sad);
                        }
                        current.title = dWord.toString();
                        data.add(current);
//                        }
                    }
                }
                mainPBar.setVisibility(View.INVISIBLE);
            }
        });

        return data;
    }

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), "Press again to exit",
                    Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (!isInternetAvailable()) {
            Toast.makeText(MainActivity.this, getString(R.string.toast_no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            currentUser.fetch();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String savedDateTime = sp.getString("submitDate", "");
        if (!currentUser.getBoolean("submittedToday")) {
            //no previous datetime was saved (allow button click)
            currentUser.put("submittedToday", false);
            submit.setEnabled(true);
            submit.setText(getString(R.string.btn_submit));

        } else {
            String dateStringNow = DateHelper.getCurrentDate();
            //compare savedDateTime with today's datetime (dateStringNow)
            if (savedDateTime != null) {
                if (savedDateTime.equals(dateStringNow)) {
                    //same date; disable button
                    currentUser.put("submittedToday", true);
                    submit.setEnabled(false);
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    String howManyMinutes = String.valueOf((c.getTimeInMillis() - System.currentTimeMillis()) / 60000);
                    if (Integer.parseInt(howManyMinutes) <= 60) {
                        submit.setText("You can submit again in " + howManyMinutes + " minutes");
                    } else if (Integer.parseInt(howManyMinutes) == 1) {

                        submit.setText("You can submit again in " + howManyMinutes + " minute");

                    } else if ((Integer.parseInt(howManyMinutes) > 60) && (Integer.parseInt(howManyMinutes) < 120)) {

                        String howManyHours = String.valueOf((c.getTimeInMillis() - System.currentTimeMillis()) / 3600000);
                        submit.setText("You can submit again in " + howManyHours + " hour");

                    } else {
                        String howManyHours = String.valueOf((c.getTimeInMillis() - System.currentTimeMillis()) / 3600000);
                        submit.setText("You can submit again in " + howManyHours + " hours");

                    }
                } else {
                    //different date; allow button click
                    currentUser.put("submittedToday", false);
                    submit.setEnabled(true);
                    submit.setText("Submit");
                }
            }
        }
    }
}