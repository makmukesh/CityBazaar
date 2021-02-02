package com.vpipl.citybazaar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Daily_Incentive_Detailed_report_Activity extends AppCompatActivity {

    Activity act = Daily_Incentive_Detailed_report_Activity.this;
    private String TAG = "Daily_Incentive_report_Activity";
    private TableLayout displayLinear;

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);

        img_login_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(act, Login_New_Activity.class));
                else
                    AppUtils.showDialogSignOut(act);
            }
        });
        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout));
        else
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.ic_login_user));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_incentive_report);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            AppUtils.changeStatusBarColor(act);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            displayLinear = findViewById(R.id.displayLinear);
            TextView txt_heading = findViewById(R.id.txt_heading);
            txt_heading.setText("Daily Incentive Detail Report");

            if (AppUtils.isNetworkAvailable(this)) {
                continueapp();
            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }

    public void continueapp() {
        executeWeeklyReportRequest();
    }

    private void executeWeeklyReportRequest() {
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
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodDailyIncentiveDetailReport, TAG);

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
                                WriteValues(jsonArrayData);
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

    private void WriteValues(JSONArray jsonArray) {

        DecimalFormat df = new DecimalFormat("#.###");

        float sp = 10;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

        TableLayout ll = findViewById(R.id.displayLinear);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.roboto);

        TableRow row1 = new TableRow(this);
        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row1.setLayoutParams(lp1);
        row1.setBackgroundColor(getResources().getColor(R.color.color_136D98));

        TextView AA1 = new TextView(this);
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
        TextView T1 = new TextView(this);
