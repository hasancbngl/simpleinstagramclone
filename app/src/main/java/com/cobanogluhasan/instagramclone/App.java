package com.cobanogluhasan.instagramclone;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("myappID")
                .clientKey("OYBtS9PrUYPR")
                .server("http://13.58.33.30/parse/")
                .build()
        );


        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }


}
