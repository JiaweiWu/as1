
/*
MIT License

Copyright (c) 2016 Jiawei Wu

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.jwu5.habittracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends Activity  {

    private Button mAddHabit;
    private static final String NEW_HABIT = "com.jwu5.habittracker.new_habit";
    private static final String EDIT_HABIT = "com.jwu5.habittracker.edit_habit";
    private static final String EDIT_HABIT_TEMP = "com.jwu5.habittracker.edit_habit_temp";
    private static final String VIEW_COMPLETION_LIST = "com.jwu5.habittracker.view_completion_list";
    private static final String RETURN_NEW_HABIT = "com.jwu5.habittracker.return_new_habit";
    private static final String RETURN_EDIT_HABIT = "com.jwu5.habittracker.return_edit_habit";
    private static final String RETURN_VIEW_COMPLETION_LIST = "com.jwu5.habittracker.return_view_completion_list";
    private static final String FILENAME = "file.sav";
    private static final int REQUEST_USER_INPUT_NEW = 0 ;
    private static final int REQUEST_USER_INPUT_EDIT = 1;
    private static final int REQUEST_COMPLETION_INPUT_EDIT = 2;
    protected HabitList habitList;
    private ListView habitListView;
    private HabitAdapter habitListAdapter;
    private Spinner spinner;
    private Calendar date;
    private Habit habit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize buttons
        mAddHabit = (Button) findViewById(R.id.add_habit);
        habitListView = (ListView) findViewById(R.id.habitList);

        //Set click event listen for add habit button to launch a new activity where the user can add new habits
        mAddHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                habit = new Habit();
                editCreateHabit(habit, NEW_HABIT, REQUEST_USER_INPUT_NEW); //Sets the correct request code for new habit mode
            }
        });

        //Set long click listener to allow the user to delete/edit/view completion of a specific task
        habitListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Habit habit = habitList.getViewList().get(position);

                new AlertDialog.Builder(MainActivity.this).setTitle("Options").setMessage("What Action Would You Like to Take?")
                        .setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editCreateHabit(habit, EDIT_HABIT, REQUEST_USER_INPUT_EDIT);
                                habitList.removeHabit(habit);
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                habitList.removeHabit(habit);
                                refreshViewAndSave();
                                dialog.cancel();
                            }
                        })
                        .setNeutralButton(R.string.view_completion, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editCompletionLogHabit(habit, REQUEST_COMPLETION_INPUT_EDIT);
                                habitList.removeHabit(habit);
                                dialog.cancel();
                            }
                        }).create().show();
                refreshViewAndSave();
                return true;
            }
        });

        //Spinner to pick the view that the user desires(Today or all habits): http://stackoverflow.com/questions/13377361/how-to-create-a-drop-down-list
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.spinner, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0) {
                    habitList.setEVERY_DAY(true);
                }
                else {
                    habitList.setEVERY_DAY(false);
                }
                habitList.setDAY_OF_WEEK(date);
                refreshViewAndSave();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    //On start, check for save file and initialize arraylist adapter
    @Override
    protected void onStart() {
        super.onStart();
        habitList = new HabitList();
        loadFromFile();
        habitListAdapter = new HabitAdapter(this, habitList.generateHabitList().getViewList());
        habitListView.setAdapter(habitListAdapter);
    }

    //Mode selection for edit/new habit, launches the correct activity
    //and serializes the habit for new activity with the correct request code
    public void editCreateHabit (Habit habit, String editOrCreate, int requestCode) {
        Intent intent = new Intent(MainActivity.this, HabitActivity.class);
        intent.putExtra(editOrCreate, habit);
        startActivityForResult(intent, requestCode);
    }

    //Mode selection for completion log, launches te correct activity
    //and serializes the habit for new activity with the correct request code
    public void editCompletionLogHabit (Habit habit, int requestCode) {
        Intent intent = new Intent(MainActivity.this, HabitCompletionActivity.class);
        intent.putExtra(VIEW_COMPLETION_LIST, habit);
        startActivityForResult(intent, requestCode);
    }

    //Gets the correct return serializable from child activity depending on return code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Habit returnHabit;
        if (requestCode == REQUEST_USER_INPUT_NEW) {
            if(resultCode == RESULT_OK) {
                returnHabit = (Habit) data.getSerializableExtra(RETURN_NEW_HABIT);
                habitList.addHabittoList(returnHabit);
            }
        }
        else if(requestCode == REQUEST_USER_INPUT_EDIT) {
            returnHabit = (Habit) data.getSerializableExtra(RETURN_EDIT_HABIT);
            habitList.addHabittoList(returnHabit);
        }
        else if(requestCode == REQUEST_COMPLETION_INPUT_EDIT) {
            returnHabit = (Habit) data.getSerializableExtra(RETURN_VIEW_COMPLETION_LIST);
            habitList.addHabittoList(returnHabit);
        }
        refreshViewAndSave();
    }

    //Taken from LonelyTwitter, loads arraylist from Gson
    private void loadFromFile() {
        try {
            FileInputStream fis = openFileInput(FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            Gson gson = new Gson();

            Type listType = new TypeToken<ArrayList<Habit>>(){}.getType();
            // Code from http://stackoverflow.com/questions/12384064/gson-convert-from-json-to-a-typed-arraylist

            ArrayList<Habit> temp = gson.fromJson(in, listType);
            habitList.setAllHabitList(temp);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
        }
    }

    //Taken from LonelyTwitter, saves arraylist as Gson
    protected void saveInFile() {
        try {
            FileOutputStream fos = openFileOutput(FILENAME,
                    0);

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));

            Gson gson = new Gson();
            gson.toJson(habitList.getAllHabitList(), out);
            out.flush();

            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
    }

    //Refreshes the view and notifies adapter, then saves arraylist
    public void refreshViewAndSave() {
        habitList.generateHabitList();
        habitListAdapter.notifyDataSetChanged();
        saveInFile();
    }
}


