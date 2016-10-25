package com.zafodB.PhoToDo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class LoginScreenActivity extends AppCompatActivity {

    private CallbackManager myCallback;

    private AuthData myAuthData;
    private Profile fbProfile;

    private EditText email;
    private EditText password;
    private ProgressDialog dialog;
    private String enteredEmail;
    private String enteredPass;
    private String fbName;
    private String fbEmail;
    private String fbBirthday;

    private Firebase localRef;

    private static final int RESULT_OK = 3;

    private ApplicationMain appObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appObj = (ApplicationMain) getApplicationContext();
        Log.i(ApplicationMain.LOGIN_TAG, appObj.getMyString());


        FacebookSdk.sdkInitialize(getApplicationContext());
        localRef = ApplicationMain.getFirebaseRef();
        setContentView(R.layout.login_screen);

        myCallback = CallbackManager.Factory.create();

        LoginButton myLogin = (LoginButton) findViewById(R.id.fb_login_button);
        TextView forgetPass = (TextView) findViewById(R.id.password_forgot);
        email = (EditText) findViewById(R.id.signInEmail);
        password = (EditText) findViewById(R.id.sign_in_pass);

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpLoadingDialog();

        //Facebook login
        myLogin.setReadPermissions("public_profile", "email", "user_birthday");
        myLogin.registerCallback(myCallback, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                Log.i(ApplicationMain.LOGIN_TAG, "Facebook Login success");
                Log.i(ApplicationMain.LOGIN_TAG, loginResult.toString());
                AccessToken token = loginResult.getAccessToken();

                if (token != null) {

                    dialog.show();
                    localRef.authWithOAuthToken("facebook", token.getToken(), new Firebase
                            .AuthResultHandler() {

                        @Override
                        public void onAuthenticated(AuthData authData) {
                            //The Facebook user is now authenticated with Firebase

                            Log.i(ApplicationMain.LOGIN_TAG, "Firebase authentication success");
                            Log.i(ApplicationMain.LOGIN_TAG, authData.toString());

                            myAuthData = authData;

                            fbProfile = Profile.getCurrentProfile();
                            fbName = fbProfile.getName();

                            //Get data from facebook
                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(
                                                JSONObject object,
                                                GraphResponse response) {
                                            Log.i(ApplicationMain.LOGIN_TAG, "LoginActivity Response" + response
                                                    .toString());
                                            try {
                                                fbEmail = object.getString("email");
                                                fbBirthday = object.getString("birthday");
                                                CreateAccountActivity.createUserRecord(myAuthData.getUid(), fbEmail,
                                                        fbName,
                                                        calculateAge(Integer.parseInt(fbBirthday.substring(0, 2)),
                                                                Integer.parseInt(fbBirthday.substring(3, 5)), Integer
                                                                        .parseInt(fbBirthday.substring(fbBirthday
                                                                                .length() - 4))));

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Log.i(ApplicationMain.LOGIN_TAG, "Error retrieving data from facebook" +
                                                        ".");
                                            }
                                        }
                                    });

                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "email, birthday");
                            request.setParameters(parameters);

                            request.executeAsync();

                            ApplicationMain.setUserAuthData(authData);

                            loadUpGroups();

                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            // there was an error
                            Log.i(ApplicationMain.LOGIN_TAG, "Firebase authentication error");
                            Log.i(ApplicationMain.LOGIN_TAG, firebaseError.getMessage());
                        }
                    });
                } else {
                    // Logged out of Facebook so do a logout from the Firebase
                    localRef.unauth();
                    Log.i(ApplicationMain.LOGIN_TAG, "Firebase disconnected.");
                }

            }

            @Override
            public void onCancel() {
                Log.i(ApplicationMain.LOGIN_TAG, "Facebook authentication cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(ApplicationMain.LOGIN_TAG, "Facebook authentication error");
                Log.i(ApplicationMain.LOGIN_TAG, error.getMessage());
            }
        });

        //Sign in into existing account
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
                    dialog.show();

                    //Authenticate account with Firebase
                    localRef.authWithPassword(enteredEmail, enteredPass, new Firebase
                            .AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            Log.i(ApplicationMain.LOGIN_TAG, "Firebase authentication success");
                            Log.i(ApplicationMain.LOGIN_TAG, "User ID: " + authData.getUid() + ", Provider: " +
                                    authData.getProvider());

                            ApplicationMain.setUserAuthData(authData);
                            loadUpGroups();

                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            // there was an error
                            Toast.makeText(getApplicationContext(),
                                    firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                            Log.i(ApplicationMain.LOGIN_TAG, "Firebase authentication error");
                            Log.i(ApplicationMain.LOGIN_TAG, firebaseError.getMessage());
                            dialog.hide();
                        }
                    });
                }
            }
        });

        //Create new account = launch CreateAccountActivity activity
        Button SignUpButton = (Button) findViewById(R.id.sign_up_button);
        if (SignUpButton != null) {
            SignUpButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(LoginScreenActivity.this, CreateAccountActivity.class);
                    LoginScreenActivity.this.startActivityForResult(myIntent, 1);

                }
            });
        }
    }

    //When returned back to activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Returned from CreateAccountActivity
        if (resultCode == RESULT_OK) {
            loadUpGroups();
        }

        //Returned from Facebook login/approve application screen.
        myCallback.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Unauthorize firebase connection, when activity is destroyed.
    @Override
    protected void onDestroy() {
        localRef.unauth();
        super.onDestroy();
    }

    //Some pre-made toolbar listener
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

    //Set up confirmation dialog for password reset
    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_text))
                .setMessage(getString(R.string.reset_pass_text))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        localRef.resetPassword(email.getText().toString(), new Firebase
                                .ResultHandler() {

                            @Override
                            public void onSuccess() {
                                Toast.makeText(LoginScreenActivity.this, getText(R.string.email_reset_message)
                                        , Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(FirebaseError firebaseError) {
                                Toast.makeText(LoginScreenActivity.this, getString(R.string.connection_error_message)
                                        , Toast
                                                .LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();

    }

    //Set up loading dialog
    private void setUpLoadingDialog() {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.loading_message));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
    }

    //Calculate age from facebook birthday data.
    private int calculateAge(int month, int day, int year) {
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

    //Load up groups user is in.
    private void loadUpGroups() {
        localRef.child("ListOfUsers").child(localRef.getAuth().getUid())
                .child("inGroups").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ApplicationMain.setUserGroups(new ArrayList<String>());
                ApplicationMain.addToUserGroups(getString(R.string.my_tasks_name));

                boolean noGroups = true;
                String groupName;
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    try {
                        groupName = i.getValue(String.class);
                    } catch (FirebaseException e) {
                        continue;
                    }
                    if (!groupName.equals(getString(R.string.my_tasks_name))) {
                        noGroups = false;

                        ApplicationMain.addToUserGroups(i.getValue(String.class));
                    }
                }

                Intent myIntent;

                if (noGroups) {
                    myIntent = new Intent(LoginScreenActivity.this, GroupPick.class);
                    LoginScreenActivity.this.startActivityForResult(myIntent, 0);
                } else {
                    myIntent = new Intent(LoginScreenActivity.this, NavDrawerActivity.class);
                    LoginScreenActivity.this.startActivity(myIntent);
                }

                dialog.hide();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    //Wipe all the local user data after logout.
    static void LogOff(Context context) {
        ApplicationMain.getFirebaseRef().unauth();

        LoginManager.getInstance().logOut();

        ApplicationMain.setUserGroups(new ArrayList<String>());
        TasksListFragment.setTasks(new ArrayList<Task>());

        Log.i(ApplicationMain.LOGIN_TAG, "Logged out.");

        Toast.makeText(context,
                "You've been logged out.", Toast.LENGTH_LONG).show();
    }

}
