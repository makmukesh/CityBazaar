package com.vpipl.citybazaar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vpipl.citybazaar.Adapters.ProductListAdapter;
import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;
import com.vpipl.citybazaar.model.ProductList_Data;
import com.vpipl.citybazaar.model.StackHelperState;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PlaceOrderActivity extends AppCompatActivity {

    Activity act = PlaceOrderActivity.this;
    private String TAG = "PlaceOrderActivity";

    RadioGroup radioGroup;
    RadioButton radioButton;
    Spinner purchanse_from;
    // Spinner1
    ArrayList<String> Company = new ArrayList<String>();
    private JSONArray result;
    // Spinner 2
    Spinner state;
    ArrayList<String> students1 = new ArrayList<String>();
    private JSONArray result1;

    List<StackHelperState> stackHelperList = new ArrayList<>();
    JSONArray jsonarray_statelist;
    String statecode = "0", statename = "";
    //Spnner
    Spinner payment_mode;
    String paymode;
    RadioButton radioButton1, radioButton2;
    String fromtype = "C";
    String PartyCode;
    // product list
    private RecyclerView rec_product_list;
    private static List<ProductList_Data> productList_Data;
    private ProductListAdapter productListAdapter;
    LinearLayout ll_paymode_bank, ll_paymode_dd;
    /*mukesh*/
    static TextView txt_bottom_totalsbv, txt_bottom_grossamount, txt_bottom_roundoff, txt_bottom_netpayable;

    EditText edtxt_address, edtxt_district, edtxt_city, edtxt_pincode, edtxt_transfer_acc_no, edtxt_deposit_bank_name, edtxt_reference_no,
            edtxt_trasnfer_name, edtxt_dd_number, edtxt_dd_payee_bank, edtxt_dd_payee_bank_branch, edtxt_remarks;
    // TextView edtxt_dd_date, edtxt_deposit_date, edtxt_upload_receipt;
    TextView edtxt_upload_receipt;
    EditText edtxt_dd_date, edtxt_deposit_date;
    String str_address, str_district, str_city, str_pincode, str_transfer_acc_no, str_deposit_bank_name, str_reference_no,
            str_trasnfer_name, str_dd_number, str_dd_payee_bank, str_dd_payee_bank_branch, str_remarks, str_dd_date, str_deposit_date, str_upload_receipt;
    Button btn_order_now, btn_cancel;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask;
    Bitmap bitmap_api;
    String imagepath = "", MinimumActivationBV = "", purchasefrom_type = "C";

    private Calendar myCalendar;
    private SimpleDateFormat sdf;
    private String whichdate = "";

    private final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (new Date().after(myCalendar.getTime())) {

                if (whichdate.equalsIgnoreCase("edtxt_deposit_date"))
                    edtxt_deposit_date.setText(sdf.format(myCalendar.getTime()));
                else if (whichdate.equalsIgnoreCase("edtxt_dd_date"))
                    edtxt_dd_date.setText(sdf.format(myCalendar.getTime()));

            } else {
                AppUtils.alertDialog(act, "Selected Date Can't be After today");
            }
        }
    };

    private void showdatePicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        datePickerDialog.show();
    }

    private DatePickerDialog datePickerDialog;

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);

        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));

        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        setContentView(R.layout.place_order_activity_main);
        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            AppUtils.changeStatusBarColor(act);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            ll_paymode_bank = findViewById(R.id.ll_paymode_bank);
            ll_paymode_dd = findViewById(R.id.ll_paymode_dd);

            edtxt_address = findViewById(R.id.edtxt_address);
            edtxt_district = findViewById(R.id.edtxt_district);
            edtxt_city = findViewById(R.id.edtxt_city);
            edtxt_pincode = findViewById(R.id.edtxt_pincode);
            edtxt_transfer_acc_no = findViewById(R.id.edtxt_transfer_acc_no);
            edtxt_deposit_date = findViewById(R.id.edtxt_deposit_date);
            edtxt_deposit_bank_name = findViewById(R.id.edtxt_deposit_bank_name);
            edtxt_reference_no = findViewById(R.id.edtxt_reference_no);
            edtxt_trasnfer_name = findViewById(R.id.edtxt_trasnfer_name);
            edtxt_upload_receipt = findViewById(R.id.edtxt_upload_receipt);
            edtxt_dd_number = findViewById(R.id.edtxt_dd_number);
            edtxt_dd_date = findViewById(R.id.edtxt_dd_date);
            edtxt_dd_payee_bank = findViewById(R.id.edtxt_dd_payee_bank);
            edtxt_dd_payee_bank_branch = findViewById(R.id.edtxt_dd_payee_bank_branch);
            edtxt_remarks = findViewById(R.id.edtxt_remarks);
            btn_order_now = findViewById(R.id.btn_order_now);
            btn_cancel = findViewById(R.id.btn_cancel);

            txt_bottom_totalsbv = findViewById(R.id.txt_bottom_totalsbv);
            txt_bottom_grossamount = findViewById(R.id.txt_bottom_grossamount);
            txt_bottom_roundoff = findViewById(R.id.txt_bottom_roundoff);
            txt_bottom_netpayable = findViewById(R.id.txt_bottom_netpayable);

            purchanse_from = (Spinner) findViewById(R.id.purchanse_from);
            state = (Spinner) findViewById(R.id.state);
