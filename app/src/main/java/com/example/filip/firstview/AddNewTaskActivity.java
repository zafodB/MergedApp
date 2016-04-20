package com.example.filip.firstview;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by filip on 07/04/2016.
 */
public class AddNewTaskActivity extends AppCompatActivity {

    final static Calendar myCal = Calendar.getInstance();
    static int hour = myCal.get(Calendar.HOUR_OF_DAY);
    static int minute = myCal.get(Calendar.MINUTE);

    static int year = myCal.get(Calendar.YEAR);
    static int month = myCal.get(Calendar.MONTH);
    static int day = myCal.get(Calendar.DAY_OF_MONTH);

    static TextView dateInput;
    static TextView timeInput;
    static TextView taskNameInput;
    Spinner pickGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_task_activity);

        dateInput = (TextView) findViewById(R.id.dateInput);
        timeInput = (TextView) findViewById(R.id.timeInput);
        taskNameInput = (TextView) findViewById(R.id.taskNameInput);
        pickGroup = (Spinner) findViewById(R.id.spinner);

        addGroupsToSpinner();

        Button setDateButton = (Button) findViewById(R.id.setDateButton);
        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(getCurrentFocus());
                setDate(day, month + 1, year);

            }
        });

        Button setTimeButton = (Button) findViewById(R.id.setTimeButton);
        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(getCurrentFocus());
                setTime(minute, hour);
            }
        });

        Button addTaskBtn = (Button) findViewById(R.id.addTaskBtn);
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(LoginScreenActivity.TAG, taskNameInput.getText().toString());
                if (taskNameInput.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "TASK IS MISSING A NAME!", Toast.LENGTH_SHORT).show();
                } else {
                    String taskName = taskNameInput.getText().toString();
                    String uuid = UUID.randomUUID().toString();

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("name", taskName);
                    map.put("dateDay", String.valueOf(day));
                    map.put("dateMonth", String.valueOf(month));
                    map.put("dateYear", String.valueOf(year));
                    map.put("responsible", "names");
                    map.put("isDone", "false");
                    map.put("isDoubleChecked", "false");

                    String selectedGroup = pickGroup.getSelectedItem().toString();

                    if (selectedGroup.equals("My Tasks")) {
                        ApplicationMain.myFirebaseRef.child("ListOfUsers").child(ApplicationMain.userAuthData.getUid
                                ()).child("inGroups").child("My Tasks").child(uuid).setValue(map);
                    } else {
                        ApplicationMain.myFirebaseRef.child("Groups").child(selectedGroup).child(uuid).setValue(map);
                    }

                    finish();
                }
            }
        });


        setDate(day, month + 1, year);
        setTime(minute, hour);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    void setTime(int minute, int hour) {
        if (minute < 10) {
            timeInput.setText(hour + ":0" + minute);
        } else
            timeInput.setText(hour + ":" + minute);
    }

    void addGroupsToSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout
                .simple_spinner_dropdown_item, ApplicationMain.userGroups);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pickGroup.setAdapter(spinnerAdapter);
    }

    public static void setDate(int day, int month, int year) {
        dateInput.setText(day + ". " + month + ". " + year);
    }

}

