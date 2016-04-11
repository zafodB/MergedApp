package com.example.filip.firstview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    EditText confirmPassword;
    CheckBox terms;
    String enteredEmail;
    String enteredPass;
    String enteredConfirmPass;
    static AuthData userAuthData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_activity);

        Firebase.setAndroidContext(this);

        email = (EditText) findViewById(R.id.singUpEmail);
        password = (EditText) findViewById(R.id.SignUpPassword);
        confirmPassword = (EditText) findViewById(R.id.SignUpPassConfirm);
        terms = (CheckBox) findViewById(R.id.iAccept);


        Button signUpButton = (Button) findViewById(R.id.CreateAccButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                enteredEmail = email.getText().toString();
                                                enteredPass = password.getText().toString();
                                                enteredConfirmPass = confirmPassword.getText().toString();

                                                if (enteredEmail.equals("")) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Enter an e-mail.", Toast.LENGTH_LONG).show();
                                                    Log.i(LoginScreenActivity.TAG, "No email entered.");

                                                } else if (enteredPass.equals("")) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Enter a password.", Toast.LENGTH_LONG).show();
                                                    Log.i(LoginScreenActivity.TAG, "No password entered.");

                                                } else if (enteredConfirmPass.equals("")) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Repeat a password.", Toast.LENGTH_LONG).show();
                                                    Log.i(LoginScreenActivity.TAG, "No password confirmation entered.");

                                                } else if (enteredPass.equals(enteredConfirmPass) != true) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Passwords do not match.", Toast.LENGTH_LONG).show();
                                                    Log.i(LoginScreenActivity.TAG, "Entered passwords don't match.");

                                                } else if (terms.isChecked() != true) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "The terms must be accepted.", Toast.LENGTH_LONG).show();
                                                    Log.i(LoginScreenActivity.TAG, "Terms weren't accepted.");

                                                } else {
                                                    LoginScreenActivity.myFirebaseRef.createUser(enteredEmail, enteredPass, new Firebase.ValueResultHandler<Map<String, Object>>() {
                                                                @Override
                                                                public void onSuccess(Map<String, Object> result) {
                                                                    Log.i(LoginScreenActivity.TAG, "Firebase user sign-up success.");
                                                                    Log.i(LoginScreenActivity.TAG, "Successfully created user account with uid: " + result.get("uid"));


                                                                    LoginScreenActivity.myFirebaseRef.authWithPassword(enteredEmail, enteredPass, new Firebase.AuthResultHandler() {
                                                                        @Override
                                                                        public void onAuthenticated(AuthData authData) {
                                                                            Log.i(LoginScreenActivity.TAG, "Firebase authentication success.");
                                                                            Log.i(LoginScreenActivity.TAG, "User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());

                                                                            createUserRecord(authData, enteredEmail, "Fifko", 19);
                                                                            Intent myIntent = new Intent(CreateAccountActivity.this, NavDrawerActivity.class);
                                                                            CreateAccountActivity.this.startActivity(myIntent);

                                                                            finish();
                                                                        }

                                                                        @Override
                                                                        public void onAuthenticationError(FirebaseError firebaseError) {
                                                                            // there was an error
                                                                            Toast.makeText(getApplicationContext(),
                                                                                    firebaseError.getMessage(), Toast.LENGTH_LONG).show();

                                                                            Log.i(LoginScreenActivity.TAG, "Firebase authentication error.");
                                                                            Log.i(LoginScreenActivity.TAG, firebaseError.getMessage());
                                                                        }
                                                                    });

                                                                }

                                                                @Override
                                                                public void onError(FirebaseError firebaseError) {
                                                                    // there was an error
                                                                    Toast.makeText(getApplicationContext(),
                                                                            firebaseError.getMessage(), Toast.LENGTH_LONG).show();

                                                                    Log.i(LoginScreenActivity.TAG, "Firebase user sign-up error.");
                                                                    Log.i(LoginScreenActivity.TAG, firebaseError.getMessage());
                                                                }
                                                            }


                                                    );
                                                }
                                            }
                                        }

        );

    }

    static void createUserRecord(AuthData authData, String email, String name, int age) {
        userAuthData = authData;

        LoginScreenActivity.myFirebaseRef.child("ListOfUsers").child(authData.getUid()).setValue("");
        LoginScreenActivity.myFirebaseRef.child("ListOfUsers").child(authData.getUid()).child("email").setValue(email);
        LoginScreenActivity.myFirebaseRef.child("ListOfUsers").child(authData.getUid()).child("name").setValue(name);
        LoginScreenActivity.myFirebaseRef.child("ListOfUsers").child(authData.getUid()).child("age").setValue(age);

    }

}
