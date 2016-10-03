package com.jwu5.habittracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Jiawei on 9/30/2016.
 * - Custom list class that helps with user defined views
 * of the habit (Today or all habits). Generates a new view on the fly
 *
 * - allHabitList is the arraylist with all habits whereas viewHabitList
 * is the generated list view depending on the user's preference
 *
 */
public class HabitList {

    private ArrayList<Habit> allHabitList;
    private ArrayList<Habit> viewHabitList;
    private Calendar date;
    private boolean EVERY_DAY;

    public HabitList(){

        allHabitList = new ArrayList<Habit>();
        viewHabitList = new ArrayList<Habit>();
        this.date = Calendar.getInstance();
        EVERY_DAY = true;
    }

    public void addHabittoList (Habit habit) {
        allHabitList.add(habit);
    }

    public void removeHabit(Habit habit) {
        allHabitList.remove(habit);
    }

    public void setDAY_OF_WEEK(Calendar date) {
        this.date = date;
    }

    public void setEVERY_DAY(boolean EVERY_DAY) {
        this.EVERY_DAY = EVERY_DAY;
    }

    public void setAllHabitList(ArrayList<Habit> allHabitList) {
        this.allHabitList = allHabitList;
    }

    public ArrayList<Habit> getAllHabitList() {
        return allHabitList;
    }
    public ArrayList<Habit> getViewList() {
        return viewHabitList;
    }

    public HabitList generateHabitList () {

        //Clears the new list view
        viewHabitList.clear();

        //Loops through each habit, IF the habit is suppose to repeat today AND it can occur today, it will be
        //added to the new list view (If the user wants today's habit that is)
        for (Habit habit : allHabitList) {
            habit.updateCompletionCount();
            Boolean repeatDay = habit.getRepeatDays().get(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
            Calendar completionDate = habit.getCompletionDate();
            Calendar temp = Calendar.getInstance();
            if (EVERY_DAY) {
                viewHabitList.add(habit);
            }
            else if ((repeatDay || completionDate.get(Calendar.DAY_OF_WEEK) == temp.get(Calendar.DAY_OF_WEEK)) && temp.after(completionDate)){
                viewHabitList.add(habit);
            }
        }

        //Sorts the habits according to date
        Collections.sort(viewHabitList, new Comparator<Habit>() {
            @Override
            public int compare(Habit lhs, Habit rhs) {
                return lhs.getCompletionDate().compareTo(rhs.getCompletionDate());
            }
        });

        //Allows for chaining of methods
        return this;
    }
}
