package com.ethereon.app;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private final List<ToDoItem> todoItems;

    public ToDoAdapter(List<ToDoItem> todoItems) {
        this.todoItems = todoItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_roadmap, parent, false); // reused layout if item_todo doesn't exist
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToDoItem item = todoItems.get(position);
        holder.taskText.setText(item.task);
        holder.doneCheck.setChecked(item.done);

        // Apply strikethrough for completed tasks
        updateStrikeThrough(holder.taskText, item.done);

        holder.doneCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.done = isChecked;
            updateStrikeThrough(holder.taskText, isChecked);
        });
    }

    private void updateStrikeThrough(TextView textView, boolean isDone) {
        if (isDone) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return todoItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskText;
        CheckBox doneCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskText = itemView.findViewById(R.id.todoText);
            doneCheck = itemView.findViewById(R.id.todoCheck);
        }
    }
}