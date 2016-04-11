package com.example.filip.firstview;

import java.util.Calendar;

/**
 * Created by filip on 08/04/2016.
 */
public class Task {

    int dueDay;
    int dueMonth;
    int dueYear;
    int dueMinute;
    int dueHour;
    boolean isDone;
    int whoDidIt; //user id within the group
    boolean isDoubleChecked;
    int[] whoIsResponsible; //kids responsible

    boolean done;
    String name;

    public Task(){
        final Calendar myCal = Calendar.getInstance();
        dueDay = myCal.get(Calendar.DAY_OF_MONTH);
        dueMonth = myCal.get(Calendar.MONTH);
        dueYear = myCal.get(Calendar.YEAR);
        dueMinute = myCal.get(Calendar.MINUTE);
        dueHour = myCal.get(Calendar.HOUR_OF_DAY);
        done = false;
    }

    public Task(String name){
        this.name = name;

        final Calendar myCal = Calendar.getInstance();
        dueDay = myCal.get(Calendar.DAY_OF_MONTH);
        dueMonth = myCal.get(Calendar.MONTH);
        dueYear = myCal.get(Calendar.YEAR);
        dueMinute = myCal.get(Calendar.MINUTE);
        dueHour = myCal.get(Calendar.HOUR_OF_DAY);

        done = false;
    }

    public Task(String name, int day, int month, int year, int minute, int hour){
        this.name = name;

        final Calendar myCal = Calendar.getInstance();
        dueDay = day;
        dueMonth = month;
        dueYear = year;
        dueMinute = minute;
        dueHour = hour;

        done = false;
    }
}
