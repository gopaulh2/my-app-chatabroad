package com.parse.starter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ChatActivity2 extends AppCompatActivity {

    String activeUser = "";

    ArrayList<String> messages = new ArrayList<>();

    ArrayAdapter arrayAdapter;



    private EditText inputToTranslate;
    private TextView translatedTv;
    private String originalText;
    private String translatedText;
    private boolean connected;
    private String  myCountry;

    Translate translate;


    public void sendChat(View view) {

        final EditText chatEditText = (EditText) findViewById(R.id.chatEditText);

        ParseObject message = new ParseObject("Message");

        final String messageContent = chatEditText.getText().toString();

        message.put("sender", ParseUser.getCurrentUser().getUsername());
        message.put("recipient", activeUser);
        message.put("message", messageContent);

        chatEditText.setText("");

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {

                    messages.add(messageContent);

                    arrayAdapter.notifyDataSetChanged();

                }

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();

        myCountry = "";

        activeUser = intent.getStringExtra("username");

        setTitle(activeUser);

        ListView chatListView = (ListView) findViewById(R.id.chatListView);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, messages);

        chatListView.setAdapter(arrayAdapter);

        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");

        query1.whereExists("userblame");

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");

        query2.whereExists("sender");

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> query = ParseQuery.or(queries);

        query.orderByAscending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    if (objects.size() > 0) {
                        String to_language = "ar";

                        for (ParseObject message : objects) {

                            String messageContent = "";
                            String theirCountry = "";

                            if (message.getString("userblame") != null) {
                                if (message.getString("userblame").equals(ParseUser.getCurrentUser().getUsername())) {
                                    myCountry = message.getString("country");
                                    Log.i("MY COUNTRY OF RESIDENCE IS......-------.......", myCountry + "--------------------------------");
                                } else {
                                    if (message.getString("userblame").equals(activeUser)) {
                                        theirCountry = message.getString("country");

                                        Log.i("THIER COUNTRY OF RESIDENCE IS......-------.......", theirCountry + "--------------------------------");
                                    }
                                }
                            } else {
                                Log.i("COUNTRY......-------.......","IT IS ACTUALLY QUITE NULL IM AFRAID");
                            }

                            if (!myCountry.equals(theirCountry)) {
                                if (myCountry.equals("Germany")) {
                                    to_language = "de";
                                } else if (myCountry.equals("France")) {
                                    to_language = "fr";
                                } else {
                                    to_language = "en";
                                }
                            }


                            if (message.get("sender")!= null ) {

                                boolean x = (message.getString("sender").equals(activeUser) && message.getString("recipient").equals(ParseUser.getCurrentUser().getUsername()))
                                        || (message.getString("recipient").equals(activeUser) && message.getString("sender").equals(ParseUser.getCurrentUser().getUsername()));

                                if (x) {

                                    if (message.get("sender").equals(activeUser) && message.getString("recipient").equals(ParseUser.getCurrentUser().getUsername())) {

                                        messageContent = activeUser + ": " + message.getString("message");

                                    } else {
                                        if (message.get("sender").equals(ParseUser.getCurrentUser().getUsername()) && message.getString("recipient").equals(activeUser))
                                            messageContent = "Me: " + message.getString("message");
                                    }

                                    Log.i("Info", messageContent);

                                    if (!to_language.equals("ar") && checkInternetConnection()) {
                                        getTranslateService();
                                        messages.add(translate(messageContent, to_language));

                                        Html.fromHtml("Let&#39;s see");
                                    } else {
                                        messages.add(messageContent);
                                    }
                                }
                            }

//                            if (message.getString("sender") != null) {
//                                boolean x = (message.getString("sender").equals(activeUser) && message.getString("recipient").equals(ParseUser.getCurrentUser().getUsername()))
//                                        || (message.getString("recipient").equals(activeUser) && message.getString("sender").equals(ParseUser.getCurrentUser().getUsername()));
//
//                                        if(!x) {
//                                            messages.clear();
//                                            System.out.println("TMESSAGES ARE BEING CLEARED ------"+message.getString("message"));
//                                        }
//                            }
                        }

                        arrayAdapter.notifyDataSetChanged();

                    }

                }

            }
        });
    }


    public void getTranslateService() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (InputStream is = getResources().openRawResource(R.raw.credentials)) {

            //Get credentials:
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //Set credentials and get translate service:
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();

        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }

    public String translate(String originalLanguage, String languageInput) {

        //Get input text to be translated:
        originalText = originalLanguage;
        Translation translation = translate.translate(originalText, Translate.TranslateOption.targetLanguage(languageInput), Translate.TranslateOption.model("base"));
        translatedText = translation.getTranslatedText();
        //Translated text and original text are set to TextViews:
        return translatedText.replace("&#39;","'");

    }

    public boolean checkInternetConnection() {

        //Check internet connection:
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Means that we are connected to a network (mobile or wi-fi)
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        return connected;

    }

}