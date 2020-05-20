package com.example.beerstoragemanager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OrdersView extends AppCompatActivity {

    private static final String TAG = "OrdersView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
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

    private void displayToast(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
