package com.around.engineerbuddy.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.BMAmplitude;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.ImagePicker;
import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.activity.ShowImageActivity;
import com.around.engineerbuddy.activity.VideoViewActivity;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.component.BMACardView;
import com.around.engineerbuddy.component.BMAFontViewField;
import com.around.engineerbuddy.component.BMASelectionDialog;
import com.around.engineerbuddy.component.ScannerDialog;
import com.around.engineerbuddy.entity.BMAUiEntity;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.entity.EOModelNumber;
import com.around.engineerbuddy.entity.EOPartName;
import com.around.engineerbuddy.entity.EOPartType;
import com.around.engineerbuddy.entity.EOSpareCosumptionRequest;
import com.around.engineerbuddy.entity.EOSpareParts;
import com.around.engineerbuddy.entity.EOSparePartsOrder;
import com.around.engineerbuddy.util.BMAConstants;
import com.around.engineerbuddy.util.BMAUIUtil;
import com.squareup.picasso.Picasso;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SparePartsOrderFragment extends BMAFragment implements View.OnClickListener {


    Button checkOut, addMorePart;
    LayoutInflater inflater;
    LinearLayout parentLayout, serialNoInvoicePhotoLayout, serialNoLayout;
    int partNumberCounter = 0;
    ScrollView partScrollView;
    ImageView serialNoPic, invoicePic;
    private HttpRequest httpRequest;
    ImageView frontDefectivePart, backDefectivePart;
    BMAFontViewField selectPurchaseDateIcon, modelDropDownIcon,scannericon;
    TextView selectpurchaseDate;
    EditText selectModel, enterSerialNo, problemdescriptionedittext;
    private EOBooking eoBooking;
    EOSparePartsOrder eoSparePartsOrder;
    ArrayList<EOPartType> partTypeList;
    ArrayList<EOPartName> partNameList;
    String mCurrentPhotoPath;

    HashMap<String, Object> partsMap = new HashMap<>();
    String SN_DIRECTORY = Environment.getExternalStorageDirectory() + "/AroundSerialNO/";
    HashMap<Integer, String> partsObject = new HashMap<>();

    BMACardView ProblemdescriptionCardView;
    EOModelNumber modelNumber;
    LinearLayout selectModelLayout;
    //   boolean isCheckedSpareWarranty;
    // String selectModelNumberCallType;
    String selectPOD;
    String scanSerialNumber;
    ArrayList<EOSpareCosumptionRequest> cosumptionArray = new ArrayList<>();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.eoBooking = getArguments().getParcelable("eoBooking");
        //  selectModelNumberCallType = getArguments().getString("modelNo");
        selectPOD = getArguments().getString("pod");
        modelNumber = getArguments().getParcelable("modelNumber");
        cosumptionArray=getArguments().getParcelableArrayList( "consumptionArray");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.spare_parts_order, container, false);
        BMAmplitude.saveUserAction("SparePartsOrderFragment", "SparePartsOrderFragment");
        this.inflater = inflater;
        this.partScrollView = this.view.findViewById(R.id.partScrollView);
        this.problemdescriptionedittext = this.view.findViewById(R.id.Problemdescriptionedittext);
        this.parentLayout = this.view.findViewById(R.id.layout);
        this.checkOut = this.view.findViewById(R.id.cancelButton);
        this.addMorePart = this.view.findViewById(R.id.submitButton);
        this.serialNoPic = this.view.findViewById(R.id.serialNoPic);
        this.invoicePic = this.view.findViewById(R.id.invoicePic);
        this.selectPurchaseDateIcon = this.view.findViewById(R.id.selectPurchaseDateIcon);
        this.selectpurchaseDate = this.view.findViewById(R.id.selectpurchaseDate);
        this.modelDropDownIcon = this.view.findViewById(R.id.modelDropDownIcon);
        this.selectModel = this.view.findViewById(R.id.selectModel);
        this.enterSerialNo = this.view.findViewById(R.id.enterSerialNo);
        this.selectModelLayout = this.view.findViewById(R.id.selectModelLayout);
        this.scannericon=this.view.findViewById(R.id.scannericon);


        this.scannericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ScannerDialog(getContext()){
                    @Override
                    public void onConfirmdata(String scanData) {
                        super.onConfirmdata(scanData);
                        if(scanData!=null){
                            enterSerialNo.setText(scanData);
                            enterSerialNo.setEnabled(false);
                         Log.d("aaaaa","csnadDATATA= "+scanData);
                        }
                    }
                }.show();
//                Scannerfragment scannerfragment=new Scannerfragment();
//                getMainActivity().updateFragment(scannerfragment,true);
            }
        });
        ////warranty Checker


        this.selectModel.setEnabled(false);
        int dialogRadius = R.dimen._20dp;
        BMAUIUtil.setBackgroundRound(this.checkOut, BMAUIUtil.getColor(R.color.missedBookingcolor), new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
        BMAUIUtil.setBackgroundRound(this.addMorePart, BMAUIUtil.getColor(R.color.missedBookingcolor), new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
        this.addPartView();
        this.addMorePart.setText(getString(R.string.addMorePart));
        this.checkOut.setText(getString(R.string.submit));
        this.addMorePart.setOnClickListener(this);
        this.checkOut.setOnClickListener(this);
        this.modelDropDownIcon.setOnClickListener(this);
        this.view.findViewById(R.id.serialPhotoLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   selectPic(1);
                //inflateDialog(serialNoPicBitmap);

                new ImagePicker(getContext(),getMainActivity(),1,SparePartsOrderFragment.this,selectedSerialNoImageUri,true);
            }
        });
        this.view.findViewById(R.id.invoicePhotoLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selectPic(2);
                new ImagePicker(getContext(),getMainActivity(),2,SparePartsOrderFragment.this,selectedInvoiceImageUri,true);
            }
        });
        this.selectPurchaseDateIcon.setOnClickListener(this);


        this.serialNoInvoicePhotoLayout = this.view.findViewById(R.id.serialNoInvoicePhotoLayout);
        this.serialNoLayout = this.view.findViewById(R.id.serialNoLayout);
        this.ProblemdescriptionCardView = this.view.findViewById(R.id.ProblemdescriptionCardView);
