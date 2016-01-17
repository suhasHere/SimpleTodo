package com.crypt.todo.simpletodo.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.crypt.todo.simpletodo.R;
import com.crypt.todo.simpletodo.adapters.TodoListAdapter;
import com.crypt.todo.simpletodo.db.TodoDatabase;
import com.crypt.todo.simpletodo.model.TodoItem;
import com.crypt.todo.simpletodo.services.TodoExpirationIntent;
import com.crypt.todo.simpletodo.services.TodoExpiryBroadcastReceiver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodoActivity extends AppCompatActivity {

    List<TodoItem> todoItems;
    TodoListAdapter adapter;
    ListView lvItems;
    TodoDatabase todoDatabase;

    public static final Map<String, Integer> STATUS_EMOTICON_MAP = new HashMap<String, Integer>() {
        {
            put("angry", R.drawable.ic_angry);
            put("sad", R.drawable.ic_sad);
            put("good", R.drawable.ic_happy);
        }
    };


    private final int REQUEST_CODE = 200; // for intent operations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        // setup the floating action bar to add new items
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddItem(view);
            }
        });


        // setup the list view and its adapter and the database
        lvItems = (ListView ) findViewById(R.id.lvItems);
        // init the sqldb and read the entries
        todoDatabase = new TodoDatabase(this);
        todoItems = todoDatabase.getAllRecords();
        // attach the adapter to the view
        //itemsAdapter = new ArrayAdapter<>(this, R.layout.todo_list_adapter_layout, R.id.itemname, todoItems);
        adapter = new TodoListAdapter(this, todoItems, STATUS_EMOTICON_MAP);
        lvItems.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // listener setup for small and long click/tap
        setupListViewListener();

        // 60 seconds alarm to trigger expiry checks on the todo items
        scheduleAlarm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Handle the result from the EditItem Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            TodoItem item = data.getParcelableExtra("item");
            int itemPosition = data.getIntExtra("itemPos", -1);
            if(itemPosition != -1)
                updateItem(item, itemPosition);
            else
                Toast.makeText(this,"Edit Failed, Please try again", Toast.LENGTH_SHORT).show();

        }
    }

    // Handle the Button click for Adding a new item
    public void onAddItem(View view) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        if(itemText.isEmpty()) {
            Toast.makeText(this, "Empty TODO. Please Retry!!!", Toast.LENGTH_SHORT);
            return;
        }

        TodoItem todoItem = new TodoItem(itemText);

        long id = todoDatabase.createRecord(todoItem);
        if(id == -1) {
            Toast.makeText(this, "Adding  database failed", Toast.LENGTH_SHORT);
            return;
        }

        todoItem.setId(String.valueOf(id));
        todoItems.add(todoItem);
        adapter.notifyDataSetChanged();

        // update the database with the row_id created for the new item added
        todoDatabase.updateRecord(todoItem, id);
        etNewItem.setText("");

    }


    // Listeners for single tap and double tap
    private void setupListViewListener() {

        // on tapping the item, launch Edit Item Activity
        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(TodoActivity.this, EditItemActivity.class);
                        i.putExtra("itemPos", position);
                        i.putExtra("item", todoItems.get(position));
                        startActivityForResult(i, REQUEST_CODE);
                    }
                }
        );

        // on long press , remove the item from the adapter
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        TodoItem item = todoItems.remove(position);
                        adapter.notifyDataSetChanged();
                        todoDatabase.deleteRecord(item);
                        return true;
                    }
                }
        );
    }


    // Utility to update the adapter/view and the database
    private  void updateItem(TodoItem item, int position) {
        TodoItem original = todoItems.get(position);


        if(item.getDate() == null && original.getDate() != null) {
            item.setDate(original.getDate());
        }


        if(item.getTime() == null && original.getTime() != null) {
            item.setTime(original.getTime());
        }

        item.setStatus("good");

        todoItems.set(position, item);
        adapter.notifyDataSetChanged();
        todoDatabase.updateRecord(item, Integer.valueOf(item.getId()));
    }




    // Setup a recurring alarm to invoke the ItemExpiryMonitoring IntentService
    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), TodoExpiryBroadcastReceiver.class);

        // Create a PendingIntent to be triggered when the alarm goes off
        PendingIntent pIntent = PendingIntent.getBroadcast(this, TodoExpiryBroadcastReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 1000 * 10, pIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        IntentFilter filter = new IntentFilter(TodoExpirationIntent.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(expiryIntentReceiver, filter);
        // or `registerReceiver(testReceiver, filter)` for a normal broadcast
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        LocalBroadcastManager.getInstance(this).unregisterReceiver(expiryIntentReceiver);
        // or `unregisterReceiver(testReceiver)` for a normal broadcast
    }

    // Define the callback for what to do when data is received from the ExpiryService
    // after monitoring
    private BroadcastReceiver expiryIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
            if (resultCode == RESULT_OK) {
                boolean hasChanges = intent.getBooleanExtra("hasChanges", false);
                if(hasChanges) {
                    List<TodoItem> responses = intent.getParcelableArrayListExtra("responses");
                    for(TodoItem item: responses) {
                        int position = findPosition(item);
                        todoItems.set(position, item);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };


    private int findPosition(TodoItem item) {
        for(int i=0; i < todoItems.size(); i++) {
            if(todoItems.get(i) != null
                    && todoItems.get(i).getId().equalsIgnoreCase(item.getId()))
            {
                return i;
            }
        }
        return  -1;
    }
}
