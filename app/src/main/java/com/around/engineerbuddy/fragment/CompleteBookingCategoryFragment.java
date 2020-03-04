package com.around.engineerbuddy.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.BMAmplitude;
import com.around.engineerbuddy.ConnectionDetector;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.Misc;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.component.BMATileView;
import com.around.engineerbuddy.database.DataBaseClient;
import com.around.engineerbuddy.database.DataBaseHelper;
import com.around.engineerbuddy.database.EOEngineerBookingInfo;
import com.around.engineerbuddy.database.EngineerBookingDao;
import com.around.engineerbuddy.database.SaveEngineerBookingAction;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.entity.EOCompleteProductQuantity;
import com.around.engineerbuddy.entity.EOCompleteProductdetail;
import com.around.engineerbuddy.entity.EOModelNumber;
import com.around.engineerbuddy.entity.EOSpareCosumptionRequest;
import com.around.engineerbuddy.entity.EOSpareParts;
import com.around.engineerbuddy.entity.EOSymptomDefect;
import com.around.engineerbuddy.util.BMAConstants;
import com.around.engineerbuddy.util.BMAUIUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CompleteBookingCategoryFragment extends BMAFragment implements View.OnClickListener {
    BMATileView tile1, tile2, tile3, tile4, tile5, tile6;
    HashMap<String, Object> requestData = new HashMap<>();
    EOBooking eoBooking;
    Misc misc;
    Button submitButton;
    int i=0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.eoBooking = getArguments().getParcelable("eoBooking");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.cancel_booking_type, container, false);
        BMAmplitude.saveUserAction("CompleteBookingCategoryFragment", "CompleteBookingCategoryFragment");
        this.view.findViewById(R.id.tileLayout).setVisibility(View.VISIBLE);
        this.view.findViewById(R.id.footerLayout).setVisibility(View.VISIBLE);
        this.view.findViewById(R.id.cancelButton).setVisibility(View.GONE);
        this.tile1 = this.view.findViewById(R.id.tile1);
        this.tile2 = this.view.findViewById(R.id.tile2);
        this.tile3 = this.view.findViewById(R.id.tile3);
        this.tile4 = this.view.findViewById(R.id.tile4);
        this.tile5 = this.view.findViewById(R.id.tile5);
        this.tile6 = this.view.findViewById(R.id.tile6);
        submitButton = this.view.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRequest();
            }
        });
        this.bindTile();
        setTileBackgroundView();
        misc = new Misc(getContext());
        if(i==0) {
            openProductDetailPage();
            i++;
        }
        return this.view;
    }

    public void bindTile() {
        this.setTileValue(this.tile1, R.string.product, R.drawable.product, "Product", true);
        this.setTileValue(this.tile2, R.string.consumption, R.drawable.spare_consumption, "Consumption", true);
        this.setTileValue(this.tile3, R.string.symptoms, R.drawable.complete_symptom_icon, "symptom", true);
        this.setTileValue(this.tile4, R.string.payment, R.drawable.payment, "Payment", true);
        this.setTileValue(this.tile5, R.string.customer, R.drawable.customer, "Customer", true);
        // this.setTileValue(this.tile5, R.string.artificial, R.drawable.artificial, "Artificial", true);
        this.setTileValue(this.tile6, R.string.accessories, R.drawable.accessories, "accesories", true);
        //this.tile5.setAlpha(.5f);
        this.tile6.setAlpha(.5f);
        this.tile1.setText(getString(R.string.details));
        this.tile2.setText(getString(R.string.reason));
        this.tile3.setText(getString(R.string.remarks));
        this.tile4.setText(getString(R.string.details));
        this.tile5.setText(getString(R.string.input));
        this.tile6.setText(getString(R.string.store));

    }

    private void setTileValue(BMATileView fnTileView, @StringRes int title, @DrawableRes int imgRes, String tag, boolean isVisible) {
        fnTileView.setTitle(title);
        fnTileView.setImageResource(imgRes);
        fnTileView.setTag(tag);
        fnTileView.setVisibility(isVisible);
        fnTileView.setBoaderViewVisible();
        fnTileView.setBoaderViewColor(BMAUIUtil.getColor(R.color.dark_gray));
        //fnTileView.setTitleColor(BMAUIUtil.getColor(R.color.tile_title_color));
        // fnTileView.setTitleBgColor(R.color.appTheamColor);
        fnTileView.setOnClickListener(this);
    }

    private void setTileBackgroundView() {
        if (eoSelectedSymptomDefect != null) {
            this.tile3.setBoaderViewColor(BMAUIUtil.getColor(R.color.green_tile));

        }
        if ((selectedProductDetail != null && !isConsumptionrequired) || (selectedCosumption != null && selectedCosumption.size() > 0)) {
            this.tile2.setBoaderViewColor(BMAUIUtil.getColor(R.color.green_tile));
        }

        if (customerSignatureBitmap != null) {
            this.tile5.setBoaderViewColor(BMAUIUtil.getColor(R.color.green_tile));
        }
        if (selectedProductDetail != null) {
            this.tile1.setBoaderViewColor(BMAUIUtil.getColor(R.color.green_tile));

        }
        if (paymentsAmount != null) {
            this.tile4.setBoaderViewColor(BMAUIUtil.getColor(R.color.green_tile));
        }




        if(callbackPageName.equalsIgnoreCase("productDetail")){
            if (isConsumptionrequired) {
                    if (selectedCosumption == null || selectedCosumption.size() == 0) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("eoBooking", eoBooking);
                        this.updateFragment(bundle, new SparePartConsumptionFragment(), "Spare Part");
                        callbackPageName="";
                        return;
                    }
                } else {

                    if (this.eoSelectedSymptomDefect == null) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("eoBooking", eoBooking);
                        this.updateFragment(bundle, new SymptomFragment(), getString(R.string.symptoms));
                        callbackPageName="";
                        return;
                    }
                }

        }
        if(callbackPageName.equalsIgnoreCase("spareConsumption")){
            if(selectedCosumption==null){
                return;
            }
            if (this.eoSelectedSymptomDefect == null) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("eoBooking", eoBooking);
                    this.updateFragment(bundle, new SymptomFragment(),getString(R.string.symptoms));
                callbackPageName="";
                return;
                }

        }
        if(callbackPageName.equalsIgnoreCase("symptom")){
            if(eoSelectedSymptomDefect==null){
                return;
            }
            if(paymentsAmount==null) {
                Bundle bundle=new Bundle();
                bundle.putParcelable("eoBooking", eoBooking);
                if (selectedProductDetail != null) {
                    bundle.putParcelable("productDetail", selectedProductDetail);
                }
                this.updateFragment(bundle, new CheckoutFragment(), "Payment Details");
                callbackPageName="";
                return;
            }

        }
        if(callbackPageName.equalsIgnoreCase("paymentDetail")){
            if(paymentsAmount==null){
                return;
            }
            if(customerSignatureBitmap==null){
                Bundle bundle=new Bundle();
                bundle.putParcelable("eoBooking", eoBooking);

                this.updateFragment(bundle, new CustomerInput(), "Customer Input");
                callbackPageName="";
                return;
            }
        }
