package com.vpipl.citybazaar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.CircularImageView;
import com.vpipl.citybazaar.Utils.QueryUtils;
import com.vpipl.citybazaar.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mukesh on 27/12/2019.
 */
public class KYCUploadDocument_Activity extends AppCompatActivity {

    Activity act = KYCUploadDocument_Activity.this;
    private static final String TAG = "KYCUploadDocument";

    private static final int RESULT_GALLERY = 0;
    private static final int CAMERA_REQUEST = 1;
    //    layout_photoProof;
    private static final int MEDIA_TYPE_IMAGE = 1;
    //    imgView_photoProof;
    private LinearLayout layout_AdrsProf;
    private LinearLayout layout_IdProf;
    private LinearLayout layout_pannoProf;
    private LinearLayout layout_Cancelled_Cheque_Prof;
    private ImageView imgView_pan_card_Prof;
    private ImageView imgView_AdrsProf;
    private ImageView imgView_IdProf;
    private ImageView imgView_Cancelled_ChequeProf;
    private String whichUpload = "";
    private String selectedImagePath = "";
    private Uri imageUri;
    private Bitmap bitmap= null;

    //    isPhotoProof = false;
    private Boolean isAddressProof = false;
    private Boolean isIdProof = false;
    private Boolean isPancardProof = false;
    private Boolean isCancelled_ChequeProf = false;
    private BottomSheetDialog mBottomSheetDialog;
    private String heading = "View";
    private TextView txt_heading;
    private TextView txt_welcome_name;
    private TextView txt_id_number;
    private TextView txt_available_wb;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private int lastExpandedPosition = -1;
    private ExpandableListView expListView;
    private CircularImageView profileImage;

    private JSONArray HeadingJarray;
    LinearLayout ll_tab_pan_card ,ll_tab_address_proof ,ll_tab_id_proof ,ll_tab_Cancelled_Cheque_proof ;
    TextView txt_tab_pan_no ,txt_tab_address_proof ,txt_tab_id_proof ,txt_tab_Cancelled_Cheque_proof ;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask;

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

        img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);

        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.icon_nav_bar_close));

        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
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
        setContentView(R.layout.activity_kyc_document);

        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            AppUtils.changeStatusBarColor(act);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            ll_tab_pan_card = findViewById(R.id.ll_tab_pan_card);
            ll_tab_address_proof = findViewById(R.id.ll_tab_address_proof);
            ll_tab_id_proof = findViewById(R.id.ll_tab_id_proof);
            ll_tab_Cancelled_Cheque_proof = findViewById(R.id.ll_tab_Cancelled_Cheque_proof);

            txt_tab_pan_no = findViewById(R.id.txt_tab_pan_no);
            txt_tab_address_proof = findViewById(R.id.txt_tab_address_proof);
            txt_tab_id_proof = findViewById(R.id.txt_tab_id_proof);
            txt_tab_Cancelled_Cheque_proof = findViewById(R.id.txt_tab_Cancelled_Cheque_proof);

            layout_pannoProf = findViewById(R.id.layout_pannoProf);
            layout_AdrsProf = findViewById(R.id.layout_AdrsProf);
            layout_IdProf = findViewById(R.id.layout_IdProf);
            layout_Cancelled_Cheque_Prof = findViewById(R.id.layout_Cancelled_Cheque_Prof);

//          layout_photoProof = (LinearLayout) findViewById(R.id.layout_photoProof);

            imgView_AdrsProf = findViewById(R.id.imgView_AdrsProf);
            imgView_IdProf = findViewById(R.id.imgView_IdProf);
            imgView_pan_card_Prof = findViewById(R.id.imgView_pan_card_Prof);
            imgView_Cancelled_ChequeProf = findViewById(R.id.imgView_Cancelled_ChequeProf);

