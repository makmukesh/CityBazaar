package com.vpipl.citybazaar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.vpipl.citybazaar.Adapters.ExpandableListAdapter;
import com.vpipl.citybazaar.Epin.Transaction_login_Activity;
import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.CircularImageView;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet_Request_Status_Report_Activity extends AppCompatActivity {

    Activity act = Wallet_Request_Status_Report_Activity.this;
    private static DrawerLayout drawer;
    private static NavigationView navigationView;
    private String TAG = "Wallet_Request_Status_Report_Activity";
    private TextView txt_from_date;
    private TextView txt_to_date;
    private Button btn_proceed;
    private Button btn_load_more;
    private TableLayout displayLinear;
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

                if (whichdate.equalsIgnoreCase("txt_from_date"))
                    txt_from_date.setText(sdf.format(myCalendar.getTime()));
                else if (whichdate.equalsIgnoreCase("txt_to_date"))
                    txt_to_date.setText(sdf.format(myCalendar.getTime()));

            } else {

                AppUtils.alertDialog(act, "Selected Date Can't be After today");
            }
        }
    };
    private int TopRows = 25;
    private TextView txt_welcome_name;
    private TextView txt_id_number;
    private TextView txt_available_wb;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private int lastExpandedPosition = -1;
    private ExpandableListView expListView;
    private CircularImageView profileImage;
    private JSONArray HeadingJarray;
    private DatePickerDialog datePickerDialog;

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);


        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(navigationView)) {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar));
                    drawer.closeDrawer(navigationView);
                } else {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));
                    drawer.openDrawer(navigationView);
                }
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
        setContentView(R.layout.activity_wallet_request_status_report);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            AppUtils.changeStatusBarColor(act);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            txt_from_date = findViewById(R.id.txt_from_date);
            txt_to_date = findViewById(R.id.txt_to_date);

            btn_proceed = findViewById(R.id.btn_proceed);
            btn_load_more = findViewById(R.id.btn_load_more);

            displayLinear = findViewById(R.id.displayLinear);

            btn_load_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    TopRows = TopRows + 25;
                    createWalletRequestStatusRequest();

                }
            });

            btn_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TopRows = 25;
                    createWalletRequestStatusRequest();
                }
            });

            myCalendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("dd MMM yyyy");

            txt_from_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    whichdate = "txt_from_date";
                    showdatePicker();
                }
            });


            txt_to_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    whichdate = "txt_to_date";
                    showdatePicker();
                }
            });

            if (AppUtils.isNetworkAvailable(this)) {
                executeLoginRequest();
            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }

            drawer = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            View navHeaderView = navigationView.getHeaderView(0);
            txt_welcome_name = navHeaderView.findViewById(R.id.txt_welcome_name);
            txt_available_wb = navHeaderView.findViewById(R.id.txt_available_wb);
            txt_id_number = navHeaderView.findViewById(R.id.txt_id_number);
            profileImage = navHeaderView.findViewById(R.id.iv_Profile_Pic);
            LinearLayout LL_Nav = navHeaderView.findViewById(R.id.LL_Nav);
            expListView = findViewById(R.id.left_drawer);

            listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();

            HeadingJarray = Splash_Activity.HeadingJarray;

            LL_Nav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
                    if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
                        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                            startActivity(new Intent(act, Profile_View_New_Activity.class));
                        } else {
                            startActivity(new Intent(act, Login_New_Activity.class));
                        }

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    }
                }
            });
            drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(@NonNull View drawerView) {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));
                }

                @Override
                public void onDrawerClosed(@NonNull View drawerView) {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar));
                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }

    private void showdatePicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        datePickerDialog.show();
    }


    private void executeLoginRequest() {
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
                            postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Password", AppController.getSpUserInfo().getString(SPUtils.USER_PASSWORD, "")));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToLoginMember, TAG);

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
                                continueapp();
                            } else {

                                Toast.makeText(act, "Please Login to continue..", Toast.LENGTH_SHORT).show();

                                AppController.getSpUserInfo().edit().clear().commit();
                                AppController.getSpIsLogin().edit().clear().commit();

                                Intent intent = new Intent(act, Login_New_Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("SendToHome", true);
                                startActivity(intent);
                                finish();

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

    public void continueapp() {

        createWalletRequestStatusRequest();

        enableExpandableList();
        LoadNavigationHeaderItems();
    }

    private void createWalletRequestStatusRequest() {

        findViewById(R.id.HSV).setVisibility(View.GONE);

        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
        postParameters.add(new BasicNameValuePair("TopRows", "" + TopRows));
        postParameters.add(new BasicNameValuePair("ToJD", "" + txt_to_date.getText().toString()));
        postParameters.add(new BasicNameValuePair("FromJD", "" + txt_from_date.getText().toString()));

        executeWalletRequestTatusReportRequest(postParameters);
    }

    private void executeWalletRequestTatusReportRequest(final List postparameters) {
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
                            response = AppUtils.callWebServiceWithMultiParam(act,
                                    postparameters, QueryUtils.methodToGetWallet_Request_Status_Report, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
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
                                if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                    WriteValues(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(act, jsonObject.getString("Message"));
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
            AppUtils.showExceptionDialog(act);
        }
    }

    private void WriteValues(final JSONArray jarray) {

        findViewById(R.id.HSV).setVisibility(View.VISIBLE);

        float sp = 8;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

        TableLayout ll = findViewById(R.id.displayLinear);
        ll.removeAllViews();

        Typeface typeface = ResourcesCompat.getFont(this, R.font.roboto);

        TableRow row1 = new TableRow(this);

        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row1.setLayoutParams(lp1);
        row1.setBackgroundColor(getResources().getColor(R.color.color_136D98));

        TextView A1 = new TextView(this);
        TextView B1 = new TextView(this);
        TextView C1 = new TextView(this);
        TextView D1 = new TextView(this);
        TextView E1 = new TextView(this);
        TextView F1 = new TextView(this);
        TextView G1 = new TextView(this);
        TextView H1 = new TextView(this);

        A1.setText("Requested\nAmount");
        B1.setText("Bank Name");
        C1.setText("Transaction No");
        D1.setText("Transaction Date");
        E1.setText("Request Date/Time");
        F1.setText("Requested From IP");
        G1.setText("Request Status");
        H1.setText("Response Remarks");

        A1.setPadding(px, px, px, px);
        B1.setPadding(px, px, px, px);
        C1.setPadding(px, px, px, px);
        D1.setPadding(px, px, px, px);
        E1.setPadding(px, px, px, px);
        F1.setPadding(px, px, px, px);
        G1.setPadding(px, px, px, px);
        H1.setPadding(px, px, px, px);

        A1.setTypeface(typeface);
        B1.setTypeface(typeface);
        C1.setTypeface(typeface);
        D1.setTypeface(typeface);
        E1.setTypeface(typeface);
        F1.setTypeface(typeface);
        G1.setTypeface(typeface);
        H1.setTypeface(typeface);

        A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        G1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        A1.setGravity(Gravity.CENTER);
        B1.setGravity(Gravity.CENTER);
        C1.setGravity(Gravity.CENTER);
        D1.setGravity(Gravity.CENTER);
        E1.setGravity(Gravity.CENTER);
        F1.setGravity(Gravity.CENTER);
        G1.setGravity(Gravity.CENTER);
        H1.setGravity(Gravity.CENTER);

        A1.setTextColor(Color.WHITE);
        B1.setTextColor(Color.WHITE);
        C1.setTextColor(Color.WHITE);
        D1.setTextColor(Color.WHITE);
        E1.setTextColor(Color.WHITE);
        F1.setTextColor(Color.WHITE);
        G1.setTextColor(Color.WHITE);
        H1.setTextColor(Color.WHITE);

        row1.addView(A1);
        row1.addView(B1);
        row1.addView(C1);
        row1.addView(D1);
        row1.addView(E1);
        row1.addView(F1);
        row1.addView(G1);
        row1.addView(H1);

        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(Color.parseColor("#cccccc"));

        ll.addView(row1);
        ll.addView(view);

        for (int i = 0; i < jarray.length(); i++) {
            try {

                JSONObject jobject = jarray.getJSONObject(i);

                String Requested_Amount = jobject.getString("RequestAmt");
                String Bank_Name = WordUtils.capitalizeFully(jobject.getString("BankName"));
                String Transaction_No = jobject.getString("TransactionNo");
                String Transaction_Date = jobject.getString("DepositDate");
                String Request_Date_Time = jobject.getString("ReqRTS");
                String Requested_From_IP = jobject.getString("HostIP");
                String Request_Status = WordUtils.capitalizeFully(jobject.getString("ReqStatus"));
                String Response_Remarks = WordUtils.capitalizeFully(jobject.getString("ResponseRemarks"));

                StringBuilder sb = new StringBuilder(Bank_Name);

                int ii = 0;
                while ((ii = sb.indexOf(" ", ii + 8)) != -1) {
                    sb.replace(ii, ii + 1, "\n");
                }

                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                if (i % 2 == 0)
                    row.setBackgroundColor(Color.WHITE);
                else
                    row.setBackgroundColor(Color.parseColor("#dddddd"));


                TextView A = new TextView(this);
                TextView B = new TextView(this);
                TextView C = new TextView(this);
                TextView D = new TextView(this);
                TextView E = new TextView(this);
                TextView F = new TextView(this);
                TextView G = new TextView(this);
                TextView H = new TextView(this);

                A.setText("₹ " + Requested_Amount);
                B.setText(sb.toString());
                C.setText(Transaction_No);
                D.setText(AppUtils.getDateFromAPIDate(Transaction_Date));
                E.setText(AppUtils.getDateFromAPIDate(Request_Date_Time));
                F.setText(Requested_From_IP);
                G.setText(Request_Status);
                H.setText(Response_Remarks);

                A.setGravity(Gravity.CENTER);
                B.setGravity(Gravity.CENTER);
                C.setGravity(Gravity.CENTER);
                D.setGravity(Gravity.CENTER);
                E.setGravity(Gravity.CENTER);
                F.setGravity(Gravity.CENTER);
                G.setGravity(Gravity.CENTER);
                H.setGravity(Gravity.CENTER);

                A.setPadding(px, px, px, px);
                B.setPadding(px, px, px, px);
                C.setPadding(px, px, px, px);
                D.setPadding(px, px, px, px);
                E.setPadding(px, px, px, px);
                F.setPadding(px, px, px, px);
                G.setPadding(px, px, px, px);
                H.setPadding(px, px, px, px);

                A.setTypeface(typeface);
                B.setTypeface(typeface);
                C.setTypeface(typeface);
                D.setTypeface(typeface);
                E.setTypeface(typeface);
                F.setTypeface(typeface);
                G.setTypeface(typeface);
                H.setTypeface(typeface);

                A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                G.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                row.addView(A);
                row.addView(B);
                row.addView(C);
                row.addView(D);
                row.addView(E);
                row.addView(F);
                row.addView(G);
                row.addView(H);

                View view_one = new View(this);
                view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view.setBackgroundColor(Color.parseColor("#cccccc"));

                ll.addView(row);
                ll.addView(view_one);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
            ////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void enableExpandableList() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            if (HeadingJarray != null && HeadingJarray.length() > 0)
                prepareListDataDistributor(listDataHeader, listDataChild, HeadingJarray);
            else
                executeTogetDrawerMenuItems();
        }

        ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                String GroupTitle = listDataHeader.get(groupPosition);

                if (GroupTitle.trim().equalsIgnoreCase(getResources().getString(R.string.dashboard))) {
                    startActivity(new Intent(act, DashBoard_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(act);
                } else if (GroupTitle.trim().equalsIgnoreCase("New Joining")) {
                    startActivity(new Intent(act, Register_Free_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Enquiry")) {
                    startActivity(new Intent(act, Register_Complaint_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
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
                String ChildItemTitle = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);

                if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.view_profile))) {
                    startActivity(new Intent(act, Profile_View_New_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Update Profile")) {
                    startActivity(new Intent(act, Profile_Update_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.change_password))) {
                    startActivity(new Intent(act, Change_Password_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.new_joining))) {
                    startActivity(new Intent(act, Register_Free_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Binary Geneology")) {
                    startActivity(new Intent(act, Sponsor_genealogy_Activity.class).putExtra("URL", QueryUtils.getViewBinarygenealogyURL(act)));
                    //startActivity(new Intent(act, Sponsor_genealogy_Activity.class).putExtra("URL", "https://boodmo.com"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Downline Team Detail")) {
                    startActivity(new Intent(act, Downline_Team_Details_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Sponsor Geneology")) {
                    startActivity(new Intent(act, Sponsor_genealogy_Activity.class).putExtra("URL", QueryUtils.getViewgenealogyURL(act)));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.sponsor_downline))) {
                    startActivity(new Intent(act, Sponsor_team_details_Activity.class).putExtra("Action", "Sponsor"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.bv_detail_report))) {
                    startActivity(new Intent(act, Sponsor_team_details_Activity.class).putExtra("Action", "Direct"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.welcome_letter))) {
                    startActivity(new Intent(act, WelcomeLetter_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.upload_kyc))) {
                    startActivity(new Intent(act, KYCUploadDocument_Activity.class).putExtra("HEADING", "Update"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("ID Card")) {
                    startActivity(new Intent(act, ID_card_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Auto Pool Details")) {
                    startActivity(new Intent(act, Auto_Growth_Pool_Details_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Downline Team Auto Pool Inc.")) {
                    startActivity(new Intent(act, Downline_Team_Auto_Growth_Incentive_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Repurchase Bill Summary")) {
                    startActivity(new Intent(act, Repurchase_Bill_Summary.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Repurchase Bv Detail")) {
                    startActivity(new Intent(act, Repurchase_BV_Detail.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Team BV Summary")) {
                    startActivity(new Intent(act, Repurchase_BV_Summary_Team_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Daily Payout")) {
                    startActivity(new Intent(act, Daily_Payout_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("BV Income")) {
                    startActivity(new Intent(act, Repurchase_BV_Summary_Team_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View Order Detail")) {
                    startActivity(new Intent(act, MyOrders_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Place Order")) {
                    //  startActivity(new Intent(act, PlaceOrderActivity.class));
                    startActivity(new Intent(act, PlaceOrderOnlineActivity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Request For Wallet Amount")) {
                    startActivity(new Intent(act, Wallet_Request_Amount_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Request Report")) {
                    startActivity(new Intent(act, Wallet_Request_Status_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Transaction Detail")) {
                    startActivity(new Intent(act, Wallet_Transaction_Report_Activity.class));

                } else if (ChildItemTitle.trim().equalsIgnoreCase("Daily Incentive")) {
                    startActivity(new Intent(act, Daily_Incentive_report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Daily Incentive Report")) {
                    startActivity(new Intent(act, Daily_Incentive_Detailed_report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Weekly Incentive")) {
                    startActivity(new Intent(act, Weekly_Incentive_report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Weekly Incentive Detailed Report")) {
                    startActivity(new Intent(act, Weekly_Incentive_Detailed_report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Monthly Incentive")) {
                    startActivity(new Intent(act, Monthly_Incentive_report_Activity.class));
                }/* else if (ChildItemTitle.trim().equalsIgnoreCase("Monthly Incentive Detail Report")) {
                    startActivity(new Intent(act, Monthly_Incentive_Detailed_report_Activity.class));
                }*/ else if (ChildItemTitle.trim().equalsIgnoreCase("Reward Detail")) {
                    startActivity(new Intent(act, Reward_Details_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Generated/Issued Pin Details")) {
                    startActivity(new Intent(act, Transaction_login_Activity.class).putExtra("SEND_TO", "Generated/Issue Pin Details"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Topup/E-Pin Detail")) {
                    startActivity(new Intent(act, Transaction_login_Activity.class).putExtra("SEND_TO", "Topup/E-Pin Detail"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Transfer")) {
                    startActivity(new Intent(act, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Transfer"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Transfer Detail")) {
                    startActivity(new Intent(act, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Transfer Detail"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Recieved Detail")) {
                    startActivity(new Intent(act, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Received Detail"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Request")) {
                    startActivity(new Intent(act, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Request"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Request Detail")) {
                    startActivity(new Intent(act, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Request Detail"));

                }

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });
    }

    private void LoadNavigationHeaderItems() {
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

            executeWalletBalanceRequest();

            String bytecode = AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "");

            if (!bytecode.equalsIgnoreCase(""))
                profileImage.setImageBitmap(AppUtils.getBitmapFromString(bytecode));
        }
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
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                    String count_text = "Wallet Balance : \u20B9 " + jsonArrayData.getJSONObject(0).getString("WBalance");
                                    txt_available_wb.setText(count_text);
                                    txt_available_wb.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareListDataDistributor(List<String> listDataHeader, Map<String, List<String>> listDataChild, JSONArray HeadingJarray) {

        List<String> Empty = new ArrayList<>();
        try {
            ArrayList<String> MenuAl = new ArrayList<>();
            for (int i = 0; i < HeadingJarray.length(); i++) {
                if (HeadingJarray.getJSONObject(i).getInt("ParentId") == 0)
                    MenuAl.add(HeadingJarray.getJSONObject(i).getString("MenuName"));
            }

            for (int aa = 0; aa < MenuAl.size(); aa++) {
                ArrayList<String> SubMenuAl = new ArrayList<>();

                for (int bb = 0; bb < HeadingJarray.length(); bb++) {
                    if (HeadingJarray.getJSONObject(aa).getInt("MenuId") == HeadingJarray.getJSONObject(bb).getInt("ParentId")) {
                        SubMenuAl.add(AppUtils.CapsFirstLetterString(HeadingJarray.getJSONObject(bb).getString("MenuName")));
                    }
                }
                listDataHeader.add(AppUtils.CapsFirstLetterString(MenuAl.get(aa)));
                listDataChild.put(listDataHeader.get(aa), SubMenuAl);
            }
            listDataHeader.add("Logout");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeTogetDrawerMenuItems() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                AppUtils.showProgressDialog(act);
            }

            @Override
            protected String doInBackground(Void... params) {

                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodtoGetDrawerMenuItems, TAG);

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
                        HeadingJarray = jsonObject.getJSONArray("Data");
                        prepareListDataDistributor(listDataHeader, listDataChild, HeadingJarray);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
