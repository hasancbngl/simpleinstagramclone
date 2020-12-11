package com.cobanogluhasan.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class UsersListActivity extends AppCompatActivity {

    ListView usersListView;
    ArrayList<String> usersList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        usersListView = findViewById(R.id.userslistView);
        usersList = new ArrayList<String>();

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_gallery_item,usersList);
        usersListView.setAdapter(arrayAdapter);


        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),UsersFeedActivity.class);
                intent.putExtra("username", usersList.get(position));
                startActivity(intent);
            }
        });


        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(objects.size()>0 && e==null) {
                    for (ParseUser user:objects) {
                        usersList.add(user.getUsername());
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
                else e.printStackTrace();
            }
        });



    }
}