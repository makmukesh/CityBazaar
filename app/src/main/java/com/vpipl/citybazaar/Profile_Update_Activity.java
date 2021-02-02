package com.vpipl.citybazaar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vpipl.citybazaar.SMS.MySMSBroadcastReceiver;
import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.Cache;
import com.vpipl.citybazaar.Utils.CircularImageView;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;
import com.vpipl.citybazaar.Utils.SmsListener;
import com.vpipl.citybazaar.model.StackHelperCity;
import com.vpipl.citybazaar.model.StackHelperState;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Mukesh on 27/12/2019.
 */
public class Profile_Update_Activity extends AppCompatActivity {

    Activity act = Profile_Update_Activity.this;
    public int RESULT_GALLERY = 0;
    public int CAMERA_REQUEST = 1;

    private String TAG = "Profile_Update_Activity";
    private TextInputEditText edtxt_memberName;
    private TextInputEditText edtxt_father_name;
    private TextInputEditText edtxt_address;
    private TextInputEditText edtxt_district;
    private TextInputEditText edtxt_city;
    private TextInputEditText edtxt_pinCode;
    private TextInputEditText edtxt_mobileNumber;
    private TextInputEditText edtxt_phoneNumber;
    private TextInputEditText edtxt_email;
    private TextInputEditText edtxt_aadhaarNumber;
    private TextInputEditText edtxt_bankIfsc;
    private TextInputEditText edtxt_bankBranch;
    private TextInputEditText edtxt_bankAcntNumber;
    private TextInputEditText edtxt_PANNumber;
    private TextInputEditText edtxt_GstNumber;
    private TextInputEditText edtxt_nomineeName;
    private TextInputEditText edtxt_nomineeRelation;
    private TextInputEditText txt_dob;
    private TextInputEditText txt_nominee_dob;
    private TextInputEditText txt_prefix;
    private TextInputEditText txt_state;
    private TextInputEditText txt_bankname;
    private TextInputEditText edtxt_phonepayno,edtxt_googlepayno,edtxt_paytmno;
    private Button btn_updateProfilesendotp;

    RadioButton rb_male, rb_female, rb_other;
    RadioGroup rg_gender;
    private CircularImageView iv_upload;
    private ImageView iv_Profile_Pic;
    private String[] stateArray;
    private String[] bankArray;
    private String[] selectRelationArray;
    private String onWhichDateClick = "";
    private String Name;
    private String Prefix;
    private String FatherName;
    private String dob;
    private String address;
    private String state;
    private String district;
    private String city;
    private String pincode;
    private String mobile_number;
    private String aadhaarNumber;
    private String phone_number;
    private String email;
    private String nominee_name;
    private String nominee_dob;
    private String nominee_relation;
    private String bank_ifsc;
    private String bank_name;
    private String bank_account_number;

    String Gender;
    private String bank_branch_name;
    private String pan_number;
    private String gst_number;
    private String phonepayno,googlepayno,paytmno;
    private Uri imageUri;
    private Bitmap bitmap = null;
    private Calendar myCalendar;
    private SimpleDateFormat sdf;
    private String selectedImagePath = "";
    private BottomSheetDialog mBottomSheetDialog;
    LinearLayout ll_update_profile_data, ll_update_profile_enter_otp;
    String recieve_otp = "";
    DatePickerDialog datePickerDialog;
    EditText ed_otp;
    Button btn_updateProfileafterotp;

