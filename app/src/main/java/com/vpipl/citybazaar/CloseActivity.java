package com.vpipl.citybazaar;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Mukesh on 27/12/2019.
 */
public class CloseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        finish();
        overridePendingTransition(0, 0);

    }
}
