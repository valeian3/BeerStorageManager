package com.example.beerstoragemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beerstoragemanager.Controller.IngredientListController;
import com.example.beerstoragemanager.Controller.PresetsListController;
import com.example.beerstoragemanager.Model.Beer;
import com.example.beerstoragemanager.Model.Ingredient;
import com.example.beerstoragemanager.databinding.ActivityPresetsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PresetsView extends AppCompatActivity{

    ActivityPresetsBinding binding;

    private static final String TAG = "PresetsView";

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    Beer beer;

    List<Ingredient> ingredientList;
    List<Beer> beerList;
    String presetId, presetName;
    Boolean checkForSelectedPreset = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_presets);

        binding = ActivityPresetsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database = FirebaseDatabase.getInstance();

        beerList = new ArrayList<>();
        ingredientList = new ArrayList<>();
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

        availablePresets();

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
                if (checkForSelectedPreset){
                    selectedPresets();
                    Intent explicitIntent = new Intent();
                    explicitIntent.setClass(getApplicationContext(), PresetHistoryView.class);
                    startActivity(explicitIntent);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy executed.");
        finishAndRemoveTask();
    }

    private void availablePresets(){
        databaseReference = database.getReference("Presets");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                beerList.clear();
                for(DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()){
                    Beer beer = ingredientSnapshot.getValue(Beer.class);
                    beerList.add(beer);
                }
                PresetsListController adapter = new PresetsListController(PresetsView.this, beerList);
                binding.presetsListIdPresets.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                displayToast("Error: database could't load.");
            }
        });
        binding.presetsListIdPresets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Beer beer = beerList.get(position);
               presetId = beer.getBeerId();
               presetName = beer.getName();
               checkForSelectedPreset = true;
               displayIngredients();
            }
        });
    }

    private void displayIngredients(){
        databaseReference = database.getReference("Preset ingredients").child(presetId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ingredientList.clear();
                for(DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()){
                    Ingredient ingredient = ingredientSnapshot.getValue(Ingredient.class);
                    ingredientList.add(ingredient);
                }
                IngredientListController adapter = new IngredientListController(PresetsView.this, ingredientList);
                binding.presetsListIdIngredients.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                displayToast("Error: database could't load.");
            }
        });
    }

    private void selectedPresets(){
        databaseReference = database.getReference().child("Selected presets");
        if(!TextUtils.isEmpty(presetName)){

            String id = databaseReference.push().getKey();

            beer = new Beer(id, presetName);
            databaseReference.child(id).setValue(beer);
            Log.i(TAG, "Preset inserted into database");
        } else {
            Log.i(TAG, "Preset is not inserted into database.");
        }
    }

    private void displayToast(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}