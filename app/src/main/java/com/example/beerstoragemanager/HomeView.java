package com.example.beerstoragemanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomeView extends AppCompatActivity {

    private static final String TAG = "HomeView";

    Button btnIdLogout, btnStorage, btnPresets, btnOrders, btnOrdersHistory, btnPresetsHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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

        logout();
        Storage();
        Orders();
        Presets();
        OrdersHistory();
        PresetsHistory();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy executed.");
    }

    private void Storage(){
        btnStorage = findViewById(R.id.home_btnIdWineBarrel);
        btnStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent explicitIntent = new Intent();
                explicitIntent.setClass(getApplicationContext(), StorageView.class);
                startActivity(explicitIntent);
                Log.i(TAG, "Storage view opened.");
            }
        });
    }
    private void Orders(){
        btnOrders = findViewById(R.id.home_btnIdBox);
        btnOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent explicitIntent = new Intent();
                explicitIntent.setClass(getApplicationContext(), OrdersView.class);
                startActivity(explicitIntent);
                Log.i(TAG, "Orders view opened.");
            }
        });
    }
    private void Presets(){
        btnPresets = findViewById(R.id.home_btnIdList);
        btnPresets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent explicitIntent = new Intent();
                explicitIntent.setClass(getApplicationContext(), PresetsView.class);
                startActivity(explicitIntent);
                Log.i(TAG, "Presets view opened.");
            }
        });
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
    private void OrdersHistory(){
        btnOrdersHistory = findViewById(R.id.home_btnIdOrders);
        btnOrdersHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent explicitIntent = new Intent();
                explicitIntent.setClass(getApplicationContext(), OrdersHistoryView.class);
                startActivity(explicitIntent);
                Log.i(TAG, "Presets history view opened.");
            }
        });

    }
    private void PresetsHistory(){
        btnPresetsHistory = findViewById(R.id.home_btnIdPresets);
        btnPresetsHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent explicitIntent = new Intent();
                explicitIntent.setClass(getApplicationContext(), PresetHistoryView.class);
                startActivity(explicitIntent);
                Log.i(TAG, "Presets history view opened.");
            }
        });
    }
    private void displayToast(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

}
