package com.crypt.todo.simpletodo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.crypt.todo.simpletodo.R;
import com.crypt.todo.simpletodo.db.TodoDatabase;
import com.crypt.todo.simpletodo.model.TodoItem;

import java.util.List;

public class TodoActivity extends AppCompatActivity {

    List<TodoItem> todoItems;
    ArrayAdapter<TodoItem> itemsAdapter;
    ListView lvItems;
    TodoDatabase todoDatabase;

    private final int REQUEST_CODE = 200; // for intent operations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        lvItems = (ListView ) findViewById(R.id.lvItems);

        // init the sqldb and read the entries
        todoDatabase = new TodoDatabase(this);
        readItems();

        // attach the adapter to the view
        itemsAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item_1, todoItems);
        lvItems.setAdapter(itemsAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // listener setup for small and long click/tap
        setupListViewListener();
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
            Toast.makeText(this, "Empty TODO ... Ummm !!!", Toast.LENGTH_SHORT);
            return;
        }

        TodoItem todoItem = new TodoItem(itemText);

        long id = todoDatabase.createRecord(todoItem);
        if(id == -1) {
            Toast.makeText(this, "Adding  database failed", Toast.LENGTH_SHORT);
            return;
        }

        todoItem.setId(String.valueOf(id));
        itemsAdapter.add(todoItem);
        // update the database with the row_id created for the new item added
        todoDatabase.updateRecord(todoItem, id);
        etNewItem.setText("");
    }

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
                        itemsAdapter.notifyDataSetChanged();
                        todoDatabase.deleteRecord(item);
                        return true;
                    }
                }
        );
    }


    // Utility to update the adapter/view and the database
    private  void updateItem(TodoItem item, int position) {
        todoItems.set(position, item);
        itemsAdapter.notifyDataSetChanged();
        todoDatabase.updateRecord(item, Integer.valueOf(item.getId()));
    }


    /*
        Lower level utils .. should be in a separate package i think !!
     */

    private void readItems() {

        todoItems = todoDatabase.getAllRecords();

        String resultStr = "";
        for(TodoItem item: todoItems) {
            resultStr += item.getText() + " ";
        }
    }


    // Bulk write operation. Not used yet.
    private void writeItems() {
        String resultStr = "";
        for(TodoItem item: todoItems) {
            resultStr += item + " ";
        }

        Log.d("APP_DEBUG", "WriteItems " + resultStr);

        // perform a bulk update
        todoDatabase.updateAll(todoItems);
    }

}
