package com.vpipl.citybazaar;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.test.pg.secure.pgsdkv4.PGConstants;
import com.test.pg.secure.pgsdkv4.PaymentGatewayPaymentInitializer;
import com.test.pg.secure.pgsdkv4.PaymentParams;
import com.vpipl.citybazaar.Adapters.ProductListAdapter;
import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;
import com.vpipl.citybazaar.Utils.SampleAppConstants;
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

public class PlaceOrderOnlineActivity extends AppCompatActivity {

    Activity act = PlaceOrderOnlineActivity.this;
    private String TAG = "PlaceOrderActivity";

    RadioGroup radioGroup;
    RadioButton radioButton_In_Stock_Products, radioButton_All_Products;
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
    LinearLayout ll_paymode_bank, ll_paymode_dd, ll_paymode_op;
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

    static TextView txt_bottom_Total_IGST_Tax_Amount, txt_bottom_Total_CGST_Tax_Amount, txt_bottom_Total_SGST_Tax_Amount;
    static TextView edtxt_bottom_Order_Amount, edtxt_bottom_TDR, edtxt_bottom_Shipping, edtxt_bottom_Total_Amount, edtxt_bottom_Round_Off, edtxt_bottom_Net_Payable_Amount;
    static Double d_order_amount = 0.0, d_tdr = 0.0, d_shipping = 0.0, final_total_dp = 0.0;
    static String static_paymode = "W", static_TDR = "0.00", static_TTl_Amt_without_TDR = "0.00";
    TextView txt_available_wb;
    Double awb = 0.0;
    String str_StockSts = "Y";

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
        setContentView(R.layout.place_order_online_activity_main);
        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            AppUtils.changeStatusBarColor(act);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            ll_paymode_bank = findViewById(R.id.ll_paymode_bank);
            ll_paymode_dd = findViewById(R.id.ll_paymode_dd);
            ll_paymode_op = findViewById(R.id.ll_paymode_op);

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
            txt_bottom_Total_IGST_Tax_Amount = findViewById(R.id.txt_bottom_Total_IGST_Tax_Amount);
            txt_bottom_Total_CGST_Tax_Amount = findViewById(R.id.txt_bottom_Total_CGST_Tax_Amount);
            txt_bottom_Total_SGST_Tax_Amount = findViewById(R.id.txt_bottom_Total_SGST_Tax_Amount);
            txt_bottom_grossamount = findViewById(R.id.txt_bottom_grossamount);
            txt_bottom_roundoff = findViewById(R.id.txt_bottom_roundoff);
            txt_bottom_netpayable = findViewById(R.id.txt_bottom_netpayable);

            edtxt_bottom_Order_Amount = findViewById(R.id.edtxt_bottom_Order_Amount);
            edtxt_bottom_TDR = findViewById(R.id.edtxt_bottom_TDR);
            edtxt_bottom_Shipping = findViewById(R.id.edtxt_bottom_Shipping);
            edtxt_bottom_Total_Amount = findViewById(R.id.edtxt_bottom_Total_Amount);
            edtxt_bottom_Round_Off = findViewById(R.id.edtxt_bottom_Round_Off);
            edtxt_bottom_Net_Payable_Amount = findViewById(R.id.edtxt_bottom_Net_Payable_Amount);

            txt_available_wb = findViewById(R.id.txt_available_wb);

