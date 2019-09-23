package com.around.engineerbuddy.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.BMAmplitude;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.component.BMACardView;
import com.around.engineerbuddy.component.BMAFontViewField;
import com.around.engineerbuddy.component.BMASelectionDialog;
import com.around.engineerbuddy.entity.BMAUiEntity;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.entity.EOModelNumber;
import com.around.engineerbuddy.entity.EOPartName;
import com.around.engineerbuddy.entity.EOPartType;
import com.around.engineerbuddy.entity.EOSparePartsOrder;
import com.around.engineerbuddy.util.BMAConstants;
import com.around.engineerbuddy.util.BMAUIUtil;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    BMAFontViewField selectPurchaseDateIcon, modelDropDownIcon;
    TextView selectpurchaseDate;
    EditText selectModel, enterSerialNo, problemdescriptionedittext;
    private EOBooking eoBooking;
    EOSparePartsOrder eoSparePartsOrder;
    ArrayList<EOPartType> partTypeList;
    ArrayList<EOPartName> partNameList;
    HashMap<String, Object> requestData = new HashMap<>();
    ArrayList<HashMap<String, Object>> partList = new ArrayList<>();
    HashMap<String, Object> partsMap = new HashMap<>();
    String SN_DIRECTORY = Environment.getExternalStorageDirectory() + "/AroundSerialNO/";
    HashMap<Integer, String> partsObject = new HashMap<>();

    BMACardView ProblemdescriptionCardView;
    EOModelNumber modelNumber;
    LinearLayout selectModelLayout;
    //   boolean isCheckedSpareWarranty;
    // String selectModelNumberCallType;
    String selectPOD;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.eoBooking = getArguments().getParcelable("eoBooking");
        //  selectModelNumberCallType = getArguments().getString("modelNo");
        selectPOD = getArguments().getString("pod");
        modelNumber = getArguments().getParcelable("modelNumber");
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
                selectPic(1);
            }
        });
        this.view.findViewById(R.id.invoicePhotoLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPic(2);
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
        this.getModelData();
        if (modelNumber != null) {
            selectModel.setTag(modelNumber);
        }
        //   this.getPartTypeData();
        return this.view;
    }

    String actionID;

    private void getModelData() {
        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = SparePartsOrderFragment.this;
        this.actionID = "engineerSparePartOrder";
        httpRequest.execute(actionID, eoBooking.partnerID, eoBooking.serviceID);

    }

    private void getPartTypeData() {
        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = SparePartsOrderFragment.this;
        this.actionID = "partTypeOnModelNumber";
        httpRequest.execute(actionID, ((EOModelNumber) this.selectModel.getTag()).id);
    }

    private void getPartNameData() {
        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = SparePartsOrderFragment.this;
        this.actionID = "sparePartName";
        EOPartType eoPartType = ((EOPartType) selectPartType.getTag());
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
        // super.processFinish(response);
        Log.d("bbbbbbb", "response  = " + response);
        httpRequest.progress.dismiss();
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
                    if (this.actionID.equalsIgnoreCase("engineerSparePartOrder")) {
                        String responseData = (new JSONObject(jsonObject.getString("response")).getString("sparePartsOrder"));
                        this.eoSparePartsOrder = BMAGson.store().getObject(EOSparePartsOrder.class, responseData);
                        if (this.eoSparePartsOrder != null) {

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
                        String responseData = (new JSONObject(jsonObject.getString("response")).getString("partTypeList"));
                        partTypeList = BMAGson.store().getList(EOPartType.class, responseData);
                    } else if (this.actionID.equalsIgnoreCase("sparePartName")) {
                        partNameList = BMAGson.store().getList(EOPartName.class, jsonObject.getString("response"));
                        if (partNameList == null || partNameList.size() == 0) {
                            selectedPartName.setEnabled(true);
                            selectedPartName.setHint(getString(R.string.enterPartName));
                            selectedPartName.setTag(partNumberCounter);
                            selectedPartName.addTextChangedListener(new CustomeTextWatcher(selectedPartName, "selectedPartName"));
                            selectedPartNameDropDownIcon.setVisibility(View.INVISIBLE);
                            // selectedPartNameDropDownIcon.setEnabled(false);

                        }
                    } else if (this.actionID.equalsIgnoreCase("submitSparePartsOrder")) {
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
                    BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {
                        @Override
                        public void onWarningDismiss() {
                            super.onWarningDismiss();
                        }

                    };
                    bmaAlertDialog.show(jsonObject.getString("result"));
                }
            } catch (Exception e) {

            }
        }
    }

    EditText selectedPartName;
    BMAFontViewField partNameDropDownIcon;
    BMAFontViewField selectedPartNameDropDownIcon;
    TextView selectPartType, selectQuantity, spareAmount;
    EditText selectPartName;

    private void addPartView() {
        partNumberCounter++;
        View childView = this.inflater.inflate(R.layout.spare_parts_items_row, null, false);
        BMAFontViewField partTypeDropDownIcon = childView.findViewById(R.id.partTypeDropDownIcon);
        partNameDropDownIcon = childView.findViewById(R.id.partNameDropDownIcon);
        TextView partNumber = childView.findViewById(R.id.rowPartNumber);
        selectPartType = childView.findViewById(R.id.selectPartType);
        BMAFontViewField deleteIcon = childView.findViewById(R.id.deleteIcon);
        selectPartName = childView.findViewById(R.id.selectPartName);
        selectQuantity = childView.findViewById(R.id.selectQuantity);
        spareAmount = childView.findViewById(R.id.spareAmount);
        selectPartName.setEnabled(false);
        LinearLayout spareRowLayout = childView.findViewById(R.id.spareRowLayout);
        LinearLayout spareNumberLayout = childView.findViewById(R.id.spareNumberLayout);
        LinearLayout productStatusLayout = childView.findViewById(R.id.productStatusLayout);
        int dialogRadius = 40;
        BMAUIUtil.setBackgroundRound(spareNumberLayout, R.color.colorPrimary, new float[]{0, 0, dialogRadius, dialogRadius, dialogRadius, dialogRadius, 0, 0});
        BMAUIUtil.setBackgroundRound(productStatusLayout, R.color.white, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        BMAFontViewField expandArrowIcon = childView.findViewById(R.id.expandArrowIcon);

        partTypeDropDownIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPartName = selectPartName;
                selectedPartNameDropDownIcon = partNameDropDownIcon;
                openPartTypeSelectionPopUp(selectPartType);
            }
        });
        partNameDropDownIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPartNameSelectionPopUp(selectPartName, selectQuantity, spareAmount);
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
                parentLayout.removeView(childView);
                int partKey = (int) deleteIcon.getTag();
                partsObject.remove(partKey);
            }
        });
        LinearLayout backDefectiveImageLayout = childView.findViewById(R.id.backDefectiveImageLayout);
        LinearLayout frontDefectiveImageLayout = childView.findViewById(R.id.frontDefectiveImageLayout);
        partNumber.setText("" + partNumberCounter);
        frontDefectiveImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frontDefectivePart = childView.findViewById(R.id.frontDefectivePartImage);
                selectPic(3);
            }
        });
        backDefectiveImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backDefectivePart = childView.findViewById(R.id.backDefectivePartImage);
                selectPic(4);
            }
        });
        partsObject.put(partNumberCounter, null);
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
                        addPartView();
                        return;
                    }
                    Toast.makeText(getContext(), getString(R.string.selectAllFieldsValidation), Toast.LENGTH_SHORT).show();
                    return;
                }
                partsMap.put("part_warranty_status", 1);
                partsMap.put("quantity", 1);
                HashMap<String, Object> partsMapObj = partsMap;
                partsObject.put(partNumberCounter, BMAGson.store().toJson(partsMapObj));
                frontDefectiveBitmap = null;
                backDefectiveBitmap = null;
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
                if (!((selectModel.getTag() != null || selectModel.getText().toString().trim().length() > 0) && enterSerialNo.getText().toString().trim().length() > 0 && selectpurchaseDate.getText().toString().trim().length() > 0 && this.serialNoPicBitmap != null && this.invoicePicBitmap != null && problemdescriptionedittext.getText().toString().trim().length() > 0)) {
                    Toast.makeText(getContext(), getString(R.string.selectAllFieldsValidation), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isSelectAllField()) {
                    if (!partsObject.containsKey(partNumberCounter)) {
                        sendToServer();
                        return;
                    }
                    Toast.makeText(getContext(), getString(R.string.selectAllFieldsValidation), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (partsObject.containsKey(partNumberCounter)) {
                    partsMap.put("part_warranty_status", 1);
                    partsMap.put("quantity", 1);
                    partsObject.put(partNumberCounter, BMAGson.store().toJson(partsMap));
                }

                sendToServer();

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


    private void openPartTypeSelectionPopUp(TextView selectPartType) {

        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getPartTypeList(selectPartType)) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if (selectedItems != null) {
                    selectPartType.setText(selectedItems.getDetail1());
                    selectPartType.setTag(selectedItems.tag);
                    partsMap.put("parts_type", selectedItems.getDetail1());
                    getPartNameData();
                }

            }
        };
        bmaSelectionDialog.setTitle(getString(R.string.selectPartType));
        bmaSelectionDialog.show();
    }

    private void openPartNameSelectionPopUp(EditText selectPartName, TextView selectQuantity, TextView spareAmount) {

        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getPartNameList(selectPartName)) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if (selectedItems != null) {
                    selectPartName.setText(selectedItems.getDetail1());
                    selectPartName.setTag(selectedItems.tag);

                    EOPartName eoPartName = (EOPartName) selectedItems.tag;
                    spareAmount.setText("₹ " + eoPartName.amount);
                    selectQuantity.setText(eoPartName.inventory_id);
                    partsMap.put("parts_name", selectedItems.getDetail1());
                    partsMap.put("requested_inventory_id", eoPartName.inventory_id);
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

    Bitmap serialNoPicBitmap, invoicePicBitmap, backDefectiveBitmap, frontDefectiveBitmap;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || data.getExtras() == null || data.getExtras().get("data") == null) {
            return;
        }
        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
        switch (requestCode) {
            case 1:
                this.serialNoPicBitmap = imageBitmap;
                serialNoPic.setImageBitmap(imageBitmap);
                break;
            case 2:
                this.invoicePicBitmap = imageBitmap;
                invoicePic.setImageBitmap(imageBitmap);

                break;
            case 3:
                Log.d("aaaaaa", "defective_front_parts = " + onCaptureImageResult(imageBitmap));
                partsMap.put("defective_front_parts", onCaptureImageResult(imageBitmap));

                frontDefectiveBitmap = imageBitmap;

                frontDefectivePart.setImageBitmap(imageBitmap);
                break;
            case 4:

                backDefectiveBitmap = imageBitmap;
                partsMap.put("defective_back_parts", onCaptureImageResult(imageBitmap));
                Log.d("aaaaaa", "defective_back_parts = " + onCaptureImageResult(imageBitmap));

                backDefectivePart.setImageBitmap(imageBitmap);

                break;

        }
    }

    public String onCaptureImageResult(Bitmap thumbnail) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 90, bytes);
        String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        String serialNoPath = eoBooking.bookingID + "_" + pic_name + ".png";
        File destination = new File(SN_DIRECTORY, serialNoPath);
      //  FileOutputStream fo;
        try {

               boolean isFile=destination.createNewFile();
               if(!isFile){
                   destination.mkdirs();
               }
                FileOutputStream fo = new FileOutputStream(destination);
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
                    startActivityForResult(galleryIntent, requestCode);
                    // user now provided permission
                    // perform function for what you want to achieve
//                }
//            }
        }
    }


    private void selectPic(int requestCode) {

        if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    requestCode);
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        } else {
            Log.d("aaaaaa","requestCODE = "+requestCode);
            Intent galleryIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(galleryIntent, requestCode);
            // user already provided permission
            // perform function for what you want to achieve
        }


    }

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
        requestData.put("booking_id", eoBooking.bookingID);
        requestData.put("amount_due", 0);
        requestData.put("partner_id", eoBooking.partnerID);
        requestData.put("price_tags", eoBooking.requestType);
        requestData.put("partner_flag", 0);
        requestData.put("spare_shipped", "");
        requestData.put("days", 0);
        requestData.put("service_center_id", getMainActivity().getServiceCenterId());

        requestData.put("reason", this.problemdescriptionedittext.getText().toString().trim());
        EOModelNumber eoModelNumber = (EOModelNumber) selectModel.getTag();
        if (eoModelNumber != null) {
            requestData.put("model_number_id", eoModelNumber.id);
        }
        requestData.put("model_number", selectModel.getText().toString().trim());
        requestData.put("dop", selectpurchaseDate.getText().toString().trim());
        requestData.put("serial_number", enterSerialNo.getText().toString().trim());
        requestData.put("serial_number_pic_exist", onCaptureImageResult(this.serialNoPicBitmap));
        requestData.put("invoice_number_pic_exist", onCaptureImageResult(this.invoicePicBitmap));
        for (Map.Entry<Integer, String> entry : partsObject.entrySet()) {
            partList.add(BMAGson.store().getObject(HashMap.class, entry.getValue()));
            //   Log.d("aaaaaa", "partListGetvALUE  = " + entry.getValue());
        }
        //   Log.d("aaaaaa","partsname  = "+partsMap.get("parts_name"));

        Log.d("aaaaaa", "partList = " + BMAGson.store().toJson(partList));

        requestData.put("part", partList);
        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = SparePartsOrderFragment.this;
        this.actionID = "submitSparePartsOrder";
        httpRequest.execute("submitSparePartsOrder", BMAGson.store().toJson(requestData));


//    },


    }

    private boolean isSelectAllField() {

        return partsMap != null && partsMap.get("parts_type") != null && (partsMap.get("parts_name") != null || selectPartName.getText().toString().trim().length() > 0) && partsMap.get("defective_front_parts") != null && partsMap.get("defective_back_parts") != null;

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
            partsMap.put("parts_name", value);
            partsMap.put("requested_inventory_id", "");

        }
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

}
