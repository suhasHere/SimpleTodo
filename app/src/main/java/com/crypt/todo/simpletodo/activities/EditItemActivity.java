package com.crypt.todo.simpletodo.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.crypt.todo.simpletodo.R;
import com.crypt.todo.simpletodo.model.TodoItem;

import java.util.Calendar;

/**
 * Activity that deals with the editing of a single item
 */
public class EditItemActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener , TimePickerDialog.OnTimeSetListener{

    // Multiline text view reference
    EditText etEditItem;
    RadioButton dateRadio;
    TextView tvDate;
    RadioButton timeRadio;
    TextView tvTime;

    // position of the item in the Listview
    int itemPosition;
    String date;
    String time;

    // Item parcelled via the intent
    TodoItem todoItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveItem(view);
            }
        });

        dateRadio = (RadioButton) findViewById(R.id.radioButtonDate);
        tvDate = (TextView) findViewById(R.id.tvDate);
        timeRadio = (RadioButton) findViewById(R.id.radioButtonTime);
        tvTime = (TextView) findViewById(R.id.tvTime);

        etEditItem = (EditText) findViewById(R.id.etEditItem);

        // deal with the extras in the intent
        todoItem = getIntent().getParcelableExtra("item");
        itemPosition = getIntent().getIntExtra("itemPos", -1);

        // update the UI
        etEditItem.setText(todoItem.getText());
        etEditItem.setSelection(todoItem.getText().length());
        tvDate.setText(todoItem.getDate());
        tvTime.setText(todoItem.getTime());
    }

    public void onSaveItem(View view) {

        Intent i = new Intent();

        // setup parcel for reporting to the source activity
        todoItem.setText(etEditItem.getText().toString());
        todoItem.setDate(date);
        todoItem.setTime(time);

        i.putExtra("itemPos", itemPosition);
        i.putExtra("item", todoItem);

        setResult(RESULT_OK, i);
        finish();
    }



    public void onRadioDate(View view) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(
                this,
                this,
                year,
                month+1,
                day).show();
    }

    public void onRadioTime(View view) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        new TimePickerDialog(
                this,
                this,
                hour,
                minute, true).show();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        dateRadio.setChecked(false);
        date = year+"-"+monthOfYear+"-"+dayOfMonth;
        Log.d("Debug", "date " + date);
        tvDate.setText(date);
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timeRadio.setChecked(false);
        time = hourOfDay+":"+minute;
        Log.d("Debug", "time " + time);
        tvTime.setText(time);
    }
}
