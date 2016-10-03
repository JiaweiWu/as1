package com.jwu5.habittracker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Jiawei on 9/30/2016.
 * Adapter used to populate main activity with habits.
 * Taken from:
 * https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
 */
public class HabitAdapter extends ArrayAdapter<Habit> {

    private SimpleDateFormat sdf;
    private SimpleDateFormat sdfRepeatDays;
    private Context mContext;

    public HabitAdapter(Context context, ArrayList<Habit> habitArraylist) {
        super(context, 0, habitArraylist);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Habit habit = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);
        }
        sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Mountain"));
        String[] dayOfWeekArray = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};

        //Initialize all textviews and buttons
        TextView habitTitle = (TextView) convertView.findViewById(R.id.habit_item_title);
        TextView habitDescription = (TextView) convertView.findViewById(R.id.habit_item_description);
        TextView habitDate = (TextView) convertView.findViewById(R.id.habit_item_date);
        TextView habitCompletions = (TextView) convertView.findViewById(R.id.habit_item_completion);
        TextView habitRepeatDate = (TextView) convertView.findViewById(R.id.habit_item_repeat);
        Button mCompleteHabit = (Button) convertView.findViewById(R.id.completion_button);
        Calendar c = Calendar.getInstance();

        //Sets the text according to habit properties
        habitTitle.setText("Habit: " + habit.getHabitTitle());
        habitTitle.setTextColor(Color.parseColor("#FFFFFF"));
        habitDescription.setText("Description: " + habit.getHabitDescription());
        habitDate.setText("Date: "+ sdf.format(habit.getCompletionDate().getTime()));
        habitCompletions.setText("Total Completions: " + habit.getCompletionAmountTotal());

        //Sets the text according to habit repeat days
        String repeatDaysString = "";
        for(int repeatDays : habit.getRepeatDays().keySet()) {
            if (habit.getRepeatDays().get(repeatDays)) {
                repeatDaysString += dayOfWeekArray[repeatDays-1] + " ";
            }
        }
        habitRepeatDate.setText("Repeats :" + repeatDaysString);

        //Initialize event listener for complete habit button
        mCompleteHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                habit.completeHabit(sdf);
                MainActivity activity = (MainActivity) mContext;
                activity.saveInFile();
                notifyDataSetChanged();
            }
        });

        //If a habit is completed today, change the color of habit text to green
        //to indicate that the habit was completed today.
        //If today's completion was deleted, color will revert back to white
        for(Calendar date: habit.getCompletionLogAll().values()) {
            if(date != null) {
                if((date.get(Calendar.DAY_OF_YEAR) == c.get(Calendar.DAY_OF_YEAR))
                        &&(date.get(Calendar.YEAR) == c.get(Calendar.YEAR))) {
                    habitTitle.setTextColor(Color.parseColor("#71ff38"));
                }
                else {
                    habitTitle.setTextColor(Color.parseColor("#FFFFFF"));
                }
            }

        }
        notifyDataSetChanged();
        return convertView;
    }

}
