package com.vpipl.citybazaar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.test.pg.secure.pgsdkv4.PGConstants;
import com.test.pg.secure.pgsdkv4.PaymentGatewayPaymentInitializer;
import com.test.pg.secure.pgsdkv4.PaymentParams;
import com.vpipl.citybazaar.Utils.AppUtils;
import com.vpipl.citybazaar.Utils.SampleAppConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by Mukesh on 29/10/2020.
 */
public class TP_Dummy_Payment_Activity extends Activity {

    private String TAG = "TP_Dummy_Payment_Activity";
    Activity act = TP_Dummy_Payment_Activity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_card);

        try {
            Random rnd = new Random();
            int n = 100000 + rnd.nextInt(900000);
            OnlinePaymentRedirection("" + n);

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void OnlinePaymentRedirection(String orderno) {

        try {
            PaymentParams pgPaymentParams = new PaymentParams();
            pgPaymentParams.setAPiKey(SampleAppConstants.PG_API_KEY);
            //pgPaymentParams.setAmount(calculateSelectedProductNetPayable().trim().replace(",", ""));
            pgPaymentParams.setAmount(String.valueOf("100").trim().replace(",", ""));
            pgPaymentParams.setEmail(SampleAppConstants.PG_EMAIL);
            pgPaymentParams.setName(SampleAppConstants.PG_NAME);
            pgPaymentParams.setPhone(SampleAppConstants.PG_PHONE);
            pgPaymentParams.setOrderId(orderno);
            pgPaymentParams.setCurrency(SampleAppConstants.PG_CURRENCY);
            pgPaymentParams.setDescription("test by app");
            pgPaymentParams.setCity(SampleAppConstants.PG_CITY);
            pgPaymentParams.setState(SampleAppConstants.PG_STATE);
            pgPaymentParams.setAddressLine1(SampleAppConstants.PG_ADD_1);
            pgPaymentParams.setAddressLine2(SampleAppConstants.PG_ADD_2);
            pgPaymentParams.setZipCode(SampleAppConstants.PG_ZIPCODE);
            pgPaymentParams.setCountry(SampleAppConstants.PG_COUNTRY);
            pgPaymentParams.setReturnUrl(SampleAppConstants.PG_RETURN_URL);
            pgPaymentParams.setMode(SampleAppConstants.PG_MODE);
            pgPaymentParams.setUdf1(SampleAppConstants.PG_UDF1);
            pgPaymentParams.setUdf2(SampleAppConstants.PG_UDF2);
            pgPaymentParams.setUdf3(SampleAppConstants.PG_UDF3);
            pgPaymentParams.setUdf4(SampleAppConstants.PG_UDF4);
            pgPaymentParams.setUdf5(SampleAppConstants.PG_UDF5);

            PaymentGatewayPaymentInitializer pgPaymentInitialzer = new PaymentGatewayPaymentInitializer(pgPaymentParams, act);
            pgPaymentInitialzer.initiatePaymentProcess();

        } catch (Exception e) {
            Log.e("gateway failed : ", e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            try {
                String paymentResponse = data.getStringExtra(PGConstants.PAYMENT_RESPONSE);
                System.out.println("paymentResponse: " + paymentResponse);
                if (paymentResponse.equals("null")) {
                    System.out.println("Transaction Error!");

                    AppUtils.alertDialogWithFinishDashboard(act, "Transaction Error!");

                } else {
                    JSONObject response = new JSONObject(paymentResponse);
                    // startAfterPaymentRequest(response);
                    AppUtils.alertDialog(act, response.toString());
                    Log.e("PG_Responce", response.toString());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("MainActivity", "Failed to payment gateway");
        }
    }

}
