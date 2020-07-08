package com.example.beerstoragemanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.beerstoragemanager.Controller.CustomerListController;
import com.example.beerstoragemanager.Controller.IngredientsListInPresetsController;
import com.example.beerstoragemanager.Controller.OrdersListController;
import com.example.beerstoragemanager.Controller.PresetsListController;
import com.example.beerstoragemanager.Model.Beer;
import com.example.beerstoragemanager.Model.Customer;
import com.example.beerstoragemanager.Model.Ingredient;
import com.example.beerstoragemanager.databinding.ActivityOrderHistoryBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrdersHistoryView extends AppCompatActivity {

    ActivityOrderHistoryBinding binding;

    private static final String TAG = "OrdersHistoryView";

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    Customer customer;
    List<Customer> customersList;
    List<Beer> beerList;

    Bitmap bmp, scaledBmp;
    Date date;
    DateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_order_history);

        binding = ActivityOrderHistoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database = FirebaseDatabase.getInstance();

        customersList = new ArrayList<>();
        beerList = new ArrayList<>();

        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE},PackageManager.PERMISSION_GRANTED);

        bmp = BitmapFactory.decodeResource(getResources(),R.drawable.logo);
        scaledBmp = Bitmap.createScaledBitmap(bmp, 40, 40, false);
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
        binding.ordersHistoryListIdCustomers.setLongClickable(true);
        binding.ordersHistoryListIdCustomers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                customer = customersList.get(position);
                aboutDialog(customer.getOrderId(), customer.getName());
            }
        });

        binding.ordersHistoryListIdCustomers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                customer = customersList.get(position);
                deleteDialog(customer.getCustomerId());
                return false;
            }
        });
    }

    private void aboutDialog(final String orderId, final String customerName){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(OrdersHistoryView.this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.about_dialog,null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final ListView itemList;

        itemList = alertDialog.findViewById(R.id.about_dialog_listIdItems);

        databaseReference = database.getReference("Orders").child(orderId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                beerList.clear();
                for(DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()){
                    Beer beer = ingredientSnapshot.getValue(Beer.class);
                    beerList.add(beer);
                }
                OrdersListController adapter = new OrdersListController(OrdersHistoryView.this, beerList);
                itemList.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                displayToast("Error: database could't load.");
            }
        });

        final Button btnExit, btnCreatePdf;

        btnExit = alertDialog.findViewById(R.id.about_dialog_btnIdExit);
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
        btnCreatePdf = alertDialog.findViewById(R.id.about_dialog_btnIdPdf);
        btnCreatePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                date = new Date();

                PdfDocument pdfDocument = new PdfDocument();
                Paint paint = new Paint();

                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(250, 400, 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                canvas.drawBitmap(scaledBmp, 10, 10, paint);

                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(14f);
                canvas.drawText("Pivovara Daruvar", pageInfo.getPageWidth()/2, 40, paint);

                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(12f);
                paint.setColor(Color.rgb(122,119,119));
                canvas.drawText("Order details", pageInfo.getPageWidth()/2, 90, paint);

                paint.setTextSize(10f);
                canvas.drawText("Customer: " + customerName, 52, 120, paint);

                dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                paint.setTextSize(10f);
                canvas.drawText("Date: " + dateFormat.format(date), 48, 140, paint);

                paint.setTextSize(12f);
                canvas.drawText("Beer name", 55, 170, paint);

                canvas.drawLine(25, 174, 210, 174, paint);

                paint.setTextSize(12f);
                canvas.drawText("Quantity", 180, 170, paint);

                int nameStartXPosition = 65;
                int amountStartXPosition = 180;
                int startYPosition = 195;

                for(int i = 0; i < beerList.size(); i++){
                    Beer beer = beerList.get(i);
                    canvas.drawText(beer.getName(), nameStartXPosition, startYPosition, paint);
                    canvas.drawText(beer.getAmountOfBags(), amountStartXPosition, startYPosition, paint);
                    startYPosition += 20;
                }


                pdfDocument.finishPage(page);

                File file = new File(Environment.getExternalStorageDirectory(), "/"+customerName + " Order Details.pdf");

                try {
                    pdfDocument.writeTo(new FileOutputStream(file));
                    displayToast("PDF created");
                } catch (IOException e) {
                    e.printStackTrace();
                    displayToast("Error: PDF not created");
                }
                pdfDocument.close();

            }
        });
    }

    private void deleteDialog(final String customerId){
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
                databaseReference = FirebaseDatabase.getInstance().getReference("Customers").child(customerId);
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
