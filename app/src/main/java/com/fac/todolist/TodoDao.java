package com.fac.todolist;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TodoDao {
    @Insert //Create
    void insertTodo(TodoData td);

    @Query("SELECT *, `rowid` FROM toDoData") //Read
    List<TodoData> getAll();

    @Query("SELECT *, `rowid` FROM toDoData WHERE status = false") //Read
    List<TodoData> getIncomplete();

    @Query("SELECT *, `rowid` FROM toDoData WHERE status = true") //Read
    List<TodoData> getComplete();

    @Update //Update
    void updateTodo(TodoData td);

    @Delete //Delete
    void deleteTodo(TodoData td);
}