//        if(eoSelectedSymptomDefect!=null){
//            this.tile2.setBoaderViewColor(BMAUIUtil.getColor(R.color.green_tile));
//        }
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.tile1:
                openProductDetailPage();
//                bundle.putParcelable("eoBooking", eoBooking);
//                if (addMoreProductDetail != null)
//                    bundle.putString("addMoreProductDetail", addMoreProductDetail);
//                if (selectedProductDetail != null) {
//                    bundle.putParcelable("productDetail", selectedProductDetail);
//                }
//                bundle.putBoolean("isCompletePage", true);
//                if (selectpurchadeDate != null) {
//                    bundle.putString("pod", selectpurchadeDate);
//                }
//                if (modelNumber != null) {
//                    bundle.putParcelable("modelNumber", modelNumber);
//                }
//
//                this.updateFragment(bundle, new EditWarrantyBooking(), "Warranty Checker");
                break;
            case R.id.tile3:

                if (selectedProductDetail == null) {
                    Toast.makeText(getContext(), getString(R.string.fillProductDetailvalidation), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isConsumptionrequired && (selectedCosumption == null || selectedCosumption.size() == 0)) {
                    Toast.makeText(getContext(), getString(R.string.fillConsumptionvalidation), Toast.LENGTH_SHORT).show();
                    return;
                }
                bundle.putParcelable("eoBooking", eoBooking);
                if (this.eoSelectedSymptomDefect != null) {
                    bundle.putParcelable("eoSelectedSymptomDefect", this.eoSelectedSymptomDefect);
                }
                this.updateFragment(bundle, new SymptomFragment(), getString(R.string.symptoms));
                break;
            case R.id.tile4:
                if (eoSelectedSymptomDefect == null) {
                    Toast.makeText(getContext(), "Please complete first Symptom Defect Tile", Toast.LENGTH_SHORT).show();
                    return;
                }
                bundle.putParcelable("eoBooking", eoBooking);
                if (addMoreProductDetail != null)
                    bundle.putString("addMoreProductDetail", addMoreProductDetail);
                if (selectedProductDetail != null) {
                    bundle.putParcelable("productDetail", selectedProductDetail);
                }
                this.updateFragment(bundle, new CheckoutFragment(), "Payment Details");
                break;
            case R.id.tile5:
                if (paymentsAmount == null) {
                    Toast.makeText(getContext(), "Please complete first Payment detail Tile", Toast.LENGTH_SHORT).show();
                    return;
                }
                bundle.putParcelable("eoBooking", eoBooking);
                if (customerSignatureBitmap != null)
                    bundle.putParcelable("customerSignatureBitmap", customerSignatureBitmap);
                this.updateFragment(bundle, new CustomerInput(), "Customer Input");
                break;
            case R.id.tile6:
                // this.updateFragment(bundle, new CompleteBookingCategoryFragment(), "Complete Booking");
                break;
            case R.id.tile2:
                if (selectedProductDetail == null) {
                    Toast.makeText(getContext(), getString(R.string.fillProductDetailvalidation), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isConsumptionrequired) {
                    Toast.makeText(getContext(), "Consumption not required for this booking", Toast.LENGTH_SHORT).show();
                    return;
                }
                bundle.putParcelable("eoBooking", eoBooking);
                if (selectedCosumption != null && selectedCosumption.size() > 0) {
                    bundle.putParcelableArrayList("selectedCosumptionList", selectedCosumption);
                }
                this.updateFragment(bundle, new SparePartConsumptionFragment(), "Spare Part");
                break;
        }
    }

    private void openProductDetailPage(){
        Bundle bundle = new Bundle();
        bundle.putParcelable("eoBooking", eoBooking);
        if (addMoreProductDetail != null)
            bundle.putString("addMoreProductDetail", addMoreProductDetail);
        if (selectedProductDetail != null) {
            bundle.putParcelable("productDetail", selectedProductDetail);
        }
        bundle.putBoolean("isCompletePage", true);
        if (selectpurchadeDate != null) {
            bundle.putString("pod", selectpurchadeDate);
        }
        if (modelNumber != null) {
            bundle.putParcelable("modelNumber", modelNumber);
        }

        this.updateFragment(bundle, new EditWarrantyBooking(), "Warranty Checker");
    }
    public void updateFragment(Bundle bundle, Fragment fragment, String headerText) {
        this.updateFragment(bundle, fragment, headerText, null);
    }

    public void updateFragment(Bundle bundle, Fragment fragment, String headerText, Integer imageDrawable) {
        bundle.putString(BMAConstants.HEADER_TXT, headerText);
        fragment.setArguments(bundle);
        getMainActivity().updateFragment(fragment, true, imageDrawable);
    }

    EOSymptomDefect eoSelectedSymptomDefect;

    Bitmap customerSignatureBitmap;

    String addMoreProductDetail;
    boolean isCash;
    EOCompleteProductdetail selectedProductDetail;
    String paymentsAmount;
    EOModelNumber modelNumber;
    String selectpurchadeDate;
    EOSpareParts eoSpareParts;
    ArrayList<EOSpareCosumptionRequest> selectedCosumption = new ArrayList<>();
    boolean isConsumptionrequired;

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (data == null || data.getStringExtra("completeCatogryPageName") == null) {
//            return;
//        }
//        switch (data.getStringExtra("completeCatogryPageName")) {
//            case "productDetail":
//                selectedProductDetail = data.getParcelableExtra("productDetail");
//                modelNumber = data.getParcelableExtra("modelNumber");
//                selectpurchadeDate = data.getStringExtra("pod");
//                eoSpareParts = data.getParcelableExtra("eoSparePart");
//                addMoreProductDetail = data.getStringExtra("AddMoreproductDetail");
//                isConsumptionrequired = data.getBooleanExtra("isConsumptionrequired", false);
//                if (isConsumptionrequired) {
//                    if (selectedCosumption == null || selectedCosumption.size() == 0) {
//                        Bundle bundle = new Bundle();
//                        bundle.putParcelable("eoBooking", eoBooking);
//                        this.updateFragment(bundle, new SparePartConsumptionFragment(), "Spare Part");
//                    }
//                } else {
//
//                    if (this.eoSelectedSymptomDefect == null) {
//                        Bundle bundle = new Bundle();
//                        bundle.putParcelable("eoBooking", eoBooking);
//                        this.updateFragment(bundle, new SymptomFragment(), getString(R.string.symptoms));
//                    }
//                }
//                break;
//            case "spareConsumption":
//                selectedCosumption = data.getParcelableArrayListExtra("spareConsumption");
//                if (this.eoSelectedSymptomDefect == null) {
//                    Bundle bundle = new Bundle();
//                    bundle.putParcelable("eoBooking", eoBooking);
//                    this.updateFragment(bundle, new SymptomFragment(), getString(R.string.symptoms));
//                }
//                break;
//
//            case "symptom":
//                this.eoSelectedSymptomDefect = data.getParcelableExtra("selectedEOSymptomDefect");
//                break;
//            case "paymentDetail":
//                this.paymentsAmount = data.getStringExtra("paymentsAmount");
//                this.isCash = data.getBooleanExtra("isCash", false);
//                break;
//            case "customerInput":
//                this.customerSignatureBitmap = data.getParcelableExtra("customerSignature");
//                break;
//
//
//        }
//    }

    String callbackPageName="";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("aaaaa","onACtivitttttt");
        if (data == null || data.getStringExtra("completeCatogryPageName") == null) {
            return;
        }

        callbackPageName=data.getStringExtra("completeCatogryPageName");
        switch (callbackPageName) {
            case "productDetail":
                selectedProductDetail = data.getParcelableExtra("productDetail");
                modelNumber = data.getParcelableExtra("modelNumber");
                selectpurchadeDate = data.getStringExtra("pod");
                eoSpareParts = data.getParcelableExtra("eoSparePart");
                addMoreProductDetail = data.getStringExtra("AddMoreproductDetail");
                isConsumptionrequired = data.getBooleanExtra("isConsumptionrequired", false);


                break;
            case "spareConsumption":
                selectedCosumption = data.getParcelableArrayListExtra("spareConsumption");
                break;

            case "symptom":
                this.eoSelectedSymptomDefect = data.getParcelableExtra("selectedEOSymptomDefect");
                break;
            case "paymentDetail":
                this.paymentsAmount = data.getStringExtra("paymentsAmount");
                this.isCash = data.getBooleanExtra("isCash", false);
                break;
            case "customerInput":
                this.customerSignatureBitmap = data.getParcelableExtra("customerSignature");
                break;


        }
    }

    HttpRequest httpRequest;

    private void submitRequest() {
        if (selectedProductDetail == null) {
            Toast.makeText(getContext(), "Please complete Product Detail Tile", Toast.LENGTH_SHORT).show();

            return;
        }
        if (isConsumptionrequired && (selectedCosumption == null || selectedCosumption.size() == 0)) {
            Toast.makeText(getContext(), "Please complete consumption Tile", Toast.LENGTH_SHORT).show();
            return;
        }
        if (eoSelectedSymptomDefect == null) {
            Toast.makeText(getContext(), "Please complete Symptom defect Tile", Toast.LENGTH_SHORT).show();
            return;
        }
        if (paymentsAmount == null) {
            Toast.makeText(getContext(), "Please complete Payment Detail Tile", Toast.LENGTH_SHORT).show();
            return;
        }
        if (customerSignatureBitmap == null) {
            Toast.makeText(getContext(), "Please complete Cutomer Input Tile", Toast.LENGTH_SHORT).show();
            return;
        }

//        if(!(this.eoBooking.pincode.equalsIgnoreCase(misc.getLocationPinCode()))){
//            Toast.makeText(getContext(), "You can not complete booking from out side of booking pincode", Toast.LENGTH_SHORT).show();
//            return;
//        }
        this.submitButton.setEnabled(false);
        Bitmap serialnppicture = null;

        requestData.put("booking_id", eoBooking.bookingID);
        requestData.put("partner_id", eoBooking.partnerID);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        customerSignatureBitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
        String signatureURL = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        requestData.put("signature_pic", signatureURL);
        requestData.put("service_center_id", getMainActivity().getServiceCenterId());
        requestData.put("engineer_id", getMainActivity().getEngineerId());
        requestData.put("location", misc.getLocation());
        requestData.put("symptom", eoSelectedSymptomDefect.id);
        requestData.put("defect", eoSelectedSymptomDefect.defect_id);
        requestData.put("solution", eoSelectedSymptomDefect.solution_id);
        requestData.put("closing_remark", eoSelectedSymptomDefect.remarks);
        requestData.put("amount_paid", paymentsAmount);
        requestData.put("sc_agent_id", MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("scAgentID", null));

        requestData.put("appliance_broken", this.selectedProductDetail.getbookingProductUnit().isProductBroken);
        requestData.put("purchase_date", this.selectedProductDetail.getbookingProductUnit().purchase_date);
        ArrayList<HashMap<String, Object>> unitlist = new ArrayList<>();

        int counter = 0;
        for (EOCompleteProductQuantity selectCompleteProduct : this.selectedProductDetail.getbookingProductUnit().quantity) {
            HashMap<String, Object> requestMap = new HashMap<>();
            requestMap.put("unit_id", selectCompleteProduct.unit_id);

            requestMap.put("complete", selectCompleteProduct.isComplete);
            if (selectCompleteProduct.serialNumber != null)
                requestMap.put("serial_number", selectCompleteProduct.serialNumber);

            if (selectCompleteProduct.model_number != null)
                requestMap.put("model_number", selectCompleteProduct.model_number);
            requestMap.put("service_charge", selectCompleteProduct.customerBasicharge);
            requestMap.put("additional_service_charge", selectCompleteProduct.customerExtraharge);
            if (selectCompleteProduct.product_or_services.equalsIgnoreCase("product") && Double.valueOf(selectCompleteProduct.amountDue) > 0) {
                requestMap.put("service_charge", selectCompleteProduct.customerPartharge);
                requestMap.put("parts_cost", "0.00");

            } else {
                requestMap.put("parts_cost", selectCompleteProduct.customerPartharge);
            }
            requestMap.put("pod", selectCompleteProduct.pod);
            if (eoSpareParts != null) {
                if (selectCompleteProduct.invoicePic != null) {
                    if (this.eoSpareParts.invoice_pic != null) {
                        requestMap.put("existing_purchase_invoice", onCaptureImageResult(selectCompleteProduct.invoicePic));
                    } else if (selectCompleteProduct.invoicePic != null) {
                        requestMap.put("purchase_invoice", onCaptureImageResult(selectCompleteProduct.invoicePic));
                    }
                }
                if (selectCompleteProduct.serialNoPic != null) {
                    if (this.eoSpareParts.serial_number_pic != null) {
                        requestMap.put("existing_serial_number_pic", onCaptureImageResult(selectCompleteProduct.serialNoPic));
                    } else if (selectCompleteProduct.serialNoPic != null) {
                        requestMap.put("serial_number_pic", onCaptureImageResult(selectCompleteProduct.serialNoPic));
                    }
                }
            } else {
                if (selectCompleteProduct.serialNoPic != null && selectCompleteProduct.serialNoPic != null) {
                    requestMap.put("serial_number_pic", onCaptureImageResult(selectCompleteProduct.serialNoPic));
                    serialnppicture = selectCompleteProduct.serialNoPic;
                }
                if (selectCompleteProduct.invoicePic != null && selectCompleteProduct.invoicePic != null) {
                    requestMap.put("purchase_invoice", onCaptureImageResult(selectCompleteProduct.invoicePic));
                }
            }

            unitlist.add(requestMap);

        }
        for (EOCompleteProductQuantity selectCompleteProduct : this.selectedProductDetail.prices) {
            HashMap<String, Object> requestMap = new HashMap<>();

            if (selectCompleteProduct.isComplete || selectCompleteProduct.isNotComplete) {
                requestMap.put("complete", selectCompleteProduct.isComplete);
                requestMap.put("service_category", selectCompleteProduct.serviceCategory);
                requestMap.put("unit_id", selectCompleteProduct.newUnitId);
                requestMap.put("service_charge", selectCompleteProduct.customerBasicharge);
                requestMap.put("additional_service_charge", selectCompleteProduct.customerExtraharge);
                requestMap.put("parts_cost", selectCompleteProduct.customerPartharge);
                unitlist.add(requestMap);
            }


        }
        requestData.put("unit_array", unitlist);
        if (selectedCosumption != null && selectedCosumption.size() > 0) {
            requestData.put("spare_consumption", selectedCosumption);//BMAGson.store().toJson(selectedCosumption));
        }


        BMAmplitude.saveUserAction("CompleteBookingcategory", "CompleteButton");


        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = CompleteBookingCategoryFragment.this;
        httpRequest.execute("completeBookingByEngineer", BMAGson.store().toJson(requestData), getMainActivity().getEngineerId());

    }

    @Override
    public void processFinish(String response) {
        httpRequest.progress.dismiss();
        Log.d("resp", "response  = " + response);
        if (response.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(response);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                if (statusCode.equals("0000")) {
                    BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext()) {


                        @Override
                        public void onDefault() {
                            super.onDefault();
                            submitButton.setEnabled(true);
                            Intent intent = new Intent();
                            intent.putExtra("completed", true);
                            getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, intent);
                            getFragmentManager().popBackStack();
                        }

                        @Override
                        public void onConfirmation() {
                            super.onConfirmation();

                        }
                    };
                    bmaAlertDialog.show(jsonObject.get("result").toString());


                }
            } catch (JSONException e) {
                this.submitButton.setEnabled(true);
                httpRequest.progress.dismiss();
                BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {


                    @Override
                    public void onWarningDismiss() {
                        super.onWarningDismiss();
                    }
                };
                bmaAlertDialog.show("Something went wrong");
            }
        } else {
            this.submitButton.setEnabled(true);
            httpRequest.progress.dismiss();
            BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {


                @Override
                public void onWarningDismiss() {
                    super.onWarningDismiss();
                }
            };
            bmaAlertDialog.show("Ops Server Error");
        }


    }

    String SN_DIRECTORY = Environment.getExternalStorageDirectory() + "/AroundSerialNO/";


    public String onCaptureImageResult(Bitmap thumbnail) {


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        thumbnail.compress(Bitmap.CompressFormat.JPEG, 30, bytes);


        //Log.d("aaaaaa","category = "+thumbnail.getWidth()+"    bitmap height ="+thumbnail.getHeight());
        String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        String serialNoPath = eoBooking.bookingID + "_" + pic_name + ".png";
        File destination = new File(SN_DIRECTORY);
        try {
            File file = new File(destination, serialNoPath);
            boolean isFile = file.createNewFile();
            if (!file.exists()) {
                isFile = file.createNewFile();
            }
            if (!isFile) {
                destination.mkdirs();
            }
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();
            String encodeImage = Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
            //Log.d("aaaaaa","image string = "+encodeImage);
            return encodeImage;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


}
