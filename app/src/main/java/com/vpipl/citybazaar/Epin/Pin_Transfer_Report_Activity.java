package com.vpipl.citybazaar.Epin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pin_Transfer_Report_Activity extends AppCompatActivity {

    String TAG = "Pin_Transfer_Report_Activity";

    TextView txt_from_date, txt_to_date, txt_heading;

    Button btn_proceed;

    TableLayout displayLinear;

    Calendar myCalendar;
    SimpleDateFormat sdf;
    String whichdate = "";
    DatePickerDialog datePickerDialog;

    private void showdatePicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(Pin_Transfer_Report_Activity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        datePickerDialog.show();
    }

    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

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

                AppUtils.alertDialog(Pin_Transfer_Report_Activity.this, "Selected Date Can't be After today");
            }
        }
    };

    public DrawerLayout drawer;
    public NavigationView navigationView;

    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;
    private TextView txt_welcome_name, txt_id_number;
    private CircularImageView profileImage;

    ImageView img_login_logout ,img_nav_back ;
    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);

        final Drawable upArrow;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
                    startActivity(new Intent(Pin_Transfer_Report_Activity.this, Login_New_Activity.class));
                } else {
                    AppUtils.showDialogSignOut(Pin_Transfer_Report_Activity.this);
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_received_report);

        try {
           /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            AppUtils.setActionbarTitle(getSupportActionBar(), this);*/

            Toolbar toolbar = findViewById(R.id.toolbar);
            img_nav_back = findViewById(R.id.img_nav_back);
            LinearLayout ll_nav_back = findViewById(R.id.ll_nav_back);
            ll_nav_back.setVisibility(View.GONE);

            img_login_logout = findViewById(R.id.img_login_logout);
            img_login_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Pin_Transfer_Report_Activity.this, DashBoard_Activity.class));
                    finish();
                }
            });
