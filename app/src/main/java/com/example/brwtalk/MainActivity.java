package com.example.brwtalk;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static int activeIdInt = 1;
    FirebaseFirestore db;
    String collectionName;
    List<Message> messages;
    ListView lv;
    ListViewAdapter lva;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = findViewById(R.id.listView);
        db = FirebaseFirestore.getInstance();
        collectionName = "BRWTalk";
        messages = new ArrayList<>();
        lva = new ListViewAdapter(this, R.layout.list_view_item, messages);
        lv.setAdapter(lva);
        Message msg = new Message(activeIdInt,"Frodo",new Date(),"123");
        //addMessageToDb(msg);
        lv.setAdapter(lva);
        readDataFromDb();
    }

    private void readDataFromDb() {
        db.collection(collectionName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Message msg = document.toObject(Message.class);
                                activeIdInt = msg.getId();
                                activeIdInt++;
                                messages.add(msg);
                            }
                        } else {
                            System.out.println("#######################################################################################################");
                        }
                    }
                });
    }

    private void addMessageToDb(Message msg)
    {
        db.collection("BRWTalk")
                .add(msg)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("LOG", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("LOG", "Error adding document", e);
                    }
                });
    }
}