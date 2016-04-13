package com.example.filip.firstview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by filip on 07/04/2016.
 */
public class AdamsFamily extends ListFragment {

    static ArrayAdapter mojAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup myView = (ViewGroup) inflater.inflate(R.layout.adams_family_fragment, null);

        return myView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mojAdapter = new CustomAdapter(getContext(), LoginScreenActivity.tasks);

        loadUpTasks();

        setListAdapter(mojAdapter);

        super.onActivityCreated(savedInstanceState);
    }

//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        Toast.makeText(getActivity() , "launch new activity", Toast.LENGTH_LONG).show();
//        super.onListItemClick(l, v, position, id);
//    }

    public static Fragment newInstance(Context context) {
        AdamsFamily instance = new AdamsFamily();

        return instance;
    }

    void loadUpTasks() {
        ApplicationMain.myFirebaseRef.child("Groups").child("group1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int j = 0;
                LoginScreenActivity.tasks.clear();

//                Log.i(LoginScreenActivity.TAG, "Children count "+String.valueOf(dataSnapshot.getChildrenCount()));
//                Log.i(LoginScreenActivity.TAG, "WHAT THE FUCK-");
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    j++;

//                    Log.i(LoginScreenActivity.TAG, i.child("dateDay").getValue(String.class)+" tuto");
                    int day = i.child("dateDay").getValue(Integer.class);
                    int month = i.child("dateMonth").getValue(Integer.class);
                    int year = i.child("dateYear").getValue(Integer.class);
                    String name = i.child("name").getValue(String.class);

                    UUID uuid = UUID.fromString(i.getKey());

                    LoginScreenActivity.tasks.add(new Task(name, day, month, year, uuid));

//                    LoginScreenActivity.tasks.add(new Task(name, 1, 2, 34, uuid));

                    mojAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


}
