package com.vpipl.citybazaar.Epin;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;
import com.vpipl.citybazaar.AppController;
import com.vpipl.citybazaar.Login_New_Activity;
import com.vpipl.citybazaar.R;
import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Topup_member_Activity extends AppCompatActivity {
    private static final String TAG = "Topup_member_Activity";

    Button btn_confirm_topup, btn_cancel;

    private TextInputEditText edtxt_id_number, edtxt_pinNumber, edtxt_scratchNumber, edtxt_memberName;

    private String sponsor_name, pin_number, scratch_no;
    private String sponsor_form_no, kitid;
    String str_sts = "N";

    ImageView img_login_logout, img_nav_back;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);

        final Drawable upArrow;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            upArrow = getDrawable(R.drawable.icon_nav_bar_close);
        } else
            upArrow = getResources().getDrawable(R.drawable.icon_nav_bar_close);
        assert upArrow != null;

        //     txt_wallet_balance.setText("\u20B9 " + str_wallet_balance);

        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));

        img_nav_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        img_login_logout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                    startActivity(new Intent(Topup_member_Activity.this, Login_New_Activity.class));
                } else {
                    AppUtils.showDialogSignOut(Topup_member_Activity.this);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_member);

      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppUtils.setActionbarTitle(getSupportActionBar(), Topup_member_Activity.this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);*/

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        SetupToolbar();

        edtxt_id_number = (TextInputEditText) findViewById(R.id.edtxt_id_number);
        edtxt_pinNumber = (TextInputEditText) findViewById(R.id.edtxt_pinNumber);
        edtxt_scratchNumber = (TextInputEditText) findViewById(R.id.edtxt_scratchNumber);
        edtxt_memberName = (TextInputEditText) findViewById(R.id.edtxt_memberName);

        btn_confirm_topup = (Button) findViewById(R.id.btn_confirm_topup);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        btn_confirm_topup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideKeyboardOnClick(Topup_member_Activity.this, view);
                ValidateData();
            }
        });

        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Topup_member_Activity.this, v);
                finish();
            }
        });

        pin_number = getIntent().getExtras().getString("PinNumber");
        scratch_no = getIntent().getExtras().getString("ScratchNo");

        edtxt_pinNumber.setText(pin_number);
        edtxt_scratchNumber.setText(scratch_no);

        edtxt_id_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String id = edtxt_id_number.getText().toString().trim();
                if (id.length() == 10) {
                    executetoCheckSponsorName(edtxt_id_number.getText().toString());
                }
            }
        });
    }

    public void executetoCheckSponsorName(final String sponsorid) {
        try {
            if (AppUtils.isNetworkAvailable(Topup_member_Activity.this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", "" + sponsorid));
                            response = AppUtils.callWebServiceWithMultiParam(Topup_member_Activity.this, postParameters, QueryUtils.methodCheckTopupIDNo, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Topup_member_Activity.this);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            JSONObject Jobject = new JSONObject(resultData);
                            setSponsorName(Jobject);

                        } catch (Exception e) {
                            str_sts = "N";
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Topup_member_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(Topup_member_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Topup_member_Activity.this);
        }
    }

    public void setSponsorName(JSONObject jobject) {
        try {
            if (jobject.getString("Status").equalsIgnoreCase("True")) {
                JSONArray jsonArrayData = jobject.getJSONArray("Data");

                sponsor_form_no = jsonArrayData.getJSONObject(0).getString("FormNo");
                sponsor_name = jsonArrayData.getJSONObject(0).getString("MemName");
                kitid = jsonArrayData.getJSONObject(0).getString("KitId");

                edtxt_memberName.setText(sponsor_name);
                str_sts = "Y";
            } else {
                edtxt_id_number.setError(jobject.getString("Message"));
                str_sts = "N";
                edtxt_memberName.setText("");
            }
        } catch (Exception ignored) {
            str_sts = "N";
        }
    }

    private void ValidateData() {
        edtxt_id_number.setError(null);

        String userid = edtxt_id_number.getText().toString().trim();

        if (TextUtils.isEmpty(userid)) {
            AppUtils.alertDialog(Topup_member_Activity.this, getResources().getString(R.string.error_required_user_id));
            edtxt_id_number.requestFocus();
        } else if (str_sts.equalsIgnoreCase("N")) {
            AppUtils.alertDialog(Topup_member_Activity.this, "Try Different Id !");
            edtxt_id_number.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(Topup_member_Activity.this)) {
                executeLoginRequest(userid);
            } else {
                AppUtils.alertDialog(Topup_member_Activity.this, getResources().getString(R.string.txt_networkAlert));

            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void executeLoginRequest(final String userId) {
        try {

            if (AppUtils.isNetworkAvailable(Topup_member_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Topup_member_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", userId));
                            postParameters.add(new BasicNameValuePair("PinNo", "" + pin_number));
                            postParameters.add(new BasicNameValuePair("ScratchNo", "" + scratch_no));
                            postParameters.add(new BasicNameValuePair("PrevKitId", "" + kitid));
                            response = AppUtils.callWebServiceWithMultiParam(Topup_member_Activity.this, postParameters, QueryUtils.methodTopupMember, TAG);

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
                                AppUtils.alertDialogWithFinishDashboard(Topup_member_Activity.this, jsonObject.getString("Message"));

                            } else {
                                AppUtils.alertDialog(Topup_member_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Topup_member_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Topup_member_Activity.this);
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
            AppUtils.showExceptionDialog(Topup_member_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }
}