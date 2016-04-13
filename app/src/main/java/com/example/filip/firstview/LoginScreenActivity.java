package com.example.filip.firstview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LoginScreenActivity extends AppCompatActivity {

    private LoginButton myLogin;
    private CallbackManager myCallback;

    EditText email;
    EditText password;
    String enteredEmail;
    String enteredPass;
    String fbName;
    String fbEmail;
    String fbBirthday;

    static List<String> userGroups = new ArrayList<String>();
    static List<Task> tasks = new ArrayList<Task>();

    public static final String TAG = "fifko";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);


        myCallback = CallbackManager.Factory.create();
        myLogin = (LoginButton) findViewById(R.id.fb_login_button);
        myLogin.setReadPermissions("public_profile", "email", "user_birthday");

        email = (EditText) findViewById(R.id.signInEmail);
        password = (EditText) findViewById(R.id.signInPass);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myLogin.registerCallback(myCallback, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult loginResult) {

                Log.i(TAG, "Facebook Login success");
                Log.i(TAG, loginResult.toString());
                AccessToken token = loginResult.getAccessToken();

                if (token != null) {
                    ApplicationMain.myFirebaseRef.authWithOAuthToken("facebook", token.getToken(), new Firebase.AuthResultHandler() {


                        @Override
                        public void onAuthenticated(AuthData authData) {
                            // The Facebook user is now authenticated with your Firebase app

                            Log.i(TAG, "Firebase authentication success");
                            Log.i(TAG, authData.toString());

                            final AuthData myAuthData = authData;

                            final Profile fbProfile = Profile.getCurrentProfile();
                            fbName = fbProfile.getName();

//                            Log.i(TAG,fbProfile.getName();

                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(
                                                JSONObject object,
                                                GraphResponse response) {
                                            Log.v(TAG, "LoginActivity Response" + response.toString());
                                            try {
                                                fbEmail = object.getString("email");
                                                fbBirthday = object.getString("birthday");
                                                CreateAccountActivity.createUserRecord(myAuthData, fbEmail, fbName, calculateAge(Integer.parseInt(fbBirthday.substring(0, 2)), Integer.parseInt(fbBirthday.substring(3, 5)), Integer.parseInt(fbBirthday.substring(fbBirthday.length() - 4))));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "email, birthday");
                            request.setParameters(parameters);

                            request.executeAsync();

                            loadUpGroups();

                            Intent myIntent = new Intent(LoginScreenActivity.this, NavDrawerActivity.class);
                            LoginScreenActivity.this.startActivity(myIntent);
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            // there was an error
                            Log.i(TAG, "Firebase authentication error");
                            Log.i(TAG, firebaseError.getMessage());
                        }
                    });
                } else {
                     /* Logged out of Facebook so do a logout from the Firebase app */
                    ApplicationMain.myFirebaseRef.unauth();
                    Log.i(TAG, "firebase unauth");
                }

            }

            @Override
            public void onCancel() {
                Log.i(TAG, "Facebook authentication cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG, "Facebook authentication error");
                Log.i(TAG, error.getMessage());
            }
        });

        Button signInButton = (Button) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredEmail = email.getText().toString();
                enteredPass = password.getText().toString();

                if (enteredEmail.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Enter an e-mail.", Toast.LENGTH_LONG).show();
                } else if (enteredPass.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Enter a password.", Toast.LENGTH_LONG).show();
                } else {

                    ApplicationMain.myFirebaseRef.authWithPassword(enteredEmail, enteredPass, new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            Log.i(TAG, "Firebase authentication success");
                            Log.i(LoginScreenActivity.TAG, "User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());

                            loadUpGroups();

                            Intent myIntent = new Intent(LoginScreenActivity.this, NavDrawerActivity.class);
                            LoginScreenActivity.this.startActivity(myIntent);
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            // there was an error
                            Toast.makeText(getApplicationContext(),
                                    firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                            Log.i(TAG, "Firebase authentication error");
                            Log.i(TAG, firebaseError.getMessage());
                        }
                    });
                }
            }
        });

        Button SignUpButton = (Button) findViewById(R.id.signUpButton);
        if (SignUpButton != null) {
            SignUpButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(LoginScreenActivity.this, CreateAccountActivity.class);
                    LoginScreenActivity.this.startActivity(myIntent);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        myCallback.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        ApplicationMain.myFirebaseRef.unauth();
        super.onDestroy();
    }

    int calculateAge(int month, int day, int year) {
        Calendar c = Calendar.getInstance();
        int thisYear = c.get(Calendar.YEAR);
        int thisMonth = c.get(Calendar.MONTH);
        int thisDay = c.get(Calendar.DAY_OF_MONTH);

        if (month > thisMonth) {
            return thisYear - year - 1;
        } else if (month < thisMonth) {
            return thisYear - year;
        } else {
            if (day > thisDay) {
                return thisYear - year - 1;
            } else {
                return thisYear - year;
            }
        }
    }

    static void LogOff(Context context) {
        ApplicationMain.myFirebaseRef.unauth();

        LoginManager.getInstance().logOut();

        Log.i(LoginScreenActivity.TAG, "Logged out.");

        Toast.makeText(context,
                "You've been logged out.", Toast.LENGTH_LONG).show();
    }

    static void loadUpGroups() {
        ApplicationMain.myFirebaseRef.child("ListOfUsers").child(ApplicationMain.myFirebaseRef.getAuth().getUid()).child("inGroups").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    userGroups.add(i.getValue(String.class));
                }

//                for (String s : userGroups) {
//                    if (s.equals("myGroup")) {
//                        Log.i(TAG, "suck me");
//                    } else
//                        ApplicationMain.myFirebaseRef.child("Groups").child(s).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                int j = 0;
//                                for (DataSnapshot i : dataSnapshot.getChildren()) {
//                                    j++;
//                                    int day = i.child("dateDay").getValue(Integer.class);
//                                    int month = i.child("dateMonth").getValue(Integer.class);
//                                    int year = i.child("dateYear").getValue(Integer.class);
//                                    String name = i.child("name").getValue(String.class);
//
//
//                                    tasks.add(new Task(name,day,month,year));
//
////                                    Log.i(TAG, "worked with task: " + String.valueOf(j));
//
//                                }
//
//                            }
//
//                            @Override
//                            public void onCancelled(FirebaseError firebaseError) {
//
//                            }
//                        });
//                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
}
