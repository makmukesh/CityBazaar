package com.vpipl.citybazaar;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.github.aakira.expandablelayout.ExpandableLayout;
import com.google.android.material.textfield.TextInputEditText;

import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mukesh on 27/12/2019.
 */
public class Profile_View_New_Activity extends AppCompatActivity {

    Activity act = Profile_View_New_Activity.this;
    public static ArrayList<HashMap<String, String>> myprofileDetailsList = new ArrayList<>();
    private static String Byte_Code;
    private String TAG = "Profile_View_New_Activity";
    private TextInputEditText txt_memberID;
    private TextInputEditText txt_memberName;
    private TextInputEditText txt_mobileNumber;
    private TextInputEditText txt_email;
    private TextInputEditText txt_aadhaarnumber;
    private TextInputEditText txt_GSTNumber;
    private TextInputEditText txt_coSponsorID;
    private TextInputEditText txt_dob;
    private TextInputEditText txt_phoneNumber;
    private TextInputEditText txt_address;

    private TextInputEditText txt_city;
    private TextInputEditText txt_district;
    private TextInputEditText txt_state;
    private TextInputEditText txt_pinCode;
    private TextInputEditText txt_PANNumber;
    private TextInputEditText txt_sponsorID;
    private TextInputEditText txt_sponsorName;
    private TextInputEditText txt_spo_position;
    private TextInputEditText txt_bankName;
    private TextInputEditText txt_bankAcntNumber;
    private TextInputEditText txt_bankIfsc;
    private TextInputEditText txt_bankBranch;
    private TextInputEditText txt_nomineeName;
    private TextInputEditText txt_nomineeRelation;

    private TextInputEditText txt_phoneno, txt_GooglePayNo, txt_PaytmNo;
    private TextView txt_prefix;
    private TextView txt_father_name;
    private TextInputEditText txt_gender;
    private TextInputEditText txt_nominee_dob;
    private ImageView iv_profile_pic;
    private Button btn_updateMyProfile;

    ExpandableLayout expandableLayout, expandableLayout2, expandableLayout3, expandableLayout4, expandableLayout5, expandableLayout6, expandableLayout7;
    ImageView drop, drop2, drop3, drop4, drop5, drop6, drop7;
    Boolean Statusdrop = false, Statusdrop2 = false, Statusdrop3 = false, Statusdrop4 = false, Statusdrop5 = false, Statusdrop6 = false, Statusdrop7 = false;
    RelativeLayout rl_0, rl_2, rl_3, rl_4, rl_5, rl_6, rl_7;

    private JSONArray HeadingJarray;

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

    // Animation
    Animation animUpDown;

