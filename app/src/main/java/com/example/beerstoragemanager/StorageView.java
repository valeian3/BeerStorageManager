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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beerstoragemanager.Controller.IngredientListController;
import com.example.beerstoragemanager.Model.Ingredient;
import com.example.beerstoragemanager.databinding.ActivityStorageBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StorageView extends AppCompatActivity {

    ActivityStorageBinding binding;

    private static final String TAG = "StorageView";

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    Ingredient ingredient;
    List<Ingredient> ingredientList;
    boolean checkIfAppInBackground = false;
    int maxValueOfStorage = 20000;
    int maxAmountOfLeftInStorage;
    int amountOfIngredientsInStorage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_storage);

        binding = ActivityStorageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        ingredientList = new ArrayList<>();

        binding.bottomNavigationMenu.setSelectedItemId(R.id.storageView);
        binding.bottomNavigationMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent explicitIntent = new Intent();
                switch (item.getItemId()){
                    case R.id.storageView:
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
        binding.bottomNavigationMenu.setSelectedItemId(R.id.storageView);
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
        binding.bottomNavigationMenu.setSelectedItemId(R.id.storageView);

        newIngredient();
        listingIngredients();
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
        checkIfAppInBackground = true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy executed.");
        finishAndRemoveTask();
        overridePendingTransition(0, 0);
    }

    private void newIngredient(){
        binding.storageFabIdAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent explicitIntent = new Intent();
                explicitIntent.setClass(getApplicationContext(), AddingToStorageView.class);
                startActivity(explicitIntent);
                checkIfAppInBackground = false;
            }
        });
    }

    private void listingIngredients(){

        databaseReference = database.getReference("Ingredients");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ingredientList.clear();
                for(DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()){
                    Ingredient ingredient = ingredientSnapshot.getValue(Ingredient.class);
                    ingredientList.add(ingredient);

                    int amount = Integer.parseInt(ingredient.getAmount());
                    amountOfIngredientsInStorage += amount;
                    if (!checkIfAppInBackground) {
                        int percentage;
                        percentage = amountOfIngredientsInStorage * 100 / maxValueOfStorage;
                        binding.storageTvIdPercentage.setText(percentage + "%");
                        maxAmountOfLeftInStorage = maxValueOfStorage - amountOfIngredientsInStorage;
                        binding.storageProgressBarIdProgressBar.setMax(maxValueOfStorage);
                        binding.storageProgressBarIdProgressBar.setProgress(amountOfIngredientsInStorage);
                    }
                }
                IngredientListController adapter = new IngredientListController(StorageView.this, ingredientList, maxValueOfStorage);
                binding.storageListIdBeers.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    displayToast("Error: database could't load.");
            }
        });

        binding.storageListIdBeers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ingredient = ingredientList.get(position);

                updateAndDeleteDialog(ingredient.getIngredientId(), ingredient.getName(), ingredient.getAmount());

                checkIfAppInBackground = false;
            }
        });
    }

    private void updateAndDeleteDialog(final String id, final String ingredientName, final String amount){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(StorageView.this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.storage_dialog,null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView tvIngredientName, tvIngredientAmount;
        final Button btnUpdate, btnDelete, btnExit;
        final SeekBar seekBar;

        tvIngredientName = alertDialog.findViewById(R.id.storage_dialog_tvIdIngredientName);
        tvIngredientAmount = alertDialog.findViewById(R.id.storage_dialog_tvIdIngredientAmount);
        btnUpdate = alertDialog.findViewById(R.id.storage_dialog_btnIdUpdate);
        btnDelete = alertDialog.findViewById(R.id.storage_dialog_btnIdDelete);
        btnExit = alertDialog.findViewById(R.id.storage_dialog_btnIdExit);
        seekBar = alertDialog.findViewById(R.id.storage_dialog_seekBarId);

        tvIngredientName.setText(ingredientName);
        tvIngredientAmount.setText(amount);

        seekBar.setProgress(Integer.parseInt(amount));
        seekBar.setMax(maxValueOfStorage);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvIngredientAmount.setText("Amount "+ progress + "/"+ seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Ingredients").child(ingredient.getIngredientId());

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int newAmount = seekBar.getProgress();

                if(newAmount <= maxAmountOfLeftInStorage) {
                    if (!TextUtils.isEmpty(String.valueOf(newAmount))) {

                        ingredient = new Ingredient(ingredient.getIngredientId(), ingredientName, String.valueOf(newAmount));
                        databaseReference.setValue(ingredient);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                        alertDialog.dismiss();
                    }
                }else {
                    displayToast("Not enough space in storage");
                    displayToast("Available space in storage " + maxAmountOfLeftInStorage);
                }
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                alertDialog.dismiss();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference = FirebaseDatabase.getInstance().getReference("Ingredients").child(id);
                databaseReference.removeValue();

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


    //Idea was to change layout and list height when new ingredient is added
    /*private void layoutSize(){

        ViewGroup.LayoutParams layoutParams = binding.storageRlIdLayoutWithList.getLayoutParams();

        int newHeight = 40;

        int layoutHeight = binding.storageRlIdLayoutWithList.getHeight();

        layoutHeight += newHeight;

        layoutParams.height = layoutHeight;

        binding.storageRlIdLayoutWithList.setLayoutParams(layoutParams);


    }*/

    private void displayToast(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
