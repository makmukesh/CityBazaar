package com.vpipl.citybazaar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.SPUtils;
import com.vpipl.citybazaar.Utils.QueryUtils;

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

public class Downline_Team_Details_Activity extends AppCompatActivity {

    Activity act = Downline_Team_Details_Activity.this;
    public ArrayList<HashMap<String, String>> PackageList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> ACtivationWeekList = new ArrayList<>();
    String TAG = "Downline_Team_Details_Activity";
    EditText txt_from_joining, txt_to_joining, txt_from_activation, txt_to_activation;
    TextView txt_activation_week, txt_package_Name;
    TextView txt_left_paid_members, txt_right_paid_members, txt_left_total_members, txt_right_total_members;
    LinearLayout ll_activation, ll_joining, ll_activation_week;
    CheckBox cb_joining, cb_activation, cb_activation_week;
    Button btn_proceed, btn_load_more;
    TableLayout displayLinear;

    Calendar myCalendar;
    SimpleDateFormat sdf;
    String whichdate = "";
    DatePickerDialog datePickerDialog;

    private void showdatePicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(act, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
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
                if (whichdate.equalsIgnoreCase("txt_from_joining")) {
                    txt_from_joining.setText(sdf.format(myCalendar.getTime()));
                    txt_to_joining.setText(sdf.format(myCalendar.getTime()));
                } else if (whichdate.equalsIgnoreCase("txt_to_joining"))
                    txt_to_joining.setText(sdf.format(myCalendar.getTime()));
                else if (whichdate.equalsIgnoreCase("txt_to_activation"))
                    txt_to_activation.setText(sdf.format(myCalendar.getTime()));
                else if (whichdate.equalsIgnoreCase("txt_from_activation")) {
                    txt_from_activation.setText(sdf.format(myCalendar.getTime()));
                    txt_to_activation.setText(sdf.format(myCalendar.getTime()));
                }
            } else {

                AppUtils.alertDialog(act, "Selected Date Can't be After today");
            }
        }
    };
    RadioButton rb_unused, rb_used, rb_both, rb_left, rb_right, rb_all;
    RadioGroup rg_view_detail_for, rg_side;
    TextView txt_heading, txt_count;

    String PackageArray[], ActivationWeekArray[];

    int TopRows = 10;
    int Count = 0;
    JSONArray AllDataArray, DataArray;
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
        setContentView(R.layout.activity_downline_team_details);

        try {
            AppUtils.changeStatusBarColor(act);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");

            SetupToolbar();

            txt_from_joining = findViewById(R.id.txt_from_joining);
            txt_to_joining = findViewById(R.id.txt_to_joining);
            txt_from_activation = findViewById(R.id.txt_from_activation);
            txt_to_activation = findViewById(R.id.txt_to_activation);

            txt_activation_week = (TextView) findViewById(R.id.txt_activation_week);

            txt_left_paid_members = (TextView) findViewById(R.id.txt_left_paid_members);
            txt_right_paid_members = (TextView) findViewById(R.id.txt_right_paid_members);
            txt_left_total_members = (TextView) findViewById(R.id.txt_left_total_members);
            txt_right_total_members = (TextView) findViewById(R.id.txt_right_total_members);

            txt_package_Name = (TextView) findViewById(R.id.txt_package_Name);
            txt_heading = (TextView) findViewById(R.id.txt_heading);
            txt_count = (TextView) findViewById(R.id.txt_count);

            ll_activation = (LinearLayout) findViewById(R.id.ll_activation);
            ll_joining = (LinearLayout) findViewById(R.id.ll_joining);
            ll_activation_week = (LinearLayout) findViewById(R.id.ll_activation_week);

            cb_joining = (CheckBox) findViewById(R.id.cb_joining);
            cb_activation = (CheckBox) findViewById(R.id.cb_activation);
            cb_activation_week = (CheckBox) findViewById(R.id.cb_activation_week);

            btn_proceed = (Button) findViewById(R.id.btn_proceed);
            btn_load_more = (Button) findViewById(R.id.btn_load_more);

            displayLinear = (TableLayout) findViewById(R.id.displayLinear);

            rg_view_detail_for = (RadioGroup) findViewById(R.id.rg_view_detail_for);
            rg_side = (RadioGroup) findViewById(R.id.rg_side);

            rb_unused = (RadioButton) findViewById(R.id.rb_unused);
            rb_used = (RadioButton) findViewById(R.id.rb_used);
            rb_both = (RadioButton) findViewById(R.id.rb_both);
            rb_left = (RadioButton) findViewById(R.id.rb_left);
            rb_right = (RadioButton) findViewById(R.id.rb_right);
            rb_all = (RadioButton) findViewById(R.id.rb_all);

            txt_package_Name.setInputType(InputType.TYPE_NULL);
            txt_package_Name.setKeyListener(null);
            txt_package_Name.setHintTextColor(getResources().getColor(R.color.colorPrimary));

            cb_joining.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        ll_joining.setVisibility(View.VISIBLE);

                        myCalendar = Calendar.getInstance();
                        //  sdf = new SimpleDateFormat("dd MMM yyyy");
                        sdf = new SimpleDateFormat("dd/MM/yyyy");

                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.DAY_OF_MONTH, 1);
                        txt_from_joining.setText(sdf.format(c.getTime()));
                        txt_to_joining.setText(sdf.format(myCalendar.getTime()));
                    } else {
                        ll_joining.setVisibility(View.GONE);
                        ll_activation_week.setVisibility(View.GONE);
                        txt_from_joining.setText("");
                        txt_to_joining.setText("");
                    }
                }
            });

            cb_activation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        ll_activation.setVisibility(View.VISIBLE);

                        myCalendar = Calendar.getInstance();
                        //  sdf = new SimpleDateFormat("dd MMM yyyy");
                        sdf = new SimpleDateFormat("dd/MM/yyyy");

                        Calendar c1 = Calendar.getInstance();
                        c1.set(Calendar.DAY_OF_MONTH, 1);
                        txt_from_activation.setText(sdf.format(c1.getTime()));
                        txt_to_activation.setText(sdf.format(myCalendar.getTime()));
                    } else {
                        ll_activation_week.setVisibility(View.GONE);
                        ll_activation.setVisibility(View.GONE);
                        txt_from_activation.setText("");
                        txt_to_activation.setText("");
                    }
                }
            });

            cb_activation_week.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if (b) {
                        ll_activation_week.setVisibility(View.VISIBLE);
                    } else {
                        ll_joining.setVisibility(View.GONE);
                        ll_activation.setVisibility(View.GONE);
                        ll_activation_week.setVisibility(View.GONE);
                        txt_activation_week.setText("");
                    }
                }
            });

            txt_package_Name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PackageList.size() != 0) {
                        showPackageDialog();
                        txt_package_Name.clearFocus();
                    } else {
                        executePackageRequest();
                    }
                }
            });

            txt_activation_week.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ACtivationWeekList.size() != 0) {
                        showWeekDialog();
                        txt_activation_week.clearFocus();
                    } else {
                        executeWeekRequest();
                    }
                }
            });

            btn_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TopRows = 10;
                    createSponsorTeamListRequest();
                }
            });

            btn_load_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TopRows = TopRows + 10;
                    WriteValues();
                }
            });

           /* myCalendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("dd-MMM-yyyy");*/
            myCalendar = Calendar.getInstance();
            //  sdf = new SimpleDateFormat("dd MMM yyyy");
            sdf = new SimpleDateFormat("dd/MM/yyyy");

            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_MONTH, 1);
            txt_from_joining.setText(sdf.format(c.getTime()));
            txt_to_joining.setText(sdf.format(myCalendar.getTime()));

            Calendar c1 = Calendar.getInstance();
            c1.set(Calendar.DAY_OF_MONTH, 1);
            txt_from_activation.setText(sdf.format(c1.getTime()));
            txt_to_activation.setText(sdf.format(myCalendar.getTime()));

            /*txt_from_joining.setOnClickListener(new View.OnClickListener() {
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
            });

            txt_from_activation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    whichdate = "txt_from_activation";
                    showdatePicker();
                }
            });

            txt_to_activation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    whichdate = "txt_to_activation";
                    showdatePicker();
                }
            });*/

            txt_heading.setText("Downline Team Detail");

            if (AppUtils.isNetworkAvailable(this)) {

                executeWeekRequest();
                createSponsorTeamListRequest();

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

            txt_from_activation.addTextChangedListener(new TextWatcher() {
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
                        txt_from_activation.setText(current);
                        txt_from_activation.setSelection(sel < current.length() ? sel : current.length());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                }
            });
            txt_to_activation.addTextChangedListener(new TextWatcher() {
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
                        txt_to_activation.setText(current);
                        txt_to_activation.setSelection(sel < current.length() ? sel : current.length());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }

    private void executePackageRequest() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                AppUtils.showProgressDialog(act);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                List<NameValuePair> postParameters = new ArrayList<>();
                response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodSponsorPageFillPackage, TAG);
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
                            getPackageResult(jsonArrayData);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeWeekRequest() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                AppUtils.showProgressDialog(act);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                List<NameValuePair> postParameters = new ArrayList<>();
                response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodSponsorActivationWeek, TAG);
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
                            getWeekResult(jsonArrayData);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getPackageResult(JSONArray jsonArray) {
        try {
            PackageList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("KitID", jsonObject.getString("KitID"));
                map.put("KitName", WordUtils.capitalizeFully(jsonObject.getString("KitName")));

                PackageList.add(map);
            }

            String[] PackageArray = new String[PackageList.size()];
            for (int i = 0; i < PackageList.size(); i++) {
                PackageArray[i] = PackageList.get(i).get("KitName");
            }
            txt_package_Name.setText(PackageArray[0]);

            createSponsorTeamListRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (AppUtils.isNetworkAvailable(this)) {

            //     executeWeekRequest();

        } else {
            AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppUtils.isNetworkAvailable(this)) {

            //     executeWeekRequest();

        } else {
            AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
        }
    }

    private void getWeekResult(JSONArray jsonArray) {
        try {
            ACtivationWeekList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("SessID", jsonObject.getString("SessID"));
                map.put("SessName", WordUtils.capitalizeFully(jsonObject.getString("SessName")));

                ACtivationWeekList.add(map);
            }

            executePackageRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPackageDialog() {
        try {
            PackageArray = new String[PackageList.size()];
            for (int i = 0; i < PackageList.size(); i++) {
                PackageArray[i] = PackageList.get(i).get("KitName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Package ");
            builder.setItems(PackageArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_package_Name.setText(PackageArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void showWeekDialog() {
        try {
            ActivationWeekArray = new String[ACtivationWeekList.size()];

            for (int i = 0; i < ACtivationWeekList.size(); i++) {
                ActivationWeekArray[i] = ACtivationWeekList.get(i).get("SessName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Activation Week");
            builder.setItems(ActivationWeekArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_activation_week.setText(ActivationWeekArray[item]);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void createSponsorTeamListRequest() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);
        findViewById(R.id.HSV).setVisibility(View.GONE);
        findViewById(R.id.txt_count).setVisibility(View.GONE);

        int selectedId = rg_view_detail_for.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        String view_detail_for = radioButton.getText().toString().trim();

        String Type = "0";

        if (view_detail_for.equalsIgnoreCase("Both"))
            Type = "ALL";
        else if (view_detail_for.equalsIgnoreCase("Paid"))
            Type = "Active";
        else if (view_detail_for.equalsIgnoreCase("Unpaid"))
            Type = "Deactive";

        int selectedIdTwo = rg_side.getCheckedRadioButtonId();
        RadioButton radioButtonTwo = (RadioButton) findViewById(selectedIdTwo);
        String view_detail_side = radioButtonTwo.getText().toString().trim();

        String side = "0";
        if (view_detail_side.equalsIgnoreCase("Left"))
            side = "1";
        else if (view_detail_side.equalsIgnoreCase("Right"))
            side = "2";
        else if (view_detail_side.equalsIgnoreCase("All"))
            side = "0";


        String packagename = txt_package_Name.getText().toString().trim();

        String packageid = "0";
        for (int i = 0; i < PackageList.size(); i++) {
            if (packagename.equals(PackageList.get(i).get("KitName"))) {
                packageid = PackageList.get(i).get("KitID");
            }
        }

        String Weekname = txt_activation_week.getText().toString().trim();

        String WeekValue = "0";
        for (int i = 0; i < ACtivationWeekList.size(); i++) {
            if (Weekname.equals(ACtivationWeekList.get(i).get("SessName"))) {
                WeekValue = ACtivationWeekList.get(i).get("SessID");
            }
        }

        String FromJD = "", ToJD = "", FromAD = "", ToAD = "";
        if (!txt_from_joining.getText().toString().equalsIgnoreCase("")
                && !txt_from_joining.getText().toString().equalsIgnoreCase("DD/MM/YYYY")) {
            FromJD = AppUtils.getFormatDate1(txt_from_joining.getText().toString());
        }
        if (!txt_to_joining.getText().toString().equalsIgnoreCase("")
                && !txt_to_joining.getText().toString().equalsIgnoreCase("DD/MM/YYYY")) {
            ToJD = AppUtils.getFormatDate1(txt_to_joining.getText().toString());
        }
        if (!txt_from_activation.getText().toString().equalsIgnoreCase("")
                && !txt_from_activation.getText().toString().equalsIgnoreCase("DD/MM/YYYY")) {
            FromAD = AppUtils.getFormatDate1(txt_from_activation.getText().toString());
        }
        if (!txt_to_activation.getText().toString().equalsIgnoreCase("")
                && !txt_to_activation.getText().toString().equalsIgnoreCase("DD/MM/YYYY")) {
            ToAD = AppUtils.getFormatDate1(txt_to_activation.getText().toString());
        }


        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
        postParameters.add(new BasicNameValuePair("WeekValue", "" + WeekValue));
        postParameters.add(new BasicNameValuePair("Side", "" + side));
        postParameters.add(new BasicNameValuePair("Status", "" + Type));
        postParameters.add(new BasicNameValuePair("FromJD", "" + FromJD));
        postParameters.add(new BasicNameValuePair("ToJD", "" + ToJD));
        postParameters.add(new BasicNameValuePair("FromAD", "" + FromAD));
        postParameters.add(new BasicNameValuePair("ToAD", "" + ToAD));
        postParameters.add(new BasicNameValuePair("PackageId", "" + packageid));
        postParameters.add(new BasicNameValuePair("TopRows", "1000"));

        executeMemberDownlineListRequest(postParameters);
    }

    private void executeMemberDownlineListRequest(final List postparameters) {
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

                            response = AppUtils.callWebServiceWithMultiParam(act, postparameters, QueryUtils.methodToGetDownlineTeamDetail, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(act);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayJoiningDetails = jsonObject.getJSONArray("TotalPaidMember");
                            // JSONArray jsonArrayJoiningDetails = jsonObject.getJSONArray("JoiningDetails");
                            JSONArray jsonArrayTotalRowCount = jsonObject.getJSONArray("TotalRowCount");
                            JSONArray jsonArrayDownLineDetail = jsonObject.getJSONArray("DownLineDetail");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                    Count = Integer.parseInt(jsonArrayTotalRowCount.getJSONObject(0).getString("Total"));
                                    AllDataArray = jsonArrayDownLineDetail;
                                    DataArray = jsonArrayJoiningDetails;

                                    WriteValues();

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

    public void WriteValues() {

        findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

        try {
            float sp = 10;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);
            int px_right = (int) (15 * getResources().getDisplayMetrics().scaledDensity);

            txt_left_paid_members.setText(DataArray.getJSONObject(0).getString("LeftPaidMember"));
            txt_right_paid_members.setText(DataArray.getJSONObject(0).getString("RightPaidMember"));
            txt_left_total_members.setText(DataArray.getJSONObject(0).getString("TotalPaidMember"));
            txt_right_total_members.setText(DataArray.getJSONObject(0).getString("TotalRightPaidMember"));

            if (AllDataArray.length() > 0) {
                findViewById(R.id.HSV).setVisibility(View.VISIBLE);
                findViewById(R.id.txt_count).setVisibility(View.VISIBLE);

                int length = 0;

                if (TopRows <= AllDataArray.length())
                    length = TopRows;
                else
                    length = AllDataArray.length();

                if (length == AllDataArray.length())
                    btn_load_more.setVisibility(View.GONE);

                String count_text = "(Showing " + length + " of " + Count + " records)";
                txt_count.setText(count_text);

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
                TextView FF1 = new TextView(this);
                TextView H1 = new TextView(this);
                TextView J1 = new TextView(this);
                TextView K1 = new TextView(this);
                TextView L1 = new TextView(this);
                TextView M1 = new TextView(this);
                TextView N1 = new TextView(this);

                A1.setText("Level No.");
                B1.setText("User ID");
                C1.setText(getString(R.string.name));
                D1.setText("Joining Date");
               // E1.setText("Status");
                F1.setText("Activation Date");
               // FF1.setText("Activation Session");
              //  H1.setText("Package Name");
                L1.setText("Sponsor ID");
                J1.setText("Placeunder ID");
                K1.setText("Side");
                M1.setText("Activation Amount");
              //  N1.setText("Upgrade BV");

                A1.setPadding(px, px, px, px);
                B1.setPadding(px, px, px, px);
                C1.setPadding(px, px, px, px);
                D1.setPadding(px, px, px, px);
                E1.setPadding(px, px, px, px);
                F1.setPadding(px, px, px, px);
                FF1.setPadding(px, px, px, px);
                H1.setPadding(px, px, px, px);
                L1.setPadding(px, px, px, px);
                J1.setPadding(px, px, px, px);
                K1.setPadding(px, px, px, px);
                M1.setPadding(px, px, px, px);
                N1.setPadding(px, px, px, px);

                A1.setTypeface(typeface);
                B1.setTypeface(typeface);
                C1.setTypeface(typeface);
                D1.setTypeface(typeface);
                E1.setTypeface(typeface);
                F1.setTypeface(typeface);
                FF1.setTypeface(typeface);
                H1.setTypeface(typeface);
                L1.setTypeface(typeface);
                J1.setTypeface(typeface);
                K1.setTypeface(typeface);
                M1.setTypeface(typeface);
                N1.setTypeface(typeface);

                A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                FF1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                J1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                L1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                K1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                M1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                N1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                A1.setGravity(Gravity.CENTER);
                B1.setGravity(Gravity.CENTER);
                C1.setGravity(Gravity.CENTER_VERTICAL);
                D1.setGravity(Gravity.CENTER_VERTICAL);
                E1.setGravity(Gravity.CENTER);
                F1.setGravity(Gravity.CENTER);
                FF1.setGravity(Gravity.CENTER);
                H1.setGravity(Gravity.CENTER);
                J1.setGravity(Gravity.CENTER);
                K1.setGravity(Gravity.CENTER);
                L1.setGravity(Gravity.CENTER);
                M1.setGravity(Gravity.CENTER);
                N1.setGravity(Gravity.CENTER);

                A1.setTextColor(Color.WHITE);
                B1.setTextColor(Color.WHITE);
                C1.setTextColor(Color.WHITE);
                D1.setTextColor(Color.WHITE);
                E1.setTextColor(Color.WHITE);
                F1.setTextColor(Color.WHITE);
                FF1.setTextColor(Color.WHITE);
                H1.setTextColor(Color.WHITE);
                J1.setTextColor(Color.WHITE);
                K1.setTextColor(Color.WHITE);
                L1.setTextColor(Color.WHITE);
                M1.setTextColor(Color.WHITE);
                N1.setTextColor(Color.WHITE);

                row1.addView(A1);
                row1.addView(B1);
                row1.addView(C1);
                row1.addView(D1);
               // row1.addView(E1);
                row1.addView(F1);
               // row1.addView(FF1);
               // row1.addView(H1);
                row1.addView(L1);
                row1.addView(J1);
                row1.addView(K1);
                row1.addView(M1);
               // row1.addView(N1);

                ll.addView(row1);

                for (int i = 0; i < length; i++) {
                    try {

                        JSONObject jobject = AllDataArray.getJSONObject(i);
                        String member_id = jobject.getString("IdNo");
                        String level = jobject.getString("Mlevel");
                        String name = WordUtils.capitalizeFully(jobject.getString("MemName"));
                        String purchase_date = jobject.getString("DOJ");

                        String Status = jobject.getString("ActiveStatus");
                        String StartUpPurchase = jobject.getString("TopUpDate");
                        String StartUpPackage = jobject.getString("KitName");
                        String SponsorID = jobject.getString("ReferralIdNo");
                        String CoSponsorID = jobject.getString("SponserIdNo");
                        String Side = jobject.getString("Leg");
                        String JoiningBV = jobject.getString("JoiningBV");
                      //  String UpgrdBV = jobject.getString("UpgrdBV");
                        String UpGrdSessId = jobject.getString("UpGrdSessId");

                        StringBuilder sb = new StringBuilder(name);


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
                        TextView FF = new TextView(this);
                        TextView H = new TextView(this);
                        TextView J = new TextView(this);
                        TextView K = new TextView(this);
                        TextView L = new TextView(this);
                        TextView M = new TextView(this);
                        TextView N = new TextView(this);


                        A.setText(level);
                        B.setText(member_id);
                        C.setText(sb.toString());
                        D.setText(AppUtils.getDateFromAPIDate(purchase_date));
                       // E.setText(Status);
                        F.setText(StartUpPurchase);
                       // FF.setText(UpGrdSessId);
                       // H.setText(StartUpPackage);
                        L.setText(SponsorID);
                        J.setText(CoSponsorID);
                        K.setText(Side);
                        M.setText(JoiningBV);
                      //  N.setText(UpgrdBV);

                        A.setGravity(Gravity.CENTER);
                        B.setGravity(Gravity.CENTER);
                        C.setGravity(Gravity.CENTER_VERTICAL);
                        D.setGravity(Gravity.CENTER_VERTICAL);
                        E.setGravity(Gravity.CENTER);
                        F.setGravity(Gravity.CENTER);
                        FF.setGravity(Gravity.CENTER);
                        H.setGravity(Gravity.CENTER);
                        J.setGravity(Gravity.CENTER);
                        K.setGravity(Gravity.CENTER);
                        L.setGravity(Gravity.CENTER);
                        M.setGravity(Gravity.CENTER);
                        N.setGravity(Gravity.CENTER);

                        A.setPadding(px, px, px, px);
                        B.setPadding(px, px, px, px);
                        C.setPadding(px, px, px, px);
                        D.setPadding(px, px, px, px);
                        E.setPadding(px, px, px, px);
                        F.setPadding(px, px, px, px);
                        FF.setPadding(px, px, px, px);
                        H.setPadding(px, px, px, px);
                        J.setPadding(px, px, px, px);
                        L.setPadding(px, px, px, px);
                        M.setPadding(px, px, px, px);
                        K.setPadding(px, px, px, px);
                        N.setPadding(px, px, px, px);

                        A.setTypeface(typeface);
                        B.setTypeface(typeface);
                        C.setTypeface(typeface);
                        D.setTypeface(typeface);
                        E.setTypeface(typeface);
                        F.setTypeface(typeface);
                        FF.setTypeface(typeface);
                        H.setTypeface(typeface);
                        J.setTypeface(typeface);
                        L.setTypeface(typeface);
                        M.setTypeface(typeface);
                        K.setTypeface(typeface);
                        N.setTypeface(typeface);

                        A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        FF.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        J.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        K.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        L.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        M.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        N.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                        row.addView(A);
                        row.addView(B);
                        row.addView(C);
                        row.addView(D);
                       // row.addView(E);
                        row.addView(F);
                       // row.addView(FF);
                       // row.addView(H);
                        row.addView(L);
                        row.addView(J);
                        row.addView(K);
                        row.addView(M);
                       // row.addView(N);

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
        } catch (Exception e) {
            e.printStackTrace();
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
            AppUtils.showExceptionDialog(act);
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
            AppUtils.showExceptionDialog(act);
        }
    }
}
