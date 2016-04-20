package com.example.filip.firstview;

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
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class LoginScreenActivity extends AppCompatActivity {

    private LoginButton myLogin;
    private CallbackManager myCallback;
    ProgressDialog dialog;


    TextView forgetPass;
    EditText email;
    EditText password;
    String enteredEmail;
    String enteredPass;
    String fbName;
    String fbEmail;
    String fbBirthday;

//    static List<String> userGroups = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.login_screen);


        myCallback = CallbackManager.Factory.create();
        myLogin = (LoginButton) findViewById(R.id.fb_login_button);
        myLogin.setReadPermissions("public_profile", "email", "user_birthday");

        email = (EditText) findViewById(R.id.signInEmail);
        password = (EditText) findViewById(R.id.signInPass);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Retrievin' data from the base o' Fire ");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        forgetPass = (TextView) findViewById(R.id.password_forgot);
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


        myLogin.registerCallback(myCallback, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult loginResult) {

                Log.i(ApplicationMain.TAG, "Facebook Login success");
                Log.i(ApplicationMain.TAG, loginResult.toString());
                AccessToken token = loginResult.getAccessToken();

                if (token != null) {

                    dialog.show();
                    ApplicationMain.myFirebaseRef.authWithOAuthToken("facebook", token.getToken(), new Firebase
                            .AuthResultHandler() {

                        @Override
                        public void onAuthenticated(AuthData authData) {
                            // The Facebook user is now authenticated with your Firebase app

                            Log.i(ApplicationMain.TAG, "Firebase authentication success");
                            Log.i(ApplicationMain.TAG, authData.toString());

                            final AuthData myAuthData = authData;

                            final Profile fbProfile = Profile.getCurrentProfile();
                            fbName = fbProfile.getName();

                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(
                                                JSONObject object,
                                                GraphResponse response) {
                                            Log.v(ApplicationMain.TAG, "LoginActivity Response" + response.toString());
                                            try {
                                                fbEmail = object.getString("email");
                                                fbBirthday = object.getString("birthday");
                                                CreateAccountActivity.createUserRecord(myAuthData, fbEmail, fbName,
                                                        calculateAge(Integer.parseInt(fbBirthday.substring(0, 2)),
                                                                Integer.parseInt(fbBirthday.substring(3, 5)), Integer
                                                                        .parseInt(fbBirthday.substring(fbBirthday
                                                                                .length() - 4))));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "email, birthday");
                            request.setParameters(parameters);

                            request.executeAsync();

                            ApplicationMain.userAuthData = authData;

                            loadUpGroups();


                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            // there was an error
                            Log.i(ApplicationMain.TAG, "Firebase authentication error");
                            Log.i(ApplicationMain.TAG, firebaseError.getMessage());
                        }
                    });
                } else {
                     /* Logged out of Facebook so do a logout from the Firebase app */
                    ApplicationMain.myFirebaseRef.unauth();
                    Log.i(ApplicationMain.TAG, "firebase unauth");
                }

            }

            @Override
            public void onCancel() {
                Log.i(ApplicationMain.TAG, "Facebook authentication cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(ApplicationMain.TAG, "Facebook authentication error");
                Log.i(ApplicationMain.TAG, error.getMessage());
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

                    dialog.show();

                    ApplicationMain.myFirebaseRef.authWithPassword(enteredEmail, enteredPass, new Firebase
                            .AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            Log.i(ApplicationMain.TAG, "Firebase authentication success");
                            Log.i(ApplicationMain.TAG, "User ID: " + authData.getUid() + ", Provider: " +
                                    authData.getProvider());

                            ApplicationMain.userAuthData = authData;

                            loadUpGroups();

                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            // there was an error
                            Toast.makeText(getApplicationContext(),
                                    firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                            Log.i(ApplicationMain.TAG, "Firebase authentication error");
                            Log.i(ApplicationMain.TAG, firebaseError.getMessage());
                            dialog.hide();
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
                    LoginScreenActivity.this.startActivityForResult(myIntent, 1);

                }
            });
        }
    }


    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Reset password?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
//                        Toast.makeText(LoginScreenActivity.this, "Yaay", Toast.LENGTH_SHORT).show();
                        ApplicationMain.myFirebaseRef.resetPassword(email.getText().toString(), new Firebase
                                .ResultHandler() {

                            @Override
                            public void onSuccess() {
                                Toast.makeText(LoginScreenActivity.this, "We've sent you an email with instructions" +
                                        ".", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(FirebaseError firebaseError) {
                                Toast.makeText(LoginScreenActivity.this, "There was an error, try again.", Toast
                                        .LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 3) {
            loadUpGroups();
        }

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

        ApplicationMain.userGroups.clear();
        TasksListFragment.tasks.clear();

        Log.i(ApplicationMain.TAG, "Logged out.");

        Toast.makeText(context,
                "You've been logged out.", Toast.LENGTH_LONG).show();
    }

    void loadUpGroups() {
        ApplicationMain.myFirebaseRef.child("ListOfUsers").child(ApplicationMain.myFirebaseRef.getAuth().getUid())
                .child("inGroups").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean noGroups = false;
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    if (i.getValue(String.class).equals("0")) {
                        noGroups = true;
                        break;
                    }
                    ApplicationMain.userGroups.add(i.getValue(String.class));
                }

                Intent myIntent;

                if (noGroups) {
                    myIntent = new Intent(LoginScreenActivity.this, GroupPick.class);
                    LoginScreenActivity.this.startActivityForResult(myIntent, 0);
                    ApplicationMain.myFirebaseRef.child("ListOfUsers").child(ApplicationMain.myFirebaseRef.getAuth().getUid())
                            .child("inGroups").removeEventListener(this);

                }else {
                    myIntent = new Intent(LoginScreenActivity.this, NavDrawerActivity.class);
                    LoginScreenActivity.this.startActivity(myIntent);
                    ApplicationMain.myFirebaseRef.child("ListOfUsers").child(ApplicationMain.myFirebaseRef.getAuth().getUid())
                            .child("inGroups").removeEventListener(this);
                }


                dialog.hide();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }


}
