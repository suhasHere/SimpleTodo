package com.crypt.todo.simpletodo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.crypt.todo.simpletodo.R;
import com.crypt.todo.simpletodo.model.TodoItem;

/**
 * Activity that deals with the editing of a single todo item
 */
public class EditItemActivity extends AppCompatActivity {

    // Multiline text view reference
    EditText etEditItem;
    // position of the item in the Listveiw
    int itemPosition;
    // Item parcelled via the intent
    TodoItem todoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        etEditItem = (EditText) findViewById(R.id.etEditItem);
        setSupportActionBar(toolbar);

        // deal with the extras in the intent
        todoItem = getIntent().getParcelableExtra("item");
        itemPosition = getIntent().getIntExtra("itemPos", -1);

        // update the UI
        etEditItem.setText(todoItem.getText());
        etEditItem.setSelection(todoItem.getText().length());
    }

    public void onSaveItem(View view) {
        Intent i = new Intent();

        // setup parcel for reporting to the source activity
        todoItem.setText(etEditItem.getText().toString());
        i.putExtra("itemPos", itemPosition);
        i.putExtra("item", todoItem);

        setResult(RESULT_OK, i);
        finish();
    }
}
