package com.zafodB.PhoToDo;

import java.util.Calendar;
import java.util.UUID;

/**
 * Created by filip on 08/04/2016.
 */
class Task {

    private final UUID id;
    private int dueDay;
    private int dueMonth;
    private int dueYear;
    private int dueMinute;
    private int dueHour;
    private boolean isDone;
    private int whoDidIt; //user id within the group
    private boolean isDoubleChecked;
    private int[] whoIsResponsible; //kids responsible
    private String name;

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

    public Task(String name, int day, int month, int year, UUID uuid, boolean isDone, boolean isDoubleChecked){
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

        this.isDone = isDone;
        whoDidIt = -1;
        this.isDoubleChecked = isDoubleChecked;
    }

    public UUID getId() {
        return id;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        this.isDone = done;
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

    public int getDate(){
        String date;

        if (dueMonth<10){
            if (dueDay<10){
                date = String.valueOf(dueYear)+"0"+String.valueOf(dueMonth)+"0"+String.valueOf(dueDay);
            }else {
                date = String.valueOf(dueYear) + "0" + String.valueOf(dueMonth) + String.valueOf(dueDay);
            }
        }else{
            if (dueDay<10){
                date = String.valueOf(dueYear)+String.valueOf(dueMonth)+"0"+String.valueOf(dueDay);
            } else {
                date = String.valueOf(dueYear) + String.valueOf(dueMonth) + String.valueOf(dueDay);
            }
        }

        return Integer.parseInt(date);
    }
}
