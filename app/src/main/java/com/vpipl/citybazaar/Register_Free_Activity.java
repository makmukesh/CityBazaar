package com.vpipl.citybazaar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.model.StackHelperBank;
import com.vpipl.citybazaar.model.StackHelperState;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Register_Free_Activity extends AppCompatActivity {

    Activity act = Register_Free_Activity.this;
    private static final String TAG = "Register_Free_Activity";

    String sponsor_id, firstname, mobile_number, email,
            pan_number, password, trans_password, sponsor_form_no, sponsor_name;

    boolean accepet_conditions = false;
    TextView txt_terms_conditions, txt_sponsor_name;
    CheckBox cb_accept;
    LinearLayout ll_button_submit;
    Button btn_cancel;
    Button btn_submit;
    ScrollView scrollView;
    RadioGroup rg_side;
    Calendar myCalendar;
    SimpleDateFormat sdf;

    Spinner spinner_selectstate, spinner_selectbank;
    String Statecode = "0", Statename = "", jsonStr_statelist = "", Bankcode = "0", Bankname = "", jsonStr_banklist = "";
    List<StackHelperState> stackHelperList = new ArrayList<>();
    List<StackHelperBank> stackHelperBankList = new ArrayList<>();
    JSONArray jsonarray_banklist;
    private String[] selectRelationArray;

    DatePickerDialog datePickerDialog;

    private void showdatePicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(act, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        if (onWhichDateClick.equals("et_dob"))
            calendar.add(Calendar.YEAR, -18);

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
                if (onWhichDateClick.equals("et_dob")) {
                   // txt_select_date.setText(sdf.format(myCalendar.getTime()));
                }
            } else {
                //txt_select_date.requestFocus();
                AppUtils.alertDialog(act, getResources().getString(R.string.error_invalid_dob));
            }
        }
    };

    private TextInputEditText edtxt_sponsor_id, edtxt_firstname, edtxt_mobile, edtxt_email, edtxt_pan_number, edtxt_password, edtxt_trans_password;


    String onWhichDateClick = "";

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

        img_login_logout.setVisibility(View.GONE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.activity_register_free);
            AppUtils.changeStatusBarColor(act);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            edtxt_sponsor_id = (TextInputEditText) findViewById(R.id.edtxt_sponsor_id);
            edtxt_firstname = (TextInputEditText) findViewById(R.id.edtxt_firstname);

            edtxt_mobile = (TextInputEditText) findViewById(R.id.edtxt_mobile);
            edtxt_email = (TextInputEditText) findViewById(R.id.edtxt_email);
            edtxt_pan_number = (TextInputEditText) findViewById(R.id.edtxt_PANNumber);
            edtxt_password = (TextInputEditText) findViewById(R.id.edtxt_password);
            edtxt_trans_password = (TextInputEditText) findViewById(R.id.edtxt_trans_password);
            txt_sponsor_name = (TextView) findViewById(R.id.txt_sponsor_name);
            scrollView = (ScrollView) findViewById(R.id.scrollView);
            rg_side = (RadioGroup) findViewById(R.id.rg_side);
            txt_terms_conditions = (TextView) findViewById(R.id.txt_terms_conditions);

            edtxt_pan_number.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(11)});
            txt_terms_conditions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.alertDialogterms(act, getResources().getString(R.string.terms_conditions));
                }
            });

            cb_accept = (CheckBox) findViewById(R.id.cb_accept);
            ll_button_submit = (LinearLayout) findViewById(R.id.ll_button_submit);

            ll_button_submit.setVisibility(View.GONE);

            btn_submit = findViewById(R.id.btn_submit);
            btn_cancel = (Button) findViewById(R.id.btn_cancel);
            jsonarray_banklist = new JSONArray();

            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(act, view);
                    ValidateData();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AppUtils.hideKeyboardOnClick(act, v);
                    finish();
                }
            });

            cb_accept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    accepet_conditions = b;

                    if (cb_accept.isChecked()) {
                        ll_button_submit.setVisibility(View.VISIBLE);
                    /*scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.smoothScrollTo(0, scrollView.getHeight());
                        }
                    });*/
                    } else {
                        ll_button_submit.setVisibility(View.GONE);
                    }
                }
            });

            edtxt_sponsor_id.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String id = edtxt_sponsor_id.getText().toString().trim();
                    if (id.length() == 10) {
                        executetoCheckSponsorName(id);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void ValidateData() {

        sponsor_id = edtxt_sponsor_id.getText().toString().trim();
        firstname = edtxt_firstname.getText().toString().trim();
        mobile_number = edtxt_mobile.getText().toString().trim();
        email = edtxt_email.getText().toString().trim();
        pan_number = edtxt_pan_number.getText().toString().trim();
        password = edtxt_password.getText().toString().trim();
        trans_password = edtxt_trans_password.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(sponsor_id)) {
            AppUtils.alertDialog(act, getResources().getString(R.string.error_required_sponsorID));
            focusView = edtxt_sponsor_id;
            cancel = true;
        }  else if (TextUtils.isEmpty(firstname)) {
            AppUtils.alertDialog(act, "Enter Your Name");
            focusView = edtxt_firstname;
            cancel = true;
        } else if (TextUtils.isEmpty(mobile_number)) {
            AppUtils.alertDialog(act, getResources().getString(R.string.error_required_mobile_number));
            focusView = edtxt_mobile;
            cancel = true;
        } else if (!AppUtils.isValid(mobile_number)) {
            AppUtils.alertDialog(act, getResources().getString(R.string.error_invalid_mobile_number));
            focusView = edtxt_mobile;
            cancel = true;
        } else if (!TextUtils.isEmpty(email) && AppUtils.isValidMail(email.trim())) {
            edtxt_email.setError(getResources().getString(R.string.error_invalid_email));
            edtxt_email.requestFocus();
            focusView = edtxt_email;
            cancel = true;
        } else if (!TextUtils.isEmpty(pan_number) && !pan_number.matches(AppUtils.mPANPattern)) {
            AppUtils.alertDialog(act, getResources().getString(R.string.error_invalid_PANno));
            focusView = edtxt_pan_number;
            cancel = true;
        }  else if (TextUtils.isEmpty(password)) {
            AppUtils.alertDialog(act, getResources().getString(R.string.error_required_password));
            focusView = edtxt_password;
            cancel = true;
        } else if (TextUtils.isEmpty(trans_password)) {
            AppUtils.alertDialog(act, "Enter Tranaction Password");
            focusView = edtxt_trans_password;
            cancel = true;
        } else if (TextUtils.isEmpty(sponsor_form_no)) {
            AppUtils.alertDialog(act, getResources().getString(R.string.error_invalid_sponsorID));
            focusView = edtxt_sponsor_id;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(act)) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        createRegistrationRequest();
                    }
                };
                new Handler().postDelayed(runnable, 500);
            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void createRegistrationRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();

            int selectedIdTwo = rg_side.getCheckedRadioButtonId();
            RadioButton radioButtonTwo = (RadioButton) findViewById(selectedIdTwo);
            String view_detail_side = radioButtonTwo.getText().toString().trim();

            String side = "1";

            if (view_detail_side.equalsIgnoreCase("Left"))
                side = "1";
            else if (view_detail_side.equalsIgnoreCase("Right"))
                side = "2";
