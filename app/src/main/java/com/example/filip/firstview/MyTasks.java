package com.example.filip.firstview;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

/**
 * Created by filip on 07/04/2016.
 */
public class MyTasks extends Fragment {public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ViewGroup myView = (ViewGroup) inflater.inflate(R.layout.other_fragment, null);

    Button myTasks = (Button) myView.findViewById(R.id.myTasks);
    myTasks.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ApplicationMain.myFirebaseRef.child("Groups").child("group1").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.i(LoginScreenActivity.TAG, "child added");
                    myData = dataSnapshot;
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    myData = dataSnapshot;
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    myData = dataSnapshot;
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    myData = dataSnapshot;
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.i(LoginScreenActivity.TAG, firebaseError.getMessage());
                }
            });
        }
    });
    return myView;
}
static DataSnapshot myData;

    public static Fragment newInstance(Context context) {
        MyTasks instance = new MyTasks();

        return instance;
    }
}
