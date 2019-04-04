package com.example.brwtalk;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    static int activeIdInt = 1;
    FirebaseFirestore db;
    String collectionName;
    List<Message> messages;
    ListView lv;
    ListViewAdapter lva;
    View view;
    AlertDialog dialog;
    TextView status;
    EditText editTextUserName;
    EditText editTextPassword;
    Button buttonSign;
    String email;
    String password;
    AlertDialog.Builder alert;
    FloatingActionButton fab;
    EditText message;
    String usernameFlo = "Flo";
    FloatingActionButton refreshList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = View.inflate(this, R.layout.authentication_layout, null);
        //editTextUserName = view.findViewById(R.id.editTextUserName);
        //editTextPassword = view.findViewById(R.id.editTextPassword);
        dialog(view);
        lv = findViewById(R.id.listView);
        db = FirebaseFirestore.getInstance();
        fab = findViewById(R.id.floatingActionButton);
        message = findViewById(R.id.editText);
        collectionName = "BRWTalk";
        mAuth = FirebaseAuth.getInstance();
        messages = new ArrayList<>();
        lva = new ListViewAdapter(this, R.layout.list_view_item, messages);
        lv.setAdapter(lva);
        lv.invalidateViews();

        //lv.setAdapter(lva);
        //readDataFromDb();

        reactOnAdded();
        sendMessage();
    }

    private void sendMessage() {
        fab.setOnClickListener(view -> {
            String s = message.getText().toString();
            if (!s.equals("")) {
                Message m = new Message(activeIdInt, usernameFlo, new Date(), s);
                //messages.add(m);
                //messages.add(m);
                addMessageToDb(m);
                String x = "";
                message.setText(x);
                lv.invalidateViews();
                activeIdInt++;
            } else {
                Toast enter_a_message = Toast.makeText(MainActivity.this, "Error: Please enter a Message", Toast.LENGTH_LONG);
                enter_a_message.show();
            }
        });
    }

    private void reactOnAdded() {
        db.collection(collectionName)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("", "listen:error", e);
                            return;
                        }

                        List<Message> tempMessages = new ArrayList<>();
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Message m = dc.getDocument().toObject(Message.class);
                                    tempMessages.add(m);
                                    //lv.invalidateViews();

                                    break;
                                case MODIFIED:
                                    Message m2 = dc.getDocument().toObject(Message.class);
                                    messages.add(m2);
                                    lv.invalidateViews();
                                    break;
                                case REMOVED:
                                    Message m3 = dc.getDocument().toObject(Message.class);
                                    messages.add(m3);
                                    lv.invalidateViews();
                                    break;
                            }
                        }
                        sortListByDate(tempMessages);
                        lva.notifyDataSetChanged();
                       // lva.notifyDataSetInvalidated();
                    }
                });
    }

    private void sortListByDate(List<Message> tempMessages) {
        List<Message> addthis = tempMessages.stream()
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toList());

        messages.addAll(addthis);
    }

    public void dialog(View view) {
        alert = new AlertDialog.Builder(this);
        alert.setView(view).setCancelable(false);

        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        dialog = alert.create();

        alert.setPositiveButton("Ok", (dialogInterface, i) -> {
            usernameFlo = editTextUserName.getText().toString();
            password = editTextPassword.getText().toString();
            //signInUser(email,password);
            //test
        });

        //buttonSign = view.findViewById(R.id.button);
        editTextUserName = view.findViewById(R.id.editTextUserName);
        editTextPassword = view.findViewById(R.id.editTextPassword);

        status = view.findViewById(R.id.textViewStatus);

        String s = "SignIn";
        status.setText(s);
        alert.show();
    }
    /*@Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }*/

    @Deprecated
    private void readDataFromDb() {
        db.collection(collectionName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                Message msg = doc.toObject(Message.class);
                                messages.add(msg);
                                activeIdInt = msg.getId() + 1;
                                lv.invalidateViews();
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void addMessageToDb(Message msg) {
        db.collection(collectionName)
                .add(msg)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void signUpUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LOG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LOG", "createUserWithEmail:failure", task.getException());
                        }

                        // ...
                    }
                });
    }

    private void signInUser(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LOG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            signUpUser(email, password);
                        }
                    }
                });
    }
}