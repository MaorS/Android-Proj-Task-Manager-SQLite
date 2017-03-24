package com.shams.maor.task_manager_sqlite;

/**
 * Copyright © 2017 Maor Shams. All rights reserved.
 */

public class Task {
    public String task;
    public int done;
    public int id;

    public Task(int id, String task, int done ) {
        this.id = id;
        this.task = task;
        this.done = done;
    }
}
