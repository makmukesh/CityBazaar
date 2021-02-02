package com.vpipl.citybazaar.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vpipl.citybazaar.PlaceOrderOnlineActivity;
import com.vpipl.citybazaar.R;
import com.vpipl.citybazaar.model.ProductList_Data;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {
    private List<ProductList_Data> productlist_data;
    private Context context;
    Double rate, bv, d, to = 0.0;
    int counter = 0;
    boolean isnumbervalid;

    public ProductListAdapter(List<ProductList_Data> productlist_data, Context context) {
        this.productlist_data = productlist_data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_line, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ProductList_Data listData = productlist_data.get(position);

        int a = position + 1;
        holder.snno.setText("" + a);
        holder.product_name.setText(listData.getProductName());
        holder.dp.setText(listData.getDP());
        holder.rate.setText(listData.getRate());
        holder.batch_no.setText(listData.getBatchNo());
        holder.discount_per.setText(listData.getDiscPer());
        holder.BV.setText(listData.getBV());
        holder.CGST_Tax_Per.setText(listData.getCGSTPer());
        holder.IGST_Tax_Per.setText(listData.getIGSTPer());
        holder.SGST_Tax_Per.setText(listData.getSGST_TaxPer());

        rate = Double.valueOf(listData.getRate());
        bv = Double.valueOf(listData.getBV());

        holder.quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Double d_cgst = 0.0, d_igst = 0.0, d_sgst = 0.0, d_disc_amt = 0.0;
                d_cgst = Double.valueOf(listData.getCGSTAmt());
                d_igst = Double.valueOf(listData.getIGSTAmt());
                d_sgst = Double.valueOf(listData.getSGST_TaxAmt());
                d_disc_amt = Double.valueOf(listData.getDiscAmt());
                rate = Double.valueOf(listData.getRate());
                bv = Double.valueOf(listData.getBV());

                if (holder.quantity.getText().toString().matches("")) {
                    d = Double.valueOf("0");
                } else {
                    d = Double.valueOf(holder.quantity.getText().toString());
                }
                holder.CGST_Tax_Amt.setText("" + String.format("%.2f", (d * d_cgst)));
                holder.IGST_Tax_Amt.setText("" + String.format("%.2f", (d * d_igst)));
                holder.SGST_Tax_Amt.setText("" + String.format("%.2f", (d * d_sgst)));
                holder.ttl_amt_withouttax.setText("" + String.format("%.2f", (d * rate)));
                holder.discount_amt.setText("" + String.format("%.2f", (d * d_disc_amt)));

                Double ttl_with_tax = 0.0;
                ttl_with_tax = (d * d_cgst) + (d * d_igst) + (d * d_sgst) + (d * rate);
                holder.totalamt.setText("" + String.format("%.2f", (ttl_with_tax)));
                holder.total_sbv.setText("" + String.format("%.2f", (d * bv)));

                listData.setQty("" + d);
               /* if (d > 0) {
                    listData.setQty("" + d);
                } else {
                    listData.setQty("");
                }*/

                //  String ProId = listData.getProdId();
                Log.e("ProIdPlusQuantity", d + "   ");
                //    notifyDataSetChanged();

                PlaceOrderOnlineActivity.setNetPaybleValue();
                // subtotal=subtotal+Double.valueOf(Price)*new_quantity;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return productlist_data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView snno;
        private TextView product_name;
        private TextView dp;
        private TextView rate;
        private TextView BV;
        private EditText quantity;
        private TextView CGST_Tax_Per, CGST_Tax_Amt, IGST_Tax_Per, IGST_Tax_Amt, SGST_Tax_Per, SGST_Tax_Amt;
        private TextView totalamt;
        private TextView total_sbv;
        private TextView batch_no, discount_per, ttl_amt_withouttax, discount_amt;

        public ViewHolder(View itemView) {
            super(itemView);
            //img=(ImageView)itemView.findViewById(R.id.image_view);
            snno = (TextView) itemView.findViewById(R.id.snno);
            product_name = (TextView) itemView.findViewById(R.id.product_name);
            dp = (TextView) itemView.findViewById(R.id.dp);
            rate = (TextView) itemView.findViewById(R.id.rate);
            BV = (TextView) itemView.findViewById(R.id.BV);
            quantity = (EditText) itemView.findViewById(R.id.quantity);
            CGST_Tax_Per = (TextView) itemView.findViewById(R.id.CGST_Tax_Per);
            CGST_Tax_Amt = (TextView) itemView.findViewById(R.id.CGST_Tax_Amt);
            IGST_Tax_Per = (TextView) itemView.findViewById(R.id.IGST_Tax_Per);
            IGST_Tax_Amt = (TextView) itemView.findViewById(R.id.IGST_Tax_Amt);
            SGST_Tax_Per = (TextView) itemView.findViewById(R.id.SGST_Tax_Per);
            SGST_Tax_Amt = (TextView) itemView.findViewById(R.id.SGST_Tax_Amt);
            totalamt = (TextView) itemView.findViewById(R.id.totalamt);
            total_sbv = (TextView) itemView.findViewById(R.id.total_sbv);
            ttl_amt_withouttax = (TextView) itemView.findViewById(R.id.ttl_amt_withouttax);
            discount_amt = (TextView) itemView.findViewById(R.id.discount_amt);
            batch_no = (TextView) itemView.findViewById(R.id.batch_no);
            discount_per = (TextView) itemView.findViewById(R.id.discount_per);

        }
    }

    public static boolean isValid(String s) {
        Pattern p = Pattern.compile("(0/91)?[0-9][0-9]{4}");
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }
}