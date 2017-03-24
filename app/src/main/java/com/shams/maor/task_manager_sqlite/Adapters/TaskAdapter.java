package com.shams.maor.task_manager_sqlite.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shams.maor.task_manager_sqlite.R;
import com.shams.maor.task_manager_sqlite.Task;

/**
 * Copyright © 2017 Maor Shams. All rights reserved.
 */

public class TaskAdapter extends BaseAdapter {

    private final Context context;
    private Task[] tasks;// not final, may update via method

    public TaskAdapter(Context context, Task[] tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    @Override
    public int getCount() {
        return tasks.length;
    }

    @Override
    public Task getItem(int i) {
        return tasks[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View rView, ViewGroup viewGroup) {
        if (rView == null) rView = LayoutInflater.from(context).inflate(R.layout.task, null);
        final Task t = tasks[i];
        // number of task
        ((TextView) rView.findViewById(R.id.task_num)).setText(i + 1 + "");
        // the task
        ((TextView) rView.findViewById(R.id.task_txt)).setText(t.task);

        LinearLayout li = (LinearLayout) rView;
        li.setMinimumHeight(100);
        ImageView img = (ImageView) li.findViewById(R.id.task_done);// done checkbox

        int currentState = tasks[i].done;
        img.setVisibility(currentState == 0 ? View.INVISIBLE : View.VISIBLE);
        return rView;
    }

    public void updateAdapter(Task[] tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }
}