package com.example.garrisonthomas.onewordday;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private EditText word;
    private ImageButton submit;
    private SharedPreferences sp;
    private ParseUser currentUser;
    private int moodAtPosition;
    private TypedArray moodImages;
    private String[] moodWords;
    private ProgressBar toolbarPbar;

    SwipeRefreshLayout swipeRefreshLayout;

    Spinner moodSpinner;

    RecyclerView resultsRecyclerView;
    RecyclerViewAdapter rvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setupUI(findViewById(R.id.activity_main_layout));

        Resources res = getResources();

        currentUser = ParseUser.getCurrentUser();
        try {
            currentUser.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setSubtitle(currentUser.getUsername());

        toolbarPbar = (ProgressBar) findViewById(R.id.toolbar_progress_bar);

        moodImages = res.obtainTypedArray(R.array.mood_images);
        moodWords = res.getStringArray(R.array.mood_words);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        submit = (ImageButton) findViewById(R.id.btn_submit);

        word = (EditText) findViewById(R.id.et_enter_word);

//        word.setHint("How was your day, " + currentUser.getUsername() + "?");

        resultsRecyclerView = (RecyclerView) findViewById(R.id.results_recycler_view);
        resultsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        rvAdapter = new RecyclerViewAdapter(this, getData());
        resultsRecyclerView.setAdapter(rvAdapter);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorButtonNormal);

        moodSpinner = (Spinner) findViewById(R.id.image_spinner);

        moodSpinner.setAdapter(new MyCustomAdapter(MainActivity.this, R.layout.custom_mood_spinner, moodWords));
        moodSpinner.setSelection(0);

        moodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                moodAtPosition = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isInternetAvailable()) {
                    Toast.makeText(MainActivity.this, getString(R.string.toast_no_internet), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!TextUtils.isEmpty(word.getText())) {

                    if (currentUser.getBoolean("emailVerified")) {

                        if (!currentUser.getBoolean("submittedToday")) {

                            toolbarPbar.setVisibility(View.VISIBLE);

                            DailyWord dailyWord = new DailyWord();
                            final String wordString = word.getText().toString();
                            dailyWord.setWord(wordString);
                            dailyWord.setMood(moodAtPosition);
                            dailyWord.setDate(todaysDate);
                            dailyWord.setRelatedUsername(currentUser.getUsername());
                            dailyWord.saveInBackground();
                            word.setText("");

                            String submitDate = DateHelper.getCurrentDate();

                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("submitDate", submitDate);
                            editor.apply();
                            currentUser.put("submittedToday", true);
                            currentUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {

                                        toolbarPbar.setVisibility(View.GONE);

                                        submit.setEnabled(false);
                                        word.setEnabled(false);

                                        Toast.makeText(MainActivity.this, "Successfully submitted " + wordString,
                                                Toast.LENGTH_LONG).show();

                                    } else {

                                        toolbarPbar.setVisibility(View.GONE);

                                        Toast.makeText(MainActivity.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        } else {

                            Calendar c = Calendar.getInstance();
                            c.add(Calendar.DAY_OF_MONTH, 1);
                            c.set(Calendar.HOUR_OF_DAY, 0);
                            c.set(Calendar.MINUTE, 0);
                            c.set(Calendar.SECOND, 0);
                            c.set(Calendar.MILLISECOND, 0);

                            String howManyMinutes = String.valueOf((c.getTimeInMillis() - System.currentTimeMillis()) / 60000);
                            String howManyHours = String.valueOf((c.getTimeInMillis() - System.currentTimeMillis()) / 3600000);

                            if (Integer.parseInt(howManyMinutes) <= 60) {

                                Toast.makeText(MainActivity.this, "You can submit again in " + howManyMinutes + " minutes", Toast.LENGTH_LONG).show();

                            } else if (Integer.parseInt(howManyMinutes) == 1) {

                                Toast.makeText(MainActivity.this, "You can submit again in " + howManyMinutes + " minute", Toast.LENGTH_LONG).show();

                            } else if ((Integer.parseInt(howManyMinutes) > 60) && (Integer.parseInt(howManyMinutes) < 120)) {


                                Toast.makeText(MainActivity.this, "You can submit again in " + howManyHours + " hour", Toast.LENGTH_LONG).show();

                            } else {

                                Toast.makeText(MainActivity.this, "You can submit again in " + howManyHours + " hours", Toast.LENGTH_LONG).show();

                            }

                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Please verify email address",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    public List<RecyclerViewData> getData() {

        final List<RecyclerViewData> data = new ArrayList<>();
        final ParseQuery<DailyWord> query = new ParseQuery<>("DailyWord");
        query.whereEqualTo("date", todaysDate);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<DailyWord>() {
            public void done(List<DailyWord> objects, com.parse.ParseException e) {

                if (e == null) {
                    for (DailyWord newWord : objects) {
                        RecyclerViewData current = new RecyclerViewData();

                        current.iconId = moodImages.getResourceId(newWord.getMood(), 0);
                        current.title = newWord.toString();
                        current.userAndDate = "Submitted by " + newWord.getRelatedUsername() + " on " + newWord.getDate();
                        data.add(current);
                    }
                }
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
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

    public class MyCustomAdapter extends ArrayAdapter<String> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            //return super.getView(position, convertView, parent);

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.custom_mood_spinner, parent, false);

            TextView moodWord = (TextView) row.findViewById(R.id.spinner_mood_word);
            moodWord.setText(moodWords[position]);
            Drawable images = moodImages.getDrawable(position);
            ImageView moodImage = (ImageView) row.findViewById(R.id.spinner_mood_image);
            moodImage.setImageDrawable(images);

            return row;
        }
    }

    @Override
    protected void onStart() {

        super.onStart();

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
//            currentUser.put("submittedToday", false);
            submit.setEnabled(true);

        } else {
            String dateStringNow = DateHelper.getCurrentDate();
            //compare savedDateTime with today's datetime (dateStringNow)
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
                        Toast.makeText(MainActivity.this, "You can submit again in " + howManyMinutes + " minutes", Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(howManyMinutes) == 1) {

                        Toast.makeText(MainActivity.this, "You can submit again in " + howManyMinutes + " minute", Toast.LENGTH_SHORT).show();

                    } else if ((Integer.parseInt(howManyMinutes) > 60) && (Integer.parseInt(howManyMinutes) < 120)) {

                        String howManyHours = String.valueOf((c.getTimeInMillis() - System.currentTimeMillis()) / 3600000);

                        Toast.makeText(MainActivity.this, "You can submit again in " + howManyHours + " hour", Toast.LENGTH_SHORT).show();

                    } else {
                        String howManyHours = String.valueOf((c.getTimeInMillis() - System.currentTimeMillis()) / 3600000);
                        Toast.makeText(MainActivity.this, "You can submit again in " + howManyHours + " hours", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    //different date; allow button click
                    currentUser.put("submittedToday", false);
                    currentUser.saveInBackground();
                    word.setEnabled(true);
                    submit.setEnabled(true);
                }

        }
    }

    @Override
    public void onRefresh() {

        resultsRecyclerView = (RecyclerView) findViewById(R.id.results_recycler_view);
        rvAdapter = new RecyclerViewAdapter(MainActivity.this, getData());
        resultsRecyclerView.setAdapter(rvAdapter);
//        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }
}