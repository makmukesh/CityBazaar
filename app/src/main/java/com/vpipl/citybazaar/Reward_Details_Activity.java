package com.vpipl.citybazaar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.List;

public class Reward_Details_Activity extends AppCompatActivity {

    Activity act = Reward_Details_Activity.this;
    private String TAG = "Reward_Details_Activity";
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
            txt_heading.setText("Reward Details");

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
        executeRewardReportRequest();
    }

    private void executeRewardReportRequest() {
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
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToRewardDetails, TAG);

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
        Typeface typeface = ResourcesCompat.getFont(this, R.font.roboto);
        TableLayout ll = findViewById(R.id.displayLinear);

        TableRow row1 = new TableRow(this);
        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row1.setLayoutParams(lp1);
        row1.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

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
        TextView L1 = new TextView(this);
        TextView M1 = new TextView(this);
        TextView N1 = new TextView(this);
        TextView O1 = new TextView(this);
        TextView P1 = new TextView(this);
        TextView Q1 = new TextView(this);
        TextView R1 = new TextView(this);
        TextView S1 = new TextView(this);
        TextView T1 = new TextView(this);
        TextView U1 = new TextView(this);
        TextView V1 = new TextView(this);
        TextView W1 = new TextView(this);
        TextView X1 = new TextView(this);
        TextView Y1 = new TextView(this);
