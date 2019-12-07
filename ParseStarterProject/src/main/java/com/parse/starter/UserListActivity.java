package com.parse.starter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    ArrayList<String> users = new ArrayList<>();

    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        setTitle(R.string.User_List);

        ListView userListView = (ListView) findViewById(R.id.userListView);

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final Intent intent = new Intent(getApplicationContext(), ChatActivity2.class);

                intent.putExtra("username", users.get(i));

                startActivity(intent);

            }
        });

        users.clear();

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, users);

        userListView.setAdapter(arrayAdapter);





//        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Message");
//
//        query.whereExists("userblame");
//
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> list, ParseException e) {
//                if (e == null) {
//                    if (list.size() > 0) {
//                        ArrayList<String> uk = new ArrayList<>();
//                        uk.add("UK");
//
//                        ArrayList<String> germany = new ArrayList<>();
//                        uk.add("Germany");
//
//                        ArrayList<String> france = new ArrayList<>();
//                        uk.add("France");
//                        for (ParseObject country : list) {
//                            if (country.getString("userblame") != null) {
//                                if (country.getString("country").equals("UK")) {
//                                    uk.add(country.getString("userblame"));
//                                } else if (country.getString("country").equals("Germnay")) {
//                                    germany.add(country.getString("userblame"));
//                                } else {
//                                    france.add(country.getString("userblame"));
//                                }
//                            }
//                        }
//                        for (String german_mandem : germany) {
//                            uk.add(german_mandem);
//                        }
//                        for (String french_mandem : france) {
//                            uk.add(french_mandem);
//                        }
//                        for (String all_mandem : uk) {
//                            users.add(all_mandem);
//                        }
//
//                        arrayAdapter.notifyDataSetChanged();
//                    }
//                }
//            }
//        });













        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if (e == null) {

                    if (objects.size() > 0) {

                        for (ParseUser user : objects) {

                            users.add(user.getUsername());

                        }

                        arrayAdapter.notifyDataSetChanged();

                    }

                }

            }
        });


    }
}
