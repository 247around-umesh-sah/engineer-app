package com.around.engineerbuddy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.around.engineerbuddy.R;
import com.around.engineerbuddy.util.BMAConstants;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class Scannerfragment extends BMAFragment  implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.scaaner_view, container, false);
        mScannerView=this.view.findViewById(R.id.scaneview);
        return this.view;
    }
    @Override
    public void onResume() {
        super.onResume();
       mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result result) {
        Log.v("kkkk", result.getContents()); // Prints scan results
        Log.v("uuuu", result.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)

        Intent intent=new Intent();
        intent.putExtra("content",result.getContents());
        intent.putExtra("scan","scandata");
        getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.scanCode, intent);
        getFragmentManager().popBackStack();
    }
}
