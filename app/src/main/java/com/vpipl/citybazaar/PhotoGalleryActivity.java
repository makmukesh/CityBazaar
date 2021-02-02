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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.BuildConfig;
import com.iammert.library.readablebottombar.ReadableBottomBar;
import com.vpipl.citybazaar.Adapters.Photo_Gallery_Adapter;
import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.Cache;
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
public class PhotoGalleryActivity extends Activity {

    private String TAG = "PhotoGalleryActivity";

    Activity act;
    public Photo_Gallery_Adapter adapter;
    RecyclerView recyclerView;
    LinearLayout ll_data_found, ll_no_data_found;
    List<StackHelperCategoryList> stackHelperListcategory = new ArrayList<>();
    JSONArray jsonarray_categorylist;
    String categorycode = "0", categoryName = "";
    Spinner spinner_selectcategory;

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
        setContentView(R.layout.activity_photo_gallery);

        try {
            act = PhotoGalleryActivity.this;
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            Toolbar toolbar = findViewById(R.id.toolbar);
            SetupToolbar();

            ll_data_found = findViewById(R.id.ll_data_found);
            ll_no_data_found = findViewById(R.id.ll_no_data_found);
            spinner_selectcategory = findViewById(R.id.spinner_selectcategory);
            /*News Section Start*/
            recyclerView = (RecyclerView) findViewById(R.id.listView);

            // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            // recyclerView.setLayoutManager(mLayoutManager);

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

        /*  GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),3);
            gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); // set Horizontal Orientation
            recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView*/

            if (AppUtils.isNetworkAvailable(act)) {
                executeCategoryListRequest();
            } else {
                AppUtils.alertDialogWithFinish(act, getResources().getString(R.string.txt_networkAlert));
            }
            jsonarray_categorylist = new JSONArray();

            spinner_selectcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                    try {
                        if (jsonarray_categorylist.length() > 0) {
                            categoryName = ((StackHelperCategoryList) spinner_selectcategory.getSelectedItem()).getName();
                            categorycode = ((StackHelperCategoryList) spinner_selectcategory.getSelectedItem()).getCode();
                        }
                        executePhotoGalleryRequest();
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }

                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

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
            AppUtils.showExceptionDialog(PhotoGalleryActivity.this);
        }
    }

    private void spincategory() {
        stackHelperListcategory = new ArrayList<>();

        try {
            for (int i = 0; i < jsonarray_categorylist.length(); i++) {
                JSONObject jsonobject = jsonarray_categorylist.getJSONObject(i);
                categorycode = jsonobject.getString("ID");
                categoryName = jsonobject.getString("CategoryName");

                StackHelperCategoryList stackHelperCategoryList = new StackHelperCategoryList();
                stackHelperCategoryList.setCode(categorycode);
                stackHelperCategoryList.setName(categoryName);
                stackHelperListcategory.add(stackHelperCategoryList);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<StackHelperCategoryList> dataAdapter = new ArrayAdapter<StackHelperCategoryList>(this, R.layout.sppiner_item, stackHelperListcategory);
        dataAdapter.setDropDownViewResource(R.layout.sppiner_item);

        spinner_selectcategory.setAdapter(dataAdapter);
    }

    private void executeCategoryListRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(act);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToSelect_Category, TAG);
                } catch (Exception ignored) {
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
                            jsonarray_categorylist = jsonArrayData;
                            spincategory();
                        } else {
                            jsonarray_categorylist = new JSONArray("[{\"ID\":0,\"CategoryName\":\"-- No Category Found --\"}]");
                            spincategory();
                        }
                    } else {
                        jsonarray_categorylist = new JSONArray("[{\"ID\":0,\"CategoryName\":\"-- No Category Found --\"}]");
                        spincategory();
                    }
                    executePhotoGalleryRequest();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executePhotoGalleryRequest() {
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
                            postParameters.add(new BasicNameValuePair("CategoryID", "" + categorycode));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToSelect_Gallary, TAG);
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
                            AppController.PhotoList.clear();
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                ll_data_found.setVisibility(View.VISIBLE);
                                ll_no_data_found.setVisibility(View.GONE);
                                if (jsonObject.getJSONArray("Data").length() > 0) {
                                    getAllActivityListResult(jsonObject.getJSONArray("Data"));
                                }
                            } else {
                                showListView();
                                //   AppUtils.alertDialog(act, jsonObject.getString("Message"));
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

    private void getAllActivityListResult(JSONArray jsonArray) {
        try {
            AppController.PhotoList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", jsonObject.getString("CategoryID"));
                hashMap.put("Name", jsonObject.getString("Name"));
                hashMap.put("Photo_Url", "" + jsonObject.getString("Image"));
                AppController.PhotoList.add(hashMap);
            }

            showListView();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void showListView() {
        try {
            if (AppController.PhotoList.size() > 0) {
                adapter = new Photo_Gallery_Adapter(act, AppController.PhotoList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);

                ll_data_found.setVisibility(View.VISIBLE);
                ll_no_data_found.setVisibility(View.GONE);
            } else {
                ll_data_found.setVisibility(View.GONE);
                ll_no_data_found.setVisibility(View.VISIBLE);
            }
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
            AppUtils.showExceptionDialog(PhotoGalleryActivity.this);
        }
        System.gc();
    }
}
