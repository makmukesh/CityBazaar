package com.vpipl.citybazaar;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.citybazaar.Adapters.MyOrdersDetailList_Adapter;
import com.vpipl.citybazaar.Adapters.MyOrdersList_Adapter;
import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mukesh on 27/12/2019
 */
public class MyOrdersDetails_Activity extends AppCompatActivity {

    Activity act = MyOrdersDetails_Activity.this;
    private String TAG = "MyOrdersDetails_Activity";
    private RecyclerView listView;
    private LinearLayout layout_listView;
    private LinearLayout layout_nodata;

    private MyOrdersDetailList_Adapter adapter;
    private ArrayList<HashMap<String, String>> ordersDetailList = new ArrayList<>();

    private TextView txt_orderNo;
    private TextView txt_orderDate;
    private TextView txt_orderStatus;
    private TextView txt_orderAmount;
    private TextView txt_OrdeBV;
    private LinearLayout lay_bv;
    TextView txt_member_name, txt_member_address, txt_member_city_state_country, txt_member_pincode_mobileno;

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);

        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));

        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        img_login_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(act, Login_New_Activity.class));
                else
                    AppUtils.showDialogSignOut(act);
            }
        });

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout));
        else
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.ic_login_user));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myordersdetails_activity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        AppUtils.changeStatusBarColor(act);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();
        try {

            listView = findViewById(R.id.listView);
            layout_listView = findViewById(R.id.layout_listView);
            layout_nodata = findViewById(R.id.layout_nodata);

            txt_orderNo = findViewById(R.id.txt_orderNo);
            txt_orderDate = findViewById(R.id.txt_orderDate);
            txt_orderStatus = findViewById(R.id.txt_orderStatus);
            txt_orderAmount = findViewById(R.id.txt_orderAmount);
            txt_OrdeBV = findViewById(R.id.txt_OrdeBV);

            txt_member_name = findViewById(R.id.txt_member_name);
            txt_member_address = findViewById(R.id.txt_member_address);
            txt_member_city_state_country = findViewById(R.id.txt_member_city_state_country);
            txt_member_pincode_mobileno = findViewById(R.id.txt_member_pincode_mobileno);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            listView.setLayoutManager(mLayoutManager);
            listView.setItemAnimator(new DefaultItemAnimator());

            lay_bv = findViewById(R.id.lay_bv);


            if (AppUtils.isNetworkAvailable(act)) {
                executeGetMyOrdersDetailsRequest();
            } else {
                showNoData();
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }

            txt_orderNo.setText(MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("OrderNo"));
            txt_orderDate.setText(AppUtils.getDateFromAPIDate(MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("ODate")));
            txt_orderStatus.setText(WordUtils.capitalizeFully(MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("OrderStatus")));
            txt_orderAmount.setText("₹ " + MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("TotalAmount"));

            txt_member_name.setText("" + MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("Name"));
            txt_member_address.setText("" + MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("Address1"));
            txt_member_city_state_country.setText("" + MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("City")
                    + "  " + MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("CountryName"));
            txt_member_pincode_mobileno.setText("" + MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("PinCode")
                    + "  " + MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("Mobl"));

            txt_OrdeBV.setText("" + MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("OrderCvp"));

           // txt_OrdeBV.setText("");
           // lay_bv.setVisibility(View.GONE);

           /* String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));

            if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
                lay_bv.setVisibility(View.VISIBLE);
                txt_OrdeBV.setText(MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("OrderQvp"));
            }*/


        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void executeGetMyOrdersDetailsRequest() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(act);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("OrderNo", MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("OrderNo")));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToGetViewOrdersDetails, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                                if (jsonArrayData.length() > 0) {
                                    getOrdersDetailListResult(jsonArrayData);
                                } else {
                                    showNoData();
                                }
                            } else {
                                AppUtils.alertDialog(act, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(act);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getOrdersDetailListResult(JSONArray jsonArray) {
        try {
            ordersDetailList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("OrderNo", "" + jsonObject.getString("OrderNo"));
                map.put("ProductID", "" + jsonObject.getString("ProductID"));
                map.put("ProductName", "" + jsonObject.getString("ProductName"));
                map.put("Size", "" + jsonObject.getString("Size"));
                map.put("Color", "" + jsonObject.getString("Color"));
                map.put("Qty", "" + jsonObject.getString("Qty"));
                map.put("Netamount", "" + jsonObject.getString("Netamount"));
                map.put("ImgPath", "" + jsonObject.getString("ImgPath"));
                map.put("ProdStatus", "" + jsonObject.getString("ProdStatus"));

//                map.put("bv", ""+jsonObject.getString("bv"));
//                map.put("ProdType", ""+jsonObject.getString("ProdType"));
//                map.put("DP", ""+jsonObject.getString("DP"));
//                map.put("CVP", ""+jsonObject.getString("CVP"));
//                map.put("ShipCharges", ""+jsonObject.getString("ShipCharges"));
//                map.put("totalAmt", ""+jsonObject.getString("totalAmt"));

                ordersDetailList.add(map);
            }

            showListView();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void showListView() {
        try {
            if (ordersDetailList.size() > 0) {
                adapter = new MyOrdersDetailList_Adapter(act, ordersDetailList);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                layout_listView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                layout_nodata.setVisibility(View.GONE);
            } else {
                showNoData();
                AppUtils.showExceptionDialog(act);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNoData() {
        try {
            layout_listView.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            layout_nodata.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }
}
