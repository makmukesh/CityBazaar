package com.vpipl.citybazaar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.vpipl.citybazaar.Adapters.ExpandableListAdapter;
import com.vpipl.citybazaar.Epin.Transaction_login_Activity;
import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.CircularImageView;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;

/**
 * Created by Mukesh on 27/12/2019.
 */
public class Wallet_Request_Amount_Activity extends AppCompatActivity {

    Activity act = Wallet_Request_Amount_Activity.this;
    public int RESULT_GALLERY = 0;
    public int CAMERA_REQUEST = 1;

    private static final int MEDIA_TYPE_IMAGE = 1;
    private static DrawerLayout drawer;
    private static NavigationView navigationView;
    private String TAG = "Wallet_Request_Amount_Activity";
    private TextInputEditText edtxt_amount;
    private TextInputEditText edtxt_transaction_no;
    private TextInputEditText txt_transaction_date;
    private TextInputEditText txt_choose_bank;
    private TextView txt_reference_receipt;
    private Button btn_request, btn_reset;
    private Button btn_choose_file;
    private ImageView iv_selected_file;
    private String[] bankArray;
    private String amount;
    private String transaction_no;
    private String transaction_date;
    private String bank_name;
    private Uri imageUri;
    private Bitmap bitmap = null;
    private Calendar myCalendar;
    private SimpleDateFormat sdf;
    private String selectedImagePath;
    private BottomSheetDialog mBottomSheetDialog;
    private TextView txt_welcome_name;
    private TextView txt_id_number;
    private TextView txt_available_wb;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private int lastExpandedPosition = -1;
    private ExpandableListView expListView;
    private CircularImageView profileImage;
    private JSONArray HeadingJarray;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask;

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            try {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                if (new Date().after(myCalendar.getTime())) {

                    txt_transaction_date.setText(sdf.format(myCalendar.getTime()));

                } else {
                    AppUtils.alertDialog(act, "Select Previous Dates");
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    };

    private DatePickerDialog datePickerDialog;

    private void showdatePicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, myDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        datePickerDialog.show();
    }

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);

        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(navigationView)) {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar));
                    drawer.closeDrawer(navigationView);
                } else {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));
                    drawer.openDrawer(navigationView);
                }
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
        setContentView(R.layout.activity_wallet_request_amount);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            AppUtils.changeStatusBarColor(act);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            executeLoginRequest();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            myCalendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("dd MMM yyyy");

            txt_transaction_date = findViewById(R.id.txt_transaction_date);
            txt_choose_bank = findViewById(R.id.txt_choose_bank);
            txt_reference_receipt = findViewById(R.id.txt_reference_receipt);

            edtxt_amount = findViewById(R.id.edtxt_amount);
            edtxt_transaction_no = findViewById(R.id.edtxt_transaction_no);

            iv_selected_file = findViewById(R.id.iv_selected_file);

            mBottomSheetDialog = new BottomSheetDialog(this);
            View sheetView = this.getLayoutInflater().inflate(R.layout.bottom_sheet, null);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.setTitle("Complete action using...");

            btn_choose_file = findViewById(R.id.btn_choose_file);
            btn_request = findViewById(R.id.btn_request);
            btn_reset = findViewById(R.id.btn_reset);

            btn_choose_file.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // mBottomSheetDialog.show();
                    selectImage();
                }
            });

            LinearLayout camera = sheetView.findViewById(R.id.bottom_sheet_camera);
            LinearLayout gallery = sheetView.findViewById(R.id.bottom_sheet_gallery);

            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    imageUri = AppUtils.getOutputMediaFileUri(1, TAG, act);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CAMERA_REQUEST);
                    mBottomSheetDialog.dismiss();
                }
            });

            gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, RESULT_GALLERY);
                    mBottomSheetDialog.dismiss();
                }
            });

            txt_choose_bank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppController.bankList.size() != 0) {
                        showBankDialog();
                    } else {
                        executeBankRequest();
                    }
                }
            });

            btn_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(act, view);
                    ValidateData();
                }
            });

            btn_reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(act, view);
                    ResetData();
                }
            });

            txt_transaction_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showdatePicker();
                }
            });

            drawer = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            View navHeaderView = navigationView.getHeaderView(0);
            txt_welcome_name = navHeaderView.findViewById(R.id.txt_welcome_name);
            txt_available_wb = navHeaderView.findViewById(R.id.txt_available_wb);
            txt_id_number = navHeaderView.findViewById(R.id.txt_id_number);
            profileImage = navHeaderView.findViewById(R.id.iv_Profile_Pic);
            LinearLayout LL_Nav = navHeaderView.findViewById(R.id.LL_Nav);
            expListView = findViewById(R.id.left_drawer);

            listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();

            HeadingJarray = Splash_Activity.HeadingJarray;

            LL_Nav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
                    if (Usertype.equalsIgnoreCase("DISTRIBUTOR")) {
                        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                            startActivity(new Intent(act, Profile_View_New_Activity.class));
                        } else {
                            startActivity(new Intent(act, Login_New_Activity.class));
                        }

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    }
                }
            });
            drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(@NonNull View drawerView) {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));
                }

                @Override
                public void onDrawerClosed(@NonNull View drawerView) {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar));
                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void ResetData() {
        try {
            edtxt_amount.setText("");
            edtxt_transaction_no.setText("");
            txt_transaction_date.setText("");
            txt_choose_bank.setText("");
            iv_selected_file.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void ValidateData() {
        try {

            amount = edtxt_amount.getText().toString().trim();
            transaction_no = edtxt_transaction_no.getText().toString().trim();
            transaction_date = txt_transaction_date.getText().toString().trim();
            bank_name = txt_choose_bank.getText().toString().trim();

            float amt = 0;
            try {
                amt = Float.parseFloat(amount);
            } catch (Exception ignored) {

            }
            if (TextUtils.isEmpty(amount)) {
                AppUtils.alertDialog(act, "Amount is Required");
                edtxt_amount.requestFocus();
            } else if (amt <= 0) {
                AppUtils.alertDialog(act, "Invalid Amount");
                edtxt_amount.requestFocus();
            } else if (amt > 99999) {
                AppUtils.alertDialog(act, "Maximum Amount Limit is 99999");
                edtxt_amount.requestFocus();
            } else if (TextUtils.isEmpty(bank_name)) {
                AppUtils.alertDialog(act, "Select Bank");
                txt_choose_bank.requestFocus();
            } else if (TextUtils.isEmpty(transaction_no)) {
                AppUtils.alertDialog(act, "Transaction Number is Required");
                edtxt_transaction_no.requestFocus();
            } else if (TextUtils.isEmpty(transaction_date)) {
                AppUtils.alertDialog(act, "Transaction Date is Required");
                txt_transaction_date.requestFocus();
            } else if (TextUtils.isEmpty(selectedImagePath)) {
                AppUtils.alertDialog(act, "Reference Receipt is Required");
                btn_choose_file.requestFocus();
            } else if (!AppUtils.isNetworkAvailable(act)) {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            } else {
                startRequestAmount();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void startRequestAmount() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {

                List<NameValuePair> postParameters = new ArrayList<>();
                postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("IdNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("BankName", bank_name));
                postParameters.add(new BasicNameValuePair("RequestedAmount", amount));
                postParameters.add(new BasicNameValuePair("DateValue", transaction_date));
                postParameters.add(new BasicNameValuePair("TransNo", transaction_no));
                postParameters.add(new BasicNameValuePair("ImageCode", AppUtils.getBase64StringFromBitmap(bitmap)));

                String Bankid = "0";
                for (int i = 0; i < AppController.bankList.size(); i++) {
                    if (bank_name.equalsIgnoreCase(AppController.bankList.get(i).get("Bank"))) {
                        Bankid = AppController.bankList.get(i).get("BID");
                    }
                }

                postParameters.add(new BasicNameValuePair("BankID", "" + Bankid));
                postParameters.add(new BasicNameValuePair("IP", AppUtils.getDeviceID(act)));

                executeRequestAmount(postParameters);

            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void executeBankRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(act);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodMaster_FillBank, TAG);
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
                            getBankResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(act, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(act, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getBankResult(JSONArray jsonArray) {
        try {
            AppController.bankList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("BID", jsonObject.getString("BID"));
                map.put("Bank", WordUtils.capitalizeFully(jsonObject.getString("Bank")));

                AppController.bankList.add(map);
            }


            showBankDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBankDialog() {

        try {
            bankArray = new String[AppController.bankList.size()];
            for (int i = 0; i < AppController.bankList.size(); i++) {
                bankArray[i] = AppController.bankList.get(i).get("Bank");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Bank");
            builder.setItems(bankArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_choose_bank.setText(bankArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void executeRequestAmount(final List<NameValuePair> postParameters) {
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
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToRequestWalletAmount, TAG);
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
//                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                ResetClass();
                                AppUtils.alertDialogWithFinish(act, "" + jsonObject.getString("Message"));
                            } else {
                                //ResetClass();
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

    private void ResetClass() {
        iv_selected_file.setVisibility(View.GONE);
        edtxt_amount.setText("");
        edtxt_transaction_no.setText("");
        txt_transaction_date.setText("");
        txt_choose_bank.setText("");
        txt_reference_receipt.setText("");
    }

 /*   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == RESULT_GALLERY) {
                    if (data != null) {
                        imageUri = data.getData();
                        String filepath = AppUtils.getPath(data.getData(), act);

                        if (filepath.length() > 0) {
                            selectedImagePath = filepath;
                            pickImageFromGallery();
                        }
                    }
                } else if (requestCode == CAMERA_REQUEST) {

                    Uri selectedImageUri = imageUri;
                    selectedImagePath = selectedImageUri.getPath();
                    pickImageFromGallery();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }*/

    private void pickImageFromGallery() {
        try {

            Matrix matrix = new Matrix();
            int rotate = 0;

            File imageFile = new File(selectedImagePath);
            long fileSizeInBytes = imageFile.length();
            long fileSizeInKB = fileSizeInBytes / 1024;
            Log.e(TAG, "Image Size(KB) before compress : " + fileSizeInKB);

            if (fileSizeInKB <= 5130) {
                try {
                    ExifInterface exif = new ExifInterface((imageFile.getName()));
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotate -= 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotate -= 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotate -= 90;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int nh = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                    nh = (int) (bitmapImage.getHeight() * (550.0 / bitmapImage.getWidth()));

                    matrix.postRotate(rotate);
                    bitmap = Bitmap.createBitmap(bitmapImage, 0, 0, bitmapImage.getWidth(), bitmapImage.getHeight(), matrix, true);
                    bitmapImage = null;
                } else {

                    File compressedImageFile = new Compressor(this).compressToFile(imageFile);
                    Log.e(TAG, "Image Size(KB) after compress one: " + compressedImageFile.length() / 1024);
                    Bitmap compressedImageBitmap = new Compressor(this).compressToBitmap(compressedImageFile);

                    nh = (int) (compressedImageBitmap.getHeight() * (480.0 / compressedImageBitmap.getWidth()));

                    matrix.postRotate(rotate);
                    bitmap = Bitmap.createBitmap(compressedImageBitmap, 0, 0, compressedImageBitmap.getWidth(), compressedImageBitmap.getHeight(), matrix, true);
                    compressedImageBitmap = null;
                }

                if (bitmap.getWidth() > 500) {
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 480, nh, true);
                    bitmap = scaled;
                    scaled = null;
                }

                iv_selected_file.setVisibility(View.VISIBLE);
                iv_selected_file.setImageBitmap(bitmap);

            } else
                AppUtils.alertDialog(this, "Selected file exceed the allowable file size limit (5 MB)");

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }


    private void executeLoginRequest() {
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
                            postParameters.add(new BasicNameValuePair("IDNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Password", AppController.getSpUserInfo().getString(SPUtils.USER_PASSWORD, "")));
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
                                continueapp();
                            } else {

                                Toast.makeText(act, "Please Login to continue..", Toast.LENGTH_SHORT).show();

                                AppController.getSpUserInfo().edit().clear().commit();
                                AppController.getSpIsLogin().edit().clear().commit();

                                Intent intent = new Intent(act, Login_New_Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("SendToHome", true);
                                startActivity(intent);
                                finish();

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

    public void continueapp() {

        enableExpandableList();
        LoadNavigationHeaderItems();
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

    private void enableExpandableList() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            if (HeadingJarray != null && HeadingJarray.length() > 0)
                prepareListDataDistributor(listDataHeader, listDataChild, HeadingJarray);
            else
                executeTogetDrawerMenuItems();
        }

        ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                String GroupTitle = listDataHeader.get(groupPosition);

                if (GroupTitle.trim().equalsIgnoreCase(getResources().getString(R.string.dashboard))) {
                    startActivity(new Intent(act, DashBoard_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(act);
                } else if (GroupTitle.trim().equalsIgnoreCase("New Joining")) {
                    startActivity(new Intent(act, Register_Free_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Enquiry")) {
                    startActivity(new Intent(act, Register_Complaint_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
                return false;
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }

        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String ChildItemTitle = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);

                if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.view_profile))) {
                    startActivity(new Intent(act, Profile_View_New_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Update Profile")) {
                    startActivity(new Intent(act, Profile_Update_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.change_password))) {
                    startActivity(new Intent(act, Change_Password_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.new_joining))) {
                    startActivity(new Intent(act, Register_Free_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Binary Geneology")) {
                    startActivity(new Intent(act, Sponsor_genealogy_Activity.class).putExtra("URL", QueryUtils.getViewBinarygenealogyURL(act)));
                    //startActivity(new Intent(act, Sponsor_genealogy_Activity.class).putExtra("URL", "https://boodmo.com"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Downline Team Detail")) {
                    startActivity(new Intent(act, Downline_Team_Details_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Sponsor Geneology")) {
                    startActivity(new Intent(act, Sponsor_genealogy_Activity.class).putExtra("URL", QueryUtils.getViewgenealogyURL(act)));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.sponsor_downline))) {
                    startActivity(new Intent(act, Sponsor_team_details_Activity.class).putExtra("Action", "Sponsor"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.bv_detail_report))) {
                    startActivity(new Intent(act, Sponsor_team_details_Activity.class).putExtra("Action", "Direct"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.welcome_letter))) {
                    startActivity(new Intent(act, WelcomeLetter_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase(getResources().getString(R.string.upload_kyc))) {
                    startActivity(new Intent(act, KYCUploadDocument_Activity.class).putExtra("HEADING", "Update"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("ID Card")) {
                    startActivity(new Intent(act, ID_card_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Auto Pool Details")) {
                    startActivity(new Intent(act, Auto_Growth_Pool_Details_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Downline Team Auto Pool Inc.")) {
                    startActivity(new Intent(act, Downline_Team_Auto_Growth_Incentive_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Repurchase Bill Summary")) {
                    startActivity(new Intent(act, Repurchase_Bill_Summary.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Repurchase Bv Detail")) {
                    startActivity(new Intent(act, Repurchase_BV_Detail.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Team BV Summary")) {
                    startActivity(new Intent(act, Repurchase_BV_Summary_Team_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Daily Payout")) {
                    startActivity(new Intent(act, Daily_Payout_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("BV Income")) {
                    startActivity(new Intent(act, Repurchase_BV_Summary_Team_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("View Order Detail")) {
                    startActivity(new Intent(act, MyOrders_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Place Order")) {
                    //  startActivity(new Intent(act, PlaceOrderActivity.class));
                    startActivity(new Intent(act, PlaceOrderOnlineActivity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Request For Wallet Amount")) {
                    startActivity(new Intent(act, Wallet_Request_Amount_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Request Report")) {
                    startActivity(new Intent(act, Wallet_Request_Status_Report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Wallet Transaction Detail")) {
                    startActivity(new Intent(act, Wallet_Transaction_Report_Activity.class));

                } else if (ChildItemTitle.trim().equalsIgnoreCase("Daily Incentive")) {
                    startActivity(new Intent(act, Daily_Incentive_report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Daily Incentive Report")) {
                    startActivity(new Intent(act, Daily_Incentive_Detailed_report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Weekly Incentive")) {
                    startActivity(new Intent(act, Weekly_Incentive_report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Weekly Incentive Detailed Report")) {
                    startActivity(new Intent(act, Weekly_Incentive_Detailed_report_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Monthly Incentive")) {
                    startActivity(new Intent(act, Monthly_Incentive_report_Activity.class));
                }/* else if (ChildItemTitle.trim().equalsIgnoreCase("Monthly Incentive Detail Report")) {
                    startActivity(new Intent(act, Monthly_Incentive_Detailed_report_Activity.class));
                }*/ else if (ChildItemTitle.trim().equalsIgnoreCase("Reward Detail")) {
                    startActivity(new Intent(act, Reward_Details_Activity.class));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Generated/Issued Pin Details")) {
                    startActivity(new Intent(act, Transaction_login_Activity.class).putExtra("SEND_TO", "Generated/Issue Pin Details"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("Topup/E-Pin Detail")) {
                    startActivity(new Intent(act, Transaction_login_Activity.class).putExtra("SEND_TO", "Topup/E-Pin Detail"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Transfer")) {
                    startActivity(new Intent(act, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Transfer"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Transfer Detail")) {
                    startActivity(new Intent(act, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Transfer Detail"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Recieved Detail")) {
                    startActivity(new Intent(act, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Received Detail"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Request")) {
                    startActivity(new Intent(act, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Request"));
                } else if (ChildItemTitle.trim().equalsIgnoreCase("E-Pin Request Detail")) {
                    startActivity(new Intent(act, Transaction_login_Activity.class).putExtra("SEND_TO", "E-Pin Request Detail"));

                }

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });
    }

    private void LoadNavigationHeaderItems() {
        txt_id_number.setText("");
        txt_id_number.setVisibility(View.GONE);

        txt_available_wb.setText("");
        txt_available_wb.setVisibility(View.GONE);

        txt_welcome_name.setText("Guest");

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            String welcome_text = WordUtils.capitalizeFully(AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
            txt_welcome_name.setText(welcome_text);

            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon_user);
            profileImage.setImageBitmap(largeIcon);

            String userid = AppController.getSpUserInfo().getString(SPUtils.USER_ID_NUMBER, "");
            txt_id_number.setText(userid);
            txt_id_number.setVisibility(View.VISIBLE);

            executeWalletBalanceRequest();

            String bytecode = AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "");

            if (!bytecode.equalsIgnoreCase(""))
                profileImage.setImageBitmap(AppUtils.getBitmapFromString(bytecode));
        }
    }

    private void executeWalletBalanceRequest() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(act,
                                    postParameters, QueryUtils.methodToGetWalletBalance, TAG);

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

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                    String count_text = "Wallet Balance : \u20B9 " + jsonArrayData.getJSONObject(0).getString("WBalance");
                                    txt_available_wb.setText(count_text);
                                    txt_available_wb.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareListDataDistributor(List<String> listDataHeader, Map<String, List<String>> listDataChild, JSONArray HeadingJarray) {

        List<String> Empty = new ArrayList<>();
        try {
            ArrayList<String> MenuAl = new ArrayList<>();
            for (int i = 0; i < HeadingJarray.length(); i++) {
                if (HeadingJarray.getJSONObject(i).getInt("ParentId") == 0)
                    MenuAl.add(HeadingJarray.getJSONObject(i).getString("MenuName"));
            }

            for (int aa = 0; aa < MenuAl.size(); aa++) {
                ArrayList<String> SubMenuAl = new ArrayList<>();

                for (int bb = 0; bb < HeadingJarray.length(); bb++) {
                    if (HeadingJarray.getJSONObject(aa).getInt("MenuId") == HeadingJarray.getJSONObject(bb).getInt("ParentId")) {
                        SubMenuAl.add(AppUtils.CapsFirstLetterString(HeadingJarray.getJSONObject(bb).getString("MenuName")));
                    }
                }
                listDataHeader.add(AppUtils.CapsFirstLetterString(MenuAl.get(aa)));
                listDataChild.put(listDataHeader.get(aa), SubMenuAl);
            }
            listDataHeader.add("Logout");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeTogetDrawerMenuItems() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                AppUtils.showProgressDialog(act);
            }

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
                    AppUtils.dismissProgressDialog();
                    JSONObject jsonObject = new JSONObject(resultData);

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        HeadingJarray = jsonObject.getJSONArray("Data");
                        prepareListDataDistributor(listDataHeader, listDataChild, HeadingJarray);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //   boolean result = Utility.checkPermission(act);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    // if (result)
                    cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    //   if (result)
                    galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap bitmap1 = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        String imageStoragePath = destination.getAbsolutePath();
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap = bitmap1;
        selectedImagePath = bitmap1.toString();
       /* executePostImageUploadRequest(bitmap);
        profileImage.setImageBitmap(bitmap);
        iv_Profile_Pic_dash.setImageBitmap(bitmap);*/

        Log.e("from camera data", imageStoragePath);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bitmap = bm ;
        selectedImagePath = bm.toString();
        /*executePostImageUploadRequest(bm);
        profileImage.setImageBitmap(bm);
        iv_Profile_Pic_dash.setImageBitmap(bm);
        String imagepath = bm.toString();*/
        Log.e("from gallery data", selectedImagePath);
    }
}