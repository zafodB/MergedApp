package com.example.filip.firstview;

import android.app.ProgressDialog;
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

    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText age;
    private ProgressDialog dialog;

    private CheckBox terms;
    private String enteredName;
    private String enteredEmail;
    private String enteredPass;
    private String enteredConfirmPass;
    private int enteredAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_activity);

        name = (EditText) findViewById(R.id.signUpName);
        email = (EditText) findViewById(R.id.singUpEmail);
        password = (EditText) findViewById(R.id.signUpPassword);
        age = (EditText) findViewById(R.id.signUpAge);
        confirmPassword = (EditText) findViewById(R.id.signUpPassConfirm);
        terms = (CheckBox) findViewById(R.id.iAccept);

        setUpLoadingDialog();

        Button signUpButton = (Button) findViewById(R.id.CreateAccButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                enteredName = name.getText().toString();
                                                enteredEmail = email.getText().toString();
                                                enteredPass = password.getText().toString();
                                                enteredConfirmPass = confirmPassword.getText().toString();

                                                //Check input for wrongly added data.
                                                if (enteredName.equals("")) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Enter a name.", Toast.LENGTH_LONG).show();

                                                } else if (enteredEmail.equals("")) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Enter an e-mail.", Toast.LENGTH_LONG).show();

                                                } else if (age.getText().toString().equals("")) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Enter age.", Toast.LENGTH_LONG).show();

                                                } else if (enteredPass.equals("")) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Enter a password.", Toast.LENGTH_LONG).show();

                                                } else if (enteredConfirmPass.equals("")) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Repeat a password.", Toast.LENGTH_LONG).show();

                                                } else if (!enteredPass.equals(enteredConfirmPass)) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Passwords do not match.", Toast.LENGTH_LONG).show();

                                                } else if (!terms.isChecked()) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "The terms must be accepted.", Toast.LENGTH_LONG).show();

                                                } else {
                                                    enteredAge = Integer.parseInt(age.getText().toString());

                                                    dialog.show();
                                                    ApplicationMain.getFirebaseRef().createUser(enteredEmail,
                                                            enteredPass, new Firebase.ValueResultHandler<Map<String,
                                                                    Object>>() {
                                                                @Override
                                                                public void onSuccess(Map<String, Object> result) {
                                                                    Log.i(ApplicationMain.LOGIN_TAG, "Firebase user " +
                                                                            "sign-up success.");
                                                                    Log.i(ApplicationMain.LOGIN_TAG, "Successfully " +
                                                                            "created user account with uid: " +
                                                                            result.get("uid"));


                                                                    ApplicationMain.getFirebaseRef().authWithPassword
                                                                            (enteredEmail, enteredPass, new Firebase
                                                                                    .AuthResultHandler() {
                                                                                @Override
                                                                                public void onAuthenticated(AuthData
                                                                                                                    authData) {


                                                                                    Log.i(ApplicationMain.LOGIN_TAG,
                                                                                            "Firebase " +
                                                                                                    "authentication " +
                                                                                                    "success.");
                                                                                    Log.i(ApplicationMain.LOGIN_TAG,
                                                                                            "User ID: " +
                                                                                                    "" + authData
                                                                                                    .getUid() +
                                                                                                    ", " +
                                                                                                    "Provider: " +
                                                                                                    authData
                                                                                                            .getProvider());

                                                                                    createUserRecord(authData.getUid(),
                                                                                            enteredEmail,
                                                                                            enteredName, enteredAge);
                                                                                    ApplicationMain.setUserAuthData
                                                                                            (authData);

                                                                                    setResult(3);
                                                                                    finish();
                                                                                    dialog.hide();
                                                                                }

                                                                                @Override
                                                                                public void onAuthenticationError
                                                                                        (FirebaseError firebaseError) {
                                                                                    // there was an error
                                                                                    Toast.makeText
                                                                                            (getApplicationContext(),
                                                                                                    firebaseError
                                                                                                            .getMessage
                                                                                                                    (), Toast
                                                                                                            .LENGTH_LONG)
                                                                                            .show();

                                                                                    Log.i(ApplicationMain.LOGIN_TAG,
                                                                                            "Firebase " +
                                                                                                    "authentication " +
                                                                                                    "error.");
                                                                                    Log.i(ApplicationMain.LOGIN_TAG,
                                                                                            firebaseError.getMessage());
                                                                                    dialog.hide();
                                                                                }
                                                                            });

                                                                }

                                                                @Override
                                                                public void onError(FirebaseError firebaseError) {
                                                                    // there was an error
                                                                    Toast.makeText(getApplicationContext(),
                                                                            firebaseError.getMessage(), Toast
                                                                                    .LENGTH_LONG).show();

                                                                    Log.i(ApplicationMain.LOGIN_TAG, "Firebase user " +
                                                                            "sign-up error.");
                                                                    Log.i(ApplicationMain.LOGIN_TAG, firebaseError
                                                                            .getMessage());
                                                                    dialog.hide();
                                                                }
                                                            }


                                                    );
                                                }
                                            }
                                        }

        );

    }

    //Set up loading dialog
    private void setUpLoadingDialog() {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.loading_message));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
    }

    //Create user record inside the database. Uses AuthData to get Uid.
    static void createUserRecord(String uid, String email, String name, int age) {
        Firebase localRef = ApplicationMain.getFirebaseRef();

        localRef.child("ListOfUsers").child(uid).child("email").setValue(email);
        localRef.child("ListOfUsers").child(uid).child("name").setValue(name);
        localRef.child("ListOfUsers").child(uid).child("age").setValue(age);
        localRef.child("ListOfUsers").child(uid).child("inGroups").child("My " +
                "Tasks").setValue("My Tasks");

        ApplicationMain.addToUserGroups("My Tasks");

        Log.i(ApplicationMain.LOGIN_TAG, "Account successfully created.");
    }

}
