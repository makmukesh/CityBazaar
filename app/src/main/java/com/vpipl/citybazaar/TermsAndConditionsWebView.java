package com.vpipl.citybazaar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.SPUtils;

public class TermsAndConditionsWebView extends AppCompatActivity {

    Activity act = TermsAndConditionsWebView.this;
    WebView wv_chord_song ;

    String str_song_url= "", str_song_letter;

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);

        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));
        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));
                    onBackPressed();
            }
        });

        img_login_logout.setVisibility(View.GONE);

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
        setContentView(R.layout.activity_terms_and_condition);

        wv_chord_song = findViewById(R.id.wv_terms_and_conditions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        wv_chord_song.setBackgroundColor(Color.TRANSPARENT);

        if (getIntent().getExtras() != null) {
            str_song_letter = getIntent().getStringExtra("str_type");
        }
        setSongList(str_song_letter);


        if (!str_song_url.equalsIgnoreCase("")) {
            wv_chord_song.loadUrl(str_song_url);
        } else {
            wv_chord_song.loadUrl("file:///android_asset/NoDataFound.html");
        }
    }

    private void setSongList(String song_code) {

        if (!song_code.equalsIgnoreCase("")) {
            if (song_code.equalsIgnoreCase("LM")) {
                str_song_url = "file:///android_asset/terms_and_condition_member_join.html";
            } else if (song_code.equalsIgnoreCase("LG")) {
                str_song_url = "file:///android_asset/terms_and_condition_guest_login.html";
            }
        }
    }
}



