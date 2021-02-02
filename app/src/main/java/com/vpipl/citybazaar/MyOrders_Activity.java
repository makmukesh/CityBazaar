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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vpipl.citybazaar.Adapters.MyOrdersList_Adapter;
import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 Created by Mukesh on 27/12/2019.
 */
public class MyOrders_Activity extends AppCompatActivity {

    Activity act = MyOrders_Activity.this;
    private static MyOrdersList_Adapter adapter;
    private String TAG = "MyOrders_Activity";
    private RecyclerView listView;
    private LinearLayout layout_listView;
    private LinearLayout layout_nodata;
    private ArrayList<HashMap<String, String>> ordersList = new ArrayList<>();

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
        setContentView(R.layout.myorders_activity);

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

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            listView.setLayoutManager(mLayoutManager);
            listView.setItemAnimator(new DefaultItemAnimator());

            if (AppUtils.isNetworkAvailable(act)) {
                executeGetMyOrdersRequest();
            } else {
                showNoData();
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void executeGetMyOrdersRequest() {
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
                            postParameters.add(new BasicNameValuePair("OrderByFormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToGetViewOrdersList, TAG);
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
                                JSONArray jsonArrayData = jsonObject.getJSONArray("FillNewOrdersDetail");

                                if (jsonArrayData.length() > 0) {
                                    getOrdersListResult(jsonArrayData);
                                } else {
                                    showNoData();
                                }
                            } else {
                                AppUtils.alertDialog(act, jsonObject.getString("Message"));
                                if (AppUtils.showLogs)
                                    Log.v(TAG, "executeGetMyOrdersRequest executed...Failed... called");
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
            AppUtils.showExceptionDialog(act);
        }
    }

    private void getOrdersListResult(JSONArray jsonArray) {
        try {
            ordersList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("OrderNo", "" + jsonObject.getString("OrderNo"));
                map.put("TotalAmount", "" + jsonObject.getString("ChAmt"));

                map.put("Name", "" + jsonObject.getString("MemFirstName"));
                map.put("Address1", "" + jsonObject.getString("Address1"));
                map.put("City", "" + jsonObject.getString("City"));
                map.put("PinCode", "" + jsonObject.getString("PinCode"));
                map.put("Mobl", "" + jsonObject.getString("Mobl"));
                map.put("CountryName", "" + jsonObject.getString("CountryName"));
                map.put("PinCode", "" + jsonObject.getString("PinCode"));
                map.put("Mobl", "" + jsonObject.getString("Mobl"));
                map.put("Email", "" + jsonObject.getString("EMail"));

//              map.put("TotalBV", ""+jsonObject.getString("TotalBV"));
                map.put("OrderStatus", "" + jsonObject.getString("OrderStatus"));
                map.put("ODate", "" + jsonObject.getString("OrderDate"));
                map.put("OrderCvp", "" + jsonObject.getString("OrderCvp"));
                map.put("Shipping", "" + jsonObject.getString("Shipping"));
                map.put("OrderAmt", "" + jsonObject.getString("OrderAmt"));
                map.put("OrderQty", "" + jsonObject.getString("OrderQty"));

                String ShippingStatus = jsonObject.getString("ShippingStatus");

                if (ShippingStatus.equalsIgnoreCase("y"))
                    map.put("ShippingStatus", "Delivered");
                else
                    map.put("ShippingStatus", "Pending");

                if (jsonObject.getString("OrderThru").equalsIgnoreCase("A"))
                    map.put("OrderVia", "App");
                else
                    map.put("OrderVia", "Web");

                ordersList.add(map);
            }

            if (AppUtils.showLogs) Log.v(TAG, "ordersList..." + ordersList);
            showListView();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void showListView() {
        try {
            if (ordersList.size() > 0) {
                adapter = new MyOrdersList_Adapter(act, ordersList);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                layout_listView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                layout_nodata.setVisibility(View.GONE);
            } else {
                showNoData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (adapter != null)
                adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
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
