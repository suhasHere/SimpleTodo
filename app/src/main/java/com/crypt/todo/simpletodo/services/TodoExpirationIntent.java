package com.crypt.todo.simpletodo.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.crypt.todo.simpletodo.db.TodoDatabase;
import com.crypt.todo.simpletodo.model.TodoItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The intent of this IntentService is to loop through
 * the items to check for expiry and soon to expiry
 * every items and report the same for UI updates (if needed)
 */
public class TodoExpirationIntent extends IntentService {

    private  static long SAD_EXPIRATION =  15 * 1000; // 15 seconds
    private  static long ANGRY_EXPIRATION =  5 * 1000; // 5 seconds

    public static final String ACTION = "com.crypt.todo.simpletodo.services.TodoExpirationIntent";
    public List<TodoItem> mTodoItems;
    TodoDatabase mTodoDatabase; // BAD - giving access to DB directly .. we might abstract it via
                                // some sort of provider ??


    public TodoExpirationIntent() {
        super("todo-exp-intent");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTodoDatabase= new TodoDatabase(this);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        mTodoItems = mTodoDatabase.getAllRecords();
        Log.d("ExpiryService", "Database read size: " + mTodoItems.size());
        Intent in = new Intent(ACTION);
        in.putExtra("resultCode", Activity.RESULT_OK);
        List<TodoItem> updatedItems = processRecordsForExpiration();
        if(updatedItems.size() > 0) {
            in.putExtra("hasChanges", true);
            in.putParcelableArrayListExtra("responses", (ArrayList) updatedItems);
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(in);
    }

    /**
     * Process the items in the database to determine the entries
     * that are already expired or soon to be expired.
     *
     * @return List of items that needs UI attention
     */
    private List<TodoItem> processRecordsForExpiration() {
        boolean hasChanges = false;
        List<TodoItem> updatedItems = new ArrayList<>();
        if(mTodoItems != null && mTodoItems.size() > 0) {
            for(TodoItem item: mTodoItems) {
                String date = item.getDate();
                String time = item.getTime();
                if(date != null && !date.isEmpty() && time != null && !time.isEmpty()) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String dateTime = date + " " + time;
                    try {
                        Date itemDateTime = format.parse(dateTime);
                        Date today = new Date();
                        if(today.after(itemDateTime)) {
                            item.setStatus("angry");
                            mTodoDatabase.updateRecord(item, Integer.valueOf(item.getId()));
                            updatedItems.add(item);
                            hasChanges = true;
                        } else if (today.compareTo(itemDateTime) < 0) {
                            // there is still time to expiry
                            // set sad if we are between Angry and Sad units of expiration
                            // set angry if we are less than Angry units of the expiration
                            long diff = itemDateTime.getTime() - System.currentTimeMillis();
                            if(diff < ANGRY_EXPIRATION ) {
                                item.setStatus("angry");
                                hasChanges = true;
                            } else if(diff > ANGRY_EXPIRATION && diff < SAD_EXPIRATION) {
                                item.setStatus("sad");
                                hasChanges = true;
                            }
                            //update db
                            if(hasChanges) {
                                mTodoDatabase.updateRecord(item, Integer.valueOf(item.getId()));
                                updatedItems.add(item);
                            }
                        }
                    } catch (ParseException e) {
                        Log.d("ExpiryService", "Date/Time PARSE failure for "+ dateTime);
                    }
                } else {
                    Log.d("ExpiryService", " Date/Time not set right for "+ item.getId());
                }
            }
        }
      return updatedItems;
    }

}
