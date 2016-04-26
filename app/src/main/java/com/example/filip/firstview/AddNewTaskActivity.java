package com.example.filip.firstview;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by filip on 07/04/2016.
 */
public class AddNewTaskActivity extends AppCompatActivity {

    private final static Calendar myCal = Calendar.getInstance();
    private static int hour = myCal.get(Calendar.HOUR_OF_DAY);
    private static int minute = myCal.get(Calendar.MINUTE);
    private static int year = myCal.get(Calendar.YEAR);
    private static int month = myCal.get(Calendar.MONTH);
    private static int day = myCal.get(Calendar.DAY_OF_MONTH);
    private static String dateText;
    private static String timeText;

    private static TextView dateInput;
    private static TextView timeInput;
    private TextView taskNameInput;
    private Spinner pickGroup;
    private Firebase localRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_task_activity);
        localRef = ApplicationMain.getFirebaseRef();

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
                setDateText(day, month + 1, year);
                updateTimeDate();

            }
        });

        Button setTimeButton = (Button) findViewById(R.id.setTimeButton);
        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(getCurrentFocus());
                setTimeText(minute, hour);
                updateTimeDate();
            }
        });

        Button addTaskBtn = (Button) findViewById(R.id.addTaskBtn);
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskNameInput.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.task_missing_name), Toast
                            .LENGTH_SHORT).show();
                } else if (taskNameInput.getText().length() > 20) {
                    Toast.makeText(getApplicationContext(), getString(R.string.task_name_long), Toast.LENGTH_SHORT)
                            .show();
                } else {
                    String taskName = taskNameInput.getText().toString();
                    String uuid = UUID.randomUUID().toString();

                    Map<String, String> map = new HashMap<>();
                    map.put("name", taskName);
                    map.put("dateDay", String.valueOf(day));
                    map.put("dateMonth", String.valueOf(month));
                    map.put("dateYear", String.valueOf(year));
                    map.put("responsible", "names");
                    map.put("isDone", "false");
                    map.put("isDoubleChecked", "false");

                    String selectedGroup = pickGroup.getSelectedItem().toString();

                    if (selectedGroup.equals("My Tasks")) {
                        localRef.child("ListOfUsers").child(ApplicationMain.getUserAuthData().getUid
                                ()).child("inGroups").child("My Tasks").child(uuid).setValue(map);
                    } else {
                        localRef.child("Groups").child(selectedGroup).child(uuid).setValue(map);
                    }

                    finish();
                }
            }
        });


        setDateText(day, month + 1, year);
        setTimeText(minute, hour);

        updateTimeDate();

    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
        timeInput.setText(AddNewTaskActivity.timeText);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    private void addGroupsToSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_dropdown_item, ApplicationMain.getUserGroups());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pickGroup.setAdapter(spinnerAdapter);
    }

    public static void setTimeText(int minute, int hour) {
        if (minute < 10) {
            timeText = (hour + ":0" + minute);
        } else
            timeText = (hour + ":" + minute);
    }

    public static void updateTimeDate() {
        dateInput.setText(AddNewTaskActivity.dateText);
        timeInput.setText(AddNewTaskActivity.timeText);
    }

    public static void setDateText(int day, int month, int year) {
        dateText = (day + ". " + month + ". " + year);
    }

    public static void setDay(int day) {
        AddNewTaskActivity.day = day;
    }

    public static void setHour(int hour) {
        AddNewTaskActivity.hour = hour;
    }

    public static void setMinute(int minute) {
        AddNewTaskActivity.minute = minute;
    }

    public static void setMonth(int month) {
        AddNewTaskActivity.month = month;
    }

    public static void setYear(int year) {
        AddNewTaskActivity.year = year;
    }
}

