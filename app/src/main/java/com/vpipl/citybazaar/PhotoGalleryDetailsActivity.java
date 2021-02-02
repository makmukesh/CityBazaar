package com.vpipl.citybazaar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.RequestQueue;
import com.vpipl.citybazaar.slider.CustomVolleyRequest;
import com.vpipl.citybazaar.slider.SliderUtils;
import com.vpipl.citybazaar.slider.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class PhotoGalleryDetailsActivity extends AppCompatActivity {
    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    RequestQueue rq;
    List<SliderUtils> sliderImg;
    ViewPagerAdapter viewPagerAdapter;
    private int currentPage = 0;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery_details);

        rq = CustomVolleyRequest.getInstance(this).getRequestQueue();
        sliderImg = new ArrayList<>();
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        /*final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == sliderImg.size()) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, 1000, 2000);*/

      //  Toast.makeText(this, "" + getIntent().getStringExtra("id"), Toast.LENGTH_SHORT).show();
        show();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                try {

                } catch (Exception ex) {

                    ex.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void show() {
        for (int i = 0; i < AppController.PhotoList.size(); i++) {
            SliderUtils sliderUtils = new SliderUtils();
            sliderUtils.setSliderImageUrl(AppController.PhotoList.get(i).get("Photo_Url"));
            sliderImg.add(sliderUtils);
        }

        viewPagerAdapter = new ViewPagerAdapter(sliderImg, PhotoGalleryDetailsActivity.this);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(Integer.parseInt(getIntent().getStringExtra("id")));

    }
}
