package com.vpipl.citybazaar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.QueryUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class Forget_Password_Activity extends AppCompatActivity {

    Activity act = Forget_Password_Activity.this;
    private static final String TAG = "Forget_Password_Activity";
    private TextInputEditText edtxt_userid;
    private String userid;
    private CircularProgressButton button_submit, btn_login;

    private String LoginType = "User";

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);

        img_login_logout.setVisibility(View.GONE);
        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));
        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(act, Login_New_Activity.class);
                intent.putExtra("SendToHome", true);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget__password);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        AppUtils.changeStatusBarColor(act);
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();*/

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        edtxt_userid = findViewById(R.id.edtxt_userid);
        btn_login = findViewById(R.id.btn_login);
        button_submit = findViewById(R.id.button_submit);

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_submit.startAnimation();
                AppUtils.hideKeyboardOnClick(act, v);
                ValidateData();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_login.startAnimation();

                Intent intent = new Intent(act, Login_New_Activity.class);
                intent.putExtra("SendToHome", true);
                startActivity(intent);
                finish();
            }
        });
    }

    private void ValidateData() {
        userid = edtxt_userid.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(userid)) {
            AppUtils.alertDialog(act, getResources().getString(R.string.error_required_user_id));
            focusView = edtxt_userid;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            button_submit.revertAnimation();
        } else {
            if (AppUtils.isNetworkAvailable(act)) {

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        executeForgetRequest();
                    }
                };
                new Handler().postDelayed(runnable, 500);
            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
                button_submit.revertAnimation();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(act, Login_New_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("SendToHome", true);
        startActivity(intent);
        finish();
    }

    private void executeForgetRequest() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(act);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;

                        List<NameValuePair> postParameters = new ArrayList<>();
                        try {
                            postParameters.add(new BasicNameValuePair("DeviceID", AppUtils.getDeviceID(act)));
                            postParameters.add(new BasicNameValuePair("IDNo", userid));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToForgetPasswordMember, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            button_submit.revertAnimation();
                            AppUtils.dismissProgressDialog();
                            JSONObject jobject = new JSONObject(resultData);
                            if (jobject.length() > 0) {
                                if (jobject.getString("Status").equalsIgnoreCase("True")) {
                                    String message = jobject.getString("Message");
                                    ShowDialog(message);
                                } else {
                                    AppUtils.alertDialog(act, jobject.getString("Message"));
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
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void ShowDialog(String message) {
        final Dialog dialog = AppUtils.createDialog(act, true);
        TextView dialog4all_txt = dialog.findViewById(R.id.txt_DialogTitle);
        dialog4all_txt.setText(message);

        TextView textView = dialog.findViewById(R.id.txt_submit);
        textView.setText("Login");
        textView.setOnClickListener(new View.OnClickListener() {
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
    }
}
