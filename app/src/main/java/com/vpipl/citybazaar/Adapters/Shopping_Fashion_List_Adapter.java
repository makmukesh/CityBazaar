package com.vpipl.citybazaar.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vpipl.citybazaar.R;
import com.vpipl.citybazaar.Utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class Shopping_Fashion_List_Adapter extends RecyclerView.Adapter<Shopping_Fashion_List_Adapter.MyViewHolder> {
    public static ArrayList<HashMap<String, String>> achiverList;
    private LayoutInflater inflater = null;
    private Context context;
    private String TAG = "Home_Shopping_List_Adapter";

    public Shopping_Fashion_List_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        achiverList = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.adapter_shopping, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            holder.txt_product_name.setText(achiverList.get(position).get("product_name"));

            AppUtils.loadProductImage(context, achiverList.get(position).get("product_img"), holder.iv_image);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return achiverList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_product_name;
        ImageView iv_image;

        public MyViewHolder(View view) {
            super(view);
            txt_product_name = view.findViewById(R.id.txt_product_name);
            iv_image = view.findViewById(R.id.iv_image);
        }
    }
}