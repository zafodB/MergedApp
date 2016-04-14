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

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by filip on 07/04/2016.
 */
public class AdamsFamily extends ListFragment {

    static ArrayAdapter mojAdapter;
    static List<Task> tasks = new ArrayList<Task>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup myView = (ViewGroup) inflater.inflate(R.layout.adams_family_fragment, null);

        return myView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mojAdapter = new CustomAdapter(getContext(), tasks);

        loadUpTasks();

        setListAdapter(mojAdapter);

        super.onActivityCreated(savedInstanceState);
    }

    public static Fragment newInstance(Context context) {
        AdamsFamily instance = new AdamsFamily();

        return instance;
    }

    void loadUpTasks() {
        ApplicationMain.myFirebaseRef.child("Groups").child("group1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int j = 0;
                tasks.clear();
                Log.i(LoginScreenActivity.TAG, "date chenged");

                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    j++;
                    int day = i.child("dateDay").getValue(Integer.class);
                    int month = i.child("dateMonth").getValue(Integer.class);
                    int year = i.child("dateYear").getValue(Integer.class);
                    String name = i.child("name").getValue(String.class);

                    boolean isDone;
                    boolean isDoubleChecked;
                    if (i.child("isDone").getValue(String.class).equals("true")) {
                        isDone = true;
                    } else isDone = false;

                    if (i.child("isDoubleChecked").getValue(String.class).equals("true")) {
                        isDoubleChecked = true;
                    } else isDoubleChecked = false;

                    UUID uuid = UUID.fromString(i.getKey());

                    if (tasks.isEmpty()) {
                        tasks.add(new Task(name, day, month, year, uuid, isDone, isDoubleChecked));
                    }else{
                        int pos = 0;
                        for(Task t:tasks){
                            if (t.getDate() > getDate(year,month,day)){
                                break;
                            }

                            pos++;
                        }
                        tasks.add(pos, new Task(name, day, month, year, uuid, isDone, isDoubleChecked));
                    }

                    mojAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    int getDate(int dueYear, int dueMonth, int dueDay){
        String date;

        if (dueMonth<10){
            if (dueDay<10){
                date = String.valueOf(dueYear)+"0"+String.valueOf(dueMonth)+"0"+String.valueOf(dueDay);
            }else {
                date = String.valueOf(dueYear) + "0" + String.valueOf(dueMonth) + String.valueOf(dueDay);
            }
        }else{
            if (dueDay<10){
                date = String.valueOf(dueYear)+String.valueOf(dueMonth)+"0"+String.valueOf(dueDay);
            } else {
                date = String.valueOf(dueYear) + String.valueOf(dueMonth) + String.valueOf(dueDay);
            }
        }

        Log.i(LoginScreenActivity.TAG,date);
        return Integer.parseInt(date);
    }


}
