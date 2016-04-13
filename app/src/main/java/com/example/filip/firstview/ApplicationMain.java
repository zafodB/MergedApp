package com.example.filip.firstview;

import com.firebase.client.Firebase;

/**
 * Created by filip on 12/04/2016.
 */
public class ApplicationMain extends android.app.Application {

    static Firebase myFirebaseRef;
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://torrid-inferno-1193.firebaseio.com/");
    }
}
