package com.example.filip.firstview;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by filip on 12/04/2016.
 */
public class ApplicationMain extends android.app.Application {

    static Firebase myFirebaseRef;
    static List<String> userGroups = new ArrayList<>();
    static AuthData userAuthData;

    public static final String TAG = "fifko";

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://torrid-inferno-1193.firebaseio.com/");
    }

    @Override
    public void onTerminate() {
        LoginScreenActivity.LogOff(getApplicationContext());
        super.onTerminate();
    }

}
