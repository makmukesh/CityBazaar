
package com.vpipl.citybazaar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.github.aakira.expandablelayout.ExpandableLayout;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.vpipl.citybazaar.Adapters.ExpandableListAdapter;
import com.vpipl.citybazaar.Adapters.SliderAdapterExample;
import com.vpipl.citybazaar.Epin.Transaction_login_Activity;
import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.Cache;
import com.vpipl.citybazaar.Utils.CircularImageView;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;
import com.vpipl.citybazaar.model.SliderItem;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashBoard_Activity extends Activity {

    Activity act = DashBoard_Activity.this;
    TextView bottom_mdts_fr_left, bottom_mdts_fr_right, bottom_mdts_fr_total,
            bottom_mdts_sr_left, bottom_mdts_sr_right, bottom_mdts_sr_total,
            bottom_mdts_tr_left, bottom_mdts_tr_right, bottom_mdts_tr_total;

    TextView bottom_mdms_fr_left, bottom_mdms_fr_right, bottom_mdms_fr_total,
            bottom_mdms_sr_left, bottom_mdms_sr_right, bottom_mdms_sr_total,
            bottom_mdms_tr_left, bottom_mdms_tr_right, bottom_mdms_tr_total;

    TextView bottom_mds_fr_left, bottom_mds_fr_right, bottom_mds_fr_total,
            bottom_mds_sr_left, bottom_mds_sr_right, bottom_mds_sr_total,
            bottom_mds_tr_left, bottom_mds_tr_right, bottom_mds_tr_total;

    LinearLayout ll_Downline_Team_Status ,ll_Direct_Members_Status,ll_Sponsoring_Team_Status;

    AVLoadingIndicatorView avi;
    TextView txt_member_name,txt_join_package, txt_joining_Date, txt_user_id, txt_Status, txt_ActivationDate, txt_ActivationPoint, txt_LastProfileUpdate;

    TextView txt_matching_bonus, txt_Core_Member_Income, txt_Reward_income, txt_Monthly_income;
    private static String Byte_Code;
    private static final String TAG = "DashBoard_Activity";

    private static DrawerLayout drawer;
    private static NavigationView navigationView;

    private TextView txt_welcome_name;
    private TextView txt_id_number;
    private TextView txt_available_wb;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private int lastExpandedPosition = -1;
    private ExpandableListView expListView;
    private CircularImageView profileImage;
    private ImageView iv_Profile_Pic_dash;
    LinearLayout ll_bottom_rep;
    private JSONArray HeadingJarray;
    String refer_url = "";
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask;
    Dialog dialog;
    SliderView sliderView;
    private SliderAdapterExample adapter_slider;
    private List<SliderItem> mSliderItems = new ArrayList<>();

    public static ArrayList<HashMap<String, String>> imageSlider = new ArrayList<>();

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
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close_dash));
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

    TextView txt_total_income, txt_rank_name;
    LinearLayout ll_top_on_dash;

    // Expanable section
    ExpandableLayout expandableLayout1, expandableLayout2, expandableLayout3;
    TextView img_dash_bottom_1, img_dash_bottom_2, img_dash_bottom_3;
    Boolean Statusdrop1 = false, Statusdrop2 = false, Statusdrop3 = false;
    TextView txt_main_heading, txt_title_core_mem_income;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);
        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            AppUtils.changeStatusBarColor(act);
            Toolbar toolbar = findViewById(R.id.toolbar_dash);
            // setSupportActionBar(toolbar);
            // getSupportActionBar().setTitle("");
            SetupToolbar();

         /*   this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {

                avi = findViewById(R.id.avi);
                txt_joining_Date = findViewById(R.id.txt_joining_Date);
                txt_join_package = findViewById(R.id.txt_join_package);
                txt_member_name = findViewById(R.id.txt_member_name);
                txt_user_id = findViewById(R.id.txt_user_id);

                txt_Status = findViewById(R.id.txt_Status);
                txt_ActivationDate = findViewById(R.id.txt_ActivationDate);
                txt_ActivationPoint = findViewById(R.id.txt_ActivationPoint);
                txt_LastProfileUpdate = findViewById(R.id.txt_LastProfileUpdate);

                ll_bottom_rep = findViewById(R.id.ll_bottom_rep);

                txt_total_income = findViewById(R.id.txt_total_income);
                txt_rank_name = findViewById(R.id.txt_rank_name);
                ll_top_on_dash = findViewById(R.id.ll_top_on_dash);

                txt_matching_bonus = findViewById(R.id.txt_matching_bonus);
                txt_Core_Member_Income = findViewById(R.id.txt_Core_Member_Income);
                txt_Reward_income = findViewById(R.id.txt_Reward_income);
                txt_Monthly_income = findViewById(R.id.txt_Monthly_income);

                drawer = findViewById(R.id.drawer_layout);
                navigationView = findViewById(R.id.nav_view);

                bottom_mdts_fr_left = findViewById(R.id.bottom_mdts_fr_left);
                bottom_mdts_fr_right = findViewById(R.id.bottom_mdts_fr_right);
                bottom_mdts_fr_total = findViewById(R.id.bottom_mdts_fr_total);
                bottom_mdts_sr_left = findViewById(R.id.bottom_mdts_sr_left);
                bottom_mdts_sr_right = findViewById(R.id.bottom_mdts_sr_right);
                bottom_mdts_sr_total = findViewById(R.id.bottom_mdts_sr_total);
                bottom_mdts_tr_left = findViewById(R.id.bottom_mdts_tr_left);
                bottom_mdts_tr_right = findViewById(R.id.bottom_mdts_tr_right);
                bottom_mdts_tr_total = findViewById(R.id.bottom_mdts_tr_total);

                bottom_mdms_fr_left = findViewById(R.id.bottom_mdms_fr_left);
                bottom_mdms_fr_right = findViewById(R.id.bottom_mdms_fr_right);
                bottom_mdms_fr_total = findViewById(R.id.bottom_mdms_fr_total);
                bottom_mdms_sr_left = findViewById(R.id.bottom_mdms_sr_left);
                bottom_mdms_sr_right = findViewById(R.id.bottom_mdms_sr_right);
                bottom_mdms_sr_total = findViewById(R.id.bottom_mdms_sr_total);
                bottom_mdms_tr_left = findViewById(R.id.bottom_mdms_tr_left);
                bottom_mdms_tr_right = findViewById(R.id.bottom_mdms_tr_right);
                bottom_mdms_tr_total = findViewById(R.id.bottom_mdms_tr_total);

                bottom_mds_fr_left = findViewById(R.id.bottom_mds_fr_left);
                bottom_mds_fr_right = findViewById(R.id.bottom_mds_fr_right);
                bottom_mds_fr_total = findViewById(R.id.bottom_mds_fr_total);
                bottom_mds_sr_left = findViewById(R.id.bottom_mds_sr_left);
                bottom_mds_sr_right = findViewById(R.id.bottom_mds_sr_right);
                bottom_mds_sr_total = findViewById(R.id.bottom_mds_sr_total);
                bottom_mds_tr_left = findViewById(R.id.bottom_mds_tr_left);
                bottom_mds_tr_right = findViewById(R.id.bottom_mds_tr_right);
                bottom_mds_tr_total = findViewById(R.id.bottom_mds_tr_total);

                ll_Downline_Team_Status = findViewById(R.id.ll_Downline_Team_Status);
                ll_Direct_Members_Status = findViewById(R.id.ll_Direct_Members_Status);
                ll_Sponsoring_Team_Status = findViewById(R.id.ll_Sponsoring_Team_Status);
                txt_main_heading = findViewById(R.id.txt_main_heading);
                txt_title_core_mem_income = findViewById(R.id.txt_title_core_mem_income);

                txt_title_core_mem_income.setSelected(true);
                txt_title_core_mem_income.setSingleLine(true);

                /*Shader textShader=new LinearGradient(40, 30, 20, 40,
                        new int[]{getResources().getColor(R.color.color_pink),Color.WHITE},
                        new float[]{0, 1}, Shader.TileMode.MIRROR);
                txt_main_heading.getPaint().setShader(textShader);*/

                View navHeaderView = navigationView.getHeaderView(0);
                txt_welcome_name = navHeaderView.findViewById(R.id.txt_welcome_name);
                txt_available_wb = navHeaderView.findViewById(R.id.txt_available_wb);
                txt_id_number = navHeaderView.findViewById(R.id.txt_id_number);
                profileImage = navHeaderView.findViewById(R.id.iv_Profile_Pic);
                LinearLayout LL_Nav = navHeaderView.findViewById(R.id.LL_Nav);

                expListView = findViewById(R.id.left_drawer);

                listDataHeader = new ArrayList<>();
                listDataChild = new HashMap<>();

                profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectImage();
                    }
                });

                iv_Profile_Pic_dash = findViewById(R.id.iv_Profile_Pic_dash);
                iv_Profile_Pic_dash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectImage();
                    }
                });

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
                        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close_dash));
                    }

                    @Override
                    public void onDrawerClosed(@NonNull View drawerView) {
                        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar));
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {

                    }
                });

            } else {
                startActivity(new Intent(act, Login_New_Activity.class).putExtra("SendToHome", true));
            }
          /*  txt_refer_url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShareApp();
                }
            });*/

            //ExpandableLayout expandableLayout, expandableLayout1,expandableLayout2, expandableLayout3, expandableLayout4 ;
            //ImageView img_dash_bottom_1, img_dash_bottom_2, img_dash_bottom_3, img_dash_bottom_4 , img_dash_bottom_5 ;
            expandableLayout1 = findViewById(R.id.expandableLayout1);
            expandableLayout2 = findViewById(R.id.expandableLayout2);
            expandableLayout3 = findViewById(R.id.expandableLayout3);

            img_dash_bottom_1 = findViewById(R.id.img_dash_bottom_1);
            img_dash_bottom_2 = findViewById(R.id.img_dash_bottom_2);
            img_dash_bottom_3 = findViewById(R.id.img_dash_bottom_3);

