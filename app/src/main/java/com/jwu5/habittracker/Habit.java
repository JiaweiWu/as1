package com.jwu5.habittracker;

import java.io.Closeable;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by Jiawei on 9/26/2016.
 * - Main habit class, holds all relevant information
 * including days which the habit should repeat and also
 * its completion log
 */
public class Habit implements Serializable{
    private Calendar completionDate;
    private String habitTitle;
    private String habitDescription;
    private int completionAmountTotal;
    private HashMap<Integer, Boolean> repeatDays;
    private LinkedHashMap<String, Calendar> completionLogAll;
    private ArrayList<String> completionLog;
    private Calendar lastCompletionDate;
    private String completingString;

    public Habit () {
        completionAmountTotal = 0;

        completionLog = new ArrayList<String>();
        completionLogAll = new LinkedHashMap<String, Calendar>();

        repeatDays = new HashMap<Integer, Boolean>();
        repeatDays.put(Calendar.MONDAY, false);
        repeatDays.put(Calendar.TUESDAY, false);
        repeatDays.put(Calendar.WEDNESDAY, false);
        repeatDays.put(Calendar.THURSDAY, false);
        repeatDays.put(Calendar.FRIDAY, false);
        repeatDays.put(Calendar.SATURDAY, false);
        repeatDays.put(Calendar.SUNDAY, false);
    }

    public Calendar getCompletionDate() {
        return completionDate;
    }

    public String getHabitTitle() {
        return habitTitle;
    }

    public String getHabitDescription() {
        return habitDescription;
    }

    public int getCompletionAmountTotal() {
        return completionAmountTotal;
    }

    public HashMap<Integer, Boolean> getRepeatDays() {
        return repeatDays;
    }

    public Calendar getLastCompletionDate() {
        return lastCompletionDate;
    }

    public ArrayList<String> getCompletionLog() {
        return completionLog;
    }

    public String getCompletingString() {
        return completingString;
    }


    public void setCompletionDate(Calendar completionDate) {
        this.completionDate = completionDate;
    }

    public void setHabitTitle(String habitTitle) {
        this.habitTitle = habitTitle;
    }

    public void setHabitDescription(String habitDescription) {
        this.habitDescription = habitDescription;
    }

    public void completeHabit(SimpleDateFormat sdf) {
        lastCompletionDate = Calendar.getInstance();
        completingString = "Habit: " + this.getHabitTitle() + System.getProperty("line.separator") + "Completion Date: " + sdf.format(lastCompletionDate.getTime())
                + System.getProperty("line.separator") + "Unique ID:" + UUID.randomUUID().toString();
        completionLogAll.put(completingString, lastCompletionDate);
        completionLog.add(completingString);
        updateCompletionCount();
    }

    public void setRepeatDays(HashMap<Integer, Boolean> repeatDays) {
        this.repeatDays = repeatDays;
    }

    public void setCompletionAmountTotal(int completionAmountTotal) {
        this.completionAmountTotal = completionAmountTotal;
    }

    public void updateCompletionCount() {
        this.completionAmountTotal = completionLog.size();
    }

    public LinkedHashMap<String, Calendar> getCompletionLogAll() {
        return completionLogAll;
    }

    public void removeCompletionLogEntry(int pos) {
        completionLogAll.remove(completionLog.get(pos));
        completionLog.remove(pos);
        updateCompletionCount();
    }

    public void setCompletionLogAll(LinkedHashMap<String, Calendar> completionLogAll) {
        this.completionLogAll = completionLogAll;
    }

    public void setCompletionLog(ArrayList<String> completionLog) {
        this.completionLog = completionLog;
    }

    public void setLastCompletionDate(Calendar lastCompletionDate) {
        this.lastCompletionDate = lastCompletionDate;
    }

    public void setCompletingString(String completingString) {
        this.completingString = completingString;
    }

}
