package com.example.beerstoragemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beerstoragemanager.Model.Ingredient;
import com.example.beerstoragemanager.databinding.ActivityAddingToStorageBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddingToStorageView extends AppCompatActivity {

    ActivityAddingToStorageBinding binding;

    private static final String TAG = "AddingToStorageView";

    boolean checkForIngredientInput = false;

    Ingredient ingredient;

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

        binding.addingToStorageBtnIdAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent explicitIntent = new Intent();
                    explicitIntent.setClass(getApplicationContext(), StorageView.class);
                    startActivity(explicitIntent);
                    addingNewIngredient();
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

    private void addingNewIngredient(){
        String name = binding.addingToStorageEtIdName.getText().toString().trim();
        String amount = binding.addingToStorageEtIdAmount.getText().toString().trim();

        if(name.isEmpty() && amount.isEmpty()){
            binding.addingToStorageEtIdName.setError("Ingredient name is required");
            binding.addingToStorageEtIdName.requestFocus();
            binding.addingToStorageEtIdAmount.setError("Ingredient amount is required");
            binding.addingToStorageEtIdAmount.requestFocus();
            return;
        }else{
            checkForIngredientInput = true;
        }

        databaseReference = database.getReference().child("Ingredients");
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(amount)){

            String id = databaseReference.push().getKey();

            ingredient = new Ingredient(id, name, amount);
            databaseReference.child(id).setValue(ingredient);
            Log.i(TAG, "Ingredient inserted into database");
        } else {
            Log.i(TAG, "Ingredient is not inserted into database.");
        }
    }

    private void displayToast(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