//     Radio button
            radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
            radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
            radioButton2 = (RadioButton) findViewById(R.id.radioButton2);


            edtxt_address.setText(AppController.getSpUserInfo().getString(SPUtils.USER_ADDRESS, ""));
            edtxt_district.setText(AppController.getSpUserInfo().getString(SPUtils.USER_DISTRICT, ""));
            edtxt_city.setText(AppController.getSpUserInfo().getString(SPUtils.USER_CITY, ""));
            String str = AppController.getSpUserInfo().getString(SPUtils.USER_PINCODE, "");
            edtxt_pincode.setText(str);

            radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked == true) {
                        Company.clear();
                        purchasefrom_type = "C";
                        PurchaseFrom(purchasefrom_type);
                        productList_Data.clear();

                        if (!PartyCode.equalsIgnoreCase("null")) {
                            if (!PartyCode.equalsIgnoreCase("0")) {
                                ProductList(PartyCode);
                            } else {
                                productList_Data.clear();
                            }
                        }
                    } else {
                        Company.clear();
                        purchasefrom_type = "F";
                        PurchaseFrom(purchasefrom_type);
                        productList_Data.clear();
                        if (!PartyCode.equalsIgnoreCase("null")) {
                            if (!PartyCode.equalsIgnoreCase("0")) {
                                ProductList(PartyCode);
                            } else {
                                productList_Data.clear();
                            }
                        }
                    }
                }
            });

            //Spinner
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(act, android.R.layout.simple_spinner_dropdown_item, Company);
            purchanse_from.setAdapter(adapter);

            purchanse_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (result.length() > 0) {
                        PartyCode = getPartyCode(position);
                        ((TextView) view).setTextColor(Color.GRAY);
                        ((TextView) view).setTextSize(15);
                        productList_Data.clear();

                        if (!PartyCode.equalsIgnoreCase("0")) {
                            ProductList(PartyCode);
                        } else {
                            productList_Data.clear();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            //   spinner state
            ArrayAdapter<String> adapter1 =
                    new ArrayAdapter<String>(act, android.R.layout.simple_spinner_dropdown_item, students1);
            state.setAdapter(adapter1);

            state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                    if (jsonarray_statelist.length() > 0) {
                        statename = ((StackHelperState) state.getSelectedItem()).getStateName();
                        statecode = ((StackHelperState) state.getSelectedItem()).getCode();

                        if (statecode.equalsIgnoreCase("0")) {
                            statename = "";
                        }
                    }
                }

                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            // spinner 2
            List<String> categories3 = new ArrayList<String>();
            categories3.add("Bank Transfer/NEFT/RTGS");
            categories3.add("Demand Draft");
            payment_mode = (Spinner)

                    findViewById(R.id.payment_mode);

            ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(act, android.R.layout.simple_spinner_dropdown_item, categories3);
            payment_mode.setAdapter(adapter3);

            payment_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position,
                                           long id) {
                    paymode = parent.getItemAtPosition(position).toString().replace(" ", "%20");
                    ((TextView) view).setTextColor(Color.GRAY);
                    ((TextView) view).setTextSize(15);
                    if (parent.getItemAtPosition(position).toString().matches("Bank Transfer/NEFT/RTGS")) {
                        paymode = "Bank Deposit";
                        ll_paymode_bank.setVisibility(View.VISIBLE);
                        ll_paymode_dd.setVisibility(View.GONE);
                    } else {
                        paymode = "DD";
                        ll_paymode_bank.setVisibility(View.GONE);
                        ll_paymode_dd.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            rec_product_list = (RecyclerView)

                    findViewById(R.id.rec_product_list);
            rec_product_list.setHasFixedSize(true);
            rec_product_list.setLayoutManager(new

                    LinearLayoutManager(this));
            productList_Data = new ArrayList<>();
            productListAdapter = new

                    ProductListAdapter(productList_Data, this);

            FillStateMaster();

            PurchaseFrom(fromtype);
            productList_Data.clear();

            myCalendar = Calendar.getInstance();
            //  sdf = new SimpleDateFormat("dd MMM yyyy");
            sdf = new SimpleDateFormat("dd/MM/yyyy");
            edtxt_deposit_date.setText(sdf.format(myCalendar.getTime()));
            edtxt_dd_date.setText(sdf.format(myCalendar.getTime()));

           /* edtxt_dd_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    whichdate = "edtxt_dd_date";
                    showdatePicker();
                }
            });*/

            /*edtxt_deposit_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    whichdate = "edtxt_deposit_date";
                    showdatePicker();
                }
            });*/
            edtxt_upload_receipt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });
            btn_order_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(act, v);
                    ValidateData();
                    //  startPaymentRequest();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
           /* if (result != null) {
                if (result.length() > 0) {
                    if(!PartyCode.equalsIgnoreCase("0")) {
                        ProductList(PartyCode);
                    }
                    else {
                        productList_Data.clear();
                    }
                }
            }*/

            edtxt_deposit_date.addTextChangedListener(new TextWatcher() {
                private String current = "";
                private String ddmmyyyy = "DDMMYYYY";
                private Calendar cal = Calendar.getInstance();

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    if (!s.toString().equals(current)) {
                        String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                        String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                        int cl = clean.length();
                        int sel = cl;
                        for (int i = 2; i <= cl && i < 6; i += 2) {
                            sel++;
                        }
                        //Fix for pressing delete next to a forward slash
                        if (clean.equals(cleanC)) sel--;

                        if (clean.length() < 8) {
                            clean = clean + ddmmyyyy.substring(clean.length());
                        } else {
                            //This part makes sure that when we finish entering numbers
                            //the date is correct, fixing it otherwise
                            int day = Integer.parseInt(clean.substring(0, 2));
                            int mon = Integer.parseInt(clean.substring(2, 4));
                            int year = Integer.parseInt(clean.substring(4, 8));

                            mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                            cal.set(Calendar.MONTH, mon - 1);
                            year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                            cal.set(Calendar.YEAR, year);
                            // ^ first set year for the line below to work correctly
                            //with leap years - otherwise, date e.g. 29/02/2012
                            //would be automatically corrected to 28/02/2012

                            day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                            clean = String.format("%02d%02d%02d", day, mon, year);
                        }

                        clean = String.format("%s/%s/%s", clean.substring(0, 2),
                                clean.substring(2, 4),
                                clean.substring(4, 8));

                        sel = sel < 0 ? 0 : sel;
                        current = clean;
                        edtxt_deposit_date.setText(current);
                        edtxt_deposit_date.setSelection(sel < current.length() ? sel : current.length());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                }
            });
            edtxt_dd_date.addTextChangedListener(new TextWatcher() {
                private String current = "";
                private String ddmmyyyy = "DDMMYYYY";
                private Calendar cal = Calendar.getInstance();

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    if (!s.toString().equals(current)) {
                        String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                        String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                        int cl = clean.length();
                        int sel = cl;
                        for (int i = 2; i <= cl && i < 6; i += 2) {
                            sel++;
                        }
                        //Fix for pressing delete next to a forward slash
                        if (clean.equals(cleanC)) sel--;

                        if (clean.length() < 8) {
                            clean = clean + ddmmyyyy.substring(clean.length());
                        } else {
                            //This part makes sure that when we finish entering numbers
                            //the date is correct, fixing it otherwise
                            int day = Integer.parseInt(clean.substring(0, 2));
                            int mon = Integer.parseInt(clean.substring(2, 4));
                            int year = Integer.parseInt(clean.substring(4, 8));

                            mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                            cal.set(Calendar.MONTH, mon - 1);
                            year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                            cal.set(Calendar.YEAR, year);
                            // ^ first set year for the line below to work correctly
                            //with leap years - otherwise, date e.g. 29/02/2012
                            //would be automatically corrected to 28/02/2012

                            day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                            clean = String.format("%02d%02d%02d", day, mon, year);
                        }

                        clean = String.format("%s/%s/%s", clean.substring(0, 2),
                                clean.substring(2, 4),
                                clean.substring(4, 8));

                        sel = sel < 0 ? 0 : sel;
                        current = clean;
                        edtxt_dd_date.setText(current);
                        edtxt_dd_date.setSelection(sel < current.length() ? sel : current.length());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /// SPinner data purchase from
    private void PurchaseFrom(final String fromtype) {
        StringRequest stringRequest = new StringRequest(AppUtils.serviceAPIURL() + "FillPurchaseType?Type=" + fromtype,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            String Status = j.getString("Status");
                            String Message = j.getString("Message");
                            Log.e("Message", Message);
                            if (Status.matches("True")) {
                                result = j.getJSONArray("Data");
                                getPartyName(result);
                            } else {
                              /*  all_nointernet.setVisibility(View.GONE);
                                booking_form.setVisibility(View.GONE);
                                all_noidata.setVisibility(View.VISIBLE);
                                all_servererror.setVisibility(View.GONE);*/
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                           /* all_nointernet.setVisibility(View.GONE);
                            booking_form.setVisibility(View.GONE);
                            all_noidata.setVisibility(View.GONE);
                            all_servererror.setVisibility(View.VISIBLE);*/
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        /*all_nointernet.setVisibility(View.GONE);
                        booking_form.setVisibility(View.GONE);
                        all_noidata.setVisibility(View.GONE);
                        all_servererror.setVisibility(View.VISIBLE);*/
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getPartyName(JSONArray j) {
        /*if(purchasefrom_type.equalsIgnoreCase("C")){
            Company.add("-- Select Company --");
        }
        else {
            Company.add("-- Select Franchise --");
        }*/

        for (int i = 0; i < j.length(); i++) {
            try {
                JSONObject json = j.getJSONObject(i);
                Log.e("val", json.toString());
                Company.add(json.getString("PartyName"));
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(act, android.R.layout.simple_spinner_dropdown_item, Company);
                purchanse_from.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private String getPartyCode(int position) {
        String PartyCode = "";
        /*if(purchasefrom_type.equalsIgnoreCase("C")){
            Company.add("0");
        }
        else {
            Company.add("0");
        }*/
        try {
            JSONObject json = result.getJSONObject(position);
            PartyCode = json.getString("PartyCode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return PartyCode;
    }

    /// state
   /* private void FillStateMaster() {
        StringRequest stringRequest = new StringRequest(AppUtils.serviceAPIURL() + "FillStateMaster",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            String Status = j.getString("Status");
                            String Message = j.getString("Message");
                            Log.e("Message", Message);
                            if (Status.matches("True")) {
                                result1 = j.getJSONArray("Data");
                                getStateName(result1);
                            } else {
                              *//*  all_nointernet.setVisibility(View.GONE);
                                booking_form.setVisibility(View.GONE);
                                all_noidata.setVisibility(View.VISIBLE);
                                all_servererror.setVisibility(View.GONE);*//*
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                           *//* all_nointernet.setVisibility(View.GONE);
                            booking_form.setVisibility(View.GONE);
                            all_noidata.setVisibility(View.GONE);
                            all_servererror.setVisibility(View.VISIBLE);*//*
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        *//*all_nointernet.setVisibility(View.GONE);
                        booking_form.setVisibility(View.GONE);
                        all_noidata.setVisibility(View.GONE);
                        all_servererror.setVisibility(View.VISIBLE);*//*
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }*/
    private void FillStateMaster() {
        String URL = "" + AppUtils.serviceAPIURL() + "FillStateMaster";
        URL = URL.replace(" ", "%20");
        Log.e("URL", URL);
        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            String Status = j.getString("Status");
                            String Message = j.getString("Message");
                            Log.e("Message", Message);
                            if (j.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayData = j.getJSONArray("Data");
                                if (jsonArrayData.length() != 0) {
                                    jsonarray_statelist = jsonArrayData;
                                    spinState();
                                } else {
                                    jsonarray_statelist = new JSONArray("[{\"STATECODE\":0,\"State\":\"-- No State Found --\"}]");
                                    spinState();
                                }
                            } else {
                                jsonarray_statelist = new JSONArray("[{\"STATECODE\":0,\"State\":\"-- No State Found --\"}]");
                                spinState();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void spinState() {
        stackHelperList = new ArrayList<>();

        try {
            for (int i = 0; i < jsonarray_statelist.length(); i++) {
                JSONObject jsonobject = jsonarray_statelist.getJSONObject(i);
                statecode = jsonobject.getString("STATECODE");
                statename = jsonobject.getString("State");

                StackHelperState stackHelper = new StackHelperState();
                stackHelper.setStateName(statename);
                stackHelper.setCode(statecode);
                stackHelperList.add(stackHelper);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
/*        ArrayAdapter<StackHelperState> dataAdapter = new ArrayAdapter<StackHelperState>(this, android.R.layout.simple_selectable_list_item, stackHelperList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

        ArrayAdapter<StackHelperState> dataAdapter = new ArrayAdapter<StackHelperState>(this, R.layout.sppiner_item, stackHelperList);
        dataAdapter.setDropDownViewResource(R.layout.sppiner_item);
        state.setAdapter(dataAdapter);

        try {
            int stateposition = 0;
            for (int i = 0; i < jsonarray_statelist.length(); i++) {
                JSONObject jsonObject = jsonarray_statelist.getJSONObject(i);
                String str = AppController.getSpUserInfo().getString(SPUtils.USER_STATE_CODE, "");
                if (str.equals(jsonObject.getString("STATECODE"))) {
                    stateposition = i;
                }
                Log.d("State", jsonarray_statelist.getString(i));
            }
            state.setSelection(stateposition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /// ProductList
    private void ProductList(final String PartyCode) {
        String formno = AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppUtils.serviceAPIURL() + "RepurchaseProductList_INMemberPanel?SoldByValue=" + PartyCode + "&FormNo=" + formno, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("response", response.toString());
                    String Status = jsonObject.getString("Status");
                    if (Status.matches("True")) {
                        rec_product_list.setVisibility(View.VISIBLE);
                        JSONArray array = jsonObject.getJSONArray("ProductList");
                        JSONArray arrayMinimumBV = jsonObject.getJSONArray("MinimumBV");
                        if (arrayMinimumBV.length() > 0) {
                            JSONObject ob = arrayMinimumBV.getJSONObject(0);
                            MinimumActivationBV = ob.getString("ActivationBV");
                        }
                        for (int i = 0; i < array.length(); i++) {
                            ProductList_Data selectedProduct = new ProductList_Data();
                            JSONObject ob = array.getJSONObject(i);

                            selectedProduct.setProdId("" + ob.getString("ProdId"));
                            selectedProduct.setProductName("" + ob.getString("ProductName"));
                            selectedProduct.setQty("0");

                            selectedProduct.setProductDesc("" + ob.getString("ProductDesc"));
                            selectedProduct.setBatchNo("" + ob.getString("BatchNo"));
                            selectedProduct.setAvailQty("" + ob.getString("AvailQty"));
                            selectedProduct.setMRP("" + ob.getString("MRP"));
                            selectedProduct.setDP("" + ob.getString("DP"));
                            selectedProduct.setRate("" + ob.getString("Rate"));
                            selectedProduct.setBV("" + ob.getString("BV"));
                            selectedProduct.setPV("" + ob.getString("PV"));
                            selectedProduct.setRP("" + ob.getString("RP"));
                            selectedProduct.setDiscPer("" + ob.getString("DiscPer"));
                            selectedProduct.setDiscAmt("" + ob.getString("DiscAmt"));
                            selectedProduct.setTaxType("" + ob.getString("TaxType"));
                            selectedProduct.setTaxPer("" + ob.getString("TaxPer"));
                            selectedProduct.setIsKit("" + ob.getString("IsKit"));
                            selectedProduct.setProdType("" + ob.getString("ProdType"));
                            selectedProduct.setTaxAmt("" + ob.getString("TaxAmt"));
                            selectedProduct.setCommssnOn("" + ob.getString("CommssnOn"));
                            productList_Data.add(selectedProduct);
                        }

                        rec_product_list.setAdapter(productListAdapter);
                    } else {
                        rec_product_list.setVisibility(View.GONE);
                        AppUtils.alertDialog(act, jsonObject.getString("Message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /// Calculation
    public static String calculateSelectedProductTotalSBV() {
        double amount = 0.0d;
        double qty = 0.0d;
        try {
            for (int i = 0; i < productList_Data.size(); i++) {
                double countAmount = 0.0d;
                //countAmount = Double.parseDouble(AppController.selectedProductsList.get(i).getTotalamt());
                countAmount = Double.parseDouble(productList_Data.get(i).getBV());
                qty = Double.parseDouble(productList_Data.get(i).getQty());
                amount = amount + countAmount * qty;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (amount) + "";
    }

    public static String calculateSelectedProductTotalGrossAmount() {
        double amount = 0.0d;
        double qty = 0.0d;
        try {
            for (int i = 0; i < productList_Data.size(); i++) {
                double countAmount = 0.0d;
                //countAmount = Double.parseDouble(AppController.selectedProductsList.get(i).getTotalamt());
                countAmount = Double.parseDouble(productList_Data.get(i).getRate());
                qty = Double.parseDouble(productList_Data.get(i).getQty());
                amount = amount + countAmount * qty;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (amount) + "";
    }

    public static String calculateSelectedProductTotalRoundoff() {
        String rndoff = "";
        double d_rndoff = 0.00;
        try {
            DecimalFormat df2 = new DecimalFormat("#.##");

            double final_amount = Double.parseDouble(calculateSelectedProductTotalGrossAmount());
            String formated_amount = String.format("%.0f", final_amount);
            rndoff = String.valueOf(Double.parseDouble(formated_amount) - final_amount);

            if (final_amount > Double.parseDouble(formated_amount))
                rndoff = "" + rndoff;

            if (rndoff.equalsIgnoreCase("-0.00")) {
                rndoff = "0.00";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rndoff;
    }

    public static String calculateSelectedProductNetPayable() {
        double amount = 0.0d;
        try {
            amount = (Double.parseDouble(calculateSelectedProductTotalGrossAmount()) + Double.parseDouble(calculateSelectedProductTotalRoundoff()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "" + (int) amount;
    }

    public String calculateSelectedProductTotalQty() {
        Double qty = 0.0;
        try {
            for (int i = 0; i < productList_Data.size(); i++) {
                qty = qty + (Double.parseDouble(productList_Data.get(i).getQty()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return qty + "";
    }

    private String calculateSelectedProductTotalDp() {
        double amount = 0.0d;
        try {
            for (int i = 0; i < productList_Data.size(); i++) {
                double countAmount;
                countAmount = ((Double.parseDouble(productList_Data.get(i).getDP())) * (Double.parseDouble(productList_Data.get(i).getQty())));
                amount = amount + countAmount;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (amount) + "";
    }

    public static void setNetPaybleValue() {
        try {
            txt_bottom_totalsbv.setText("\u20B9 " + String.format("%.2f", Double.parseDouble(calculateSelectedProductTotalSBV())));
            txt_bottom_grossamount.setText("\u20B9 " + String.format("%.2f", Double.parseDouble(calculateSelectedProductTotalGrossAmount())));
            txt_bottom_roundoff.setText("\u20B9 " + String.format("%.2f", Double.parseDouble(calculateSelectedProductTotalRoundoff())));
            txt_bottom_netpayable.setText("\u20B9 " + String.format("%.2f", Double.parseDouble(calculateSelectedProductNetPayable())));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ValidateData() {
        boolean cancel = false;
        View focusView = null;

/*        if (!MinimumActivationBV.equalsIgnoreCase("")) {
            if (Double.parseDouble(MinimumActivationBV) >= Double.parseDouble(calculateSelectedProductTotalSBV())) {
                AppUtils.alertDialog(act, "Can't order place below " + MinimumActivationBV);
                focusView = edtxt_address;
                cancel = true;
            }
        }*/

        str_address = edtxt_address.getText().toString().trim();
        str_district = edtxt_district.getText().toString().trim();
        str_city = edtxt_city.getText().toString().trim();
        str_pincode = edtxt_pincode.getText().toString().trim();
        str_transfer_acc_no = edtxt_transfer_acc_no.getText().toString().trim();
        str_deposit_bank_name = edtxt_deposit_bank_name.getText().toString().trim();
        str_reference_no = edtxt_reference_no.getText().toString().trim();
        str_trasnfer_name = edtxt_trasnfer_name.getText().toString().trim();
        str_dd_number = edtxt_dd_number.getText().toString().trim();
        str_dd_payee_bank = edtxt_dd_payee_bank.getText().toString().trim();
        str_dd_payee_bank_branch = edtxt_dd_payee_bank_branch.getText().toString().trim();
        str_remarks = edtxt_remarks.getText().toString().trim();
        str_dd_date = edtxt_dd_date.getText().toString().trim();
        str_deposit_date = edtxt_deposit_date.getText().toString().trim();
        str_upload_receipt = edtxt_upload_receipt.getText().toString().trim();

        if (TextUtils.isEmpty(str_address)) {
            AppUtils.alertDialog(act, "Enter Your Addrsss");
            focusView = edtxt_address;
            cancel = true;
        } else if (TextUtils.isEmpty(statecode)) {
            AppUtils.alertDialog(act, "Select Your State");
            focusView = edtxt_address;
            cancel = true;
        } else if (TextUtils.isEmpty(str_district)) {
            AppUtils.alertDialog(act, "Enter Your District");
            focusView = edtxt_address;
            cancel = true;
        } else if (!TextUtils.isEmpty(str_pincode) && str_pincode.length() != 6) {
            AppUtils.alertDialog(act, "Invalid Pin Code");
            focusView = edtxt_pincode;
            cancel = true;
        } else if (paymode.equalsIgnoreCase("Bank Deposit")) {
            if (TextUtils.isEmpty(str_transfer_acc_no)) {
                AppUtils.alertDialog(act, "Enter Your Transfer Account No");
                focusView = edtxt_transfer_acc_no;
                cancel = true;
            } else if (TextUtils.isEmpty(str_deposit_date)) {
                AppUtils.alertDialog(act, "Enter Your Deposit Date");
                focusView = edtxt_deposit_date;
                cancel = true;
            } else if (str_deposit_date.equalsIgnoreCase("DD/MM/YYYY")) {
                AppUtils.alertDialog(act, "Enter Your Deposit Date");
                focusView = edtxt_deposit_date;
                cancel = true;
            } else if (TextUtils.isEmpty(str_deposit_bank_name)) {
                AppUtils.alertDialog(act, "Enter Your Deposit Bank Name");
                focusView = edtxt_deposit_bank_name;
                cancel = true;
            } else if (TextUtils.isEmpty(str_reference_no)) {
                AppUtils.alertDialog(act, "Enter Your Reference No.");
                focusView = edtxt_reference_no;
                cancel = true;
            } else if (TextUtils.isEmpty(str_trasnfer_name)) {
                AppUtils.alertDialog(act, "Enter Your Transfer Name");
                focusView = edtxt_trasnfer_name;
                cancel = true;
            }
        } else {
            if (TextUtils.isEmpty(str_dd_number)) {
                AppUtils.alertDialog(act, "Enter Your DD No");
                focusView = edtxt_dd_number;
                cancel = true;
            } else if (TextUtils.isEmpty(str_dd_date)) {
                AppUtils.alertDialog(act, "Enter Your DD Date");
                focusView = edtxt_dd_date;
                cancel = true;
            } else if (str_dd_date.equalsIgnoreCase("DD/MM/YYYY")) {
                AppUtils.alertDialog(act, "Enter Your DD Date");
                focusView = edtxt_dd_date;
                cancel = true;
            } else if (TextUtils.isEmpty(str_dd_payee_bank)) {
                AppUtils.alertDialog(act, "Enter Your DD Payee Bank");
                focusView = edtxt_dd_payee_bank;
                cancel = true;
            } else if (TextUtils.isEmpty(str_dd_payee_bank_branch)) {
                AppUtils.alertDialog(act, "Enter Your DD Payee Bank Branch");
                focusView = edtxt_dd_payee_bank_branch;
                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(act)) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        startPaymentRequest();
                    }
                };
                new Handler().postDelayed(runnable, 500);
            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    /*Place Order Submit Code Added by mukesh 18-01-2020 02:35 PM*/
    private void startPaymentRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();

            JSONArray jsonArrayOrder = new JSONArray();
            JSONArray jsonArrayOrderDetail = new JSONArray();

            JSONObject jsonObjectOrder = new JSONObject();
            //  jsonObjectOrder.put("MemFirstName", productList_Data.get(0));
            jsonObjectOrder.put("MemFirstName", AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
            jsonObjectOrder.put("MemLasttName", AppController.getSpUserInfo().getString(SPUtils.USER_LAST_NAME, "").trim());
            jsonObjectOrder.put("Address1", str_address.trim());
            jsonObjectOrder.put("Address2", "");
            jsonObjectOrder.put("StateID", statecode.trim().replaceAll(",", " "));
            jsonObjectOrder.put("StateName", statename.trim().replaceAll(",", " "));
            jsonObjectOrder.put("District", str_district.trim().replaceAll(",", " "));

            String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
            if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
                jsonObjectOrder.put("UserType", "D");
                jsonObjectOrder.put("IDType", "D");
            } else {
                jsonObjectOrder.put("UserType", "N");
                jsonObjectOrder.put("IDType", "G");
            }

            jsonObjectOrder.put("PayMode", "COD");
            jsonObjectOrder.put("Remarks", "");

            jsonObjectOrder.put("ChDDNo", "0");
            jsonObjectOrder.put("ChDate", "");
            jsonObjectOrder.put("BankName", "");
            jsonObjectOrder.put("BranchName", "");

            jsonObjectOrder.put("TotalDP", calculateSelectedProductNetPayable().trim().replace(",", " "));
            jsonObjectOrder.put("Item", "" + productList_Data.size());
            jsonObjectOrder.put("TotalQty", calculateSelectedProductTotalQty().trim().replace(",", " "));
            jsonObjectOrder.put("HostIp", AppUtils.getDeviceID(act).trim().replace(",", " "));
            jsonObjectOrder.put("ShippingCharge", "");

            jsonObjectOrder.put("color", "");
            jsonObjectOrder.put("Size", "");
            jsonObjectOrder.put("Pack", "0");
            jsonObjectOrder.put("Packing", "");
            jsonObjectOrder.put("OrderFor", "");

            jsonObjectOrder.put("City", str_city.trim().replace(",", " "));
            jsonObjectOrder.put("PinCode", str_pincode.trim().replace(",", " "));
            jsonObjectOrder.put("MobileNo", AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, "").trim().replace(",", " "));
            jsonObjectOrder.put("Email", AppController.getSpUserInfo().getString(SPUtils.USER_EMAIL, "").trim().replace(",", " "));
            jsonObjectOrder.put("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "").trim().replace(",", " "));
            jsonObjectOrder.put("TotalBV", calculateSelectedProductTotalSBV().trim().replace(",", " "));

            jsonObjectOrder.put("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "").trim().replace(",", " "));
            jsonObjectOrder.put("ShipCharge", "");

            jsonObjectOrder.put("AmountBeforeTDR", "" + calculateSelectedProductTotalGrossAmount().trim().replaceAll(",", " "));
            jsonObjectOrder.put("TDRAmount", "" + calculateSelectedProductTotalGrossAmount().trim().replaceAll(",", " "));
            jsonObjectOrder.put("RndOff", "" + calculateSelectedProductTotalRoundoff().trim().replaceAll("-", ""));

            jsonArrayOrder.put(jsonObjectOrder);

            for (int j = 0; j < productList_Data.size(); j++) {
                JSONObject jsonObjectDetail = new JSONObject();
                if (!productList_Data.get(j).getQty().trim().replace(",", " ").equalsIgnoreCase("0")) {

                    jsonObjectDetail.put("Productid", productList_Data.get(j).getProdId().trim().replace(",", " "));

                    //TODO Weight
                    jsonObjectDetail.put("ProductName", productList_Data.get(j).getProductName().trim().replace(",", " "));
                    jsonObjectDetail.put("Qty", productList_Data.get(j).getQty().trim().replace(",", " "));
                    jsonObjectDetail.put("DP", productList_Data.get(j).getDP().trim().replace(",", " "));
                    jsonObjectDetail.put("Price", productList_Data.get(j).getRate().trim().replace(",", " "));

                    double SubTotal = 0.0d;
                    SubTotal = ((Double.parseDouble(productList_Data.get(j).getDP())) * (Double.parseDouble(productList_Data.get(j).getQty())));

                    jsonObjectDetail.put("SubTotal", "" + (SubTotal));
                    jsonObjectDetail.put("colorDetails", "");
                    jsonObjectDetail.put("SizeDetails", "");
                    jsonObjectDetail.put("PackDetails", "0");

                    jsonObjectDetail.put("PackingDetails", "");
                    jsonObjectDetail.put("OrderForDetails", "");
                    jsonObjectDetail.put("ShipChargeDetails", "");

                    jsonObjectDetail.put("ImageUrl", "");

                    jsonObjectDetail.put("BV", "" + productList_Data.get(j).getBV().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("UID", "");
                    jsonObjectDetail.put("IsKit", "" + productList_Data.get(j).getIsKit().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("ProdType", "" + productList_Data.get(j).getProdType().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("DiscountPer", "" + productList_Data.get(j).getDiscPer().trim().replace(",", " ").replace("\\", ""));

                    jsonArrayOrderDetail.put(jsonObjectDetail);
                }

            }
            postParameters.add(new BasicNameValuePair("Order", jsonArrayOrder.toString()));
            postParameters.add(new BasicNameValuePair("OrderDetails", "" + jsonArrayOrderDetail.toString()));
            postParameters.add(new BasicNameValuePair("PayMode", "" + paymode));

            if (paymode.equalsIgnoreCase("Bank Deposit")) {
                postParameters.add(new BasicNameValuePair("AccountNo", "" + edtxt_transfer_acc_no.getText().toString()));
                postParameters.add(new BasicNameValuePair("BankName", "" + edtxt_deposit_bank_name.getText().toString()));
                postParameters.add(new BasicNameValuePair("BranchName", "" + edtxt_reference_no.getText().toString()));
                postParameters.add(new BasicNameValuePair("DepositDate", "" + AppUtils.getFormatDate1(edtxt_deposit_date.getText().toString())));
                postParameters.add(new BasicNameValuePair("TransfereeName", "" + edtxt_trasnfer_name.getText().toString()));
            } else {
                postParameters.add(new BasicNameValuePair("AccountNo", "" + edtxt_dd_number.getText().toString()));
                postParameters.add(new BasicNameValuePair("BankName", "" + edtxt_dd_payee_bank.getText().toString()));
                postParameters.add(new BasicNameValuePair("BranchName", "" + edtxt_dd_payee_bank_branch.getText().toString()));
                postParameters.add(new BasicNameValuePair("DepositDate", "" + AppUtils.getFormatDate1(edtxt_dd_date.getText().toString())));
                postParameters.add(new BasicNameValuePair("TransfereeName", ""));
            }
            postParameters.add(new BasicNameValuePair("ImageByteCode", "" + AppUtils.getBase64StringFromBitmap(bitmap_api)));
            postParameters.add(new BasicNameValuePair("Type", "PNG"));

            //F for First Purchase and R for Re-Purchase
            if (AppController.getSpUserInfo().getString(SPUtils.USER_ACTIVE_STATUS, "").equalsIgnoreCase("N")) {
                postParameters.add(new BasicNameValuePair("OrderType", "F"));
            } else {
                postParameters.add(new BasicNameValuePair("OrderType", "R"));
            }
            postParameters.add(new BasicNameValuePair("ActiveStatus", "" + AppController.getSpUserInfo().getString(SPUtils.USER_ACTIVE_STATUS, "")));

            executeToMakeOrderPaymentRequest(postParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToMakeOrderPaymentRequest(final List<NameValuePair> postParameters) {
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
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodAddOrder_INMemberPanel, TAG);
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
                                //   AppController.selectedProductsList.clear();
                                JSONArray jsonArrayOrderDetails = jsonObject.getJSONArray("OrderDetails");
                                JSONObject jsonObjectMainOrder = jsonArrayOrderDetails.getJSONObject(0);
                                /*  startActivity(new Intent(act, ThanksScreen_Activity.class).putExtra("ORDERNUMBER", jsonObjectMainOrder.getString("OrderNo")));*/

                                AppUtils.alertDialogWithFinishDashboard(act, "" +
                                        jsonObject.getString("Message") + " : " + jsonObjectMainOrder.getString("OrderNo"));
                                // finish();

                            } else {
                                AppUtils.alertDialog(act, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(act);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    /*Code added by mukesh upload recipt photo selection 21-01-2020*/
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //   boolean result = Utility.checkPermission(act);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    // if (result)
                    cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    //   if (result)
                    galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        String imageStoragePath = destination.getAbsolutePath();
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //iv_Profile_Pic_dash.setImageBitmap(bitmap);
        bitmap_api = bitmap;
        imagepath = bitmap.toString();
        edtxt_upload_receipt.setText("" + imagepath);
        Log.e("from camera data", imageStoragePath);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bitmap_api = bm;
        //  iv_Profile_Pic_dash.setImageBitmap(bm);
        imagepath = bm.toString();
        edtxt_upload_receipt.setText("" + imagepath);
       /* File f = new File(imagepath);
        String imageName = f.getName();*/
        Log.e("from gallery data", imagepath);
    }
}
