package com.jwu5.habittracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Jiawei on 9/27/2016.
 */
public class HabitActivity extends Activity {

    private EditText mTitleField;
    private EditText mDescriptionField;
    private EditText mhabitDateText;
    private ToggleButton mMon;
    private ToggleButton mTue;
    private ToggleButton mWed;
    private ToggleButton mThu;
    private ToggleButton mFri;
    private ToggleButton mSat;
    private ToggleButton mSun;
    private Button mOk;
    private Button mCancel;
    private Calendar date = Calendar.getInstance();
    private SimpleDateFormat sdf;

    private Habit habit;
    private Habit habitTemp;
    private static final String NEW_HABIT = "com.jwu5.habittracker.new_habit";
    private static final String EDIT_HABIT = "com.jwu5.habittracker.edit_habit";
    private static final String EDIT_HABIT_TEMP = "com.jwu5.habittracker.edit_habit_temp";
    private static final String RETURN_NEW_HABIT = "com.jwu5.habittracker.return_new_habit";
    private static final String RETURN_EDIT_HABIT = "com.jwu5.habittracker.return_edit_habit";
    private Boolean EDIT_MODE = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit);

        //Initialize simple date format
        sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Mountain"));

        //Initialize all buttons and text fields
        mOk = (Button) findViewById(R.id.ok_button);
        mCancel = (Button) findViewById(R.id.cancel_button);
        mTitleField = (EditText) findViewById(R.id.habit_title);
        mDescriptionField = (EditText) findViewById(R.id.habit_description);
        mhabitDateText = (EditText) findViewById(R.id.habit_date);

        //Initialize toggle buttons
        mMon = (ToggleButton) findViewById(R.id.toggle_mon);
        mTue = (ToggleButton) findViewById(R.id.toggle_tue);
        mWed = (ToggleButton) findViewById(R.id.toggle_wed);
        mThu = (ToggleButton) findViewById(R.id.toggle_thu);
        mFri = (ToggleButton) findViewById(R.id.toggle_fri);
        mSat = (ToggleButton) findViewById(R.id.toggle_sat);
        mSun = (ToggleButton) findViewById(R.id.toggle_sun);

        //Create hashmap of toggle buttons for edit mode to remember
        //the user's past data
        HashMap<Integer, ToggleButton > toggleButtonHashMap = new HashMap<Integer, ToggleButton>();
        toggleButtonHashMap.put(Calendar.MONDAY, mMon);
        toggleButtonHashMap.put(Calendar.TUESDAY, mTue);
        toggleButtonHashMap.put(Calendar.WEDNESDAY, mWed);
        toggleButtonHashMap.put(Calendar.THURSDAY, mThu);
        toggleButtonHashMap.put(Calendar.FRIDAY, mFri);
        toggleButtonHashMap.put(Calendar.SATURDAY, mSat);
        toggleButtonHashMap.put(Calendar.SUNDAY, mSun);

        habit = editCreateMode();

        //Check either its edit mode or new habit mode
        if (EDIT_MODE) {
            //Set up edit mode
            habitTemp = new Habit();
            habitTemp = saveHabit(habit, habitTemp);
            editCreateModeSetup(habit, toggleButtonHashMap);
        }

        //Initialize event listeners for repeat day of the week toggle buttons
        toggleRepeatDays(mMon, Calendar.MONDAY);
        toggleRepeatDays(mTue, Calendar.TUESDAY);
        toggleRepeatDays(mWed, Calendar.WEDNESDAY);
        toggleRepeatDays(mThu, Calendar.THURSDAY);
        toggleRepeatDays(mFri, Calendar.FRIDAY);
        toggleRepeatDays(mSat, Calendar.SATURDAY);
        toggleRepeatDays(mSun, Calendar.SUNDAY);

        //Initialize date picker dialog
        //http://stackoverflow.com/a/14933515
        final DatePickerDialog.OnDateSetListener habitDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthofYear, int dayofMonth) {
                date.set(Calendar.YEAR, year);
                date.set(Calendar.MONTH, monthofYear);
                date.set(Calendar.DAY_OF_MONTH, dayofMonth);
                habit.setCompletionDate(date);
                mhabitDateText.setText(sdf.format(date.getTime()));

            }
        };

        //Sets event listener on date edittext to launch date picker dialog
        mhabitDateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    //http://stackoverflow.com/a/23762355
                    DatePickerDialog habitDatePicker = new DatePickerDialog(HabitActivity.this, habitDate, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
                    habitDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    habitDatePicker.show();
                }
            }
        });

        //Initialize OK button listener, sends back the habit object to main activity
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Set the habit title and descriptions
                habit.setHabitTitle(mTitleField.getText().toString());
                habit.setHabitDescription(mDescriptionField.getText().toString());

                //Sets default completion date in case the user does not input any completiond ate
                if(habit.getCompletionDate() == null) {
                    habit.setCompletionDate(Calendar.getInstance());
                }

                //Sends back the habit to main activity depending on request code
                Intent returnData = new Intent();
                if(habit.getHabitTitle().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Make Sure Your Habit Has a Name!", Toast.LENGTH_SHORT).show();
                }
                else if(EDIT_MODE){
                    returnData.putExtra(RETURN_EDIT_HABIT, habit);
                    setResult(RESULT_OK, returnData);
                    finish();
                }
                else {
                    returnData.putExtra(RETURN_NEW_HABIT, habit);
                    setResult(RESULT_OK, returnData);
                    finish();
                }
            }
        });

        //Cancel button event listener, closes current activity and sends back RESULT_CANCELED
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitCleanUp();
                }
        });
    }

    //Function for initializing toggle button event listeners
    public void toggleRepeatDays(ToggleButton dayofWeekToggle, final Integer DAY_OF_WEEK) {
        dayofWeekToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    habit.getRepeatDays().put(DAY_OF_WEEK, true);
                }
                else {
                    habit.getRepeatDays().put(DAY_OF_WEEK, false);
                }
            }
        });
    }

    //Adding dialog on back press: http://stackoverflow.com/a/10907411
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("Exit Create/Edit Habit").setMessage("Exit Without Saving Changes?")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitCleanUp();
                        dialog.cancel();
                    }
                }).create().show();
    }

    //Helper function for deciding between edit more and new habit mode
    //And de-serializing from the correct intent
    public Habit editCreateMode () {
        habit = (Habit) getIntent().getSerializableExtra(NEW_HABIT);
        if (habit == null){
            habit = (Habit) getIntent().getSerializableExtra(EDIT_HABIT);
            EDIT_MODE = true;
        }
        return habit;
    }

    //Function to set up edit mode by setting
    //Textviews according to user preference
    public void editCreateModeSetup (Habit habit, HashMap<Integer, ToggleButton> toggleButtonHashMap) {

        mTitleField.setText(habit.getHabitTitle());
        mDescriptionField.setText(habit.getHabitDescription());

        for (int DAY_OF_WEEK : habit.getRepeatDays().keySet()) {
            if (habit.getRepeatDays().get(DAY_OF_WEEK)) {
                toggleButtonHashMap.get(DAY_OF_WEEK).setChecked(true);
            }
        }
        mhabitDateText.setText(sdf.format(habit.getCompletionDate().getTime()));
    }

    public void exitCleanUp() {
        if(EDIT_MODE){
            Intent returnData = new Intent();
            returnData.putExtra(RETURN_EDIT_HABIT, habitTemp);
            setResult(RESULT_OK, returnData);
            finish();
        }
        else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    public Habit saveHabit(Habit habit, Habit habitTemp) {
        habitTemp.setCompletionDate(habit.getCompletionDate());
        habitTemp.setHabitTitle(habit.getHabitTitle());
        habitTemp.setHabitDescription(habit.getHabitDescription());
        habitTemp.setCompletionAmountTotal(habit.getCompletionAmountTotal());
        habitTemp.setRepeatDays(habit.getRepeatDays());
        habitTemp.setCompletionLogAll(habit.getCompletionLogAll());
        habitTemp.setCompletionLog(habit.getCompletionLog());
        habitTemp.setLastCompletionDate(habit.getLastCompletionDate());

        return habitTemp;
    }



}