/*          Drawable upArrow = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_dash_bottom_0, null);
            upArrow.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null), PorterDuff.Mode.SRC_ATOP);
            img_dash_bottom_1.setBackgroundDrawable(upArrow);*/

            //  img_dash_bottom_1.setColorFilter(ContextCompat.getColor(act, R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
            img_dash_bottom_1.setTextColor(getResources().getColor(R.color.colorPrimary));
           /* Statusdrop = false;
            Statusdrop1 = true;
            Statusdrop2 = true;
            Statusdrop3 = true;
            Statusdrop4 = true;*/

            //expandableLayout1.collapse();
          //  expandableLayout2.collapse();
          //  expandableLayout3.collapse();

            img_dash_bottom_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout1(v);
                }
            });
            img_dash_bottom_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout2(v);
                }
            });
            img_dash_bottom_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout3(v);
                }
            });

            sliderView = findViewById(R.id.imageSlider);

            adapter_slider = new SliderAdapterExample(this);
            sliderView.setSliderAdapter(adapter_slider);

            sliderView.setIndicatorAnimation(IndicatorAnimations.DROP); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
            sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
            sliderView.setIndicatorSelectedColor(Color.WHITE);
            sliderView.setIndicatorUnselectedColor(Color.GRAY);
            sliderView.setScrollTimeInSec(3);
            sliderView.setAutoCycle(true);
            sliderView.startAutoCycle();

            sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
                @Override
                public void onIndicatorClicked(int position) {
                    sliderView.setCurrentPagePosition(position);
                }
            });

            continueapp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void expandableLayout1(View view) {

       /* expandableLayout1.toggle(); // toggle expand and collapse
        if (Statusdrop1) {
            Statusdrop1 = false;
        } else {
            Statusdrop1 = true;
        }
        Statusdrop2 = true;
        Statusdrop3 = true;

        expandableLayout2.collapse();
        expandableLayout3.collapse();*/

        ll_Downline_Team_Status.setVisibility(View.VISIBLE);
        ll_Direct_Members_Status.setVisibility(View.GONE);
        ll_Sponsoring_Team_Status.setVisibility(View.GONE);

        img_dash_bottom_1.setTypeface(img_dash_bottom_1.getTypeface(), Typeface.BOLD);
        img_dash_bottom_2.setTypeface(img_dash_bottom_2.getTypeface(), Typeface.NORMAL);
        img_dash_bottom_3.setTypeface(img_dash_bottom_3.getTypeface(), Typeface.NORMAL);

        img_dash_bottom_1.setTextColor(getResources().getColor(R.color.colorPrimary));
        img_dash_bottom_2.setTextColor(getResources().getColor(R.color.color_919191));
        img_dash_bottom_3.setTextColor(getResources().getColor(R.color.color_919191));
    }

    public void expandableLayout2(View view) {

       /* expandableLayout2.toggle(); // toggle expand and collapse
        if (Statusdrop2) {
            Statusdrop2 = false;
        } else {
            Statusdrop2 = true;
        }
        Statusdrop1 = true;
        Statusdrop3 = true;

        expandableLayout1.collapse();
        expandableLayout3.collapse();*/
        ll_Downline_Team_Status.setVisibility(View.GONE);
        ll_Direct_Members_Status.setVisibility(View.VISIBLE);
        ll_Sponsoring_Team_Status.setVisibility(View.GONE);

        img_dash_bottom_1.setTypeface(img_dash_bottom_1.getTypeface(), Typeface.NORMAL);
        img_dash_bottom_2.setTypeface(img_dash_bottom_2.getTypeface(), Typeface.BOLD);
        img_dash_bottom_3.setTypeface(img_dash_bottom_3.getTypeface(), Typeface.NORMAL);

        img_dash_bottom_1.setTextColor(getResources().getColor(R.color.color_919191));
        img_dash_bottom_2.setTextColor(getResources().getColor(R.color.colorPrimary));
        img_dash_bottom_3.setTextColor(getResources().getColor(R.color.color_919191));
    }

    public void expandableLayout3(View view) {

       /* expandableLayout3.toggle(); // toggle expand and collapse
        if (Statusdrop3) {
            Statusdrop3 = false;
        } else {
            Statusdrop3 = true;
        }
        Statusdrop1 = true;
        Statusdrop2 = true;

        expandableLayout1.collapse();
        expandableLayout2.collapse();*/
        ll_Downline_Team_Status.setVisibility(View.GONE);
        ll_Direct_Members_Status.setVisibility(View.GONE);
        ll_Sponsoring_Team_Status.setVisibility(View.VISIBLE);

        img_dash_bottom_1.setTypeface(img_dash_bottom_1.getTypeface(), Typeface.NORMAL);
        img_dash_bottom_2.setTypeface(img_dash_bottom_2.getTypeface(), Typeface.NORMAL);
        img_dash_bottom_3.setTypeface(img_dash_bottom_3.getTypeface(), Typeface.BOLD);

        img_dash_bottom_1.setTextColor(getResources().getColor(R.color.color_919191));
        img_dash_bottom_2.setTextColor(getResources().getColor(R.color.color_919191));
        img_dash_bottom_3.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void ShareApp() {
        try {
            final String appPackageName = getPackageName();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
            String sAux = "Join our refer program and share it with your friends";
            sAux = sAux + "\n\n" + "Rootsberry Concept (P) Ltd.";

            if (refer_url.equalsIgnoreCase("")) {
                refer_url = "https://play.google.com/store/apps/details?id=com.vpipl.rootsberry";
            }
            sAux = sAux + "\n\n" + refer_url;
            //  sAux = sAux + "\n\n https://play.google.com/store/apps/details?id=" + appPackageName;
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));
        } catch (Exception e) {
        }


    }

    public void continueapp() {
        // executeSliderPhotosRequest();

        addNewItem("http://rmconline.in/design_img/slide-1.jpg");
        addNewItem("http://rmconline.in/design_img/slide-2.jpg");

        executeGetProfilePicture();
        executeLoginRequest();
        executeGetDashBoardDetails();
        enableExpandableList();
        LoadNavigationHeaderItems();
        // executeGetDashBottomData();
    }

    @Override
    public void onBackPressed() {
        try {
            if (drawer.isDrawerOpen(navigationView)) {
                drawer.closeDrawer(navigationView);
            } else {
                showExitDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showExitDialog() {
        try {
            final Dialog dialog = AppUtils.createDialog(act, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml("Are you sure!!! Do you want to Exit from Dashboard?"));

            TextView txt_submit = dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Yes");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(act, CloseActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });

            TextView txt_cancel = dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setText(getResources().getString(R.string.txt_signout_no));
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            enableExpandableList();
            LoadNavigationHeaderItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeGetDashBoardDetails() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        AppUtils.showProgressDialog(act);
                        // displayLoader(act);
                        // avi.show();
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodDashboardDetails, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(act);
                        }

                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();
                        // displayLoader(act);
                        // avi.hide();
                        try {
                            JSONObject jsonObject = new JSONObject(resultData);

                            JSONArray jsonArrayIncome = jsonObject.getJSONArray("Income");
                            JSONArray jsonArrayMyDetails = jsonObject.getJSONArray("MyDetails");
                            JSONArray jsonArrayMyDownlineTeamStatus = jsonObject.getJSONArray("MyDowlineTeamStatus");
                            JSONArray jsonArrayMyDirectMemberStatus = jsonObject.getJSONArray("MyDirectMemberstatus");
                            JSONArray jsonArrayMySponsoringTeamStatus = jsonObject.getJSONArray("MySponsorimngTeamStatus");
                            JSONArray jsonArrayCorememberIncome = jsonObject.getJSONArray("CorememberIncome");
                            JSONArray jsonArrayRewardIncome = jsonObject.getJSONArray("RewardIncome");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                WriteValues(jsonArrayIncome, jsonArrayMyDetails, jsonArrayMyDownlineTeamStatus, jsonArrayMyDirectMemberStatus,
                                        jsonArrayMySponsoringTeamStatus, jsonArrayCorememberIncome, jsonArrayRewardIncome);
                            } else {
                                AppUtils.alertDialog(act, jsonObject.getString("Message"));
                                if (AppUtils.showLogs)
                                    Log.v(TAG, "executeGetKYCUploadRequest executed...Failed... called");
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

    private void WriteValues(JSONArray jsonArrayIncome, JSONArray jsonArrayMyDetails, JSONArray jsonArrayMyDownlineTeamStatus, JSONArray jsonArrayMyDirectMemberStatus,
                             JSONArray jsonArrayMySponsoringTeamStatus, JSONArray jsonArrayCorememberIncome, JSONArray jsonArrayRewardIncome) {
        DecimalFormat df = new DecimalFormat("#.###");
        try {
            ll_top_on_dash.setVisibility(View.VISIBLE);
//txt_Core_Member_Income, txt_Reward_income, txt_Monthly_income
            if (jsonArrayIncome.length() > 0) {
                txt_matching_bonus.setText("" + jsonArrayIncome.getJSONObject(0).getInt("totalMatchingBonus"));
                txt_Core_Member_Income.setText("" + jsonArrayIncome.getJSONObject(0).getInt("totalDirSponsorBonus"));
                txt_Monthly_income.setText(jsonArrayIncome.getJSONObject(0).getString("totalSLIIncome"));
            } else {
                txt_matching_bonus.setText("0.00");
                txt_Core_Member_Income.setText("0.00");
                txt_Monthly_income.setText("0.00");
            }
            if (jsonArrayRewardIncome.length() > 0) {
                txt_Reward_income.setText("" + jsonArrayRewardIncome.getJSONObject(0).getInt("RewardIncome"));
            } else {
                txt_Reward_income.setText("0.00");
            }
            if (jsonArrayMyDetails.length() > 0) {
                txt_Status.setText(jsonArrayMyDetails.getJSONObject(0).getString("Status"));
                txt_joining_Date.setText("Joining Date : " + jsonArrayMyDetails.getJSONObject(0).getString("JoiningDt"));
                txt_user_id.setText("ID : " + jsonArrayMyDetails.getJSONObject(0).getString("IdNo"));
                txt_ActivationDate.setText(jsonArrayMyDetails.getJSONObject(0).getString("PayDate"));
                txt_ActivationPoint.setText(jsonArrayMyDetails.getJSONObject(0).getString("BV"));
                txt_LastProfileUpdate.setText(jsonArrayMyDetails.getJSONObject(0).getString("LastUpdateTime"));
                txt_member_name.setText(jsonArrayMyDetails.getJSONObject(0).getString("Name"));
                txt_join_package.setText(jsonArrayMyDetails.getJSONObject(0).getString("Package"));
            } else {
                txt_member_name.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_NAME, ""));
                txt_join_package.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_KIT_NAME, ""));
                txt_joining_Date.setText("NA");
                txt_user_id.setText("NA");
                txt_Status.setText("NA");
                txt_ActivationDate.setText("NA");
                txt_ActivationPoint.setText("NA");
                txt_LastProfileUpdate.setText("NA");
            }
            if (jsonArrayMyDownlineTeamStatus.length() > 0) {
                bottom_mdts_fr_left.setText(jsonArrayMyDownlineTeamStatus.getJSONObject(0).getString("LeftJoining"));
                bottom_mdts_fr_right.setText(jsonArrayMyDownlineTeamStatus.getJSONObject(0).getString("RightJoining"));
                //  bottom_mdts_fr_total.setText(jsonArrayMyDownlineTeamStatus.getJSONObject(0).getString("Total"));
                Double d1 = Double.valueOf(jsonArrayMyDownlineTeamStatus.getJSONObject(0).getDouble("LeftJoining")) +
                        Double.valueOf(jsonArrayMyDownlineTeamStatus.getJSONObject(0).getDouble("RightJoining"));
                bottom_mdts_fr_total.setText("" + df.format(d1));
                bottom_mdts_sr_left.setText(jsonArrayMyDownlineTeamStatus.getJSONObject(0).getString("LeftPaid"));
                bottom_mdts_sr_right.setText(jsonArrayMyDownlineTeamStatus.getJSONObject(0).getString("RightPaid"));
                Double d2 = Double.valueOf(jsonArrayMyDownlineTeamStatus.getJSONObject(0).getDouble("LeftPaid")) +
                        Double.valueOf(jsonArrayMyDownlineTeamStatus.getJSONObject(0).getDouble("RightPaid"));
                bottom_mdts_sr_total.setText("" + df.format(d2));
                //  bottom_mdts_sr_total.setText("" + jsonArrayMyDownlineTeamStatus.getJSONObject(0).getInt("TotalPaid"));
                bottom_mdts_tr_left.setText("" + jsonArrayMyDownlineTeamStatus.getJSONObject(0).getInt("LeftBV"));
                bottom_mdts_tr_right.setText("" + jsonArrayMyDownlineTeamStatus.getJSONObject(0).getInt("RightBV"));
                Double d3 = Double.valueOf(jsonArrayMyDownlineTeamStatus.getJSONObject(0).getDouble("LeftBV")) +
                        Double.valueOf(jsonArrayMyDownlineTeamStatus.getJSONObject(0).getDouble("RightBV"));
                bottom_mdts_tr_total.setText("" + df.format(d3));
                // bottom_mdts_tr_total.setText("" + jsonArrayMyDownlineTeamStatus.getJSONObject(0).getInt("TotalBV"));
            } else {
                bottom_mdts_fr_left.setText("-");
                bottom_mdts_fr_right.setText("-");
                bottom_mdts_fr_total.setText("-");
                bottom_mdts_sr_left.setText("-");
                bottom_mdts_sr_right.setText("-");
                bottom_mdts_sr_total.setText("-");
                bottom_mdts_tr_left.setText("-");
                bottom_mdts_tr_right.setText("-");
                bottom_mdts_tr_total.setText("-");
            }
            if (jsonArrayMyDirectMemberStatus.length() > 0) {
                bottom_mdms_fr_left.setText(jsonArrayMyDirectMemberStatus.getJSONObject(0).getString("LeftJoining"));
                bottom_mdms_fr_right.setText(jsonArrayMyDirectMemberStatus.getJSONObject(0).getString("RightJoining"));
                Double d1 = Double.valueOf(jsonArrayMyDirectMemberStatus.getJSONObject(0).getDouble("LeftJoining")) +
                        Double.valueOf(jsonArrayMyDirectMemberStatus.getJSONObject(0).getDouble("RightJoining"));
                bottom_mdms_fr_total.setText("" + df.format(d1));
                // bottom_mdms_fr_total.setText(jsonArrayMyDirectMemberStatus.getJSONObject(0).getString("TotalJoining"));
                bottom_mdms_sr_left.setText(jsonArrayMyDirectMemberStatus.getJSONObject(0).getString("LeftBv"));
                bottom_mdms_sr_right.setText(jsonArrayMyDirectMemberStatus.getJSONObject(0).getString("RightBv"));
                Double d2 = Double.valueOf(jsonArrayMyDirectMemberStatus.getJSONObject(0).getDouble("LeftBv")) +
                        Double.valueOf(jsonArrayMyDirectMemberStatus.getJSONObject(0).getDouble("RightBv"));
                bottom_mdms_sr_total.setText("" + df.format(d2));
                // bottom_mdms_sr_total.setText(jsonArrayMyDirectMemberStatus.getJSONObject(0).getString("TotalBV"));
                bottom_mdms_tr_left.setText(jsonArrayMyDirectMemberStatus.getJSONObject(0).getString("LeftPV"));
                bottom_mdms_tr_right.setText(jsonArrayMyDirectMemberStatus.getJSONObject(0).getString("RightPV"));
                Double d3 = Double.valueOf(jsonArrayMyDirectMemberStatus.getJSONObject(0).getDouble("LeftPV")) +
                        Double.valueOf(jsonArrayMyDirectMemberStatus.getJSONObject(0).getDouble("RightPV"));
                bottom_mdms_tr_total.setText("" + df.format(d3));
                // bottom_mdms_tr_total.setText(jsonArrayMyDirectMemberStatus.getJSONObject(0).getString("TotalPV"));
            } else {
                bottom_mdms_fr_left.setText("-");
                bottom_mdms_fr_right.setText("-");
                bottom_mdms_fr_total.setText("-");
                bottom_mdms_sr_left.setText("-");
                bottom_mdms_sr_right.setText("-");
                bottom_mdms_sr_total.setText("-");
                bottom_mdms_tr_left.setText("-");
                bottom_mdms_tr_right.setText("-");
                bottom_mdms_tr_total.setText("-");
            }

            if (jsonArrayMySponsoringTeamStatus.length() > 0) {
                bottom_mds_fr_left.setText(jsonArrayMySponsoringTeamStatus.getJSONObject(0).getString("LeftJoining"));
                bottom_mds_fr_right.setText(jsonArrayMySponsoringTeamStatus.getJSONObject(0).getString("RightJoining"));
                Double d1 = Double.valueOf(jsonArrayMySponsoringTeamStatus.getJSONObject(0).getDouble("LeftJoining")) +
                        Double.valueOf(jsonArrayMySponsoringTeamStatus.getJSONObject(0).getDouble("RightJoining"));
                bottom_mds_fr_total.setText("" + df.format(d1));
                // bottom_mds_fr_total.setText(jsonArrayMySponsoringTeamStatus.getJSONObject(0).getString("TotalJoining"));
                bottom_mds_sr_left.setText(jsonArrayMySponsoringTeamStatus.getJSONObject(0).getString("LeftBv"));
                bottom_mds_sr_right.setText(jsonArrayMySponsoringTeamStatus.getJSONObject(0).getString("RightBv"));
                Double d2 = Double.valueOf(jsonArrayMySponsoringTeamStatus.getJSONObject(0).getDouble("LeftBv")) +
                        Double.valueOf(jsonArrayMySponsoringTeamStatus.getJSONObject(0).getDouble("RightBv"));
                bottom_mds_sr_total.setText("" + df.format(d2));
                // bottom_mds_sr_total.setText(jsonArrayMySponsoringTeamStatus.getJSONObject(0).getString("TotalBV"));
                bottom_mds_tr_left.setText(jsonArrayMySponsoringTeamStatus.getJSONObject(0).getString("LeftPV"));
                bottom_mds_tr_right.setText(jsonArrayMySponsoringTeamStatus.getJSONObject(0).getString("RightPV"));
                Double d3 = Double.valueOf(jsonArrayMySponsoringTeamStatus.getJSONObject(0).getDouble("LeftPV")) +
                        Double.valueOf(jsonArrayMySponsoringTeamStatus.getJSONObject(0).getDouble("RightPV"));
                bottom_mds_tr_total.setText("" + df.format(d3));
                // bottom_mds_tr_total.setText(jsonArrayMySponsoringTeamStatus.getJSONObject(0).getString("TotalPV"));
            } else {
                bottom_mds_fr_left.setText("-");
                bottom_mds_fr_right.setText("-");
                bottom_mds_fr_total.setText("-");
                bottom_mds_sr_left.setText("-");
                bottom_mds_sr_right.setText("-");
                bottom_mds_sr_total.setText("-");
                bottom_mds_tr_left.setText("-");
                bottom_mds_tr_right.setText("-");
                bottom_mds_tr_total.setText("-");
            }
            /*if (jsonArrayTotalIncome.length() > 0) {
                txt_total_income.setText("Total income : ₹ " + df.format(jsonArrayTotalIncome.getJSONObject(0).getDouble("TotalNetIncome")));
                String count_text = "Total Income : \u20B9 " + df.format(jsonArrayTotalIncome.getJSONObject(0).getDouble("TotalNetIncome"));
                txt_available_wb.setText(count_text);
            } else {
                txt_total_income.setText("Total income : 0");
                txt_available_wb.setText("Total income : 0");
            }*/

            /*if (jsonArrayRank.length() > 0) {
                txt_rank_name.setText("Rank : " + jsonArrayRank.getJSONObject(0).getString("Rank"));
            } else {
                txt_rank_name.setText("Rank : N/A");
            }*/

            //"LeftJoining":0,"RightJoining":0,"totalLeftRightJoining":0,"LeftPaid":0,"RightPaid":0,"TotalLeftRightPaid":0,"LeftBV":0.00,"RightBV":0.00,"TotalLeftRightBv"
           /* if (jsonArrayCurrentMonthDownlineTeamStatusStatus.length() > 0) {
                bottom_mdcms_fr_left.setText(jsonArrayCurrentMonthDownlineTeamStatusStatus.getJSONObject(0).getString("LeftJoining"));
                bottom_mdcms_fr_right.setText(jsonArrayCurrentMonthDownlineTeamStatusStatus.getJSONObject(0).getString("RightJoining"));
                Double d1 = Double.valueOf(jsonArrayCurrentMonthDownlineTeamStatusStatus.getJSONObject(0).getDouble("LeftJoining")) +
                        Double.valueOf(jsonArrayCurrentMonthDownlineTeamStatusStatus.getJSONObject(0).getDouble("RightJoining"));
                bottom_mdcms_fr_total.setText("" + df.format(d1));
                // bottom_mdcms_fr_total.setText(jsonArrayCurrentMonthDownlineTeamStatusStatus.getJSONObject(0).getString("totalLeftRightJoining"));
                bottom_mdcms_sr_left.setText(jsonArrayCurrentMonthDownlineTeamStatusStatus.getJSONObject(0).getString("LeftPaid"));
                bottom_mdcms_sr_right.setText(jsonArrayCurrentMonthDownlineTeamStatusStatus.getJSONObject(0).getString("RightPaid"));
                Double d2 = Double.valueOf(jsonArrayCurrentMonthDownlineTeamStatusStatus.getJSONObject(0).getDouble("LeftPaid")) +
                        Double.valueOf(jsonArrayCurrentMonthDownlineTeamStatusStatus.getJSONObject(0).getDouble("RightPaid"));
                bottom_mdcms_sr_total.setText("" + df.format(d2));
                //  bottom_mdcms_sr_total.setText(jsonArrayCurrentMonthDownlineTeamStatusStatus.getJSONObject(0).getString("TotalLeftRightPaid"));
                bottom_mdcms_tr_left.setText(jsonArrayCurrentMonthDownlineTeamStatusStatus.getJSONObject(0).getString("LeftBV"));
                bottom_mdcms_tr_right.setText(jsonArrayCurrentMonthDownlineTeamStatusStatus.getJSONObject(0).getString("RightBV"));
                Double d3 = Double.valueOf(jsonArrayCurrentMonthDownlineTeamStatusStatus.getJSONObject(0).getDouble("LeftBV")) +
                        Double.valueOf(jsonArrayCurrentMonthDownlineTeamStatusStatus.getJSONObject(0).getDouble("RightBV"));
                bottom_mdcms_tr_total.setText("" + df.format(d3));
                //  bottom_mdcms_tr_total.setText(jsonArrayCurrentMonthDownlineTeamStatusStatus.getJSONObject(0).getString("TotalLeftRightBv"));
            } else {
                bottom_mdcms_fr_left.setText("-");
                bottom_mdcms_fr_right.setText("-");
                bottom_mdcms_fr_total.setText("-");
                bottom_mdcms_sr_left.setText("-");
                bottom_mdcms_sr_right.setText("-");
                bottom_mdcms_sr_total.setText("-");
                bottom_mdcms_tr_left.setText("-");
                bottom_mdcms_tr_right.setText("-");
                bottom_mdcms_tr_total.setText("-");
            }*/
            /*if (jsonArrayCurrentMonthSVBDetail.length() > 0) {
                txt_direct_sponsor_bonus.setText("₹ " + df.format(jsonArrayCurrentMonthSVBDetail.getJSONObject(0).getDouble("SBVMatchingBonus")));
                txt_gold_bonus.setText("₹ " + df.format(jsonArrayCurrentMonthSVBDetail.getJSONObject(0).getDouble("SBVOverrideBonus")));
            } else {
                txt_direct_sponsor_bonus.setText("");
                txt_gold_bonus.setText("");
            }*/

        } catch (Exception e) {
            e.printStackTrace();
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
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Share Sponsor Link")) {
                    startActivity(new Intent(act, Share_Sponsor_Link_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Genereated/Issued Pin Details")) {
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

        txt_available_wb.setText("Wallet Balance :");
        //  txt_available_wb.setVisibility(View.GONE);
        txt_welcome_name.setText("Guest");

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            String welcome_text = WordUtils.capitalizeFully(AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
            txt_welcome_name.setText(welcome_text);

            txt_user_id.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, ""));

            String userid = AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "");
            txt_id_number.setText(userid);
            txt_id_number.setVisibility(View.VISIBLE);

            //   executeWalletBalanceRequest();

            String bytecode = AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "");

            if (!bytecode.equalsIgnoreCase("")) {
                profileImage.setImageBitmap(AppUtils.getBitmapFromString(bytecode));
                //Picasso.with(act).load(AppUtils.getBitmapFromString(bytecode)).transform(new CircleTransform()).into(iv_Profile_Pic_dash);
                iv_Profile_Pic_dash.setImageBitmap(AppUtils.getBitmapFromString(bytecode));
            }
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
                            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

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
           /* listDataHeader.add("Home");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);*/

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
                listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), SubMenuAl);
            }

          /*  listDataHeader.add("Logout");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeTogetDrawerMenuItems() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                // AppUtils.showProgressDialog(act);
                avi.show();
            }

            @Override
            protected String doInBackground(Void... params) {

                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                String response = "";
                try {
                    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

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
                    // AppUtils.dismissProgressDialog();
                    avi.hide();
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
                        //    AppUtils.showProgressDialog(act);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Password", AppController.getSpUserInfo().getString(SPUtils.USER_PASSWORD, "")));
                            postParameters.add(new BasicNameValuePair("UserType", "D"));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToLoginMember, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            //      AppUtils.dismissProgressDialog();

                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                //   continueapp();
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                if (jsonArrayData.length() != 0) {
                                    saveLoginUserInfo(jsonArrayData);
                                }
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
                            //   AppUtils.showExceptionDialog(act);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //   AppUtils.showExceptionDialog(act);
        }
    }

    private void saveLoginUserInfo(final JSONArray jsonArray) {
        try {
            //  AppUtils.dismissProgressDialog();

            AppController.getSpUserInfo().edit().clear().commit();

            AppController.getSpUserInfo().edit()
                    .putString(SPUtils.USER_TYPE, "DISTRIBUTOR")
                    .putString(SPUtils.USER_ID_NUMBER, jsonArray.getJSONObject(0).getString("IDNo"))
                    .putString(SPUtils.USER_PASSWORD, jsonArray.getJSONObject(0).getString("passw"))
                    .putString(SPUtils.USER_FORM_NUMBER, jsonArray.getJSONObject(0).getString("FormNo"))
                    .putString(SPUtils.USER_FIRST_NAME, jsonArray.getJSONObject(0).getString("MemFirstName"))
                    .putString(SPUtils.USER_LAST_NAME, jsonArray.getJSONObject(0).getString("MemLastName"))
                    .putString(SPUtils.USER_MOBILE_NO, jsonArray.getJSONObject(0).getString("Mobl"))
                    .putString(SPUtils.USER_KIT_ID, jsonArray.getJSONObject(0).getString("KitId"))
                    .putString(SPUtils.USER_STATE_CODE, jsonArray.getJSONObject(0).getString("statecode"))
                    .putString(SPUtils.USER_ACTIVE_STATUS, jsonArray.getJSONObject(0).getString("ActiveStatus"))
                    .putString(SPUtils.USER_KIT_NAME, jsonArray.getJSONObject(0).getString("KitName"))
                    .putString(SPUtils.USER_IS_PORTAL, jsonArray.getJSONObject(0).getString("isPortal"))
                    .putString(SPUtils.USER_ADDRESS, jsonArray.getJSONObject(0).getString("Address1"))
                    .putString(SPUtils.USER_CITY, jsonArray.getJSONObject(0).getString("City"))
                    .putString(SPUtils.USER_DISTRICT, jsonArray.getJSONObject(0).getString("District"))
                    .putString(SPUtils.USER_PINCODE, jsonArray.getJSONObject(0).getString("Pincode"))
                    // .putString(SPUtils.USER_EMAIL, jsonArray.getJSONObject(0).getString("EMail"))
                    .putString(SPUtils.USER_EMAIL, "")
                    .putString(SPUtils.USER_DOJ, AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("DOJ")))
                    .commit();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void executeGetProfilePicture() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));

                            //ImageType----AddrProof=AP,IdentityProof=IP,PhotoProof=PP,Signature=S,CancelChq=CC,SpousePic=SP,All=*
                            postParameters.add(new BasicNameValuePair("ImageType", "PP"));

                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodGetImages, TAG);
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
                                if (!jsonArrayData.getJSONObject(0).getString("PhotoProof").equals("")) {
                                    Byte_Code = jsonArrayData.getJSONObject(0).getString("PhotoProof");
                                    AppController.getSpUserInfo().edit().putString(SPUtils.USER_profile_pic_byte_code, Byte_Code).commit();
                                    profileImage.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));
                                    iv_Profile_Pic_dash.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //   AppUtils.showExceptionDialog(act);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //  AppUtils.showExceptionDialog(act);
        }
    }

    /*Code added by mukesh executeGetDashBottomData added 13-01-2020 02:31 PM*/
    private void executeGetDashBottomData() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodtoDashBoardBottomData, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayDownLineTeamStatus = jsonObject.getJSONArray("DownLineTeamStatus");
                                JSONArray jsonArrayDirectMemberStatus = jsonObject.getJSONArray("DirectMemberStatus");
                                JSONArray jsonArrayDownLineStatus = jsonObject.getJSONArray("DownLineStatus");
                                JSONArray jsonArrayDownLineCurrentMonthStatus = jsonObject.getJSONArray("DownLineCurrentMonthStatus");

                                WriteValuesBottom(jsonArrayDownLineTeamStatus, jsonArrayDirectMemberStatus, jsonArrayDownLineStatus, jsonArrayDownLineCurrentMonthStatus);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //  AppUtils.showExceptionDialog(act);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // AppUtils.showExceptionDialog(act);
        }
    }

    private void WriteValuesBottom(JSONArray jsonArrayDownLineTeamStatus, JSONArray jsonArrayDirectMemberStatus, JSONArray jsonArrayDownLineStatus,
                                   JSONArray jsonArrayDownLineCurrentMonthStatus) {
        DecimalFormat df = new DecimalFormat("#.###");
        try {
            //  ll_bottom_on_dash.setVisibility(View.VISIBLE);

            if (jsonArrayDownLineTeamStatus.length() > 0) {
                bottom_mdts_fr_left.setText(jsonArrayDownLineTeamStatus.getJSONObject(0).getString("LeftJoining"));
                bottom_mdts_fr_right.setText(jsonArrayDownLineTeamStatus.getJSONObject(0).getString("RightJoining"));
                bottom_mdts_fr_total.setText(jsonArrayDownLineTeamStatus.getJSONObject(0).getString("Total"));
                bottom_mdts_sr_left.setText(jsonArrayDownLineTeamStatus.getJSONObject(0).getString("LeftPaid"));
                bottom_mdts_sr_right.setText(jsonArrayDownLineTeamStatus.getJSONObject(0).getString("RightPaid"));
                bottom_mdts_sr_total.setText(jsonArrayDownLineTeamStatus.getJSONObject(0).getString("TotalPaid"));
                bottom_mdts_tr_left.setText(jsonArrayDownLineTeamStatus.getJSONObject(0).getString("LeftBV"));
                bottom_mdts_tr_right.setText(jsonArrayDownLineTeamStatus.getJSONObject(0).getString("RightBV"));
                bottom_mdts_tr_total.setText(jsonArrayDownLineTeamStatus.getJSONObject(0).getString("TotalLeftRightBv"));
            } else {
                bottom_mdts_fr_left.setText("-");
                bottom_mdts_fr_right.setText("-");
                bottom_mdts_fr_total.setText("-");
                bottom_mdts_sr_left.setText("-");
                bottom_mdts_sr_right.setText("-");
                bottom_mdts_sr_total.setText("-");
                bottom_mdts_tr_left.setText("-");
                bottom_mdts_tr_right.setText("-");
                bottom_mdts_tr_total.setText("-");
            }
            if (jsonArrayDirectMemberStatus.length() > 0) {
                bottom_mdms_fr_left.setText(jsonArrayDirectMemberStatus.getJSONObject(0).getString("LeftJoining"));
                bottom_mdms_fr_right.setText(jsonArrayDirectMemberStatus.getJSONObject(0).getString("RightJoining"));
                bottom_mdms_fr_total.setText(jsonArrayDirectMemberStatus.getJSONObject(0).getString("TotalJoining"));
                bottom_mdms_sr_left.setText(jsonArrayDirectMemberStatus.getJSONObject(0).getString("LeftBv"));
                bottom_mdms_sr_right.setText(jsonArrayDirectMemberStatus.getJSONObject(0).getString("RightBv"));
                bottom_mdms_sr_total.setText(jsonArrayDirectMemberStatus.getJSONObject(0).getString("TotalLeftRightBv"));
                bottom_mdms_tr_left.setText(jsonArrayDirectMemberStatus.getJSONObject(0).getString("LeftPV"));
                bottom_mdms_tr_right.setText(jsonArrayDirectMemberStatus.getJSONObject(0).getString("RightPV"));
                bottom_mdms_tr_total.setText(jsonArrayDirectMemberStatus.getJSONObject(0).getString("TotalLeftRightPV"));
            } else {
                bottom_mdms_fr_left.setText("-");
                bottom_mdms_fr_right.setText("-");
                bottom_mdms_fr_total.setText("-");
                bottom_mdms_sr_left.setText("-");
                bottom_mdms_sr_right.setText("-");
                bottom_mdms_sr_total.setText("-");
                bottom_mdms_tr_left.setText("-");
                bottom_mdms_tr_right.setText("-");
                bottom_mdms_tr_total.setText("-");
            }
            if (jsonArrayDownLineStatus.length() > 0) {
                bottom_mds_fr_left.setText(jsonArrayDownLineStatus.getJSONObject(0).getString("LeftJoining"));
                bottom_mds_fr_right.setText(jsonArrayDownLineStatus.getJSONObject(0).getString("RightJoining"));
                bottom_mds_fr_total.setText(jsonArrayDownLineStatus.getJSONObject(0).getString("TotalLeftRightJoining"));
                bottom_mds_sr_left.setText(jsonArrayDownLineStatus.getJSONObject(0).getString("LeftPaid"));
                bottom_mds_sr_right.setText(jsonArrayDownLineStatus.getJSONObject(0).getString("RightPaid"));
                bottom_mds_sr_total.setText(jsonArrayDownLineStatus.getJSONObject(0).getString("TotalLeftRightPaid"));
                bottom_mds_tr_left.setText(jsonArrayDownLineStatus.getJSONObject(0).getString("LeftBV"));
                bottom_mds_tr_right.setText(jsonArrayDownLineStatus.getJSONObject(0).getString("RightBV"));
                bottom_mds_tr_total.setText(jsonArrayDownLineStatus.getJSONObject(0).getString("LeftRightBV"));
            } else {
                bottom_mds_fr_left.setText("-");
                bottom_mds_fr_right.setText("-");
                bottom_mds_fr_total.setText("-");
                bottom_mds_sr_left.setText("-");
                bottom_mds_sr_right.setText("-");
                bottom_mds_sr_total.setText("-");
                bottom_mds_tr_left.setText("-");
                bottom_mds_tr_right.setText("-");
                bottom_mds_tr_total.setText("-");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Code added by mukesh dashbaord photo selection 02-01-2020*/
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //   boolean result = Utility.checkPermission(act);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    // if (result)
                    cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    //   if (result)
                    galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        String imageStoragePath = destination.getAbsolutePath();
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        executePostImageUploadRequest(bitmap);
        profileImage.setImageBitmap(bitmap);
        iv_Profile_Pic_dash.setImageBitmap(bitmap);

        Log.e("from camera data", imageStoragePath);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        executePostImageUploadRequest(bm);
        profileImage.setImageBitmap(bm);
        iv_Profile_Pic_dash.setImageBitmap(bm);
        String imagepath = bm.toString();
        Log.e("from gallery data", imagepath);
    }

    private void executePostImageUploadRequest(final Bitmap bitmap) {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                           AppUtils.showProgressDialog(act);
                     //   avi.show();
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Type", "PP"));
                            postParameters.add(new BasicNameValuePair("ImageByteCode", AppUtils.getBase64StringFromBitmap(bitmap)));

                            try {
                                postParameters.add(new BasicNameValuePair("IPAddress", "" + AppUtils.getDeviceID(act)));
                            } catch (Exception ignored) {
                            }

                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodUploadImages, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(act);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                             AppUtils.dismissProgressDialog();
                          //  avi.hide();

                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (!jsonArrayData.getJSONObject(0).getString("PhotoProof").equals("")) {
                                    profileImage.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));
                                    iv_Profile_Pic_dash.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));
                                    Cache.getInstance().getLru().put("profileImage", AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));
                                    AppController.getSpUserInfo().edit().putString(SPUtils.USER_profile_pic_byte_code, (jsonArrayData.getJSONObject(0).getString("PhotoProof"))).commit();
                                }
                            } else {
                                //   AppUtils.alertDialog(act, jsonObject.getString("Message"));
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

    private void displayLoader(Context con) {
        dialog = new Dialog(con);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_progress_bar);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    public void addNewItem(String URL) {
        SliderItem sliderItem = new SliderItem();
        sliderItem.setDescription("");
        sliderItem.setImageUrl("" + URL);
        adapter_slider.addItem(sliderItem);
    }

    private void executeSliderPhotosRequest() {
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
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToSelectSliderImages, TAG);
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
                                for (int i = 0; i < jsonObject.getJSONArray("Data").length(); i++) {
                                    JSONObject jsonObject1 = jsonObject.getJSONArray("Data").getJSONObject(i);
                                    addNewItem(AppUtils.productImageURL() + jsonObject1.getString("ImageUrl"));
                                }
                            } else {
                                //   AppUtils.alertDialog(act, jsonObject.getString("Message"));
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
}