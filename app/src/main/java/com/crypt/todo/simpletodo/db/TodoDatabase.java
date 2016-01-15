package com.crypt.todo.simpletodo.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.crypt.todo.simpletodo.model.TodoItem;
import com.crypt.todo.simpletodo.model.TodoDbContract;

import java.util.ArrayList;
import java.util.List;


/**
 * Simple SqLite helper class to store TODOItems in the database
 * !!!! NOT HAPPY WITH SOME OF THE WAY THIS LOGIC IS IMPLEMENTED HMMM !!!
 */
public class TodoDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "TodoItems.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TodoDbContract.TodoEntry.TABLE_NAME + " (" +
                    TodoDbContract.TodoEntry._ID + " INTEGER PRIMARY KEY," +
                    TodoDbContract.TodoEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    TodoDbContract.TodoEntry.COLUMN_NAME_TODO_ITEM + TEXT_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TodoDbContract.TodoEntry.TABLE_NAME;


    public TodoDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 1) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }

    // USED ONLY FOR DEBUGGING (THIS PRIVATE)
    private void clean() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }


    public TodoItem getRecord(String id) {

        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                TodoDbContract.TodoEntry._ID,
                TodoDbContract.TodoEntry.COLUMN_NAME_ENTRY_ID,
                TodoDbContract.TodoEntry.COLUMN_NAME_TODO_ITEM
        };

        Cursor c = db.query(
                TodoDbContract.TodoEntry.TABLE_NAME,
                projection,
                String.format("%s= ?", TodoDbContract.TodoEntry.COLUMN_NAME_ENTRY_ID),
                new String[] {id},
                null,
                null,
                null,
                null);

        if (c != null) {

            if (!c.moveToFirst()) {
                return null;
            }

            TodoItem item = new TodoItem(
                    c.getString(c.getColumnIndexOrThrow(TodoDbContract.TodoEntry.COLUMN_NAME_ENTRY_ID)),
                    c.getString(c.getColumnIndexOrThrow(TodoDbContract.TodoEntry.COLUMN_NAME_TODO_ITEM))
            );


            Log.d("DB_DEBUG", " DB returned " + item.getText());
            c.close();
            return  item;
        }

        return null;
    }


    public long createRecord(TodoItem item) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if(item.getId() != null &&  !item.getId().isEmpty()) {
            values.put(TodoDbContract.TodoEntry.COLUMN_NAME_ENTRY_ID, item.getId());
        }
        values.put(TodoDbContract.TodoEntry.COLUMN_NAME_TODO_ITEM, item.getText());

        long newRowId;
        newRowId = db.insert(
                TodoDbContract.TodoEntry.TABLE_NAME,
                null,
                values);

        db.close();

        if(newRowId != -1) {
            Log.d("DB_DEBUG", "Successfully Added Url "+ item.getId());
            return newRowId;
        } else {
            Log.d("DB_DEBUG", "Failed adding Url " + item.getId());
        }

        return -1;
    }

    public int updateRecord(TodoItem item, long rowId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TodoDbContract.TodoEntry.COLUMN_NAME_ENTRY_ID, item.getId());
        values.put(TodoDbContract.TodoEntry.COLUMN_NAME_TODO_ITEM, item.getText());


        String selection = TodoDbContract.TodoEntry._ID  + " LIKE ?";
        String[] selectionArgs = {String.valueOf(rowId)};

        int count = db.update(
                TodoDbContract.TodoEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        return count;

    }


    public int deleteRecord(TodoItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = TodoDbContract.TodoEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {item.getId()};
        return db.delete(
                TodoDbContract.TodoEntry.TABLE_NAME,
                selection,
                selectionArgs
        );
    }



    /**
     * Bulk DB operations
     *
     */

    public List<TodoItem> getAllRecords() {

        List<TodoItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                TodoDbContract.TodoEntry._ID,
                TodoDbContract.TodoEntry.COLUMN_NAME_ENTRY_ID,
                TodoDbContract.TodoEntry.COLUMN_NAME_TODO_ITEM
        };

        Cursor c = db.query(
                TodoDbContract.TodoEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null,
                null);

        if(c != null) {
            c.moveToFirst();
            while (c.isAfterLast() == false) {
                items.add(new TodoItem(
                        c.getString(c.getColumnIndexOrThrow(TodoDbContract.TodoEntry.COLUMN_NAME_ENTRY_ID)),
                        c.getString(c.getColumnIndexOrThrow(TodoDbContract.TodoEntry.COLUMN_NAME_TODO_ITEM))
                ));
                c.moveToNext();
            }
        }

        Log.d("DB_DEBUG", "num items in cursor after read "+ items.size());
        return items;
    }


    public boolean updateAll(List<TodoItem> items) {
        Log.d("DB_DEBUG", "Update All with item count "+ items.size());
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "INSERT or REPLACE INTO " + TodoDbContract.TodoEntry.TABLE_NAME + " (" +
                TodoDbContract.TodoEntry.COLUMN_NAME_ENTRY_ID + COMMA_SEP +
                TodoDbContract.TodoEntry.COLUMN_NAME_TODO_ITEM +
                " ) VALUES (?,?)" ;

        Log.d("DB_DEBUG","Bulk update Statement " + sql);


        db.beginTransaction();
        SQLiteStatement statement = db.compileStatement(sql);
        for(TodoItem item: items) {
            statement.bindString(1, item.getId());
            statement.bindString(2, item.getText());
            statement.execute();
        }
        db.setTransactionSuccessful();
        db.endTransaction();

        return true;
    }


}
