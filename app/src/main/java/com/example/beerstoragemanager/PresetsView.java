package com.example.beerstoragemanager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.beerstoragemanager.Controller.IngredientsListInPresetsController;
import com.example.beerstoragemanager.Controller.PresetsListController;
import com.example.beerstoragemanager.Model.Beer;
import com.example.beerstoragemanager.Model.Ingredient;
import com.example.beerstoragemanager.databinding.ActivityPresetsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class PresetsView extends AppCompatActivity{

    ActivityPresetsBinding binding;

    private static final String TAG = "PresetsView";

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    Beer beer;
    Ingredient ingredient;

    List<Ingredient> listOfIngredientsFromPresets, listOfIngredientsFromStorage, newListOfIngredientsForStorage;
    List<Beer> listOfAvailableBeerPresets;

    String presetId, presetName;
    Boolean checkForSelectedPreset = false, checkIfSelectedPreset = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_presets);

        binding = ActivityPresetsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database = FirebaseDatabase.getInstance();

        listOfAvailableBeerPresets = new ArrayList<>();
        listOfIngredientsFromPresets = new ArrayList<>();
        listOfIngredientsFromStorage = new ArrayList<>();
        newListOfIngredientsForStorage = new ArrayList<>();

        binding.bottomNavigationMenu.setSelectedItemId(R.id.presetsView);
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
                        return  true;
                    case R.id.ordersView:
                        explicitIntent.setClass(getApplicationContext(), OrdersView.class);
                        explicitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        explicitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(explicitIntent);
                        overridePendingTransition(0, 0);
                        return  true;
                    case R.id.other:
                        explicitIntent.setClass(getApplicationContext(), HomeView.class);
                        explicitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        explicitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(explicitIntent);
                        overridePendingTransition(0, 0);
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
        binding.bottomNavigationMenu.setSelectedItemId(R.id.presetsView);
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
        binding.bottomNavigationMenu.setSelectedItemId(R.id.presetsView);

        availablePresets();
        loadStorageIngredients();

        binding.presetsFabIdAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent explicitIntent = new Intent();
                explicitIntent.setClass(getApplicationContext(), AddingNewPresetView.class);
                startActivity(explicitIntent);
            }
        });
        binding.presetsBtnIdSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForSelectedPreset && onPresetSelectDeleteFromStorage()){
                    selectedPresets();

                    Intent explicitIntent = new Intent();
                    explicitIntent.setClass(getApplicationContext(), PresetHistoryView.class);
                    explicitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(explicitIntent);
                    overridePendingTransition(0, 0);
                    checkIfSelectedPreset = true;
                }else {
                    displayToast("Select preset");
                }
            }
        });
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
        if(checkIfSelectedPreset){
            finishAndRemoveTask();
            overridePendingTransition(0, 0);
            checkIfSelectedPreset = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy executed.");
        finishAndRemoveTask();
        overridePendingTransition(0, 0);
    }

    private void availablePresets(){
        databaseReference = database.getReference("Presets");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listOfAvailableBeerPresets.clear();
                for(DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()){
                    Beer beer = ingredientSnapshot.getValue(Beer.class);
                    listOfAvailableBeerPresets.add(beer);
                }
                PresetsListController adapter = new PresetsListController(PresetsView.this, listOfAvailableBeerPresets);
                binding.presetsListIdPresets.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                displayToast("Error: database could't load.");
            }
        });
        binding.presetsListIdPresets.setLongClickable(true);
        binding.presetsListIdPresets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Beer beer = listOfAvailableBeerPresets.get(position);
               presetId = beer.getBeerId();
               presetName = beer.getName();
               checkForSelectedPreset = true;
               displayIngredients();
            }
        });
        binding.presetsListIdPresets.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Beer beer = listOfAvailableBeerPresets.get(position);
                presetId = beer.getBeerId();
                deleteDialog(presetId);
                return false;
            }
        });
    }

    private void displayIngredients(){
        databaseReference = database.getReference("Preset ingredients").child(presetId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listOfIngredientsFromPresets.clear();
                for(DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()){
                    Ingredient ingredient = ingredientSnapshot.getValue(Ingredient.class);
                    listOfIngredientsFromPresets.add(ingredient);
                }
                IngredientsListInPresetsController adapter = new IngredientsListInPresetsController(PresetsView.this, listOfIngredientsFromPresets);
                binding.presetsListIdIngredients.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                displayToast("Error: database could't load.");
            }
        });
    }

    private void selectedPresets(){
        if(onPresetSelectDeleteFromStorage()){
            databaseReference = database.getReference().child("Selected presets");
            if(!TextUtils.isEmpty(presetName)){

                String id = databaseReference.push().getKey();

                beer = new Beer(id, presetName);
                databaseReference.child(id).setValue(beer);
                Log.i(TAG, "Preset inserted into database");
            } else {
                Log.i(TAG, "Preset is not inserted into database.");
            }
        }else {
            displayToast("Not enough ingredients in storage");
        }
    }

    private void loadStorageIngredients(){
        databaseReference = database.getReference().child("Ingredients");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listOfIngredientsFromStorage.clear();
                for (DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()){
                    Ingredient ingredient = ingredientSnapshot.getValue(Ingredient.class);
                    listOfIngredientsFromStorage.add(ingredient);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                displayToast("Error: database could't load.");
            }
        });
    }

    private boolean onPresetSelectDeleteFromStorage(){

        Boolean checkIfAllIngredientsAreInStorage = true;

        for(int i = 0; i < listOfIngredientsFromPresets.size(); i++){
            Ingredient presetIngredient = listOfIngredientsFromPresets.get(i);
            for(int j = 0; j < listOfIngredientsFromStorage.size(); j++){
                Ingredient storageIngredient = listOfIngredientsFromStorage.get(j);
                if(presetIngredient.getName().equals(storageIngredient.getName())){
                    int presetAmount = parseInt(presetIngredient.getAmount());
                    int storageAmount = parseInt(storageIngredient.getAmount());
                    if(storageAmount >= presetAmount){
                        storageAmount -= presetAmount;
                    }else{
                        checkIfAllIngredientsAreInStorage = false;
                    }
                    ingredient = new Ingredient(storageIngredient.getIngredientId(), storageIngredient.getName(), String.valueOf(storageAmount));
                    newListOfIngredientsForStorage.add(ingredient);
                }
                //This should check if ingredient exists in storage but it doesn't(Error!)
                /*else{
                    *//*checkIfAllIngredientsAreInStorage = true;*//*
                    displayToast("That ingredient does not exists in storage");
                    break;
                }*/
            }
        }
        if(checkIfAllIngredientsAreInStorage){
            for(int k = 0; k < newListOfIngredientsForStorage.size(); k++){
                Ingredient newStorageIngredient = newListOfIngredientsForStorage.get(k);
                databaseReference = database.getReference().child("Ingredients");
                if(!TextUtils.isEmpty(String.valueOf(newStorageIngredient.getAmount()))){
                    databaseReference.child(newStorageIngredient.getIngredientId()).setValue(newStorageIngredient);
                    Log.i(TAG, "Amount changed in database");
                }else {
                    Log.i(TAG, "Amount didn't changed in database");
                }
            }
        }else {
            displayToast("Could't select preset, not enough ingredients");
        }
        return checkIfAllIngredientsAreInStorage;
    }

    private void deleteDialog(final String id){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PresetsView.this);
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
                databaseReference = FirebaseDatabase.getInstance().getReference("Presets").child(id);
                databaseReference.removeValue();
                databaseReference = FirebaseDatabase.getInstance().getReference("Preset ingredients").child(id);
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