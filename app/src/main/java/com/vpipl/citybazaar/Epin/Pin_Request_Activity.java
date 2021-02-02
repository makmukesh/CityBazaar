package com.vpipl.citybazaar.Epin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.vpipl.citybazaar.Adapters.ExpandableListAdapter;
import com.vpipl.citybazaar.AppController;
import com.vpipl.citybazaar.DashBoard_Activity;
import com.vpipl.citybazaar.Login_New_Activity;
import com.vpipl.citybazaar.R;
import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.CircularImageView;
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
import java.util.Map;

/**
 * Created by Mukesh on 27/12/2019.
 */
public class Pin_Request_Activity extends AppCompatActivity {

    public static DrawerLayout drawer;
    public static NavigationView navigationView;
    String TAG = "Pin_Request_Activity";
    TextInputEditText edtxt_total_pin, edtxt_amount, edtxt_account_no, edtxt_branch_name,
            edtxt_transaction_no, txt_choose_bank, txt_choose_pay_mode;
    Button btn_request;
    String bankArray[];
    String paymodeArray[] = {"Bank", "Cash"};
    String amount, transaction_no, bank_name, breanch_name, account_number, paymode = "Bank";
    TextView txt_welcome_name, txt_available_wb, txt_id_number;
    CircularImageView profileImage;
    List<EditText> allEds = new ArrayList<EditText>();
    List<TextView> allTvs = new ArrayList<TextView>();
    List<String> kitid = new ArrayList<String>();
    List<String> kitname = new ArrayList<String>();
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;

