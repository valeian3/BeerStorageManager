package com.example.beerstoragemanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beerstoragemanager.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeView extends AppCompatActivity {

    ActivityHomeBinding binding;

    private static final String TAG = "HomeView";

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
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

        binding.homeTvIdUser.setText(firebaseUser.getEmail());
        userEmail = firebaseUser.getEmail();

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
        binding.homeBtnIdWineBarrel.setOnClickListener(new View.OnClickListener() {
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
        binding.homeBtnIdBox.setOnClickListener(new View.OnClickListener() {
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
        binding.homeBtnIdList.setOnClickListener(new View.OnClickListener() {
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
        binding.homeBtnIdLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                displayToast("User " + userEmail + " signed out.");
                Intent intent = new Intent(HomeView.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
    private void OrdersHistory(){
        binding.homeBtnIdOrders.setOnClickListener(new View.OnClickListener() {
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
        binding.homeBtnIdPresets.setOnClickListener(new View.OnClickListener() {
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