/*            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");

            SetupToolbar();*/


            txt_from_date = (TextView) findViewById(R.id.txt_from_date);
            txt_to_date = (TextView) findViewById(R.id.txt_to_date);
            txt_heading = (TextView) findViewById(R.id.txt_heading);
            btn_proceed = (Button) findViewById(R.id.btn_proceed);

            displayLinear = (TableLayout) findViewById(R.id.displayLinear);

            txt_heading.setText("E-Pin Transfer Report");

            btn_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createWalletRequestStatusRequest();
                }
            });

            myCalendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("dd-MMM-yyyy");

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
                createWalletRequestStatusRequest();
            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }


            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            navigationView = (NavigationView) findViewById(R.id.nav_view);

            View navHeaderView = navigationView.getHeaderView(0);
            txt_welcome_name = (TextView) navHeaderView.findViewById(R.id.txt_welcome_name);

            txt_id_number = (TextView) navHeaderView.findViewById(R.id.txt_id_number);
            profileImage = (CircularImageView) navHeaderView.findViewById(R.id.iv_Profile_Pic);
            LinearLayout ll_van_bottom_ttl_income =  navHeaderView.findViewById(R.id.ll_van_bottom_ttl_income);
            ll_van_bottom_ttl_income.setVisibility(View.GONE);
            expListView = (ExpandableListView) findViewById(R.id.left_drawer);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                toggle.getDrawerArrowDrawable().setColor(getColor(R.color.app_color_white));
            } else {
                toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.app_color_white));
            }
            listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();

            enableExpandableList();
            LoadNavigationHeaderItems();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppUtils.isNetworkAvailable(this)) {
        //    createWalletRequestStatusRequest();
        } else {
            AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if (AppUtils.isNetworkAvailable(this)) {
         //   createWalletRequestStatusRequest();
        } else {
            AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
        }
    }

    private void createWalletRequestStatusRequest() {

        findViewById(R.id.HSV).setVisibility(View.GONE);

        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
        postParameters.add(new BasicNameValuePair("FromDate", "" + txt_from_date.getText().toString()));
        postParameters.add(new BasicNameValuePair("ToDate", "" + txt_to_date.getText().toString()));

        executeWalletRequestTatusReportRequest(postParameters);
    }

    private void executeWalletRequestTatusReportRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Pin_Transfer_Report_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Pin_Transfer_Report_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Pin_Transfer_Report_Activity.this,
                                    postparameters, QueryUtils.methodPinTransferReport, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_Transfer_Report_Activity.this);
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
                                    AppUtils.alertDialog(Pin_Transfer_Report_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Pin_Transfer_Report_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Pin_Transfer_Report_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Transfer_Report_Activity.this);
        }
    }

    public void WriteValues(final JSONArray jarray) {

        findViewById(R.id.HSV).setVisibility(View.VISIBLE);

        float sp = 10;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);
        int px_right = (int) (15 * getResources().getDisplayMetrics().scaledDensity);

        TableLayout ll = (TableLayout) findViewById(R.id.displayLinear);
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

        A1.setText("Transfer\nFrom ID");
        B1.setText("Transfer From\nMember");
        C1.setText("Transfer To ID");
        D1.setText("Transfer To\nMember");
        E1.setText("Package");
        F1.setText("Bill No");
        G1.setText("Bill Date");
        H1.setText("");

        A1.setPadding(px, px, px, px);
        B1.setPadding(px, px, px, px);
        C1.setPadding(px, px, px, px);
        D1.setPadding(px, px, px, px);
        E1.setPadding(px, px, px, px);
        F1.setPadding(px, px, px, px);
        G1.setPadding(px, px, px, px);
        H1.setPadding(px, px, px_right, px);

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
        view.setBackgroundColor(Color.BLACK);

        ll.addView(row1);
       

        for (int i = 0; i < jarray.length(); i++) {
            try {

                JSONObject jobject = jarray.getJSONObject(i);

                String fromIdNo = jobject.getString("fromIdNo");
                String FromMemName = WordUtils.capitalizeFully(jobject.getString("FromMemName"));
                String ToIdNo = jobject.getString("ToIdNo");
                String ToMemName = jobject.getString("ToMemName");
                String KitName = jobject.getString("KitName");
                final String BillNo = jobject.getString("BillNo");
                String TDate = (jobject.getString("TDate"));

                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                row.setBackgroundColor(Color.WHITE);

                TextView A = new TextView(this);
                TextView B = new TextView(this);
                TextView C = new TextView(this);
                TextView D = new TextView(this);
                TextView E = new TextView(this);
                TextView F = new TextView(this);
                TextView G = new TextView(this);
                TextView H = new TextView(this);

                A.setText(fromIdNo);
                B.setText(FromMemName);
                C.setText(ToIdNo);
                D.setText(ToMemName);
                E.setText(KitName);
                F.setText(BillNo);
                G.setText(AppUtils.getDateFromAPIDate(TDate));
                H.setText("View Detail");

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
                H.setPadding(px, px, px_right, px);

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

                H.setTextColor(Color.BLUE);

                H.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Pin_Transfer_Report_Activity.this, Pin_details_for_transfer_Activity.class);
                        i.putExtra("BillNo", "" + BillNo);
                        startActivity(i);
                    }
                });

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
                view_one.setBackgroundColor(Color.parseColor("#dddddd"));

                ll.addView(row);
                
                    ll.addView(view_one);
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
            AppUtils.showExceptionDialog(Pin_Transfer_Report_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
            ////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Pin_Transfer_Report_Activity.this);
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

                if (GroupTitle.trim().equalsIgnoreCase("Topup/E-Pin Detail")) {
                    startActivity(new Intent(Pin_Transfer_Report_Activity.this, Pin_details_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("Generated/Issued Pin Details")) {
                    startActivity(new Intent(Pin_Transfer_Report_Activity.this, Pin_Generated_issued_details_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("E-pin Transfer")) {
                    startActivity(new Intent(Pin_Transfer_Report_Activity.this, Pin_transfer_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("E-pin Received Detail")) {
                    startActivity(new Intent(Pin_Transfer_Report_Activity.this, Pin_Received_Report_Activity.class));
                    finish();

                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    startActivity(new Intent(Pin_Transfer_Report_Activity.this, DashBoard_Activity.class));
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

            listDataHeader.add("Generated/Issued Pin Details");
            listDataHeader.add("Topup/E-Pin Detail");
            listDataHeader.add("E-pin Transfer");
            listDataHeader.add("E-pin Received Detail");
            listDataHeader.add("Logout");


            listDataChild.put(listDataHeader.get(0), Empty);
            listDataChild.put(listDataHeader.get(1), Empty);
            listDataChild.put(listDataHeader.get(2), Empty);
            listDataChild.put(listDataHeader.get(3), Empty);
            listDataChild.put(listDataHeader.get(4), Empty);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void LoadNavigationHeaderItems() {
        txt_id_number.setText("");
        txt_id_number.setVisibility(View.GONE);

        txt_welcome_name.setText("");

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            String welcome_text = WordUtils.capitalizeFully(AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
            txt_welcome_name.setText(welcome_text);


            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon_user);
            profileImage.setImageBitmap(largeIcon);

            String userid = AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "");
            txt_id_number.setText(userid);
            txt_id_number.setVisibility(View.VISIBLE);

            String bytecode = AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "");
            profileImage.setImageBitmap(AppUtils.getBitmapFromString(bytecode));
        }
    }
}