    ImageView img_login_logout, img_nav_back;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);

        final Drawable upArrow;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            upArrow = getDrawable(R.drawable.icon_nav_bar_close);
        } else
            upArrow = getResources().getDrawable(R.drawable.icon_nav_bar_close);
        assert upArrow != null;

        //     txt_wallet_balance.setText("\u20B9 " + str_wallet_balance);

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
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                    startActivity(new Intent(Pin_Request_Activity.this, Login_New_Activity.class));
                } else {
                    AppUtils.showDialogSignOut(Pin_Request_Activity.this);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_request_amount);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {
           /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            AppUtils.setActionbarTitle(getSupportActionBar(), Pin_Request_Activity.this);*/

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");

            SetupToolbar();


            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            txt_choose_bank = (TextInputEditText) findViewById(R.id.txt_choose_bank);
            txt_choose_pay_mode = (TextInputEditText) findViewById(R.id.txt_choose_pay_mode);

            edtxt_total_pin = (TextInputEditText) findViewById(R.id.edtxt_total_pin);
            edtxt_amount = (TextInputEditText) findViewById(R.id.edtxt_amount);
            edtxt_transaction_no = (TextInputEditText) findViewById(R.id.edtxt_transaction_no);
            edtxt_branch_name = (TextInputEditText) findViewById(R.id.edtxt_branch_name);
            edtxt_account_no = (TextInputEditText) findViewById(R.id.edtxt_account_no);


            btn_request = (Button) findViewById(R.id.btn_request);


            txt_choose_bank.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        if (AppController.bankList.size() != 0) {
                            showBankDialog();
                        } else {
                            executeBankRequest();
                        }
                    }
                }
            });

            txt_choose_bank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppController.bankList.size() != 0) {
                        showBankDialog();
                    } else {
                        executeBankRequest();
                    }
                }
            });

            txt_choose_pay_mode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        showpaymodeDialog();
                    }
                }
            });

            txt_choose_pay_mode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showpaymodeDialog();
                }
            });

            txt_choose_pay_mode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().trim().equalsIgnoreCase("Cash")) {
                        showCashLayout();
                    } else {
                        showBankLayout();
                    }
                }
            });

            btn_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(Pin_Request_Activity.this, view);
                    if (paymode.equalsIgnoreCase("Cash"))
                        ValidateDataCash();
                    else
                        ValidateData();
                }
            });

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            View navHeaderView = navigationView.getHeaderView(0);
            txt_welcome_name = (TextView) navHeaderView.findViewById(R.id.txt_welcome_name);
            txt_available_wb = (TextView) navHeaderView.findViewById(R.id.txt_available_wb);
            txt_id_number = (TextView) navHeaderView.findViewById(R.id.txt_id_number);
            profileImage = (CircularImageView) navHeaderView.findViewById(R.id.iv_Profile_Pic);
            LinearLayout ll_van_bottom_ttl_income = navHeaderView.findViewById(R.id.ll_van_bottom_ttl_income);
            ll_van_bottom_ttl_income.setVisibility(View.GONE);

            LinearLayout LL_Nav = (LinearLayout) navHeaderView.findViewById(R.id.LL_Nav);
            expListView = (ExpandableListView) findViewById(R.id.left_drawer);


            listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();


            enableExpandableList();
            LoadNavigationHeaderItems();

            executeLoadPackages();


        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    public void showCashLayout() {
        findViewById(R.id.choose_bank).setVisibility(View.GONE);
        findViewById(R.id.tran_number).setVisibility(View.GONE);

        findViewById(R.id.bank_branch).setVisibility(View.GONE);
        findViewById(R.id.account_number).setVisibility(View.GONE);
    }

    public void showBankLayout() {
        findViewById(R.id.choose_bank).setVisibility(View.VISIBLE);
        findViewById(R.id.tran_number).setVisibility(View.VISIBLE);

        findViewById(R.id.bank_branch).setVisibility(View.VISIBLE);
        findViewById(R.id.account_number).setVisibility(View.VISIBLE);
    }

    private void ValidateDataCash() {
        try {

            amount = edtxt_amount.getText().toString().trim();
            transaction_no = edtxt_transaction_no.getText().toString().trim();

            bank_name = txt_choose_bank.getText().toString().trim();

            account_number = edtxt_account_no.getText().toString().trim();
            breanch_name = edtxt_branch_name.getText().toString().trim();

            float amt = 0;
            try {
                amt = Float.parseFloat(amount);
            } catch (Exception ignored) {

            }
            if (TextUtils.isEmpty(amount)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Amount is Required");
                edtxt_amount.requestFocus();
            } else if (amt <= 0) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Invalid Amount");
                edtxt_amount.requestFocus();
            } else if (amt > 99999) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Maximum Amount Limit is 99999");
                edtxt_amount.requestFocus();
//            } else if (TextUtils.isEmpty(bank_name)) {
//                AppUtils.alertDialog(Pin_Request_Activity.this, "Select Bank");
//                txt_choose_bank.requestFocus();
//            } else if (TextUtils.isEmpty(transaction_no)) {
//                AppUtils.alertDialog(Pin_Request_Activity.this, "Transaction Number is Required");
//                edtxt_transaction_no.requestFocus();
//            } else if (TextUtils.isEmpty(selectedImagePath)) {
//                AppUtils.alertDialog(Pin_Request_Activity.this, "Reference Receipt is Required");
//                btn_choose_file.requestFocus();
            } else if (!AppUtils.isNetworkAvailable(Pin_Request_Activity.this)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, getResources().getString(R.string.txt_networkAlert));
            } else {
                startRequestAmountCash();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void startRequestAmountCash() {
        try {
            if (AppUtils.isNetworkAvailable(Pin_Request_Activity.this)) {

                List<NameValuePair> postParameters = new ArrayList<>();
                postParameters.add(new BasicNameValuePair("DepositAmount", amount));
                postParameters.add(new BasicNameValuePair("PayMode", "Cash"));
                postParameters.add(new BasicNameValuePair("AccountNo", "0"));
                postParameters.add(new BasicNameValuePair("BranchName", "Cash"));
                postParameters.add(new BasicNameValuePair("BankName", "Cash"));
                postParameters.add(new BasicNameValuePair("TransactionNo", "0"));
                postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("IdNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("DeviceID", AppUtils.getDeviceID(Pin_Request_Activity.this)));


                JSONArray jsonArrayOrder = new JSONArray();

                int[] qtyarr = new int[allEds.size()];
                int[] amtarr = new int[allEds.size()];

                for (int i = 0; i < allEds.size(); i++) {
                    if (allEds.get(i).getText().toString().trim().length() > 0)
                        qtyarr[i] = Math.round(Float.parseFloat(allEds.get(i).getText().toString().trim()));
                    else
                        qtyarr[i] = 0;

                    if (allTvs.get(i).getText().toString().trim().length() > 0)
                        amtarr[i] = Math.round(Float.parseFloat(allTvs.get(i).getText().toString().trim()));
                    else
                        amtarr[i] = 0;
                }

                for (int i = 0; i < kitid.size(); i++) {
                    JSONObject jsonObjectDetail = new JSONObject();
                    jsonObjectDetail.put("KitId", "" + kitid.get(i).trim().replace(",", " "));
                    jsonObjectDetail.put("KitName", "" + kitname.get(i).trim().replace(",", " "));
                    jsonObjectDetail.put("Qty", "" + qtyarr[i]);
                    jsonObjectDetail.put("Amount", amtarr[i]);

                    jsonArrayOrder.put(jsonObjectDetail);
                }

                postParameters.add(new BasicNameValuePair("PackageDetails", jsonArrayOrder.toString().trim()));


                executeRequestAmount(postParameters);

            } else {
                AppUtils.alertDialog(Pin_Request_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void enableExpandableList() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        prepareListDataDistributor(listDataHeader, listDataChild);

        ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                String GroupTitle = listDataHeader.get(groupPosition);

                if (GroupTitle.trim().equalsIgnoreCase("Generated/Issued Pin Details")) {
                    startActivity(new Intent(Pin_Request_Activity.this, Pin_Generated_issued_details_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("E-pin Transfer")) {
                    startActivity(new Intent(Pin_Request_Activity.this, Pin_transfer_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("E-pin Transfer Detail")) {
                    startActivity(new Intent(Pin_Request_Activity.this, Pin_Transfer_Report_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("E-pin Received Detail")) {
                    startActivity(new Intent(Pin_Request_Activity.this, Pin_Received_Report_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("E-Pin Request Details")) {
                    startActivity(new Intent(Pin_Request_Activity.this, Pin_Request_Report_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("Topup/E-Pin Detail")) {
                    startActivity(new Intent(Pin_Request_Activity.this, Pin_details_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    startActivity(new Intent(Pin_Request_Activity.this, DashBoard_Activity.class));
                    finish();
                }

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }

                return false;
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }

        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });
    }

    private void prepareListDataDistributor(List<String> listDataHeader, Map<String, List<String>> listDataChild) {

        List<String> Empty = new ArrayList<>();
        try {

            listDataHeader.add("Topup/E-Pin Detail");
            listDataHeader.add("Generated/Issued Pin Details");
            listDataHeader.add("E-pin Transfer");
            listDataHeader.add("E-pin Transfer Detail");
            listDataHeader.add("E-pin Received Detail");
            listDataHeader.add("E-Pin Request Details");
            listDataHeader.add("Logout");

            listDataChild.put(listDataHeader.get(0), Empty);
            listDataChild.put(listDataHeader.get(1), Empty);
            listDataChild.put(listDataHeader.get(2), Empty);
            listDataChild.put(listDataHeader.get(3), Empty);
            listDataChild.put(listDataHeader.get(4), Empty);
            listDataChild.put(listDataHeader.get(5), Empty);
            listDataChild.put(listDataHeader.get(6), Empty);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void LoadNavigationHeaderItems() {
        txt_id_number.setText("");
        txt_id_number.setVisibility(View.GONE);

        txt_available_wb.setText("");
        txt_available_wb.setVisibility(View.GONE);

        txt_welcome_name.setText("Guest");

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            String welcome_text = WordUtils.capitalizeFully(AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
            txt_welcome_name.setText(welcome_text);

            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon_user);
            profileImage.setImageBitmap(largeIcon);

            String userid = AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "");
            txt_id_number.setText(userid);
            txt_id_number.setVisibility(View.VISIBLE);

            String bytecode = AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "");

            if (bytecode.equalsIgnoreCase(""))
                executeGetProfilePicture();
            else
                profileImage.setImageBitmap(AppUtils.getBitmapFromString(bytecode));
        }
    }

    private void executeGetProfilePicture() {
        try {
            if (AppUtils.isNetworkAvailable(Pin_Request_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            //ImageType----AddrProof=AP,IdentityProof=IP,PhotoProof=PP,Signature=S,CancelChq=CC,SpousePic=SP,All=*
                            postParameters.add(new BasicNameValuePair("ImageType", "PP"));

                            response = AppUtils.callWebServiceWithMultiParam(Pin_Request_Activity.this, postParameters, QueryUtils.methodGetImages, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
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
                                if (!jsonArrayData.getJSONObject(0).getString("PhotoProof").equals("")) {

                                    AppController.getSpUserInfo().edit().putString(SPUtils.USER_profile_pic_byte_code, jsonArrayData.getJSONObject(0).getString("PhotoProof")).commit();
                                    profileImage.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void ValidateData() {
        try {

            amount = edtxt_amount.getText().toString().trim();
            transaction_no = edtxt_transaction_no.getText().toString().trim();

            bank_name = txt_choose_bank.getText().toString().trim();

            account_number = edtxt_account_no.getText().toString().trim();
            breanch_name = edtxt_branch_name.getText().toString().trim();

            float amt = 0;
            try {
                amt = Float.parseFloat(amount);
            } catch (Exception ignored) {

            }
            if (TextUtils.isEmpty(amount)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Amount is Required");
                edtxt_amount.requestFocus();
            } else if (amt <= 0) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Invalid Amount");
                edtxt_amount.requestFocus();
            } else if (amt > 99999) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Maximum Amount Limit is 99999");
                edtxt_amount.requestFocus();
            } else if (TextUtils.isEmpty(bank_name)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Select Bank");
                txt_choose_bank.requestFocus();
            } else if (TextUtils.isEmpty(breanch_name)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Branch Name is Required");
                edtxt_branch_name.requestFocus();
            } else if (TextUtils.isEmpty(account_number)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Account Number is Required");
                edtxt_account_no.requestFocus();
            } else if (TextUtils.isEmpty(transaction_no)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, "Transaction Number is Required");
                edtxt_transaction_no.requestFocus();
            } else if (!AppUtils.isNetworkAvailable(Pin_Request_Activity.this)) {
                AppUtils.alertDialog(Pin_Request_Activity.this, getResources().getString(R.string.txt_networkAlert));
            } else {
                startRequestAmount();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void startRequestAmount() {
        try {
            if (AppUtils.isNetworkAvailable(Pin_Request_Activity.this)) {

                List<NameValuePair> postParameters = new ArrayList<>();
                postParameters.add(new BasicNameValuePair("DepositAmount", amount));
                postParameters.add(new BasicNameValuePair("PayMode", "Bank"));
                postParameters.add(new BasicNameValuePair("AccountNo", "" + account_number));
                postParameters.add(new BasicNameValuePair("BranchName", "" + breanch_name));
                postParameters.add(new BasicNameValuePair("BankName", bank_name));
                postParameters.add(new BasicNameValuePair("TransactionNo", transaction_no));
                postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("IdNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("DeviceID", AppUtils.getDeviceID(Pin_Request_Activity.this)));


                JSONArray jsonArrayOrder = new JSONArray();

                int[] qtyarr = new int[allEds.size()];
                int[] amtarr = new int[allEds.size()];

                for (int i = 0; i < allEds.size(); i++) {
                    if (allEds.get(i).getText().toString().trim().length() > 0)
                        qtyarr[i] = Math.round(Float.parseFloat(allEds.get(i).getText().toString().trim()));
                    else
                        qtyarr[i] = 0;

                    if (allTvs.get(i).getText().toString().trim().length() > 0)
                        amtarr[i] = Math.round(Float.parseFloat(allTvs.get(i).getText().toString().trim()));
                    else
                        amtarr[i] = 0;
                }

                for (int i = 0; i < kitid.size(); i++) {
                    JSONObject jsonObjectDetail = new JSONObject();
                    jsonObjectDetail.put("KitId", "" + kitid.get(i).trim().replace(",", " "));
                    jsonObjectDetail.put("KitName", "" + kitname.get(i).trim().replace(",", " "));
                    jsonObjectDetail.put("Qty", "" + qtyarr[i]);
                    jsonObjectDetail.put("Amount", amtarr[i]);

                    jsonArrayOrder.put(jsonObjectDetail);
                }

                postParameters.add(new BasicNameValuePair("PackageDetails", jsonArrayOrder.toString().trim()));

                executeRequestAmount(postParameters);

//                String Bankid = "0";
//                for (int i = 0; i < AppController.bankList.size(); i++) {
//                    if (bank_name.equalsIgnoreCase(AppController.bankList.get(i).get("Bank"))) {
//                        Bankid = AppController.bankList.get(i).get("BID");
//                    }
//                }

            } else {
                AppUtils.alertDialog(Pin_Request_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void executeBankRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Pin_Request_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Pin_Request_Activity.this, postParameters, QueryUtils.methodMaster_FillBank, TAG);
                } catch (Exception ignored) {
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
                        if (jsonArrayData.length() != 0) {
                            getBankResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Pin_Request_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Pin_Request_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getBankResult(JSONArray jsonArray) {
        try {
            AppController.bankList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("BID", jsonObject.getString("BID"));
                map.put("Bank", WordUtils.capitalizeFully(jsonObject.getString("Bank")));

                AppController.bankList.add(map);
            }


            showBankDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBankDialog() {

        try {
            bankArray = new String[AppController.bankList.size()];
            for (int i = 0; i < AppController.bankList.size(); i++) {
                bankArray[i] = AppController.bankList.get(i).get("Bank");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Bank");
            builder.setItems(bankArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_choose_bank.setText(bankArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void showpaymodeDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose Payment Mode");
            builder.setItems(paymodeArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_choose_pay_mode.setText(paymodeArray[item]);
                    paymode = paymodeArray[item];
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void executeRequestAmount(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Pin_Request_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Pin_Request_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            if (paymode.equalsIgnoreCase("Cash")) {
                                // Api idetify by PayMode = Cash
                                response = AppUtils.callWebServiceWithMultiParam(Pin_Request_Activity.this, postParameters, QueryUtils.methodToRequestEpin, TAG);
                            } else
                                response = AppUtils.callWebServiceWithMultiParam(Pin_Request_Activity.this, postParameters, QueryUtils.methodToRequestEpin, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
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
                                AppUtils.alertDialogWithFinish(Pin_Request_Activity.this, "" + jsonObject.getString("Message"));
                            } else {
                                AppUtils.alertDialog(Pin_Request_Activity.this, jsonObject.getString("Message"));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    private void executeLoadPackages() {
        try {
            if (AppUtils.isNetworkAvailable(Pin_Request_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Pin_Request_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            response = AppUtils.callWebServiceWithMultiParam(Pin_Request_Activity.this, postParameters, QueryUtils.methodToPinRequestPackage, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                WriteValues(jsonArrayData);
                            } else {
                                AppUtils.dismissProgressDialog();
                                AppUtils.alertDialog(Pin_Request_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
    }

    public void WriteValues(JSONArray jsonArray) {
        float sp = 8;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

        allEds.clear();
        allTvs.clear();
        kitid.clear();
        kitname.clear();


        TableLayout ll = (TableLayout) findViewById(R.id.displayLinear);

        TableRow row1 = new TableRow(this);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.roboto);

        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row1.setLayoutParams(lp1);
        row1.setBackgroundColor(getResources().getColor(R.color.color_136D98));

        TextView A1 = new TextView(this);
        TextView B1 = new TextView(this);
        TextView C1 = new TextView(this);
        TextView D1 = new TextView(this);

        A1.setText("Kit Name");
        B1.setText("Kit Amount");
        C1.setText("Qty.");
        D1.setText("Amount");

        A1.setPadding(px, px, px, px);
        B1.setPadding(px, px, px, px);
        C1.setPadding(px, px, px, px);
        D1.setPadding(px, px, px, px);

        A1.setTypeface(typeface);
        B1.setTypeface(typeface);
        C1.setTypeface(typeface);
        D1.setTypeface(typeface);

        A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        A1.setGravity(Gravity.CENTER);
        B1.setGravity(Gravity.CENTER);
        C1.setGravity(Gravity.CENTER);
        D1.setGravity(Gravity.CENTER);

        A1.setTextColor(Color.WHITE);
        B1.setTextColor(Color.WHITE);
        C1.setTextColor(Color.WHITE);
        D1.setTextColor(Color.WHITE);

        row1.addView(A1);
        row1.addView(B1);
        row1.addView(C1);
        row1.addView(D1);


        ll.addView(row1);


        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jobject = jsonArray.getJSONObject(i);

                int payout_number = jobject.getInt("KitId");

                String KitName = jobject.getString("KitName");
                final int KitAmount = jobject.getInt("KitAmount");

                kitid.add("" + payout_number);
                kitname.add(KitName);

                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                row.setBackgroundColor(Color.WHITE);

                TextView A = new TextView(this);
                TextView B = new TextView(this);
                EditText C = new EditText(this);
                final TextView D = new TextView(this);

                A.setText("" + KitName);
                B.setText("" + KitAmount);
                C.setText("0");
                D.setText("0");

                C.setInputType(InputType.TYPE_CLASS_PHONE);
                C.setKeyListener(DigitsKeyListener.getInstance("0123456789"));

                C.setId(payout_number);
                D.setId(payout_number);

                A.setGravity(Gravity.CENTER);
                B.setGravity(Gravity.CENTER);
                C.setGravity(Gravity.CENTER);
                D.setGravity(Gravity.CENTER);

                A.setPadding(px, px, px, px);
                B.setPadding(px, px, px, px);
                C.setPadding(px, px, px, px);
                D.setPadding(px, px, px, px);

                A.setTypeface(typeface);
                B.setTypeface(typeface);
                C.setTypeface(typeface);
                D.setTypeface(typeface);

                A.setTextColor(Color.BLACK);
                B.setTextColor(Color.BLACK);
                C.setTextColor(Color.BLACK);
                D.setTextColor(Color.BLACK);

                A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                allEds.add(C);
                allTvs.add(D);

                C.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        int abc = 0;
                        try {
                            if (s.length() > 0)
                                abc = Math.round(Float.parseFloat(s.toString().trim()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        D.setText("" + (abc * KitAmount));

                        int[] strings = new int[allEds.size()];

                        for (int i = 0; i < allEds.size(); i++) {
                            if (allEds.get(i).getText().toString().trim().length() > 0)
                                strings[i] = Math.round(Float.parseFloat(allEds.get(i).getText().toString().trim()));
                            else
                                strings[i] = 0;
                        }

                        int sum = 0;

                        for (int i = 0; i < strings.length; i++) {
                            sum += strings[i];
                        }

                        edtxt_total_pin.setText("" + sum);

                    }
                });

                D.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        int[] strings = new int[allTvs.size()];

                        for (int i = 0; i < allTvs.size(); i++) {
                            strings[i] = Math.round(Float.parseFloat(allTvs.get(i).getText().toString().trim()));
                        }

                        int sum = 0;

                        for (int i = 0; i < strings.length; i++) {
                            sum += strings[i];
                        }
                        edtxt_amount.setText("" + sum);
                    }
                });

                row.addView(A);
                row.addView(B);
                row.addView(C);
                row.addView(D);

                View view_one = new View(this);
                view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view_one.setBackgroundColor(getResources().getColor(R.color.app_color_green_one));

                ll.addView(row);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Request_Activity.this);
        }

        System.gc();
    }
}