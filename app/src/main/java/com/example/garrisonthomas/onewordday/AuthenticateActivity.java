package com.example.garrisonthomas.onewordday;

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

public class AuthenticateActivity extends BaseActivity {

    protected String mAction;
    protected EditText mUsernameField, mEmailField, mPasswordField;
    protected Button mButton;
    protected ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticate_layout);

        mUsernameField = (EditText) findViewById(R.id.et_enter_username);
        mEmailField = (EditText) findViewById(R.id.et_enter_email);
        mPasswordField = (EditText) findViewById(R.id.et_enter_password);
        mButton = (Button) findViewById(R.id.btn_signup_login);
        pBar = (ProgressBar) findViewById(R.id.auth_pbar);

        Bundle bundle = getIntent().getExtras();
        mAction = bundle.getString(LoginOrSignupActivity.TYPE);

        if (mAction.equals(LoginOrSignupActivity.SIGNUP)) {

            mButton.setText("Sign Up");
            setTitle("Sign Up");

        } else if (mAction.equals(LoginOrSignupActivity.LOGIN)) {

            mButton.setText("Login");
            mUsernameField.setVisibility(View.GONE);
            mPasswordField.setHint("Enter password...");
            mEmailField.setHint("Enter username...");

        }

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isInternetAvailable()) {
                    Toast.makeText(AuthenticateActivity.this, getString(R.string.toast_no_internet), Toast.LENGTH_SHORT).show();
                    return;
                }

                pBar.setVisibility(View.VISIBLE);

                if (mAction.equals(LoginOrSignupActivity.SIGNUP)) {
                    ParseUser user = new ParseUser();
                    String username = mUsernameField.getText().toString();
                    String email = mEmailField.getText().toString();
                    String password = mPasswordField.getText().toString();
                    user.setUsername(username);
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
                                        getString(R.string.toast_login_failed), Toast.LENGTH_LONG).show();
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
                                        getString(R.string.toast_login_failed), Toast.LENGTH_LONG).show();
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
