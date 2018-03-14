package com.around.technician;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PaytmQrActivity extends AppCompatActivity {
    String bookingID, remarks;
    String amountDue, amountPaid;
    String formData, qrUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytm_qr);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Pay Through Paytm");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();

        bookingID  = intent.getStringExtra("bookingID");
        formData   = intent.getStringExtra("formData");
        amountDue  = intent.getStringExtra("amountDue");
        amountPaid = intent.getStringExtra("amountPaid");
        remarks    = intent.getStringExtra("remarks");
        qrUrl      = intent.getStringExtra("qrUrl");

        ImageView qrUrlImage = findViewById(R.id.qrImage);
        final ProgressBar progressBar = findViewById(R.id.progressBar1);

        Picasso.with(this).load(qrUrl)
                .fit().centerInside()
                .into(qrUrlImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });

    }

    public void submitProcess(View view){
        Intent intent = new Intent(PaytmQrActivity.this, DigitalSignatureActivity.class);
        intent.putExtra("formData", formData);
        intent.putExtra("amountDue", amountDue);
        intent.putExtra("bookingID", bookingID);
        intent.putExtra("amountPaid", amountPaid);
        intent.putExtra("remarks", remarks);
        intent.putExtra("paymentMethod", getResources().getString(R.string.payment_method_paytm));
        startActivity(intent);
    }

}
