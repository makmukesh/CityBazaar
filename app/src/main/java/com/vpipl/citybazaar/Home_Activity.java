package com.vpipl.citybazaar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.BuildConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.iammert.library.readablebottombar.ReadableBottomBar;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.vpipl.citybazaar.Adapters.Shopping_Fashion_List_Adapter;
import com.vpipl.citybazaar.Adapters.Shopping_Home_Appliances_List_Adapter;
import com.vpipl.citybazaar.Adapters.Shopping_Mobiles_List_Adapter;
import com.vpipl.citybazaar.Adapters.SliderAdapterExample;
import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;
import com.vpipl.citybazaar.model.SliderItem;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mukesh on 25/01/2021.
 */
public class Home_Activity extends AppCompatActivity {

    private String TAG = "Home_Activity";
    Activity act = Home_Activity.this;
    SliderView sliderView;
    private SliderAdapterExample adapter_slider;
    ImageView iv_login_user;
    ImageView iv_banner_first, iv_banner_second;
    public static ArrayList<HashMap<String, String>> arraylist_Fashion = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> arraylist_Home_Appliances = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> arraylist_Mobiles = new ArrayList<>();
    private RecyclerView rec_Fashion;
    private RecyclerView rec_Home_Appliances;
    private RecyclerView rec_Mobiles;
    private Shopping_Fashion_List_Adapter adapter_Fashion;
    private Shopping_Home_Appliances_List_Adapter adapter_Home_Appliances;
    private Shopping_Mobiles_List_Adapter adapter_Mobiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            AppUtils.changeStatusBarColor(act);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            rec_Fashion = findViewById(R.id.rec_Fashion);
            rec_Home_Appliances = findViewById(R.id.rec_Home_Appliances);
            rec_Mobiles = findViewById(R.id.rec_Mobiles);
            iv_login_user = findViewById(R.id.iv_login_user);
            iv_banner_first = findViewById(R.id.iv_banner_first);
            iv_banner_second = findViewById(R.id.iv_banner_second);

