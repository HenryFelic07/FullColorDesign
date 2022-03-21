package com.example.fullcolordesign;

import android.widget.Filter;

import com.example.fullcolordesign.adapters.AdapterProductVendedor;
import com.example.fullcolordesign.models.ModelProduct;

import java.util.ArrayList;

public class FilterProducts extends Filter {

    private AdapterProductVendedor adapter;
    private ArrayList<ModelProduct> filterList;

    public FilterProducts(AdapterProductVendedor adapter, ArrayList<ModelProduct> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        if (constraint != null && constraint.length() > 0){

            constraint = constraint.toString().toUpperCase();

            ArrayList<ModelProduct> filteredModels = new ArrayList<>();
            for (int i=0; i<filterList.size(); i++){
                if(filterList.get(i).getProductTitulo().toUpperCase().contains(constraint) ||
                        filterList.get(i).getProductCategoria().toUpperCase().contains(constraint)){
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.productList = (ArrayList<ModelProduct>) results.values;
        adapter.notifyDataSetChanged();
    }
}
