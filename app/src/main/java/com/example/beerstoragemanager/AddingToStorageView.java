package com.example.beerstoragemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beerstoragemanager.Model.Ingredient;
import com.example.beerstoragemanager.databinding.ActivityAddingToStorageBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class AddingToStorageView extends AppCompatActivity {

    ActivityAddingToStorageBinding binding;

    private static final String TAG = "AddingToStorageView";

    boolean checkForIngredientInput = false, checkIfIngredientExists;

    Ingredient ingredient;

    ArrayList<Ingredient> ingredientList;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_adding_to_storage);

        binding = ActivityAddingToStorageBinding.inflate(getLayoutInflater());
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

        loadStorageIngredients();

        binding.addingToStorageBtnIdAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingNewIngredient();
                Intent explicitIntent = new Intent();
                explicitIntent.setClass(getApplicationContext(), StorageView.class);
                explicitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(explicitIntent);
                overridePendingTransition(0, 0);
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
        overridePendingTransition(0, 0);
    }

    private void loadStorageIngredients(){
        databaseReference = database.getReference("Ingredients");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ingredientList.clear();
                for(DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()){
                    Ingredient ingredient = ingredientSnapshot.getValue(Ingredient.class);
                    ingredientList.add(ingredient);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                displayToast("Error: database could't load.");
            }
        });
    }

    private void addingNewIngredient(){
        String name = binding.addingToStorageEtIdName.getText().toString().trim();
        String amount = binding.addingToStorageEtIdAmount.getText().toString().trim();

        if(name.isEmpty() && amount.isEmpty()){
            binding.addingToStorageEtIdName.setError("Ingredient name is required");
            binding.addingToStorageEtIdName.requestFocus();
            binding.addingToStorageEtIdAmount.setError("Ingredient amount is required");
            binding.addingToStorageEtIdAmount.requestFocus();
            checkForIngredientInput = false;
            return;
        }else{
            checkForIngredientInput = true;
        }

        for (int i = 0; i < ingredientList.size(); i++) {
            Ingredient ingredientFromDatabase = ingredientList.get(i);
            if (name.equals(ingredientFromDatabase.getName())) {
                int oldStorageAmount = parseInt(ingredientFromDatabase.getAmount());
                int newStorageAmount = parseInt(amount);
                newStorageAmount += oldStorageAmount;

                databaseReference = database.getReference().child("Ingredients");
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(amount)) {
                    ingredient = new Ingredient(ingredientFromDatabase.getIngredientId(), name, String.valueOf(newStorageAmount));
                    databaseReference.child(ingredientFromDatabase.getIngredientId()).setValue(ingredient);
                    checkIfIngredientExists = true;
                    Log.i(TAG, "Ingredient amount value updated");
                } else {
                    displayToast("Error: ingredient amount is not updated");
                }
            }
         }

        if(!checkIfIngredientExists) {
            databaseReference = database.getReference().child("Ingredients");
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(amount)) {

                String id = databaseReference.push().getKey();

                ingredient = new Ingredient(id, name, amount);
                databaseReference.child(id).setValue(ingredient);
                Log.i(TAG, "Ingredient inserted into database");
            } else {
                displayToast("Ingredient is not inserted into database.");
            }
        }
    }


    private void displayToast(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
