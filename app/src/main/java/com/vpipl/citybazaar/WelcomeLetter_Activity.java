package com.vpipl.citybazaar;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mukesh on 27/12/2019.
 */
public class WelcomeLetter_Activity extends AppCompatActivity {

    Activity act = WelcomeLetter_Activity.this;
    private String TAG = "WelcomeLetter_Activity";
    private TextView txt_id;
    private TextView txt_name;
    private TextView txt_address;
    private TextView txt_city;
    private TextView txt_pincode;
    private TextView txt_joiningDate;
    private TextView txt_sponsorId;
    private TextView txt_kit_amount, txt_package_Name, txt_placeunderId;
    String Form_number = "" ;
    LinearLayout layout_info;

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);

        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));

        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        setContentView(R.layout.activity_welcome_letter);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            AppUtils.changeStatusBarColor(act);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                Form_number = getIntent().getStringExtra("Form Number");
            } else
                Form_number = AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "");

            txt_id = findViewById(R.id.txt_id);
            txt_name = findViewById(R.id.txt_name);
            txt_address = findViewById(R.id.txt_address);
            txt_city = findViewById(R.id.txt_city);
            txt_pincode = findViewById(R.id.txt_pincode);
            txt_joiningDate = findViewById(R.id.txt_joiningDate);
            txt_sponsorId = findViewById(R.id.txt_sponsorId);

            txt_placeunderId = findViewById(R.id.txt_placeunderId);
            txt_package_Name = findViewById(R.id.txt_package_Name);
            txt_kit_amount = findViewById(R.id.txt_kit_amount);

            layout_info = findViewById(R.id.layout_info);
            layout_info.setVisibility(View.GONE);

            if (AppUtils.isNetworkAvailable(act)) {
                executeToGetWelcomeUserInfo();
            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void executeToGetWelcomeUserInfo() {
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
                            postParameters.add(new BasicNameValuePair("FormNo", Form_number));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodWelcomeLetter, TAG);
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
                                    setDetails(jsonArrayData);
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

    private void setDetails(JSONArray jsonArray) {
        try {
            txt_id.setText("" + jsonArray.getJSONObject(0).getString("IDNo"));
            txt_name.setText("" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("MemFirstName")));
            txt_city.setText("" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("City")));
            txt_address.setText("" + WordUtils.capitalizeFully(jsonArray.getJSONObject(0).getString("Address1")));
            txt_pincode.setText("" + jsonArray.getJSONObject(0).getString("Pincode"));
            txt_joiningDate.setText("" + AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("DOJ")));
            txt_sponsorId.setText("" + jsonArray.getJSONObject(0).getString("RefIdNo"));

            txt_placeunderId.setText("" + jsonArray.getJSONObject(0).getString("UpLnIdNo"));
            txt_package_Name.setText("" + jsonArray.getJSONObject(0).getString("Category"));
            txt_kit_amount.setText("" + jsonArray.getJSONObject(0).getString("KitAmount"));

            layout_info.setVisibility(View.VISIBLE);
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

}
