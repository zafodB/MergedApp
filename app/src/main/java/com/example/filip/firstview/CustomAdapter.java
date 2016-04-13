package com.example.filip.firstview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;

import java.util.List;
import java.util.Map;

/**
 * Created by filip on 09/04/2016.
 */

class CustomAdapter extends ArrayAdapter<DataSnapshot> {



    public CustomAdapter(Context context, List<DataSnapshot> taskData) {
        super(context, R.layout.task_details_row, taskData);
        Log.i(LoginScreenActivity.TAG, "adapter created");
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater myInflater = LayoutInflater.from(getContext());
        View customView = myInflater.inflate(R.layout.task_details_row, parent, false);

        DataSnapshot taskDetails = getItem(position);

//        taskDetails.

        TextView rowTaskName = (TextView) customView.findViewById(R.id.rowTaskName);
        rowTaskName.setText("ggg");

//        TextView rowTaskDate = (TextView) customView.findViewById(R.id.rowTaskDate);
//        TextView rowTaskResponsible = (TextView) customView.findViewById(R.id.rowTaskResponsible);
//        CheckBox rowCheckBox = (CheckBox) customView.findViewById(R.id.rowCheckBox);

        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext() , "You clicked "+getItem(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return customView;
    }
}
