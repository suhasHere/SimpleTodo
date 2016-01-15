package com.crypt.todo.simpletodo.model;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents each item in the database and also
 * represents the Parcelable object for Intents
 */
public class TodoItem implements Parcelable {
    private String id; //could be a url
    private String todoText;

    public TodoItem(String itemText) {
        setText(itemText);
    }

    public TodoItem(String id, String itemText) {
        setId(id);
        setText(itemText);
    }

    protected TodoItem(Parcel in) {
        id = in.readString();
        todoText = in.readString();
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.todoText = text;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return todoText;
    }

    public String toString() {
        return this.getText();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getText());
    }

    public static final Creator<TodoItem> CREATOR = new Creator<TodoItem>() {
        @Override
        public TodoItem createFromParcel(Parcel in) {
            return new TodoItem(in);
        }

        @Override
        public TodoItem[] newArray(int size) {
            return new TodoItem[size];
        }
    };
}
