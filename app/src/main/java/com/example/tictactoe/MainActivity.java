package com.example.tictactoe;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText editName,editEmail,editPassword;
    private Button buttonSignWindow;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference dataRef;


    private String myUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editName = (EditText) findViewById(R.id.editName);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        dataRef = database.getReference("Users");

        //if already login go to choose player
        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(this,ChoosePlayerActivity.class));
            finish();
        }

    }
    public void onRegisterClick(View view){
        final String username = editName.getText().toString();
        final String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        if (username.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter Username",Toast.LENGTH_LONG).show();
            return;
        }
        if (email.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter Email",Toast.LENGTH_LONG).show();
            return;
        }
        if (password.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter Password",Toast.LENGTH_LONG).show();
            return;
        }
        if (password.length() < 6 ){
            Toast.makeText(getApplicationContext(),"Password is too short",Toast.LENGTH_LONG).show();
            return;
       }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"Authintication Failed"+task.getException(),Toast.LENGTH_LONG).show();
                            Log.e("Mytag",task.getException().toString());
                        }else {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            myUid = user.getUid();

                            User myUser = new User(username,email,myUid);
                            dataRef.child(myUid).setValue(myUser);

                            startActivity(new Intent(MainActivity.this,ChoosePlayerActivity.class));
                            finish();

                        }
                    }
                });

    }
    public void onSignInWindowClick(View view){
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }
}
