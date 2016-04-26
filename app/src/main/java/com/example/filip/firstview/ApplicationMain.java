package com.example.filip.firstview;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by filip on 12/04/2016.
 */
public class ApplicationMain extends android.app.Application {

    static private Firebase myFirebaseRef;
    static private List<String> userGroups = new ArrayList<>();
    static private AuthData userAuthData;

    public static final String LOGIN_TAG = "login";

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

    public static Firebase getFirebaseRef() {
        return myFirebaseRef;
    }

    public static List<String> getUserGroups() {
        return userGroups;
    }

    public static void setUserGroups(List<String> userGroups) {
        ApplicationMain.userGroups = userGroups;
    }

    public  static void addToUserGroups (String group){
        ApplicationMain.userGroups.add(group);
    }

    public  static void addToUserGroups (int pos, String group){
        ApplicationMain.userGroups.add(pos, group);
    }

    public static AuthData getUserAuthData() {
        return userAuthData;
    }

    public static void setUserAuthData(AuthData userAuthData) {
        ApplicationMain.userAuthData = userAuthData;
    }
}
