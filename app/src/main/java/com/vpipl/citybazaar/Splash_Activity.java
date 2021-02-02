package com.vpipl.citybazaar;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;

import static android.view.View.GONE;

public class Splash_Activity extends AppCompatActivity {

    Activity act = Splash_Activity.this;
    private static final String TAG = "Splash_Activity";
    public static JSONArray HeadingJarray;

    private String[] PermissionGroup = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.GET_ACCOUNTS};

    int versionCode;
    String version;
    String currentVersion, latestVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        try {

            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;

            String DeviceModel = manufacturer + " " + model;

            AppController.getSpIsInstall().edit()
                    .putString(SPUtils.IS_INSTALL_DeviceModel, "" + DeviceModel)
                    .putString(SPUtils.IS_INSTALL_DeviceName, "" + DeviceModel).commit();

            PackageManager manager = getApplicationContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
            version = info.versionName;
            versionCode = info.versionCode;

            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                if (AppUtils.isNetworkAvailable(act)) {
                    getCurrentVersionnew();
                } else {
                    AppUtils.alertDialogWithFinish(act, getResources().getString(R.string.txt_networkAlert));
                }
            } else ActivityCompat.requestPermissions(this, PermissionGroup, 84);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeTogetDrawerMenuItems() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodtoGetDrawerMenuItems, TAG);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {

                    JSONObject jsonObject = new JSONObject(resultData);

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        HeadingJarray = jsonObject.getJSONArray("Data");
                    }

                    moveNextScreen();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void startSplash(final Intent intent) {
        try {

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveNextScreen() {
        try {
            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                startSplash(new Intent(act, Home_Activity.class));
               // startSplash(new Intent(act, DashBoard_Activity.class));
            } else {
                startSplash(new Intent(act, Login_New_Activity.class));
              //  startSplash(new Intent(act, Home_Activity.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 84) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentVersionnew();
            } else {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CLEAR_APP_CACHE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)
                ) {

                    showDialogOK(
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            ActivityCompat.requestPermissions(act, PermissionGroup, 84);
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }
                                }
                            });
                }
                //permission is denied (and never ask again is  checked)
                //shouldShowRequestPermissionRationale will return false
                else {
                    AppUtils.alertDialogWithFinish(this, "Go to settings and Manually Enable these permissions");
                    //proceed with logic by disabling the related features or quit the app.
                }
            }
        }
    }

    private void showDialogOK(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("These Permissions are required for use this Application")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    public void executesGetAppStatusRequest() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToCheckAppRunningStatus, TAG);
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

                            if (jsonArrayData.getJSONObject(0).getString("Status").equalsIgnoreCase("False")) {
                                showUpdateDialog(jsonArrayData.getJSONObject(0).getString("Msg"));
                            } else {
                                executeTogetDrawerMenuItems();
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

    public void showUpdateDialog(String Msg) {
        try {
            final Dialog dialog = AppUtils.createDialog(act, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml(Msg));

            TextView txt_submit = (TextView) dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("OK");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = (TextView) dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setVisibility(GONE);
            txt_cancel.setTextColor(getResources().getColor(R.color.colorPrimary));
            txt_cancel.setText("Update Later");
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void getCurrentVersionnew() {
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        currentVersion = pInfo.versionName;

        new GetLatestVersionnew().execute();

    }

    private class GetLatestVersionnew extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName()).get();
                latestVersion = doc.getElementsByClass("htlgb").get(6).text();

            } catch (Exception e) {
                Log.e("latestversionerr", e.getMessage());
                e.printStackTrace();
                latestVersion = currentVersion;
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (latestVersion != null) {
                if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                    if (!isFinishing()) { //This would help to prevent Error : BinderProxy@45d459c0 is not valid; is your activity running? error
                        showUpdateDialog();
                    }
                } else {
                    executesGetAppStatusRequest();
                }
            } else
                //   background.start();
                super.onPostExecute(jsonObject);
        }
    }

    private void showUpdateDialog() {
        final Dialog dialog = new Dialog(act, R.style.ThemeDialogCustom);
        //   dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_update);

        TextView dialog4all_txt = dialog.findViewById(R.id.tvDescription);
        Button btnNone = dialog.findViewById(R.id.btnNone);
        ImageView iv_update_image = dialog.findViewById(R.id.iv_update_image);
        dialog4all_txt.setText("An Update is available,Please Update App from Play Store.");
        GifDrawable gifDrawable = null;
        try {
            gifDrawable = new GifDrawable(getAssets(), "update.gif");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (gifDrawable != null) {
            gifDrawable.addAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationCompleted(int loopNumber) {
                    Log.d("splashscreen", "Gif animation completed");
                }
            });
            iv_update_image.setImageDrawable(gifDrawable);
        }

        btnNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();

    }

}