            iv_login_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, DashBoard_Activity.class));
                }
            });

            sliderView = findViewById(R.id.imageSlider);

            adapter_slider = new SliderAdapterExample(this);
            sliderView.setSliderAdapter(adapter_slider);

            sliderView.setIndicatorAnimation(IndicatorAnimations.DROP); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
            sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
            sliderView.setIndicatorSelectedColor(Color.WHITE);
            sliderView.setIndicatorUnselectedColor(Color.GRAY);
            sliderView.setScrollTimeInSec(3);
            sliderView.setAutoCycle(true);
            sliderView.startAutoCycle();

            sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
                @Override
                public void onIndicatorClicked(int position) {
                    sliderView.setCurrentPagePosition(position);
                }
            });

            if (AppUtils.isNetworkAvailable(act)) {
                executeSliderPhotosRequest();
            } else {
                AppUtils.alertDialogWithFinish(act, getResources().getString(R.string.txt_networkAlert));
            }

            rec_Fashion.setHasFixedSize(true);
            LinearLayoutManager layoutManager112 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
            layoutManager112.setReverseLayout(false);
            rec_Fashion.setLayoutManager(layoutManager112);
            arraylist_Fashion = new ArrayList<>();
            adapter_Fashion = new Shopping_Fashion_List_Adapter(this, arraylist_Fashion);
            int resId1112 = R.anim.test;
            LayoutAnimationController animation1112 = AnimationUtils.loadLayoutAnimation(this, resId1112);
            rec_Fashion.setLayoutAnimation(animation1112);

            rec_Home_Appliances.setHasFixedSize(true);
            LinearLayoutManager layoutManager1123 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
            layoutManager1123.setReverseLayout(false);
            rec_Home_Appliances.setLayoutManager(layoutManager1123);
            arraylist_Home_Appliances = new ArrayList<>();
            adapter_Home_Appliances = new Shopping_Home_Appliances_List_Adapter(this, arraylist_Home_Appliances);
            int resId11122 = R.anim.test;
            LayoutAnimationController animation11122 = AnimationUtils.loadLayoutAnimation(this, resId11122);
            rec_Home_Appliances.setLayoutAnimation(animation11122);

            rec_Mobiles.setHasFixedSize(true);
            LinearLayoutManager layoutManager11234 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
            layoutManager11234.setReverseLayout(false);
            rec_Mobiles.setLayoutManager(layoutManager11234);
            arraylist_Mobiles = new ArrayList<>();
            adapter_Mobiles = new Shopping_Mobiles_List_Adapter(this, arraylist_Mobiles);
            int resId111223 = R.anim.test;
            LayoutAnimationController animation111223 = AnimationUtils.loadLayoutAnimation(this, resId111223);
            rec_Mobiles.setLayoutAnimation(animation111223);

            getAllActivityListResult();
            getAllActivityListResult_Home();
            getAllActivityListResult_Mobiles();

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
                                /*Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "" + getResources().getString(R.string.app_name));
                                String shareMessage = "\nLet me recommend you this application\n\n";
                                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                                startActivity(Intent.createChooser(shareIntent, "choose one"));*/
                                createlink();
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
            AppUtils.showExceptionDialog(act);
        }
    }
    public void createlink() {
        Log.e("CreateLink", "");

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("http://rmconline.in/"))
                .setDynamicLinkDomain("citybazaar.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();

        Log.e("long Link", "" + dynamicLinkUri);
       // String shareMessage = "\nLet me recommend you this application\n\n";

        String str = "https://citybazaar.page.link/?" +
                "link=http://rmconline.in/?referid=" + AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "") +
                "&apn=" + getPackageName() +
                "&et=" + "My refer link" +
                "&sd=" + "Get Share Points" +
                "&si=" + "http://rmconline.in/design_img/Royal-Marketing-Concept.png";

        Log.e("manual Link", "" + dynamicLinkUri);

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                // .setLongLink(dynamicLink.getUri())
                .setLongLink(Uri.parse(str))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();

                            Log.e("Short Link", "" + shortLink);
                            Log.e("Flow Link", "" + flowchartLink);

                            Intent i = new Intent(android.content.Intent.ACTION_SEND);
                            i.setType("text/plain");
                            i.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                            i.putExtra(android.content.Intent.EXTRA_TEXT, "" + shortLink);
                            startActivity(Intent.createChooser(i, "Share App via"));
                        } else {
                            // Error
                            Log.e("Error", "" + task.getException());
                        }
                    }
                });

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

    public void addNewItem(String URL) {
        SliderItem sliderItem = new SliderItem();
        sliderItem.setDescription("");
        sliderItem.setImageUrl("" + URL);
        adapter_slider.addItem(sliderItem);
    }

    private void executeSliderPhotosRequest() {
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
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodSelect_Advertisment, TAG);
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
                                for (int i = 0; i < jsonObject.getJSONArray("SliderAdverisment").length(); i++) {
                                    JSONObject jsonObject1 = jsonObject.getJSONArray("SliderAdverisment").getJSONObject(i);
                                    addNewItem("" + jsonObject1.getString("Image"));
                                }

                                for (int i = 0; jsonObject.getJSONArray("BannerAdvertisment").length() > i; i++) {
                                    JSONObject jsonObject1 = jsonObject.getJSONArray("BannerAdvertisment").getJSONObject(i);
                                    if (i == 0) {
                                        AppUtils.loadProductImage(act, "" + jsonObject1.getString("Image"), iv_banner_first);
                                    } else if (i == 1) {
                                        AppUtils.loadProductImage(act, "" + jsonObject1.getString("Image"), iv_banner_second);
                                    } else {
                                        AppUtils.loadProductImage(act, "" + jsonObject1.getString("Image"), iv_banner_second);
                                    }
                                }

                            } else {
                                //   AppUtils.alertDialog(act, jsonObject.getString("Message"));
                                addNewItem("http://rmconline.in/design_img/slide-1.jpg");
                                addNewItem("http://rmconline.in/design_img/slide-2.jpg");
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

    private void getAllActivityListResult() {
        try {
            arraylist_Fashion.clear();

            HashMap<String, String> hashMap1 = new HashMap<>();
            hashMap1.put("id", "101");
            hashMap1.put("product_name", "Fashion-1");
            hashMap1.put("product_img", "http://rmconline.in/Shopping/Fashion-1.jpg");
            arraylist_Fashion.add(hashMap1);

            HashMap<String, String> hashMap2 = new HashMap<>();
            hashMap2.put("id", "102");
            hashMap2.put("product_name", "Fashion-2");
            hashMap2.put("product_img", "http://rmconline.in/Shopping/Fashion-2.jpg");
            arraylist_Fashion.add(hashMap2);

            HashMap<String, String> hashMap3 = new HashMap<>();
            hashMap3.put("id", "103");
            hashMap3.put("product_name", "Fashion-3");
            hashMap3.put("product_img", "http://rmconline.in/Shopping/Fashion-3.jpg");
            arraylist_Fashion.add(hashMap3);

            HashMap<String, String> hashMap4 = new HashMap<>();
            hashMap4.put("id", "104");
            hashMap4.put("product_name", "Fashion-4");
            hashMap4.put("product_img", "http://rmconline.in/Shopping/Fashion-4.jpg");
            arraylist_Fashion.add(hashMap4);

            HashMap<String, String> hashMap5 = new HashMap<>();
            hashMap5.put("id", "105");
            hashMap5.put("product_name", "Fashion-5");
            hashMap5.put("product_img", "http://rmconline.in/Shopping/Fashion-5.jpg");
            arraylist_Fashion.add(hashMap5);

            HashMap<String, String> hashMap6 = new HashMap<>();
            hashMap6.put("id", "106");
            hashMap6.put("product_name", "Fashion-6");
            hashMap6.put("product_img", "http://rmconline.in/Shopping/Fashion-6.jpg");
            arraylist_Fashion.add(hashMap6);

            HashMap<String, String> hashMap7 = new HashMap<>();
            hashMap7.put("id", "107");
            hashMap7.put("product_name", "Fashion-7");
            hashMap7.put("product_img", "http://rmconline.in/Shopping/Fashion-7.jpg");
            arraylist_Fashion.add(hashMap7);

            HashMap<String, String> hashMap8 = new HashMap<>();
            hashMap8.put("id", "108");
            hashMap8.put("product_name", "Fashion-8");
            hashMap8.put("product_img", "http://rmconline.in/Shopping/Fashion-8.jpg");
            arraylist_Fashion.add(hashMap8);

            HashMap<String, String> hashMap9 = new HashMap<>();
            hashMap9.put("id", "109");
            hashMap9.put("product_name", "Fashion-9");
            hashMap9.put("product_img", "http://rmconline.in/Shopping/Fashion-9.jpg");
            arraylist_Fashion.add(hashMap9);

            HashMap<String, String> hashMap10 = new HashMap<>();
            hashMap10.put("id", "110");
            hashMap10.put("product_name", "Fashion-10");
            hashMap10.put("product_img", "http://rmconline.in/Shopping/Fashion-10.jpg");
            arraylist_Fashion.add(hashMap10);

            showListView();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void showListView() {
        try {
            if (arraylist_Fashion.size() > 0) {
                adapter_Fashion = new Shopping_Fashion_List_Adapter(act, arraylist_Fashion);
                rec_Fashion.setAdapter(adapter_Fashion);
                adapter_Fashion.notifyDataSetChanged();
                rec_Fashion.setVisibility(View.VISIBLE);
            } else {
                //  ll_data_found.setVisibility(View.GONE);
                //  ll_no_data_found.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }
    private void getAllActivityListResult_Home() {
        try {
            arraylist_Home_Appliances.clear();

            HashMap<String, String> hashMap1 = new HashMap<>();
            hashMap1.put("id", "201");
            hashMap1.put("product_name", "Home Appliances-1");
            hashMap1.put("product_img", "http://rmconline.in/Shopping/home-appliances-1.jpg");
            arraylist_Home_Appliances.add(hashMap1);

            HashMap<String, String> hashMap2 = new HashMap<>();
            hashMap2.put("id", "202");
            hashMap2.put("product_name", "Home Appliances-2");
            hashMap2.put("product_img", "http://rmconline.in/Shopping/home-appliances-2.jpg");
            arraylist_Home_Appliances.add(hashMap2);

            HashMap<String, String> hashMap3 = new HashMap<>();
            hashMap3.put("id", "103");
            hashMap3.put("product_name", "Home Appliances-3");
            hashMap3.put("product_img", "http://rmconline.in/Shopping/home-appliances-3.jpg");
            arraylist_Home_Appliances.add(hashMap3);

            HashMap<String, String> hashMap4 = new HashMap<>();
            hashMap4.put("id", "104");
            hashMap4.put("product_name", "Home Appliances-4");
            hashMap4.put("product_img", "http://rmconline.in/Shopping/home-appliances-4.jpg");
            arraylist_Home_Appliances.add(hashMap4);

            HashMap<String, String> hashMap5 = new HashMap<>();
            hashMap5.put("id", "105");
            hashMap5.put("product_name", "Home Appliances-5");
            hashMap5.put("product_img", "http://rmconline.in/Shopping/home-appliances-5.jpg");
            arraylist_Home_Appliances.add(hashMap5);

            HashMap<String, String> hashMap6 = new HashMap<>();
            hashMap6.put("id", "106");
            hashMap6.put("product_name", "Home Appliances-6");
            hashMap6.put("product_img", "http://rmconline.in/Shopping/home-appliances-6.jpg");
            arraylist_Home_Appliances.add(hashMap6);

            HashMap<String, String> hashMap7 = new HashMap<>();
            hashMap7.put("id", "107");
            hashMap7.put("product_name", "Home Appliances-7");
            hashMap7.put("product_img", "http://rmconline.in/Shopping/home-appliances-7.jpg");
            arraylist_Home_Appliances.add(hashMap7);

            HashMap<String, String> hashMap8 = new HashMap<>();
            hashMap8.put("id", "108");
            hashMap8.put("product_name", "Home Appliances-8");
            hashMap8.put("product_img", "http://rmconline.in/Shopping/home-appliances-8.jpg");
            arraylist_Home_Appliances.add(hashMap8);

            HashMap<String, String> hashMap9 = new HashMap<>();
            hashMap9.put("id", "109");
            hashMap9.put("product_name", "Home Appliances-9");
            hashMap9.put("product_img", "http://rmconline.in/Shopping/home-appliances-9.jpg");
            arraylist_Home_Appliances.add(hashMap9);

            HashMap<String, String> hashMap10 = new HashMap<>();
            hashMap10.put("id", "110");
            hashMap10.put("product_name", "Home Appliances-10");
            hashMap10.put("product_img", "http://rmconline.in/Shopping/home-appliances-10.jpg");
            arraylist_Home_Appliances.add(hashMap10);

            showListView_Home();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void showListView_Home() {
        try {
            if (arraylist_Home_Appliances.size() > 0) {
                adapter_Home_Appliances = new Shopping_Home_Appliances_List_Adapter(act, arraylist_Home_Appliances);
                rec_Home_Appliances.setAdapter(adapter_Home_Appliances);
                adapter_Home_Appliances.notifyDataSetChanged();
                rec_Home_Appliances.setVisibility(View.VISIBLE);
            } else {
                //  ll_data_found.setVisibility(View.GONE);
                //  ll_no_data_found.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void getAllActivityListResult_Mobiles() {
        try {
            arraylist_Mobiles.clear();

            HashMap<String, String> hashMap1 = new HashMap<>();
            hashMap1.put("id", "301");
            hashMap1.put("product_name", "Mobiles-1");
            hashMap1.put("product_img", "http://rmconline.in/Shopping/mobile-1.jpg");
            arraylist_Mobiles.add(hashMap1);

            HashMap<String, String> hashMap2 = new HashMap<>();
            hashMap2.put("id", "302");
            hashMap2.put("product_name", "Mobiles-2");
            hashMap2.put("product_img", "http://rmconline.in/Shopping/mobile-2.jpg");
            arraylist_Mobiles.add(hashMap2);

            HashMap<String, String> hashMap3 = new HashMap<>();
            hashMap3.put("id", "303");
            hashMap3.put("product_name", "Mobiles-3");
            hashMap3.put("product_img", "http://rmconline.in/Shopping/mobile-3.jpg");
            arraylist_Mobiles.add(hashMap3);

            HashMap<String, String> hashMap4 = new HashMap<>();
            hashMap4.put("id", "304");
            hashMap4.put("product_name", "Mobiles-4");
            hashMap4.put("product_img", "http://rmconline.in/Shopping/mobile-4.jpg");
            arraylist_Mobiles.add(hashMap4);

            HashMap<String, String> hashMap5 = new HashMap<>();
            hashMap5.put("id", "305");
            hashMap5.put("product_name", "Mobiles-5");
            hashMap5.put("product_img", "http://rmconline.in/Shopping/mobile-5.jpg");
            arraylist_Mobiles.add(hashMap5);

            HashMap<String, String> hashMap6 = new HashMap<>();
            hashMap6.put("id", "306");
            hashMap6.put("product_name", "Mobiles-6");
            hashMap6.put("product_img", "http://rmconline.in/Shopping/mobile-6.jpg");
            arraylist_Mobiles.add(hashMap6);

            HashMap<String, String> hashMap7 = new HashMap<>();
            hashMap7.put("id", "307");
            hashMap7.put("product_name", "Mobiles-7");
            hashMap7.put("product_img", "http://rmconline.in/Shopping/mobile-7.jpg");
            arraylist_Mobiles.add(hashMap7);

            HashMap<String, String> hashMap8 = new HashMap<>();
            hashMap8.put("id", "308");
            hashMap8.put("product_name", "Mobiles-8");
            hashMap8.put("product_img", "http://rmconline.in/Shopping/mobile-8.jpg");
            arraylist_Mobiles.add(hashMap8);

            HashMap<String, String> hashMap9 = new HashMap<>();
            hashMap9.put("id", "309");
            hashMap9.put("product_name", "Mobiles-9");
            hashMap9.put("product_img", "http://rmconline.in/Shopping/mobile-9.jpg");
            arraylist_Mobiles.add(hashMap9);

            HashMap<String, String> hashMap10 = new HashMap<>();
            hashMap10.put("id", "310");
            hashMap10.put("product_name", "Mobiles-10");
            hashMap10.put("product_img", "http://rmconline.in/Shopping/mobile-10.jpg");
            arraylist_Mobiles.add(hashMap10);

            showListView_Mobiles();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void showListView_Mobiles() {
        try {
            if (arraylist_Mobiles.size() > 0) {
                adapter_Mobiles = new Shopping_Mobiles_List_Adapter(act, arraylist_Mobiles);
                rec_Mobiles.setAdapter(adapter_Mobiles);
                adapter_Mobiles.notifyDataSetChanged();
                rec_Mobiles.setVisibility(View.VISIBLE);
            } else {
                //  ll_data_found.setVisibility(View.GONE);
                //  ll_no_data_found.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }
}