/*Payout No	Left New Business	Right New Business	Left Total Business	Right Total Business	Power Side Business	Weaker Side Business	Matched Business
Matching Income	Monthly Bonus Income	Gross Incentive	TDS Amount	Admin Charge	Previous Balance	Net Incentive	Closing Balance  */
        //AA1.setText("Statement");
        A1.setText("Payout\nNo");
        B1.setText("Left New\nBusiness");
        C1.setText("Right New\nBusiness");
        D1.setText("Left Total\nBusiness");
        E1.setText("Right Total\nBusiness");
        F1.setText("Power Side\nBusiness");
        G1.setText("Weaker Side\nBusiness");
        H1.setText("Matched\nBusiness");
        I1.setText("Matching\nIncome");
        J1.setText("Monthly Bonus\nIncome");
        L1.setText("Gross\nIncentive");
        M1.setText("TDS\nAmount");
        N1.setText("Admin\nCharge");
        O1.setText("Previous\nBalance");
        P1.setText("Net\nIncentive");
        Q1.setText("Closing\nBalance");
        /*R1.setText("Net\nIncentive");
        S1.setText("Closing\nBalance");
        T1.setText("Closing\nBalance Reason");*/

        AA1.setPadding(px, px, px, px);
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
        T1.setPadding(px, px, px, px);

        AA1.setTypeface(typeface);
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
        T1.setTypeface(typeface);

        AA1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
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
        T1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        AA1.setGravity(Gravity.CENTER);
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
        T1.setGravity(Gravity.CENTER);

        AA1.setTextColor(Color.WHITE);
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
        T1.setTextColor(Color.WHITE);

       // row1.addView(AA1);
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
        row1.addView(O1);
        row1.addView(P1);
        row1.addView(Q1);
        /*row1.addView(R1);
        row1.addView(S1);
        row1.addView(T1);*/


        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(Color.parseColor("#cccccc"));

        ll.addView(row1);
        ll.addView(view);

        for (int i = 0; i < jsonArray.length(); i++) {
            try {

                JSONObject jobject = jsonArray.getJSONObject(i);
/*{"Payout No":20200810,"Left New Business":2.00,"Right New Business":0.00,"Left Total Business":5260.00,
"Right Total Business":0.00,"Power Side Business":5260.00,"Weaker Side Business":0.00,"Matched Business":0.00,
"Matching Income":0.00,"Monthly Bonus Income":100.00,"Gross Incentive":100.00,"TDS Amount":5.00,
"Admin Charge":5.00,"Previous Balance":0.00,"Net Incentive":90.00,"Closing Balance":0.00}*/
                int payout_number = jobject.getInt("Payout No");
//String.format("%.2f",jobject.getString("Matching Bonus"));
                final String str1 = jobject.getString("Left New Business");
                final String str2 = jobject.getString("Right New Business");
                final String str3 = jobject.getString("Left Total Business");
                final String str4 = jobject.getString("Right Total Business");
                final String str5 = jobject.getString("Power Side Business");
                final String str6 = jobject.getString("Weaker Side Business");
                final String str7 = jobject.getString("Matched Business");
                final String str8 = jobject.getString("Matching Income");
                final String str9 = jobject.getString("Monthly Bonus Income");
                final String str10 = jobject.getString("Gross Incentive");
                final String str11 = jobject.getString("TDS Amount");
                final String str12 = jobject.getString("Admin Charge");
                final String str13 = jobject.getString("Previous Balance");
                final String str14 = jobject.getString("Net Incentive");
                final String str15 = jobject.getString("Closing Balance");
                /*final String str16 = jobject.getString("Net Incentive");
                final String str17 = jobject.getString("Closing Balance");
                final String str18 = jobject.getString("Closing Balance Reason");*/

                StringBuilder sb = new StringBuilder(str1);

                int ii = 0;
                while ((ii = sb.indexOf(" ", ii + 11)) != -1) {
                    sb.replace(ii, ii + 1, "\n");
                }

                StringBuilder sb1 = new StringBuilder(str2);

                int ii1 = 0;
                while ((ii1 = sb1.indexOf(" ", ii1 + 11)) != -1) {
                    sb1.replace(ii1, ii1 + 1, "\n");
                }

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
                final TextView T = new TextView(this);
                final TextView U = new TextView(this);

                A.setText("" + payout_number);
                B.setText(sb.toString());
                C.setText(sb1.toString());
                D.setText(str3);
                E.setText(str4);
                F.setText(str5);
                G.setText(str6);
                H.setText(str7);
                I.setText(str8);
                J.setText(str9);
                L.setText(str10);
                M.setText(str11);
                N.setText(str12);
                O.setText(str13);
                P.setText(str14);
                Q.setText(str15);
                /*RR.setText(str16);
                S.setText(str17);
                T.setText(str18);
                U.setText("View");
                U.setId(payout_number);*/

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
                T.setPadding(px, px, px, px);
                U.setPadding(px, px, px, px);

                U.setTextColor(Color.BLACK);
                A.setTextColor(Color.BLACK);
                B.setTextColor(Color.BLACK);
                C.setTextColor(Color.BLACK);
                D.setTextColor(Color.BLACK);
                E.setTextColor(Color.BLACK);
                F.setTextColor(Color.BLACK);
                G.setTextColor(Color.BLACK);
                H.setTextColor(Color.BLACK);
                I.setTextColor(Color.BLACK);
                J.setTextColor(Color.BLACK);
                L.setTextColor(Color.BLACK);
                M.setTextColor(Color.BLACK);
                N.setTextColor(Color.BLACK);
                O.setTextColor(Color.BLACK);
                P.setTextColor(Color.BLACK);
                Q.setTextColor(Color.BLACK);
                RR.setTextColor(Color.BLACK);
                S.setTextColor(Color.BLACK);
                T.setTextColor(Color.BLACK);
                
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
                T.setTypeface(typeface);
                U.setTypeface(typeface);

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
                T.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                U.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

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
                T.setGravity(Gravity.CENTER);
                U.setGravity(Gravity.CENTER);
                U.setTextColor(getResources().getColor(R.color.black));

                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                params.setMargins(px, 0, px, 0);
                U.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_gray));
                U.setLayoutParams(params);
                U.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        byte[] data1 = new byte[0];
                        try {
                            data1 = AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "").getBytes("UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        String encryptedFormno = Base64.encodeToString(data1, Base64.DEFAULT);

                        String str = "" + U.getId();
                        byte[] data2 = new byte[0];
                        try {
                            data2 = str.getBytes("UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        String encryptedpayoutno = Base64.encodeToString(data2, Base64.DEFAULT);

                        String path = AppUtils.ViewDailyStatementURL() + encryptedpayoutno + "&FNo=" + encryptedFormno;
                        Log.e("Path", path);
                        startActivity(new Intent(act, Incentive_Statement_Activity.class).putExtra("URL", path));
                    }
                });

              //  row.addView(U);
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
                row.addView(O);
                row.addView(P);
                row.addView(Q);
                /*row.addView(RR);
                row.addView(S);
                row.addView(T);*/

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

}
