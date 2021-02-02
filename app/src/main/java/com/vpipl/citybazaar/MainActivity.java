package com.vpipl.citybazaar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = "MainActivity";

    Activity act;

    TextView txt_name, txt_nav_user_name, txt_header_mobileno, txt_department;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            act = MainActivity.this;
            final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            final LinearLayout holder = findViewById(R.id.holder);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    float scaleFactor = 7f;
                    float slideX = drawerView.getWidth() * slideOffset;

                    holder.setTranslationX(slideX);
                    holder.setScaleX(1 - (slideOffset / scaleFactor));
                    holder.setScaleY(1 - (slideOffset / scaleFactor));

                    super.onDrawerSlide(drawerView, slideOffset);
                }
            };

            drawer.addDrawerListener(toggle);

            //  drawer.setScrimColor(getResources().getColor(R.color.colorPrimary));
            drawer.setScrimColor(Color.TRANSPARENT);
            toggle.syncState();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimaryDark));
            } else {
                toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimaryDark));
            }

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            View headerview = navigationView.getHeaderView(0);
            txt_nav_user_name = (TextView) headerview.findViewById(R.id.txt_welcome_name);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
             super.onBackPressed();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
