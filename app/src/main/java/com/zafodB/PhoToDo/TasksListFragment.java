package com.zafodB.PhoToDo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by filip on 07/04/2016.
 */


public class TasksListFragment extends ListFragment {

    private static ArrayAdapter myAdapter;
    private static List<Task> tasks = new ArrayList<>();

    private TextView listEmpty;
    private String groupId;
    private Firebase localRef;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup myView = (ViewGroup) inflater.inflate(R.layout.task_list_fragment, null);
        listEmpty = (TextView) myView.findViewById(R.id.empty_list_message);

        return myView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        localRef = ApplicationMain.getFirebaseRef();
        myAdapter = new CustomAdapter(getContext(), tasks, groupId, getString(R.string.my_tasks_name));

        loadUpContent();

        setListAdapter(myAdapter);

        super.onActivityCreated(savedInstanceState);
    }

    public static Fragment newInstance(String groupId) {
        TasksListFragment instance = new TasksListFragment();

        instance.groupId = groupId;

        return instance;
    }

    private void loadUpContent() {
        if (groupId.equals(getString(R.string.my_tasks_name))) {
            localRef.child("ListOfUsers").child(ApplicationMain.getUserAuthData().getUid()).child("inGroups").child
                    (getString(R.string.my_tasks_name)).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    loadUpTasks(dataSnapshot);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        } else {
            localRef.child("Groups").child(groupId).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    loadUpTasks(dataSnapshot);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    private void loadUpTasks(DataSnapshot dataSnapshot) {
        tasks.clear();
        try {
            for (DataSnapshot i : dataSnapshot.getChildren()) {
                int day = i.child("dateDay").getValue(Integer.class);
                int month = i.child("dateMonth").getValue(Integer.class);
                int year = i.child("dateYear").getValue(Integer.class);
                String name = i.child("name").getValue(String.class);
                boolean isDone = i.child("isDone").getValue(String.class).equals("true");
                boolean isDoubleChecked = i.child("isDoubleChecked").getValue(String.class).equals("true");

                UUID uuid = UUID.fromString(i.getKey());

                if (tasks.isEmpty()) {
                    tasks.add(new Task(name, day, month, year, uuid, isDone, isDoubleChecked));
                } else {
                    int pos = 0;
                    for (Task t : tasks) {
                        if (t.getDate() > getDate(year, month, day)) {
                            break;
                        }

                        pos++;
                    }
                    tasks.add(pos, new Task(name, day, month, year, uuid, isDone, isDoubleChecked));
                }

            }
            myAdapter.notifyDataSetChanged();

        } catch (NullPointerException e) {
            Log.i(ApplicationMain.LOGIN_TAG, "firebase too late");
        }

        if (tasks.isEmpty()) {
            listEmpty.setVisibility(View.VISIBLE);
        } else
            listEmpty.setVisibility(View.GONE);
    }

    public static void setTasks(List<Task> tasks) {
        TasksListFragment.tasks = tasks;
    }

    private int getDate(int dueYear, int dueMonth, int dueDay) {
        String date;

        if (dueMonth < 10) {
            if (dueDay < 10) {
                date = String.valueOf(dueYear) + "0" + String.valueOf(dueMonth) + "0" + String.valueOf(dueDay);
            } else {
                date = String.valueOf(dueYear) + "0" + String.valueOf(dueMonth) + String.valueOf(dueDay);
            }
        } else {
            if (dueDay < 10) {
                date = String.valueOf(dueYear) + String.valueOf(dueMonth) + "0" + String.valueOf(dueDay);
            } else {
                date = String.valueOf(dueYear) + String.valueOf(dueMonth) + String.valueOf(dueDay);
            }
        }

//        Log.i(LoginScreenActivity.LOGIN_TAG,date);
        return Integer.parseInt(date);
    }
}








