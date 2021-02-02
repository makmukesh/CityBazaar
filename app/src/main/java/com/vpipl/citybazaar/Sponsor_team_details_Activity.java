package com.vpipl.citybazaar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sponsor_team_details_Activity extends AppCompatActivity {

    Activity act = Sponsor_team_details_Activity.this;
    private static DrawerLayout drawer;
    private static NavigationView navigationView;
    private String TAG = "Sponsor_team_details_Activity";
    private EditText txt_from_joining, txt_to_joining, txt_from_activation, txt_to_activation;
    private LinearLayout ll_activation;
    private LinearLayout ll_joining;
    private LinearLayout ll_activation_week;
    private CheckBox cb_joining;
    private CheckBox cb_activation;
    private CheckBox cb_activation_week;
    private Button btn_proceed;
    private Button btn_load_more;
    private TableLayout displayLinear;
    private Calendar myCalendar;
    private SimpleDateFormat sdf;
    private String whichdate = "";

    public ArrayList<HashMap<String, String>> PackageList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> ACtivationWeekList = new ArrayList<>();
    RadioButton rb_unused, rb_used, rb_both, rb_left, rb_right, rb_all;
    RadioGroup rg_view_detail_for, rg_side;
    String PackageArray[], ActivationWeekArray[];
    TextView txt_package_Name;
    TextView txt_activation_week;
    LinearLayout ll_button_bottom_data;

    private final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (new Date().after(myCalendar.getTime())) {

                if (whichdate.equalsIgnoreCase("txt_from_joining")) {
                    txt_from_joining.setText(sdf.format(myCalendar.getTime()));
                } else if (whichdate.equalsIgnoreCase("txt_to_joining"))
                    txt_to_joining.setText(sdf.format(myCalendar.getTime()));
                else if (whichdate.equalsIgnoreCase("txt_to_activation"))
                    txt_to_activation.setText(sdf.format(myCalendar.getTime()));
                else if (whichdate.equalsIgnoreCase("txt_from_activation")) {
                    txt_from_activation.setText(sdf.format(myCalendar.getTime()));
                }
            } else {
                AppUtils.alertDialog(act, "Selected Date Can't be After today");
            }
        }
    };

    private TextView txt_heading;
    private TextView txt_count;
    private int TopRows = 25;
    private String action = "Sponsor";
    private TextView txt_welcome_name;
    private TextView txt_id_number;
    private TextView txt_available_wb;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private int lastExpandedPosition = -1;
    private ExpandableListView expListView;
    private CircularImageView profileImage;
    private JSONArray HeadingJarray;

    DatePickerDialog datePickerDialog;


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

    TextView txt_left_paid_members, txt_right_paid_members, txt_ttl_left_members, txt_ttl_right_members;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponsor_team_details);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            AppUtils.changeStatusBarColor(act);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            if (getIntent().getExtras() != null)
                action = getIntent().getStringExtra("Action");

            ll_activation_week = findViewById(R.id.ll_activation_week);
            cb_activation_week = findViewById(R.id.cb_activation_week);
            txt_from_joining = findViewById(R.id.txt_from_joining);
            txt_to_joining = findViewById(R.id.txt_to_joining);
            txt_from_activation = findViewById(R.id.txt_from_activation);
            txt_to_activation = findViewById(R.id.txt_to_activation);

            txt_heading = findViewById(R.id.txt_heading);

            txt_count = findViewById(R.id.txt_count);

            ll_activation = findViewById(R.id.ll_activation);
            ll_joining = findViewById(R.id.ll_joining);

            cb_joining = findViewById(R.id.cb_joining);
            cb_activation = findViewById(R.id.cb_activation);

            btn_proceed = findViewById(R.id.btn_proceed);
            btn_load_more = findViewById(R.id.btn_load_more);
            txt_package_Name = findViewById(R.id.txt_package_Name);
            txt_activation_week = findViewById(R.id.txt_activation_week);

            displayLinear = findViewById(R.id.displayLinear);

            txt_left_paid_members = findViewById(R.id.txt_left_paid_members);
            txt_right_paid_members = findViewById(R.id.txt_right_paid_members);
            txt_ttl_left_members = findViewById(R.id.txt_ttl_left_members);
            txt_ttl_right_members = findViewById(R.id.txt_ttl_right_members);
            ll_button_bottom_data = findViewById(R.id.ll_button_bottom_data);



           /* cb_joining.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        ll_joining.setVisibility(View.VISIBLE);
                    } else {
                        ll_joining.setVisibility(View.GONE);
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
                    } else {
                        ll_activation.setVisibility(View.GONE);
                        txt_from_activation.setText("");
                        txt_to_activation.setText("");
                    }
                }
            });*/

            btn_load_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TopRows = TopRows + 25;
                    createSponsorTeamListRequest();
                }
            });
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
                    TopRows = 25;
                    createSponsorTeamListRequest();
                }
            });
           /* myCalendar = Calendar.getInstance();
            //  sdf = new SimpleDateFormat("dd MMM yyyy");
            sdf = new SimpleDateFormat("dd/MM/yyyy");

            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_MONTH, 1);
            txt_from_joining.setText(sdf.format(c.getTime()));
            txt_to_joining.setText(sdf.format(myCalendar.getTime()));

            Calendar c1 = Calendar.getInstance();
            c1.set(Calendar.DAY_OF_MONTH, 1);
            txt_from_activation.setText(sdf.format(c1.getTime()));
            txt_to_activation.setText(sdf.format(myCalendar.getTime()));*/

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

            if (action.equalsIgnoreCase("Sponsor")) {
                txt_heading.setText(getResources().getString(R.string.sponsor_downline));

            } else if (action.equalsIgnoreCase("Direct")) {
                txt_heading.setText(getResources().getString(R.string.direct_members_detail));
            }

            if (AppUtils.isNetworkAvailable(this)) {
                executeLoginRequest();
                executeWeekRequest();
                //   createSponsorTeamListRequest();
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

    private void createSponsorTeamListRequest() {
        /*List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
        postParameters.add(new BasicNameValuePair("TopRows", "" + TopRows));
        postParameters.add(new BasicNameValuePair("FromJD", "" + txt_from_joining.getText().toString()));
        postParameters.add(new BasicNameValuePair("ToJD", "" + txt_to_joining.getText().toString()));
        postParameters.add(new BasicNameValuePair("FromAD", "" + txt_from_activation.getText().toString()));
        postParameters.add(new BasicNameValuePair("ToAD", "" + txt_to_activation.getText().toString()));

        postParameters.add(new BasicNameValuePair("WeekValue", "0"));
        postParameters.add(new BasicNameValuePair("Side", "0"));
        postParameters.add(new BasicNameValuePair("Status", "ALL" ));
        postParameters.add(new BasicNameValuePair("PackageId", "0"));*/

        /*FormNo:	WeekValue:	Side:	Status:	FromJD:	ToJD:	FromAD:	ToAD:	PackageId:	TopRows:*/
        findViewById(R.id.ll_showData).setVisibility(View.GONE);
        //  findViewById(R.id.HSV).setVisibility(View.GONE);
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
                            if (action.equalsIgnoreCase("Sponsor")) {
                                response = AppUtils.callWebServiceWithMultiParam(act, postparameters, QueryUtils.methodToGetSponsorTeamDetail, TAG);
                            } else if (action.equalsIgnoreCase("Direct")) {
                                response = AppUtils.callWebServiceWithMultiParam(act, postparameters, QueryUtils.methodToGetMyDirectMembers, TAG);
                            }

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
                                if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                    JSONArray jsonArrayTotalPaidMember;
                                    jsonArrayTotalPaidMember = new JSONArray();
                                    if (action.equalsIgnoreCase("Sponsor")) {
                                        JSONArray jsonArrayData = jsonObject.getJSONArray("DownLineTeamDetails");
                                        JSONArray jsonArrayTotalRowCount = jsonObject.getJSONArray("TotalRowCount");
                                        jsonArrayTotalPaidMember = jsonObject.getJSONArray("TotalPaidMember");
                                        WriteValues(jsonArrayData, jsonArrayTotalPaidMember);
                                    } else if (action.equalsIgnoreCase("Direct")) {
                                        JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                        jsonArrayTotalPaidMember = jsonObject.getJSONArray("TotalPaidMember");
                                        WriteValues(jsonArrayData, jsonArrayTotalPaidMember);
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

    private void WriteValues(final JSONArray jarray, final JSONArray jsonArrayTotalPaidMember) {

        findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

        float sp = 10;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);
        int px_right = (int) (12 * getResources().getDisplayMetrics().scaledDensity);

        if (jsonArrayTotalPaidMember.length() > 0) {
            ll_button_bottom_data.setVisibility(View.VISIBLE);
            try {
                txt_left_paid_members.setText("Left Paid Members : " + jsonArrayTotalPaidMember.getJSONObject(0).getString("LeftPaidMember"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                txt_right_paid_members.setText("Right Paid Members : " + jsonArrayTotalPaidMember.getJSONObject(0).getString("RightPaidMember"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                txt_ttl_left_members.setText("Total Left Members : " + jsonArrayTotalPaidMember.getJSONObject(0).getString("TotalLeftPaidMember"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                txt_ttl_right_members.setText("Total Right Members : " + jsonArrayTotalPaidMember.getJSONObject(0).getString("TotalRightPaidMember"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            ll_button_bottom_data.setVisibility(View.GONE);
        }
        Typeface typeface = ResourcesCompat.getFont(this, R.font.roboto);

        if (jarray.length() > 0) {
            findViewById(R.id.txt_count).setVisibility(View.VISIBLE);
            String count_text = "(Showing " + jarray.length() + " of " + jarray.length() + " records)";
            txt_count.setText(count_text);
        }

        TableLayout ll = findViewById(R.id.displayLinear);
        ll.removeAllViews();

        TableRow row1 = new TableRow(this);

        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row1.setLayoutParams(lp1);
        row1.setBackgroundColor(getResources().getColor(R.color.color_136D98));

        TextView A1 = new TextView(this);
        TextView B1 = new TextView(this);
        TextView C1 = new TextView(this);
        TextView D1 = new TextView(this);
        TextView E1 = new TextView(this);
        TextView EE1 = new TextView(this);
        TextView F1 = new TextView(this);
        TextView G1 = new TextView(this);
        TextView H1 = new TextView(this);

        A1.setText("Level No.");
        B1.setText(getString(R.string.member_id));
        C1.setText(getString(R.string.name));
        D1.setText("Joining Date");
        E1.setText(getString(R.string.activation_date));
        EE1.setText("Activation Session");
        F1.setText(getString(R.string.sponsorId));
        G1.setText("Side");
        H1.setText("Activation BV");

        A1.setPadding(px, px, px, px);
        B1.setPadding(px, px, px, px);
        C1.setPadding(px, px, px, px);
        D1.setPadding(px, px, px, px);
        E1.setPadding(px, px, px, px);
        EE1.setPadding(px, px, px, px);
        F1.setPadding(px, px, px, px);
        G1.setPadding(px, px, px, px);
        H1.setPadding(px, px, px, px);

        A1.setTypeface(typeface);
        B1.setTypeface(typeface);
        C1.setTypeface(typeface);
        D1.setTypeface(typeface);
        E1.setTypeface(typeface);
        EE1.setTypeface(typeface);
        F1.setTypeface(typeface);
        G1.setTypeface(typeface);
        H1.setTypeface(typeface);

        A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        EE1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        G1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        A1.setGravity(Gravity.CENTER);
        B1.setGravity(Gravity.CENTER);
        C1.setGravity(Gravity.CENTER_VERTICAL);
        D1.setGravity(Gravity.CENTER);
        E1.setGravity(Gravity.CENTER);
        EE1.setGravity(Gravity.CENTER);
        F1.setGravity(Gravity.CENTER);
        G1.setGravity(Gravity.CENTER);
        H1.setGravity(Gravity.CENTER);

        A1.setTextColor(Color.WHITE);
        B1.setTextColor(Color.WHITE);
        C1.setTextColor(Color.WHITE);
        D1.setTextColor(Color.WHITE);
        E1.setTextColor(Color.WHITE);
        EE1.setTextColor(Color.WHITE);
        F1.setTextColor(Color.WHITE);
        G1.setTextColor(Color.WHITE);
        H1.setTextColor(Color.WHITE);

        row1.addView(A1);
        row1.addView(B1);
        row1.addView(C1);
        row1.addView(D1);
        row1.addView(E1);
        row1.addView(EE1);
        row1.addView(F1);
        row1.addView(G1);
        row1.addView(H1);

        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
//        view.setBackgroundColor(Color.parseColor("#999999"));
        view.setBackgroundColor(Color.parseColor("#cccccc"));

        ll.addView(row1);
        ll.addView(view);

        for (int i = 0; i < jarray.length(); i++) {
            try {

                JSONObject jobject = jarray.getJSONObject(i);

                String member_id = jobject.getString("IdNo");
                String level = jobject.getString("MLevel");
                String name = WordUtils.capitalizeFully(jobject.getString("MemName"));
                String purchase_date = jobject.getString("JoinDate");
                String activation_date = jobject.getString("TopUpDate");
                String sponsor_id = jobject.getString("ReferralIdNo");
                String SponsorLegNo = jobject.getString("Leg");
                String Activation_Session = jobject.getString("UpGrdSessId");
                String JoiningBV = jobject.getString("JoiningBV");

                StringBuilder sb = new StringBuilder(name);


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
                TextView EE = new TextView(this);
                TextView F = new TextView(this);
                TextView G = new TextView(this);
                TextView H = new TextView(this);

                A.setText(level);
                B.setText(member_id);
                C.setText(sb.toString());
               // D.setText(AppUtils.getDateFromAPIDate(purchase_date));
                D.setText(purchase_date);
                E.setText(activation_date);
                EE.setText(Activation_Session);
                F.setText(sponsor_id);
                G.setText(SponsorLegNo);
                H.setText(JoiningBV);

                A.setGravity(Gravity.CENTER);
                B.setGravity(Gravity.CENTER);
                C.setGravity(Gravity.CENTER_VERTICAL);
                D.setGravity(Gravity.CENTER);
                E.setGravity(Gravity.CENTER);
                EE.setGravity(Gravity.CENTER);
                F.setGravity(Gravity.CENTER);
                G.setGravity(Gravity.CENTER);
                H.setGravity(Gravity.CENTER);

                A.setTypeface(typeface);
                B.setTypeface(typeface);
                C.setTypeface(typeface);
                D.setTypeface(typeface);
                E.setTypeface(typeface);
                EE.setTypeface(typeface);
                F.setTypeface(typeface);
                G.setTypeface(typeface);
                H.setTypeface(typeface);

                A.setPadding(px, px, px, px);
                B.setPadding(px, px, px, px);
                C.setPadding(px, px, px, px);
                D.setPadding(px, px, px, px);
                E.setPadding(px, px, px, px);
                EE.setPadding(px, px, px, px);
                F.setPadding(px, px, px, px);
                G.setPadding(px, px, px, px);
                H.setPadding(px, px, px, px);

                A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                EE.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                G.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                row.addView(A);
                row.addView(B);
                row.addView(C);
                row.addView(D);
                row.addView(E);
                row.addView(EE);
                row.addView(F);
                row.addView(G);
                row.addView(H);

                View view_one = new View(this);
                view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
//                view_one.setBackgroundColor(Color.parseColor("#999999"));
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

        createSponsorTeamListRequest();

        enableExpandableList();
        LoadNavigationHeaderItems();
    }

    private void showdatePicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        datePickerDialog.show();
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

}