    List<StackHelperState> stackHelperList = new ArrayList<>();
    List<StackHelperCity> stackHelperListcity = new ArrayList<>();
    // String jsonStr_franchiselist = "";
    JSONArray jsonarray_statelist;
    JSONArray jsonarray_franchiselist;
    String Statecode = "0", Statename = "", franchisecode = "0", franchiseName = "";
    Spinner spinner_selectstate, spinner_selectfranchise;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask;

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            try {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                if (new Date().after(myCalendar.getTime())) {
                    if (onWhichDateClick.equals("et_dob")) {
                        txt_dob.setText(sdf.format(myCalendar.getTime()));
                    } else {
                        txt_nominee_dob.setText(sdf.format(myCalendar.getTime()));
                    }
                } else {
                    AppUtils.alertDialog(act, getResources().getString(R.string.error_invalid_dob));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

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
        setContentView(R.layout.activity_profile_update);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            AppUtils.changeStatusBarColor(act);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();


            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


            selectRelationArray = getResources().getStringArray(R.array.selectRelation);

            myCalendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("dd MMM yyyy");

            iv_Profile_Pic = findViewById(R.id.iv_Profile_Pic);
            iv_upload = findViewById(R.id.iv_upload);

            txt_dob = findViewById(R.id.txt_dob);
            txt_nominee_dob = findViewById(R.id.txt_nominee_dob);
            txt_prefix = findViewById(R.id.txt_prefix);
            txt_state = findViewById(R.id.txt_state);
            txt_bankname = findViewById(R.id.txt_bankname);
            ed_otp = findViewById(R.id.ed_otp);
            btn_updateProfileafterotp = findViewById(R.id.btn_updateProfileafterotp);

            rg_gender = (RadioGroup) findViewById(R.id.rg_gender);
            rb_male = (RadioButton) findViewById(R.id.rb_male);
            rb_female = (RadioButton) findViewById(R.id.rb_female);
            rb_other = (RadioButton) findViewById(R.id.rb_other);

            edtxt_memberName = findViewById(R.id.edtxt_memberName);
            edtxt_father_name = findViewById(R.id.edtxt_father_name);
            edtxt_address = findViewById(R.id.edtxt_address);
            edtxt_district = findViewById(R.id.edtxt_district);
            edtxt_city = findViewById(R.id.edtxt_city);
            edtxt_pinCode = findViewById(R.id.edtxt_pinCode);
            edtxt_mobileNumber = findViewById(R.id.edtxt_mobileNumber);
            edtxt_phoneNumber = findViewById(R.id.edtxt_phoneNumber);
            edtxt_email = findViewById(R.id.edtxt_email);
            edtxt_aadhaarNumber = findViewById(R.id.edtxt_aadhaarNumber);
            edtxt_nomineeName = findViewById(R.id.edtxt_nomineeName);
            edtxt_nomineeRelation = findViewById(R.id.edtxt_nomineeRelation);
            edtxt_bankIfsc = findViewById(R.id.edtxt_bankIfsc);
            edtxt_bankAcntNumber = findViewById(R.id.edtxt_bankAcntNumber);
            edtxt_bankBranch = findViewById(R.id.edtxt_bankBranch);
            edtxt_PANNumber = findViewById(R.id.edtxt_PANNumber);
            edtxt_GstNumber = findViewById(R.id.edtxt_GstNumber);
            ll_update_profile_enter_otp = findViewById(R.id.ll_update_profile_enter_otp);
            ll_update_profile_data = findViewById(R.id.ll_update_profile_data);
            spinner_selectstate = findViewById(R.id.spinner_selectstate);
            spinner_selectfranchise = findViewById(R.id.spinner_selectfranchise);
            edtxt_phonepayno = findViewById(R.id.edtxt_phonepayno);
            edtxt_googlepayno = findViewById(R.id.edtxt_googlepayno);
            edtxt_paytmno = findViewById(R.id.edtxt_paytmno);

            edtxt_PANNumber.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            edtxt_bankIfsc.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

            jsonarray_statelist = new JSONArray();
            jsonarray_franchiselist = new JSONArray();


            iv_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectImage();
                }
            });

            edtxt_bankIfsc.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    final int DRAWABLE_RIGHT = 2;

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (edtxt_bankIfsc.getRight() - edtxt_bankIfsc.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            if (AppUtils.isNetworkAvailable(act)) {
                                if (!edtxt_bankIfsc.getText().toString().isEmpty()) {
                                    if (edtxt_bankIfsc.getText().toString().trim().length() == 11) {

                                        executeToGetIFSCInfo(edtxt_bankIfsc.getText().toString());
                                    } else {
                                        AppUtils.alertDialog(act, getResources().getString(R.string.error_et_mr_Ifsc));
                                        edtxt_bankIfsc.requestFocus();
                                    }
                                } else {
                                    AppUtils.alertDialog(act, getResources().getString(R.string.error_et_Ifsc));
                                    edtxt_bankIfsc.requestFocus();
                                }
                            } else {
                                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
                            }
                            return true;
                        }
                    }
                    return false;
                }
            });

            txt_state.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        if (AppController.stateList.size() != 0) {
                            showStateDialog();
                            txt_state.clearFocus();
                        } else {
                            executeStateRequest();
                        }
                    }
                }
            });


            txt_bankname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        if (AppController.bankList.size() != 0) {
                            showBankDialog();
                            txt_bankname.clearFocus();
                        } else {
                            executeBankRequest();
                        }
                    }
                }
            });
            /*SmsReceiver.bindListener(new SmsListener() {
                @Override
                public void messageReceived(String messageText) {
                    try {
                        if (messageText.length() == 6) {
                            ed_otp.setText(messageText);
                            ValidateData();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });*/
            MySMSBroadcastReceiver.bindListener(new SmsListener() {
                @Override
                public void messageReceived(String messageText) {
                    try {
                        if (messageText.length() == 6) {
                            ed_otp.setText(messageText);
                            ValidateData();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            ed_otp.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() >= 6) {
                        btn_updateProfileafterotp.setVisibility(View.VISIBLE);
                      /*  tv_otp_expired.setVisibility(View.GONE);
                        tv_resend.setVisibility(View.GONE);*/
                    } else {
                        btn_updateProfileafterotp.setVisibility(View.GONE);
                    }
                }
            });


            txt_prefix.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                    if (b) {
                        if (selectRelationArray.length != 0) {
                            showMemRelationDialog();
                            txt_prefix.clearFocus();
                        } else {
                            AppUtils.showExceptionDialog(act);
                        }
                    }
                }
            });

            btn_updateProfilesendotp = findViewById(R.id.btn_updateProfilesendotp);
            btn_updateProfilesendotp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(act, view);
                    validateUpdateProfileRequest();
                }
            });

            txt_dob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onWhichDateClick = "et_dob";
                    showDOBPicker();
                }
            });

            txt_nominee_dob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onWhichDateClick = "et_nomineeDob";
                    showdatePicker();
                }
            });


            btn_updateProfileafterotp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ValidateData();
                }
            });

            if (AppUtils.isNetworkAvailable(act)) {

                executeLoginRequest();
                executeBankRequestone();
                // executeStateRequestone();
                executeStateRequest();

                /*spinner state and frachisee list*/
                spinner_selectstate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                        if (jsonarray_statelist.length() > 0) {
                            Statename = ((StackHelperState) spinner_selectstate.getSelectedItem()).getStateName();
                            Statecode = ((StackHelperState) spinner_selectstate.getSelectedItem()).getCode();

                            if (Statecode.equalsIgnoreCase("0")) {
                                Statename = "";
                            }
                            //     executeFranchiseelistRequest(Statename);
                        }
                    }

                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });

                spinner_selectfranchise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                        if (jsonarray_franchiselist.length() > 0) {
                            franchiseName = ((StackHelperCity) spinner_selectfranchise.getSelectedItem()).getName();
                            franchisecode = ((StackHelperCity) spinner_selectfranchise.getSelectedItem()).getCode();
                        }
                    }

                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });

            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }


        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void showDOBPicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, myDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.YEAR, -18);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        datePickerDialog.show();
    }

    private void showdatePicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, myDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
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
//                            postParameters.add(new BasicNameValuePair("UserID", userId));
//                            postParameters.add(new BasicNameValuePair("Password", passwd));

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
        executeToGetProfileInfo();
    }

    private void validateUpdateProfileRequest() {
        try {

            Name = edtxt_memberName.getText().toString().trim();
            Prefix = txt_prefix.getText().toString().trim();
            FatherName = edtxt_father_name.getText().toString().trim();

            int selectedId = rg_gender.getCheckedRadioButtonId();
            RadioButton rb_gender = (RadioButton) findViewById(selectedId);
            Gender = rb_gender.getText().toString().trim();

            dob = txt_dob.getText().toString().trim();
            address = edtxt_address.getText().toString().trim();
            state = txt_state.getText().toString().trim();

            district = edtxt_district.getText().toString().trim();
            city = edtxt_city.getText().toString().trim();
            pincode = edtxt_pinCode.getText().toString().trim();
            mobile_number = edtxt_mobileNumber.getText().toString().trim();
            aadhaarNumber = edtxt_aadhaarNumber.getText().toString().trim();
            phone_number = edtxt_phoneNumber.getText().toString().trim();
            email = edtxt_email.getText().toString().trim();
            nominee_name = edtxt_nomineeName.getText().toString().trim();
            nominee_dob = txt_nominee_dob.getText().toString().trim();
            nominee_relation = edtxt_nomineeRelation.getText().toString().trim();
            bank_ifsc = edtxt_bankIfsc.getText().toString().trim();
            bank_name = txt_bankname.getText().toString().trim();
            bank_account_number = edtxt_bankAcntNumber.getText().toString().trim();
            bank_branch_name = edtxt_bankBranch.getText().toString().trim();
            pan_number = edtxt_PANNumber.getText().toString().trim();
            gst_number = edtxt_GstNumber.getText().toString().trim();
            phonepayno = edtxt_phonepayno.getText().toString().trim();
            googlepayno = edtxt_googlepayno.getText().toString().trim();
            paytmno = edtxt_paytmno.getText().toString().trim();

            if (TextUtils.isEmpty(Name)) {
                AppUtils.alertDialog(act, getResources().getString(R.string.error_et_mr_name));
                edtxt_memberName.requestFocus();
//            } else if (TextUtils.isEmpty(Prefix)) {
//                AppUtils.alertDialog(act, getResources().getString(R.string.error_et_mr_fname_prefix));
//            } else if (TextUtils.isEmpty(FatherName)) {
//                AppUtils.alertDialog(act, getResources().getString(R.string.error_et_mr_fname));
//                edtxt_father_name.requestFocus();
//            } else if (TextUtils.isEmpty(dob)) {
//                AppUtils.alertDialog(act, getResources().getString(R.string.error_et_mr_date));
//                txt_dob.requestFocus();
            } else if (TextUtils.isEmpty(address)) {
                AppUtils.alertDialog(act, getResources().getString(R.string.error_et_mr_address));
                edtxt_address.requestFocus();
//            } else if (TextUtils.isEmpty(city)) {
//                AppUtils.alertDialog(act, "Please Enter City.");
//                edtxt_city.requestFocus();
//            } else if (TextUtils.isEmpty(district)) {
//                AppUtils.alertDialog(act, "Please Enter District.");
//                edtxt_district.requestFocus();
            } else if (TextUtils.isEmpty(state)) {
                AppUtils.alertDialog(act, getResources().getString(R.string.error_et_mr_state));
                txt_state.requestFocus();
//            } else if (TextUtils.isEmpty(pincode)) {
//                AppUtils.alertDialog(act, "Please Enter PinCode.");
//                edtxt_pinCode.requestFocus();
            } else if (!TextUtils.isEmpty(pincode) && !pincode.trim().matches(AppUtils.mPINCodePattern)) {
                AppUtils.alertDialog(act, getResources().getString(R.string.error_et_mr_PINno));
                edtxt_pinCode.requestFocus();
            } else if (TextUtils.isEmpty(mobile_number)) {
                AppUtils.alertDialog(act, getResources().getString(R.string.error_required_mobile_number));
                edtxt_mobileNumber.requestFocus();
            } else if (mobile_number.trim().length() != 10) {
                AppUtils.alertDialog(act, getResources().getString(R.string.error_invalid_mobile_number));
                edtxt_mobileNumber.requestFocus();
//            } else if (!TextUtils.isEmpty(phone_number))
//            {
//                AppUtils.alertDialog(act, getResources().getString(R.string.error_et_mr_phoneNumber));
//                edtxt_phoneNumber.requestFocus();
            } else if (!TextUtils.isEmpty(phone_number) && phone_number.trim().length() != 10) {
                AppUtils.alertDialog(act, "Alternate Mobile Number is Invalid");
                edtxt_phoneNumber.requestFocus();
            } else if (!TextUtils.isEmpty(email) && AppUtils.isValidMail(email.trim())) {
                AppUtils.alertDialog(act, getResources().getString(R.string.error_invalid_email));
                edtxt_email.requestFocus();
            } else if (!TextUtils.isEmpty(aadhaarNumber) && aadhaarNumber.length() != 12) {
                AppUtils.alertDialog(act, "Invalid Aadhaar Number");
                edtxt_aadhaarNumber.requestFocus();
            } else if (!TextUtils.isEmpty(pan_number) && !pan_number.matches(AppUtils.mPANPattern)) {
                AppUtils.alertDialog(act, getResources().getString(R.string.error_invalid_PANno));
                edtxt_PANNumber.requestFocus();
            } else if (!TextUtils.isEmpty(gst_number) && !AppUtils.isValidGSTNo(gst_number.trim())) {
                AppUtils.alertDialog(act, "Invalid GST Number.");
                edtxt_GstNumber.requestFocus();
            } else if (!AppUtils.isNetworkAvailable(act)) {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            } else {
                startUpdateProfile();
                //  executeSendOtpForUpdateProfileRequest();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void startUpdateProfile() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
//edtxt_phonepayno,edtxt_googlepayno,edtxt_paytmno
                List<NameValuePair> postParameters = new ArrayList<>();
/*FormNo:Name:Prefix:FatherName:DOB:Address:StateCode:City:District:PinCode:MobileNo1:MobileNo2:EMailID:Gender:IFSCCode:BankID:BranchName:AccountNo:
PanNo:NoMineeName:Relation:NomineeDOB:PhonePeNo:GooglePayNo:PaytmNo:IPAddress:*/
                postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("Name", "" + Name));
                postParameters.add(new BasicNameValuePair("Prefix", "" + Prefix));
                postParameters.add(new BasicNameValuePair("FatherName", "" + FatherName));
                postParameters.add(new BasicNameValuePair("DOB", "" + dob.trim()));
                postParameters.add(new BasicNameValuePair("Address", "" + address.trim()));
                postParameters.add(new BasicNameValuePair("StateCode", "" + Statecode));
                postParameters.add(new BasicNameValuePair("City", "" + city.trim()));
                postParameters.add(new BasicNameValuePair("District", "" + district.trim()));
                postParameters.add(new BasicNameValuePair("PinCode", "" + pincode.trim()));
                postParameters.add(new BasicNameValuePair("MobileNo1", "" + mobile_number.trim()));
                postParameters.add(new BasicNameValuePair("MobileNo2", "" + phone_number.trim()));
                postParameters.add(new BasicNameValuePair("EMailID", "" + email.trim()));
                if(!Gender.equalsIgnoreCase("")) {
                    postParameters.add(new BasicNameValuePair("Gender", "" + Gender.charAt(0)));
                }
                else {
                    postParameters.add(new BasicNameValuePair("Gender", "M"));
                }
                String Bankid = "0";
                for (int i = 0; i < AppController.bankList.size(); i++) {
                    if (bank_name.equalsIgnoreCase(AppController.bankList.get(i).get("Bank"))) {
                        Bankid = AppController.bankList.get(i).get("BID");
                    }
                }
                postParameters.add(new BasicNameValuePair("IFSCCode", "" + bank_ifsc.trim()));
                postParameters.add(new BasicNameValuePair("BankID", "" + Bankid));
                postParameters.add(new BasicNameValuePair("BranchName", "" + bank_branch_name.trim()));
                postParameters.add(new BasicNameValuePair("AccountNo", "" + bank_account_number.trim()));
                postParameters.add(new BasicNameValuePair("PanNo", "" + pan_number.trim()));
                postParameters.add(new BasicNameValuePair("NoMineeName", "" + nominee_name.trim()));
                postParameters.add(new BasicNameValuePair("Relation", "" + nominee_relation.trim()));
                postParameters.add(new BasicNameValuePair("NomineeDOB", "" + nominee_dob.trim()));
                postParameters.add(new BasicNameValuePair("PhonePeNo", "" + phonepayno));
                postParameters.add(new BasicNameValuePair("GooglePayNo", "" + googlepayno));
                postParameters.add(new BasicNameValuePair("PaytmNo", "" + paytmno));
                postParameters.add(new BasicNameValuePair("IPAddress", AppUtils.getDeviceID(act)));

                executeUpdateprofileRequest(postParameters);
            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void executeToGetIFSCInfo(final String ifscCode) {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(act);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("IFSCCode", ifscCode));

                    response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodGet_BankDetail, TAG);
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

                            for (int i = 0; i < AppController.bankList.size(); i++) {
                                if (jsonArrayData.getJSONObject(0).getString("Branchcode").equals(AppController.bankList.get(i).get("BID"))) {
                                    txt_bankname.setText(AppController.bankList.get(i).get("Bank"));
                                }
                            }
                            edtxt_bankBranch.setText(jsonArrayData.getJSONObject(0).getString("Branch"));
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
                    AppUtils.dismissProgressDialog();

                    JSONObject jsonObject = new JSONObject(resultData);
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            getStateResult(jsonArrayData);

                            jsonarray_statelist = jsonArrayData;
                            spinState();
                        } else {
                            jsonarray_statelist = new JSONArray("[{\"STATECODE\":0,\"State\":\"-- No State Found --\"}]");
                            spinState();
                        }
