package com.fac.todolist;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    private List<TodoData> todoList;
    private Context context;

    public TodoAdapter(List<TodoData> todoList, Context context) {
        this.todoList = todoList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_box, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Handler mainHandler;
        ExecutorService executor;
        TodoDatabase db ;
        db = Room.databaseBuilder(context,
                TodoDatabase.class, "TodoData").build();

        TodoData todo = todoList.get(position);

        holder.statusBx.setText((todo.status?"Complete":"Incomplete"));
        holder.bodyTV.setText(todo.body);
        holder.dateTV.setText(todo.deadline);
        if(holder.dateTV.getText().toString().equals(""))
        {
            holder.dateTV.setVisibility(View.GONE);
        }
        holder.urgencyTV.setText((todo.urgency==0?"Not urgent":"Urgent"));
        mainHandler = new Handler(Looper.getMainLooper());
        executor = Executors.newSingleThreadExecutor();

        if(todo.urgency==1)
        {
            holder.img.setImageResource(R.drawable.baseline_warning_24);
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.red)));
            holder.statusBx.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white)));
            holder.bodyTV.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white)));
            holder.dateTV.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white)));
            holder.urgencyTV.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white)));
        }
        else if(todo.urgency==0)
        {
            holder.img.setImageResource(R.drawable.baseline_info_24);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof MainActivity)
                {
                    ((MainActivity) context).editData(todo);
                }
            }
        });

        holder.statusBx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (todo)
                        {
                            Log.w("UPDATE","TEST "+todo);
                            TodoDao todoDao = db.todoDao();
                            if(isChecked)
                            {
                                todo.status = true;
                                todoDao.updateTodo(todo);
                            }
                            else
                            {
                                todo.status = false;
                                todoDao.updateTodo(todo);
                            }
                        }
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run()
                            {
                                if(isChecked)
                                {
                                    holder.statusBx.setText("Complete");
                                }
                                else
                                {
                                    holder.statusBx.setText("Incomplete");
                                }
                            }
                        });
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        TextView bodyTV, dateTV, urgencyTV;
        CheckBox statusBx;
        ImageView img;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            statusBx = itemView.findViewById(R.id.statusBx);
            bodyTV = itemView.findViewById(R.id.bodyTV);
            dateTV = itemView.findViewById(R.id.dateTV);
            urgencyTV = itemView.findViewById(R.id.urgentTV);
            img = itemView.findViewById(R.id.urgentIV);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }
}
