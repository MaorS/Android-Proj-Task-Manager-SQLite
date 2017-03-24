package com.shams.maor.task_manager_sqlite;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shams.maor.task_manager_sqlite.Adapters.TaskAdapter;

/**
 * Copyright © 2017 Maor Shams. All rights reserved.
 */

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private TextView notify, edit_task;
    private ListView listView;
    private Task[] tasks;
    TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = DBManager.getInstance(this).getDB();

        edit_task = (EditText) findViewById(R.id.edit_task);
        notify = (TextView) findViewById(R.id.task_notification);

        listView = (ListView) findViewById(R.id.task_taskList);
        listView.setOnItemClickListener(clickListener);
        listView.setOnItemLongClickListener(longClickListener);

        reloadData(null, null, Constant.TSK_NULL);
    }


    // update checkbox
    private void saveIsChecked(int done, Task t) {
        db.execSQL("UPDATE tasks SET done=" + done + " WHERE id=" + t.id);
        reloadData(null, null, Constant.TSK_NULL);
    }

    private void reloadData(String orderBy, String ASC_OR_DESC, int filter) {

        // get data from SQLite
        String query = "SELECT id,task,done,date_added FROM tasks ";
        if (!(orderBy == null && ASC_OR_DESC == null)) { // if selecting order
            query += "ORDER BY " + orderBy + " " + ASC_OR_DESC;
        } else if (filter == Constant.TSK_TRUE || filter == 0) { // if filtering
            query += "WHERE done=" + filter;
        }

        Cursor c = db.rawQuery(query, null);
        tasks = new Task[c.getCount()];
        c.moveToFirst();
        // loop throw, add to array
        for (int i = 0; i < tasks.length; i++) {
            tasks[i] = new Task(
                    c.getInt(0),
                    c.getString(1),
                    c.getInt(2)
            );
            c.moveToNext();
        }
        c.close();

        // update list
        if (taskAdapter == null) {
            taskAdapter = new TaskAdapter(this, tasks);
            listView.setAdapter(taskAdapter);
        } else {
            taskAdapter.updateAdapter(tasks);
        }

        // if there is no tasks
        if (tasks.length == 0) {
            notify.setText(R.string.no_tasks);
            notify.setVisibility(View.VISIBLE);
        } else {
            notify.setVisibility(View.GONE);
        }
    }

    // add new Task
    public void onClickAddNewTask(View view) {
        String task = edit_task.getText().toString().trim();
        if (!task.isEmpty()) { // create new task
            ContentValues c = new ContentValues();
            c.put("task", task);
            c.put("done", Constant.TSK_FALSE + "");
            c.put("date_added", System.currentTimeMillis() + "");
            db.insert("tasks", null, c); // insert to SQLite
            reloadData(null, null, Constant.TSK_NULL);// update
        }
        edit_task.setText("");
    }


    // when clicking Delete all button
    private void onClickDelete() {
        if (tasks.length < 1) return;

        // count how many tasks
        int counter = 0;
        for (Task task : tasks) {
            if (task.done == 0) counter++;
        }

        // build alert
        String toDel = getString(R.string.delete_all);
        if (counter >= 1)
            toDel = "(" + counter + ")" + getString(R.string.are_you_sure_delete_all_tasks);
        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle(R.string.delete_tasks)
                .setMessage(toDel)
                .setNegativeButton("Cancel", null).setIcon(android.R.drawable.ic_menu_delete)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.execSQL("DELETE FROM tasks");// delete from SQLite
                        reloadData(null, null, Constant.TSK_NULL);//update
                    }
                }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        reloadData(null, null, Constant.TSK_NULL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                onClickDelete();
                break;
            case R.id.menu_AllTasks:
                reloadData(null, null, Constant.TSK_NULL);
                break;
            case R.id.menu_DoneTasks:
                reloadData(null, null, Constant.TSK_TRUE);
                break;
            case R.id.menu_OpenTasks:
                reloadData(null, null, Constant.TSK_FALSE);
                break;
            case R.id.menu_newestTasks:
                reloadData("date_added", "DESC", Constant.TSK_NULL);
                break;
            case R.id.menu_oldestTasks:
                reloadData("date_added", "ASC", Constant.TSK_NULL);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            View editTask = LayoutInflater.from(MainActivity.this).inflate(R.layout.edit_task, null);
            final TextView input = (TextView) editTask.findViewById(R.id.editOldTask);
            final Task t = (Task) listView.getAdapter().getItem(i);

            input.setText(tasks[i].task);

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.edit_task).setView(editTask)
                    .setIcon(android.R.drawable.ic_menu_edit)
                    .setNeutralButton(R.string.cancel, null)// do nothing
                    .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //delete from SQLite
                            db.execSQL("DELETE FROM tasks WHERE id=" + t.id);
                            reloadData(null, null, Constant.TSK_NULL);
                        }
                    })
                    .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // update SQLite
                            ContentValues c = new ContentValues();
                            c.put("task", input.getText().toString());
                            int currentState = t.done;
                            c.put("done", currentState + "");
                            c.put("date_added", System.currentTimeMillis() + "");
                            db.update(Constant.DB_TABLE, c, "id=" + t.id, null);
                            reloadData(null, null, Constant.TSK_NULL);
                        }
                    }).show();
            return true;
        }
    };

    // when clicking on task -> mark as done
    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            // get clicked task
            final Task t = (Task) listView.getAdapter().getItem(i);
            // task layout
            LinearLayout li = (LinearLayout) view;
            ImageView img = (ImageView) li.findViewById(R.id.task_done);
            // stage of checkbox (clicked or not)
            int currentState = tasks[i].done;
            if (currentState == Constant.TSK_FALSE) {
                img.setVisibility(View.VISIBLE);
                saveIsChecked(Constant.TSK_TRUE, t);
            } else {
                img.setVisibility(View.INVISIBLE);
                saveIsChecked(Constant.TSK_FALSE, t);
            }
        }
    };
}