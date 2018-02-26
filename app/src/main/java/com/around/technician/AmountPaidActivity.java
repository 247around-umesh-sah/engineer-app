package com.around.technician;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AmountPaidActivity extends AppCompatActivity {

    // GPSTracker class

    ConnectionDetector cd;
    Misc misc;
    String bookingID;
    String amountDue;
    String formData;
    EditText amountPaidInput, amountDueInput;
    EditText remarks;
    String amountPaid ="", enRemarks = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_paid);
        FirebaseCrash.log("Activity created");

        cd = new ConnectionDetector(this);
        misc = new Misc(this);
        misc.checkAndLocationRequestPermissions();

        Intent intent = getIntent();

        bookingID = intent.getStringExtra("bookingID");
        formData = intent.getStringExtra("formData");
        amountDue = intent.getStringExtra("amountDue");
        amountPaidInput = findViewById(R.id.amountPaid);
        amountDueInput = findViewById(R.id.amountDue);
        remarks = findViewById(R.id.remarks);

        amountDueInput.setText(amountDue);
        amountDueInput.setFocusable(false);

        RadioGroup paymentGroup = findViewById(R.id.paymentGroup);
        RadioButton paymentcash = findViewById(R.id.cash);

        paymentGroup.check(paymentcash.getId());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        RadioButton cashButton = findViewById(R.id.cash);
        RadioButton paytmButton = findViewById(R.id.paytm);
        cashButton.setEnabled(false);
        paytmButton.setEnabled(false);

    }

    public void submitProcess(View view) {
        boolean validation = true;
        amountPaid = amountPaidInput.getText().toString();
        enRemarks = remarks.getText().toString();
        if (Integer.parseInt(amountDue) > 0) {

            if (amountPaid.isEmpty()) {
                Snackbar.make(view, R.string.customerPaidAmountRequired, Snackbar.LENGTH_LONG).show();
            } else if (Integer.parseInt(amountPaid) == 0) {
                validation = false;
                Snackbar.make(view, R.string.customerPaidAmountRequired, Snackbar.LENGTH_LONG).show();

            }
        }

        if(enRemarks.isEmpty()){
            validation = false;
            Snackbar.make(view, R.string.remarksRequired, Snackbar.LENGTH_LONG).show();
        }

        if(validation){
            Intent intent = new Intent(AmountPaidActivity.this, DigitalSignatureActivity.class);
            intent.putExtra("formData", formData);
            intent.putExtra("amountDue", amountDue);
            intent.putExtra("bookingID", bookingID);
            intent.putExtra("amountPaid", amountPaid);
            intent.putExtra("remarks", enRemarks);
            startActivity(intent);
        }

    }
}