//        this.serialNoInvoicePhotoLayout.setVisibility(View.GONE);
//        this.ProblemdescriptionCardView.setVisibility(View.GONE);
//        this.serialNoLayout.setVisibility(View.GONE);
//        this.addMorePart.setVisibility(View.GONE);
//        this.checkOut.setText("Check Warranty");
//        this.parentLayout.setVisibility(View.GONE);

        if (this.modelNumber != null && this.modelNumber.modelNumber != null) {
            selectModel.setText(this.modelNumber.modelNumber);
            modelDropDownIcon.setEnabled(false);
            selectModel.setEnabled(false);
        }
        if (selectPOD != null) {
            selectpurchaseDate.setText(selectPOD);
            selectPurchaseDateIcon.setEnabled(false);
        }
        this.modelDropDownIcon.setVisibility(View.INVISIBLE);
       // if(this.eoSparePartsOrder==null) {
            this.getModelData();
        //}
        if (modelNumber != null) {
            selectModel.setTag(modelNumber);
        }
       // Log.d("aaaaaa","scaned data ="+this.scanSerialNumber);
       // //enterSerialNo.setText("gggggggggggggggggg");
//        if(this.scanSerialNumber!=null){
//            enterSerialNo.setText(this.scanSerialNumber);
//            enterSerialNo.setHint(this.scanSerialNumber);
//           // enterSerialNo.setEnabled(false);
//        }
        //Log.d("aaaaaa","scaned GET data ="+enterSerialNo.getText().toString());
        //   this.getPartTypeData();
        return this.view;
    }

    String actionID;

    private void getModelData() {
        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = SparePartsOrderFragment.this;
        this.actionID = "engineerSparePartOrder";
        httpRequest.execute(actionID, eoBooking.partnerID, eoBooking.serviceID, eoBooking.bookingID);

    }

    private void getPartTypeData() {
        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = SparePartsOrderFragment.this;
        this.actionID = "partTypeOnModelNumber";
        httpRequest.execute(actionID, ((EOModelNumber) this.selectModel.getTag()).id);
    }

    private void getPartNameData(TextView selectedPartTypeView) {
        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = SparePartsOrderFragment.this;
        this.actionID = "sparePartName";
        EOPartType eoPartType = ((EOPartType) selectedPartTypeView.getTag());
        if (this.selectModel.getTag() != null && ((EOModelNumber) this.selectModel.getTag()).id != null) {
            httpRequest.execute(actionID, eoPartType.part_type, eoBooking.partnerID, eoBooking.serviceID, ((EOModelNumber) this.selectModel.getTag()).id);
        } else {
            httpRequest.execute(actionID, eoPartType.part_type, eoBooking.partnerID, eoBooking.serviceID);
        }
    }