    private void edit_false() {
        txt_memberID.setInputType(InputType.TYPE_NULL);
        txt_memberID.setKeyListener(null);
        txt_memberID.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_memberName.setInputType(InputType.TYPE_NULL);
        txt_memberName.setKeyListener(null);
        txt_memberName.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_mobileNumber.setInputType(InputType.TYPE_NULL);
        txt_mobileNumber.setKeyListener(null);
        txt_mobileNumber.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_email.setInputType(InputType.TYPE_NULL);
        txt_email.setKeyListener(null);
        txt_email.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_aadhaarnumber.setInputType(InputType.TYPE_NULL);
        txt_aadhaarnumber.setKeyListener(null);
        txt_aadhaarnumber.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_GSTNumber.setInputType(InputType.TYPE_NULL);
        txt_GSTNumber.setKeyListener(null);
        txt_GSTNumber.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_coSponsorID.setInputType(InputType.TYPE_NULL);
        txt_coSponsorID.setKeyListener(null);
        txt_coSponsorID.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_dob.setInputType(InputType.TYPE_NULL);
        txt_dob.setKeyListener(null);
        txt_dob.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_phoneNumber.setInputType(InputType.TYPE_NULL);
        txt_phoneNumber.setKeyListener(null);
        txt_phoneNumber.setHintTextColor(getResources().getColor(R.color.colorPrimary));
        txt_phoneNumber.setClickable(false);

        txt_address.setInputType(InputType.TYPE_NULL);
        txt_address.setKeyListener(null);
        txt_address.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_city.setInputType(InputType.TYPE_NULL);
        txt_city.setKeyListener(null);
        txt_city.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_district.setInputType(InputType.TYPE_NULL);
        txt_district.setKeyListener(null);
        txt_district.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_state.setInputType(InputType.TYPE_NULL);
        txt_state.setKeyListener(null);
        txt_state.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_pinCode.setInputType(InputType.TYPE_NULL);
        txt_pinCode.setKeyListener(null);
        txt_pinCode.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_PANNumber.setInputType(InputType.TYPE_NULL);
        txt_PANNumber.setKeyListener(null);
        txt_PANNumber.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_sponsorID.setInputType(InputType.TYPE_NULL);
        txt_sponsorID.setKeyListener(null);
        txt_sponsorID.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_sponsorName.setInputType(InputType.TYPE_NULL);
        txt_sponsorName.setKeyListener(null);
        txt_sponsorName.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_spo_position.setInputType(InputType.TYPE_NULL);
        txt_spo_position.setKeyListener(null);
        txt_spo_position.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_bankName.setInputType(InputType.TYPE_NULL);
        txt_bankName.setKeyListener(null);
        txt_bankName.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_bankAcntNumber.setInputType(InputType.TYPE_NULL);
        txt_bankAcntNumber.setKeyListener(null);
        txt_bankAcntNumber.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_bankIfsc.setInputType(InputType.TYPE_NULL);
        txt_bankIfsc.setKeyListener(null);
        txt_bankIfsc.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_bankBranch.setInputType(InputType.TYPE_NULL);
        txt_bankBranch.setKeyListener(null);
        txt_bankBranch.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_nomineeName.setInputType(InputType.TYPE_NULL);
        txt_nomineeName.setKeyListener(null);
        txt_nomineeName.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_nomineeRelation.setInputType(InputType.TYPE_NULL);
        txt_nomineeRelation.setKeyListener(null);
        txt_nomineeRelation.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_gender.setInputType(InputType.TYPE_NULL);
        txt_gender.setKeyListener(null);
        txt_gender.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_nominee_dob.setInputType(InputType.TYPE_NULL);
        txt_nominee_dob.setKeyListener(null);
        txt_nominee_dob.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_phoneno.setInputType(InputType.TYPE_NULL);
        txt_phoneno.setKeyListener(null);
        txt_phoneno.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_GooglePayNo.setInputType(InputType.TYPE_NULL);
        txt_GooglePayNo.setKeyListener(null);
        txt_GooglePayNo.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        txt_PaytmNo.setInputType(InputType.TYPE_NULL);
        txt_PaytmNo.setKeyListener(null);
        txt_PaytmNo.setHintTextColor(getResources().getColor(R.color.colorPrimary));

        /*  txt_memberID;          txt_memberName;          txt_mobileNumber;          txt_email;          txt_aadhaarnumber;          txt_coSponsorID;
          txt_dob;          txt_phoneNumber;          txt_address;          txt_city;          txt_district;          txt_state;          txt_pinCode;
          txt_PANNumber;          txt_sponsorID;          txt_sponsorName;          txt_bankName;          txt_bankAcntNumber;          txt_bankIfsc;
          txt_bankBranch;          txt_nomineeName;          txt_nomineeRelation;          txt_gender;          txt_nominee_dob;*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view_new);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            AppUtils.changeStatusBarColor(act);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            iv_profile_pic = findViewById(R.id.iv_Profile_Pic);

            txt_memberID = findViewById(R.id.txt_memberID);
            txt_coSponsorID = findViewById(R.id.txt_coSponsorID);
            txt_memberName = findViewById(R.id.txt_memberName);
            txt_mobileNumber = findViewById(R.id.txt_mobileNumber);
            txt_email = findViewById(R.id.txt_email);
            txt_aadhaarnumber = findViewById(R.id.txt_aadhaarnumber);
            txt_GSTNumber = findViewById(R.id.txt_GSTNumber);

            txt_prefix = findViewById(R.id.txt_prefix);
            txt_father_name = findViewById(R.id.txt_father_name);
            txt_gender = findViewById(R.id.txt_gender);

            txt_dob = findViewById(R.id.txt_dob);
            txt_phoneNumber = findViewById(R.id.txt_phoneNumber);
            txt_address = findViewById(R.id.txt_address);
            txt_city = findViewById(R.id.txt_city);
            txt_district = findViewById(R.id.txt_district);
            txt_state = findViewById(R.id.txt_state);
            txt_pinCode = findViewById(R.id.txt_pinCode);
            txt_PANNumber = findViewById(R.id.txt_PANNumber);

            txt_sponsorID = findViewById(R.id.txt_sponsorID);
            txt_sponsorName = findViewById(R.id.txt_sponsorName);
            txt_spo_position = findViewById(R.id.txt_spo_position);

            txt_bankName = findViewById(R.id.txt_bankName);
            txt_bankAcntNumber = findViewById(R.id.txt_bankAcntNumber);
            txt_bankIfsc = findViewById(R.id.txt_bankIfsc);
            txt_bankBranch = findViewById(R.id.txt_bankBranch);

            txt_nomineeName = findViewById(R.id.txt_nomineeName);
            txt_nominee_dob = findViewById(R.id.txt_nominee_dob);
            txt_nomineeRelation = findViewById(R.id.txt_nomineeRelation);

            txt_phoneno = findViewById(R.id.txt_phoneno);
            txt_GooglePayNo = findViewById(R.id.txt_GooglePayNo);
            txt_PaytmNo = findViewById(R.id.txt_PaytmNo);

            edit_false();

            btn_updateMyProfile = findViewById(R.id.btn_updateMyProfile);
            // expandableLayout ,expandableLayout2,expandableLayout3,expandableLayout4,expandableLayout5,expandableLayout6
            expandableLayout = findViewById(R.id.expandableLayout);
            expandableLayout2 = findViewById(R.id.expandableLayout2);
            expandableLayout3 = findViewById(R.id.expandableLayout3);
            expandableLayout4 = findViewById(R.id.expandableLayout4);
            expandableLayout5 = findViewById(R.id.expandableLayout5);
            expandableLayout6 = findViewById(R.id.expandableLayout6);
            expandableLayout7 = findViewById(R.id.expandableLayout7);

            drop = findViewById(R.id.drop);
            drop2 = findViewById(R.id.drop2);
            drop3 = findViewById(R.id.drop3);
            drop4 = findViewById(R.id.drop4);
            drop5 = findViewById(R.id.drop5);
            drop6 = findViewById(R.id.drop6);
            drop7 = findViewById(R.id.drop7);

            rl_0 = findViewById(R.id.rl_0);
            rl_2 = findViewById(R.id.rl_2);
            rl_3 = findViewById(R.id.rl_3);
            rl_4 = findViewById(R.id.rl_4);
            rl_5 = findViewById(R.id.rl_5);
            rl_6 = findViewById(R.id.rl_6);
            rl_7 = findViewById(R.id.rl_7);

            rl_0.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            btn_updateMyProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(act, Profile_Update_Activity.class));
                        }
                    };
                    new Handler().postDelayed(runnable, 500);
                }
            });

            myprofileDetailsList.clear();

            if (AppUtils.isNetworkAvailable(act)) {


            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }

            String bytecode = AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "");

            if (bytecode.equalsIgnoreCase(""))
                executeGetProfilePicture();
            else
                iv_profile_pic.setImageBitmap(AppUtils.getBitmapFromString(bytecode));

            executeLoginRequest();

//            rl_0 ,rl_2 ,rl_3 ,rl_4 ,rl_5 ,rl_6
            rl_0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout(v);
                }
            });
            rl_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout2(v);
                }
            });
            rl_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout3(v);
                }
            });
            rl_4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout4(v);
                }
            });
            rl_5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout5(v);
                }
            });
            rl_6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout6(v);
                }
            });
            rl_7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout7(v);
                }
            });

            drop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout(v);
                }
            });
            drop2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout2(v);
                }
            });
            drop3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout3(v);
                }
            });
            drop4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout4(v);
                }
            });
            drop5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout5(v);
                }
            });
            drop6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout6(v);
                }
            });
            drop7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout7(v);
                }
            });

            expandableLayout2.collapse();
            expandableLayout3.collapse();
            expandableLayout4.collapse();
            expandableLayout5.collapse();
            expandableLayout6.collapse();
            expandableLayout7.collapse();

            // load the animation
            animUpDown = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.up_down);
            //    android.R.anim.linear_interpolator);

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    public void expandableLayout(View view) {

        expandableLayout.toggle(); // toggle expand and collapse
        if (Statusdrop) {
            drop.setImageResource(R.drawable.ic_expand_less_white_profile);
            Statusdrop = false;
            rl_0.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            drop.setImageResource(R.drawable.ic_expand_more_white_profile);
            Statusdrop = true;
            rl_0.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        }
        drop2.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop3.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop4.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop5.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop6.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop7.setImageResource(R.drawable.ic_expand_more_white_profile);

        drop.startAnimation(animUpDown);

        Statusdrop2 = true;
        Statusdrop3 = true;
        Statusdrop4 = true;
        Statusdrop5 = true;
        Statusdrop6 = true;
        Statusdrop7 = true;

        expandableLayout2.collapse();
        expandableLayout3.collapse();
        expandableLayout4.collapse();
        expandableLayout5.collapse();
        expandableLayout6.collapse();
        expandableLayout7.collapse();

        //  rl_0.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        rl_2.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_3.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_4.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_5.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_6.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_7.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
    }

    public void expandableLayout2(View view) {

        expandableLayout2.toggle(); // toggle expand and collapse
        if (Statusdrop2) {
            drop2.setImageResource(R.drawable.ic_expand_less_white_profile);
            Statusdrop2 = false;
            rl_2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            drop2.setImageResource(R.drawable.ic_expand_more_white_profile);
            Statusdrop2 = true;
            rl_2.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        }
        drop.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop3.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop4.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop5.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop6.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop7.setImageResource(R.drawable.ic_expand_more_white_profile);

        drop2.startAnimation(animUpDown);

        Statusdrop = true;
        Statusdrop3 = true;
        Statusdrop4 = true;
        Statusdrop5 = true;
        Statusdrop6 = true;
        Statusdrop7 = true;

        expandableLayout.collapse();
        expandableLayout3.collapse();
        expandableLayout4.collapse();
        expandableLayout5.collapse();
        expandableLayout6.collapse();
        expandableLayout7.collapse();

        rl_0.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        //rl_2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        rl_3.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_4.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_5.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_6.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_7.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
    }

    public void expandableLayout3(View view) {

        expandableLayout3.toggle(); // toggle expand and collapse
        if (Statusdrop3) {
            drop3.setImageResource(R.drawable.ic_expand_less_white_profile);
            Statusdrop3 = false;
            rl_3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            drop3.setImageResource(R.drawable.ic_expand_more_white_profile);
            Statusdrop3 = true;
            rl_3.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        }
        drop.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop2.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop4.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop5.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop6.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop7.setImageResource(R.drawable.ic_expand_more_white_profile);

        drop3.startAnimation(animUpDown);

        Statusdrop = true;
        Statusdrop2 = true;
        Statusdrop4 = true;
        Statusdrop5 = true;
        Statusdrop6 = true;
        Statusdrop7 = true;
        expandableLayout.collapse();
        expandableLayout2.collapse();
        expandableLayout4.collapse();
        expandableLayout5.collapse();
        expandableLayout6.collapse();
        expandableLayout7.collapse();

        rl_0.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_2.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        //rl_3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        rl_4.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_5.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_6.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_7.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
    }

    public void expandableLayout4(View view) {

        expandableLayout4.toggle(); // toggle expand and collapse
        if (Statusdrop4) {
            drop4.setImageResource(R.drawable.ic_expand_less_white_profile);
            Statusdrop4 = false;
            rl_4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            drop4.setImageResource(R.drawable.ic_expand_more_white_profile);
            Statusdrop4 = true;
            rl_4.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        }
        drop.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop2.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop3.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop5.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop6.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop7.setImageResource(R.drawable.ic_expand_more_white_profile);

        drop4.startAnimation(animUpDown);

        Statusdrop = true;
        Statusdrop2 = true;
        Statusdrop3 = true;
        Statusdrop5 = true;
        Statusdrop6 = true;
        Statusdrop7 = true;
        expandableLayout.collapse();
        expandableLayout2.collapse();
        expandableLayout3.collapse();
        expandableLayout5.collapse();
        expandableLayout6.collapse();
        expandableLayout7.collapse();

        rl_0.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_2.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_3.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        //rl_4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        rl_5.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_6.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_7.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
    }

    public void expandableLayout5(View view) {

        expandableLayout5.toggle(); // toggle expand and collapse
        if (Statusdrop5) {
            drop5.setImageResource(R.drawable.ic_expand_less_white_profile);
            Statusdrop5 = false;
            rl_5.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            drop5.setImageResource(R.drawable.ic_expand_more_white_profile);
            Statusdrop5 = true;
            rl_5.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        }
        drop.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop2.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop3.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop4.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop6.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop7.setImageResource(R.drawable.ic_expand_more_white_profile);

        drop5.startAnimation(animUpDown);

        Statusdrop = true;
        Statusdrop2 = true;
        Statusdrop3 = true;
        Statusdrop4 = true;
        Statusdrop6 = true;
        Statusdrop7 = true;
        expandableLayout.collapse();
        expandableLayout2.collapse();
        expandableLayout3.collapse();
        expandableLayout4.collapse();
        expandableLayout6.collapse();
        expandableLayout7.collapse();

        rl_0.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_2.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_3.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_4.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        //rl_5.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        rl_6.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_7.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
    }

    public void expandableLayout6(View view) {

        expandableLayout6.toggle(); // toggle expand and collapse
        if (Statusdrop6) {
            drop6.setImageResource(R.drawable.ic_expand_less_white_profile);
            Statusdrop6 = false;
            rl_6.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            drop6.setImageResource(R.drawable.ic_expand_more_white_profile);
            Statusdrop6 = true;
            rl_6.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        }
        drop.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop2.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop3.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop4.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop5.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop7.setImageResource(R.drawable.ic_expand_more_white_profile);

        drop6.startAnimation(animUpDown);

        Statusdrop = true;
        Statusdrop2 = true;
        Statusdrop3 = true;
        Statusdrop4 = true;
        Statusdrop5 = true;
        Statusdrop7 = true;
        expandableLayout.collapse();
        expandableLayout2.collapse();
        expandableLayout3.collapse();
        expandableLayout4.collapse();
        expandableLayout5.collapse();
        expandableLayout7.collapse();

        rl_0.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_2.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_3.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_4.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_5.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        //rl_6.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        rl_7.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    public void expandableLayout7(View view) {

        expandableLayout7.toggle(); // toggle expand and collapse
        if (Statusdrop7) {
            drop7.setImageResource(R.drawable.ic_expand_less_white_profile);
            Statusdrop7 = false;
            rl_7.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            drop7.setImageResource(R.drawable.ic_expand_more_white_profile);
            Statusdrop7 = true;
            rl_7.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        }
        drop.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop2.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop3.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop4.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop5.setImageResource(R.drawable.ic_expand_more_white_profile);
        drop6.setImageResource(R.drawable.ic_expand_more_white_profile);

        drop7.startAnimation(animUpDown);

        Statusdrop = true;
        Statusdrop2 = true;
        Statusdrop3 = true;
        Statusdrop4 = true;
        Statusdrop5 = true;
        Statusdrop6 = true;
        expandableLayout.collapse();
        expandableLayout2.collapse();
        expandableLayout3.collapse();
        expandableLayout4.collapse();
        expandableLayout5.collapse();
        expandableLayout6.collapse();

        rl_0.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_2.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_3.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_4.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_5.setBackgroundColor(getResources().getColor(R.color.color_orange_profile));
        rl_6.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
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
                    response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodMaster_FillState, TAG);
                } catch (Exception ignored) {
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {
                    JSONObject jsonObject = new JSONObject(resultData);
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            getStateResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(act, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(act, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getStateResult(JSONArray jsonArray) {
        try {
            AppController.stateList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("STATECODE", jsonObject.getString("STATECODE"));
                map.put("State", WordUtils.capitalizeFully(jsonObject.getString("State")));

                AppController.stateList.add(map);
            }
            executeBankRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeBankRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(act);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodMaster_FillBank, TAG);
                } catch (Exception ignored) {
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {
                    JSONObject jsonObject = new JSONObject(resultData);
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            getBankResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(act, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(act, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getBankResult(JSONArray jsonArray) {
        try {
            AppController.bankList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("BID", jsonObject.getString("BID"));
                map.put("Bank", WordUtils.capitalizeFully(jsonObject.getString("Bank")));

                AppController.bankList.add(map);
            }

            executeToGetProfileInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToGetProfileInfo() {
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
                            postParameters.add(new BasicNameValuePair("Formno", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToGetUserProfile, TAG);
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
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (jsonArrayData.length() != 0) {
                                    getProfileInfo(jsonArrayData);
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

    private void getProfileInfo(JSONArray jsonArray) {
        try {

            AppController.getSpUserInfo().edit()
                    .putString(SPUtils.USER_ID_NUMBER, jsonArray.getJSONObject(0).getString("IDNo"))
                    .putString(SPUtils.USER_FORM_NUMBER, jsonArray.getJSONObject(0).getString("FormNo"))
                    .putString(SPUtils.USER_FIRST_NAME, WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("MemName")))
                    .putString(SPUtils.USER_PINCODE, jsonArray.getJSONObject(0).getString("Pincode"))
                    .putString(SPUtils.USER_CITY, jsonArray.getJSONObject(0).getString("CityName"))
                    .putString(SPUtils.USER_STATE_CODE, jsonArray.getJSONObject(0).getString("StateCode"))
                    .commit();

            myprofileDetailsList.clear();

            HashMap<String, String> map = new HashMap<>();
            map.put(SPUtils.USER_ID_NUMBER, "" + jsonArray.getJSONObject(0).getString("IDNo"));
            map.put(SPUtils.USER_CoSponsorID, "" + jsonArray.getJSONObject(0).getString("UpLnFormNo"));
            map.put(SPUtils.USER_NAME, "" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("MemName")));
            map.put(SPUtils.USER_FATHER_NAME, "" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("MemFName")));
            map.put(SPUtils.USER_Relation_Prefix, "" + jsonArray.getJSONObject(0).getString("MemRelation"));
            map.put(SPUtils.USER_FORM_NUMBER, "" + jsonArray.getJSONObject(0).getString("FormNo"));
            map.put(SPUtils.USER_PASSWORD, "" + jsonArray.getJSONObject(0).getString("Passw"));
            map.put(SPUtils.USER_ADDRESS, "" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("Address1")));
            map.put(SPUtils.USER_MOBILE_NO, "" + jsonArray.getJSONObject(0).getString("Mobl"));
            map.put(SPUtils.USER_Phone_NO, "" + jsonArray.getJSONObject(0).getString("PhN1"));

            if (AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("MemDOB")).equalsIgnoreCase("01-Jan-1900")) {
                map.put(SPUtils.USER_DOB, "");
            } else {
                map.put(SPUtils.USER_DOB, "" + AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("MemDOB")));
            }

            map.put(SPUtils.USER_GENDER, "" + jsonArray.getJSONObject(0).getString("MemGender"));
            map.put(SPUtils.USER_EMAIL, "" + jsonArray.getJSONObject(0).getString("Email"));
            // map.put(SPUtils.USER_AADHAAR, "" + jsonArray.getJSONObject(0).getString("AdhaarNo"));
            map.put(SPUtils.USER_CITY, "" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("CityName")));

            String StateName = "";
            for (int i = 0; i < AppController.stateList.size(); i++) {
                if (jsonArray.getJSONObject(0).getString("StateCode").equalsIgnoreCase(AppController.stateList.get(i).get("STATECODE"))) {
                    StateName = AppController.stateList.get(i).get("State");
                }
            }
            map.put(SPUtils.USER_STATE, "" + StateName);

            map.put(SPUtils.USER_DISTRICT, "" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("DistrictName")));
            map.put(SPUtils.USER_PINCODE, "" + jsonArray.getJSONObject(0).getString("Pincode"));
            //  map.put(SPUtils.USER_GSTNo, "" + jsonArray.getJSONObject(0).getString("MemGSTIN"));
            map.put(SPUtils.USER_PAN, "" + jsonArray.getJSONObject(0).getString("PanNo"));
            map.put(SPUtils.USER_CATEGORY, "" + jsonArray.getJSONObject(0).getString("Category"));
            map.put(SPUtils.USER_SPONSOR_ID, "" + jsonArray.getJSONObject(0).getString("RefIdNo"));
            map.put(SPUtils.USER_SPONSOR_NAME, "" + jsonArray.getJSONObject(0).getString("RefName"));

            String BankName = "";
            for (int i = 0; i < AppController.bankList.size(); i++) {
                if (jsonArray.getJSONObject(0).getString("BankID").equalsIgnoreCase(AppController.bankList.get(i).get("BID"))) {
                    BankName = AppController.bankList.get(i).get("Bank");
                }
            }

            map.put(SPUtils.USER_BANKNAME, "" + BankName);
            map.put(SPUtils.USER_BANKACNTNUM, "" + jsonArray.getJSONObject(0).getString("AcNo"));
            map.put(SPUtils.USER_BANKIFSC, "" + jsonArray.getJSONObject(0).getString("IFSCode"));
            map.put(SPUtils.USER_BANKBRANCH, "" + jsonArray.getJSONObject(0).getString("Fld4"));
            map.put(SPUtils.USER_NOMINEE_NAME, "" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("NomineeName")));
            map.put(SPUtils.USER_NOMINEE_RELATION, "" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("Relation")));

            map.put(SPUtils.USER_Phoneno, "" + jsonArray.getJSONObject(0).getString("PhonePeNo"));
            map.put(SPUtils.USER_GooglePayNo, "" + jsonArray.getJSONObject(0).getString("GooglePayNo"));
            map.put(SPUtils.USER_PaytmNo, "" + jsonArray.getJSONObject(0).getString("PaytmNo"));

            map.put(SPUtils.USER_SIDE, "" + jsonArray.getJSONObject(0).getString("Side"));
            if (AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("NomineeDob")).equalsIgnoreCase("14-Mar-1905")) {
                map.put(SPUtils.USER_NOMINEE_DOB, "");
            } else {
                map.put(SPUtils.USER_NOMINEE_DOB, "" + AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("NomineeDob")));
            }
//          map.put(SPUtils.USER_POSITION,""+ jsonArray.getJSONObject(0).getString("MemLegNo").toString());

            myprofileDetailsList.add(map);
            setProfileDetails();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void setProfileDetails() {
        try {

            txt_memberID.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_ID_NUMBER));
            txt_coSponsorID.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_CoSponsorID));
            txt_memberName.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_NAME));

            String gender = "Male";
            if (myprofileDetailsList.get(0).get(SPUtils.USER_GENDER).equalsIgnoreCase("M")) {
                gender = "Male";
            } else if (myprofileDetailsList.get(0).get(SPUtils.USER_GENDER).equalsIgnoreCase("F")) {
                gender = "Female";
            } else if (myprofileDetailsList.get(0).get(SPUtils.USER_GENDER).equalsIgnoreCase("O")) {
                gender = "Other";
            } else {
                gender = "Male";
            }
            txt_gender.setText("" + gender);
            txt_mobileNumber.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_MOBILE_NO));
            txt_email.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_EMAIL));
            //  txt_aadhaarnumber.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_AADHAAR));

            txt_prefix.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_Relation_Prefix));
            txt_father_name.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_FATHER_NAME));


            txt_dob.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_DOB));
            txt_phoneNumber.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_Phone_NO));
            txt_address.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_ADDRESS));
            txt_city.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_CITY));
            txt_district.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_DISTRICT));
            txt_state.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_STATE));
            txt_pinCode.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_PINCODE));
            txt_PANNumber.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_PAN));
            // txt_GSTNumber.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_GSTNo));

            txt_sponsorID.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_SPONSOR_ID));
            txt_sponsorName.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_SPONSOR_NAME));

            String str = "";
            str = myprofileDetailsList.get(0).get(SPUtils.USER_SIDE);
            if (str.equalsIgnoreCase("")) {
                txt_spo_position.setText("");
            } else {
                txt_spo_position.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_SIDE));
            }

            txt_bankName.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_BANKNAME));
            txt_bankAcntNumber.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_BANKACNTNUM));
            txt_bankIfsc.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_BANKIFSC));
            txt_bankBranch.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_BANKBRANCH));

            txt_nomineeName.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_NOMINEE_NAME));
            txt_nominee_dob.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_NOMINEE_DOB));
            txt_nomineeRelation.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_NOMINEE_RELATION));

            txt_phoneno.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_Phoneno));
            txt_GooglePayNo.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_GooglePayNo));
            txt_PaytmNo.setText("" + myprofileDetailsList.get(0).get(SPUtils.USER_PaytmNo));

            String bytecode = AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "");
            if (bytecode.length() > 0) {
                iv_profile_pic.setImageBitmap(AppUtils.getBitmapFromString(AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            executeLoginRequest();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
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

        System.gc();
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
                                    iv_profile_pic.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));

                                    //  profileImage.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));

                                }
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

        if (AppController.stateList.size() == 0) {
            executeStateRequest();
        } else if (AppController.bankList.size() == 0) {
            executeBankRequest();
        } else {
            executeToGetProfileInfo();
        }
    }
}