/*SponsorFormNo:Position:Name:MobileNo:EmailID:PanNo:LoginPassword:TransactioNPassword:DeviceID:*/
            postParameters.add(new BasicNameValuePair("SponsorFormNo", "" + sponsor_form_no));
            postParameters.add(new BasicNameValuePair("Position", "" + side));
            postParameters.add(new BasicNameValuePair("Name", "" + firstname.trim()));
            postParameters.add(new BasicNameValuePair("MobileNo", "" + mobile_number));
            postParameters.add(new BasicNameValuePair("EmailID", "" + email));
            postParameters.add(new BasicNameValuePair("PanNo", "" + pan_number));
            postParameters.add(new BasicNameValuePair("LoginPassword", "" + password));
            postParameters.add(new BasicNameValuePair("TransactioNPassword", "" + trans_password));
            postParameters.add(new BasicNameValuePair("DeviceID", "" + AppUtils.getDeviceID(Register_Free_Activity.this)));

            executeMemberRegistrationRequest(postParameters);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeMemberRegistrationRequest(final List<NameValuePair> postParameters) {
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
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToNewJoiningFree, TAG);
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
                            JSONArray JArray;
                            JSONObject object;

                            try {
                                JArray = new JSONArray(resultData);
                                object = JArray.getJSONObject(0);
                            } catch (Exception e) {
                                object = new JSONObject(resultData);
                            }

                            if (object.length() > 0) {
                                if (object.getString("Status").equalsIgnoreCase("True")) {
                                    MovetoNext(new Intent(act, WelcomeLetter_Activity.class).putExtra("Form Number", object.getString("formno")));
                                } else {
                                    if (object.getString("Message").contains("System.Data.SqlClient.SqlException"))
                                        AppUtils.alertDialog(act, object.getString("Message"));
                                    else
                                        AppUtils.alertDialog(act, object.getString("Message"));
                                }
                            } else {
                                AppUtils.showExceptionDialog(act);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(act);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
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

    public void executetoCheckSponsorName(final String sponsorid) {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("SponsorID", "" + sponsorid));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodCheckSponsor, TAG);
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

                            JSONArray jsonArrayData = new JSONArray(resultData);
                            JSONObject Jobject = jsonArrayData.getJSONObject(0);

                            setSponsorName(Jobject);


                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(act);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    public void setSponsorName(JSONObject jobject) {
        try {
            if (jobject.getString("Status").equalsIgnoreCase("True")) {
                sponsor_form_no = jobject.getString("FormNo");
                sponsor_name = jobject.getString("MemName");
                txt_sponsor_name.setTextColor(getResources().getColor(R.color.colorPrimary));
                txt_sponsor_name.setText(sponsor_name);
                txt_sponsor_name.setVisibility(View.VISIBLE);

            } else {
                txt_sponsor_name.setText(jobject.getString("Message"));
                txt_sponsor_name.setTextColor(Color.RED);
                sponsor_form_no = "";
                sponsor_name = "";
                txt_sponsor_name.setVisibility(View.VISIBLE);
                edtxt_sponsor_id.requestFocus();
            }
        } catch (Exception ignored) {

        }
    }

    private void MovetoNext(Intent intent) {
        try {
            startActivity(intent);
            ////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }
}