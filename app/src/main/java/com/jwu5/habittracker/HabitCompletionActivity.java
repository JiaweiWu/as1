package com.jwu5.habittracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * Created by Jiawei on 10/1/2016.
 */
public class HabitCompletionActivity extends Activity {

    private static final String NEW_HABIT = "com.jwu5.habittracker.new_habit";
    private static final String EDIT_HABIT = "com.jwu5.habittracker.edit_habit";
    private static final String VIEW_COMPLETION_LIST = "com.jwu5.habittracker.view_completion_list";
    private static final String RETURN_NEW_HABIT = "com.jwu5.habittracker.return_new_habit";
    private static final String RETURN_EDIT_HABIT = "com.jwu5.habittracker.return_edit_habit";
    private static final String RETURN_VIEW_COMPLETION_LIST = "com.jwu5.habittracker.return_view_completion_list";

    private ListView completionListView;
    private ArrayAdapter<String> completionListAdapter;
    private Habit habit;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completion);

        //Completion log view list initialization
        //De-serializing habit from main activity
        completionListView = (ListView) findViewById(R.id.completionList);
        habit = (Habit) getIntent().getSerializableExtra(VIEW_COMPLETION_LIST);

        //Long hold on each completion log entry will give the user
        //the option to delete said entry
        completionListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(HabitCompletionActivity.this).setTitle("Delete Completion").setMessage("Are You Sure You Want to Delete the Selected Completion?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                habit.removeCompletionLogEntry(position);
                                completionListAdapter.notifyDataSetChanged();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create().show();
                completionListAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    //Attach adapter
    @Override
    protected void onStart() {
        super.onStart();
        completionListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, habit.getCompletionLog());
        completionListView.setAdapter(completionListAdapter);

    }

    //Return result code when back button is pressed and serializes
    //the updated habit back to main activity
    public void onBackPressed() {
        Intent returnData = new Intent();
        returnData.putExtra(RETURN_VIEW_COMPLETION_LIST, habit);
        setResult(RESULT_OK, returnData);
        finish();
    }

}
