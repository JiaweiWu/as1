package com.jwu5.habittracker;

import android.test.ActivityInstrumentationTestCase2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by Jiawei on 10/2/2016.
 */
public class HabitUnitTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public HabitUnitTest() {
        super(MainActivity.class);
    }

    public void testHabitCreation () {
        Habit habit = new Habit();
        habit.setHabitTitle("Testing Setters");

        habit.getRepeatDays().put(Calendar.MONDAY, true);
        habit.getRepeatDays().put(Calendar.WEDNESDAY, true);
        assertTrue(habit.getHabitTitle().equals("Testing Setters"));
        assertTrue(habit.getRepeatDays().get(Calendar.MONDAY));
        assertTrue(!habit.getRepeatDays().get(Calendar.SATURDAY));

    }

    public void testHabitCompletion() {
        Habit habit = new Habit();
        habit.setHabitTitle("Testing Setters");

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Mountain"));

        habit.completeHabit(sdf);

        assertTrue(habit.getLastCompletionDate().get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
        assertTrue(habit.getLastCompletionDate().get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR));

        assertTrue(!habit.getCompletionLog().isEmpty());
        assertTrue(habit.getCompletionAmountTotal() == 1);

        habit.removeCompletionLogEntry(0);

        assertTrue(habit.getCompletionLog().isEmpty());
        assertTrue(habit.getCompletionAmountTotal() == 0);
    }

    public void testHabitList() {
        HabitList habitList = new HabitList();
        habitList.setEVERY_DAY(true);
        Habit habit1 = new Habit();
        Habit habit2 = new Habit();

        habit1.setHabitTitle("Habit 1");
        habit2.setHabitTitle("Habit 2");

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();
        date2.set(Calendar.DAY_OF_YEAR , 2018);

        habit1.setCompletionDate(date1);
        habit2.setCompletionDate(date2);
        habitList.addHabittoList(habit1);
        habitList.addHabittoList(habit2);

        assertTrue(habitList.generateHabitList().getViewList().size() == 2);

        habitList.setEVERY_DAY(false);

        assertTrue(habitList.generateHabitList().getViewList().size() == 1);
    }

    public void testHabitDeletions() {
        HabitList habitList = new HabitList();
        habitList.setEVERY_DAY(true);
        Habit habit1 = new Habit();
        Habit habit2 = new Habit();

        habit1.setHabitTitle("Habit 1");
        habit2.setHabitTitle("Habit 2");

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();
        date2.set(Calendar.DAY_OF_YEAR , 2018);

        habit1.setCompletionDate(date1);
        habit2.setCompletionDate(date2);
        habitList.addHabittoList(habit1);
        habitList.addHabittoList(habit2);

        habitList.removeHabit(habit1);

        assertTrue(habitList.getAllHabitList().size() == 1);
    }

}
