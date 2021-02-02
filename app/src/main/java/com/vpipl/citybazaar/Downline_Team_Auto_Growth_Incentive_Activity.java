package com.vpipl.citybazaar;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.google.android.material.textfield.TextInputEditText;
import com.vpipl.citybazaar.Adapters.ExpandableListAdapter;
import com.vpipl.citybazaar.Epin.Transaction_login_Activity;
import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.CircularImageView;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;
import com.vpipl.citybazaar.model.StackHelperState;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Downline_Team_Auto_Growth_Incentive_Activity extends AppCompatActivity {

    Activity act = Downline_Team_Auto_Growth_Incentive_Activity.this;
    private static DrawerLayout drawer;
    private static NavigationView navigationView;
    private String TAG = "Downline_Team_Auto_Growth_Incentive_Activity";
    private TableLayout displayLinear;
    private TextView txt_welcome_name;
    private TextView txt_id_number;
    private TextView txt_available_wb;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private int lastExpandedPosition = -1;
    private ExpandableListView expListView;
    private CircularImageView profileImage;
    private JSONArray HeadingJarray;
    TextInputEditText edtxt_search_keyword ;
    Button btn_submit ;

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);


        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(navigationView)) {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable. icon_nav_bar));
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
    List<StackHelperState> stackHelperList = new ArrayList<>();
    JSONArray jsonarray_statelist;
    String Statecode = "0", Statename = "" ;
    Spinner spinner_selectstate;
    TableLayout ll ;
    private RadioGroup rg_view_detail_for;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downline_team_auto_growth_incentive);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            AppUtils.changeStatusBarColor(act);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            displayLinear = findViewById(R.id.displayLinear);

            if (AppUtils.isNetworkAvailable(this)) {
                executeLoginRequest();
            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }

            ll = findViewById(R.id.displayLinear);
            drawer = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            View navHeaderView = navigationView.getHeaderView(0);
            txt_welcome_name = navHeaderView.findViewById(R.id.txt_welcome_name);
            txt_available_wb = navHeaderView.findViewById(R.id.txt_available_wb);
            txt_id_number = navHeaderView.findViewById(R.id.txt_id_number);
            profileImage = navHeaderView.findViewById(R.id.iv_Profile_Pic);
            LinearLayout LL_Nav = navHeaderView.findViewById(R.id.LL_Nav);
            expListView = findViewById(R.id.left_drawer);
            spinner_selectstate = findViewById(R.id.spinner_selectstate);
            edtxt_search_keyword = findViewById(R.id.edtxt_search_keyword);
            btn_submit = findViewById(R.id.btn_submit);
            rg_view_detail_for = findViewById(R.id.rg_view_detail_for);

            jsonarray_statelist = new JSONArray();
            
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
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable. icon_nav_bar));
                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });

            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(act , v);
                    executeFranchiseelistRequest(edtxt_search_keyword.getText().toString(), Statecode);
                }
            });

            /*spinner state and frachisee list*/
            spinner_selectstate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                    if (jsonarray_statelist.length() > 0) {
                        Statename = ((StackHelperState) spinner_selectstate.getSelectedItem()).getStateName();
                        Statecode = ((StackHelperState) spinner_selectstate.getSelectedItem()).getCode();

                        executeFranchiseelistRequest(edtxt_search_keyword.getText().toString(), Statecode);
                    }
                }

                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
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

     //   executeFranchiseelistRequest("");
        executeStateRequest();
        enableExpandableList();
        LoadNavigationHeaderItems();
    }

    private void executeFranchiseelistRequest(final String partyname,final String statename) {
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
                            /* FormNo:SessId:IdNoOrName:Level:	*/
                            int selectedId = rg_view_detail_for.getCheckedRadioButtonId();
                            RadioButton radioButton = findViewById(selectedId);
                            String view_detail = radioButton.getText().toString().trim();

                            String Type = "0";

                            if (view_detail.equalsIgnoreCase("Direct Downline Members"))
                                Type = "1";

                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("SessId", "" + statename));
                            postParameters.add(new BasicNameValuePair("IdNoOrName", "" + partyname));
                            postParameters.add(new BasicNameValuePair("Level", "" + Type));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToDownlineTeamAutoPoolIncomeDetail, TAG);

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
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Report");
                                ll.setVisibility(View.VISIBLE);
                               // WriteValues(jsonArrayData);
                            } else {
                                ll.setVisibility(View.GONE);
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

    private void WriteValues(JSONArray jsonArray) {

        ll.removeAllViews();

        float sp = 10;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);


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
        TextView I1 = new TextView(this);
        TextView J1 = new TextView(this);
        TextView L1 = new TextView(this);
        TextView M1 = new TextView(this);
        TextView N1 = new TextView(this);

        TextView O1 = new TextView(this);
        TextView P1 = new TextView(this);
        TextView Q1 = new TextView(this);
        TextView R1 = new TextView(this);
        TextView S1 = new TextView(this);

        A1.setText("S. No.");
        B1.setText("Payout\nNo.");
        C1.setText("From\nDate");
        D1.setText("To Date");
        E1.setText("ID\nNumber");
        F1.setText("Member\nName");
        G1.setText("Package\nName");
        H1.setText("Mobile\nNo.");
        I1.setText("Bonus");
        J1.setText("Brought Forward\nMatching Bonus\nBenefit for\nSponsors");
        L1.setText("Current Session\nNew Matching\nBonus Benefit\nfor Sponsors");
        M1.setText("Total Matching\nBonus Benefit\nfor Sponsors");
        N1.setText("Carry Forward\nMatching Bonus\nBenefit for\nSponsors");

      /*  O1.setText("Payment\nDate");
        P1.setText("Transaction\nNo.");
        Q1.setText("Payment\nRemarks");
        R1.setText("Closing\nBalance");
        S1.setText("Statement");*/

        A1.setPadding(px, px, px, px);
        B1.setPadding(px, px, px, px);
        C1.setPadding(px, px, px, px);
        D1.setPadding(px, px, px, px);
        E1.setPadding(px, px, px, px);
        F1.setPadding(px, px, px, px);
        G1.setPadding(px, px, px, px);
        H1.setPadding(px, px, px, px);
        I1.setPadding(px, px, px, px);
        J1.setPadding(px, px, px, px);
        L1.setPadding(px, px, px, px);
        L1.setPadding(px, px, px, px);
        M1.setPadding(px, px, px, px);
        N1.setPadding(px, px, px, px);
        O1.setPadding(px, px, px, px);
        P1.setPadding(px, px, px, px);
        Q1.setPadding(px, px, px, px);
        R1.setPadding(px, px, px, px);
        S1.setPadding(px, px, px, px);

        A1.setTypeface(typeface);
        B1.setTypeface(typeface);
        C1.setTypeface(typeface);
        D1.setTypeface(typeface);
        E1.setTypeface(typeface);
        F1.setTypeface(typeface);
        G1.setTypeface(typeface);
        H1.setTypeface(typeface);
        I1.setTypeface(typeface);
        J1.setTypeface(typeface);
        L1.setTypeface(typeface);
        M1.setTypeface(typeface);
        N1.setTypeface(typeface);
        O1.setTypeface(typeface);
        P1.setTypeface(typeface);
        Q1.setTypeface(typeface);
        R1.setTypeface(typeface);
        S1.setTypeface(typeface);

        A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        G1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        I1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        J1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        L1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        M1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        N1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        O1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        P1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        Q1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        R1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        S1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        A1.setGravity(Gravity.CENTER);
        B1.setGravity(Gravity.CENTER);
        C1.setGravity(Gravity.CENTER);
        D1.setGravity(Gravity.CENTER);
        E1.setGravity(Gravity.CENTER);
        F1.setGravity(Gravity.CENTER);
        G1.setGravity(Gravity.CENTER);
        H1.setGravity(Gravity.CENTER);
        I1.setGravity(Gravity.CENTER);
        J1.setGravity(Gravity.CENTER);
        L1.setGravity(Gravity.CENTER);
        M1.setGravity(Gravity.CENTER);
        N1.setGravity(Gravity.CENTER);
        O1.setGravity(Gravity.CENTER);
        P1.setGravity(Gravity.CENTER);
        Q1.setGravity(Gravity.CENTER);
        R1.setGravity(Gravity.CENTER);
        S1.setGravity(Gravity.CENTER);

        A1.setTextColor(Color.WHITE);
        B1.setTextColor(Color.WHITE);
        C1.setTextColor(Color.WHITE);
        D1.setTextColor(Color.WHITE);
        E1.setTextColor(Color.WHITE);
        F1.setTextColor(Color.WHITE);
        G1.setTextColor(Color.WHITE);
        H1.setTextColor(Color.WHITE);
        I1.setTextColor(Color.WHITE);
        J1.setTextColor(Color.WHITE);
        L1.setTextColor(Color.WHITE);
        M1.setTextColor(Color.WHITE);
        N1.setTextColor(Color.WHITE);
        O1.setTextColor(Color.WHITE);
        P1.setTextColor(Color.WHITE);
        Q1.setTextColor(Color.WHITE);
        R1.setTextColor(Color.WHITE);
        S1.setTextColor(Color.WHITE);

        row1.addView(A1);
        row1.addView(B1);
        row1.addView(C1);
        row1.addView(D1);
        row1.addView(E1);
        row1.addView(F1);
        row1.addView(G1);
        row1.addView(H1);
        row1.addView(I1);
        row1.addView(J1);
        row1.addView(L1);
        row1.addView(M1);
        row1.addView(N1);

     /* row1.addView(O1);
        row1.addView(P1);
        row1.addView(Q1);
        row1.addView(R1);
        row1.addView(S1);*/

        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(Color.parseColor("#cccccc"));

        ll.addView(row1);
        ll.addView(view);

        for (int i = 0; i < jsonArray.length(); i++) {
            try {

                JSONObject jobject = jsonArray.getJSONObject(i);

                final String str_1 = jobject.getString("SessId");
                final String str_2 = jobject.getString("FromDate");
                final String str_3 = jobject.getString("ToDate");
                final String str_4 = jobject.getString("idNo");
                final String str_5 = jobject.getString("MemName");
                final String str_6 = jobject.getString("Kitname");
                final String str_7 = jobject.getString("Mobl");
                final String str_8 = jobject.getString("Bonus");
                final String str_9 = jobject.getString("MatchingBonusBF");
                final String str_10 = jobject.getString("MatchingBonusNew");
                final String str_11 = jobject.getString("MatchingBonusTotal");
                final String str_12 = jobject.getString("MatchingBonusCF");

                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                row.setPadding(0, px, 0, px);

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
                TextView I = new TextView(this);
                TextView J = new TextView(this);
                TextView L = new TextView(this);
                TextView M = new TextView(this);
                TextView N = new TextView(this);

                TextView O = new TextView(this);
                TextView P = new TextView(this);
                TextView Q = new TextView(this);
                TextView RR = new TextView(this);
                final TextView S = new TextView(this);

                int a  = i + 1 ;
                A.setText("" + a);
                B.setText(str_1);
                C.setText(str_2);
                D.setText(str_3);
                E.setText(str_4);
                F.setText(str_5);
                G.setText(str_6);
                H.setText(str_7);
                I.setText(str_8);
                J.setText(str_9);
                L.setText(str_10);
                M.setText(str_11);
                N.setText(str_12);

             /* O.setText(TransDate);
                P.setText(TransNo);
                Q.setText(Pay_Remarks);
                RR.setText(ClosingBalance);
                S.setText("View");
                F.setId(payout_number);
                G.setId(payout_number);*/

                A.setPadding(px, px, px, px);
                B.setPadding(px, px, px, px);
                C.setPadding(px, px, px, px);
                D.setPadding(px, px, px, px);
                E.setPadding(px, px, px, px);
                F.setPadding(px, px, px, px);
                G.setPadding(px, px, px, px);
                H.setPadding(px, px, px, px);
                I.setPadding(px, px, px, px);
                J.setPadding(px, px, px, px);
                L.setPadding(px, px, px, px);
                L.setPadding(px, px, px, px);
                M.setPadding(px, px, px, px);
                N.setPadding(px, px, px, px);
                O.setPadding(px, px, px, px);
                P.setPadding(px, px, px, px);
                Q.setPadding(px, px, px, px);
                RR.setPadding(px, px, px, px);
                S.setPadding(px, px, px, px);

                A.setTypeface(typeface);
                B.setTypeface(typeface);
                C.setTypeface(typeface);
                D.setTypeface(typeface);
                E.setTypeface(typeface);
                F.setTypeface(typeface);
                G.setTypeface(typeface);
                H.setTypeface(typeface);
                I.setTypeface(typeface);
                J.setTypeface(typeface);
                L.setTypeface(typeface);
                M.setTypeface(typeface);
                N.setTypeface(typeface);
                O.setTypeface(typeface);
                P.setTypeface(typeface);
                Q.setTypeface(typeface);
                RR.setTypeface(typeface);
                S.setTypeface(typeface);

                A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                G.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                I.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                J.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                L.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                M.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                N.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                O.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                P.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                Q.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                RR.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                S.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                A.setGravity(Gravity.CENTER);
                B.setGravity(Gravity.CENTER);
                C.setGravity(Gravity.CENTER);
                D.setGravity(Gravity.CENTER);
                E.setGravity(Gravity.CENTER);
                F.setGravity(Gravity.CENTER);
                G.setGravity(Gravity.CENTER);
                H.setGravity(Gravity.CENTER);
                I.setGravity(Gravity.CENTER);
                J.setGravity(Gravity.CENTER);
                L.setGravity(Gravity.CENTER);
                M.setGravity(Gravity.CENTER);
                N.setGravity(Gravity.CENTER);
                O.setGravity(Gravity.CENTER);
                P.setGravity(Gravity.CENTER);
                Q.setGravity(Gravity.CENTER);
                RR.setGravity(Gravity.CENTER);
                S.setGravity(Gravity.CENTER);

                row.addView(A);
                row.addView(B);
                row.addView(C);
                row.addView(D);
                row.addView(E);
                row.addView(F);
                row.addView(G);
                row.addView(H);
                row.addView(I);
                row.addView(J);
                row.addView(L);
                row.addView(M);
                row.addView(N);

             /* row.addView(O);
                row.addView(P);
                row.addView(Q);
             // row.addView(RR);
                row.addView(S);*/

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

    private void executeStateRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(act);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToFillKitMaster, TAG);
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
                          //  getStateResult(jsonArrayData);

                            jsonarray_statelist = jsonArrayData;
                            spinState();
                        } else {
                            jsonarray_statelist = new JSONArray("[{\"SessID\":0,\"SessnName\":\"-- No Session Found --\"}]");
                            spinState();
                        }

                    } else {
                        jsonarray_statelist = new JSONArray("[{\"SessID\":0,\"SessnName\":\"-- No Session Found --\"}]");
                        spinState();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void spinState() {
        stackHelperList = new ArrayList<>();

        try {
            StackHelperState stackHelper1 = new StackHelperState();
            stackHelper1.setStateName("-- All --");
            stackHelper1.setCode("0");
            stackHelperList.add(stackHelper1);

            for (int i = 0; i < jsonarray_statelist.length(); i++) {
                JSONObject jsonobject = jsonarray_statelist.getJSONObject(i);
                Statecode = jsonobject.getString("SessID");
                Statename = jsonobject.getString("SessnName");

                StackHelperState stackHelper = new StackHelperState();
                stackHelper.setStateName(Statename);
                stackHelper.setCode(Statecode);
                stackHelperList.add(stackHelper);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<StackHelperState> dataAdapter = new ArrayAdapter<StackHelperState>(this, R.layout.sppiner_item, stackHelperList);
        dataAdapter.setDropDownViewResource(R.layout.sppiner_item);
        spinner_selectstate.setAdapter(dataAdapter);
    }
}
