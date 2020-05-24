package com.example.beerstoragemanager.Controller;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.beerstoragemanager.Model.Ingredient;
import com.example.beerstoragemanager.R;

import java.util.List;

public class IngredientList extends ArrayAdapter<Ingredient> {

    private Activity context;
    private List<Ingredient> IngredientList;

    public IngredientList(Activity context, List<Ingredient> ingredientList){
        super(context, R.layout.order_list, ingredientList);
        this.context = context;
        this.IngredientList = ingredientList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View ingredientViewItem = inflater.inflate(R.layout.order_list, null, true);

        TextView tvName = (TextView) ingredientViewItem.findViewById(R.id.orders_tvIdBeerName);
        TextView tvAmount = (TextView) ingredientViewItem.findViewById(R.id.orders_tvIdBeerAmount);

        Ingredient ingredient = IngredientList.get(position);

        tvName.setText(ingredient.getName());
        tvAmount.setText(String.valueOf(ingredient.getAmount()));

        return ingredientViewItem;
    }
}
