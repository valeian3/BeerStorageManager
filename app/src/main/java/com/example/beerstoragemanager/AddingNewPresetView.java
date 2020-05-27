package com.example.beerstoragemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beerstoragemanager.Controller.IngredientListController;
import com.example.beerstoragemanager.Model.Beer;
import com.example.beerstoragemanager.Model.Ingredient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddingNewPresetView extends AppCompatActivity {

    private static final String TAG = "AddingNewPresetsView";
    int amount;

    Button btnAddIngredient, btnAddPreset, btnAddBeerName;
    EditText etBeerName, etIngredientName, etIngredientAmount;
    ListView listViewIngredients;
    List<Ingredient> ingredientList;

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    String beerNameId, presetBeerName, ingredientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_new_presets);
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

        btnAddIngredient = findViewById(R.id.adding_new_presets_btnIdAddIngredient);
        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingIngredient();
                displayIngredients();
            }
        });

        btnAddPreset = findViewById(R.id.adding_new_presets_btnIdAddPreset);
        btnAddPreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingPreset();
                Intent explicitIntent = new Intent();
                explicitIntent.setClass(getApplicationContext(), PresetHistoryView.class);
                startActivity(explicitIntent);
            }
        });

        btnAddBeerName = findViewById(R.id.adding_new_presets_btnIdAddName);
        btnAddBeerName.setOnClickListener(new View.OnClickListener() {
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
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy executed.");
        finish();
    }

    public void addName(){

        Beer beer;

        etBeerName = findViewById(R.id.adding_new_presets_etIdName);
        String beerName = etBeerName.getText().toString().trim();
        presetBeerName = beerName;

        if(beerName.isEmpty()){
            etIngredientName.setError("Beer name is required");
            etIngredientName.requestFocus();
            return;
        }

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

        Ingredient ingredient;

        etIngredientName = findViewById(R.id.adding_new_presets_etIdIngredients);
        etIngredientAmount = findViewById(R.id.adding_new_presets_etIdAmount);

        String IngredientName = etIngredientName.getText().toString().trim();
        amount = Integer.parseInt(etIngredientAmount.getText().toString());

        if(IngredientName.isEmpty()){
            etIngredientName.setError("Ingredient name is required");
            etIngredientName.requestFocus();
            return;
        }
        if(amount == 0){
            etIngredientAmount.setError("Ingredient amount is required");
            etIngredientAmount.requestFocus();
            return;
        }

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Preset ingredients").child(beerNameId);

        if(!TextUtils.isEmpty(IngredientName)){

            String id = databaseReference.push().getKey();
            ingredientId = id;

            ingredient = new Ingredient(id, IngredientName, amount);
            databaseReference.child(id).setValue(ingredient);
            Log.i(TAG, "Ingredient inserted into database");
            etIngredientName.getText().clear();
            etIngredientAmount.getText().clear();
        } else {
            Log.i(TAG, "Ingredient is not inserted into database.");
        }
    }

    private void displayIngredients(){

        listViewIngredients = findViewById(R.id.adding_new_presets_listIdIngredients);

        ingredientList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
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
                listViewIngredients.setAdapter(adapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                displayToast("Error: database could't load.");
            }
        });
    }

    private void addingPreset(){

    }

    private void displayToast(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
