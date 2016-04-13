package com.example.filip.firstview;

import java.util.Calendar;
import java.util.UUID;

/**
 * Created by filip on 08/04/2016.
 */
public class Task {

    UUID id;
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
        id = UUID.randomUUID();

        final Calendar myCal = Calendar.getInstance();
        dueDay = myCal.get(Calendar.DAY_OF_MONTH);
        dueMonth = myCal.get(Calendar.MONTH);
        dueYear = myCal.get(Calendar.YEAR);
        dueMinute = myCal.get(Calendar.MINUTE);
        dueHour = myCal.get(Calendar.HOUR_OF_DAY);

        isDone = false;
        whoDidIt = -1;
        isDoubleChecked = false;
        whoIsResponsible = null;

    }

//    public Task(String name){
//        this.name = name;
//
//        final Calendar myCal = Calendar.getInstance();
//        dueDay = myCal.get(Calendar.DAY_OF_MONTH);
//        dueMonth = myCal.get(Calendar.MONTH);
//        dueYear = myCal.get(Calendar.YEAR);
//        dueMinute = myCal.get(Calendar.MINUTE);
//        dueHour = myCal.get(Calendar.HOUR_OF_DAY);
//
//        isDone = false;
//    }

    public UUID getId() {
        return id;
    }

    public Task(String name, int day, int month, int year, UUID uuid){
        if (uuid == null) {
            id = UUID.randomUUID();
        } else
            id = uuid;

        this.name = name;

        dueDay = day;
        dueMonth = month;
        dueYear = year;
        dueMinute = 1;
        dueHour = 1;

        isDone = false;
        whoDidIt = -1;
        isDoubleChecked = false;
    }

    public Task(String name, int day, int month, int year, int minute, int hour, int[] responsible){
        id = UUID.randomUUID();
        this.name = name;

        dueDay = day;
        dueMonth = month;
        dueYear = year;
        dueMinute = minute;
        dueHour = hour;

        isDone = false;
        whoDidIt = -1;
        isDoubleChecked = false;

        whoIsResponsible = responsible;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isDoubleChecked() {
        return isDoubleChecked;
    }

    public void setDoubleChecked(boolean doubleChecked) {
        isDoubleChecked = doubleChecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWhoDidIt() {
        return whoDidIt;
    }

    public void setWhoDidIt(int whoDidIt) {
        this.whoDidIt = whoDidIt;
    }

    public int[] getWhoIsResponsible() {
        return whoIsResponsible;
    }

    public void setWhoIsResponsible(int[] whoIsResponsible) {
        this.whoIsResponsible = whoIsResponsible;
    }

    public int getDueDay() {
        return dueDay;
    }

    public void setDueDay(int dueDay) {
        this.dueDay = dueDay;
    }

    public int getDueHour() {
        return dueHour;
    }

    public void setDueHour(int dueHour) {
        this.dueHour = dueHour;
    }

    public int getDueMinute() {
        return dueMinute;
    }

    public void setDueMinute(int dueMinute) {
        this.dueMinute = dueMinute;
    }

    public int getDueMonth() {
        return dueMonth;
    }

    public void setDueMonth(int dueMonth) {
        this.dueMonth = dueMonth;
    }

    public int getDueYear() {
        return dueYear;
    }

    public void setDueYear(int dueYear) {
        this.dueYear = dueYear;
    }
}
