package com.around.engineerbuddy.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.around.engineerbuddy.BMAmplitude;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.util.BMAConstants;
import com.around.engineerbuddy.util.BMAUIUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomerInput extends BMAFragment implements View.OnClickListener {

    LinearLayout parentLayout;
    CardView confirmationSignatureCardview;
    CardView signatureLayoutCardView;
    LinearLayout okLayout, confirmationSignature, signatureLayout;
    LinearLayout clearIconLayout;
    EOBooking eoBooking;
    File file, serialFile;
    // LinearLayout mContent;
    View drawView;
    signature mSignature;
    Bitmap bitmap;
    FileOutputStream mFileOutStream;
    String DIRECTORY = Environment.getExternalStorageDirectory() + "/AroundDigitSign/";
    String SN_DIRECTORY = Environment.getExternalStorageDirectory() + "/AroundSerialNO/";
    String StoredPath = "";
    Button doneButton;
    Bitmap customerSignatureBitmap;
    ImageView image;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.eoBooking = getArguments().getParcelable("eoBooking");
        this.customerSignatureBitmap = getArguments().getParcelable("customerSignatureBitmap");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.customer_input, container, false);
        image = new ImageView(getContext());
        BMAmplitude.saveUserAction("CustomerInput", "CustomerInput");
        parentLayout = this.view.findViewById(R.id.customerDetailLayout);
        confirmationSignature = this.view.findViewById(R.id.confirmationSignatureLayout);
        this.view.findViewById(R.id.cancelButton).setVisibility(View.GONE);
        this.doneButton = this.view.findViewById(R.id.submitButton);
        this.doneButton.setText("Done");

        //customerDetailLayout=this.view.findViewById(R.id.customerDetailLayout);

        confirmationSignatureCardview = this.view.findViewById(R.id.confirmationSignatureCardView);
        signatureLayoutCardView = this.view.findViewById(R.id.signatureLayoutCardView);
        signatureLayout = this.view.findViewById(R.id.signatureLayout);
        okLayout = this.view.findViewById(R.id.okLayout);
        clearIconLayout = this.view.findViewById(R.id.clearIconLayout);
        View childView = inflater.inflate(R.layout.booking_detail_fragment, null, false);
        childView.findViewById(R.id.bookingDetai_chargellayout).setVisibility(View.GONE);
        childView.findViewById(R.id.tileRowLayout).setVisibility(View.GONE);
        childView.findViewById(R.id.dateCalenderLayout).setVisibility(View.GONE);
        childView.findViewById(R.id.bookingdetial_ServiceLayout).setVisibility(View.GONE);
        childView.findViewById(R.id.bookingDetaillayout).setVisibility(View.GONE);
        childView.findViewById(R.id.dateLayout).setVisibility(View.GONE);
        TextView name = childView.findViewById(R.id.name);
        TextView address = childView.findViewById(R.id.address);
        TextView brandName = childView.findViewById(R.id.brandName);
        TextView capacityOfApliance = childView.findViewById(R.id.capacityOfApliance);
        TextView applianceCategory = childView.findViewById(R.id.applianceCatg);
        TextView applianceName = childView.findViewById(R.id.applianceName);
        TextView bookingDetail = childView.findViewById(R.id.bookingDetail);
        TextView customerDetail = childView.findViewById(R.id.customerDetail);
        bookingDetail.setBackgroundColor(BMAUIUtil.getColor(R.color.missedBookingcolor));
        bookingDetail.setTextColor(BMAUIUtil.getColor(R.color.white));
        customerDetail.setBackgroundColor(BMAUIUtil.getColor(R.color.missedBookingcolor));
        customerDetail.setTextColor(BMAUIUtil.getColor(R.color.white));
        // customerDetail.setText("Customer Signature");
        customerDetail.setVisibility(View.GONE);
        name.setText(eoBooking.name);
        address.setText(eoBooking.bookingAddress);
        brandName.setText(eoBooking.applianceBrand);
        applianceName.setText(eoBooking.services);
        capacityOfApliance.setText(eoBooking.applianceCapacity);
        applianceCategory.setText(eoBooking.applianceCategory);
        int dialogRadius = 30;
        BMAUIUtil.setBackgroundRound(clearIconLayout, BMAUIUtil.getColor(R.color.missedBookingcolor), new float[]{0, 0, 0, 0, dialogRadius, dialogRadius, 0, 0});
        BMAUIUtil.setBackgroundRound(okLayout, BMAUIUtil.getColor(R.color.missedBookingcolor), new float[]{0, 0, dialogRadius, dialogRadius, 0, 0, 0, 0});

        parentLayout.addView(childView);
        signatureLayoutCardView.setOnClickListener(this);
        okLayout.setOnClickListener(this);
        clearIconLayout.setOnClickListener(this);
        this.initSignature();
        this.doneButton.setEnabled(false);
        this.doneButton.setAlpha(.5f);
        this.doneButton.setOnClickListener(this);
        if (this.customerSignatureBitmap != null) {
            //ImageView image = new ImageView(getContext());
            image.setImageBitmap(customerSignatureBitmap);
            signatureLayout.addView(image);
            this.doneButton.setVisibility(View.VISIBLE);
            doneButton.setEnabled(true);
            this.doneButton.setAlpha(1f);
        }
        return this.view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signatureLayoutCardView:
                this.doneButton.setVisibility(View.GONE);
                signatureLayoutCardView.setVisibility(View.GONE);
                parentLayout.setVisibility(View.GONE);
                confirmationSignatureCardview.setVisibility(View.VISIBLE);
                break;
            case R.id.okLayout:

                drawView.setDrawingCacheEnabled(true);
                String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                StoredPath = DIRECTORY + eoBooking.bookingID + "_" + pic_name + ".png";

                mSignature.save(drawView, StoredPath);
                Toast.makeText(v.getContext(), R.string.signature_saved, Toast.LENGTH_SHORT).show();
                confirmationSignatureCardview.setVisibility(View.GONE);
                signatureLayoutCardView.setVisibility(View.VISIBLE);
                parentLayout.setVisibility(View.VISIBLE);


                image.setImageBitmap(bitmap);
                signatureLayout.removeAllViews();
                signatureLayout.addView(image);
                this.doneButton.setVisibility(View.VISIBLE);
                doneButton.setEnabled(true);
                this.doneButton.setAlpha(1f);
                break;
            case R.id.clearIconLayout:
                mSignature.clear();
                okLayout.setEnabled(false);
                okLayout.setAlpha(.5f);

                break;
            case R.id.submitButton:
                Intent intent = new Intent();
                if (bitmap == null) {
                    if (customerSignatureBitmap != null) {
                        bitmap = customerSignatureBitmap;
                    }
                }
                intent.putExtra("customerSignature", bitmap);
                intent.putExtra("completeCatogryPageName", "customerInput");
                getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, intent);
                getFragmentManager().popBackStack();
                break;


        }
    }

    private void initSignature() {
        file = new File(DIRECTORY);
        if (!file.exists()) {
            file.mkdir();
        }

        serialFile = new File(SN_DIRECTORY);
        if (!serialFile.exists()) {
            serialFile.mkdir();
        }

        mSignature = new signature(getActivity(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        confirmationSignature.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        okLayout.setEnabled(false);
        okLayout.setAlpha(.5f);

        drawView = confirmationSignature;


    }


    public class signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private final RectF dirtyRect = new RectF();
        private Paint paint = new Paint();
        private Path path = new Path();
        private float lastTouchX;
        private float lastTouchY;

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v, String StoredPath) {
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(confirmationSignature.getWidth(), confirmationSignature.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);
                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);

                mFileOutStream.flush();
                mFileOutStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            okLayout.setEnabled(true);
            okLayout.setAlpha(1);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;
                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {
            Log.v("log_tag", string);
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}
