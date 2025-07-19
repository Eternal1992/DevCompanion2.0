package com.ethereon.app;

import androidx.annotation.NonNull;

public class ToDoItem {
    public String task;
    public boolean done;

    public ToDoItem(String task, boolean done) {
        this.task = task;
        this.done = done;
    }

    @NonNull
    @Override
    public String toString() {
        return "ToDoItem{" +
                "task='" + task + '\'' +
                ", done=" + done +
                '}';
    }
}