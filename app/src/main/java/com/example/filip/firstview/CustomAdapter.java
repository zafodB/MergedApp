package com.example.filip.firstview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.List;

/**
 * Created by filip on 09/04/2016.
 */

class CustomAdapter extends ArrayAdapter<Task> {

    private CheckBox rowCheckBox;
    private final String groupId;
    private Firebase localRef;

    public CustomAdapter(Context context, List<Task> taskData, String groupId) {
        super(context, R.layout.task_details_row, taskData);
        this.groupId = groupId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        localRef = ApplicationMain.getFirebaseRef();

        LayoutInflater myInflater = LayoutInflater.from(getContext());
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
                if (getItem(position).isDone()) {
                    rowCheckBox.setChecked(false);
                    localRef.child("Groups").child(groupId).child(getItem(position).getId()
                            .toString()).child("isDone").setValue("false");
                } else {
                    rowCheckBox.setChecked(true);
                    localRef.child("Groups").child(groupId).child(getItem(position).getId()
                            .toString()).child("isDone").setValue("true");
                }
            }

        });

        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "You clicked " + getItem(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return customView;
    }
}
