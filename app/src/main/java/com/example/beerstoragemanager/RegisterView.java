package com.example.beerstoragemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beerstoragemanager.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterView extends AppCompatActivity {

    TextView tvIdAlreadyHaveAccount;
    EditText etIdName, etIdEmail, etIdUsername, etIdPassword;
    Button btnSignUp;

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        btnSignUp = findViewById(R.id.register_btnIdSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterUser();
            }
        });
        AlreadyHaveAccount();
    }

    private void RegisterUser(){

        User user;

        etIdName = findViewById(R.id.register_etIdName);
        etIdEmail = findViewById(R.id.register_etIdEmail);
        etIdUsername = findViewById(R.id.register_etIdUsername);
        etIdPassword = findViewById(R.id.register_etIdPassword);

        String name = etIdName.getText().toString().trim();
        String email = etIdEmail.getText().toString().trim();
        String username = etIdUsername.getText().toString().trim();
        String password = etIdPassword.getText().toString().trim();

        //Ako su prazna polja za unos podataka
        if(name.isEmpty()){
            etIdName.setError("Name is required");
            etIdName.requestFocus();
            return;
        }
        if(email.isEmpty()){
            etIdEmail.setError("Email is required");
            etIdEmail.requestFocus();
            return;
        }
        if(username.isEmpty()){
            etIdUsername.setError("Username is required");
            etIdUsername.requestFocus();
            return;
        }
        if(password.isEmpty()){
            etIdPassword.setError("Password is required");
            etIdPassword.requestFocus();
            return;
        }

        //Ostale provjere unesenih polja
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etIdEmail.setError("Please enter a valid email");
            etIdEmail.requestFocus();
            return;
        }
        if(password.length()<6){
            etIdPassword.setError("Minimum length of password should be 6");
            etIdPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    displayToast("User Registered Sucessfully.");
                    Intent explicitIntent = new Intent();
                    explicitIntent.setClass(getApplicationContext(), MainActivity.class);
                    startActivity(explicitIntent);
                }else{
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        displayToast("This account already exist.");
                    }else{
                        displayToast(task.getException().getMessage());
                    }
                }
            }
        });

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("User");
        if(!TextUtils.isEmpty(name)){

            String id = databaseReference.push().getKey();

            user = new User(id, name, email, username, password);
            databaseReference.child(id).setValue(user);
            displayToast("User inserted into database");
        } else {
            displayToast("User's name cannot stay empty.");
        }
    }

    private void AlreadyHaveAccount(){

        tvIdAlreadyHaveAccount = findViewById(R.id.register_tvIdAlreadyHaveAccount);
        tvIdAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent explicitIntent = new Intent();
                explicitIntent.setClass(getApplicationContext(), MainActivity.class);
                startActivity(explicitIntent);
            }
        });
    }

    private void displayToast(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}