package com.example.beerstoragemanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beerstoragemanager.Model.Ingredient;
import com.example.beerstoragemanager.Model.User;
import com.example.beerstoragemanager.databinding.ActivityHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeView extends AppCompatActivity {

    ActivityHomeBinding binding;

    private static final String TAG = "HomeView";

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String userEmail;
    ArrayList<User> listOfUsersFromDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        listOfUsersFromDatabase = new ArrayList<>();

        binding.bottomNavigationMenu.setSelectedItemId(R.id.other);
        binding.bottomNavigationMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent explicitIntent = new Intent();
                switch (item.getItemId()){
                    case R.id.storageView:
                        explicitIntent.setClass(getApplicationContext(), StorageView.class);
                        explicitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        explicitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(explicitIntent);
                        overridePendingTransition(0, 0);
                        return  true;
                    case R.id.presetsView:
                        explicitIntent.setClass(getApplicationContext(), PresetsView.class);
                        explicitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        explicitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(explicitIntent);
                        overridePendingTransition(0, 0);
                        return  true;
                    case R.id.ordersView:
                        explicitIntent.setClass(getApplicationContext(), OrdersView.class);
                        explicitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        explicitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(explicitIntent);
                        overridePendingTransition(0, 0);
                        startActivity(new Intent(getApplicationContext(), OrdersView.class));
                        overridePendingTransition(0, 0);
                        return  true;
                    case R.id.other:
                        return  true;
                }
                return false;
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart executed.");
        if(firebaseUser!=null){
            displayUserData();
        }
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

        loadUsers();
        logout();
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
        finish();
        overridePendingTransition(0, 0);
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
                overridePendingTransition(0, 0);
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

    private void loadUsers(){

        databaseReference = database.getReference().child("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listOfUsersFromDatabase.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    listOfUsersFromDatabase.add(user);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                displayToast("Error: database could't load.");
            }
        });
    }

    private void displayUserData(){

        userEmail = firebaseUser.getEmail();

        for(int i = 0; i < listOfUsersFromDatabase.size(); i++){
            User userFromDatabase = listOfUsersFromDatabase.get(i);
            if(userFromDatabase.getEmail().equals(userEmail)){
                binding.homeTvIdUser.setText(userFromDatabase.getUsername());
            }else {
                displayToast("Couldn't find that user");
            }
        }

    }

    private void displayToast(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

}
