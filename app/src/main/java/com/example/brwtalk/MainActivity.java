package com.example.brwtalk;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Message> messages;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = findViewById(R.id.listView);
        messages = new ArrayList<>();
        ListViewAdapter lva = new ListViewAdapter(this, R.layout.list_view_item, messages);
        lv.setAdapter(lva);
        Message m = new Message("Flo",new Date(),"Frodo is komisch");
        messages.add(m);
    }
}
