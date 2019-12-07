/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity implements View.OnKeyListener{

    // Radio Button

    RadioGroup radioGroup;
    RadioButton radioButton;
    TextView textView;

    Boolean loginModeActive = false;
    RelativeLayout relativeLayout;
    EditText usernameEditText;

    EditText passwordEditText;

    public void checkButton(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(radioId);
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

    if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
        signupLogin(view);
    }

        return false;
    }


    public void messageCountryLogin() {
        ParseObject country = new ParseObject("Message");
        country.put("userblame", usernameEditText.getText().toString());
//        country.put("sender", usernameEditText.getText().toString());
//        country.put("recipient","x");
        country.put("country", radioButton.getText());
        country.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("Success!!", "Country added");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }



    public void redirectIfLoggedIn() {

        if (ParseUser.getCurrentUser() != null) {

            Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
            startActivity(intent);

        }

    }

    public void toggleLoginMode(View view) {

        Button loginSignupButton = (Button) findViewById(R.id.loginSignupButton);

        TextView toggleLoginModeTextView = (TextView) findViewById(R.id.toggleLoginModeTextView);

        if (loginModeActive) {

            loginModeActive = false;
            loginSignupButton.setText(R.string.sign_up);
            toggleLoginModeTextView.setText(R.string.or_login);
            radioGroup.setVisibility(View.VISIBLE);



        } else {

            loginModeActive = true;
            loginSignupButton.setText(R.string.login);
            toggleLoginModeTextView.setText(R.string.or_signup);
            radioGroup.setVisibility(View.GONE);

        }

    }

    public void relativeOnClick(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }



    public void signupLogin(View view) {

        checkButton(view);


        if (loginModeActive) {

          ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
              @Override
              public void done(ParseUser user, ParseException e) {

                  if (e == null) {

                      Log.i("Info", "user logged in");

                      redirectIfLoggedIn();

                  } else {

                      String message = e.getMessage();

                      if (message.toLowerCase().contains("java")) {

                          message = e.getMessage().substring(e.getMessage().indexOf(" "));

                      }

                      Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                  }

              }
          });


      } else {

          ParseUser user = new ParseUser();

          user.setUsername(usernameEditText.getText().toString());

          user.setPassword(passwordEditText.getText().toString());

          messageCountryLogin();

          user.signUpInBackground(new SignUpCallback() {
              @Override
              public void done(ParseException e) {

                  if (e == null) {

                      redirectIfLoggedIn();

                  } else {

                      String message = e.getMessage();

                      if (message.toLowerCase().contains("java")) {

                          message = e.getMessage().substring(e.getMessage().indexOf(" "));

                      }

                      Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                  }

              }
          });

      }
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

      setTitle(R.string.Chatabroad_Login);

      usernameEditText = (EditText) findViewById(R.id.usernameEditText);
      passwordEditText = (EditText) findViewById(R.id.passwordEditText);

      relativeLayout = (RelativeLayout) findViewById(R.id.activity_chat);

      passwordEditText.setOnKeyListener(this);

      radioGroup = (RadioGroup)  findViewById(R.id.radioGroup);
      textView = (TextView)  findViewById(R.id.countryTextView);

      redirectIfLoggedIn();
    
    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}