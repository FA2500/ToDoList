package com.fac.todolist;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = TodoData.class, version = 1)

public abstract class TodoDatabase extends RoomDatabase {
    public abstract TodoDao todoDao();
}
