package com.example.filip.firstview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by filip on 09/04/2016.
 */

class CustomAdapter extends ArrayAdapter<Task> {


    public CustomAdapter(Context context, List<Task> taskData) {
        super(context, R.layout.task_details_row, taskData);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater myInflater = LayoutInflater.from(getContext());
        View customView = myInflater.inflate(R.layout.task_details_row, parent, false);

        String taskName = getItem(position).getName();
        String taskDate = getItem(position).getDueDay() + "." + getItem(position).getDueMonth() + "." + getItem(position).getDueYear();


        TextView rowTaskName = (TextView) customView.findViewById(R.id.rowTaskName);
        TextView rowTaskDate = (TextView) customView.findViewById(R.id.rowTaskDate);
//        TextView rowTaskResponsible = (TextView) customView.findViewById(R.id.rowTaskResponsible);
        final CheckBox rowCheckBox = (CheckBox) customView.findViewById(R.id.rowCheckBox);

        if (getItem(position).isDone()) {
            rowCheckBox.setChecked(true);
        } else rowCheckBox.setChecked(false);

        rowTaskName.setText(taskName);
        rowTaskDate.setText(taskDate);

        rowCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LoginScreenActivity.TAG, String.valueOf(rowCheckBox.isChecked()));
                if (rowCheckBox.isChecked()) {
                    rowCheckBox.setChecked(true);
                    ApplicationMain.myFirebaseRef.child("Groups").child("group1").child(getItem(position).getId().toString()).child("isDone").setValue("true");
                }else {
                    rowCheckBox.setChecked(false);
                    ApplicationMain.myFirebaseRef.child("Groups").child("group1").child(getItem(position).getId().toString()).child("isDone").setValue("false");
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