//          imgView_photoProof = (ImageView) findViewById(R.id.imgView_photoProof);

            // ll_tab_pan_card ,ll_tab_address_proof ,ll_tab_id_proof
            layout_AdrsProf.setVisibility(View.VISIBLE);
            layout_IdProf.setVisibility(View.GONE);
            layout_Cancelled_Cheque_Prof.setVisibility(View.GONE);
            layout_pannoProf.setVisibility(View.GONE);

            ll_tab_address_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_orange));
            ll_tab_id_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));
            ll_tab_Cancelled_Cheque_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));
            ll_tab_pan_card.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));

            txt_tab_address_proof.setTextColor(Color.WHITE);
            txt_tab_id_proof.setTextColor(Color.BLACK);
            txt_tab_Cancelled_Cheque_proof.setTextColor(Color.BLACK);
            txt_tab_pan_no.setTextColor(Color.BLACK);

            ll_tab_pan_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_pannoProf.setVisibility(View.VISIBLE);
                    layout_AdrsProf.setVisibility(View.GONE);
                    layout_IdProf.setVisibility(View.GONE);
                    layout_Cancelled_Cheque_Prof.setVisibility(View.GONE);

                    ll_tab_pan_card.setBackground(getResources().getDrawable(R.drawable.bg_round_button_orange));
                    ll_tab_address_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));
                    ll_tab_id_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));
                    ll_tab_Cancelled_Cheque_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));

                    txt_tab_pan_no.setTextColor(Color.WHITE);
                    txt_tab_address_proof.setTextColor(Color.BLACK);
                    txt_tab_id_proof.setTextColor(Color.BLACK);
                    txt_tab_Cancelled_Cheque_proof.setTextColor(Color.BLACK);

                }
            });
            ll_tab_address_proof.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_pannoProf.setVisibility(View.GONE);
                    layout_AdrsProf.setVisibility(View.VISIBLE);
                    layout_IdProf.setVisibility(View.GONE);
                    layout_Cancelled_Cheque_Prof.setVisibility(View.GONE);

                    ll_tab_pan_card.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));
                    ll_tab_address_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_orange));
                    ll_tab_id_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));
                    ll_tab_Cancelled_Cheque_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));

                    txt_tab_pan_no.setTextColor(Color.BLACK);
                    txt_tab_address_proof.setTextColor(Color.WHITE);
                    txt_tab_id_proof.setTextColor(Color.BLACK);
                    txt_tab_Cancelled_Cheque_proof.setTextColor(Color.BLACK);

                }
            });
            ll_tab_id_proof.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_pannoProf.setVisibility(View.GONE);
                    layout_AdrsProf.setVisibility(View.GONE);
                    layout_IdProf.setVisibility(View.VISIBLE);
                    layout_Cancelled_Cheque_Prof.setVisibility(View.GONE);

                    ll_tab_pan_card.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));
                    ll_tab_address_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));
                    ll_tab_id_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_orange));
                    ll_tab_Cancelled_Cheque_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));

                    txt_tab_pan_no.setTextColor(Color.BLACK);
                    txt_tab_address_proof.setTextColor(Color.BLACK);
                    txt_tab_id_proof.setTextColor(Color.WHITE);
                    txt_tab_Cancelled_Cheque_proof.setTextColor(Color.BLACK);

                }
            });
            ll_tab_Cancelled_Cheque_proof.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_pannoProf.setVisibility(View.GONE);
                    layout_AdrsProf.setVisibility(View.GONE);
                    layout_IdProf.setVisibility(View.GONE);
                    layout_Cancelled_Cheque_Prof.setVisibility(View.VISIBLE);

                    ll_tab_pan_card.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));
                    ll_tab_address_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));
                    ll_tab_id_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_eee));
                    ll_tab_Cancelled_Cheque_proof.setBackground(getResources().getDrawable(R.drawable.bg_round_button_orange));

                    txt_tab_pan_no.setTextColor(Color.BLACK);
                    txt_tab_address_proof.setTextColor(Color.BLACK);
                    txt_tab_id_proof.setTextColor(Color.BLACK);
                    txt_tab_Cancelled_Cheque_proof.setTextColor(Color.WHITE);

                }
            });

            mBottomSheetDialog = new BottomSheetDialog(act);
            View sheetView = this.getLayoutInflater().inflate(R.layout.bottom_sheet, null);
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.setTitle("Complete action using...");

            heading = getIntent().getStringExtra("HEADING");
            txt_heading = findViewById(R.id.txt_heading);

            if (heading.equalsIgnoreCase("View")) {
                txt_heading.setText("View KYC Documents");
                findViewById(R.id.textView).setVisibility(View.GONE);
                findViewById(R.id.textView5).setVisibility(View.GONE);
            } else
                txt_heading.setText("Upload KYC Documents");

            if (heading.equalsIgnoreCase("Update")) {
                layout_AdrsProf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isAddressProof) {
                            AppUtils.alertDialog(act, "Sorry, You can upload only once.");
                        } else {
                            whichUpload = "AP";
                            selectImage();
                        }
                    }
                });

                layout_IdProf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isIdProof) {
                            AppUtils.alertDialog(act, "Sorry, You can upload only once.");
                        } else {
                            whichUpload = "IP";
                            selectImage();                        }
                    }
                });
                layout_pannoProf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isPancardProof) {
                            AppUtils.alertDialog(act, "Sorry, You can upload only once.");
                        } else {
                            whichUpload = "PC";
                            selectImage();                        }
                    }
                });
                layout_Cancelled_Cheque_Prof.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isCancelled_ChequeProf) {
                            AppUtils.alertDialog(act, "Sorry, You can upload only once.");
                        } else {
                            whichUpload = "CC";
                            selectImage();                        }
                    }
                });
            }

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

            if (AppUtils.isNetworkAvailable(act)) {
                continueapp();
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }


    public void continueapp() {
        executeGetImageLoadRequest();
    }

    private void executeGetImageLoadRequest() {
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
                            //ImageType----AddrProof=AP,IdentityProof=IP,PhotoProof=PP,Signature=S,CancelChq=CC,SpousePic=SP,All=*
                            postParameters.add(new BasicNameValuePair("ImageType", "*"));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodGetImages, TAG);
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
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (!jsonArrayData.getJSONObject(0).getString("AddrProof").equals("")) {
                                    isAddressProof = true;
                                    imgView_AdrsProf.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("AddrProof")));
                                }
                                if (!jsonArrayData.getJSONObject(0).getString("IdentityProof").equals("")) {
                                    isIdProof = true;
                                    imgView_IdProf.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("IdentityProof")));
                                }
                                if (!jsonArrayData.getJSONObject(0).getString("PanProof").equals("")) {
                                    isPancardProof = true;
                                    imgView_pan_card_Prof.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PanProof")));
                                }
                                if (!jsonArrayData.getJSONObject(0).getString("CancelChq").equals("")) {
                                    isCancelled_ChequeProf = true;
                                    imgView_Cancelled_ChequeProf.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("CancelChq")));
                                }
                                /*if (!jsonArrayData.getJSONObject(0).getString("PhotoProof").equals("")) {
                                    profileImage.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof")));

                                }*/
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

    private void showUploadImageDailog(final Bitmap imageBitmap) {
        try {
            final Dialog dialog = new Dialog(act, R.style.ThemeDialogCustom);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_dialog_img_upload);

            TextView dialog4all_txt = dialog.findViewById(R.id.txt_DialogTitle);
            if (whichUpload.equals("AP")) {
                dialog4all_txt.setText("Are you sure you want to upload this image as Address Proof?");
            } else if (whichUpload.equals("IP")) {
                dialog4all_txt.setText("Are you sure you want to upload this image as Identity Proof?");
            } else if (whichUpload.equals("PC")) {
                dialog4all_txt.setText("Are you sure you want to upload this image as Pan Card ?");
            }else if (whichUpload.equals("CC")) {
                dialog4all_txt.setText("Are you sure you want to upload this image as Cancelled Cheque ?");
            }


            final ImageView imgView_Upload = dialog.findViewById(R.id.imgView_Upload);
            imgView_Upload.setImageBitmap(imageBitmap);

            TextView txt_submit = dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Upload");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(act, v);
                    dialog.dismiss();
                    executePostImageUploadRequest(imageBitmap);

                }
            });

            TextView txt_cancel = dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void executePostImageUploadRequest(final Bitmap bitmap) {
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
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Type", whichUpload));
                            postParameters.add(new BasicNameValuePair("ImageByteCode", AppUtils.getBase64StringFromBitmap(bitmap)));

                            try {
                                postParameters.add(new BasicNameValuePair("IPAddress", AppUtils.getDeviceID(act)));
                            } catch (Exception ignored) {
                            }

                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodUploadImages, TAG);
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
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                if (whichUpload.equals("AP")) {
                                    if (!jsonArrayData.getJSONObject(0).getString("AddrProof").equals("")) {
                                        isAddressProof = true;
                                        imgView_AdrsProf.setImageResource(android.R.color.transparent);
                                        imgView_AdrsProf.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("AddrProof")));
                                    }
                                } else if (whichUpload.equals("IP")) {
                                    if (!jsonArrayData.getJSONObject(0).getString("IdentityProof").equals("")) {
                                        isIdProof = true;
                                        imgView_IdProf.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("IdentityProof")));
                                    }
                                } else if (whichUpload.equals("PC")) {
                                    if (!jsonArrayData.getJSONObject(0).getString("PanProof").equals("")) {
                                        isPancardProof = true;
                                        imgView_pan_card_Prof.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PanProof")));
                                    }
                                }else if (whichUpload.equals("CC")) {
                                    if (!jsonArrayData.getJSONObject(0).getString("CancelChq").equals("")) {
                                        isCancelled_ChequeProf = true;
                                        imgView_Cancelled_ChequeProf.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("CancelChq")));
                                    }
                                }
//                                else if(whichUpload.equals("PP"))
//                                {
//                                    if(!jsonArrayData.getJSONObject(0).getString("PhotoProof").toString().equals(""))
//                                    {
//                                        isPhotoProof=true;
//                                        imgView_photoProof.setImageBitmap(AppUtils.getBitmapFromString(jsonArrayData.getJSONObject(0).getString("PhotoProof").toString()));
//                                    }
//                                }
                            } else {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
            ////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }

        System.gc();
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
        showUploadImageDailog(bitmap);
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
        showUploadImageDailog(bitmap);
        /*executePostImageUploadRequest(bm);
        profileImage.setImageBitmap(bm);
        iv_Profile_Pic_dash.setImageBitmap(bm);
        String imagepath = bm.toString();*/
        Log.e("from gallery data", selectedImagePath);
    }
}