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

import com.example.beerstoragemanager.Controller.OrdersListController;
import com.example.beerstoragemanager.Controller.PresetsListController;
import com.example.beerstoragemanager.Model.Beer;
import com.example.beerstoragemanager.Model.Customer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrdersView extends AppCompatActivity {

    private static final String TAG = "OrdersView";

    Button btnOrder, btnInput;
    EditText etBeerName, etBeerAmountOfBags, etCustomerName;

    ListView lvOrders;
    List<Beer> beerOrderList;

    Beer beer;
    Customer customer;
    int amount;
    String orderId;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        database = FirebaseDatabase.getInstance();
        orderId = database.getReference("Orders").push().getKey();

        lvOrders = findViewById(R.id.orders_listIdBeers);

        beerOrderList = new ArrayList<>();
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

        btnInput = findViewById(R.id.orders_btnIdInput);
        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingNewOrder();
                displayInputtedOrder();
            }
        });
        btnOrder = findViewById(R.id.orders_btnIdOrder);
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderForCustomer();
                Intent explicitIntent = new Intent();
                explicitIntent.setClass(getApplicationContext(), OrdersHistoryView.class);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy executed.");
    }

    private void addingNewOrder(){

        etBeerName = findViewById(R.id.orders_etIdBeerName);
        etBeerAmountOfBags = findViewById(R.id.orders_etIdAmountOfBags);

        String BeerName = etBeerName.getText().toString().trim();
        amount = Integer.parseInt(etBeerAmountOfBags.getText().toString());

        if(BeerName.isEmpty()){
            etBeerName.setError("Beer name is required");
            etBeerName.requestFocus();
            return;
        }
        if(amount == 0){
            etBeerAmountOfBags.setError("Beer amount is required");
            etBeerAmountOfBags.requestFocus();
            return;
        }

        databaseReference = database.getReference().child("Orders");
        if(!TextUtils.isEmpty(BeerName)){

            String id = databaseReference.push().getKey();

            beer = new Beer(id, BeerName, amount);
            databaseReference.child(orderId).child(id).setValue(beer);
            Log.i(TAG, "Beer inserted into database");
            etBeerName.getText().clear();
            etBeerAmountOfBags.getText().clear();
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
                lvOrders.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                displayToast("Error: database could't load.");
            }
        });
    }

    private void orderForCustomer(){

        etCustomerName = findViewById(R.id.orders_etIdOrderName);

        String CustomerName = etCustomerName.getText().toString().trim();

        if(CustomerName.isEmpty()){
            etCustomerName.setError("Customer name is required");
            etCustomerName.requestFocus();
            return;
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
    }

    private void displayToast(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
