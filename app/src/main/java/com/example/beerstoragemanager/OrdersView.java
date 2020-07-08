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

import com.example.beerstoragemanager.Controller.OrdersListController;
import com.example.beerstoragemanager.Model.Beer;
import com.example.beerstoragemanager.Model.Customer;
import com.example.beerstoragemanager.databinding.ActivityOrdersBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrdersView extends AppCompatActivity {

    ActivityOrdersBinding binding;

    private static final String TAG = "OrdersView";

    List<Beer> beerOrderList;
    Beer beer;
    Customer customer;
    String orderId;
    boolean checkForCustomerName = false;
    boolean checkForOneOrder = false, checkIfOrderSelected = false;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_orders);

        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database = FirebaseDatabase.getInstance();
        orderId = database.getReference("Orders").push().getKey();

        beerOrderList = new ArrayList<>();

        binding.bottomNavigationMenu.setSelectedItemId(R.id.ordersView);
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
        binding.bottomNavigationMenu.setSelectedItemId(R.id.ordersView);
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
        binding.bottomNavigationMenu.setSelectedItemId(R.id.ordersView);

        binding.ordersBtnIdInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                addingItemInNewOrder();
                displayInputtedOrder();
            }
        });
        binding.ordersBtnIdOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(orderForCustomer() && checkForOneOrder){
                    Intent explicitIntent = new Intent();
                    explicitIntent.setClass(getApplicationContext(), OrdersHistoryView.class);
                    explicitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(explicitIntent);
                    overridePendingTransition(0, 0);
                    checkIfOrderSelected = true;
                }else{
                    displayToast("Order cannot be empty");
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
        if(checkIfOrderSelected){
            finishAndRemoveTask();
            overridePendingTransition(0, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy executed.");
        finishAndRemoveTask();
        overridePendingTransition(0, 0);
    }

    private boolean orderForCustomer(){
        String CustomerName = binding.ordersEtIdOrderName.getText().toString().trim();

        if(CustomerName.isEmpty()){
            binding.ordersEtIdOrderName.setError("Customer name is required");
            binding.ordersEtIdOrderName.requestFocus();
            checkForCustomerName = false;
        }else{
            checkForCustomerName = true;
        }

        databaseReference = database.getReference().child("Customers");
        if(!TextUtils.isEmpty(CustomerName)){

            String id = databaseReference.push().getKey();

            customer = new Customer(id, CustomerName, orderId);
            databaseReference.child(id).setValue(customer);
            Log.i(TAG, "Customer inserted into database");
        } else {
            Log.i(TAG, "Customer is not inserted into database.");
        }
        return checkForCustomerName;
    }

    private void addingItemInNewOrder(){

        String BeerName = binding.ordersEtIdBeerName.getText().toString().trim();
        String amount = binding.ordersEtIdAmountOfBags.getText().toString();

        if(BeerName.isEmpty() && amount.isEmpty()){
            binding.ordersEtIdBeerName.setError("Beer name is required");
            binding.ordersEtIdBeerName.requestFocus();
            binding.ordersEtIdAmountOfBags.setError("Amount of bags is required");
            binding.ordersEtIdAmountOfBags.requestFocus();
            checkForOneOrder = false;
            checkForCustomerName = false;
            return;
        }else{
            checkForOneOrder = true;
            checkForCustomerName = true;
        }

        databaseReference = database.getReference().child("Orders");
        if(!TextUtils.isEmpty(BeerName)){

            String id = databaseReference.push().getKey();

            beer = new Beer(id, BeerName, amount);
            databaseReference.child(orderId).child(id).setValue(beer);
            Log.i(TAG, "Beer inserted into database");
            binding.ordersEtIdBeerName.getText().clear();
            binding.ordersEtIdAmountOfBags.getText().clear();
        } else {
            Log.i(TAG, "Beer is not inserted into database.");
        }
    }

    private void displayInputtedOrder(){
        databaseReference = database.getReference("Orders").child(orderId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                beerOrderList.clear();
                for(DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()){
                    Beer beer = ingredientSnapshot.getValue(Beer.class);
                    beerOrderList.add(beer);
                }
                OrdersListController adapter = new OrdersListController(OrdersView.this, beerOrderList);
                binding.ordersListIdBeers.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                displayToast("Error: database could't load.");
            }
        });

        binding.ordersListIdBeers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                beer =  beerOrderList.get(position);
                deleteDialog(beer.getBeerId());
            }
        });

    }

    private void deleteDialog(final String id){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(OrdersView.this);
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
                databaseReference = FirebaseDatabase.getInstance().getReference("Orders").child(orderId).child(id);
                databaseReference.removeValue();
                overridePendingTransition(0, 0);
                alertDialog.dismiss();
            }
        });
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(0, 0);
                alertDialog.dismiss();
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(0, 0);
                alertDialog.dismiss();
            }
        });
    }

    private void displayToast(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
