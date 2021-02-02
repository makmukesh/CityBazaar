package com.vpipl.citybazaar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.citybazaar.MyOrdersDetails_Activity;
import com.vpipl.citybazaar.R;
import com.vpipl.citybazaar.Sponsor_genealogy_Activity;
import com.vpipl.citybazaar.Utils.AppUtils;

import org.apache.commons.lang3.text.WordUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class MyOrdersList_Adapter extends RecyclerView.Adapter<MyOrdersList_Adapter.MyViewHolder> {
    public static ArrayList<HashMap<String, String>> ordersList;
    private LayoutInflater inflater = null;
    private Context context;
    private String TAG = "MyOrdersList_Adapter";

    public MyOrdersList_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        ordersList = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.myorders_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            holder.txt_OrderNo.setText(ordersList.get(position).get("OrderNo"));
            holder.txt_OrderAmt.setText("₹ " + ordersList.get(position).get("OrderAmt"));
            holder.txt_OrderDate.setText(AppUtils.getDateFromAPIDate(ordersList.get(position).get("ODate")));
            holder.txt_OrderStatus.setText(WordUtils.capitalizeFully(ordersList.get(position).get("OrderStatus")));

            holder.txt_Orderqty.setText(ordersList.get(position).get("OrderQty"));
            holder.txt_ttl_sbv.setText(ordersList.get(position).get("OrderCvp"));
            holder.txt_shipping_charges.setText(WordUtils.capitalizeFully(ordersList.get(position).get("Shipping")));
            holder.txt_ttl_amount.setText("₹ " +ordersList.get(position).get("TotalAmount"));
            holder.txt_Order_Throught.setText(ordersList.get(position).get("OrderVia"));

//			String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));

//			if (Usertype.equalsIgnoreCase("DISTRIBUTOR")){
//				holder.lay_bv.setVisibility(View.VISIBLE);
//				holder.txt_OrdeBV.setText((ordersList.get(position).get("OrderQvp")));
//			}

            Drawable upArrow = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_arrow, null);
            upArrow.setColorFilter(ResourcesCompat.getColor(context.getResources(), R.color.colorAccent, null), PorterDuff.Mode.SRC_ATOP);
            holder.icon.setBackgroundDrawable(upArrow);

            holder.txt_PrintOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //http://marketnetworkindia.com/Dashboard/PrintOrder.aspx?Req1=NDQ1MTUxMjM=
                    byte[] data1 = new byte[0];
                    try {
                        data1 = ordersList.get(position).get("OrderNo").getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String str = Base64.encodeToString(data1, Base64.DEFAULT);

                    Intent intent = new Intent(context, Sponsor_genealogy_Activity.class);
                    intent.putExtra("URL", AppUtils.PintInvoiceURL() + str);
                    context.startActivity(intent);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_OrderNo, txt_OrderAmt, txt_OrderDate, txt_OrderStatus, txt_OrdeBV , txt_PrintOrder;
        TextView txt_Order_Throught ,txt_ttl_amount , txt_shipping_charges , txt_ttl_sbv ,txt_Orderqty;
        LinearLayout lay_bv;
        ImageView icon;

        public MyViewHolder(View view) {
            super(view);
            txt_OrderNo = view.findViewById(R.id.txt_OrderNo);
            txt_OrderAmt = view.findViewById(R.id.txt_OrderAmt);
            txt_OrderDate = view.findViewById(R.id.txt_OrderDate);
            txt_OrderStatus = view.findViewById(R.id.txt_OrderStatus);
            txt_OrdeBV = view.findViewById(R.id.txt_OrdeBV);
            txt_PrintOrder = view.findViewById(R.id.txt_PrintOrder);

            txt_Order_Throught = view.findViewById(R.id.txt_Order_Throught);
            txt_ttl_amount = view.findViewById(R.id.txt_ttl_amount);
            txt_shipping_charges = view.findViewById(R.id.txt_shipping_charges);
            txt_ttl_sbv = view.findViewById(R.id.txt_ttl_sbv);
            txt_Orderqty = view.findViewById(R.id.txt_Orderqty);

            lay_bv = view.findViewById(R.id.lay_bv);

            icon = view.findViewById(R.id.icon);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppUtils.showLogs) Log.e(TAG, "setOnClickListener.." + getPosition());

                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(context, MyOrdersDetails_Activity.class);
                            intent.putExtra("position", getPosition());
                            context.startActivity(intent);
                        }
                    };
                    handler.postDelayed(runnable, 10);

                }
            });
        }
    }
}