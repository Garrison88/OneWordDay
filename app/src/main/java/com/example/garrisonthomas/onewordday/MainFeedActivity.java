package com.example.garrisonthomas.onewordday;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MainFeedActivity extends ListActivity {

    public static final String TAG = MainFeedActivity.class.getSimpleName();

    protected ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLatestPosts();
    }

    protected void getLatestPosts() {
        mProgressBar.setVisibility(View.VISIBLE);

		/*
		 * Use ParseQuery to get latest posts
		 */
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        TextView urlLabel = (TextView) v.findViewById(android.R.id.text2);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(urlLabel.getText().toString()));
        startActivity(intent);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.activity_main_list, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.addButton:
//                startActivity(new Intent(this, AddLinkActivity.class));
//                return true;
//            case R.id.followButton:
//                startActivity(new Intent(this, SelectUsersActivity.class));
//                return true;
//            case R.id.logoutButton:
//			/*
//			 * Log current user out using ParseUser.logOut()
//			 */
//                Intent intent = new Intent(this, LoginOrSignupActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}