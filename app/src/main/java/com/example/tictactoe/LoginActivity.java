package com.example.tictactoe;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText editPassword,editEmail;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editPassword = (EditText) findViewById(R.id.editPassword);
        editEmail = (EditText) findViewById(R.id.editEmail);

        mAuth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }
    public void onSignInClick(View view){
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

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
        mAuth.signInWithEmailAndPassword(email,password)
               .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       progressBar.setVisibility(View.GONE);
                       if (!task.isSuccessful()){
                           Toast.makeText(LoginActivity.this,"Failed",Toast.LENGTH_LONG).show();
                       }else {
                           Toast.makeText(LoginActivity.this,"Success",Toast.LENGTH_LONG).show();
                       }

                   }
               });

    }
}
