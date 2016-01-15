package com.crypt.todo.simpletodo.model;


import android.provider.BaseColumns;

/**
 * Define contract for our TODOs database
 */
public final class TodoDbContract {

    public TodoDbContract() {

    }

    public static abstract class TodoEntry implements BaseColumns {
        public static final String TABLE_NAME = "todo";
        public static final String COLUMN_NAME_ENTRY_ID = "rowid"; // same as _ID for now
        public static final String COLUMN_NAME_TODO_ITEM = "todoitem"; // the actual text
    }

}
