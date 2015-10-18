package com.example.garrisonthomas.onewordday;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class AuthenticateActivity extends BaseActivity {

    protected String mAction;
    protected EditText mUsernameField, mEmailField, mPasswordField, mConfirmPasswordField;
    protected Button mButton;
    ProgressBar toolbarPbar;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.authenticate_layout);

        setupUI(findViewById(R.id.authenticate_layout));

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        toolbarPbar = (ProgressBar) findViewById(R.id.toolbar_progress_bar);

        mUsernameField = (EditText) findViewById(R.id.et_enter_username);
        mEmailField = (EditText) findViewById(R.id.et_enter_email);
        mPasswordField = (EditText) findViewById(R.id.et_enter_password);
        mConfirmPasswordField = (EditText) findViewById(R.id.et_confirm_password);
        mButton = (Button) findViewById(R.id.btn_signup_login);

        Bundle bundle = getIntent().getExtras();
        mAction = bundle.getString(LoginOrSignupActivity.TYPE);

        if (mAction.equals(LoginOrSignupActivity.SIGNUP)) {

            mButton.setText(R.string.sign_up);
            setTitle(R.string.sign_up);

        } else if (mAction.equals(LoginOrSignupActivity.LOGIN)) {

            mButton.setText(R.string.login);
            mEmailField.setVisibility(View.GONE);
            mConfirmPasswordField.setVisibility(View.GONE);

        }

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isInternetAvailable()) {
                    Toast.makeText(AuthenticateActivity.this, getString(R.string.toast_no_internet), Toast.LENGTH_SHORT).show();
                    return;
                }

                toolbarPbar.setVisibility(View.VISIBLE);

                if (mAction.equals(LoginOrSignupActivity.SIGNUP)) {
                    final ParseUser user = new ParseUser();
                    final String email = mEmailField.getText().toString();
                    final String username = mUsernameField.getText().toString();
                    final String password = mPasswordField.getText().toString();
                    final String confirmPassword = mConfirmPasswordField.getText().toString();
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPassword(password);
                    user.put("submittedToday", false);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            user.signUpInBackground(new SignUpCallback() {
                                public void done(com.parse.ParseException e) {

                                    toolbarPbar.setVisibility(View.GONE);

                                    if (e == null && confirmPassword.equals(password)) {
                                        startActivity(new Intent(AuthenticateActivity.this, MainActivity.class));
                                        AuthenticateActivity.this.finish();
                                    } else if (e != null) {
                                        Toast.makeText(AuthenticateActivity.this,
                                                getString(R.string.toast_login_failed), Toast.LENGTH_LONG).show();

                                    } else if (!confirmPassword.equals(password)) {
                                        Toast.makeText(AuthenticateActivity.this,
                                                "Passwords do not match", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                    });

                } else {

                    final String username = mUsernameField.getText().toString();
                    final String password = mPasswordField.getText().toString();

                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        public void done(ParseUser user, com.parse.ParseException e) {

                            toolbarPbar.setVisibility(View.GONE);

                            if (user != null) {

                                AuthenticateActivity.this.finish();
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
}