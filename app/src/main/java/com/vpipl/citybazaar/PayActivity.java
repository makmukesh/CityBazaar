package com.vpipl.citybazaar;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.BuildConfig;
import com.iammert.library.readablebottombar.ReadableBottomBar;
import com.vpipl.citybazaar.Adapters.Photo_Gallery_Adapter;
import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.model.StackHelperCategoryList;
import com.vpipl.citybazaar.model.StackHelperCity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mukesh on 01/02/2021.
 */
public class PayActivity extends Activity {

    private String TAG = "PayActivity";

    Activity act;

    ImageView img_nav_back;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_nav_back = findViewById(R.id.img_nav_back);

        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));
        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageView img_login_logout = findViewById(R.id.img_login_logout);

        img_login_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.showDialogSignOut(act);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        try {
            act = PayActivity.this;
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            Toolbar toolbar = findViewById(R.id.toolbar);
            SetupToolbar();


            ReadableBottomBar bottomBar = (ReadableBottomBar) findViewById(R.id.bottomBar);
            bottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
                @Override
                public void onItemSelected(int i) {

                    switch (i) {
                        case 0:
                            startActivity(new Intent(act, Home_Activity.class));
                            finish();
                            break;
                        case 1:
                            startActivity(new Intent(act, PhotoGalleryActivity.class));
                            break;
                        case 2:
                            startActivity(new Intent(act, PayActivity.class));
                            break;
                        case 3:
                            try {
                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "" + getResources().getString(R.string.app_name));
                                String shareMessage = "\nLet me recommend you this application\n\n";
                                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                                startActivity(Intent.createChooser(shareIntent, "choose one"));
                            } catch (Exception e) {
                                e.toString();
                            }
                            break;
                        case 4:
                            startActivity(new Intent(act, Wallet_Transaction_Report_Activity.class));
                            break;
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(PayActivity.this);
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
            AppUtils.showExceptionDialog(PayActivity.this);
        }
        System.gc();
    }
}
