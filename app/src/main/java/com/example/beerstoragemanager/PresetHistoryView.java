package com.example.beerstoragemanager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beerstoragemanager.Controller.PresetsListController;
import com.example.beerstoragemanager.Model.Beer;
import com.example.beerstoragemanager.databinding.ActivityPresetsHistoryBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PresetHistoryView extends AppCompatActivity {

    ActivityPresetsHistoryBinding binding;

    private static final String TAG = "PresetHistoryView";

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    Beer beer;
    List<Beer> presetsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_presets_history);

        binding = ActivityPresetsHistoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database = FirebaseDatabase.getInstance();

        presetsList = new ArrayList<>();

        binding.bottomNavigationMenu.setSelectedItemId(R.id.presetsHistory);
        binding.bottomNavigationMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.storageView:
                        startActivity(new Intent(getApplicationContext(), StorageView.class));
                        overridePendingTransition(0, 0);
                        return  true;
                    case R.id.presetsView:
                        startActivity(new Intent(getApplicationContext(), PresetsView.class));
                        overridePendingTransition(0, 0);
                        return  true;
                    case R.id.ordersView:
                        startActivity(new Intent(getApplicationContext(), OrdersView.class));
                        overridePendingTransition(0, 0);
                        return  true;
                    case R.id.ordersHistory:
                        startActivity(new Intent(getApplicationContext(), OrdersHistoryView.class));
                        overridePendingTransition(0, 0);
                        return  true;
                    case R.id.presetsHistory:
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

        displayPresets();
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
        finishAndRemoveTask();
        overridePendingTransition(0, 0);
    }

    private void displayPresets(){
        databaseReference = database.getReference("Selected presets");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                presetsList.clear();
                for(DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()){
                    Beer beer = ingredientSnapshot.getValue(Beer.class);
                    presetsList.add(beer);
                }
                PresetsListController adapter = new PresetsListController(PresetHistoryView.this, presetsList);
                binding.presetsHistoryListIdPresets.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                displayToast("Error: database could't load.");
            }
        });
        binding.presetsHistoryListIdPresets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                beer = presetsList.get(position);
                deleteDialog(beer.getBeerId());
            }
        });
    }

    private void deleteDialog(final String id){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PresetHistoryView.this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.history_dialog,null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView tvYes, tvNo;
        final Button btnExit;

        tvYes = alertDialog.findViewById(R.id.history_dialog_tvIdYes);
        tvNo = alertDialog.findViewById(R.id.history_dialog_tvIdNo);
        btnExit = alertDialog.findViewById(R.id.history_dialog_btnIdExit);

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference = FirebaseDatabase.getInstance().getReference("Selected presets").child(id);
                databaseReference.removeValue();
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                alertDialog.dismiss();
            }
        });
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                alertDialog.dismiss();
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                alertDialog.dismiss();
            }
        });
    }

    private void displayToast(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}