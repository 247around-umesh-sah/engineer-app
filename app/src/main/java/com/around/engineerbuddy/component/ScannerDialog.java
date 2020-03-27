package com.around.engineerbuddy.component;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.util.BMAConstants;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class ScannerDialog extends Dialog implements ZBarScannerView.ResultHandler {
    Button cancelButton;
    private ZBarScannerView mScannerView;
    public ScannerDialog(@NonNull Context context) {
        super(context);
    }
    protected int layoutID() {
        return R.layout.scaaner_view;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getAttributes().windowAnimations = R.style.ScaleFromCenter;
        this.setContentView(this.layoutID());
        this.setCanceledOnTouchOutside(false);
        Resources resources = getContext().getResources();
        int scrWidth = resources.getConfiguration().screenWidthDp;
        if ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrWidth, MainActivityHelper.application().getResources().getDisplayMetrics()) <= 700) {
            this.getWindow().setLayout((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrWidth, MainActivityHelper.application().getResources().getDisplayMetrics()), LinearLayout.LayoutParams.WRAP_CONTENT);
        Log.d("aaaaa","if call");
        } else {
            Log.d("aaaaa","ELSE call");
            this.getWindow().setLayout((4 * (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrWidth+50, MainActivityHelper.application().getResources().getDisplayMetrics())) / 4, LinearLayout.LayoutParams.WRAP_CONTENT/2);
        }
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mScannerView=findViewById(R.id.scaneview);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        Log.v("kkkk", result.getContents()); // Prints scan results
        Log.v("uuuu", result.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)

        Intent intent=new Intent();
        intent.putExtra("content",result.getContents());
        intent.putExtra("scan","scandata");
        this.dismiss();
        onConfirmdata(result.getContents());

//        getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.scanCode, intent);
//        getFragmentManager().popBackStack();
    }
    public void onConfirmdata(String scanData){

    }
}
