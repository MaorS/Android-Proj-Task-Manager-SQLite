package com.shams.maor.task_manager_sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Copyright © 2017 Maor Shams. All rights reserved.
 */

public class DBManager {

    private SQLiteDatabase db;
    private static DBManager instance;

    // Singleton pattern , Private constructor
    // Share access to db ONLY from the same instance
    // To not be instantiated from outside
    // DBManager make instantiation to db with the api
    private DBManager(Context context) {
        // context because we want to get data from app (context)
        // name = db name (if we had few dbs, make dependency injection)
        // cursorFactory = interface to create custom cursor
        // version = db version (increase version for upgrade)
        db = new SQLiteOpenHelper(context,Constant.DB_NAME,null,1){

            @Override
            public void onCreate(SQLiteDatabase db) {
                String query = "CREATE TABLE tasks (id INTEGER PRIMARY KEY, task VARCHAR(100), done INTEGER, date_added INTEGER)";
                db.execSQL(query);
            }
            @Override
            public void onUpgrade(SQLiteDatabase db, int i, int i1) {}

        }.getWritableDatabase();
    }

    static DBManager getInstance(Context context) {
        //share the context if exist, else create new
        if (instance == null) instance = new DBManager(context);
        return instance;
    }

    // Reference to db connection
    SQLiteDatabase getDB() {
        return this.db;
    }
}
