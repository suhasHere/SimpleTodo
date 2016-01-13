package com.crypt.todo.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {

    // Multiline text view reference
    EditText etEditItem;
    int itemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        etEditItem = (EditText) findViewById(R.id.etEditItem);
        setSupportActionBar(toolbar);
        String itemText = getIntent().getStringExtra("item");
        itemPosition = getIntent().getIntExtra("itemPos", -1);
        etEditItem.setText(itemText);
        etEditItem.setSelection(itemText.length());

    }

    public void onSaveItem(View view) {
        Intent data = new Intent();
        data.putExtra("item", etEditItem.getText().toString());
        data.putExtra("itemPos", itemPosition);
        setResult(RESULT_OK, data);
        finish();
    }
}
