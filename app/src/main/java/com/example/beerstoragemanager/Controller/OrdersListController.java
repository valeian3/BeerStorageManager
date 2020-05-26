package com.example.beerstoragemanager.Controller;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.beerstoragemanager.Model.Beer;
import com.example.beerstoragemanager.R;

import java.util.List;

public class OrdersListController extends ArrayAdapter<Beer> {

    private Activity context;
    private List<Beer> OrdersList;

    public OrdersListController(Activity context, List<Beer> ordersList){
        super(context, R.layout.order_list, ordersList);
        this.context = context;
        this.OrdersList = ordersList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View ingredientViewItem = inflater.inflate(R.layout.order_list, null, true);

        TextView tvName = (TextView) ingredientViewItem.findViewById(R.id.orders_tvIdBeerName);
        TextView tvAmount = (TextView) ingredientViewItem.findViewById(R.id.orders_tvIdBeerAmount);

        Beer beer = OrdersList.get(position);

        tvName.setText(beer.getName());
        tvAmount.setText(String.valueOf(beer.getAmountOfBags()));

        return ingredientViewItem;
    }
}
