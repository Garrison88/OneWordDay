package com.example.garrisonthomas.onewordday;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.text.ParseException;

public class AuthenticateActivity extends Activity {

    protected String mAction;
    protected EditText mEmailField;
    protected EditText mPasswordField;
    protected Button mButton;
    protected ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticate_layout);

        mEmailField = (EditText) findViewById(R.id.et_enter_email);
        mPasswordField = (EditText) findViewById(R.id.et_enter_password);
        mButton = (Button) findViewById(R.id.btn_sign_up);

        Bundle bundle = getIntent().getExtras();
        mAction = bundle.getString(LoginOrSignupActivity.TYPE);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);

                String username = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();

                if (mAction.equals(LoginOrSignupActivity.SIGNUP)) {
                    ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setPassword(password);

                    user.signUpInBackground(new SignUpCallback() {
                        public void done(com.parse.ParseException e) {

                            if (e == null) {

                                startActivity(new Intent(AuthenticateActivity.this, MainFeedActivity.class));

                            } else {

                                Toast.makeText(AuthenticateActivity.this,
                                        "Woops! Signup failed, please try again", Toast.LENGTH_LONG).show();

                            }

                        }
                    });
                } else {
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        public void done(ParseUser user, com.parse.ParseException e) {
                            if (user != null) {
                                startActivity(new Intent(AuthenticateActivity.this, MainFeedActivity.class));
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

}
