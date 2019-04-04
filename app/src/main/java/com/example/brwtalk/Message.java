package com.example.brwtalk;

import java.util.Date;

public class Message {
    int id;
    String username;
    Date date;
    String text;

    public Message(int id, String username, Date date, String message) {
        this.id = id;
        this.username = username;
        this.date = date;
        this.text = message;
    }

    public Message(){
        //nothingtodo
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
