package com.zafodB.PhoToDo;

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

/**
 * Created by filip on 07/04/2016.
 */
public class EditTaskActivity extends AppCompatActivity {

    private final static int TASK_NAME_MAX_LENGTH = 100;

    private final static Calendar myCal = Calendar.getInstance();
    private static int hour = myCal.get(Calendar.HOUR_OF_DAY);
    private static int minute = myCal.get(Calendar.MINUTE);
    private static int year = myCal.get(Calendar.YEAR);
    private static int month = myCal.get(Calendar.MONTH) + 1;
    private static int day = myCal.get(Calendar.DAY_OF_MONTH);
    private static String dateText;
    private static String timeText;

    private static TextView dateInput;
    private static TextView timeInput;
    private TextView taskNameInput;
    private Spinner pickGroup;
    private Firebase localRef;
    private boolean isEdit;
    private String uuid;
    private String group;
    private final Map<String, Integer> groupMap = new HashMap();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task_activity);

        localRef = ApplicationMain.getFirebaseRef();

        dateInput = (TextView) findViewById(R.id.dateInput);
        timeInput = (TextView) findViewById(R.id.timeInput);
        taskNameInput = (TextView) findViewById(R.id.taskNameInput);
        pickGroup = (Spinner) findViewById(R.id.spinner);

        addGroupsToSpinner();


        Bundle myBundle = getIntent().getExtras();
        isEdit = (boolean) myBundle.get("edit");
        uuid = (String) myBundle.get("uuid");

        if (isEdit) {
            taskNameInput.setText(myBundle.getString("name"));
            hour = myBundle.getInt("hour");
            minute = myBundle.getInt("minute");
            year = myBundle.getInt("year");
            month = myBundle.getInt("month");
            day = myBundle.getInt("day");
            group = myBundle.getString("group");

        } else {
            hour = myCal.get(Calendar.HOUR_OF_DAY);
            minute = myCal.get(Calendar.MINUTE);
            year = myCal.get(Calendar.YEAR);
            month = myCal.get(Calendar.MONTH);
            day = myCal.get(Calendar.DAY_OF_MONTH);
            group = getString(R.string.my_tasks_name);
        }

        pickGroup.setSelection(groupMap.get(group));

        Button setDateButton = (Button) findViewById(R.id.setDateButton);
        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(getCurrentFocus());
                setDateText(day, month, year);
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

        Button addTaskBtn = (Button) findViewById(R.id.add_task_btn);
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        Button deleteTaskBtn = (Button) findViewById(R.id.delete_task_btn);
        if (isEdit) {
            deleteTaskBtn.setText(getString(R.string.delete_btn_text));
        }

        deleteTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask();
            }
        });


        setDateText(day, month, year);
        setTimeText(minute, hour);


        updateTimeDate();

    }

    private void addTask() {
        if (taskNameInput.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.task_missing_name), Toast
                    .LENGTH_SHORT).show();
        } else if (taskNameInput.getText().length() > TASK_NAME_MAX_LENGTH) {
            Toast.makeText(getApplicationContext(), getString(R.string.task_name_long), Toast.LENGTH_SHORT)
                    .show();
        } else {
            String taskName = taskNameInput.getText().toString();

            Map<String, String> map = new HashMap<>();
            map.put("name", taskName);
            map.put("dateDay", String.valueOf(day));
            map.put("dateMonth", String.valueOf(month));
            map.put("dateYear", String.valueOf(year));
            map.put("responsible", "names");
            map.put("isDone", "false");
            map.put("isDoubleChecked", "false");

            String selectedGroup = pickGroup.getSelectedItem().toString();

            if (selectedGroup.equals(getString(R.string.my_tasks_name))) {
                localRef.child("ListOfUsers").child(ApplicationMain.getUserAuthData().getUid
                        ()).child("inGroups").child(getString(R.string.my_tasks_name)).child(uuid).setValue(map);
            } else {
                localRef.child("Groups").child(selectedGroup).child(uuid).setValue(map);
            }

            setResult(groupMap.get(selectedGroup));

            finish();
        }
    }

    private void deleteTask() {
        String selectedGroup = pickGroup.getSelectedItem().toString();

        if (isEdit) {
            if (selectedGroup.equals(getString(R.string.my_tasks_name))) {
                localRef.child("ListOfUsers").child(ApplicationMain.getUserAuthData().getUid
                        ()).child("inGroups").child(getString(R.string.my_tasks_name)).child(uuid).setValue("a");
                localRef.child("ListOfUsers").child(ApplicationMain.getUserAuthData().getUid
                        ()).child("inGroups").child(getString(R.string.my_tasks_name)).child(uuid).removeValue();
            } else {
                localRef.child("Groups").child(selectedGroup).child(uuid).setValue("a");
                localRef.child("Groups").child(selectedGroup).child(uuid).removeValue();
            }
            finish();
        } else {

            finish();
        }
    }

    private void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
        timeInput.setText(EditTaskActivity.timeText);
    }

    private void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void addGroupsToSpinner() {
        int i = 0;
        groupMap.clear();
        for (String s : ApplicationMain.getUserGroups()) {
            groupMap.put(s, i);
            i++;
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_dropdown_item, ApplicationMain.getUserGroups());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pickGroup.setAdapter(spinnerAdapter);
    }

    public static void updateTimeDate() {
        dateInput.setText(EditTaskActivity.dateText);
        timeInput.setText(EditTaskActivity.timeText);
    }

    public static void setTimeText(int minute, int hour) {
        if (minute < 10) {
            timeText = (hour + ":0" + minute);
        } else
            timeText = (hour + ":" + minute);
    }

    public static void setDateText(int day, int month, int year) {
        dateText = (day + ". " + month + ". " + year);
    }

    public static void setDay(int day) {
        EditTaskActivity.day = day;
    }

    public static void setHour(int hour) {
        EditTaskActivity.hour = hour;
    }

    public static void setMinute(int minute) {
        EditTaskActivity.minute = minute;
    }

    public static void setMonth(int month) {
        EditTaskActivity.month = month;
    }

    public static void setYear(int year) {
        EditTaskActivity.year = year;
    }


}

