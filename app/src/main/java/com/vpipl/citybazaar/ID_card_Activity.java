package com.vpipl.citybazaar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mukesh on 27/12/2019.
 */
public class ID_card_Activity extends AppCompatActivity {

    private String TAG = "ID_card_Activity";

    Activity act = ID_card_Activity.this;
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
    Button btn_download ;
    TextView  tv_mem_name ,tv_rank_name ,tv_mem_mobileno ,tv_mem_emailid ,txt_heading ,tv_idno;
    ImageView iv_bc_img ;
    ScrollView sv_detail ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_card);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            AppUtils.changeStatusBarColor(act);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            tv_mem_name = findViewById(R.id.tv_mem_name);
            tv_rank_name = findViewById(R.id.tv_rank_name);
            tv_mem_mobileno = findViewById(R.id.tv_mem_mobileno);
            tv_mem_emailid = findViewById(R.id.tv_mem_emailid);
            iv_bc_img = findViewById(R.id.iv_bc_img);
            sv_detail = findViewById(R.id.sv_detail);
            txt_heading = findViewById(R.id.txt_heading);
            tv_idno = findViewById(R.id.tv_idno);

            String bytecode = AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "");

            if (!bytecode.equalsIgnoreCase("")) {
                iv_bc_img.setImageBitmap(AppUtils.getBitmapFromString(bytecode));
            }

            if (AppUtils.isNetworkAvailable(act)) {
                //executeToGetBusinessCardInfo();
                executeGetDashBoardDetails();
                tv_mem_name.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
                tv_mem_mobileno.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, ""));
                tv_mem_emailid.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_EMAIL, ""));
                tv_idno.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, ""));

            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }

            btn_download = findViewById(R.id.btn_download) ;
            findViewById(R.id.btn_download).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 /*   Bitmap bitmap = takeScreenshot();
                    saveBitmap(bitmap);*/
                    sv_detail.post(new Runnable() {
                        @Override
                        public void run() {
                            sv_detail.fullScroll(ScrollView.FOCUS_UP);
                        }
                    });
                    btn_download.setVisibility(View.GONE);
                    txt_heading.setVisibility(View.GONE);
                    takeScreenshot();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

/*    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }*/
    public void saveBitmap(Bitmap bitmap) {
        File imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    private void executeGetDashBoardDetails() {
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
                            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));

                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToGetDashboardDetail, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(act);
                        }

                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(resultData);

                            JSONArray jsonArrayMemberRank = jsonObject.getJSONArray("MemberRank");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                WriteValues(jsonArrayMemberRank);
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

    private void WriteValues(JSONArray jsonArrayMemberRank ) {

        try {
            tv_mem_name.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
            tv_mem_mobileno.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, ""));
            tv_mem_emailid.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_EMAIL, ""));
            tv_idno.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, ""));

            if (jsonArrayMemberRank.length() > 0) {
                tv_rank_name.setText("Rank : " + jsonArrayMemberRank.getJSONObject(0).getString("Rank"));
            }
            else {
                tv_rank_name.setText("Rank : NA");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }
    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }
}
