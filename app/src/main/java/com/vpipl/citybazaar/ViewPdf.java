package com.vpipl.citybazaar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.SPUtils;

public class ViewPdf extends AppCompatActivity {

    Activity act = ViewPdf.this;
    LinearLayout viewepaper_back;
    String pdfurl;


    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);

        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));
        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
               /* if (drawer.isDrawerOpen(navigationView)) {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable. icon_nav_bar));
                    drawer.closeDrawer(navigationView);
                } else {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));
                    drawer.openDrawer(navigationView);
                }*/
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
        setContentView(R.layout.activity_sponsor_genealogy);

        try
        {
            AppUtils.changeStatusBarColor(act);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            Intent intent = getIntent();
            pdfurl = intent.getStringExtra("URL");

            /*viewepaper_back = (LinearLayout)findViewById(R.id.viewepaper_back);
            viewepaper_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                }
            });
*/
        /*    getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);*/

            WebView htmlWebView = (WebView)findViewById(R.id.webView_viewGenealogy);
            htmlWebView.setWebViewClient(new CustomWebViewClient());
            WebSettings webSetting = htmlWebView.getSettings();
            webSetting.setJavaScriptEnabled(true);
            webSetting.setDisplayZoomControls(true);
            htmlWebView.loadUrl("http://docs.google.com/gview?embedded=true&url="+pdfurl);

        }
        catch (Exception ex)
        {
            ex.getMessage();
        }
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
    public void onBackPressed() {
        super.onBackPressed();
    }
}
