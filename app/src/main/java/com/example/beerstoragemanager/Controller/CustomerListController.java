package com.example.beerstoragemanager.Controller;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.beerstoragemanager.Model.Customer;
import com.example.beerstoragemanager.R;

import java.util.List;

public class CustomerListController extends ArrayAdapter<Customer> {
    private Activity context;
    private List<Customer> ordersList;

    public CustomerListController(Activity context, List<Customer> ordersList){
        super(context, R.layout.history_list, ordersList);
        this.context = context;
        this.ordersList = ordersList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View ingredientViewItem = inflater.inflate(R.layout.history_list, null, true);

        TextView tvName = (TextView) ingredientViewItem.findViewById(R.id.history_tvIdBeerName);

        Customer customer = ordersList.get(position);

        tvName.setText(customer.getName());

        return ingredientViewItem;
    }
}
