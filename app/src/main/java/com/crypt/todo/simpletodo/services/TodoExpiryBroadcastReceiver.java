package com.crypt.todo.simpletodo.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.crypt.todo.simpletodo.model.TodoItem;

import java.util.ArrayList;
import java.util.List;


/**
 * This class handles the periodic pending intent invocation
 * by the Alarm every 15 seconds.
 */
public class TodoExpiryBroadcastReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.crypt.todo.simpletodo.services.TodoExpiryBroadcastReceiver";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        List<TodoItem> todoItems = intent.getParcelableArrayListExtra("items");
        Intent i = new Intent(context, TodoExpirationIntent.class);
        i.putParcelableArrayListExtra("items", (ArrayList) todoItems);
        context.startService(i);
    }
}
