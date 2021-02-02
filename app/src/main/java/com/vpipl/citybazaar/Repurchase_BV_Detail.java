package com.vpipl.citybazaar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Repurchase_BV_Detail extends AppCompatActivity {

    Activity act = Repurchase_BV_Detail.this;
    private String TAG = "Repurchase_BV_Detail";
    private EditText txt_from_joining, txt_to_joining;
    private TextView txt_self_bv;
    private TextView txt_team_bv;
    private TextView txt_total_bv;
    private TextView txt_count;
    private Button btn_proceed;
    private Button btn_load_more;

    private TableLayout displayLinear1, displayLinear2, displayLinear3;
    private HorizontalScrollView HSV1, HSV2, HSV3;
    private RadioGroup rg_view_detail_for;
    private RadioButton rb_self, rb_left, rb_right, rb_all;

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

                if (whichdate.equalsIgnoreCase("txt_from_joining"))
                    txt_from_joining.setText(sdf.format(myCalendar.getTime()));
                else if (whichdate.equalsIgnoreCase("txt_to_joining"))
                    txt_to_joining.setText(sdf.format(myCalendar.getTime()));

            } else {

                AppUtils.alertDialog(act, "Selected Date Can't be After today");
            }
        }
    };
    private int TopRows = 25;

    private DatePickerDialog datePickerDialog;

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
        setContentView(R.layout.activity_repurchase_bv_detail);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        AppUtils.changeStatusBarColor(act);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        txt_from_joining = findViewById(R.id.txt_from_joining);
        txt_to_joining = findViewById(R.id.txt_to_joining);
        txt_self_bv = findViewById(R.id.txt_self_bv);
        txt_team_bv = findViewById(R.id.txt_team_bv);

        txt_total_bv = findViewById(R.id.txt_total_bv);

        txt_count = findViewById(R.id.txt_count);

        btn_proceed = findViewById(R.id.btn_proceed);
        btn_load_more = findViewById(R.id.btn_load_more);

        displayLinear1 = findViewById(R.id.displayLinear1);
        displayLinear2 = findViewById(R.id.displayLinear2);
        displayLinear3 = findViewById(R.id.displayLinear3);

        HSV1 = findViewById(R.id.HSV1);
        HSV2 = findViewById(R.id.HSV2);
        HSV3 = findViewById(R.id.HSV3);

        rg_view_detail_for = findViewById(R.id.rg_view_detail_for);
        rb_self = findViewById(R.id.rb_self);
        rb_left = findViewById(R.id.rb_left);
        rb_right = findViewById(R.id.rb_right);
        rb_all = findViewById(R.id.rb_all);

        btn_load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TopRows = TopRows + 25;
                createRepurchaseBvDetailMy();
            }
        });

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TopRows = 25;
                createRepurchaseBvDetailMy();
            }
        });

        myCalendar = Calendar.getInstance();
        //  sdf = new SimpleDateFormat("dd MMM yyyy");
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        txt_to_joining.setText(sdf.format(myCalendar.getTime()));

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        txt_from_joining.setText(sdf.format(c.getTime()));

       /* txt_from_joining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                whichdate = "txt_from_joining";
                showdatePicker();
            }
        });


        txt_to_joining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                whichdate = "txt_to_joining";
                showdatePicker();
            }
        });*/

        if (AppUtils.isNetworkAvailable(this)) {
            executeLoginRequest();
        } else {
            AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
        }


        txt_from_joining.addTextChangedListener(new TextWatcher() {
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
                    txt_from_joining.setText(current);
                    txt_from_joining.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        txt_to_joining.addTextChangedListener(new TextWatcher() {
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
                    txt_to_joining.setText(current);
                    txt_to_joining.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
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
        createRepurchaseBvDetailMy();
    }

    private void createRepurchaseBvDetailMy() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);
        findViewById(R.id.HSV1).setVisibility(View.GONE);
        findViewById(R.id.HSV2).setVisibility(View.GONE);
        findViewById(R.id.HSV3).setVisibility(View.GONE);
        findViewById(R.id.LL_count).setVisibility(View.GONE);

        String FromJD = "", ToDate = "";
        if (!txt_from_joining.getText().toString().equalsIgnoreCase("")
                && !txt_from_joining.getText().toString().equalsIgnoreCase("DD/MM/YYYY")) {
            FromJD = AppUtils.getFormatDate1(txt_from_joining.getText().toString());
        }
        if (!txt_to_joining.getText().toString().equalsIgnoreCase("")
                && !txt_to_joining.getText().toString().equalsIgnoreCase("DD/MM/YYYY")) {
            ToDate = AppUtils.getFormatDate1(txt_to_joining.getText().toString());
        }

        int selectedId = rg_view_detail_for.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        String view_detail_for = radioButton.getText().toString().trim();

        String Type = "0";
        /* Type=0(Self)  Type=1(Left)  Type=2(Right)   Type=3(ALL) */

        if (view_detail_for.equalsIgnoreCase("Self"))
            Type = "0";
        else if (view_detail_for.equalsIgnoreCase("Left"))
            Type = "1";
        else if (view_detail_for.equalsIgnoreCase("Right"))
            Type = "2";
        else if (view_detail_for.equalsIgnoreCase("All"))
            Type = "3";

        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
        // postParameters.add(new BasicNameValuePair("TopRows", "" + TopRows));
        postParameters.add(new BasicNameValuePair("FromJD", "" + FromJD));
        postParameters.add(new BasicNameValuePair("ToJD", "" + ToDate));
        postParameters.add(new BasicNameValuePair("Type", "" + Type));
        executeRepurchaseBvDetailMy(postParameters);
    }

    private void executeRepurchaseBvDetailMy(final List postparameters) {
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
                            response = AppUtils.callWebServiceWithMultiParam(act, postparameters, QueryUtils.methodMyRepurchaseBVDetailNew, TAG);

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

                            if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                JSONArray jsonArrayBVDetailsReports = jsonObject.getJSONArray("BVDetailsReports");
                                JSONArray jsonArraySelfSBVDetails = jsonObject.getJSONArray("SelfSBVDetails");
                                JSONArray jsonArrayLeftSVBDetails = jsonObject.getJSONArray("LeftSBVDetails");
                                JSONArray jsonArrayRightSBVDetails = jsonObject.getJSONArray("RightSBVDetails");

                                if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                    if (jsonArraySelfSBVDetails.length() > 0) {
                                        WriteValues1(jsonArrayBVDetailsReports, jsonArraySelfSBVDetails);
                                        findViewById(R.id.txt1).setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        findViewById(R.id.txt1).setVisibility(View.GONE);
                                    }
                                    if (jsonArrayLeftSVBDetails.length() > 0) {
                                        WriteValues2(jsonArrayBVDetailsReports, jsonArrayLeftSVBDetails);
                                        findViewById(R.id.txt2).setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        findViewById(R.id.txt2).setVisibility(View.GONE);
                                    }
                                    if (jsonArrayRightSBVDetails.length() > 0) {
                                        WriteValues3(jsonArrayBVDetailsReports, jsonArrayRightSBVDetails);
                                        findViewById(R.id.txt3).setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        findViewById(R.id.txt3).setVisibility(View.GONE);
                                    }
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

    private void WriteValues1(final JSONArray count, final JSONArray jarray) {
        try {

            findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

            String text = "(Showing " + jarray.length() + " records)";
            txt_count.setText(text);

            if (jarray.length() > 0) {
                findViewById(R.id.HSV1).setVisibility(View.VISIBLE);
                // findViewById(R.id.LL_count).setVisibility(View.VISIBLE);
            }

            float sp = 8;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

            if (count.length() > 0) {
                JSONObject jsonObject = count.getJSONObject(0);
                DecimalFormat df = new DecimalFormat("#.###");

                txt_self_bv.setText(df.format(jsonObject.getDouble("SelfBV")));
                txt_team_bv.setText(df.format(jsonObject.getDouble("LeftBv")));
                txt_total_bv.setText(df.format(jsonObject.getDouble("RightBV")));
            }
            TableLayout ll = findViewById(R.id.displayLinear1);
            ll.removeAllViews();

            Typeface typeface = ResourcesCompat.getFont(this, R.font.roboto);

            TableRow row1 = new TableRow(this);

            TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row1.setLayoutParams(lp1);
            row1.setBackgroundColor(getResources().getColor(R.color.color_136D98));

//            TextView A1 = new TextView(this);
//            TextView B1 = new TextView(this);
            TextView C1 = new TextView(this);
            TextView D1 = new TextView(this);
            TextView E1 = new TextView(this);
            TextView F1 = new TextView(this);
            TextView G1 = new TextView(this);
            TextView H1 = new TextView(this);

//            A1.setText("ID No.");
//            B1.setText("Member Name");
            C1.setText("Id Number");
            D1.setText("Member Name");
            E1.setText("Bill Number");
            F1.setText("Bill Date");
            G1.setText("BV");
            //  H1.setText("Remarks");

            C1.setTypeface(typeface);
            D1.setTypeface(typeface);
            E1.setTypeface(typeface);
            F1.setTypeface(typeface);
            G1.setTypeface(typeface);
            H1.setTypeface(typeface);

//            A1.setPadding(px, px, px, px);
//            B1.setPadding(px, px, px, px);
            C1.setPadding(px, px, px, px);
            D1.setPadding(px, px, px, px);
            E1.setPadding(px, px, px, px);
            F1.setPadding(px, px, px, px);
            G1.setPadding(px, px, px, px);
            H1.setPadding(px, px, px, px);

//            A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//            B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            G1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

//            A1.setGravity(Gravity.CENTER);
//            B1.setGravity(Gravity.CENTER);
            C1.setGravity(Gravity.CENTER);
            D1.setGravity(Gravity.CENTER);
            E1.setGravity(Gravity.CENTER);
            F1.setGravity(Gravity.CENTER);
            G1.setGravity(Gravity.CENTER);
            H1.setGravity(Gravity.CENTER);

//            A1.setTextColor(Color.WHITE);
//            B1.setTextColor(Color.WHITE);
            C1.setTextColor(Color.WHITE);
            D1.setTextColor(Color.WHITE);
            E1.setTextColor(Color.WHITE);
            F1.setTextColor(Color.WHITE);
            G1.setTextColor(Color.WHITE);
            H1.setTextColor(Color.WHITE);

//            row1.addView(A1);
//            row1.addView(B1);
            row1.addView(C1);
            row1.addView(D1);
            row1.addView(E1);
            row1.addView(F1);
            row1.addView(G1);
            //  row1.addView(H1);

            View view = new View(this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            view.setBackgroundColor(Color.parseColor("#cccccc"));

            ll.addView(row1);
            ll.addView(view);

            for (int i = 0; i < jarray.length(); i++) {
                try {
                    JSONObject jobject = jarray.getJSONObject(i);

//                    String member_id = jobject.getString("IdNo");
//                    String MemName = WordUtils.capitalizeFully(jobject.getString("MemName"));
                    String Idno = jobject.getString("Idno");
                    String MemName = jobject.getString("MemName");
                    String BillNo = jobject.getString("BillNo");
                    String Billdate = jobject.getString("TransDate");
                    String BV = jobject.getString("BV");
                    //  String Remarks = WordUtils.capitalizeFully(jobject.getString("Remarks"));

                   /* StringBuilder sb = new StringBuilder(Remarks);

                    int ii = 0;
                    while ((ii = sb.indexOf(" ", ii + 11)) != -1) {
                        sb.replace(ii, ii + 1, "\n");
                    }*/

                    TableRow row = new TableRow(this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);

                    if (i % 2 == 0)
                        row.setBackgroundColor(Color.WHITE);
                    else
                        row.setBackgroundColor(Color.parseColor("#dddddd"));


//                    TextView A = new TextView(this);
//                    TextView B = new TextView(this);
                    TextView C = new TextView(this);
                    TextView D = new TextView(this);
                    TextView E = new TextView(this);
                    TextView F = new TextView(this);
                    TextView G = new TextView(this);
                    TextView H = new TextView(this);

//                    A.setText(member_id);
//                    B.setText(MemName);
                    C.setText(Idno);
                    D.setText(MemName);
                    E.setText(BillNo);
                    F.setText(Billdate);
                    G.setText(BV);
                    //H.setText(sb.toString());

                    C.setTypeface(typeface);
                    D.setTypeface(typeface);
                    E.setTypeface(typeface);
                    F.setTypeface(typeface);
                    G.setTypeface(typeface);
                    H.setTypeface(typeface);

//                    A.setGravity(Gravity.CENTER);
//                    B.setGravity(Gravity.CENTER);
                    C.setGravity(Gravity.CENTER);
                    D.setGravity(Gravity.CENTER);
                    E.setGravity(Gravity.CENTER);
                    F.setGravity(Gravity.CENTER);
                    G.setGravity(Gravity.CENTER);
                    H.setGravity(Gravity.CENTER);

//                    A.setPadding(px, px, px, px);
//                    B.setPadding(px, px, px, px);
                    C.setPadding(px, px, px, px);
                    D.setPadding(px, px, px, px);
                    E.setPadding(px, px, px, px);
                    F.setPadding(px, px, px, px);
                    G.setPadding(px, px, px, px);
                    H.setPadding(px, px, px, px);

//                    A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                    B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    G.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

//                    row.addView(A);
//                    row.addView(B);
                    row.addView(C);
                    row.addView(D);
                    row.addView(E);
                    row.addView(F);
                    row.addView(G);
                    //  row.addView(H);

                    View view_one = new View(this);
                    view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                    view.setBackgroundColor(Color.parseColor("#cccccc"));

                    ll.addView(row);
                    ll.addView(view_one);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void WriteValues2(final JSONArray count, final JSONArray jarray) {
        try {

            findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

            String text = "(Showing " + jarray.length() + " records)";
            txt_count.setText(text);

            if (jarray.length() > 0) {
                findViewById(R.id.HSV2).setVisibility(View.VISIBLE);
                // findViewById(R.id.LL_count).setVisibility(View.VISIBLE);
            }

            float sp = 8;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

            if (count.length() > 0) {
                JSONObject jsonObject = count.getJSONObject(0);
                DecimalFormat df = new DecimalFormat("#.###");

                txt_self_bv.setText(df.format(jsonObject.getDouble("SelfBV")));
                txt_team_bv.setText(df.format(jsonObject.getDouble("LeftBv")));
                txt_total_bv.setText(df.format(jsonObject.getDouble("RightBV")));
            }
            TableLayout ll = findViewById(R.id.displayLinear2);
            ll.removeAllViews();

            Typeface typeface = ResourcesCompat.getFont(this, R.font.roboto);

            TableRow row1 = new TableRow(this);

            TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row1.setLayoutParams(lp1);
            row1.setBackgroundColor(getResources().getColor(R.color.color_136D98));

//            TextView A1 = new TextView(this);
//            TextView B1 = new TextView(this);
            TextView C1 = new TextView(this);
            TextView D1 = new TextView(this);
            TextView E1 = new TextView(this);
            TextView F1 = new TextView(this);
            TextView G1 = new TextView(this);
            TextView H1 = new TextView(this);

//            A1.setText("ID No.");
//            B1.setText("Member Name");
            C1.setText("Id Number");
            D1.setText("Member Name");
            E1.setText("Bill Number");
            F1.setText("Bill Date");
            G1.setText("BV");
            //  H1.setText("Remarks");

            C1.setTypeface(typeface);
            D1.setTypeface(typeface);
            E1.setTypeface(typeface);
            F1.setTypeface(typeface);
            G1.setTypeface(typeface);
            H1.setTypeface(typeface);

//            A1.setPadding(px, px, px, px);
//            B1.setPadding(px, px, px, px);
            C1.setPadding(px, px, px, px);
            D1.setPadding(px, px, px, px);
            E1.setPadding(px, px, px, px);
            F1.setPadding(px, px, px, px);
            G1.setPadding(px, px, px, px);
            H1.setPadding(px, px, px, px);

//            A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//            B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            G1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

//            A1.setGravity(Gravity.CENTER);
//            B1.setGravity(Gravity.CENTER);
            C1.setGravity(Gravity.CENTER);
            D1.setGravity(Gravity.CENTER);
            E1.setGravity(Gravity.CENTER);
            F1.setGravity(Gravity.CENTER);
            G1.setGravity(Gravity.CENTER);
            H1.setGravity(Gravity.CENTER);

//            A1.setTextColor(Color.WHITE);
//            B1.setTextColor(Color.WHITE);
            C1.setTextColor(Color.WHITE);
            D1.setTextColor(Color.WHITE);
            E1.setTextColor(Color.WHITE);
            F1.setTextColor(Color.WHITE);
            G1.setTextColor(Color.WHITE);
            H1.setTextColor(Color.WHITE);

//            row1.addView(A1);
//            row1.addView(B1);
            row1.addView(C1);
            row1.addView(D1);
            row1.addView(E1);
            row1.addView(F1);
            row1.addView(G1);
            //  row1.addView(H1);

            View view = new View(this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            view.setBackgroundColor(Color.parseColor("#cccccc"));

            ll.addView(row1);
            ll.addView(view);

            for (int i = 0; i < jarray.length(); i++) {
                try {
                    JSONObject jobject = jarray.getJSONObject(i);

//                    String member_id = jobject.getString("IdNo");
//                    String MemName = WordUtils.capitalizeFully(jobject.getString("MemName"));
                    String Idno = jobject.getString("Idno");
                    String MemName = jobject.getString("MemName");
                    String BillNo = jobject.getString("BillNo");
                    String Billdate = jobject.getString("TransDate");
                    String BV = jobject.getString("BV");
                    //  String Remarks = WordUtils.capitalizeFully(jobject.getString("Remarks"));

                   /* StringBuilder sb = new StringBuilder(Remarks);

                    int ii = 0;
                    while ((ii = sb.indexOf(" ", ii + 11)) != -1) {
                        sb.replace(ii, ii + 1, "\n");
                    }*/

                    TableRow row = new TableRow(this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);

                    if (i % 2 == 0)
                        row.setBackgroundColor(Color.WHITE);
                    else
                        row.setBackgroundColor(Color.parseColor("#dddddd"));


//                    TextView A = new TextView(this);
//                    TextView B = new TextView(this);
                    TextView C = new TextView(this);
                    TextView D = new TextView(this);
                    TextView E = new TextView(this);
                    TextView F = new TextView(this);
                    TextView G = new TextView(this);
                    TextView H = new TextView(this);

//                    A.setText(member_id);
//                    B.setText(MemName);
                    C.setText(Idno);
                    D.setText(MemName);
                    E.setText(BillNo);
                    F.setText(Billdate);
                    G.setText(BV);
                    //H.setText(sb.toString());

                    C.setTypeface(typeface);
                    D.setTypeface(typeface);
                    E.setTypeface(typeface);
                    F.setTypeface(typeface);
                    G.setTypeface(typeface);
                    H.setTypeface(typeface);

//                    A.setGravity(Gravity.CENTER);
//                    B.setGravity(Gravity.CENTER);
                    C.setGravity(Gravity.CENTER);
                    D.setGravity(Gravity.CENTER);
                    E.setGravity(Gravity.CENTER);
                    F.setGravity(Gravity.CENTER);
                    G.setGravity(Gravity.CENTER);
                    H.setGravity(Gravity.CENTER);

//                    A.setPadding(px, px, px, px);
//                    B.setPadding(px, px, px, px);
                    C.setPadding(px, px, px, px);
                    D.setPadding(px, px, px, px);
                    E.setPadding(px, px, px, px);
                    F.setPadding(px, px, px, px);
                    G.setPadding(px, px, px, px);
                    H.setPadding(px, px, px, px);

//                    A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                    B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    G.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

//                    row.addView(A);
//                    row.addView(B);
                    row.addView(C);
                    row.addView(D);
                    row.addView(E);
                    row.addView(F);
                    row.addView(G);
                    //  row.addView(H);

                    View view_one = new View(this);
                    view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                    view.setBackgroundColor(Color.parseColor("#cccccc"));

                    ll.addView(row);
                    ll.addView(view_one);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void WriteValues3(final JSONArray count, final JSONArray jarray) {
        try {

            findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

            String text = "(Showing " + jarray.length() + " records)";
            txt_count.setText(text);

            if (jarray.length() > 0) {
                findViewById(R.id.HSV3).setVisibility(View.VISIBLE);
                // findViewById(R.id.LL_count).setVisibility(View.VISIBLE);
            }

            float sp = 8;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);

            if (count.length() > 0) {
                JSONObject jsonObject = count.getJSONObject(0);
                DecimalFormat df = new DecimalFormat("#.###");

                txt_self_bv.setText(df.format(jsonObject.getDouble("SelfBV")));
                txt_team_bv.setText(df.format(jsonObject.getDouble("LeftBv")));
                txt_total_bv.setText(df.format(jsonObject.getDouble("RightBV")));
            }
            TableLayout ll = findViewById(R.id.displayLinear3);
            ll.removeAllViews();

            Typeface typeface = ResourcesCompat.getFont(this, R.font.roboto);

            TableRow row1 = new TableRow(this);

            TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row1.setLayoutParams(lp1);
            row1.setBackgroundColor(getResources().getColor(R.color.color_136D98));

//            TextView A1 = new TextView(this);
//            TextView B1 = new TextView(this);
            TextView C1 = new TextView(this);
            TextView D1 = new TextView(this);
            TextView E1 = new TextView(this);
            TextView F1 = new TextView(this);
            TextView G1 = new TextView(this);
            TextView H1 = new TextView(this);

//            A1.setText("ID No.");
//            B1.setText("Member Name");
            C1.setText("Id Number");
            D1.setText("Member Name");
            E1.setText("Bill Number");
            F1.setText("Bill Date");
            G1.setText("BV");
            //  H1.setText("Remarks");

            C1.setTypeface(typeface);
            D1.setTypeface(typeface);
            E1.setTypeface(typeface);
            F1.setTypeface(typeface);
            G1.setTypeface(typeface);
            H1.setTypeface(typeface);

//            A1.setPadding(px, px, px, px);
//            B1.setPadding(px, px, px, px);
            C1.setPadding(px, px, px, px);
            D1.setPadding(px, px, px, px);
            E1.setPadding(px, px, px, px);
            F1.setPadding(px, px, px, px);
            G1.setPadding(px, px, px, px);
            H1.setPadding(px, px, px, px);

//            A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//            B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            G1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

//            A1.setGravity(Gravity.CENTER);
//            B1.setGravity(Gravity.CENTER);
            C1.setGravity(Gravity.CENTER);
            D1.setGravity(Gravity.CENTER);
            E1.setGravity(Gravity.CENTER);
            F1.setGravity(Gravity.CENTER);
            G1.setGravity(Gravity.CENTER);
            H1.setGravity(Gravity.CENTER);

//            A1.setTextColor(Color.WHITE);
//            B1.setTextColor(Color.WHITE);
            C1.setTextColor(Color.WHITE);
            D1.setTextColor(Color.WHITE);
            E1.setTextColor(Color.WHITE);
            F1.setTextColor(Color.WHITE);
            G1.setTextColor(Color.WHITE);
            H1.setTextColor(Color.WHITE);

//            row1.addView(A1);
//            row1.addView(B1);
            row1.addView(C1);
            row1.addView(D1);
            row1.addView(E1);
            row1.addView(F1);
            row1.addView(G1);
            //  row1.addView(H1);

            View view = new View(this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            view.setBackgroundColor(Color.parseColor("#cccccc"));

            ll.addView(row1);
            ll.addView(view);

            for (int i = 0; i < jarray.length(); i++) {
                try {
                    JSONObject jobject = jarray.getJSONObject(i);

//                    String member_id = jobject.getString("IdNo");
//                    String MemName = WordUtils.capitalizeFully(jobject.getString("MemName"));
                    String Idno = jobject.getString("Idno");
                    String MemName = jobject.getString("MemName");
                    String BillNo = jobject.getString("BillNo");
                    String Billdate = jobject.getString("TransDate");
                    String BV = jobject.getString("BV");
                    //  String Remarks = WordUtils.capitalizeFully(jobject.getString("Remarks"));

                   /* StringBuilder sb = new StringBuilder(Remarks);

                    int ii = 0;
                    while ((ii = sb.indexOf(" ", ii + 11)) != -1) {
                        sb.replace(ii, ii + 1, "\n");
                    }*/

                    TableRow row = new TableRow(this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);

                    if (i % 2 == 0)
                        row.setBackgroundColor(Color.WHITE);
                    else
                        row.setBackgroundColor(Color.parseColor("#dddddd"));


//                    TextView A = new TextView(this);
//                    TextView B = new TextView(this);
                    TextView C = new TextView(this);
                    TextView D = new TextView(this);
                    TextView E = new TextView(this);
                    TextView F = new TextView(this);
                    TextView G = new TextView(this);
                    TextView H = new TextView(this);

//                    A.setText(member_id);
//                    B.setText(MemName);
                    C.setText(Idno);
                    D.setText(MemName);
                    E.setText(BillNo);
                    F.setText(Billdate);
                    G.setText(BV);
                    //H.setText(sb.toString());

                    C.setTypeface(typeface);
                    D.setTypeface(typeface);
                    E.setTypeface(typeface);
                    F.setTypeface(typeface);
                    G.setTypeface(typeface);
                    H.setTypeface(typeface);

//                    A.setGravity(Gravity.CENTER);
//                    B.setGravity(Gravity.CENTER);
                    C.setGravity(Gravity.CENTER);
                    D.setGravity(Gravity.CENTER);
                    E.setGravity(Gravity.CENTER);
                    F.setGravity(Gravity.CENTER);
                    G.setGravity(Gravity.CENTER);
                    H.setGravity(Gravity.CENTER);

//                    A.setPadding(px, px, px, px);
//                    B.setPadding(px, px, px, px);
                    C.setPadding(px, px, px, px);
                    D.setPadding(px, px, px, px);
                    E.setPadding(px, px, px, px);
                    F.setPadding(px, px, px, px);
                    G.setPadding(px, px, px, px);
                    H.setPadding(px, px, px, px);

//                    A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                    B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    G.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

//                    row.addView(A);
//                    row.addView(B);
                    row.addView(C);
                    row.addView(D);
                    row.addView(E);
                    row.addView(F);
                    row.addView(G);
                    //  row.addView(H);

                    View view_one = new View(this);
                    view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                    view.setBackgroundColor(Color.parseColor("#cccccc"));

                    ll.addView(row);
                    ll.addView(view_one);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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

}
