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
import java.util.ArrayList;
import java.util.List;

public class Daily_Incentive_report_Activity extends AppCompatActivity {

    Activity act = Daily_Incentive_report_Activity.this;
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
            TextView  txt_heading = findViewById(R.id.txt_heading);
            txt_heading.setText("Daily Incentive");

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
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodDailyIncentive, TAG);

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

        float sp = 10;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

        TableLayout ll = findViewById(R.id.displayLinear);
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
        TextView K1 = new TextView(this);

     //   Statement	Payout No	Matching Income	Monthly Bonus Income	Gross Incentive	TDS Amount	Admin Charge	Previous Balance	Net Incentive	Closing Balance
        A1.setText("Statement");
        B1.setText("Payout No");
        C1.setText("Matching Income");
        D1.setText("Monthly Bonus Income");
        E1.setText("Gross Incentive");
        F1.setText("TDS Amount");
        G1.setText("Admin Charge");
        H1.setText("Previous Balance");
        I1.setText("Net Incentive");
        J1.setText("Closing Balance");
      //  K1.setText("Closing Balance Reason");

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
        K1.setPadding(px, px, px, px);

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
        K1.setTypeface(typeface);

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
        K1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

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
        K1.setGravity(Gravity.CENTER);

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
        K1.setTextColor(Color.WHITE);

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
       // row1.addView(K1);

        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(Color.parseColor("#cccccc"));

        ll.addView(row1);
        ll.addView(view);

        for (int i = 0; i < jsonArray.length(); i++) {
            try {

                JSONObject jobject = jsonArray.getJSONObject(i);
/*{"Payout No":20200810,"Matching Income":0.00,"Monthly Bonus Income":100.00,"Gross Incentive":100.00,"TDS Amount":5.00,"Admin Charge":5.00,"Previous Balance":0.00,"Net Incentive":90.00,"Closing Balance":0.00}*/
                final int PayoutNo = jobject.getInt("Payout No");
                final String str1 = jobject.getString("Payout No");
                final String str2 = jobject.getString("Matching Income");
                final String str3 = jobject.getString("Monthly Bonus Income");
                final String str4 = jobject.getString("Gross Incentive");
                final String str5 = jobject.getString("TDS Amount");
                final String str6 = jobject.getString("Admin Charge");
                final String str7 = jobject.getString("Previous Balance");
                final String str8 = jobject.getString("Net Incentive");
                final String str9 = jobject.getString("Closing Balance");
              //  final String str10 = jobject.getString("Closing Balance Reason");

                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                row.setPadding(0, px, 0, px);

                if (i % 2 == 0)
                    row.setBackgroundColor(Color.WHITE);
                else
                    row.setBackgroundColor(Color.parseColor("#dddddd"));

                final TextView A = new TextView(this);
                final TextView B = new TextView(this);
                TextView C = new TextView(this);
                TextView D = new TextView(this);
                TextView E = new TextView(this);
                TextView F = new TextView(this);
                final TextView G = new TextView(this);
                TextView H = new TextView(this);
                TextView I = new TextView(this);
                TextView J = new TextView(this);
                TextView K = new TextView(this);

                A.setText("View");
                B.setText(str1);
                C.setText(str2);
                D.setText(str3);
                E.setText(str4);
                F.setText(str5);
                G.setText(str6);
                H.setText(str7);
                I.setText(str8);
                J.setText(str9);
              // K.setText(str10);
                B.setId(PayoutNo);

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
                K.setGravity(Gravity.CENTER);

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
                K.setPadding(px, px, px, px);

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
                K.setTextColor(Color.BLACK);

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
                K.setTypeface(typeface);

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
                K.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                params.setMargins(px, 0, px, 0);

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
              //  row.addView(K);

                A.setTextColor(getResources().getColor(R.color.black));
                TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                params1.setMargins(px, 0, px, 0);
                A.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_gray));
                A.setLayoutParams(params1);
                A.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        byte[] data1 = new byte[0];
                        try {
                            data1 = AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "").getBytes("UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        String encryptedFormno = Base64.encodeToString(data1, Base64.DEFAULT);

                        String str = "" + B.getId();
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
