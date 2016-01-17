package com.crypt.todo.simpletodo.model;


import android.provider.BaseColumns;

/**
 * Define contract for the database
 */
public final class TodoDbContract {

    public TodoDbContract() {

    }

    public static abstract class TodoEntry implements BaseColumns {
        public static final String TABLE_NAME = "todo";
        public static final String COLUMN_NAME_ENTRY_ID = "rowid"; // same as _ID for now
        public static final String COLUMN_NAME_TODO_ITEM = "item"; // the actual text
        public static final String COLUMN_NAME_TODO_DATE = "date"; // expiry date
        public static final String COLUMN_NAME_TODO_TIME = "time"; // expiry time
        public static final String COLUMN_NAME_TODO_STATUS = "status"; //status - good , sad, angry
    }

}
