package com.zafodB.PhoToDo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.List;

/**
 * Created by filip on 09/04/2016.
 */

class CustomAdapter extends ArrayAdapter<Task> {

    private CheckBox rowCheckBox;
    private final String groupId;
    private Firebase localRef;
    private final String myTasksName;


    public CustomAdapter(Context context, List<Task> taskData, String groupId, String myTasksName) {
        super(context, R.layout.task_details_row, taskData);
        this.groupId = groupId;
        this.myTasksName = myTasksName;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        localRef = ApplicationMain.getFirebaseRef();

        final LayoutInflater myInflater = LayoutInflater.from(getContext());
        View customView = myInflater.inflate(R.layout.task_details_row, parent, false);

        String taskName = getItem(position).getName();
        String taskDate = getItem(position).getDueDay() + "." + getItem(position).getDueMonth() + "." + getItem
                (position).getDueYear();


        TextView rowTaskName = (TextView) customView.findViewById(R.id.rowTaskName);
        TextView rowTaskDate = (TextView) customView.findViewById(R.id.rowTaskDate);
//        TextView rowTaskResponsible = (TextView) customView.findViewById(R.id.rowTaskResponsible);
        rowCheckBox = (CheckBox) customView.findViewById(R.id.rowCheckBox);

        if (getItem(position).isDone()) {
            rowCheckBox.setChecked(true);
        }

        rowTaskName.setText(taskName);
        rowTaskDate.setText(taskDate);

        rowCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowCheckBox.setChecked(false);
                if (getItem(position).isDone()) {
                    if (groupId.equals(myTasksName)) {
                        localRef.child("ListOfUsers").child(ApplicationMain.getUserAuthData().getUid
                                ()).child("inGroups").child(myTasksName).child(getItem(position).getId()
                                .toString()).child("isDone").setValue("false");
                    } else {
                        localRef.child("Groups").child(groupId).child(getItem(position).getId()
                                .toString()).child("isDone").setValue("false");
                    }
                } else {
                    rowCheckBox.setChecked(true);
                    if (groupId.equals(myTasksName)) {
                        localRef.child("ListOfUsers").child(ApplicationMain.getUserAuthData().getUid
                                ()).child("inGroups").child(myTasksName).child(getItem(position).getId()
                                .toString()).child("isDone").setValue("true");
                    } else {
                        localRef.child("Groups").child(groupId).child(getItem(position).getId()
                                .toString()).child("isDone").setValue("true");
                    }

                }
            }

        });

        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), EditTaskActivity.class);
                myIntent.putExtra("edit", true);
                myIntent.putExtra("uuid", getItem(position).getId().toString());
                myIntent.putExtra("name", getItem(position).getName());
                myIntent.putExtra("day", getItem(position).getDueDay());
                myIntent.putExtra("month", getItem(position).getDueMonth());
                myIntent.putExtra("year", getItem(position).getDueYear());
                myIntent.putExtra("minute", getItem(position).getDueMinute());
                myIntent.putExtra("hour", getItem(position).getDueHour());
                myIntent.putExtra("group", groupId);

                getContext().startActivity(myIntent);
            }
        });


        return customView;
    }

}