/*Claim Product	Claim Amount	Reward	Reward Name	Required PV	Time Limit	Status	Power Side PV	Weaker Side PV	Balance PV	Last Reward Date	Remarks	Claimed Status
Claimed Date	Reward Amount	Gross Incentive	TDS Amount	Admin Charge	Cheque Amount	Paid Status	Paid Date	Paid Narration*/
        A1.setText("Claim Product");
        B1.setText("Claim Amount");
        C1.setText("Reward");
        D1.setText("Reward Name");
        E1.setText("Required PV");
        F1.setText("Time Limit");
        G1.setText("Status");
        H1.setText("Power Side PV");
        I1.setText("Weaker Side PV");
        J1.setText("Balance PV");
        K1.setText("Last Reward Date");
        L1.setText("Remarks");
        M1.setText("Claimed Status");
        N1.setText("Claimed Date");
        O1.setText("Reward Amount");
        P1.setText("Gross Incentive");
        Q1.setText("TDS Amount");
        R1.setText("Admin Charge");
        S1.setText("Cheque Amount");
        T1.setText("Paid Status");
        U1.setText("Paid Date");
        V1.setText("Paid Narration");
        /*W1.setText("");
        X1.setText("Transaction No.");
        Y1.setText("Remarks");*/

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
        L1.setPadding(px, px, px, px);
        M1.setPadding(px, px, px, px);
        N1.setPadding(px, px, px, px);
        O1.setPadding(px, px, px, px);
        P1.setPadding(px, px, px, px);
        Q1.setPadding(px, px, px, px);
        R1.setPadding(px, px, px, px);
        S1.setPadding(px, px, px, px);
        T1.setPadding(px, px, px, px);
        U1.setPadding(px, px, px, px);
        V1.setPadding(px, px, px, px);
        W1.setPadding(px, px, px, px);
        X1.setPadding(px, px, px, px);
        Y1.setPadding(px, px, px, px);

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
        L1.setTypeface(typeface);
        M1.setTypeface(typeface);
        N1.setTypeface(typeface);
        O1.setTypeface(typeface);
        P1.setTypeface(typeface);
        Q1.setTypeface(typeface);
        R1.setTypeface(typeface);
        S1.setTypeface(typeface);
        T1.setTypeface(typeface);
        U1.setTypeface(typeface);
        V1.setTypeface(typeface);
        W1.setTypeface(typeface);
        X1.setTypeface(typeface);
        Y1.setTypeface(typeface);

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
        J1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        K1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        L1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        M1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        N1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        O1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        P1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        Q1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        R1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        S1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        T1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        U1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        V1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        W1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        X1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        Y1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);


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
        L1.setGravity(Gravity.CENTER);
        M1.setGravity(Gravity.CENTER);
        N1.setGravity(Gravity.CENTER);
        O1.setGravity(Gravity.CENTER);
        P1.setGravity(Gravity.CENTER);
        Q1.setGravity(Gravity.CENTER);
        R1.setGravity(Gravity.CENTER);
        S1.setGravity(Gravity.CENTER);
        T1.setGravity(Gravity.CENTER);
        U1.setGravity(Gravity.CENTER);
        V1.setGravity(Gravity.CENTER);
        W1.setGravity(Gravity.CENTER);
        X1.setGravity(Gravity.CENTER);
        Y1.setGravity(Gravity.CENTER);

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
        L1.setTextColor(Color.WHITE);
        M1.setTextColor(Color.WHITE);
        N1.setTextColor(Color.WHITE);
        O1.setTextColor(Color.WHITE);
        P1.setTextColor(Color.WHITE);
        Q1.setTextColor(Color.WHITE);
        R1.setTextColor(Color.WHITE);
        S1.setTextColor(Color.WHITE);
        T1.setTextColor(Color.WHITE);
        U1.setTextColor(Color.WHITE);
        V1.setTextColor(Color.WHITE);
        W1.setTextColor(Color.WHITE);
        X1.setTextColor(Color.WHITE);
        Y1.setTextColor(Color.WHITE);

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
        row1.addView(K1);
        row1.addView(L1);
        row1.addView(M1);
        row1.addView(N1);
        row1.addView(O1);
        row1.addView(P1);
        row1.addView(Q1);
        row1.addView(R1);
        row1.addView(S1);
        row1.addView(T1);
        row1.addView(U1);
        row1.addView(V1);
        /*row1.addView(W1);
        row1.addView(X1);
        row1.addView(Y1);*/

        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(Color.parseColor("#cccccc"));

        ll.addView(row1);
        ll.addView(view);

        for (int i = 0; i < jsonArray.length(); i++) {
            try {

                JSONObject jobject = jsonArray.getJSONObject(i);
                int i_1 = jobject.getInt("RewardID");
                final String str2 = jobject.getString("Reward");
                final String str3 = jobject.getString("ActualImgPath");
                final String str4 = jobject.getString("Reward");
                final String str5 = jobject.getString("CumPairAsRatio");
                final String str6 = jobject.getString("TimeLimit");
                final String str7 = jobject.getString("Status");
                final String str8 = jobject.getString("PwrLegRP");
                final String str9 = jobject.getString("WkrLegRP");
                final String str10 = jobject.getString("BalanceRPAsRatio");
                final String str11 = ((jobject.getString("AchvDate") == null) ? "" : (jobject.getString("AchvDate").equalsIgnoreCase("null")) ? "" : jobject.getString("AchvDate"));
                final String str12 = ((jobject.getString("Remarks") == null) ? "" : (jobject.getString("Remarks").equalsIgnoreCase("null")) ? "" : jobject.getString("Remarks"));
                final String str13 = ((jobject.getString("ClaimRewardStatus") == null) ? "" : (jobject.getString("ClaimRewardStatus").equalsIgnoreCase("null")) ? "" : jobject.getString("ClaimRewardStatus"));
                final String str14 = ((jobject.getString("RewardClaimDtTime") == null) ? "" : (jobject.getString("RewardClaimDtTime").equalsIgnoreCase("null")) ? "" : jobject.getString("RewardClaimDtTime"));
                final String str15 = ((jobject.getString("RewardAmount") == null) ? "" : (jobject.getString("RewardAmount").equalsIgnoreCase("null")) ? "" : jobject.getString("RewardAmount"));
                final String str16 = ((jobject.getString("NetIncome") == null) ? "" : (jobject.getString("NetIncome").equalsIgnoreCase("null")) ? "" : jobject.getString("NetIncome"));
                final String str17 = ((jobject.getString("TdsAmount") == null) ? "" : (jobject.getString("TdsAmount").equalsIgnoreCase("null")) ? "" : jobject.getString("TdsAmount"));
                final String str18 = ((jobject.getString("AdminCharge") == null) ? "" : (jobject.getString("AdminCharge").equalsIgnoreCase("null")) ? "" : jobject.getString("AdminCharge"));
                final String str19 = ((jobject.getString("ChqAmt") == null) ? "" : (jobject.getString("ChqAmt").equalsIgnoreCase("null")) ? "" : jobject.getString("ChqAmt"));
                final String str20 = ((jobject.getString("PaidStatus") == null) ? "" : (jobject.getString("PaidStatus").equalsIgnoreCase("null")) ? "" : jobject.getString("PaidStatus"));
                final String str21 = ((jobject.getString("PaidDate") == null) ? "" : (jobject.getString("PaidDate").equalsIgnoreCase("null")) ? "" : jobject.getString("PaidDate"));
                final String str22 = ((jobject.getString("PayRemarks") == null) ? "" : (jobject.getString("PayRemarks").equalsIgnoreCase("null")) ? "" : jobject.getString("PayRemarks"));
                /*final String str23 = jobject.getString("Payment Date");
                final String str24 = jobject.getString("Transaction No.");
                final String str25 = jobject.getString("Remarks");*/

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
                TextView K = new TextView(this);
                TextView L = new TextView(this);
                TextView M = new TextView(this);
                TextView N = new TextView(this);
                TextView O = new TextView(this);
                TextView P = new TextView(this);
                TextView Q = new TextView(this);
                TextView R = new TextView(this);
                TextView S = new TextView(this);
                TextView T = new TextView(this);
                final TextView U = new TextView(this);
                TextView V = new TextView(this);
                TextView W = new TextView(this);
                final TextView X = new TextView(this);
                final TextView Y = new TextView(this);

                float density = getResources().getDisplayMetrics().density;
                LinearLayout.LayoutParams imageparams = new LinearLayout.LayoutParams((int) (35 * density), (int) (35 * density));
                imageparams.setMargins(0, (int) (3 * density), 0, 0);

                final LinearLayout LL = new LinearLayout(getApplicationContext());
                LL.setOrientation(LinearLayout.VERTICAL);
                LL.setMinimumHeight((int) (35 * density));
                LL.setMinimumWidth((int) (35 * density));

                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(imageparams);
                imageView.setPadding((int) (2 * density),(int) (2 * density),(int) (2 * density),(int) (2 * density));
                //    imageView.setScaleType(ImageView.ScaleType.CENTER);
                //imageView.setImageDrawable(getResources().getDrawable(R.drawable.genealogylpic));
                AppUtils.loadProductImage(act , "" + str3 , imageView);
                imageparams.gravity=Gravity.CENTER;
                LL.addView(imageView);

               // A.setText("" + i_1);
                A.setText("");
                B.setText("");
               // C.setText(str3);
                D.setText(str4);
                E.setText(str5);
                F.setText(str6);
                G.setText(str7);
                H.setText(str8);
                I.setText(str9);
                J.setText(str10);
                K.setText(str11);
                L.setText(str12);
                M.setText(str13);
                N.setText(str14);
                O.setText(str15);
                P.setText(str16);
                Q.setText(str17);
                R.setText(str18);
                S.setText(str19);
                T.setText(str20);
                U.setText(str21);
                V.setText(str22);
                 /*W.setText(str23);
                X.setText(str24);
                Y.setText(str25);*/

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
                L.setPadding(px, px, px, px);
                M.setPadding(px, px, px, px);
                N.setPadding(px, px, px, px);
                O.setPadding(px, px, px, px);
                P.setPadding(px, px, px, px);
                Q.setPadding(px, px, px, px);
                R.setPadding(px, px, px, px);
                S.setPadding(px, px, px, px);
                T.setPadding(px, px, px, px);
                U.setPadding(px, px, px, px);
                V.setPadding(px, px, px, px);
                W.setPadding(px, px, px, px);
                X.setPadding(px, px, px, px);
                Y.setPadding(px, px, px, px);
                
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
                L.setTextColor(Color.BLACK);
                M.setTextColor(Color.BLACK);
                N.setTextColor(Color.BLACK);
                O.setTextColor(Color.BLACK);
                P.setTextColor(Color.BLACK);
                Q.setTextColor(Color.BLACK);
                R.setTextColor(Color.BLACK);
                S.setTextColor(Color.BLACK);
                T.setTextColor(Color.BLACK);
                U.setTextColor(Color.BLACK);
                V.setTextColor(Color.BLACK);
                W.setTextColor(Color.BLACK);
                X.setTextColor(Color.BLACK);
                Y.setTextColor(Color.BLACK);
                
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
                L.setTypeface(typeface);
                M.setTypeface(typeface);
                N.setTypeface(typeface);
                O.setTypeface(typeface);
                P.setTypeface(typeface);
                Q.setTypeface(typeface);
                R.setTypeface(typeface);
                S.setTypeface(typeface);
                T.setTypeface(typeface);
                U.setTypeface(typeface);
                V.setTypeface(typeface);
                W.setTypeface(typeface);
                X.setTypeface(typeface);
                Y.setTypeface(typeface);

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
                L.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                M.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                N.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                O.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                P.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                Q.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                R.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                S.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                T.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                U.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                V.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                W.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                X.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                Y.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

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
                L.setGravity(Gravity.CENTER);
                M.setGravity(Gravity.CENTER);
                N.setGravity(Gravity.CENTER);
                O.setGravity(Gravity.CENTER);
                P.setGravity(Gravity.CENTER);
                Q.setGravity(Gravity.CENTER);
                R.setGravity(Gravity.CENTER);
                S.setGravity(Gravity.CENTER);
                T.setGravity(Gravity.CENTER);
                U.setGravity(Gravity.CENTER);
                V.setGravity(Gravity.CENTER);
                W.setGravity(Gravity.CENTER);
                X.setGravity(Gravity.CENTER);
                Y.setGravity(Gravity.CENTER);

                row.addView(A);
                row.addView(B);
               // row.addView(C);
                row.addView(LL);
                row.addView(D);
                row.addView(E);
                row.addView(F);
                row.addView(G);
                row.addView(H);
                row.addView(I);
                row.addView(J);
                row.addView(K);
                row.addView(L);
                row.addView(M);
                row.addView(N);
                row.addView(O);
                row.addView(P);
                row.addView(Q);
                row.addView(R);
                row.addView(S);
                row.addView(T);
                row.addView(U);
                row.addView(V);
                 /*row.addView(W);
                row.addView(X);
                row.addView(Y);*/

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
