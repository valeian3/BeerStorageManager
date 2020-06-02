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
import com.example.beerstoragemanager.Model.Beer;
import com.example.beerstoragemanager.Model.Ingredient;
import com.example.beerstoragemanager.databinding.ActivityAddingNewPresetsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddingNewPresetView extends AppCompatActivity {

    ActivityAddingNewPresetsBinding binding;

    private static final String TAG = "AddingNewPresetsView";

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    List<Ingredient> ingredientList;
    boolean checkForIngredient = false;
    Ingredient ingredient;
    Beer beer;
    String beerNameId, presetBeerName, ingredientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_adding_new_presets);

        binding = ActivityAddingNewPresetsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database = FirebaseDatabase.getInstance();

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

        binding.addingNewPresetsBtnIdAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingIngredient();
                displayIngredients();
            }
        });

        binding.addingNewPresetsBtnIdAddPreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkForIngredient){
                    Intent explicitIntent = new Intent();
                    explicitIntent.setClass(getApplicationContext(), PresetsView.class);
                    explicitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(explicitIntent);
                    overridePendingTransition(0, 0);
                }
            }
        });

        binding.addingNewPresetRlId.setVisibility(View.GONE);

        binding.addingNewPresetsBtnIdNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addName();
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
        deletePreset();
        finishAndRemoveTask();
        overridePendingTransition(0, 0);
    }

    public void addName(){
        String beerName = binding.addingNewPresetsEtIdName.getText().toString().trim();

        if(beerName.isEmpty()){
            binding.addingNewPresetsEtIdName.setError("Beer name is required");
            binding.addingNewPresetsEtIdName.requestFocus();
            return;
        }else{
            binding.addingNewPresetRlId.setVisibility(View.VISIBLE);
            binding.addingNewPresetsBtnIdNext.setVisibility(View.GONE);
        }
        presetBeerName = beerName;

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Presets");
        if(!TextUtils.isEmpty(beerName)){

            String id = databaseReference.push().getKey();
            beerNameId = id;

            beer = new Beer(id, beerName);
            databaseReference.child(id).setValue(beer);
            Log.i(TAG, "Preset inserted into database");
        } else {
            Log.i(TAG, "Preset is not inserted into database.");
        }
    }

    private void addingIngredient(){
        String IngredientName = binding.addingNewPresetsEtIdIngredients.getText().toString().trim();
        String amount = binding.addingNewPresetsEtIdAmount.getText().toString();

        if(IngredientName.isEmpty() && amount.isEmpty()){
            binding.addingNewPresetsEtIdIngredients.setError("Ingredient name is required");
            binding.addingNewPresetsEtIdIngredients.requestFocus();
            binding.addingNewPresetsEtIdAmount.setError("Ingredient amount is required");
            binding.addingNewPresetsEtIdAmount.requestFocus();
            checkForIngredient = false;
            return;
        }else {
            checkForIngredient = true;
        }

        databaseReference = database.getReference().child("Preset ingredients").child(beerNameId);
        if(!TextUtils.isEmpty(IngredientName)){

            String id = databaseReference.push().getKey();
            ingredientId = id;

            ingredient = new Ingredient(id, IngredientName, amount);
            databaseReference.child(id).setValue(ingredient);
            Log.i(TAG, "Ingredient inserted into database");
            binding.addingNewPresetsEtIdIngredients.getText().clear();
            binding.addingNewPresetsEtIdAmount.getText().clear();
        } else {
            Log.i(TAG, "Ingredient is not inserted into database.");
        }
    }

    private void displayIngredients(){
        databaseReference = database.getReference("Preset ingredients").child(beerNameId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ingredientList.clear();
                for(DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()){
                    Ingredient ingredient = ingredientSnapshot.getValue(Ingredient.class);
                    ingredientList.add(ingredient);
                }
                IngredientListController adapter = new IngredientListController(AddingNewPresetView.this, ingredientList);
                binding.addingNewPresetsListIdIngredients.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                displayToast("Error: database could't load.");
            }
        });

        binding.addingNewPresetsListIdIngredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ingredient = ingredientList.get(position);
                databaseReference = FirebaseDatabase.getInstance().getReference("Preset ingredients").child(beerNameId).child(ingredient.getIngredientId());
                databaseReference.removeValue();
            }
        });
    }

    private void deletePreset(){
        if(ingredientList.isEmpty()){
            databaseReference = FirebaseDatabase.getInstance().getReference("Presets").child(beerNameId);
            databaseReference.removeValue();
        }
    }

    private void displayToast(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
