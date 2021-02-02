package com.vpipl.citybazaar;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.textfield.TextInputEditText;
import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.omidh.ripplevalidatoredittext.RVEValidatorFactory;
import me.omidh.ripplevalidatoredittext.RVEValidatorType;
import me.omidh.ripplevalidatoredittext.RippleValidatorEditText;

public class Test_Login_Activity extends AppCompatActivity {

    Activity act = Test_Login_Activity.this;
    private static final String TAG = "Test_Login_Activity";

    boolean accepet_conditions = false;
    Button button_login;
    TextView txt_forgot_password;
    CheckBox cb_login_rememberMe;
    private TextView txt_terms_conditions ,txt_show_password;
    private CheckBox cb_accept;
    LinearLayout ll_terms_and_condition;
    RelativeLayout root_layout;
    private TextInputEditText edtxt_userid_member, edtxt_password_member;

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {
        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);
        img_login_logout.setVisibility(View.GONE);

        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));
        img_nav_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    LinearLayout ll_login, ll_terms_condition_show;
    String userid;
    String password;
    TextView txt_sign_up ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.activity_test);
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            //   requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            root_layout = findViewById(R.id.root_layout);

            edtxt_userid_member = findViewById(R.id.edtxt_userid_member);
            edtxt_password_member = findViewById(R.id.edtxt_password_member);

            button_login = findViewById(R.id.button_login);
            txt_sign_up = findViewById(R.id.txt_sign_up);

            cb_login_rememberMe = findViewById(R.id.cb_login_rememberMe);

            txt_forgot_password = findViewById(R.id.txt_forgot_password);

            ll_terms_and_condition = findViewById(R.id.ll_terms_and_condition);
            cb_accept = findViewById(R.id.cb_accept);
            txt_terms_conditions = findViewById(R.id.txt_terms_conditions);
            txt_show_password = findViewById(R.id.txt_show_password);

            final RippleValidatorEditText userName= (RippleValidatorEditText) findViewById(R.id.editText_name);

            userName.addValidator(
                    RVEValidatorFactory.getValidator(RVEValidatorType.EMPTY,"User Id can't be empty",null),
                    RVEValidatorFactory.getValidator(RVEValidatorType.BEGIN,"Name should Begin with UA","UA"),
                    RVEValidatorFactory.getValidator(RVEValidatorType.MIN_LENGTH,"Minimum length is 10 words",10)/*,
                    RVEValidatorFactory.getValidator(RVEValidatorType.EQUAL,"Name should Be Angelina","Angelina")*/
            );

            cb_accept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    accepet_conditions = b;

                    if (cb_accept.isChecked()) {
                        button_login.setVisibility(View.VISIBLE);
                    } else {
                        button_login.setVisibility(View.GONE);
                    }
                }
            });
            txt_show_password.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (txt_show_password.getText().toString().equals("Show Password")) {
                        edtxt_password_member.setTransformationMethod(null);
                        txt_show_password.setText("Hide Password");
                    } else {
                        edtxt_password_member.setTransformationMethod(new PasswordTransformationMethod());
                        txt_show_password.setText("Show Password");
                    }
                }
            });
            button_login.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(act, v);
                   // ValidateData();

                    userName.addValidator(
                            RVEValidatorFactory.getValidator(RVEValidatorType.EMPTY,"User Id can't be empty",null),
                            RVEValidatorFactory.getValidator(RVEValidatorType.BEGIN,"Name should Begin with UA","UA"),
                            RVEValidatorFactory.getValidator(RVEValidatorType.MIN_LENGTH,"Minimum length is 10 words",10)/*,
                    RVEValidatorFactory.getValidator(RVEValidatorType.EQUAL,"Name should Be Angelina","Angelina")*/
                    );                }
            });
            txt_sign_up.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, Register_Free_Activity.class));
                }
            });
            txt_terms_conditions.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                  /*  Intent intent = new Intent(act, TermsAndConditionsWebView.class);
                    intent.putExtra("str_type", "main");
                    startActivity(intent);*/
            /*        Intent intent = new Intent(act, SampleActivity.class);
                   // Intent intent = new Intent(act, ViewPdf.class);
                    intent.putExtra("URL", "http://myphilan.com/philan-terms-condition-22-01-2020.pdf");
                    startActivity(intent);*/
                }
            });

            if (AppController.getSpRememberUserInfo().getBoolean(SPUtils.IS_REMEMBER_User, false)) {
                cb_login_rememberMe.setChecked(true);

                String useridmember = AppController.getSpRememberUserInfo().getString(SPUtils.IS_REMEMBER_ID_Member, "");

                edtxt_userid_member.setText(useridmember);
            }

            edtxt_password_member.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == 1234 || id == EditorInfo.IME_NULL) {
                        AppUtils.hideKeyboardOnClick(act, textView);
                        ValidateData();
                        return true;
                    }
                    return false;
                }
            });

            txt_forgot_password.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(act, v);
                    startActivity(new Intent(act, Forget_Password_Activity.class));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    private void ValidateData() {
            edtxt_userid_member.setError(null);
            edtxt_password_member.setError(null);

            userid = edtxt_userid_member.getText().toString().trim();
            password = edtxt_password_member.getText().toString().trim();

            if (TextUtils.isEmpty(userid)) {
                AppUtils.alertDialog(act, getResources().getString(R.string.error_required_user_id));
                edtxt_userid_member.requestFocus();
            } else if (TextUtils.isEmpty(password)) {
                AppUtils.alertDialog(act, getResources().getString(R.string.error_required_password));
                edtxt_password_member.requestFocus();
            }/* else if (!cb_accept.isChecked()) {
                AppUtils.alertDialog(act, "Kindly Checked Terms & Conditions First");
                edtxt_password_member.requestFocus();
                btn.revertAnimation();
            }*/ else {
                if (AppUtils.isNetworkAvailable(act)) {
                    executeLoginRequest(userid, password);
                } else {
                    AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
                }
            }
    }

    private void executeLoginRequest(final String userId, final String passwd) {
        try {

            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        //        AppUtils.showProgressDialog(act);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IDNo", userId));
                            postParameters.add(new BasicNameValuePair("Password", passwd));
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

                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                                if (jsonArrayData.length() != 0) {
                                    saveLoginUserInfo(jsonArrayData);
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

    private void saveLoginUserInfo(final JSONArray jsonArray) {
        try {
            //  AppUtils.dismissProgressDialog();

            if (cb_login_rememberMe.isChecked()) {
                String memberuserid = edtxt_userid_member.getText().toString().trim();

                AppController.getSpRememberUserInfo().edit().putBoolean(SPUtils.IS_REMEMBER_User, true)
                        .putString(SPUtils.IS_REMEMBER_ID_Member, memberuserid).commit();
            } else {
                AppController.getSpRememberUserInfo().edit().putBoolean(SPUtils.IS_REMEMBER_User, false)
                        .putString(SPUtils.IS_REMEMBER_ID_Member, " ; ").commit();
            }

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
                    // .putString(SPUtils.USER_EMAIL, jsonArray.getJSONObject(0).getString("EMail"))
                    .putString(SPUtils.USER_EMAIL, "")
                    .putString(SPUtils.USER_DOJ, AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("DOJ")))
                    .commit();

            startSplash(new Intent(act, DashBoard_Activity.class));

            AppController.getSpIsLogin().edit().putBoolean(SPUtils.IS_LOGIN, true).commit();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    private void startSplash(final Intent intent) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}