//
//    private void checkWarranty() {
//
//        httpRequest = new HttpRequest(getMainActivity(), true);
//        httpRequest.delegate = SparePartsOrderFragment.this;
//        this.actionID = "sparePartsWarrantyChecker";
//        httpRequest.execute(actionID, eoBooking.bookingID, eoBooking.partnerID, eoBooking.bookingCreateDate, eoBooking
//                .requestType, selectModel.getText().toString().trim(), selectpurchaseDate.getText().toString().trim());
//
//    }


    @Override
    public void processFinish(String response) {
        // super.processFinish(respononse);
        Log.d("bbbbbbb", "response  = " + response);

        if (response.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(response);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                if (statusCode.equals("0000")) {
//                    if (this.actionID.equalsIgnoreCase("sparePartsWarrantyChecker")) {
//                        JSONObject warrntyJsonObject =jsonObject.getJSONObject("response");
//                        if(warrntyJsonObject.getString("warranty_flag").equalsIgnoreCase("1")) {
//                            String warrantyMessage = warrntyJsonObject.getString("message");
//                            showWarrantyMessage(warrantyMessage);
//                        }else{
//                            updateSparePartView();
//                        }

                    // } else
                    if(this.actionID.equalsIgnoreCase("submitPrevPartsConsumption")){
                       // httpRequest.progress.dismiss();
                        sendToServer();
                    }
                    else if (this.actionID.equalsIgnoreCase("engineerSparePartOrder")) {
                        httpRequest.progress.dismiss();
                        String responseData = (new JSONObject(jsonObject.getString("response")).getString("sparePartsOrder"));
                        this.eoSparePartsOrder = BMAGson.store().getObject(EOSparePartsOrder.class, responseData);
                        if (this.eoSparePartsOrder != null) {


                            if (this.eoSparePartsOrder.eoSpareParts != null) {
                                setInvoiceAndSerialPic(this.eoSparePartsOrder.eoSpareParts);
                            }
//                            if (this.eoSparePartsOrder.modelNumberList.size() < 1) {
//                                selectModel.setEnabled(true);
//                                this.selectModel.setHint(getString(R.string.enterModelNumber));
//                                this.modelDropDownIcon.setEnabled(false);
//                                this.modelDropDownIcon.setVisibility(View.INVISIBLE);
//                            }
                            if (this.eoSparePartsOrder.getPartOnModel) {
                                for (EOModelNumber eoModelNumber : this.eoSparePartsOrder.modelNumberList) {
                                    if (modelNumber.modelNumber.equalsIgnoreCase(eoModelNumber.modelNumber)) {
                                        modelNumber.id = eoModelNumber.id;
                                        break;
                                    }
                                }
                                getPartTypeData();
//                                this.modelDropDownIcon.setEnabled(true);
//                                selectModel.setEnabled(true);
                            } else {
                                this.modelDropDownIcon.setEnabled(false);
                                selectModel.setEnabled(false);
                                //  getPartTypeData();
                            }

                        }
                    } else if (this.actionID.equalsIgnoreCase("partTypeOnModelNumber")) {
                        httpRequest.progress.dismiss();
                        String responseData = (new JSONObject(jsonObject.getString("response")).getString("partTypeList"));
                        partTypeList = BMAGson.store().getList(EOPartType.class, responseData);
                    } else if (this.actionID.equalsIgnoreCase("sparePartName")) {
                        httpRequest.progress.dismiss();
                        partNameList = BMAGson.store().getList(EOPartName.class, jsonObject.getString("response"));
                        if (partNameList == null || partNameList.size() == 0) {
                            EditText selectedPartName = selectedPartTypeChildView.findViewById(R.id.selectPartName);
                            BMAFontViewField selectedPartNameDropDownIcon = selectedPartTypeChildView.findViewById(R.id.partNameDropDownIcon);
                            TextView partNumber = selectedPartTypeChildView.findViewById(R.id.selectPartNo);
                            selectedPartName.setEnabled(true);
                            selectedPartName.setHint(getString(R.string.enterPartName));
                            selectedPartName.setTag(selectedPartTypeChildView.getTag());
                            selectedPartName.addTextChangedListener(new CustomeTextWatcher(selectedPartName, "selectedPartName"));
                            selectedPartNameDropDownIcon.setVisibility(View.INVISIBLE);
                            partNumber.setText("");
                            partNumber.setVisibility(View.GONE);
                            // selectedPartNameDropDownIcon.setEnabled(false);

                        }
                    } else if (this.actionID.equalsIgnoreCase("submitSparePartsOrder")) {
                        httpRequest.progress.dismiss();
                        BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext()) {
                            @Override
                            public void onDefault() {
                                super.onDefault();
                                Intent intent = new Intent();
                                intent.putExtra("completed", true);
                                getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, intent);
                                getFragmentManager().popBackStack();
                            }
                        };
                        bmaAlertDialog.show(jsonObject.getString("response"));


                    }

                } else {
                    httpRequest.progress.dismiss();
                    BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {
                        @Override
                        public void onWarningDismiss() {
                            super.onWarningDismiss();
                        }

                    };
                    bmaAlertDialog.show(jsonObject.getString("result"));
                }
            } catch (Exception e) {
                httpRequest.progress.dismiss();
            }
        }else{
            httpRequest.progress.dismiss();
            BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {
                @Override
                public void onWarningDismiss() {
                    super.onWarningDismiss();
                }

            };
            bmaAlertDialog.show("Server Error");
        }
    }

    EditText selectedPartName;
    //    BMAFontViewField partNameDropDownIcon;
    //  BMAFontViewField selectedPartNameDropDownIcon;
    // TextView selectPartType, selectPartNo, spareAmount;
    EditText partName, selectQuantity;
    int ImagePartKey;

    private void addPartView() {
        partNumberCounter++;
        View childView = this.inflater.inflate(R.layout.spare_parts_items_row, null, false);
        BMAFontViewField partTypeDropDownIcon = childView.findViewById(R.id.partTypeDropDownIcon);
        BMAFontViewField partNameDropDownIcon = childView.findViewById(R.id.partNameDropDownIcon);
        TextView partNumber = childView.findViewById(R.id.rowPartNumber);
        // selectPartType = childView.findViewById(R.id.selectPartType);
        BMAFontViewField deleteIcon = childView.findViewById(R.id.deleteIcon);

        TextView selectPartNo = childView.findViewById(R.id.selectPartNo);
        TextView spareAmount = childView.findViewById(R.id.spareAmount);
        selectQuantity = childView.findViewById(R.id.selectQuantity);

        LinearLayout spareRowLayout = childView.findViewById(R.id.spareRowLayout);
        LinearLayout spareNumberLayout = childView.findViewById(R.id.spareNumberLayout);
        LinearLayout productStatusLayout = childView.findViewById(R.id.productStatusLayout);
        partName = childView.findViewById(R.id.selectPartName);
        partName.setEnabled(false);
        int dialogRadius = 40;
        BMAUIUtil.setBackgroundRound(spareNumberLayout, R.color.colorPrimary, new float[]{0, 0, dialogRadius, dialogRadius, dialogRadius, dialogRadius, 0, 0});
        BMAUIUtil.setBackgroundRound(productStatusLayout, R.color.white, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        BMAFontViewField expandArrowIcon = childView.findViewById(R.id.expandArrowIcon);
        selectQuantity.setText("1");
        partsMap.put("quantity", "1");
        selectQuantity.setTag(partNumberCounter);
        selectQuantity.addTextChangedListener(new CustomeTextWatcher(selectQuantity, "quantity"));
        childView.setTag(partNumberCounter);
        partTypeDropDownIcon.setTag(partNumberCounter);
        partTypeDropDownIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int choledViewKey = (Integer) v.getTag();
                View setChildView = parentLayout.getChildAt(choledViewKey - 1);
//                partNameDropDownIcon = childView.findViewById(R.id.partNameDropDownIcon);
//                selectPartName = childView.findViewById(R.id.selectPartName);
//                selectPartNo = childView.findViewById(R.id.selectPartNo);


//                selectedPartName = selectPartName;
//                selectedPartNameDropDownIcon = partNameDropDownIcon;
//                selectPartType = childView.findViewById(R.id.selectPartType);
                openPartTypeSelectionPopUp(setChildView, (BMAFontViewField) v);
            }
        });
        partNameDropDownIcon.setTag(partNumberCounter);
        partNameDropDownIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int choledViewKey = (Integer) v.getTag();
                View setChildView = parentLayout.getChildAt(choledViewKey - 1);

                openPartNameSelectionPopUp(setChildView, (BMAFontViewField) v);
            }
        });
        expandArrowIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spareRowLayout.setVisibility(spareRowLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });
        deleteIcon.setVisibility(partsObject.size() == 0 ? View.GONE : View.VISIBLE);
        deleteIcon.setTag(partNumberCounter);

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   parentLayout.removeView(childView);
                parentLayout.getChildAt((Integer) deleteIcon.getTag()-1).setVisibility(View.GONE);
                int partKey = (int) deleteIcon.getTag();
                partsObject.remove(partKey);
            }
        });
        LinearLayout backDefectiveImageLayout = childView.findViewById(R.id.backDefectiveImageLayout);
        LinearLayout frontDefectiveImageLayout = childView.findViewById(R.id.frontDefectiveImageLayout);
        partNumber.setText("" + partNumberCounter);
        frontDefectiveImageLayout.setTag(partNumberCounter);
        frontDefectiveImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePartKey = (int) v.getTag();
                View setChildView = parentLayout.getChildAt(ImagePartKey - 1);
                frontDefectivePart = setChildView.findViewById(R.id.frontDefectivePartImage);
                new ImagePicker(getContext(),getMainActivity(),3,SparePartsOrderFragment.this,null,true);
              //  selectPic(3);
            }
        });
        backDefectiveImageLayout.setTag(partNumberCounter);
        backDefectiveImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePartKey = (int) v.getTag();
                View setChildView = parentLayout.getChildAt(ImagePartKey - 1);
                backDefectivePart = setChildView.findViewById(R.id.backDefectivePartImage);
                //selectPic(4);
                new ImagePicker(getContext(),getMainActivity(),4,SparePartsOrderFragment.this,null,true);
            }
        });
        partsObject.put(partNumberCounter, null);
        this.parentLayout.setTag(partNumberCounter);
        this.parentLayout.addView(childView);
        this.partScrollView.fullScroll(View.FOCUS_DOWN);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.selectPurchaseDateIcon:
                openCalender();
                break;
            case R.id.submitButton:
                if (!isSelectAllField()) {
                    if (!partsObject.containsKey(partNumberCounter)) {
                        // if(checkQuantity()){
                        addPartView();
                        return;
//                        }else {
//                            return;
//                        }

                    }
                    Toast.makeText(getContext(), getString(R.string.selectAllFieldsValidation), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!checkQuantity()) {
                    return;
                }
                partsMap.put("part_warranty_status", 1);
                //partsMap.put("quantity", selectQuantity.getText().toString().trim());
                HashMap<String, Object> partsMapObj = partsMap;
                partsObject.put(partNumberCounter, BMAGson.store().toJson(partsMapObj));
                frontDefectiveBitmapUri = null;
                backDefectiveBitmapUri = null;
                partsMap.clear();
                this.addPartView();
                break;
            case R.id.modelDropDownIcon:
                openSelectionPopUp();
                break;
            case R.id.cancelButton:

//                if (this.checkOut.getText().toString().equalsIgnoreCase("Check Warranty")) {
//                    checkWarranty();
//                    return;
//                }
                if (!((selectModel.getTag() != null || selectModel.getText().toString().trim().length() > 0) && enterSerialNo.getText().toString().trim().length() > 0 && selectpurchaseDate.getText().toString().trim().length() > 0 && this.selectedSerialNoImageUri!= null && this.selectedInvoiceImageUri != null && problemdescriptionedittext.getText().toString().trim().length() > 0)) {
                    Toast.makeText(getContext(), getString(R.string.selectAllFieldsValidation), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isSelectAllField()) {
                    if (!partsObject.containsKey(partNumberCounter)) {
                        //  if(checkQuantity()){
                        if(this.eoBooking!=null && this.eoBooking.pre_consume_req && this.cosumptionArray!=null && this.cosumptionArray.size()>0){
                            submitRequestToServer();
                        }else{
                            sendToServer();
                        }
                        // }

                        return;
                    }
                    //Toast.makeText(getContext(), getString(R.string.selectAllFieldsValidation), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (partsObject.containsKey(partNumberCounter)) {
                    if (!checkQuantity()) {
                        return;
                    }
                    partsMap.put("part_warranty_status", 1);
                    //partsMap.put("quantity", selectQuantity.getText().toString().trim());
                    partsObject.put(partNumberCounter, BMAGson.store().toJson(partsMap));
                }

                if(this.eoBooking!=null && this.eoBooking.pre_consume_req && this.cosumptionArray!=null && this.cosumptionArray.size()>0){
                    submitRequestToServer();
                }else {
                    sendToServer();
                }

                break;
        }
    }

    private void openSelectionPopUp() {
        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getModelList(), true) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if (selectedItems != null) {
                    SparePartsOrderFragment.this.selectModel.setText(selectedItems.getDetail1());
                    SparePartsOrderFragment.this.selectModel.setTag(selectedItems.tag);
                    //   if (isCheckedSpareWarranty) {
                    getPartTypeData();
                    // }
                }

            }
        };
        bmaSelectionDialog.setTitle(getString(R.string.selectModelNumber));
        bmaSelectionDialog.show();
    }

    private ArrayList<BMAUiEntity> getModelList() {
        ArrayList<BMAUiEntity> bmaUiEntityArrayList = new ArrayList<>();
        if (this.eoSparePartsOrder == null) {
            return bmaUiEntityArrayList;
        }
        for (EOModelNumber eoModelNumber : this.eoSparePartsOrder.modelNumberList) {
            BMAUiEntity bmaUiEntity = new BMAUiEntity();
            bmaUiEntity.setDetail1(eoModelNumber.modelNumber);
            if (selectModel.getTag() != null && ((EOModelNumber) selectModel.getTag()).id.equalsIgnoreCase(eoModelNumber.id)) {
                bmaUiEntity.setChecked(true);
            }
            bmaUiEntity.tag = eoModelNumber;
            bmaUiEntityArrayList.add(bmaUiEntity);
        }
        return bmaUiEntityArrayList;
    }

    View selectedPartTypeChildView;

    private void openPartTypeSelectionPopUp(View setChildView, BMAFontViewField partTypeIcon) {
        selectedPartTypeChildView = setChildView;
        TextView selectedPartTypeView = setChildView.findViewById(R.id.selectPartType);
        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getPartTypeList(selectedPartTypeView)) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if (selectedItems != null) {
                    selectedPartTypeView.setText(selectedItems.getDetail1());
                    selectedPartTypeView.setTag(selectedItems.tag);
                    Integer getKey = (Integer) partTypeIcon.getTag();
                    String getPartString = partsObject.get(getKey);
                    if (getPartString != null) {
                        HashMap<String, Object> getPartsMap = BMAGson.store().getObject(HashMap.class, getPartString);
                        getPartsMap.put("parts_type", selectedItems.getDetail1());
                        partsObject.put(getKey, BMAGson.store().toJson(getPartsMap));
                    } else {
                        partsMap.put("parts_type", selectedItems.getDetail1());
                    }
                    EditText partNameView = setChildView.findViewById(R.id.selectPartName);
                    TextView selectPartNo = setChildView.findViewById(R.id.selectPartNo);
                    partNameView.setText("");
                    selectPartNo.setText("");


                    getPartNameData(selectedPartTypeView);
                }

            }
        };
        bmaSelectionDialog.setTitle(getString(R.string.selectPartType));
        bmaSelectionDialog.show();
    }

    int maxCountity=1;

    private void openPartNameSelectionPopUp(View setChildView, BMAFontViewField v) {
        EditText selectedPartNameView = setChildView.findViewById(R.id.selectPartName);
        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getPartNameList(selectedPartNameView)) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if (selectedItems != null) {

                    TextView spareAmount = setChildView.findViewById(R.id.spareAmount);
                    TextView selectPartNo = setChildView.findViewById(R.id.selectPartNo);
                    EditText selectQuantity = setChildView.findViewById(R.id.selectQuantity);
                    // selectQuantity.setText("1");


                    selectedPartNameView.setText(selectedItems.getDetail1());
                    selectedPartNameView.setTag(selectedItems.tag);
                    EOPartName eoPartName = (EOPartName) selectedItems.tag;
                    spareAmount.setText("₹ " + eoPartName.amount+" /- (Per Part)");
                    selectPartNo.setText(eoPartName.part_number);
                    if (eoPartName.max_quantity != null && eoPartName.max_quantity.trim().length() != 0) {
                        maxCountity = Integer.valueOf(eoPartName.max_quantity);
                        // selectQuantity.setHint("Enter quantity ( Max " + eoPartName.max_quantity + " )");
                    }
//                    else {
//                        maxCountity=1;
//                    }
                    int getKey = (int) v.getTag();
                    String getPartString = partsObject.get(getKey);
                    if (getPartString != null) {
                        HashMap<String, Object> getPartsMap = BMAGson.store().getObject(HashMap.class, getPartString);
                        getPartsMap.put("parts_name", selectedItems.getDetail1());
                        getPartsMap.put("requested_inventory_id", eoPartName.inventory_id);
                        getPartsMap.put("maxQuantity", eoPartName.max_quantity);
                        partsObject.put(getKey, BMAGson.store().toJson(getPartsMap));
                    } else {
                        partsMap.put("parts_name", selectedItems.getDetail1());
                        partsMap.put("requested_inventory_id", eoPartName.inventory_id);
                        partsMap.put("maxQuantity", eoPartName.max_quantity);
                    }
                }

            }
        };
        bmaSelectionDialog.setTitle(getString(R.string.selectPartName));
        bmaSelectionDialog.show();
    }

    private ArrayList<BMAUiEntity> getPartNameList(EditText selectPartName) {
        ArrayList<BMAUiEntity> bmaUiEntityArrayList = new ArrayList<>();
        if (this.partNameList == null) {
            return bmaUiEntityArrayList;
        }
        for (EOPartName eoPartType : this.partNameList) {
            BMAUiEntity bmaUiEntity = new BMAUiEntity();
            bmaUiEntity.setDetail1(eoPartType.part_name);
            if (selectPartName.getTag() != null && ((EOPartName) selectPartName.getTag()).part_name.equalsIgnoreCase(eoPartType.part_name)) {
                bmaUiEntity.setChecked(true);
            }

            bmaUiEntity.tag = eoPartType;
            bmaUiEntityArrayList.add(bmaUiEntity);
        }
        return bmaUiEntityArrayList;
    }


    private ArrayList<BMAUiEntity> getPartTypeList(TextView selectPartType) {
        ArrayList<BMAUiEntity> bmaUiEntityArrayList = new ArrayList<>();
        //if (this.eoSparePartsOrder != null && this.eoSparePartsOrder.modelNumberList.size() < 1) {
        if (this.eoSparePartsOrder != null && this.eoSparePartsOrder.partTypeList.size() > 0) {
            for (EOPartType eoPartType : this.eoSparePartsOrder.partTypeList) {
                BMAUiEntity bmaUiEntity = new BMAUiEntity();
                bmaUiEntity.setDetail1(eoPartType.part_type);
                if (selectPartType.getTag() != null && ((EOPartType) selectPartType.getTag()).part_type.equalsIgnoreCase(eoPartType.part_type)) {
                    bmaUiEntity.setChecked(true);
                }
                bmaUiEntity.tag = eoPartType;
                bmaUiEntityArrayList.add(bmaUiEntity);
            }
            return bmaUiEntityArrayList;
        }

        if (this.partTypeList == null) {
            return bmaUiEntityArrayList;
        }
        for (EOPartType eoPartType : this.partTypeList) {
            BMAUiEntity bmaUiEntity = new BMAUiEntity();
            bmaUiEntity.setDetail1(eoPartType.part_type);
            bmaUiEntity.tag = eoPartType;
            bmaUiEntityArrayList.add(bmaUiEntity);
        }
        return bmaUiEntityArrayList;
    }

   // Bitmap serialNoPicBitmap, invoicePicBitmap, backDefectiveBitmap, frontDefectiveBitmap;

    Uri selectedImageUri,selectedSerialNoImageUri,selectedInvoiceImageUri,backDefectiveBitmapUri,frontDefectiveBitmapUri;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      //  Log.d("aaaaa","spare Fragment ="+mCurrentPhotoPath);
        if(data!=null && data.getStringExtra("scan")!=null) {

             scanSerialNumber=data.getStringExtra("content");
//             enterSerialNo.setText(scanSerialNumber);
//             enterSerialNo.setHint(scanSerialNumber);
           // enterSerialNo.setText(scanSerialNumber);
            Log.d("aaaaa","setComnetvt = "+scanSerialNumber);



            return;
        }


        File file = new File(mCurrentPhotoPath);
        Bitmap imageBitmap = null;
        try {
            selectedImageUri=Uri.fromFile(file);
            imageBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(imageBitmap==null && data!=null && data.getData()!=null){
            Uri getImageUri=data.getData();
            selectedImageUri=getImageUri;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getMainActivity().getContentResolver(), getImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        switch (requestCode) {
            case 1:
                if(imageBitmap!=null) {
                    selectedSerialNoImageUri=selectedImageUri;
                   // this.serialNoPicBitmap = imageBitmap;
                    serialNoPic.setImageBitmap(imageBitmap);
                }
                break;
            case 2:
                if(imageBitmap!=null) {
                    selectedInvoiceImageUri=selectedImageUri;
                    //this.invoicePicBitmap = imageBitmap;
                    invoicePic.setImageBitmap(imageBitmap);
                }


                break;
            case 3:
                if(imageBitmap!=null) {
                    frontDefectiveBitmapUri=selectedImageUri;
                 //   Log.d("aaaaaa", "defective_front_parts = " + onCaptureImageResult(frontDefectiveBitmapUri));
                    String getPartString = partsObject.get(ImagePartKey);
                    if (getPartString != null) {
                        HashMap<String, Object> getPartsMap = BMAGson.store().getObject(HashMap.class, getPartString);
                        getPartsMap.put("defective_front_parts", onCaptureImageResult(frontDefectiveBitmapUri));
                        partsObject.put(ImagePartKey, BMAGson.store().toJson(getPartsMap));
                    } else {
                        partsMap.put("defective_front_parts", onCaptureImageResult(frontDefectiveBitmapUri));
                    }

                 //   frontDefectiveBitmap = imageBitmap;


                    frontDefectivePart.setImageBitmap(imageBitmap);
                }
                break;
            case 4:
                if(imageBitmap!=null) {
                  //  backDefectiveBitmap = imageBitmap;
                    backDefectiveBitmapUri=selectedImageUri;

                    String getPartString1 = partsObject.get(ImagePartKey);
                    if (getPartString1 != null) {
                        HashMap<String, Object> getPartsMap = BMAGson.store().getObject(HashMap.class, getPartString1);
                        partsMap.put("defective_back_parts", onCaptureImageResult(backDefectiveBitmapUri));
                        partsObject.put(ImagePartKey, BMAGson.store().toJson(getPartsMap));
                    } else {
                        partsMap.put("defective_back_parts", onCaptureImageResult(backDefectiveBitmapUri));
                    }
                    //Log.d("aaaaaa", "defective_back_parts = " + onCaptureImageResult(imageBitmap));

                    backDefectivePart.setImageBitmap(imageBitmap);
                }
                break;

        }
    }


    public String onCaptureImageResult(Uri uri) {
        Bitmap thumbnail= null;
        try {
            thumbnail = MediaStore.Images.Media.getBitmap(getMainActivity().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(thumbnail==null){
            String uriString=uri.toString();
            thumbnail=getBitmapFromURL(uriString);
            Log.d("aaaaa","thumbnail = "+thumbnail);
            if(thumbnail==null)
            return null;
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 30, bytes);
        String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        String serialNoPath = eoBooking.bookingID + "_" + pic_name + ".png";
        File destination = new File(SN_DIRECTORY);//, serialNoPath);
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
            return encodeImage;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        boolean canUseExternalStorage = false;

//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            canUseExternalStorage = true;
        }

        if (!canUseExternalStorage) {
            Toast.makeText(getActivity(), "Cannot use this feature without requested permission", Toast.LENGTH_SHORT).show();
        } else {
            Intent galleryIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            getMainActivity().startActivityForResult(galleryIntent, requestCode);
            // user now provided permission
            // perform function for what you want to achieve
//                }
//            }
        }
    }


//    private void selectPic(int requestCode) {
//
//        if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
//
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    requestCode);
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
//        } else {
//            Log.d("aaaaaa", "requestCODE = " + requestCode);
////            Intent galleryIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
////            startActivityForResult(galleryIntent, requestCode);
//           dispatchTakePictureIntent(requestCode);
//        }


  //  }

    Calendar mcalendar;
    private int day, month, year;

    private void openCalender() {
        mcalendar = Calendar.getInstance();
        day = mcalendar.get(Calendar.DAY_OF_MONTH);
        year = mcalendar.get(Calendar.YEAR);
        month = mcalendar.get(Calendar.MONTH);


        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectpurchaseDate.setText(getDate(year, month + 1, dayOfMonth));
            }

        };
        DatePickerDialog dpDialog = new DatePickerDialog(getContext(), listener, year, month, day);
        dpDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dpDialog.show();

    }

    private String getDate(int year, int month, int dayOfMonth) {
        return year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
    }

    private void sendToServer() {
        HashMap<String, Object> requestData = new HashMap<>();
        ArrayList<HashMap<String, Object>> partList = new ArrayList<>();
        requestData.put("booking_id", eoBooking.bookingID);
        requestData.put("amount_due", 0);
        requestData.put("partner_id", eoBooking.partnerID);
        requestData.put("price_tags", eoBooking.requestType);
        requestData.put("partner_flag", 0);
        requestData.put("spare_shipped", "");
        requestData.put("days", 0);
        requestData.put("service_center_id", getMainActivity().getServiceCenterId());

        requestData.put("reason_text", this.problemdescriptionedittext.getText().toString().trim());
        EOModelNumber eoModelNumber = (EOModelNumber) selectModel.getTag();
        if (eoModelNumber != null) {
            requestData.put("model_number_id", eoModelNumber.id);
        }
        if (this.eoSparePartsOrder != null && this.eoSparePartsOrder.eoSpareParts != null) {
            if (this.eoSparePartsOrder.eoSpareParts.invoice_pic != null) {

                String invoicepic=onCaptureImageResult(this.selectedSerialNoImageUri);
                requestData.put("existing_purchase_invoice", invoicepic);
            } else {
                requestData.put("invoice_number_pic_exist", onCaptureImageResult(this.selectedInvoiceImageUri));
            }
            if (this.eoSparePartsOrder.eoSpareParts.serial_number_pic != null) {
                requestData.put("existing_serial_number_pic", onCaptureImageResult(this.selectedSerialNoImageUri));
            } else {
                requestData.put("serial_number_pic_exist", onCaptureImageResult(this.selectedSerialNoImageUri));
            }
        } else {
            requestData.put("serial_number_pic_exist", onCaptureImageResult(this.selectedSerialNoImageUri));
            requestData.put("invoice_number_pic_exist", onCaptureImageResult(this.selectedInvoiceImageUri));
        }
        requestData.put("model_number", selectModel.getText().toString().trim());
        requestData.put("dop", selectpurchaseDate.getText().toString().trim());
        requestData.put("serial_number", enterSerialNo.getText().toString().trim());
       //   Log.d("aaaaaa", "submit SERAIL NUMBER   = " +enterSerialNo.getText().toString().trim());


        Log.d("aaaaaaa","GSON  = "+BMAGson.store().toJson(requestData));
        for (Map.Entry<Integer, String> entry : partsObject.entrySet()) {
            HashMap<String, Object> selectedPartsMap = BMAGson.store().getObject(HashMap.class, entry.getValue());


            if(!checkSelectAllFieldForSubmit(selectedPartsMap)){
                return;
            }

            partList.add(selectedPartsMap);
          //  Log.d("aaaaaa", "partListGetvALUE  = " + entry.getKey());
        }
        //   Log.d("aaaaaa","partsname  = "+partsMap.get("parts_name"));

       // Log.d("aaaaaa", "partList = " + BMAGson.store().toJson(partList));

        requestData.put("part", partList);
        if(httpRequest!=null){
            this.httpRequest.progress.dismiss();
        }
        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = SparePartsOrderFragment.this;
        this.actionID = "submitSparePartsOrder";
        //Log.d("aaaaaa","aentID = "+MainActivityHelper.applicationHelper().getSharedPrefrences().getString("scAgentID", null));
        //Toast.makeText(getContext(), "submitSparePartsOrder", Toast.LENGTH_SHORT).show();
       httpRequest.execute("submitSparePartsOrder", BMAGson.store().toJson(requestData), MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("scAgentID", null));


//    },


    }

    private boolean isSelectAllField() {


        //  return partsMap != null && partsMap.get("parts_type") != null && (partsMap.get("parts_name") != null || partName.getText().toString().trim().length() > 0) && partsMap.get("defective_front_parts") != null && partsMap.get("defective_back_parts") != null && partsMap.get("quantity") != null;
        if (partsMap == null) {
            Toast.makeText(getContext(), "Select All fields", Toast.LENGTH_SHORT).show();
            return false;
        } else if (partsMap.get("parts_type") == null) {
            if(partsObject.containsKey(partNumberCounter)) {
                Toast.makeText(getContext(), "Part type can't blank", Toast.LENGTH_SHORT).show();
            }
            return false;
        } else if (partsMap.get("parts_name") == null) {
            if (partName.getText().toString().trim().length() > 0) {

                return true;

            }
            if(partsObject.containsKey(partNumberCounter)) {
                Toast.makeText(getContext(), "Part name can't blank", Toast.LENGTH_SHORT).show();
            }
            return false;
        } else if (partsMap.get("defective_front_parts") == null) {
            if(partsObject.containsKey(partNumberCounter)) {
                Toast.makeText(getContext(), "Defective front image can't be blank ", Toast.LENGTH_SHORT).show();
            }
            return false;
        } else if (partsMap.get("defective_back_parts") == null) {
            if(partsObject.containsKey(partNumberCounter)) {
                Toast.makeText(getContext(), "Defective back image can't be blank ", Toast.LENGTH_SHORT).show();
            }
            return false;
        } else if (partsMap.get("quantity") == null) {
            if(partsObject.containsKey(partNumberCounter)) {
                Toast.makeText(getContext(), "Enter quantity", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;

    }

    class CustomeTextWatcher implements TextWatcher {

        EditText editText;
        String key;

        public CustomeTextWatcher(EditText editText) {
            this(editText, "");
        }

        public CustomeTextWatcher(EditText editText, String key) {
            this.editText = editText;
            this.key = key;

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
        @Override
        public void afterTextChanged(Editable s) {
            String value = s.toString();
            int getKey = (int) editText.getTag();
            String getPartString = partsObject.get(getKey);
            Log.d("aaaaaa","vale = "+value);
            if (key.equalsIgnoreCase("quantity")) {
                if (getPartString != null) {
                    HashMap<String, Object> getPartsMap = BMAGson.store().getObject(HashMap.class, getPartString);
                    getPartsMap.put("quantity", value);
                    partsObject.put(getKey, BMAGson.store().toJson(getPartsMap));
                } else {
                    partsMap.put("quantity", value);
                }
            }
            if (key.equalsIgnoreCase("selectedPartName")) {
                if (getPartString != null) {
                    HashMap<String, Object> getPartsMap = BMAGson.store().getObject(HashMap.class, getPartString);
                    getPartsMap.put("parts_name", value);
                    getPartsMap.put("requested_inventory_id", "");
                    partsObject.put(getKey, BMAGson.store().toJson(getPartsMap));
                } else {
                    partsMap.put("parts_name", value);
                    partsMap.put("requested_inventory_id", "");
                }
            }
        }
    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.d("aaaaa","getBitmapURl CALL = ");
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Log.d("aaaaa","input = "+input);
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            int width = myBitmap.getWidth();
            int height = myBitmap.getHeight();

            float bitmapRatio = (float)width / (float) height;
            if (bitmapRatio > 1) {
                width = 500;
                height = (int) (width / bitmapRatio);
            } else {
                height = 500;
                width = (int) (height * bitmapRatio);
            }
            return Bitmap.createScaledBitmap(myBitmap, width, height, true);

            //  return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
//    public static Bitmap getBitmapFromURL(String src) {
//        try {
//            URL url = new URL(src);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            Bitmap myBitmap = BitmapFactory.decodeStream(input);
//            return myBitmap;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    private void setInvoiceAndSerialPic(EOSpareParts eoSpareParts) {
        Log.d("aaaaa","setInvoiceAndSerialPic "+eoSpareParts.serial_number_pic);

        if (eoSpareParts.invoice_pic != null) {
            Picasso.with(getContext()).load(eoSpareParts.invoice_pic).into(invoicePic);
        }
        if (eoSpareParts.serial_number_pic != null) {
            Picasso.with(getContext()).load(eoSpareParts.serial_number_pic).into(serialNoPic);
        }
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //Your code goes here

                    Log.d("aaaaa","serial selevcted no "+Uri.parse(eoSpareParts.serial_number_pic));
                    Log.d("aaaaa","getBitmap "+getBitmapFromURL(eoSpareParts.serial_number_pic));
                    SparePartsOrderFragment.this.selectedSerialNoImageUri = Uri.parse(eoSpareParts.serial_number_pic);
                    //Log.d("aaaaaa","run serialUR i = "+ SparePartsOrderFragment.this.selectedSerialNoImageUri);
                    SparePartsOrderFragment.this.selectedInvoiceImageUri = Uri.parse(eoSpareParts.invoice_pic);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        if (eoSpareParts.serial_number != null)
            enterSerialNo.setText(eoSpareParts.serial_number);
    }

//    private void showWarrantyMessage(String message) {
//        BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, false) {
//            @Override
//            public void onConfirmation(String inputValue) {
//                super.onConfirmation(inputValue);
//              //  isCheckedSpareWarranty = true;
//              //  updateSparePartView();
//
//            }
//        };
//        bmaAlertDialog.show(message);
//    }
//    private void updateSparePartView(){
//        this.serialNoInvoicePhotoLayout.setVisibility(View.VISIBLE);
//        this.ProblemdescriptionCardView.setVisibility(View.VISIBLE);
//        this.serialNoLayout.setVisibility(View.VISIBLE);
//        this.addMorePart.setVisibility(View.VISIBLE);
//        this.checkOut.setText(getString(R.string.submit));
//        this.addMorePart.setVisibility(View.VISIBLE);
//        selectModel.setText("");
//        selectpurchaseDate.setText("");
//        this.parentLayout.setVisibility(View.VISIBLE);
//    }

    private boolean checkQuantity() {
//        String quantity=selectQuantity.getText().toString().trim();
//        if(partsMap==null){
//            return false;
//        }
        String quantity = (String) partsMap.get("quantity");
        if (this.eoSparePartsOrder != null && this.eoSparePartsOrder.getPartOnModel) {

            if (quantity.length() > 0) {
                int selectqnty = Integer.valueOf(quantity);
                if (selectqnty == 0) {
                    Toast.makeText(getContext(), "Quantity should not be 0", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (maxCountity >= selectqnty) {
                    return true;
                } else {
                    Toast.makeText(getContext(), "Quantity should not be maximum than actual quantity is " + maxCountity, Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(getContext(), "Please enter quantity", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            if (quantity!=null && quantity.trim().length() > 0) {
                int selectqnty = Integer.valueOf(quantity);
                if(maxCountity>=selectqnty) {
                    return true;
                }else{
                    Toast.makeText(getContext(), "Quantity should not be maximum than actual quantity is " + maxCountity, Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(getContext(), "Please enter quantity", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }
    private boolean checkSelectAllFieldForSubmit(HashMap<String, Object> selectPartsMap) {
        String quantity = (String) selectPartsMap.get("quantity");
        String maxQuantity = (String) selectPartsMap.get("maxQuantity");

        if(quantity.length()==0){
            Toast.makeText(getContext(), "Please enter quantity", Toast.LENGTH_SHORT).show();
            return false;
        }
        int quantityVal = Integer.valueOf(quantity);
        int maxQuantityVal;
        if(maxQuantity==null){
            maxQuantityVal=1;
        }else {
            maxQuantityVal = Integer.valueOf(maxQuantity);
        }
        //  return partsMap != null && partsMap.get("parts_type") != null && (partsMap.get("parts_name") != null || partName.getText().toString().trim().length() > 0) && partsMap.get("defective_front_parts") != null && partsMap.get("defective_back_parts") != null && partsMap.get("quantity") != null;
        if (selectPartsMap == null) {
            Toast.makeText(getContext(), "Select All fields", Toast.LENGTH_SHORT).show();
            return false;
        } else if (selectPartsMap.get("parts_type") == null) {
            Toast.makeText(getContext(), "Part type can't blank", Toast.LENGTH_SHORT).show();
            return false;
        } else if (selectPartsMap.get("parts_name") == null || selectPartsMap.get("parts_name").toString().trim().length()==0) {

            Toast.makeText(getContext(), "Part name can't blank", Toast.LENGTH_SHORT).show();
            return false;
        }  else if (quantity == null || quantity.trim().length() == 0) {
            Toast.makeText(getContext(), "Please enter quantity", Toast.LENGTH_SHORT).show();
            return false;
        }else if (quantityVal == 0) {
            Toast.makeText(getContext(), "Quantity should not be 0", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (maxQuantityVal < quantityVal) {
            Toast.makeText(getContext(), "Quantity should not be maximum than actual quantity is " + maxQuantityVal, Toast.LENGTH_SHORT).show();
            return false;

        }
        return true;

    }
    private void showQuantity(){
        //  int maxQuantity=
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG" + timeStamp + "_";
        File storageDir =new File(Environment.getExternalStorageDirectory() + "/AroundSerialNO/");
        if(!storageDir.exists()){
            storageDir.mkdirs();
            //("/storage/emulated/0/AroundSerialNO/"); //getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    static final int REQUEST_TAKE_PHOTO = 1;

    public void dispatchTakePictureIntent(int requestCode, Intent takePictureIntent) {
        Log.d("aaaaaa","takepictureDispatch  ");
     //   Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       // Intent takePictureIntent=new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(photoFile);// FileProvider.getUriForFile(getContext(), "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                getMainActivity().startActivityForResult(takePictureIntent, requestCode);
            }
        }
    }

    private void inflateDialog(Bitmap imageBitmap){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.imagedialog, null);
        ImageView image=alertLayout.findViewById(R.id.showImage);
        image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setImageBitmap(imageBitmap);
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.myFullscreenAlertDialogStyle);
        alert.setTitle("serialimage");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        AlertDialog dialog = alert.create();
        dialog.show();
    }
    //this method is used to submit request with selected data
    private void submitRequestToServer(){

        HashMap<String, Object> requestData = new HashMap<>();
        requestData.put("spares_data",cosumptionArray);
        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = SparePartsOrderFragment.this;
        this.actionID="submitPrevPartsConsumption";
        //openSparePartOrderPage(cosumptionArray);
          httpRequest.execute(this.actionID, BMAGson.store().toJson(requestData));

    }
}
