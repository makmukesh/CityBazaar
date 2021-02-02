package com.vpipl.citybazaar.Epin;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.vpipl.citybazaar.AppController;
import com.vpipl.citybazaar.DashBoard_Activity;
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

public class Transaction_login_Activity extends AppCompatActivity {
    private static final String TAG = "Transaction_login_Activity";

    Button button_login;
    String send_to = "";
    private TextInputEditText edtxt_trans_password;


    ImageView img_login_logout ,img_nav_back ;
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
                    startActivity(new Intent(Transaction_login_Activity.this, Login_New_Activity.class));
                } else {
                    AppUtils.showDialogSignOut(Transaction_login_Activity.this);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_login);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppUtils.setActionbarTitle(getSupportActionBar(), Transaction_login_Activity.this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);*/

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        SetupToolbar();

        send_to = getIntent().getStringExtra("SEND_TO");

        edtxt_trans_password = (TextInputEditText) findViewById(R.id.edtxt_trans_password);

        button_login = (Button) findViewById(R.id.button_login);

        edtxt_trans_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == 1234 || id == EditorInfo.IME_NULL) {
                    ValidateData();
                    return true;
                }
                return false;
            }
        });

        button_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideKeyboardOnClick(Transaction_login_Activity.this, view);
                ValidateData();
            }
        });
    }


    private void ValidateData() {
        edtxt_trans_password.setError(null);

        String password = edtxt_trans_password.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            AppUtils.alertDialog(Transaction_login_Activity.this, getResources().getString(R.string.error_required_password));
            edtxt_trans_password.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(Transaction_login_Activity.this)) {

                executeTransLoginRequest(password);

            } else {
                AppUtils.alertDialog(Transaction_login_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void executeTransLoginRequest(final String passwd) {
        try {

            if (AppUtils.isNetworkAvailable(Transaction_login_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Transaction_login_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Password", passwd));
                            response = AppUtils.callWebServiceWithMultiParam(Transaction_login_Activity.this, postParameters, QueryUtils.methodTransactionLogin, TAG);
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
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                                if (jsonArrayData.length() != 0) {
                                    MovetoNext();
                                } else {
                                    AppUtils.alertDialog(Transaction_login_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Transaction_login_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Transaction_login_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Transaction_login_Activity.this);
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
            AppUtils.showExceptionDialog(Transaction_login_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void MovetoNext() {
        try {
            Intent intent;

            if (send_to.equalsIgnoreCase("Generated/Issue Pin Details")) {
                intent = new Intent(Transaction_login_Activity.this, Pin_Generated_issued_details_Activity.class);
            } else if (send_to.equalsIgnoreCase("Topup/E-Pin Detail")) {
                intent = new Intent(Transaction_login_Activity.this, Pin_details_Activity.class);
            } else if (send_to.equalsIgnoreCase("E-Pin Transfer")) {
                intent = new Intent(Transaction_login_Activity.this, Pin_transfer_Activity.class);
            } else if (send_to.equalsIgnoreCase("E-Pin Received Detail")) {
                intent = new Intent(Transaction_login_Activity.this, Pin_Received_Report_Activity.class);
            } else if (send_to.equalsIgnoreCase("E-Pin Transfer Detail")) {
                intent = new Intent(Transaction_login_Activity.this, Pin_Transfer_Report_Activity.class);
            } else if (send_to.equalsIgnoreCase("E-Pin Request")) {
                intent = new Intent(Transaction_login_Activity.this, Pin_Request_Activity.class);
            } else if (send_to.equalsIgnoreCase("E-Pin Request Detail")) {
                intent = new Intent(Transaction_login_Activity.this, Pin_Request_Report_Activity.class);
            } else
                intent = new Intent(Transaction_login_Activity.this, DashBoard_Activity.class);

            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}