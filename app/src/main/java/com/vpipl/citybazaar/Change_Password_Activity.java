package com.vpipl.citybazaar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Change_Password_Activity extends AppCompatActivity {

    Activity act = Change_Password_Activity.this;
    private static final String TAG = "Change_Password_Activity";
    private TextInputEditText edtxt_new_password;
    private TextInputEditText edtxt_confirm_password;
    private TextInputEditText edtxt_old_password;
    private TextView txt_password_type;
    private Button button_change_password;
    private String mobile;
    private String old_pass;
    private String new_pass;
    private String confirm_pass;
    private String password_type = "Login";

   // private String[] passtypearray = {"Login", "Account"};
    private String[] passtypearray = {"Login", "Transaction"};

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
        setContentView(R.layout.activity_change__password);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        AppUtils.changeStatusBarColor(act);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        mobile = AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, "");

        edtxt_new_password = findViewById(R.id.edtxt_new_password);
        edtxt_confirm_password = findViewById(R.id.edtxt_confirm_password);
        edtxt_old_password = findViewById(R.id.edtxt_old_password);

        txt_password_type = findViewById(R.id.txt_password_type);
        txt_password_type.setText("Login");

        txt_password_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPasswordTypedialog();
            }
        });

        button_change_password = findViewById(R.id.btn_submit);
        button_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(act, v);
                ValidateData();
            }
        });

        executeLoginRequest();
    }

    private void showPasswordTypedialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Password Type");
            builder.setItems(passtypearray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_password_type.setText(passtypearray[item]);
                    password_type = passtypearray[item];
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void ValidateData() {
        old_pass = edtxt_old_password.getText().toString().trim();
        new_pass = edtxt_new_password.getText().toString().trim();
        confirm_pass = edtxt_confirm_password.getText().toString().trim();
        password_type = txt_password_type.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(old_pass)) {
            AppUtils.alertDialog(act, getResources().getString(R.string.error_required_old_password));
            focusView = edtxt_old_password;
            cancel = true;
        } else if (TextUtils.isEmpty(new_pass)) {
            AppUtils.alertDialog(act, getResources().getString(R.string.error_required_new_password));
            focusView = edtxt_new_password;
            cancel = true;
        } else if (TextUtils.isEmpty(confirm_pass)) {
            AppUtils.alertDialog(act, getResources().getString(R.string.error_required_confirm_password));
            focusView = edtxt_confirm_password;
            cancel = true;
        } else if (!new_pass.equalsIgnoreCase(confirm_pass)) {
            AppUtils.alertDialog(act, getResources().getString(R.string.password_mismatch));
            focusView = edtxt_confirm_password;
            cancel = true;
        } else if (TextUtils.isEmpty(password_type)) {
            AppUtils.alertDialog(act, getResources().getString(R.string.error_required_pass_type));
            focusView = edtxt_old_password;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(act)) {

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        createChangePasswordRequest();
                    }
                };
                new Handler().postDelayed(runnable, 500);
            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void createChangePasswordRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();
            if(password_type.equalsIgnoreCase("Transaction")){
                password_type = "Account";
            }
            postParameters.add(new BasicNameValuePair("ChangeType", password_type));
            postParameters.add(new BasicNameValuePair("Formno", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
            postParameters.add(new BasicNameValuePair("OldPassword", old_pass));
            postParameters.add(new BasicNameValuePair("NewPassword", new_pass));
            postParameters.add(new BasicNameValuePair("DeviceID", AppUtils.getDeviceID(Change_Password_Activity.this)));

            executeChangePasswordRequest(postParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeChangePasswordRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(act);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToChangePassword, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                final Dialog dialog = AppUtils.createDialog(act, true);
                                TextView dialog4all_txt = dialog.findViewById(R.id.txt_DialogTitle);
                                dialog4all_txt.setText(jsonObject.getString("Message"));

                                dialog.findViewById(R.id.txt_submit).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();

                                        AppController.getSpUserInfo().edit().clear().commit();
                                        AppController.getSpIsLogin().edit().clear().commit();

                                        Intent intent = new Intent(act, Login_New_Activity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("SendToHome", true);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                dialog.show();
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
        
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return true;
//    }
}