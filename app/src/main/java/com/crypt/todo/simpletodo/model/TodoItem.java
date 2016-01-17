package com.crypt.todo.simpletodo.model;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents each item in the database and also
 * represents the Parcelable object for Intents
 */
public class TodoItem implements Parcelable {
    private String mId; //could be a url
    private String mTodoText;
    private String mDate;
    private String mTime;
    private String mStatus = "good"; // start as not expired, happy status

    public TodoItem(String itemText) {
        setText(itemText);
        setDate("");
        setTime("");
    }


    public TodoItem(String id, String itemText, String date, String time, String status) {
        setId(id);
        setText(itemText);
        setDate(date);
        setTime(time);
        setStatus(status);
    }

    protected TodoItem(Parcel in) {
        mId = in.readString();
        mTodoText = in.readString();
        mDate = in.readString();
        mTime = in.readString();
        mStatus = in.readString();
    }


    public void setId(String id) {
        this.mId = id;
    }

    public void setText(String text) {
        this.mTodoText = text;
    }

    public String getId() {
        return mId;
    }

    public String getText() {
        return mTodoText;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        this.mTime = time;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        this.mStatus = status;
    }


    public String toString() {
        String result = this.getText() + "\n" + this.getDate() +"\t ...." + this.getTime();
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getText());
        dest.writeString(getDate());
        dest.writeString(getTime());
        dest.writeString(getStatus());
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
