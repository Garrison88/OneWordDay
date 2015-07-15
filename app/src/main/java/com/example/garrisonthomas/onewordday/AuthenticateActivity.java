package com.example.garrisonthomas.onewordday;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class AuthenticateActivity extends BaseActivity {

    protected String mAction;
    protected EditText mEmailField, mPasswordField;
    protected Button mButton;
    protected ProgressBar pBar;
    public static String status = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticate_layout);

        mEmailField = (EditText) findViewById(R.id.et_enter_email);
        mPasswordField = (EditText) findViewById(R.id.et_enter_password);
        mButton = (Button) findViewById(R.id.btn_signup_login);
        pBar = (ProgressBar) findViewById(R.id.auth_pbar);

        Bundle bundle = getIntent().getExtras();
        mAction = bundle.getString(LoginOrSignupActivity.TYPE);

        if (mAction.equals(LoginOrSignupActivity.SIGNUP)) {

//            Button lsBtn = (Button) findViewById(R.id.btn_signup_login);
            mButton.setText("Sign Up");

        } else if (mAction.equals(LoginOrSignupActivity.LOGIN)) {

//            Button lsBtn = (Button) findViewById(R.id.btn_signup_login);
            mButton.setText("Login");

        }





        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isInternetAvailable()) {
                    Toast.makeText(AuthenticateActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                pBar.setVisibility(View.VISIBLE);

                if (mAction.equals(LoginOrSignupActivity.SIGNUP)) {
                    ParseUser user = new ParseUser();
                    String email = mEmailField.getText().toString();
                    String userName = email;
                    String password = mPasswordField.getText().toString();
                    user.setUsername(userName);
                    user.setEmail(email);
                    user.setPassword(password);
                    user.put("submittedToday", false);
                    user.saveInBackground();

                    user.signUpInBackground(new SignUpCallback() {
                        public void done(com.parse.ParseException e) {

                            pBar.setVisibility(View.INVISIBLE);
                            if (e == null) {
                                startActivity(new Intent(AuthenticateActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(AuthenticateActivity.this,
                                        "Woops! Signup failed, please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    String email = mEmailField.getText().toString();
                    String password = mPasswordField.getText().toString();

                    ParseUser.logInInBackground(email, password, new LogInCallback() {
                        public void done(ParseUser user, com.parse.ParseException e) {

                            pBar.setVisibility(View.INVISIBLE);

                            if (user != null) {

                                startActivity(new Intent(AuthenticateActivity.this, MainActivity.class));

                            } else {
                                Toast.makeText(AuthenticateActivity.this,
                                        "Woops! Login failed, please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
