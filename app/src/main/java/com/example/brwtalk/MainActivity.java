package com.example.brwtalk;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    EditText editTextEmail;
    EditText editTextPassword;
    Button buttonSign;
    String email;
    String password;
    AlertDialog.Builder alert;
    FloatingActionButton fab;
    EditText message;
    String username = "Flo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = View.inflate(this,R.layout.authentication_layout,null);
        editTextEmail = view.findViewById(R.id.textViewUsername);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        //dialog(view);
        lv = findViewById(R.id.listView);
        db = FirebaseFirestore.getInstance();
        fab = findViewById(R.id.floatingActionButton);
        message = findViewById(R.id.editText);
        collectionName = "BRWTalk";
        mAuth = FirebaseAuth.getInstance();
        messages = new ArrayList<>();
        lva = new ListViewAdapter(this, R.layout.list_view_item, messages);
        lv.setAdapter(lva);
        lv.setAdapter(lva);
        readDataFromDb();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = message.getText().toString();
                if(!s.equals("")) {
                    Message m = new Message(activeIdInt, username, new Date(), s);
                    messages.add(m);
                    addMessageToDb(m);
                    String x = "";
                    message.setText(x);
                    lv.invalidateViews();
                    activeIdInt++;
                }
                else
                {
                    Toast enter_a_message = Toast.makeText(MainActivity.this, "Error: Please enter a Message", Toast.LENGTH_LONG);
                    enter_a_message.show();
                }
            }
        });
        DocumentReference contactListener = db.collection("PhoneBook").document("Contacts");

        contactListener.addSnapshotListener(new EventListener<DocumentSnapshot>() {

            @Override

            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (e != null) {

                    Log.d("ERROR", e.getMessage());

                    return;

                }

                if (documentSnapshot != null && documentSnapshot.exists()) {

                    Toast.makeText(MainActivity.this, "Current data:" + documentSnapshot.getData(), Toast.LENGTH_SHORT).show();

                }

            }

        });
    }

    public void dialog(View view){
        alert = new AlertDialog.Builder(this);
        alert.setView(view).setCancelable(false);
        ViewGroup parent = (ViewGroup) view.getParent();
        if(parent != null)
        {
            parent.removeView(view);
        }
        dialog = alert.create();
        buttonSign = view.findViewById(R.id.button);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        buttonSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                //signInUser(email,password);
            }
        });
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

    private void readDataFromDb() {
        db.collection(collectionName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        Message loadedMessage = document.toObject(Message.class);
                        messages.add(loadedMessage);
                    }
                }
            }
        });
    }

    private void addMessageToDb(Message msg)
    {
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

    private void signUpUser(String email, String password)
    {
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

    private void signInUser(final String email, final String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LOG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            signUpUser(email,password);
                        }
                    }
                });
    }
}