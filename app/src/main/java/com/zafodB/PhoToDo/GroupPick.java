package com.zafodB.PhoToDo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by filip on 19/04/2016.
 */

public class GroupPick extends AppCompatActivity {

    private Button createGroup;
    private EditText groupNameInput;

    private final List<String> allGroups = new ArrayList<>();
    private boolean joinGroup = false;

    private Firebase localRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_pick);
        localRef = ApplicationMain.getFirebaseRef();

        loadAllGroups();

//        for (String s : ApplicationMain.getUserGroups()){
//            Log.i(ApplicationMain.LOGIN_TAG, s);
//        }

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
                    createGroup.setText(getString(R.string.group_join_button));
                    joinGroup = true;
                } else {
                    createGroup.setText(getString(R.string.group_create_button));
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
                        localRef.child("ListOfUsers").child(ApplicationMain.getUserAuthData().getUid
                                ()).child("inGroups").child(groupName).setValue(groupName);

                        localRef.child("Groups").child(groupName).setValue("0");
//                        ApplicationMain.addToUserGroups(groupName);
                        finish();
                    }
                } else {
                    //Join existing group
                    localRef.child("ListOfUsers").child(ApplicationMain.getUserAuthData().getUid
                            ()).child("inGroups").child(groupNameInput.getText().toString()).setValue(groupNameInput
                            .getText().toString());
                    finish();
                }
            }
        });
    }

    private void loadAllGroups() {
        localRef.child("Groups").addValueEventListener(new ValueEventListener() {
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