//
                        executeToGetProfileInfo();
                      /*    } else {
                            AppUtils.alertDialog(act, jsonObject.getString("Message"));
                        }*/
                    } else {
                        jsonarray_statelist = new JSONArray("[{\"STATECODE\":0,\"State\":\"-- No State Found --\"}]");
                        spinState();
                        //    executeToGetProfileInfo();
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

            // showStateDialog();
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


            showBankDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showStateDialog() {
        try {
            stateArray = new String[AppController.stateList.size()];
            for (int i = 0; i < AppController.stateList.size(); i++) {
                stateArray[i] = AppController.stateList.get(i).get("State");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select State");
            builder.setItems(stateArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_state.setText(stateArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void showBankDialog() {
        try {
            bankArray = new String[AppController.bankList.size()];
            for (int i = 0; i < AppController.bankList.size(); i++) {
                bankArray[i] = AppController.bankList.get(i).get("Bank");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Bank");
            builder.setItems(bankArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_bankname.setText(bankArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
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
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
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
                    .putString(SPUtils.USER_FIRST_NAME, jsonArray.getJSONObject(0).getString("MemName"))
                    .putString(SPUtils.USER_PINCODE, jsonArray.getJSONObject(0).getString("Pincode"))
                    .putString(SPUtils.USER_CITY, jsonArray.getJSONObject(0).getString("CityName"))
                    .putString(SPUtils.USER_STATE_CODE, jsonArray.getJSONObject(0).getString("StateCode"))
                    .commit();

            Profile_View_New_Activity.myprofileDetailsList.clear();
            HashMap<String, String> map = new HashMap<>();
            map.put(SPUtils.USER_ID_NUMBER, "" + jsonArray.getJSONObject(0).getString("IDNo"));
            map.put(SPUtils.USER_CoSponsorID, "" + jsonArray.getJSONObject(0).getString("UpLnFormNo"));
            map.put(SPUtils.USER_NAME, "" + jsonArray.getJSONObject(0).getString("MemName"));
            map.put(SPUtils.USER_FATHER_NAME, "" + jsonArray.getJSONObject(0).getString("MemFName"));
            map.put(SPUtils.USER_Relation_Prefix, "" + jsonArray.getJSONObject(0).getString("MemRelation"));
            map.put(SPUtils.USER_FORM_NUMBER, "" + jsonArray.getJSONObject(0).getString("FormNo"));
            map.put(SPUtils.USER_PASSWORD, "" + jsonArray.getJSONObject(0).getString("Passw"));
            map.put(SPUtils.USER_ADDRESS, "" + jsonArray.getJSONObject(0).getString("Address1"));
            map.put(SPUtils.USER_MOBILE_NO, "" + jsonArray.getJSONObject(0).getString("Mobl"));
            map.put(SPUtils.USER_Phone_NO, "" + jsonArray.getJSONObject(0).getString("PhN1"));
            map.put(SPUtils.USER_DOB, "" + AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("MemDOB")));
            map.put(SPUtils.USER_GENDER, "" + jsonArray.getJSONObject(0).getString("MemGender"));
            map.put(SPUtils.USER_EMAIL, "" + jsonArray.getJSONObject(0).getString("Email"));
           // map.put(SPUtils.USER_AADHAAR, "" + jsonArray.getJSONObject(0).getString("AdhaarNo"));
            map.put(SPUtils.USER_CITY, "" + jsonArray.getJSONObject(0).getString("CityName"));

            String StateName = "";

            for (int i = 0; i < AppController.stateList.size(); i++) {
                if (jsonArray.getJSONObject(0).getString("StateCode").equals(AppController.stateList.get(i).get("STATECODE"))) {
                    StateName = AppController.stateList.get(i).get("State");
                }
            }
            map.put(SPUtils.USER_STATE, "" + StateName);

            map.put(SPUtils.USER_DISTRICT, "" + jsonArray.getJSONObject(0).getString("DistrictName"));
            map.put(SPUtils.USER_PINCODE, "" + jsonArray.getJSONObject(0).getString("Pincode"));
            map.put(SPUtils.USER_PAN, "" + jsonArray.getJSONObject(0).getString("PanNo"));
          //  map.put(SPUtils.USER_GSTNo, "" + jsonArray.getJSONObject(0).getString("MemGSTIN"));
            map.put(SPUtils.USER_CATEGORY, "" + jsonArray.getJSONObject(0).getString("Category"));
            map.put(SPUtils.USER_SPONSOR_ID, "" + jsonArray.getJSONObject(0).getString("UpLnId"));
            map.put(SPUtils.USER_SPONSOR_NAME, "" + jsonArray.getJSONObject(0).getString("UpLnName"));

            String BankName = "";
            for (int i = 0; i < AppController.bankList.size(); i++) {
                if (jsonArray.getJSONObject(0).getString("BankID").equals(AppController.bankList.get(i).get("BID"))) {
                    BankName = AppController.bankList.get(i).get("Bank");
                }
            }
            map.put(SPUtils.USER_BANKNAME, "" + BankName);
            map.put(SPUtils.USER_BANKACNTNUM, "" + jsonArray.getJSONObject(0).getString("AcNo"));
            map.put(SPUtils.USER_BANKIFSC, "" + jsonArray.getJSONObject(0).getString("IFSCode"));
            map.put(SPUtils.USER_BANKBRANCH, "" + jsonArray.getJSONObject(0).getString("Fld4"));
            map.put(SPUtils.USER_NOMINEE_NAME, "" + jsonArray.getJSONObject(0).getString("NomineeName"));
            map.put(SPUtils.USER_NOMINEE_RELATION, "" + jsonArray.getJSONObject(0).getString("Relation"));
            map.put(SPUtils.USER_NOMINEE_DOB, "" + AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("NomineeDob")));
            map.put(SPUtils.USER_Phoneno, "" + jsonArray.getJSONObject(0).getString("PhonePeNo"));
            map.put(SPUtils.USER_GooglePayNo, "" + jsonArray.getJSONObject(0).getString("GooglePayNo"));
            map.put(SPUtils.USER_PaytmNo, "" + jsonArray.getJSONObject(0).getString("PaytmNo"));
            /*if (AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("NomineeDob")).equalsIgnoreCase("14-Mar-1905")) {
                map.put(SPUtils.USER_NOMINEE_DOB, "");
            } else {
                map.put(SPUtils.USER_NOMINEE_DOB, "" + AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("NomineeDob")));
            }*/
           // map.put(SPUtils.USER_PartyCode, "" + jsonArray.getJSONObject(0).getString("PartyCode"));

            Profile_View_New_Activity.myprofileDetailsList.add(map);

            setProfileDetails();
            try {
                int stateposition = 0;
                for (int i = 0; i < jsonarray_statelist.length(); i++) {
                    JSONObject jsonObject = jsonarray_statelist.getJSONObject(i);
                    if (jsonArray.getJSONObject(0).getString("StateCode").equals(jsonObject.getString("STATECODE"))) {
                        stateposition = i;
                    }
                    Log.d("Type", jsonarray_statelist.getString(i));
                }
                spinner_selectstate.setSelection(stateposition);
            } catch (Exception e) {
                e.printStackTrace();
            }
          /*  try {
                int franposition = 0;
                for (int i = 0; i < jsonarray_franchiselist.length(); i++) {
                    JSONObject jsonObject = jsonarray_franchiselist.getJSONObject(i);
                    if (jsonArray.getJSONObject(0).getString("PartyCode").equals(jsonObject.getString("Rn"))) {
                        franposition = i;
                    }
                    Log.d("franpositionType", jsonarray_franchiselist.getString(i));
                }
                spinner_selectfranchise.setSelection(franposition);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void setProfileDetails() {
        try {

            edtxt_memberName.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_NAME));
            edtxt_father_name.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_FATHER_NAME));
            txt_dob.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_DOB));
            edtxt_address.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_ADDRESS));

            txt_state.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_STATE));

            edtxt_district.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_DISTRICT));
            edtxt_city.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_CITY));
            edtxt_pinCode.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_PINCODE));
            edtxt_mobileNumber.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_MOBILE_NO));
            edtxt_phoneNumber.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_Phone_NO));
            edtxt_email.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_EMAIL));
           // edtxt_aadhaarNumber.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_AADHAAR));
            edtxt_nomineeName.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_NOMINEE_NAME));
            txt_nominee_dob.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_NOMINEE_DOB));
            edtxt_nomineeRelation.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_NOMINEE_RELATION));

            edtxt_bankAcntNumber.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_BANKACNTNUM));
            txt_bankname.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_BANKNAME));
            edtxt_bankBranch.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_BANKBRANCH));
            edtxt_bankIfsc.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_BANKIFSC));
            edtxt_PANNumber.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_PAN));
          //  edtxt_GstNumber.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_GSTNo));

            txt_prefix.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_Relation_Prefix));

            edtxt_phonepayno.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_Phoneno));
            edtxt_googlepayno.setText("" + Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_GooglePayNo));
            edtxt_paytmno.setText("" +Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_PaytmNo));
            String G = Profile_View_New_Activity.myprofileDetailsList.get(0).get(SPUtils.USER_GENDER);

            if (G.equalsIgnoreCase("M"))
                rb_male.setChecked(true);
            else if (G.equalsIgnoreCase("F"))
                rb_female.setChecked(true);
            else if (G.equalsIgnoreCase("O"))
                rb_other.setChecked(true);
            else
                rb_male.setChecked(true);

            edtxt_memberName.setClickable(false);
//          edtxt_memberName.setEnabled(false);
            edtxt_memberName.setFocusable(false);
            edtxt_memberName.setFocusableInTouchMode(false);
            edtxt_memberName.setCursorVisible(false);

            edtxt_mobileNumber.setClickable(false);
            edtxt_mobileNumber.setFocusable(false);
            edtxt_mobileNumber.setFocusableInTouchMode(false);
            edtxt_mobileNumber.setCursorVisible(false);

            if (!edtxt_PANNumber.getText().toString().isEmpty()) {
                edtxt_PANNumber.setClickable(false);
                edtxt_PANNumber.setFocusable(false);
                edtxt_PANNumber.setFocusableInTouchMode(false);
                edtxt_PANNumber.setCursorVisible(false);
            }
         /*   if (!edtxt_bankIfsc.getText().toString().isEmpty()) {
                edtxt_bankIfsc.setClickable(false);
                edtxt_bankIfsc.setFocusable(false);
                edtxt_bankIfsc.setFocusableInTouchMode(false);
                edtxt_bankIfsc.setCursorVisible(false);
            }

            if (!edtxt_address.getText().toString().isEmpty()) {
                edtxt_address.setClickable(false);
                edtxt_address.setFocusable(false);
                edtxt_address.setFocusableInTouchMode(false);
                edtxt_address.setCursorVisible(false);
            }

            if (!edtxt_bankBranch.getText().toString().isEmpty()) {
                edtxt_bankBranch.setClickable(false);
                edtxt_bankBranch.setFocusable(false);
                edtxt_bankBranch.setFocusableInTouchMode(false);
                edtxt_bankBranch.setCursorVisible(false);
            }

            if (!edtxt_bankAcntNumber.getText().toString().isEmpty()) {
                edtxt_bankAcntNumber.setClickable(false);
                edtxt_bankAcntNumber.setFocusable(false);
                edtxt_bankAcntNumber.setFocusableInTouchMode(false);
                edtxt_bankAcntNumber.setCursorVisible(false);
            }

            if (!edtxt_aadhaarNumber.getText().toString().isEmpty()) {
                edtxt_aadhaarNumber.setClickable(false);
                edtxt_aadhaarNumber.setFocusable(false);
                edtxt_aadhaarNumber.setFocusableInTouchMode(false);
                edtxt_aadhaarNumber.setCursorVisible(false);
            }

            if (!txt_bankname.getText().toString().isEmpty() && !txt_bankname.getText().toString().equalsIgnoreCase("-- No Bank Found --")) {
                txt_bankname.setClickable(false);
                txt_bankname.setFocusable(false);
                txt_bankname.setFocusableInTouchMode(false);
                txt_bankname.setCursorVisible(false);
            }
*/
            String bytecode = AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "");
            if (bytecode.length() > 0) {
                iv_Profile_Pic.setImageBitmap(AppUtils.getBitmapFromString(AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "")));
            }

            //  String selectedVal = getResources().getStringArray(R.array.spinner_selectstate)[spinner_selectstate.getSelectedItemPosition()];
            //    spinner_selectstate.setSelection(Integer.parseInt(selectedVal));

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void executeUpdateprofileRequest(final List<NameValuePair> postParameters) {
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
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToUpdateUserProfile, TAG);
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

                                    AppUtils.alertDialogWithFinish(act, "" + jsonObject.getString("Message"));
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


    private void showUploadImageDailog(final Bitmap imageBitmap) {
        try {
            final Dialog dialog = new Dialog(act, R.style.ThemeDialogCustom);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_dialog_img_upload);

            TextView dialog4all_txt = dialog.findViewById(R.id.txt_DialogTitle);
            dialog4all_txt.setText("Are you sure you want to upload this image as Profile Picture ?");

            final ImageView imgView_Upload = dialog.findViewById(R.id.imgView_Upload);
            imgView_Upload.setImageBitmap(imageBitmap);

            TextView txt_submit = dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Upload");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AppUtils.hideKeyboardOnClick(act, v);
                    dialog.dismiss();
                    executePostProfilePictureUploadRequest(imageBitmap);
                    iv_Profile_Pic.setImageBitmap(imageBitmap);
                }
            });

            TextView txt_cancel = dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void executePostProfilePictureUploadRequest(final Bitmap bitmap) {
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
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Type", "PP"));
                            postParameters.add(new BasicNameValuePair("ImageByteCode", AppUtils.getBase64StringFromBitmap(bitmap)));

                            try {
                                postParameters.add(new BasicNameValuePair("IPAddress", AppUtils.getDeviceID(act)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodUploadImages, TAG);
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
                                if (!jsonArrayData.getJSONObject(0).getString("PhotoProof").equals("")) {
                                    iv_Profile_Pic.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));
                                    Cache.getInstance().getLru().put("profileImage", AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));
                                    AppController.getSpUserInfo().edit().putString(SPUtils.USER_profile_pic_byte_code, (jsonArrayData.getJSONObject(0).getString("PhotoProof"))).commit();
                                }
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

    private void showMemRelationDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Member Relation");
            builder.setItems(selectRelationArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_prefix.setText(selectRelationArray[item]);
                }
            });
            builder.create().show();
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

                                    AppController.getSpUserInfo().edit().putString(SPUtils.USER_profile_pic_byte_code, jsonArrayData.getJSONObject(0).getString("PhotoProof")).commit();
                                    iv_Profile_Pic.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));
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

    /*******************OTP System**************************/
    private void executeSendOtpForUpdateProfileRequest() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {


                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        AppUtils.showProgressDialog(act);
                    }


                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("MobileNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, "")));
                            response = AppUtils.callWebServiceWithMultiParam(act,
                                    postParameters, QueryUtils.methodToSendOTPOnUpdateProfile, TAG);

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
                                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                                btn_updateProfilesendotp.setVisibility(View.GONE);
                                ll_update_profile_enter_otp.setVisibility(View.VISIBLE);
                                ll_update_profile_data.setVisibility(View.GONE);
                                // ed_otp.setVisibility(View.VISIBLE);

                                recieve_otp = jsonArray.getJSONObject(0).getString("OTP");
                                AppUtils.alertDialog(act, "OTP Send Succefully on your number");
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

    public void ValidateData() {

        String Otp = ed_otp.getText().toString();
        if (TextUtils.isEmpty(Otp)) {
            ed_otp.setError("OTP is Required");
            ed_otp.requestFocus();
        } else if (!recieve_otp.equalsIgnoreCase(Otp)) {
            ed_otp.setError("Invalid OTP");
            ed_otp.requestFocus();
            //   tv_resend.setVisibility(View.VISIBLE);
        } else {
            if (AppUtils.isNetworkAvailable(this)) {
                startUpdateProfile();
            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    /*Code added by 01-12-2018 06-19 Pm*/

    private void executeBankRequestone() {
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
                            getBankResultone(jsonArrayData);
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

    private void getBankResultone(JSONArray jsonArray) {
        try {
            AppController.bankList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("BID", jsonObject.getString("BID"));
                map.put("Bank", WordUtils.capitalizeFully(jsonObject.getString("Bank")));

                AppController.bankList.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void spinState() {
        stackHelperList = new ArrayList<>();

        try {
            for (int i = 0; i < jsonarray_statelist.length(); i++) {
                JSONObject jsonobject = jsonarray_statelist.getJSONObject(i);
                Statecode = jsonobject.getString("STATECODE");
                Statename = jsonobject.getString("State");

                StackHelperState stackHelper = new StackHelperState();
                stackHelper.setStateName(Statename);
                stackHelper.setCode(Statecode);
                stackHelperList.add(stackHelper);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
/*        ArrayAdapter<StackHelperState> dataAdapter = new ArrayAdapter<StackHelperState>(this, android.R.layout.simple_selectable_list_item, stackHelperList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

        ArrayAdapter<StackHelperState> dataAdapter = new ArrayAdapter<StackHelperState>(this, R.layout.sppiner_item, stackHelperList);
        dataAdapter.setDropDownViewResource(R.layout.sppiner_item);
        spinner_selectstate.setAdapter(dataAdapter);
    }

    private void spincity() {
        stackHelperListcity = new ArrayList<>();

        try {
            for (int i = 0; i < jsonarray_franchiselist.length(); i++) {
                JSONObject jsonobject = jsonarray_franchiselist.getJSONObject(i);
                franchisecode = jsonobject.getString("Rn");
                franchiseName = jsonobject.getString("PartyName");

                StackHelperCity stackHelpercity = new StackHelperCity();
                stackHelpercity.setCode(franchisecode);
                stackHelpercity.setName(franchiseName);
                stackHelperListcity.add(stackHelpercity);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
/*        ArrayAdapter<StackHelperCity> dataAdapter = new ArrayAdapter<StackHelperCity>(this,android.R.layout.simple_selectable_list_item, stackHelperListcity);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

        ArrayAdapter<StackHelperCity> dataAdapter = new ArrayAdapter<StackHelperCity>(this, R.layout.sppiner_item, stackHelperListcity);
        dataAdapter.setDropDownViewResource(R.layout.sppiner_item);

        spinner_selectfranchise.setAdapter(dataAdapter);
    }

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
        Bitmap bitmap1 = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

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
        showUploadImageDailog(bitmap1);
        iv_Profile_Pic.setImageBitmap(bitmap1);

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
        showUploadImageDailog(bm);
        iv_Profile_Pic.setImageBitmap(bm);
        String imagepath = bm.toString();
        Log.e("from gallery data", imagepath);
    }
}