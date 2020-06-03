package com.example.beerstoragemanager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import com.example.beerstoragemanager.Controller.CustomerListController;
import com.example.beerstoragemanager.Model.Customer;
import com.example.beerstoragemanager.databinding.ActivityOrderHistoryBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrdersHistoryView extends AppCompatActivity {

    ActivityOrderHistoryBinding binding;

    private static final String TAG = "OrdersHistoryView";

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    Customer customer;
    List<Customer> customersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_order_history);

        binding = ActivityOrderHistoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database = FirebaseDatabase.getInstance();

        customersList = new ArrayList<>();

        binding.bottomNavigationMenu.setSelectedItemId(R.id.ordersHistory);
        binding.bottomNavigationMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.storageView:
                        startActivity(new Intent(getApplicationContext(), StorageView.class));
                        overridePendingTransition(0, 0);
                        return  true;
                    case R.id.presetsView:
                        startActivity(new Intent(getApplicationContext(), PresetsView.class));
                        overridePendingTransition(0, 0);
                        return  true;
                    case R.id.ordersView:
                        startActivity(new Intent(getApplicationContext(), OrdersView.class));
                        overridePendingTransition(0, 0);
                        return  true;
                    case R.id.ordersHistory:
                        return  true;
                    case R.id.presetsHistory:
                        startActivity(new Intent(getApplicationContext(), PresetHistoryView.class));
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

        showOrderList();
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

    private void showOrderList(){
        databaseReference = database.getReference("Customers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customersList.clear();
                for(DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()){
                    Customer customer = ingredientSnapshot.getValue(Customer.class);
                    customersList.add(customer);
                }
                CustomerListController adapter = new CustomerListController(OrdersHistoryView.this, customersList);
                binding.ordersHistoryListIdCustomers.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                displayToast("Error: database could't load.");
            }
        });

        binding.ordersHistoryListIdCustomers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                customer = customersList.get(position);
                deleteDialog(customer.getCustomerId());
            }
        });
    }

    private void deleteDialog(final String id){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(OrdersHistoryView.this);
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
                databaseReference = FirebaseDatabase.getInstance().getReference("Customers").child(id);
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
