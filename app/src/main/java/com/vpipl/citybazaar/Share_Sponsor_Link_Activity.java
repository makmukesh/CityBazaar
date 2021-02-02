package com.vpipl.citybazaar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.vpipl.citybazaar.Epin.Pin_Request_Activity;
import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Mukesh on 28/01/2021.
 */
public class Share_Sponsor_Link_Activity extends AppCompatActivity {

    Activity act = Share_Sponsor_Link_Activity.this;
    private String TAG = "Share_Sponsor_Link_Activity";

    private TextInputEditText txt_choose_pay_mode;
    private TextInputEditText edtxt_mobileNumber;
    private TextInputEditText edtxt_email;
    String paymodeArray[] = {"Both", "SMS", "Email"};

    private Button btn_Submit, btn_reset;

    private String mobile_number;
    private String email;
    private String share_type = "";

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
        setContentView(R.layout.activity_share_sponsor_link);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            AppUtils.changeStatusBarColor(act);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            edtxt_mobileNumber = findViewById(R.id.edtxt_mobileNumber);
            edtxt_email = findViewById(R.id.edtxt_email);
            txt_choose_pay_mode = (TextInputEditText) findViewById(R.id.txt_choose_pay_mode);

            // edtxt_email.setText(AppController.getSpUserInfo().getString(SPUtils.USER_EMAIL, ""));
            // edtxt_mobileNumber.setText(AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, ""));

            btn_Submit = findViewById(R.id.btn_Submit);
            btn_reset = findViewById(R.id.btn_reset);
            btn_Submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(act, view);
                    if (AppUtils.isNetworkAvailable(act)) {
                        if (share_type.equalsIgnoreCase("Both")) {
                            validateQueryBothRequest();
                        } else if (share_type.equalsIgnoreCase("SMS")) {
                            validateQuerySMSRequest();
                        } else if (share_type.equalsIgnoreCase("Email")) {
                            validateQueryEmailRequest();
                        } else {
                            AppUtils.alertDialog(act, "Select Share Type First");
                        }
                    } else {
                        AppUtils.alertDialogWithFinish(act, getString(R.string.txt_networkAlert));
                    }
                }
            });
            btn_reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(act, view);
                    ResetData();
                }
            });

            txt_choose_pay_mode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        showpaymodeDialog();
                    }
                }
            });

            txt_choose_pay_mode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showpaymodeDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void showpaymodeDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose Share Type");
            builder.setItems(paymodeArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_choose_pay_mode.setText(paymodeArray[item]);
                    share_type = paymodeArray[item];
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void ResetData() {
        try {
            edtxt_mobileNumber.setText("");
            edtxt_email.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void validateQueryBothRequest() {
        try {
            mobile_number = edtxt_mobileNumber.getText().toString().trim();
            email = edtxt_email.getText().toString().trim();

            if (TextUtils.isEmpty(mobile_number)) {
                AppUtils.alertDialog(act, "Please Enter Mobile No");
                edtxt_mobileNumber.requestFocus();
            } else if (!AppUtils.isValid(mobile_number)) {
                AppUtils.alertDialog(act, getResources().getString(R.string.error_invalid_mobile_number));
                edtxt_mobileNumber.requestFocus();
            } else if (TextUtils.isEmpty(email)) {
                AppUtils.alertDialog(act, "Please Enter Email-ID");
                edtxt_email.requestFocus();
            } else if (!TextUtils.isEmpty(email) && AppUtils.isValidMail(email.trim())) {
                AppUtils.alertDialog(act, getResources().getString(R.string.error_invalid_email));
                edtxt_email.requestFocus();
            } else if (!AppUtils.isNetworkAvailable(act)) {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            } else {
                startSubmitShareSponsorLink();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void validateQuerySMSRequest() {
        try {
            mobile_number = edtxt_mobileNumber.getText().toString().trim();
            email = edtxt_email.getText().toString().trim();

            if (TextUtils.isEmpty(mobile_number)) {
                AppUtils.alertDialog(act, "Please Enter Mobile No");
                edtxt_mobileNumber.requestFocus();
            } else if (!AppUtils.isValid(mobile_number)) {
                AppUtils.alertDialog(act, getResources().getString(R.string.error_invalid_mobile_number));
                edtxt_mobileNumber.requestFocus();
            }/*else if (TextUtils.isEmpty(email)) {
                AppUtils.alertDialog(act, "Please Enter Email-ID");
                edtxt_email.requestFocus();
            }*/ else if (!AppUtils.isNetworkAvailable(act)) {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            } else {
                startSubmitShareSponsorLink();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void validateQueryEmailRequest() {
        try {
            mobile_number = edtxt_mobileNumber.getText().toString().trim();
            email = edtxt_email.getText().toString().trim();

            /*if (TextUtils.isEmpty(mobile_number)) {
                AppUtils.alertDialog(act, "Please Enter Mobile No");
                edtxt_mobileNumber.requestFocus();
            } else*/
            if (TextUtils.isEmpty(email)) {
                AppUtils.alertDialog(act, "Please Enter Email-ID");
                edtxt_email.requestFocus();
            } else if (!TextUtils.isEmpty(email) && AppUtils.isValidMail(email.trim())) {
                AppUtils.alertDialog(act, getResources().getString(R.string.error_invalid_email));
                edtxt_email.requestFocus();
            } else if (!AppUtils.isNetworkAvailable(act)) {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            } else {
                startSubmitShareSponsorLink();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void startSubmitShareSponsorLink() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                char first = share_type.charAt(0);

                List<NameValuePair> postParameters = new ArrayList<>();
                postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("Type", "" + first));
                postParameters.add(new BasicNameValuePair("MobileNo", "" + mobile_number.trim()));
                postParameters.add(new BasicNameValuePair("Email", "" + email.trim()));
                executeSubmitShareSponsorLinkRequest(postParameters);
            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void executeSubmitShareSponsorLinkRequest(final List<NameValuePair> postParameters) {
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
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodShareSponsorLink, TAG);
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
                                AppUtils.alertDialogWithFinish(act, jsonObject.getString("Message"));
                               /* JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                if (jsonArrayData.length() != 0) {
                                    AppUtils.alertDialogWithFinish(act, jsonObject.getString("Message"));
                                } else {
                                    AppUtils.alertDialogWithFinish(act, jsonObject.getString("Message"));
                                }*/
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
}