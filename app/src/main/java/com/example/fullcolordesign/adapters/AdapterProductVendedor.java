package com.example.fullcolordesign.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fullcolordesign.FilterProducts;
import com.example.fullcolordesign.models.ModelProduct;
import com.example.fullcolordesign.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductVendedor extends RecyclerView.Adapter<AdapterProductVendedor.HolderProductVendedor> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList, filterList;
    private FilterProducts filter;

    public AdapterProductVendedor(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderProductVendedor onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_vendedor, parent, false);
        return new HolderProductVendedor(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductVendedor holder, int position) {
        //obtener data
        ModelProduct modelProduct = productList.get(position);
        String id= modelProduct.getProductId();
        String uid = modelProduct.getUid();
        String productCategory = modelProduct.getProductCategoria();
        String productDescription = modelProduct.getProductDescripcion();
        String icon = modelProduct.getProductIcon();
        String quantity = modelProduct.getProductCantidad();
        String title = modelProduct.getProductTitulo();
        String timestamp = modelProduct.getTimestamp();
        String originalPrice= modelProduct.getOriginalPrecio();

        //set data
        holder.titleTv.setText(title);
        holder.quantityTv.setText(quantity);
        holder.originalPriceTv.setText("$"+originalPrice);

        //aquí puede estar la falla
        try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_add_shopping_primary).into(holder.productIconIv);
        }
        catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_add_shopping_primary);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter = new FilterProducts(this, filterList);
        }
        return filter;
    }

    class HolderProductVendedor extends RecyclerView.ViewHolder{

        private ImageView productIconIv;
        private TextView titleTv, quantityTv, originalPriceTv;

        public HolderProductVendedor(@NonNull View itemView) {
            super(itemView);

            productIconIv = itemView.findViewById(R.id.productIconIv);
            titleTv = itemView.findViewById(R.id.titleTv);
            quantityTv = itemView.findViewById(R.id.quantityTv);
            originalPriceTv = itemView.findViewById(R.id.originalPriceTv);
        }
    }
}
