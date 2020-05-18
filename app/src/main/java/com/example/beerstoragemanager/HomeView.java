package com.example.beerstoragemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomeView extends AppCompatActivity {

    Button btnIdLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        logout();
    }

    private void logout(){
        btnIdLogout = findViewById(R.id.home_btnIdLogout);
        btnIdLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                displayToast("Logged out.");
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
