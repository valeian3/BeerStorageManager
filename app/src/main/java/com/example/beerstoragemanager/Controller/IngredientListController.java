package com.example.beerstoragemanager.Controller;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.beerstoragemanager.Model.Ingredient;
import com.example.beerstoragemanager.R;

import java.util.List;

public class IngredientListController extends ArrayAdapter<Ingredient> {

    private Activity context;
    private List<Ingredient> IngredientList;

    int max = 10;

    public IngredientListController(Activity context, List<Ingredient> ingredientList){
        super(context, R.layout.storage_list, ingredientList);
        this.context = context;
        this.IngredientList = ingredientList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View ingredientViewItem = inflater.inflate(R.layout.storage_list, null, true);

        TextView tvName = (TextView) ingredientViewItem.findViewById(R.id.storage_list_tvIdBeerName);
        TextView tvAmount = (TextView) ingredientViewItem.findViewById(R.id.storage_list_tvIdBeerAmount);
        ProgressBar progressBar = (ProgressBar) ingredientViewItem.findViewById(R.id.storage_list_pbId);

        Ingredient ingredient = IngredientList.get(position);



        /*for(int i = 0; i <= IngredientList.size(); i++){
            max += Integer.valueOf(ingredient.getAmount();
        }*/

        tvName.setText(ingredient.getName());
        tvAmount.setText(String.valueOf(ingredient.getAmount()));

        progressBar.setMax(max);
        progressBar.setProgress(Integer.valueOf(ingredient.getAmount()));

        return ingredientViewItem;
    }
}
