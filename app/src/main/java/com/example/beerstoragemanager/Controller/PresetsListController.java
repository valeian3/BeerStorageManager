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

public class PresetsListController extends ArrayAdapter<Beer> {

    private Activity context;
    private List<Beer> PresetsList;

    public PresetsListController(Activity context, List<Beer> presetsList){
        super(context, R.layout.order_list, presetsList);
        this.context = context;
        this.PresetsList = presetsList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View ingredientViewItem = inflater.inflate(R.layout.order_list, null, true);

        TextView tvName = (TextView) ingredientViewItem.findViewById(R.id.orders_tvIdBeerName);

        Beer beer = PresetsList.get(position);

        tvName.setText(beer.getName());

        return ingredientViewItem;
    }
}