            purchanse_from = (Spinner) findViewById(R.id.purchanse_from);
            state = (Spinner) findViewById(R.id.state);
//     Radio button
            radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
            radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
            radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
            radioButton_In_Stock_Products = (RadioButton) findViewById(R.id.radioButton_In_Stock_Products);
            radioButton_All_Products = (RadioButton) findViewById(R.id.radioButton_All_Products);

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
            radioButton_In_Stock_Products.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked == true) {
                        Company.clear();
                        productList_Data.clear();

                        if (!PartyCode.equalsIgnoreCase("null")) {
                            if (!PartyCode.equalsIgnoreCase("0")) {
                                //  ll_second_hide.setVisibility(View.VISIBLE);
                                str_StockSts = "Y";
                                ProductList(PartyCode);
                            } else {
                                //  ll_second_hide.setVisibility(View.GONE);
                                productList_Data.clear();
                            }
                        }
                    } else {
                        Company.clear();
                        productList_Data.clear();
                        if (!PartyCode.equalsIgnoreCase("null")) {
                            if (!PartyCode.equalsIgnoreCase("0")) {
                                str_StockSts = "N";
                                ProductList(PartyCode);
                                // ll_second_hide.setVisibility(View.VISIBLE);
                            } else {
                                productList_Data.clear();
                                // ll_second_hide.setVisibility(View.GONE);
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
            categories3.add("Wallet");
            categories3.add("Bank Transfer/NEFT/RTGS");
           /* categories3.add("Demand Draft");
            categories3.add("Online Payment");*/
            payment_mode = (Spinner) findViewById(R.id.payment_mode);

            ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(act, android.R.layout.simple_spinner_dropdown_item, categories3);
            payment_mode.setAdapter(adapter3);

            payment_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position,
                                           long id) {
                    paymode = parent.getItemAtPosition(position).toString().replace(" ", "%20");
                    ((TextView) view).setTextColor(Color.GRAY);
                    ((TextView) view).setTextSize(15);
                    if (parent.getItemAtPosition(position).toString().matches("Wallet")) {
                        paymode = "W";
                        ll_paymode_bank.setVisibility(View.GONE);
                        ll_paymode_dd.setVisibility(View.GONE);
                        ll_paymode_op.setVisibility(View.GONE);
                    } else if (parent.getItemAtPosition(position).toString().matches("Bank Transfer/NEFT/RTGS")) {
                        paymode = "Bank Deposit";
                        ll_paymode_bank.setVisibility(View.VISIBLE);
                        ll_paymode_dd.setVisibility(View.GONE);
                        ll_paymode_op.setVisibility(View.GONE);
                    } else if (parent.getItemAtPosition(position).toString().matches("Demand Draft")) {
                        paymode = "DD";
                        ll_paymode_bank.setVisibility(View.GONE);
                        ll_paymode_dd.setVisibility(View.VISIBLE);
                        ll_paymode_op.setVisibility(View.VISIBLE);
                    } else if (parent.getItemAtPosition(position).toString().matches("Online Payment")) {
                        paymode = "OP";
                        ll_paymode_bank.setVisibility(View.GONE);
                        ll_paymode_dd.setVisibility(View.GONE);
                        ll_paymode_op.setVisibility(View.VISIBLE);
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
            executeWalletBalanceRequest();

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
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppUtils.serviceAPIURL() +
                "RepurchaseProductList_INMemberPanel?SoldByValue=" + PartyCode + "&FormNo=" + formno + "&StockSts=" + str_StockSts, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("response", "RepurchaseProductList_INMemberPanel  :  " + response.toString());
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

                            selectedProduct.setCGSTPer("" + ob.getString("CGSTPer"));
                            selectedProduct.setCGSTAmt("" + ob.getString("CGSTAmt"));
                            selectedProduct.setIGSTPer("" + ob.getString("IGSTPer"));
                            selectedProduct.setIGSTAmt("" + ob.getString("IGSTAmt"));
                            selectedProduct.setSGST_TaxPer("" + ob.getString("TaxPer"));
                            selectedProduct.setSGST_TaxAmt("" + ob.getString("TaxAmt"));

                            selectedProduct.setDiscPer("" + ob.getString("DiscPer"));
                            selectedProduct.setDiscAmt("" + ob.getString("DiscAmt"));
                            selectedProduct.setTaxType("" + ob.getString("TaxType"));
                            selectedProduct.setTaxPer("" + ob.getString("TaxPer"));
                            selectedProduct.setIsKit("" + ob.getString("IsKit"));
                            selectedProduct.setProdType("" + ob.getString("ProdType"));
                            selectedProduct.setTaxAmt("" + ob.getString("TaxAmt"));

                            selectedProduct.setBarcode("" + ob.getString("Barcode"));
                            selectedProduct.setHSNCode("" + ob.getString("HSNCode"));
                            selectedProduct.setUOM("" + ob.getString("UOM"));
                            selectedProduct.setInvRate("" + ob.getString("Rate"));


                            /*jsonObjectDetail.put("Barcode", "" + productList_Data.get(j).getBarcode().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("HSNCode", "" + productList_Data.get(j).getHSNCode().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("UOM", "" + productList_Data.get(j).getUOM().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("TaxType", "" + productList_Data.get(j).getTaxType().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("InvRate"*/

                           /* selectedProduct.setCGSTPer("10");
                            selectedProduct.setCGSTAmt("50");
                            selectedProduct.setIGSTPer("12");
                            selectedProduct.setIGSTAmt("60");
                            selectedProduct.setSGST_TaxPer("14");
                            selectedProduct.setSGST_TaxAmt("80");

                            selectedProduct.setDiscPer("8");
                            selectedProduct.setDiscAmt("68");
                            selectedProduct.setTaxType("" + ob.getString("TaxType"));
                            selectedProduct.setTaxPer("5");
                            selectedProduct.setIsKit("" + ob.getString("IsKit"));
                            selectedProduct.setProdType("" + ob.getString("ProdType"));
                            selectedProduct.setTaxAmt("55");*/
                            //  selectedProduct.setCommssnOn("" + ob.getString("CommssnOn"));
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

   /* /// Calculation
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
    }*/

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

    public static String calculateSelectedTotalCGSTAmt() {
        double amount = 0.0d;
        double qty = 0.0d;
        try {
            for (int i = 0; i < productList_Data.size(); i++) {
                double countAmount = 0.0d;
                countAmount = Double.parseDouble(productList_Data.get(i).getCGSTAmt());
                qty = Double.parseDouble(productList_Data.get(i).getQty());
                amount = amount + countAmount * qty;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (amount) + "";
    }

    public static String calculateSelectedTotalIGSTAmt() {
        double amount = 0.0d;
        double qty = 0.0d;
        try {
            for (int i = 0; i < productList_Data.size(); i++) {
                double countAmount = 0.0d;
                countAmount = Double.parseDouble(productList_Data.get(i).getIGSTAmt());
                qty = Double.parseDouble(productList_Data.get(i).getQty());
                amount = amount + countAmount * qty;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (amount) + "";
    }

    public static String calculateSelectedTotalSGSTAmt() {
        double amount = 0.0d;
        double qty = 0.0d;
        try {
            for (int i = 0; i < productList_Data.size(); i++) {
                double countAmount = 0.0d;
                countAmount = Double.parseDouble(productList_Data.get(i).getSGST_TaxAmt());
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

    public static String calculateSelectedProductTotalGrossAmountWithTax() {
        double amount = 0.0d;
        double qty = 0.0d;
        try {
            if (productList_Data.size() > 0) {
                Double a = Double.parseDouble(calculateSelectedProductTotalGrossAmount());
                Double b = Double.parseDouble(calculateSelectedTotalCGSTAmt());
                Double c = Double.parseDouble(calculateSelectedTotalIGSTAmt());
                Double d = Double.parseDouble(calculateSelectedTotalSGSTAmt());
                amount = a + b + c + d;

                Log.e("grosswithtax", "TotalGrossAmount  " + calculateSelectedProductTotalGrossAmount() + " TotalCGSTAmt " + calculateSelectedTotalCGSTAmt()
                        + "  TotalIGSTAmt " + calculateSelectedTotalIGSTAmt() + " TotalSGSTAmt  " + calculateSelectedTotalSGSTAmt() + " amount  " + amount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (amount) + "";
    }

    public static String calculateSelectedProductTotalRoundoff() {
        String rndoff = "";
        try {
            DecimalFormat df2 = new DecimalFormat("#.##");

            double final_amount = Double.parseDouble(calculateSelectedProductTotalGrossAmountWithTax());
            String formated_amount = String.format("%.0f", final_amount);
            rndoff = "" + String.format("%.2f", (Double.parseDouble(formated_amount)) - final_amount);

            if (final_amount > Double.parseDouble(formated_amount))
                rndoff = "" + rndoff;

            Log.e("TotalRoundoff", "TotalGrossAmountWithTax  " + calculateSelectedProductTotalGrossAmountWithTax() + " rndoff " + rndoff);

            if (rndoff.equalsIgnoreCase("-0.00")) {
                rndoff = "0.00";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rndoff;
    }

    public static String calculateSelectedFinalTotalRoundoff(Double ttl) {
        String rndoff = "";
        try {
            DecimalFormat df2 = new DecimalFormat("#.##");

            double final_amount = ttl;
            String formated_amount = String.format("%.0f", final_amount);
            rndoff = "" + String.format("%.2f", (Double.parseDouble(formated_amount) - final_amount));

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
            String a = calculateSelectedProductTotalGrossAmountWithTax();
            String b = calculateSelectedProductTotalRoundoff();
            //   a = String.format("%.0f",Double.parseDouble(a));

            /*if(b.startsWith("-")){
              //  amount = (Double.parseDouble(a) - Double.parseDouble(b.replaceAll("-" , "")));
            }
            else {*/
            amount = (Double.parseDouble(a) + Double.parseDouble(b));
            //}

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "" + (int) amount;
    }

    public static String calculateSelectedProductTDRAmount() {
        double amount = 0.0d;
        try {  //500/100*2
            if (static_paymode.equalsIgnoreCase("OP")) {
                amount = Double.parseDouble(calculateSelectedProductNetPayable()) / 100 * 2;
            } else {
                amount = 0.0;
            }
            static_TDR = "" + amount;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (amount) + "";
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

    /*public static void setNetPaybleValue() {
        try {
            txt_bottom_totalsbv.setText("\u20B9 " + String.format("%.2f", Double.parseDouble(calculateSelectedProductTotalSBV())));
            txt_bottom_grossamount.setText("\u20B9 " + String.format("%.2f", Double.parseDouble(calculateSelectedProductTotalGrossAmount())));
            txt_bottom_roundoff.setText("\u20B9 " + String.format("%.2f", Double.parseDouble(calculateSelectedProductTotalRoundoff())));
            txt_bottom_netpayable.setText("\u20B9 " + String.format("%.2f", Double.parseDouble(calculateSelectedProductNetPayable())));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    public static void setNetPaybleValue2() {
        d_order_amount = Double.parseDouble(calculateSelectedProductNetPayable());

        edtxt_bottom_TDR.setText("\u20B9 " + calculateSelectedProductTDRAmount());
        d_tdr = Double.parseDouble(calculateSelectedProductTDRAmount());
        edtxt_bottom_Order_Amount.setText("" + String.format("%.2f", Double.parseDouble(calculateSelectedProductNetPayable())));

        static_TTl_Amt_without_TDR = edtxt_bottom_Order_Amount.getText().toString();

        if (Double.parseDouble(calculateSelectedProductNetPayable()) < 1000) {
            edtxt_bottom_Shipping.setText("\u20B9 " + "50.00");
            d_shipping = 50.00;
        } else {
            edtxt_bottom_Shipping.setText("\u20B9 " + "0.00");
            d_shipping = 0.00;
        }
        //  edtxt_bottom_Shipping.setText("\u20B9 " + "0.00");
        //  d_shipping = 0.00;

        edtxt_bottom_Total_Amount.setText("" + String.format("%.2f", d_order_amount + d_tdr + d_shipping));
        Double ttl = d_order_amount + d_tdr + d_shipping;
        Double rnd_off = Double.parseDouble(calculateSelectedFinalTotalRoundoff(ttl));
        final_total_dp = d_order_amount + d_tdr + rnd_off;
        edtxt_bottom_Round_Off.setText("" + String.format("%.2f", rnd_off));
        edtxt_bottom_Net_Payable_Amount.setText("" + String.format("%.2f", ttl + rnd_off));
    }

    public static void setNetPaybleValue() {
        try {
            txt_bottom_totalsbv.setText("  " + calculateSelectedProductTotalSBV());

            txt_bottom_Total_IGST_Tax_Amount.setText("\u20B9 " + String.format("%.2f", Double.parseDouble(calculateSelectedTotalIGSTAmt())));
            txt_bottom_Total_CGST_Tax_Amount.setText("\u20B9 " + String.format("%.2f", Double.parseDouble(calculateSelectedTotalCGSTAmt())));
            txt_bottom_Total_SGST_Tax_Amount.setText("\u20B9 " + String.format("%.2f", Double.parseDouble(calculateSelectedTotalSGSTAmt())));
            txt_bottom_grossamount.setText("\u20B9 " + String.format("%.2f", Double.parseDouble(calculateSelectedProductTotalGrossAmountWithTax())));
            txt_bottom_roundoff.setText("\u20B9 " + String.format("%.2f", Double.parseDouble(calculateSelectedProductTotalRoundoff())));
            edtxt_bottom_Round_Off.setText("\u20B9 " + String.format("%.2f", Double.parseDouble(calculateSelectedProductTotalRoundoff())));


            d_order_amount = Double.parseDouble(calculateSelectedProductNetPayable());

            edtxt_bottom_TDR.setText("\u20B9 " + calculateSelectedProductTDRAmount());
            d_tdr = Double.parseDouble(calculateSelectedProductTDRAmount());

            edtxt_bottom_Shipping.setText("\u20B9 " + "00.00");
            d_shipping = 00.00;

            /*if (Double.parseDouble(calculateSelectedProductNetPayable()) < 1000) {
                edtxt_bottom_Shipping.setText("\u20B9 " + "50.00");
                d_shipping = 50.00;
            } else {
                edtxt_bottom_Shipping.setText("\u20B9 " + "0.00");
                d_shipping = 0.00;
            }*/
            txt_bottom_netpayable.setText("\u20B9 " + String.format("%.2f", Double.parseDouble(calculateSelectedProductNetPayable()) + d_shipping));

            //--------------------------------
            edtxt_bottom_Total_Amount.setText("\u20B9 " + String.format("%.2f", Double.parseDouble(calculateSelectedProductTotalGrossAmountWithTax())));
            edtxt_bottom_Order_Amount.setText("\u20B9 " + String.format("%.2f", Double.parseDouble(calculateSelectedProductTotalGrossAmountWithTax())));
            static_TTl_Amt_without_TDR = edtxt_bottom_Order_Amount.getText().toString();
            //   edtxt_bottom_Total_Amount.setText("" + String.format("%.2f", d_order_amount + d_tdr));
            Double ttl = d_order_amount + d_tdr;
            Double rnd_off = Double.parseDouble(calculateSelectedFinalTotalRoundoff(ttl));
            final_total_dp = d_order_amount + d_tdr + rnd_off;
            //  edtxt_bottom_Round_Off.setText("" + String.format("%.2f", rnd_off));
            edtxt_bottom_Net_Payable_Amount.setText("" + String.format("%.2f", ttl + rnd_off + d_shipping));
            // edtxt_bottom_TDR,edtxt_bottom_Shipping,edtxt_bottom_Total_Amount,edtxt_bottom_Round_Off,edtxt_bottom_Net_Payable_Amount
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ValidateData() {
        boolean cancel = false;
        View focusView = null;

/*        if (!MinimumActivationBV.equalsIgnoreCase("")) {
            if (Double.parseDouble(MinimumActivationBV) >= Double.parseDouble(calculateSelectedProductTotalSBV())) {
                AppUtils.alertDialog(PlaceOrderActivity.this, "Can't order place below " + MinimumActivationBV);
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
        } else if (paymode.equalsIgnoreCase("DD")) {
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
            JSONObject jsonObjectOnlineRequest = new JSONObject();
            if (paymode.equalsIgnoreCase("OP")) {
                jsonObjectOnlineRequest.put("API_KEY", "" + SampleAppConstants.PG_API_KEY);
                jsonObjectOnlineRequest.put("AMOUNT", "" + calculateSelectedProductNetPayable().trim().replace(",", ""));
                if (!AppController.getSpUserInfo().getString(SPUtils.USER_EMAIL, "").equalsIgnoreCase(""))
                    jsonObjectOnlineRequest.put("EMAIL", "" + AppController.getSpUserInfo().getString(SPUtils.USER_EMAIL, ""));
                else
                    jsonObjectOnlineRequest.put("EMAIL", "" + SampleAppConstants.PG_EMAIL);
                jsonObjectOnlineRequest.put("NAME", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
                jsonObjectOnlineRequest.put("PHONE", "" + AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, ""));
                jsonObjectOnlineRequest.put("ORDER_ID", "");
                jsonObjectOnlineRequest.put("CURRENCY", "" + SampleAppConstants.PG_CURRENCY);
                //  jsonObjectOnlineRequest.put("DESCRIPTION", getResources().getString(R.string.online_remarks));
                jsonObjectOnlineRequest.put("DESCRIPTION", str_remarks);
                if (!str_city.equalsIgnoreCase(""))
                    jsonObjectOnlineRequest.put("CITY", "" + str_city);
                else
                    jsonObjectOnlineRequest.put("CITY", "" + SampleAppConstants.PG_CITY);
                if (!statecode.equalsIgnoreCase(""))
                    jsonObjectOnlineRequest.put("STATE", "" + statecode);
                else
                    jsonObjectOnlineRequest.put("STATE", "" + SampleAppConstants.PG_STATE);
                if (!statecode.equalsIgnoreCase(""))
                    jsonObjectOnlineRequest.put("ADD_1", "" + str_address);
                else
                    jsonObjectOnlineRequest.put("ADD_1", "" + SampleAppConstants.PG_ADD_1);
                jsonObjectOnlineRequest.put("ADD_2", "");
                jsonObjectOnlineRequest.put("ZIPCODE", "" + str_pincode);
                jsonObjectOnlineRequest.put("COUNTRY", "" + SampleAppConstants.PG_COUNTRY);
                jsonObjectOnlineRequest.put("RETURN_URL", "" + SampleAppConstants.PG_RETURN_URL);
                jsonObjectOnlineRequest.put("MODE", "" + SampleAppConstants.PG_MODE);
                jsonObjectOnlineRequest.put("UDF1", "" + SampleAppConstants.PG_UDF1);
                jsonObjectOnlineRequest.put("UDF2", "" + SampleAppConstants.PG_UDF2);
                jsonObjectOnlineRequest.put("UDF3", "" + SampleAppConstants.PG_UDF3);
                jsonObjectOnlineRequest.put("UDF4", "" + SampleAppConstants.PG_UDF4);
                jsonObjectOnlineRequest.put("UDF5", "" + SampleAppConstants.PG_UDF5);
            }

            JSONArray jsonArrayOrder = new JSONArray();
            JSONArray jsonArrayOrderDetail = new JSONArray();
            /*IDNo,MemFirstName,MemLastName,StateID,UserType,TotalDP,Item,TotalQty,HostIp,ChDDNo,ChDate,Remarks,City,PinCode,MobileNo,Email,FormNo,TotalBV*/
            JSONObject jsonObjectOrder = new JSONObject();
            //  jsonObjectOrder.put("MemFirstName", productList_Data.get(0));
            jsonObjectOrder.put("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "").trim().replace(",", " "));
            jsonObjectOrder.put("MemFirstName", AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
            jsonObjectOrder.put("MemLastName", AppController.getSpUserInfo().getString(SPUtils.USER_LAST_NAME, "").trim());
            jsonObjectOrder.put("Address1", str_address.trim());
            jsonObjectOrder.put("Address2", "");
            jsonObjectOrder.put("StateID", statecode.trim().replaceAll(",", " "));
            String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
            if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
                jsonObjectOrder.put("UserType", "D");
                //    jsonObjectOrder.put("IDType", "D");
            } else {
                jsonObjectOrder.put("UserType", "N");
                //  jsonObjectOrder.put("IDType", "G");
            }
            jsonObjectOrder.put("TotalDP", calculateSelectedProductNetPayable().trim().replace(",", " "));
            jsonObjectOrder.put("Item", "" + productList_Data.size());
            jsonObjectOrder.put("TotalQty", calculateSelectedProductTotalQty().trim().replace(",", " "));
            jsonObjectOrder.put("HostIp", AppUtils.getDeviceID(act).trim().replace(",", " "));
            jsonObjectOrder.put("ChDDNo", "0");
            jsonObjectOrder.put("ChDate", "");
            jsonObjectOrder.put("Remarks", "" + str_remarks);
            jsonObjectOrder.put("City", str_city.trim().replace(",", " "));
            jsonObjectOrder.put("PinCode", str_pincode.trim().replace(",", " "));
            jsonObjectOrder.put("MobileNo", AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, "").trim().replace(",", " "));
            jsonObjectOrder.put("Email", AppController.getSpUserInfo().getString(SPUtils.USER_EMAIL, "").trim().replace(",", " "));
            jsonObjectOrder.put("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "").trim().replace(",", " "));
            jsonObjectOrder.put("TotalBV", calculateSelectedProductTotalSBV().trim().replace(",", " "));

            /*
            jsonObjectOrder.put("StateName", statename.trim().replaceAll(",", " "));
            jsonObjectOrder.put("District", str_district.trim().replaceAll(",", " "));
            jsonObjectOrder.put("PayMode", "" + paymode);
            jsonObjectOrder.put("BankName", "");
            jsonObjectOrder.put("BranchName", "");
            jsonObjectOrder.put("ShippingCharge", "");
            jsonObjectOrder.put("color", "");
            jsonObjectOrder.put("Size", "");
            jsonObjectOrder.put("Pack", "0");
            jsonObjectOrder.put("Packing", "");
            jsonObjectOrder.put("OrderFor", "");
            jsonObjectOrder.put("ShipCharge", "");
            jsonObjectOrder.put("AmountBeforeTDR", "" + calculateSelectedProductTotalGrossAmount().trim().replaceAll(",", " "));
            jsonObjectOrder.put("TDRAmount", "" + calculateSelectedProductTotalGrossAmount().trim().replaceAll(",", " "));
            jsonObjectOrder.put("RndOff", "" + calculateSelectedProductTotalRoundoff().trim().replaceAll("-", ""));*/

            jsonArrayOrder.put(jsonObjectOrder);

            for (int j = 0; j < productList_Data.size(); j++) {
                JSONObject jsonObjectDetail = new JSONObject();
                if (!productList_Data.get(j).getQty().trim().replace(",", "0").replace("0.0", "0").equalsIgnoreCase("0")) {
/*Productid,ProductName,Description,Qty,DP,Price,Rate,IGSTAmt,Barcode,HSNCode,UOM,TaxType,BatchNo,Packing,ImageUrl,BV,IsKit,ProdType,DiscountPer,
Discount,InvRate,PV,RP,Package,CGSTPer,CGSTAmt,IGSTPer,IGSTAmt,Taxper,TaxAmt,Total,TotalAmt*/
                    jsonObjectDetail.put("Productid", productList_Data.get(j).getProdId().trim().replace(",", " "));
                    jsonObjectDetail.put("ProductName", productList_Data.get(j).getProductName().trim().replace(",", " "));
                    jsonObjectDetail.put("Description", "");
                    jsonObjectDetail.put("Qty", productList_Data.get(j).getQty().trim().replace(",", " "));
                    jsonObjectDetail.put("DP", productList_Data.get(j).getDP().trim().replace(",", " "));
                    jsonObjectDetail.put("Price", productList_Data.get(j).getMRP().trim().replace(",", " "));
                    jsonObjectDetail.put("Rate", "" + productList_Data.get(j).getRate().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("Barcode", "" + productList_Data.get(j).getBarcode().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("HSNCode", "" + productList_Data.get(j).getHSNCode().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("UOM", "" + productList_Data.get(j).getUOM().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("TaxType", "" + productList_Data.get(j).getTaxType().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("BatchNo", "" + productList_Data.get(j).getBatchNo().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("Packing", "0");
                    jsonObjectDetail.put("ImageUrl", "");
                    jsonObjectDetail.put("BV", "" + productList_Data.get(j).getBV().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("IsKit", "" + productList_Data.get(j).getIsKit().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("ProdType", "" + productList_Data.get(j).getProdType().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("DiscountPer", "" + productList_Data.get(j).getDiscPer().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("Discount", "" + productList_Data.get(j).getDiscPer().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("InvRate", "" + productList_Data.get(j).getInvRate().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("PV", "0");
                    jsonObjectDetail.put("RP", "0");
                    jsonObjectDetail.put("CGSTPer", "" + productList_Data.get(j).getCGSTPer().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("CGSTAmt", "" + productList_Data.get(j).getCGSTAmt().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("IGSTPer", "" + productList_Data.get(j).getIGSTPer().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("IGSTAmt", "" + productList_Data.get(j).getIGSTAmt().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("Taxper", "" + productList_Data.get(j).getTaxPer().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("TaxAmt", "" + productList_Data.get(j).getTaxAmt().trim().replace(",", " ").replace("\\", ""));
                    double SubTotal = 0.0d;
                    SubTotal = ((Double.parseDouble(productList_Data.get(j).getDP())) * (Double.parseDouble(productList_Data.get(j).getQty())));
                    jsonObjectDetail.put("TotalAmt", "" + (SubTotal));

                    /*jsonObjectDetail.put("colorDetails", "");
                    jsonObjectDetail.put("SizeDetails", "");
                    jsonObjectDetail.put("PackDetails", "0");
                    jsonObjectDetail.put("PackingDetails", "");
                    jsonObjectDetail.put("OrderForDetails", "");
                    jsonObjectDetail.put("ShipChargeDetails", "");
                    jsonObjectDetail.put("UID", "");
                    jsonObjectDetail.put("lblDisc", "" + productList_Data.get(j).getDiscAmt().trim().replace(",", " ").replace("\\", ""));
                    jsonObjectDetail.put("Rate", "" + productList_Data.get(j).getRate().trim().replace(",", " ").replace("\\", ""));*/

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
            } else if (paymode.equalsIgnoreCase("DD")) {
                postParameters.add(new BasicNameValuePair("AccountNo", "" + edtxt_dd_number.getText().toString()));
                postParameters.add(new BasicNameValuePair("BankName", "" + edtxt_dd_payee_bank.getText().toString()));
                postParameters.add(new BasicNameValuePair("BranchName", "" + edtxt_dd_payee_bank_branch.getText().toString()));
                postParameters.add(new BasicNameValuePair("DepositDate", "" + AppUtils.getFormatDate1(edtxt_dd_date.getText().toString())));
                postParameters.add(new BasicNameValuePair("TransfereeName", ""));
            } else if (paymode.equalsIgnoreCase("W")) {
                postParameters.add(new BasicNameValuePair("AccountNo", "0"));
                postParameters.add(new BasicNameValuePair("BankName", ""));
                postParameters.add(new BasicNameValuePair("BranchName", ""));
                postParameters.add(new BasicNameValuePair("DepositDate", "" + AppUtils.getFormatDate1(edtxt_deposit_date.getText().toString())));
                postParameters.add(new BasicNameValuePair("TransfereeName", ""));
            } else if (paymode.equalsIgnoreCase("OP")) {
                postParameters.add(new BasicNameValuePair("AccountNo", "0"));
                postParameters.add(new BasicNameValuePair("BankName", ""));
                postParameters.add(new BasicNameValuePair("BranchName", ""));
                postParameters.add(new BasicNameValuePair("DepositDate", "" + AppUtils.getFormatDate1(edtxt_deposit_date.getText().toString())));
                postParameters.add(new BasicNameValuePair("TransfereeName", ""));
            }

            //F for First Purchase and R for Re-Purchase
            if (AppController.getSpUserInfo().getString(SPUtils.USER_ACTIVE_STATUS, "").equalsIgnoreCase("N")) {
                postParameters.add(new BasicNameValuePair("OrderType", "F"));
            } else {
                postParameters.add(new BasicNameValuePair("OrderType", "R"));
            }
            postParameters.add(new BasicNameValuePair("ImageByteCode", "" + AppUtils.getBase64StringFromBitmap(bitmap_api)));
            postParameters.add(new BasicNameValuePair("Type", "PNG"));
            postParameters.add(new BasicNameValuePair("ActiveStatus", "" + AppController.getSpUserInfo().getString(SPUtils.USER_ACTIVE_STATUS, "")));
            postParameters.add(new BasicNameValuePair("WRBranch", "" + PartyCode));
            postParameters.add(new BasicNameValuePair("FullRequest", "" + jsonObjectOnlineRequest.toString()));

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
                                /*  startActivity(new Intent(PlaceOrderActivity.this, ThanksScreen_Activity.class).putExtra("ORDERNUMBER", jsonObjectMainOrder.getString("OrderNo")));*/
                                if (paymode.equalsIgnoreCase("OP")) {
                                    OnlinePaymentRedirection(jsonObjectMainOrder.getString("OrderNo"));
                                } else {
                                    AppUtils.alertDialogWithFinishDashboard(act, "" +
                                            jsonObject.getString("Message") + " : " + jsonObjectMainOrder.getString("OrderNo"));
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
                //   boolean result = Utility.checkPermission(PlaceOrderActivity.this);

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

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }*/

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

    private void executeWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(act,
                                    postParameters, QueryUtils.methodToGetWalletBalance, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(act);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {

                                if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                    if (jsonArrayData.length() > 0) {
                                        for (int i = 0; i < jsonArrayData.length(); i++) {
                                            if (jsonArrayData.getJSONObject(i).getString("WType").equalsIgnoreCase("W")) {
                                                awb = Double.parseDouble(jsonArrayData.getJSONObject(i).getString("WBalance"));
                                                txt_available_wb.setText("Available Wallet Balance : ₹ " + awb);
                                                txt_available_wb.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }

                                }
                            } else {
                                awb = 0.0;
                                txt_available_wb.setText("Available Wallet Balance : ₹ " + awb);
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
            AppUtils.showExceptionDialog(act);
        }
    }

    /*Online Payment Gateway*/
    private void startPlaceOrderOnlineBeforeRequest() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {

                List<NameValuePair> postParameters = new ArrayList<>();

                JSONObject jsonObjectOnlineRequest = new JSONObject();
                jsonObjectOnlineRequest.put("API_KEY", "" + SampleAppConstants.PG_API_KEY);
                jsonObjectOnlineRequest.put("AMOUNT", "" + calculateSelectedProductNetPayable().trim().replace(",", ""));
                if (!AppController.getSpUserInfo().getString(SPUtils.USER_EMAIL, "").equalsIgnoreCase(""))
                    jsonObjectOnlineRequest.put("EMAIL", "" + AppController.getSpUserInfo().getString(SPUtils.USER_EMAIL, ""));
                else
                    jsonObjectOnlineRequest.put("EMAIL", "" + SampleAppConstants.PG_EMAIL);
                jsonObjectOnlineRequest.put("NAME", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
                jsonObjectOnlineRequest.put("PHONE", "" + AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, ""));
                jsonObjectOnlineRequest.put("ORDER_ID", "");
                jsonObjectOnlineRequest.put("CURRENCY", "" + SampleAppConstants.PG_CURRENCY);
                //  jsonObjectOnlineRequest.put("DESCRIPTION", getResources().getString(R.string.online_remarks));
                jsonObjectOnlineRequest.put("DESCRIPTION", str_remarks);
                if (!str_city.equalsIgnoreCase(""))
                    jsonObjectOnlineRequest.put("CITY", "" + str_city);
                else
                    jsonObjectOnlineRequest.put("CITY", "" + SampleAppConstants.PG_CITY);
                if (!statecode.equalsIgnoreCase(""))
                    jsonObjectOnlineRequest.put("STATE", "" + statecode);
                else
                    jsonObjectOnlineRequest.put("STATE", "" + SampleAppConstants.PG_STATE);
                if (!statecode.equalsIgnoreCase(""))
                    jsonObjectOnlineRequest.put("ADD_1", "" + str_address);
                else
                    jsonObjectOnlineRequest.put("ADD_1", "" + SampleAppConstants.PG_ADD_1);
                jsonObjectOnlineRequest.put("ADD_2", "");
                jsonObjectOnlineRequest.put("ZIPCODE", "" + str_pincode);
                jsonObjectOnlineRequest.put("COUNTRY", "" + SampleAppConstants.PG_COUNTRY);
                jsonObjectOnlineRequest.put("RETURN_URL", "" + SampleAppConstants.PG_RETURN_URL);
                jsonObjectOnlineRequest.put("MODE", "" + SampleAppConstants.PG_MODE);
                jsonObjectOnlineRequest.put("UDF1", "" + SampleAppConstants.PG_UDF1);
                jsonObjectOnlineRequest.put("UDF2", "" + SampleAppConstants.PG_UDF2);
                jsonObjectOnlineRequest.put("UDF3", "" + SampleAppConstants.PG_UDF3);
                jsonObjectOnlineRequest.put("UDF4", "" + SampleAppConstants.PG_UDF4);
                jsonObjectOnlineRequest.put("UDF5", "" + SampleAppConstants.PG_UDF5);

                JSONArray jsonArrayOrder = new JSONArray();
                JSONArray jsonArrayOrderDetail = new JSONArray();

                JSONObject jsonObjectOrder = new JSONObject();
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
                jsonObjectOrder.put("PayMode", "" + paymode);
                jsonObjectOrder.put("Remarks", "");
                jsonObjectOrder.put("ChDDNo", "0");
                jsonObjectOrder.put("ChDate", "");
                jsonObjectOrder.put("BankName", "");
                jsonObjectOrder.put("BranchName", "");
                jsonObjectOrder.put("TotalDP", String.valueOf(final_total_dp).trim().replace(",", " "));
                jsonObjectOrder.put("Item", "" + productList_Data.size());
                jsonObjectOrder.put("TotalQty", calculateSelectedProductTotalQty().trim().replace(",", " "));
                jsonObjectOrder.put("HostIp", "" + AppUtils.getDeviceID(act));
                jsonObjectOrder.put("ShippingCharge", "0");
                jsonObjectOrder.put("color", "");
                jsonObjectOrder.put("Size", "");
                jsonObjectOrder.put("Pack", "0");
                jsonObjectOrder.put("Packing", "");
                jsonObjectOrder.put("OrderFor", "");
                jsonObjectOrder.put("City", str_city.trim().replace(",", " "));
                if (!str_pincode.equalsIgnoreCase("")) {
                    jsonObjectOrder.put("PinCode", str_pincode.trim().replace(",", " "));
                } else {
                    jsonObjectOrder.put("PinCode", "0");
                }
                jsonObjectOrder.put("MobileNo", AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, "").trim().replace(",", " "));
                jsonObjectOrder.put("Email", AppController.getSpUserInfo().getString(SPUtils.USER_EMAIL, "").trim().replace(",", " "));
                jsonObjectOrder.put("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "").trim().replace(",", " "));
                jsonObjectOrder.put("TotalBV", calculateSelectedProductTotalSBV().trim().replace(",", " "));
                jsonObjectOrder.put("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "").trim().replace(",", " "));
                jsonObjectOrder.put("ShipCharge", "");
                // jsonObjectOrder.put("AmountBeforeTDR", "" + calculateSelectedProductTotalGrossAmount().trim().replaceAll(",", " "));
                //jsonObjectOrder.put("TDRAmount", "" + calculateSelectedProductTotalGrossAmount().trim().replaceAll(",", " "));
                jsonObjectOrder.put("RndOff", "" + calculateSelectedProductTotalRoundoff().trim().replaceAll("-", ""));
                jsonObjectOrder.put("AmountBeforeTDR", "" + static_TTl_Amt_without_TDR.trim().replaceAll(",", " "));
                jsonObjectOrder.put("TDRAmount", "" + static_TDR.trim().replaceAll(",", " "));

                jsonArrayOrder.put(jsonObjectOrder);

                for (int j = 0; j < productList_Data.size(); j++) {
                    JSONObject jsonObjectDetail = new JSONObject();
                    if (!productList_Data.get(j).getQty().trim().replace(",", " ").equalsIgnoreCase("0")) {
                        jsonObjectDetail.put("CGSTAmt", productList_Data.get(j).getCGSTAmt().trim().replace(",", " "));
                        jsonObjectDetail.put("IGSTAmt", productList_Data.get(j).getIGSTAmt().trim().replace(",", " "));
                        jsonObjectDetail.put("TaxAmt", productList_Data.get(j).getSGST_TaxAmt().trim().replace(",", " "));
                        jsonObjectDetail.put("Rate", productList_Data.get(j).getRate().trim().replace(",", " "));

                        jsonObjectDetail.put("ProdId", productList_Data.get(j).getProdId().trim().replace(",", " "));
                        jsonObjectDetail.put("Productid", productList_Data.get(j).getProdId().trim().replace(",", " "));
                        jsonObjectDetail.put("ProductName", productList_Data.get(j).getProductName().trim().replace(",", " "));
                        jsonObjectDetail.put("BatchNo", productList_Data.get(j).getBatchNo().trim().replace(",", " "));
                        jsonObjectDetail.put("Qty", productList_Data.get(j).getQty().trim().replace(",", " "));
                        jsonObjectDetail.put("DP", productList_Data.get(j).getDP().trim().replace(",", " "));
                        jsonObjectDetail.put("Price", productList_Data.get(j).getMRP().trim().replace(",", " "));

                        double SubTotal = 0.0d;
                        SubTotal = ((Double.parseDouble(productList_Data.get(j).getDP())) * (Double.parseDouble(productList_Data.get(j).getQty())));

                        jsonObjectDetail.put("SubTotal", "" + (SubTotal));
                        jsonObjectDetail.put("colorDetails", "");
                        jsonObjectDetail.put("SizeDetails", "");
                        jsonObjectDetail.put("PackDetails", "0");
                        jsonObjectDetail.put("PackingDetails", "");
                        jsonObjectDetail.put("OrderForDetails", "");
                        jsonObjectDetail.put("ShipChargeDetails", "0");
                        jsonObjectDetail.put("ImageUrl", "");
                        jsonObjectDetail.put("BV", "" + productList_Data.get(j).getBV().trim().replace(",", " ").replace("\\", ""));
                        jsonObjectDetail.put("UID", "");
                        jsonObjectDetail.put("IsKit", "" + productList_Data.get(j).getIsKit().trim().replace(",", " ").replace("\\", ""));
                        jsonObjectDetail.put("ProdType", "" + productList_Data.get(j).getProdType().trim().replace(",", " ").replace("\\", ""));
                        jsonObjectDetail.put("DiscountPer", "" + productList_Data.get(j).getDiscPer().trim().replace(",", " ").replace("\\", ""));

                        /*13-03-2020 04:49 PM New paramerters added by mukesh (according to deepesh sir for POS testing sheet issue) // SGST,SGSTAmount,CGST,CGSTAmount,IGST,IGSTAmount,BatchNo,Barcode,HSNCode,UOM,TaxType,InvRate*/
                        //SGST,SGSTAmount,CGST,CGSTAmount,IGST,IGSTAmount,BatchNo,Barcode,HSNCode,UOM,TaxType,InvRate
                        jsonObjectDetail.put("SGST", "" + productList_Data.get(j).getSGST_TaxPer().trim().replace(",", " ").replace("\\", ""));
                        jsonObjectDetail.put("SGSTAmount", "" + productList_Data.get(j).getSGST_TaxAmt().trim().replace(",", " ").replace("\\", ""));
                        jsonObjectDetail.put("CGST", "" + productList_Data.get(j).getCGSTPer().trim().replace(",", " ").replace("\\", ""));
                        jsonObjectDetail.put("CGSTAmount", "" + productList_Data.get(j).getCGSTAmt().trim().replace(",", " ").replace("\\", ""));
                        jsonObjectDetail.put("IGST", "" + productList_Data.get(j).getIGSTPer().trim().replace(",", " ").replace("\\", ""));
                        jsonObjectDetail.put("IGSTAmount", "" + productList_Data.get(j).getIGSTAmt().trim().replace(",", " ").replace("\\", ""));
                        jsonObjectDetail.put("BatchNo", "" + productList_Data.get(j).getBatchNo().trim().replace(",", " ").replace("\\", ""));
                        jsonObjectDetail.put("Barcode", "" + productList_Data.get(j).getBarcode().trim().replace(",", " ").replace("\\", ""));
                        jsonObjectDetail.put("HSNCode", "" + productList_Data.get(j).getHSNCode().trim().replace(",", " ").replace("\\", ""));
                        jsonObjectDetail.put("UOM", "" + productList_Data.get(j).getUOM().trim().replace(",", " ").replace("\\", ""));
                        jsonObjectDetail.put("TaxType", "" + productList_Data.get(j).getTaxType().trim().replace(",", " ").replace("\\", ""));
                        jsonObjectDetail.put("InvRate", "" + productList_Data.get(j).getInvRate().trim().replace(",", " ").replace("\\", ""));

                        jsonArrayOrderDetail.put(jsonObjectDetail);
                    }
                }
                /*Order:OrderDetails:PayMode:AccountNo:BankName:BranchName:DepositDate:TransfereeName:OrderType:Type:
                ImageByteCode:ActiveStatus:Request:Device:*/
                postParameters.add(new BasicNameValuePair("Order", jsonArrayOrder.toString()));
                postParameters.add(new BasicNameValuePair("OrderDetails", "" + jsonArrayOrderDetail.toString()));
                postParameters.add(new BasicNameValuePair("PayMode", "" + paymode));
                postParameters.add(new BasicNameValuePair("AccountNo", ""));
                postParameters.add(new BasicNameValuePair("BankName", ""));
                postParameters.add(new BasicNameValuePair("BranchName", "" + PartyCode));
                postParameters.add(new BasicNameValuePair("DepositDate", ""));
                postParameters.add(new BasicNameValuePair("TransfereeName", ""));
                postParameters.add(new BasicNameValuePair("ImageByteCode", "" + AppUtils.getBase64StringFromBitmap(bitmap_api)));
                postParameters.add(new BasicNameValuePair("Type", "PNG"));

                //F for First Purchase and R for Re-Purchase
                if (AppController.getSpUserInfo().getString(SPUtils.USER_ACTIVE_STATUS, "").equalsIgnoreCase("N")) {
                    postParameters.add(new BasicNameValuePair("OrderType", "F"));
                    postParameters.add(new BasicNameValuePair("ActiveStatus", "N"));
                } else {
                    postParameters.add(new BasicNameValuePair("OrderType", "R"));
                    postParameters.add(new BasicNameValuePair("ActiveStatus", "Y"));
                }
                postParameters.add(new BasicNameValuePair("Request", "" + jsonObjectOnlineRequest.toString()));
                postParameters.add(new BasicNameValuePair("Device", "" + AppUtils.getDeviceID(act)));

                executePlaceOrderOnlineBeforeRequest(postParameters);

            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void executePlaceOrderOnlineBeforeRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        AppUtils.showProgressDialog(act);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodAddOrder_INMemberPanelOnline, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            // resultData = "{\"Status\":\"True\",\"Message\":\"Your order has been successfully placed.!\",\"MainOrder\":[{\"ID\":399,\"OrderNo\":79656211,\"OrderDate\":\"\\/Date(1583260200000)\\/\",\"MemFirstName\":\"Shree App\",\"MemLastName\":\"\",\"Address1\":\"Gandhi Nagar\",\"Address2\":\"\",\"CountryID\":1,\"CountryName\":\"India\",\"StateCode\":21,\"District\":\"\",\"City\":\"Bhilwara\",\"PinCode\":0,\"Mobl\":9672433276,\"EMail\":\"\",\"FormNo\":95787445,\"UserType\":\"D\",\"Passw\":\"\",\"PayMode\":\"OP\",\"ChDDNo\":0,\"ChDate\":\"\\/Date(1583299242100)\\/\",\"ChAmt\":2222.00,\"BankName\":\"\",\"BranchName\":\"WR\",\"Remark\":\"\",\"GroupNo\":0,\"OrderType\":\"F\",\"OrderAmt\":2221.56,\"OrderItem\":29,\"OrderQty\":6,\"OrderQvp\":0.00,\"OrderCvp\":2080.00,\"OrderByFormNo\":95787445,\"ActiveStatus\":\"Y\",\"HostIp\":\"000000000000000\",\"RecTimeStamp\":\"\\/Date(1583299242100)\\/\",\"IsTransfer\":\"N\",\"DispatchDate\":null,\"DispatchStatus\":\"N\",\"DispatchQty\":0,\"RemainQty\":0,\"DispatchAmount\":0.00,\"Shipping\":0.00,\"SessID\":0,\"RcptImage\":[],\"StatusCode\":null,\"IDType\":\"D\",\"IDNo\":\"ME95787445\",\"IsConfirm\":\"N\",\"ConfDate\":null,\"ConfUserId\":0,\"OrderFor\":\"WR\",\"DepositAcNm\":null,\"DepositAcNo\":null,\"DepositBank\":null,\"DepositBranch\":null,\"DepositIfsc\":null,\"MSessID\":0,\"RefFormNo\":0,\"DiscPer\":0.00,\"Discount\":0.00,\"OldShipping\":0.00,\"ShippingStatus\":\"Y\",\"PromoID\":0,\"VoucherAmt\":0.00,\"IsVoucher\":\"N\",\"ActQvp\":0.00,\"Color\":\"NA\",\"Size\":\"NA\",\"Packing\":\"0\",\"PackCharge\":0.00,\"DocStatus\":\"P\",\"DocRemarks\":\"\",\"OrderThru\":\"A\",\"GrossOrderAmt\":2221.56,\"EntryType\":\"F\",\"RndOff\":0.44,\"TDRAmt\":43.56,\"TotalCGSTAmt\":0.00,\"TotalSGSTAmt\":0.00,\"TotalIGSTAmt\":204.74,\"TotalAmt\":1973.26,\"StateName\":\"RAJASTHAN\"}],\"OrderDetails\":[{\"OrderNo\":79656211,\"OrderDate\":\"04 Mar 2020\",\"ProductName\":\"MOLT T SHIRT (38)..\",\"productcode\":1019,\"Price\":1.00,\"ImageUrl\":\"\",\"Quantity\":1,\"NetAmount\":300.00,\"OrderDesc\":\"\",\"Bv\":100.00,\"Color\":\"NA\",\"Size\":\"NA\",\"Packing\":\"0\",\"CVP\":100.00,\"totalAmt\":300.00},{\"OrderNo\":79656211,\"OrderDate\":\"04 Mar 2020\",\"ProductName\":\"MOLT AGRO BOOSTER..\",\"productcode\":1015,\"Price\":1.00,\"ImageUrl\":\"\",\"Quantity\":1,\"NetAmount\":1099.00,\"OrderDesc\":\"\",\"Bv\":550.00,\"Color\":\"NA\",\"Size\":\"NA\",\"Packing\":\"0\",\"CVP\":550.00,\"totalAmt\":1099.00},{\"OrderNo\":79656211,\"OrderDate\":\"04 Mar 2020\",\"ProductName\":\"MOLT KING OF LAND..\",\"productcode\":1016,\"Price\":1.00,\"ImageUrl\":\"\",\"Quantity\":1,\"NetAmount\":779.00,\"OrderDesc\":\"\",\"Bv\":390.00,\"Color\":\"NA\",\"Size\":\"NA\",\"Packing\":\"0\",\"CVP\":390.00,\"totalAmt\":779.00}]}" ;
                            AppUtils.dismissProgressDialog();

                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayMainOrder = jsonObject.getJSONArray("MainOrder");
                                JSONObject jsonObjectMainOrder = jsonArrayMainOrder.getJSONObject(0);
                                OnlinePaymentRedirection(jsonObjectMainOrder.getString("OrderNo"));
                            } else
                                AppUtils.alertDialog(act, jsonObject.getString("Message"));

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

    private void OnlinePaymentRedirection(String orderno) {
        try {
            PaymentParams pgPaymentParams = new PaymentParams();
            pgPaymentParams.setAPiKey(SampleAppConstants.PG_API_KEY);
            //pgPaymentParams.setAmount(calculateSelectedProductNetPayable().trim().replace(",", ""));
            pgPaymentParams.setAmount(String.valueOf(final_total_dp).trim().replace(",", ""));
            if (!AppController.getSpUserInfo().getString(SPUtils.USER_EMAIL, "").equalsIgnoreCase(""))
                pgPaymentParams.setEmail(AppController.getSpUserInfo().getString(SPUtils.USER_EMAIL, ""));
            else
                pgPaymentParams.setEmail(SampleAppConstants.PG_EMAIL);
            pgPaymentParams.setName(AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
            pgPaymentParams.setPhone(AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, "").trim().replace(",", " "));
            pgPaymentParams.setOrderId(orderno);
            pgPaymentParams.setCurrency(SampleAppConstants.PG_CURRENCY);
            pgPaymentParams.setDescription("" + edtxt_remarks.getText().toString());
            if (!AppController.getSpUserInfo().getString(SPUtils.USER_CITY, "").equalsIgnoreCase(""))
                pgPaymentParams.setCity(AppController.getSpUserInfo().getString(SPUtils.USER_CITY, "").trim().replace(",", " "));
            else
                pgPaymentParams.setCity("city");

            pgPaymentParams.setState(AppController.getSpUserInfo().getString(SPUtils.USER_STATE_CODE, "0").trim().replaceAll(",", " "));
            pgPaymentParams.setAddressLine1(AppController.getSpUserInfo().getString(SPUtils.USER_ADDRESS, "address").trim().replaceAll(",", " "));
            pgPaymentParams.setAddressLine2(SampleAppConstants.PG_ADD_2);
            pgPaymentParams.setZipCode(AppController.getSpUserInfo().getString(SPUtils.USER_PINCODE, "0").trim().replace(",", " "));
            pgPaymentParams.setCountry(SampleAppConstants.PG_COUNTRY);
            pgPaymentParams.setReturnUrl(SampleAppConstants.PG_RETURN_URL);
            pgPaymentParams.setMode(SampleAppConstants.PG_MODE);
            pgPaymentParams.setUdf1(SampleAppConstants.PG_UDF1);
            pgPaymentParams.setUdf2(SampleAppConstants.PG_UDF2);
            pgPaymentParams.setUdf3(SampleAppConstants.PG_UDF3);
            pgPaymentParams.setUdf4(SampleAppConstants.PG_UDF4);
            pgPaymentParams.setUdf5(SampleAppConstants.PG_UDF5);

            PaymentGatewayPaymentInitializer pgPaymentInitialzer = new PaymentGatewayPaymentInitializer(pgPaymentParams, act);
            pgPaymentInitialzer.initiatePaymentProcess();

        } catch (Exception e) {
            Log.e("gateway failed : ", e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut

            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            else {
                try {
                    String paymentResponse = data.getStringExtra(PGConstants.PAYMENT_RESPONSE);
                    System.out.println("paymentResponse: " + paymentResponse);
                    if (paymentResponse.equals("null")) {
                        System.out.println("Transaction Error!");

                        AppUtils.alertDialogWithFinishDashboard(act, "Transaction Error!");

                    } else {
                        JSONObject response = new JSONObject(paymentResponse);
                        startAfterPaymentRequest(response);
                        // AppUtils.alertDialog(act, response.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else {
            Log.e("MainActivity", "Failed to payment gateway");
        }
    }

    private void startAfterPaymentRequest(JSONObject onlineresponse) {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();
            /*FormNo:OrderID:TransactionID:ResponseCode:FailureMessage:SendFullResponse:PayMode:CardName:ResponseMessage:Currency:
Amount:	Blling_name:Blling_Address:Billing_City:Billing_State:Billing_Zip:Billing_Country:PhoneNo:Email:Billing_Description*/

            postParameters.add(new BasicNameValuePair("FormNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
            postParameters.add(new BasicNameValuePair("OrderID", "" + onlineresponse.getString("order_id")));
            postParameters.add(new BasicNameValuePair("TransactionID", "" + onlineresponse.getString("transaction_id")));
            postParameters.add(new BasicNameValuePair("ResponseCode", "" + onlineresponse.getString("response_code")));
            if (onlineresponse.getString("response_code").equalsIgnoreCase("0")) {
                postParameters.add(new BasicNameValuePair("FailureMessage", ""));
            } else {
                postParameters.add(new BasicNameValuePair("FailureMessage", "" + onlineresponse.getString("response_message")));
            }
            postParameters.add(new BasicNameValuePair("SendFullResponse", "" + onlineresponse.toString()));
            postParameters.add(new BasicNameValuePair("PayMode", "OP"));
            postParameters.add(new BasicNameValuePair("CardName", "" + onlineresponse.getString("payment_channel")));
            postParameters.add(new BasicNameValuePair("ResponseMessage", "" + onlineresponse.getString("response_message")));
            postParameters.add(new BasicNameValuePair("Currency", "" + onlineresponse.getString("currency")));
            postParameters.add(new BasicNameValuePair("Amount", "" + onlineresponse.getString("amount")));
            postParameters.add(new BasicNameValuePair("Blling_name", "" + onlineresponse.getString("name")));
            postParameters.add(new BasicNameValuePair("Blling_Address", "" + onlineresponse.getString("address_line_1")));
            postParameters.add(new BasicNameValuePair("Billing_City", "" + onlineresponse.getString("city")));
            postParameters.add(new BasicNameValuePair("Billing_State", "" + onlineresponse.getString("state")));
            postParameters.add(new BasicNameValuePair("Billing_Zip", "" + onlineresponse.getString("zip_code")));
            postParameters.add(new BasicNameValuePair("Billing_Country", "" + onlineresponse.getString("country")));
            postParameters.add(new BasicNameValuePair("PhoneNo", "" + onlineresponse.getString("phone")));
            postParameters.add(new BasicNameValuePair("Email", "" + onlineresponse.getString("email")));
            postParameters.add(new BasicNameValuePair("Billing_Description", "" + str_remarks));

            executeToAfterOnlinePaymentRequest(postParameters, onlineresponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToAfterOnlinePaymentRequest(final List<NameValuePair> postParameters, final JSONObject onlineafterresponse) {
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
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodOnlineOrderAfterSucess, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(act);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (onlineafterresponse.getString("response_code").equalsIgnoreCase("0")) {
                                    AppUtils.alertDialogWithFinishDashboard(act, jsonObject.getString("Message")
                                            + " . Your Txn No. is : " + onlineafterresponse.getString("order_id"));
                                } else {
                                    AppUtils.alertDialogWithFinishDashboard(act, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialogWithFinishDashboard(act, jsonObject.getString("Message"));
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

}
