package com.example.filip.firstview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by filip on 19/04/2016.
 */

public class GroupPick extends AppCompatActivity {

    Button createGroup;
    EditText groupNameInput;

    List<String> allGroups = new ArrayList<>();
    boolean joinGroup = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_pick);
        loadAllGroups();

        createGroup = (Button) findViewById(R.id.create_group_button);

        groupNameInput = (EditText) findViewById(R.id.group_name_input);
        groupNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                boolean isThere = false;
                for (String group : allGroups) {
                    if (group.equals(s.toString())) {
                        isThere = true;
                        break;
                    }
                }

                if (isThere) {
                    createGroup.setText("JOIN");
                    joinGroup = true;
                } else {
                    createGroup.setText("CREATE");
                    joinGroup = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupNameInput.getText().length() < 3) {
                    Toast.makeText(getApplicationContext(), "The name is too short.", Toast.LENGTH_SHORT).show();
                } else if (groupNameInput.getText().length() > 25) {
                    Toast.makeText(getApplicationContext(), "The name is too short.", Toast.LENGTH_SHORT).show();
                } else if (!joinGroup) {
                    String groupName = groupNameInput.getText().toString();
                    boolean validName = true;
                    for (int i = 0; i < groupName.length(); i++) {
                        char myChar = groupName.charAt(i);
                        if (myChar < 32 || (myChar > 32 && myChar < 39) || (myChar > 39 && myChar < 48) || (myChar >
                                57 && myChar < 65) || (myChar >
                                91 && myChar < 97)) {
                            validName = false;
                            break;
                        }
                    }

                    if (!validName) {
                        Toast.makeText(getApplicationContext(), "The name is invalid.", Toast.LENGTH_SHORT).show();
                    } else {
                        ApplicationMain.myFirebaseRef.child("ListOfUsers").child(ApplicationMain.userAuthData.getUid
                                ()).child("inGroups").child(groupName).setValue(groupName);
                        ApplicationMain.myFirebaseRef.child("ListOfUsers").child(ApplicationMain.userAuthData.getUid
                                ()).child("inGroups").child("My Tasks").setValue("My Tasks");
                        ApplicationMain.myFirebaseRef.child("ListOfUsers").child(ApplicationMain.userAuthData.getUid
                                ()).child("inGroups").child("0").removeValue();
                        ApplicationMain.myFirebaseRef.child("Groups").child(groupName).setValue("0");
                        finish();
                    }
                }
            }
        });
    }

    void loadAllGroups() {
        ApplicationMain.myFirebaseRef.child("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allGroups.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    allGroups.add(d.getKey());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
