package com.example.beerstoragemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beerstoragemanager.Model.Ingredient;
import com.example.beerstoragemanager.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddingToStorageView extends AppCompatActivity {

    private static final String TAG = "AddingToStorageView";
    EditText etIdName, etIdAmount;
    Button btnIdAdd;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    int amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_to_storage);
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

        btnIdAdd = findViewById(R.id.adding_to_storage_btnIdAdd);
        btnIdAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingNewIngredient();
                Intent explicitIntent = new Intent();
                explicitIntent.setClass(getApplicationContext(), StorageView.class);
                startActivity(explicitIntent);
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

    private void addingNewIngredient(){

        Ingredient ingredient;

        etIdName = findViewById(R.id.adding_to_storage_etIdName);
        etIdAmount = findViewById(R.id.adding_to_storage_etIdAmount);

        String name = etIdName.getText().toString().trim();
        amount = Integer.parseInt(etIdAmount.getText().toString());

        if(name.isEmpty()){
            etIdName.setError("Ingredient name is required");
            etIdName.requestFocus();
            return;
        }
        if(amount == 0){
            etIdAmount.setError("Ingredient amount is required");
            etIdAmount.requestFocus();
            return;
        }

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Ingredients");
        if(!TextUtils.isEmpty(name)){

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
