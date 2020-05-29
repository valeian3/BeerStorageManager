package com.example.beerstoragemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beerstoragemanager.Model.User;
import com.example.beerstoragemanager.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterView extends AppCompatActivity {

    ActivityRegisterBinding binding;

    private static final String TAG = "RegisterView";

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_register);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        //firebaseUser = firebaseAuth.getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart executed.");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart executed.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume executed.");

        binding.registerBtnIdSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterUser();
            }
        });
        AlreadyHaveAccount();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause executed.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop executed.");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy executed.");
        finishAndRemoveTask();
    }

    private void RegisterUser(){
        String name = binding.registerEtIdName.getText().toString().trim();
        String email = binding.registerEtIdEmail.getText().toString().trim();
        String username = binding.registerEtIdUsername.getText().toString().trim();
        String password = binding.registerEtIdPassword.getText().toString().trim();

        if(name.isEmpty()){
            binding.registerEtIdName.setError("Name is required");
            binding.registerEtIdName.requestFocus();
            return;
        }
        if(email.isEmpty()){
            binding.registerEtIdEmail.setError("Email is required");
            binding.registerEtIdEmail.requestFocus();
            return;
        }
        if(username.isEmpty()){
            binding.registerEtIdUsername.setError("Username is required");
            binding.registerEtIdUsername.requestFocus();
            return;
        }
        if(password.isEmpty()){
            binding.registerEtIdPassword.setError("Password is required");
            binding.registerEtIdPassword.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.registerEtIdEmail.setError("Please enter a valid email");
            binding.registerEtIdEmail.requestFocus();
            return;
        }
        if(password.length() <= 6){
            binding.registerEtIdPassword.setError("Minimum length of password should be 6");
            binding.registerEtIdPassword.requestFocus();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                    firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                displayToast("Registered successfully. Please verify your email address");
                                Intent explicitIntent = new Intent();
                                explicitIntent.setClass(getApplicationContext(), MainActivity.class);
                                startActivity(explicitIntent);
                            }else {
                                displayToast(task.getException().getMessage());
                            }
                        }
                    });
                }else{
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        displayToast("This account already exist.");
                    }else{
                        displayToast(task.getException().getMessage());
                    }
                }
            }
        });

        databaseReference = database.getReference().child("User");
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(username)){

            String id = databaseReference.push().getKey();

            user = new User(id, name, email, username);
            databaseReference.child(id).setValue(user);
            Log.i(TAG, "User inserted into database");
            binding.registerEtIdName.setText("");
            binding.registerEtIdEmail.setText("");
            binding.registerEtIdUsername.setText("");
            binding.registerEtIdPassword.setText("");
        } else {
            Log.i(TAG, "User is not inserted into database.");
        }
    }

    private void AlreadyHaveAccount(){
        binding.registerTvIdAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
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
