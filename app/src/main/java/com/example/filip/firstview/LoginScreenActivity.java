package com.example.filip.firstview;

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
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginScreenActivity extends AppCompatActivity {

    private LoginButton myLogin;
    private CallbackManager myCallback;
    static Firebase myFirebaseRef;
    EditText email;
    EditText password;
    String enteredEmail;
    String enteredPass;
    String fbName;
    String fbEmail;


    public static final String TAG = "fifko";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);

        myFirebaseRef = new Firebase("https://torrid-inferno-1193.firebaseio.com/");

        myCallback = CallbackManager.Factory.create();
        myLogin = (LoginButton) findViewById(R.id.fb_login_button);
        myLogin.setReadPermissions("public_profile");
        myLogin.setReadPermissions("email");
        myLogin.setReadPermissions("user_birthday");


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
                    myFirebaseRef.authWithOAuthToken("facebook", token.getToken(), new Firebase.AuthResultHandler() {


                        @Override
                        public void onAuthenticated(AuthData authData) {
                            // The Facebook user is now authenticated with your Firebase app


                            Log.i(TAG, "Firebase authentication success");
                            Log.i(TAG, authData.toString());

                            Profile fbProfile = Profile.getCurrentProfile();


                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(
                                                JSONObject object,
                                                GraphResponse response) {
                                            Log.v("LoginActivity Response ", response.toString());

                                            try {
                                                fbName = object.getString("name");

                                                fbEmail = object.getString("email");
                                                Log.v(TAG, "Email = " + fbEmail);
                                                Toast.makeText(getApplicationContext(), "Name " + fbName, Toast.LENGTH_LONG).show();


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

//                            try {
                                CreateAccountActivity.createUserRecord(authData, fbName, fbEmail, 0);
//                            } catch (NullPointerException e) {
//                                e.printStackTrace();
//                            }

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
                    myFirebaseRef.unauth();
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

                    myFirebaseRef.authWithPassword(enteredEmail, enteredPass, new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            Log.i(TAG, "Firebase authentication success");
                            Log.i(LoginScreenActivity.TAG, "User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());

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
        myFirebaseRef.unauth();
        super.onDestroy();
    }

    static void LogOff(Context context) {
        LoginScreenActivity.myFirebaseRef.unauth();

        LoginManager.getInstance().logOut();

        Log.i(LoginScreenActivity.TAG, "Logged out.");

        Toast.makeText(context,
                "You've been logged out.", Toast.LENGTH_LONG).show();
    }

    ;


}
