package com.crypt.todo.simpletodo.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crypt.todo.simpletodo.R;
import com.crypt.todo.simpletodo.model.TodoItem;

import java.util.List;
import java.util.Map;


/**
 * Custom List Adapter.
 * Includes 2 Text Views and one Image View
 */
public class TodoListAdapter extends ArrayAdapter<TodoItem> {

    private final Activity mContext;
    private final List<TodoItem> mTodoItems;
    private final Map<String, Integer> mImages;

    public TodoListAdapter(Activity context, List<TodoItem> objects, Map<String, Integer> icons) {
        super(context, R.layout.todo_list_adapter_layout, objects);
        this.mContext = context;
        this.mTodoItems = objects;
        this.mImages = icons;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = mContext.getLayoutInflater();
        View rowView = layoutInflater.inflate(R.layout.todo_list_adapter_layout, null, true);
        TextView itemText = (TextView) rowView.findViewById(R.id.itemname);
        TextView itemDateInfo = (TextView) rowView.findViewById(R.id.itemdateInfo);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon_smiley);
        TodoItem item = mTodoItems.get(position);
        itemText.setText(mTodoItems.get(position).getText() + "\n");
        itemDateInfo.setText(item.getDate() + " " + item.getTime());
        imageView.setImageResource(mImages.get(item.getStatus()));
        return rowView;
    }
}
