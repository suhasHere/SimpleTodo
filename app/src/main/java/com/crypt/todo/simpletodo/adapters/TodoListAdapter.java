package com.crypt.todo.simpletodo.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.crypt.todo.simpletodo.model.TodoItem;

import java.util.List;

//TODO: This might be needed for custom list view .. let's see

public class TodoListAdapter extends ArrayAdapter<TodoItem> {

    public TodoListAdapter(Context context, int resource, List<TodoItem> objects) {
        super(context, resource, objects);
    }
}
