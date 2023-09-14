package com.fac.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rc,rc2;
    private FloatingActionButton fab;

    private CheckBox completeCB, incompleteCB;

    private TodoDatabase db ;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private String mDate;
    private TodoAdapter todoAdapter,completeTodoAdapter;

    Handler mainHandler;
    ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = Room.databaseBuilder(MainActivity.this,
                TodoDatabase.class, "TodoData").build();
        mainHandler = new Handler(Looper.getMainLooper());
        executor = Executors.newSingleThreadExecutor();
        initUI();
        getData();
    }

    private void initUI()
    {
        rc = findViewById(R.id.incompleteRC);
        rc2 = findViewById(R.id.completeRC);

        completeCB = findViewById(R.id.completeCB);
        incompleteCB = findViewById(R.id.incompleteCB);

        incompleteCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    rc.setVisibility(View.VISIBLE);
                }
                else
                {
                    rc.setVisibility(View.GONE);
                }
            }
        });

        completeCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    rc2.setVisibility(View.VISIBLE);
                }
                else
                {
                    rc2.setVisibility(View.GONE);
                }
            }
        });

        rc.setLayoutManager(new LinearLayoutManager(this));
        rc2.setLayoutManager(new LinearLayoutManager(this));

        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
            }
        });
    }

    private void getData()
    {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //Background work here
                TodoDao todoDao = db.todoDao();
                List<TodoData> dataList = todoDao.getIncomplete();
                todoAdapter = new TodoAdapter(dataList,MainActivity.this);

                List<TodoData> comdataList = todoDao.getComplete();
                completeTodoAdapter = new TodoAdapter(comdataList, MainActivity.this);

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        rc.setAdapter(todoAdapter);
                        rc2.setAdapter(completeTodoAdapter);
                        Log.d("RoomData", "GOT DATA");
                    }
                });
            }
        });
    }

    private void saveData(String body, boolean status)
    {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //Background work here
                TodoDao todoDao = db.todoDao();
                TodoData todoData = new TodoData();
                todoData.body = body;
                todoData.deadline = (mDate==null ? "" : mDate );
                todoData.urgency = (status?1:0);
                todoData.reminder = (mDate==null?false:true);
                todoData.status = false;
                Log.d("RoomData", "GOT DATA");
                todoDao.insertTodo(todoData);
                getData();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run()
                    {
                        Toast.makeText(MainActivity.this, "To-do activity saved", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void addData()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Add To-Do Activity");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_todo,null);
        dialog.setView(dialogView);

        EditText editText = dialogView.findViewById(R.id.editText);
        CheckBox checkbox = dialogView.findViewById(R.id.checkBox);

        Button dateBtn = dialogView.findViewById(R.id.chooseDateTimeButton);
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePickerDialog();
            }
        });

        dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveData(editText.getText().toString(), checkbox.isChecked());

            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog1 = dialog.create();
        dialog1.show();

    }

    public void editData(TodoData todoData)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Add To-Do Activity");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_todo,null);
        dialog.setView(dialogView);

        EditText editText = dialogView.findViewById(R.id.editText);
        CheckBox checkbox = dialogView.findViewById(R.id.checkBox);
        TextView textView = dialogView.findViewById(R.id.dateTVE);

        Button dateBtn = dialogView.findViewById(R.id.chooseDateTimeButton);


        editText.setText(todoData.body);
        checkbox.setChecked(todoData.urgency == 1);
        if(!Objects.equals(todoData.deadline, ""))
        {
            textView.setText(todoData.deadline);
        }
        else
        {
            textView.setVisibility(View.GONE);
        }


        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePickerDialog();
            }
        });

        dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveData(editText.getText().toString(), checkbox.isChecked());

            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog1 = dialog.create();
        dialog1.show();

    }

    private void showDateTimePickerDialog() {
        final Calendar currentDate = Calendar.getInstance();
        mYear = currentDate.get(Calendar.YEAR);
        mMonth = currentDate.get(Calendar.MONTH);
        mDay = currentDate.get(Calendar.DAY_OF_MONTH);
        mHour = currentDate.get(Calendar.HOUR_OF_DAY);
        mMinute = currentDate.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;

                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHour = hourOfDay;
                        mMinute = minute;

                        mDate = mYear + "-" + (mMonth + 1) + "-" + mDay + "-" + mHour + "-" + mMinute;
                    }
                }, mHour, mMinute, false);

                timePickerDialog.show();
            }
        }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

}




