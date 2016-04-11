package com.example.filip.firstview;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_task_activity);

        dateInput = (TextView)findViewById(R.id.dateInput);

        timeInput = (TextView)findViewById(R.id.timeInput);

        Button setDateButton = (Button) findViewById(R.id.setDateButton);
        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(getCurrentFocus());
                setDate(day,month+1, year);

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


        setDate(day,month+1,year);
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

    void setTime(int minute, int hour){
        if (minute<10){
            timeInput.setText(hour + ":0"+minute);
        } else
            timeInput.setText(hour + ":"+minute);
    }

    public static void setDate(int day, int month, int year){
        dateInput.setText(day + ". "+month+". "+year);
    }

}

