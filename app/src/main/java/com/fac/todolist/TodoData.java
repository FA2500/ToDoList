package com.fac.todolist;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;

@Entity(tableName = "toDoData")
@Fts4()
public class TodoData {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    public int rowid;
    @ColumnInfo(name = "body")
    public String body;

    @ColumnInfo(name = "deadline")
    public String deadline;

    @ColumnInfo(name = "urgency")
    public int urgency;

    @ColumnInfo(name = "reminder")
    public boolean reminder;

    @ColumnInfo(name = "status")
    public boolean status;
}
