package com.zafodB.PhoToDo;

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
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText nameET;
    private EditText emailET;
    private EditText passwordET;
    private EditText confirmPasswordET;
    private EditText ageET;
    private ProgressDialog dialog;

    private CheckBox terms;
    private String enteredName;
    private String enteredEmail;
    private String enteredPass;
    private String enteredConfirmPass;
    private int enteredAge;
    private static boolean newUser;
    private static Firebase localRef;

    private static String uid;
    private static String email;
    private static String name;
    private static int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_activity);

        nameET = (EditText) findViewById(R.id.sign_up_name);
        emailET = (EditText) findViewById(R.id.sing_up_email);
        passwordET = (EditText) findViewById(R.id.sign_up_password);
        ageET = (EditText) findViewById(R.id.sign_up_age);
        confirmPasswordET = (EditText) findViewById(R.id.sign_up_pass_confirm);
        terms = (CheckBox) findViewById(R.id.i_accept);

        setUpLoadingDialog();

        Button signUpButton = (Button) findViewById(R.id.create_acc_button);
        signUpButton.setOnClickListener
                (new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {

                         enteredName = nameET.getText().toString();
                         enteredEmail = emailET.getText().toString();
                         enteredPass = passwordET.getText().toString();
                         enteredConfirmPass = confirmPasswordET.getText().toString();

                         //Check input for wrongly added data.
                         if (enteredName.equals("")) {
                             Toast.makeText(getApplicationContext(),
                                     "Enter a name.", Toast.LENGTH_LONG).show();

                         } else if (enteredEmail.equals("")) {
                             Toast.makeText(getApplicationContext(),
                                     "Enter an e-mail.", Toast.LENGTH_LONG).show();

                         } else if (ageET.getText().toString().equals("")) {
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
                             enteredAge = Integer.parseInt(ageET.getText().toString());

                             dialog.show();
                             ApplicationMain.getFirebaseRef().createUser(enteredEmail,
                                     enteredPass, new Firebase.ValueResultHandler<Map<String,
                                             Object>>() {
                                         @Override
                                         public void onSuccess(Map<String, Object> result) {
                                             Log.i(ApplicationMain.LOGIN_TAG, "Firebase user " +
                                                     "sign-up success.");
                                             Log.i(ApplicationMain.FIREBASE_COMMUNICATION_TAG,
                                                     "Successfully " +
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
                                                             Log.i(ApplicationMain.FIREBASE_COMMUNICATION_TAG,
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
                                                             Log.i(ApplicationMain.FIREBASE_COMMUNICATION_TAG,
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
                                             Log.i(ApplicationMain.FIREBASE_COMMUNICATION_TAG, firebaseError
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
        localRef = ApplicationMain.getFirebaseRef();

        CreateAccountActivity.uid = uid;
        CreateAccountActivity.email = email;
        CreateAccountActivity.name = name;
        CreateAccountActivity.age = age;

        newUser = true;

        localRef.child("ListOfUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (d.getKey().equals(CreateAccountActivity.uid)) {
                        Log.i(ApplicationMain.FIREBASE_COMMUNICATION_TAG, d.getKey());
                        newUser = false;
                        break;
                    }
                }

                if (newUser) {
                    localRef.child("ListOfUsers").child(CreateAccountActivity.uid).child("email").setValue
                            (CreateAccountActivity.email);
                    localRef.child("ListOfUsers").child(CreateAccountActivity.uid).child("name").setValue
                            (CreateAccountActivity.name);
                    localRef.child("ListOfUsers").child(CreateAccountActivity.uid).child("age").setValue
                            (CreateAccountActivity.age);
                    localRef.child("ListOfUsers").child(CreateAccountActivity.uid).child("inGroups").child("My " +
                            "Tasks").setValue("My Tasks");

                    ApplicationMain.addToUserGroups("My Tasks");
                }


                Log.i(ApplicationMain.LOGIN_TAG, "Account successfully created.");
                Log.i(ApplicationMain.FIREBASE_COMMUNICATION_TAG, "Account created with uid: " +
                        CreateAccountActivity.uid + " and email: " + CreateAccountActivity.email);

                localRef.child("ListOfUsers").removeEventListener(this);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

}
