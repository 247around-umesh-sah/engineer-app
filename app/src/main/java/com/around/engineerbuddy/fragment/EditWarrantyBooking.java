package com.around.engineerbuddy.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.adapters.BMARecyclerAdapter;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.component.BMAFontViewField;
import com.around.engineerbuddy.component.BMASelectionDialog;
import com.around.engineerbuddy.entity.BMAUiEntity;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.entity.EOCompleteProductQuantity;
import com.around.engineerbuddy.entity.EOCompleteProductdetail;
import com.around.engineerbuddy.entity.EOModelNumber;
import com.around.engineerbuddy.entity.EOSparePartWarrantyChecker;
import com.around.engineerbuddy.entity.EOSpareParts;
import com.around.engineerbuddy.entity.EOWarrantyChecker;
import com.around.engineerbuddy.util.BMAConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class EditWarrantyBooking extends BMAFragment implements View.OnClickListener {

    LinearLayout selectModelLayout, selectPurchaseDateLayout;
    TextView selectPurchaseDate, selectModelNumber;
    EditText selectModel;
    BMAFontViewField modelDropDownIcon;
    RecyclerView recyclerView;
    HttpRequest httpRequest;

    Button checkWarrantyButton;
    EOBooking eoBooking;
    EOSparePartWarrantyChecker eoSparePartWarrantyChecker;
    ArrayList<EOWarrantyChecker> callTypeList = new ArrayList<>();

    private String actionID;
    String purchaseDate;
    EOModelNumber selectedEOModelNumber;
    EOCompleteProductdetail selectedCompleteDetail;
    boolean isCompletePage;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.eoBooking = getArguments().getParcelable("eoBooking");
        this.isCompletePage = getArguments().getBoolean("isCompletePage");
        selectedCompleteDetail = getArguments().getParcelable("productDetail");
        selectedEOModelNumber = getArguments().getParcelable("modelNumber");
        this.purchaseDate = getArguments().getString("pod");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.edit_warranty_booking, container, false);
        this.view.findViewById(R.id.cancelButton).setVisibility(View.GONE);
        this.checkWarrantyButton = this.view.findViewById(R.id.submitButton);
        this.recyclerView = this.view.findViewById(R.id.callTypeRecyclerView);
        this.selectModel = this.view.findViewById(R.id.selectModel);
        this.selectPurchaseDate = this.view.findViewById(R.id.selectpurchaseDate);
        this.modelDropDownIcon = this.view.findViewById(R.id.modelDropDownIcon);
        this.selectModel.setEnabled(false);
        this.checkWarrantyButton.setText("Check Warranty");
        this.modelDropDownIcon.setOnClickListener(this);
        selectPurchaseDateLayout = this.view.findViewById(R.id.selectPurchaseDateLayout);
        selectPurchaseDateLayout.setOnClickListener(this);
        this.checkWarrantyButton.setOnClickListener(this);
        if (purchaseDate != null) {
            selectPurchaseDate.setText(purchaseDate);
        }
        if (selectedEOModelNumber != null) {
            selectModel.setTag(selectedEOModelNumber);
        }
        // if(this.actionID==null || !this.actionID.equalsIgnoreCase("submitWarrantyCheckerAndEditCallType")) {
        this.getRequest();
        //  }


        return this.view;
    }

    private void dataToView() {
        BMARecyclerAdapter bmaRecyclerAdapter = new BMARecyclerAdapter(getContext(), callTypeList, recyclerView, this, R.layout.edit_call_type);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bmaRecyclerAdapter);
    }

    private void getRequest() {
        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = EditWarrantyBooking.this;
        // Log.d("aaaaaa", "partnerID = " + eoBooking.partnerID + "        " + eoBooking.serviceID);
        this.actionID = "warrantyCheckerAndCallTypeData";
        httpRequest.execute(this.actionID, this.eoBooking.bookingID, this.eoBooking.partnerID, this.eoBooking.serviceID,eoBooking.primaryContact);

    }

    @Override
    public void processFinish(String response) {
        Log.d("aaaaaa", "response Warranty = " + response);

        //System.out.println(response);

        httpRequest.progress.dismiss();
        if (response.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(response);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                if (statusCode.equals("0000")) {
                    String res = jsonObject.getString("response");
                    if (this.actionID.equalsIgnoreCase("submitWarrantyCheckerAndEditCallType")) {
                        if (res.length() == 0 || !res.contains("warranty_flag")) {
                            BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {


                                @Override
                                public void onWarningDismiss() {
                                    super.onWarningDismiss();
                                }
                            };
                            bmaAlertDialog.show(jsonObject.getString("result"));
                            return;
                        }
                        JSONObject resObject = new JSONObject(res);
                        String flag = resObject.getString("warranty_flag");
                        if (flag == null || flag.equalsIgnoreCase("1")) {
                            BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {


                                @Override
                                public void onWarningDismiss() {
                                    super.onWarningDismiss();
                                }
                            };
                            bmaAlertDialog.show(jsonObject.getString("result"));
                            //return;
                        } else if (flag.equalsIgnoreCase("0")) {
                            if (isCompletePage) {
                                openProductDetialPage();
                            } else {
                                openSparePartPage();
                            }
                        } else if (flag.equalsIgnoreCase("2")) {
                            BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), true, false) {


                                @Override
                                public void onConfirmation() {
                                    super.onConfirmation();
                                    if (isCompletePage) {
                                        openProductDetialPage();
                                    } else {
                                        openSparePartPage();
                                    }
                                }

                                @Override
                                public void cancel() {
                                    super.cancel();
                                }
                            };
                            bmaAlertDialog.show(jsonObject.getString("result"));
                        }


                    } else {


                        this.eoSparePartWarrantyChecker = BMAGson.store().getObject(EOSparePartWarrantyChecker.class, res);
                        bindDataTOArray();
                    }
//                    //  this.bookingInfo = BMAGson.store().getObject(BookingInfo.class, jsonObject);
//                    if (this.bookingInfo != null) {
//                        this.dataToView(true);
//                        if(this.getBookingList().size()==0) {
//                            noDataToDisplayLayout.setVisibility(View.VISIBLE);
//                        }
//
//                    }
//

                } else if (this.actionID.equalsIgnoreCase("submitWarrantyCheckerAndEditCallType")) {
                    JSONObject resJson = new JSONObject(jsonObject.getString("response"));
                    String flag = resJson.getString("warranty_flag");
                    if (flag == null || flag.equalsIgnoreCase("1")) {
                        BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {


                            @Override
                            public void onWarningDismiss() {
                                super.onWarningDismiss();
                            }
                        };
                        bmaAlertDialog.show(jsonObject.getString("result"));
                        //return;
                    } else if (flag.equalsIgnoreCase("0")) {
                        if (isCompletePage) {
                            openProductDetialPage();
                        } else {
                            openSparePartPage();
                        }
                    } else if (flag.equalsIgnoreCase("2")) {
                        BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), true, true) {


                            @Override
                            public void onConfirmation() {
                                super.onConfirmation();
                                if (isCompletePage) {
                                    openProductDetialPage();
                                } else {
                                    openSparePartPage();
                                }
                            }


                        };
                        bmaAlertDialog.show(jsonObject.getString("result"));
                    }
//                    BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {
//
//
//                        @Override
//                        public void onWarningDismiss() {
//                            super.onWarningDismiss();
//                        }
//                    };
//                    bmaAlertDialog.show(jsonObject.getString("result"));
                }
                // {"data":{"code":"0055","result":"Not Allow to select multiple different type of service category"}}
            } catch (JSONException e) {
                httpRequest.progress.dismiss();
                BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {


                    @Override
                    public void onWarningDismiss() {
                        super.onWarningDismiss();
                    }
                };
                bmaAlertDialog.show("Server Issue");
            }
        } else {
            httpRequest.progress.dismiss();
            BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {


                @Override
                public void onWarningDismiss() {
                    super.onWarningDismiss();
                }
            };
            bmaAlertDialog.show("Server Error");//getString(R.string.somethingWentWrong));
        }
    }

    @Override
    public <T> void createRow(RecyclerView.ViewHolder viewHolder, View itemView, T rowObject, int position) {
        EOWarrantyChecker eoWarrantyChecker = (EOWarrantyChecker) rowObject;
        LinearLayout requestTypeLayout = itemView.findViewById(R.id.edit_request_Type_Layout);
        View headerView = itemView.findViewById(R.id.headerDevider);
        if (eoWarrantyChecker.isHeader) {
            requestTypeLayout.setVisibility(View.GONE);
            headerView.setVisibility(View.VISIBLE);
        } else {
            requestTypeLayout.setVisibility(View.VISIBLE);
            headerView.setVisibility(View.GONE);
        }
        TextView callTypeText = itemView.findViewById(R.id.callTypeText);
        TextView callTypeCustomerCharges = itemView.findViewById(R.id.callTypeCustomerCharges);
        CheckBox checkbox = itemView.findViewById(R.id.editTypeCheckBox);
        callTypeText.setText(eoWarrantyChecker.serviceCategory);
        callTypeCustomerCharges.setText(eoWarrantyChecker.amount);
        checkbox.setChecked(eoWarrantyChecker.isCheckBoxChecked);
        checkbox.setTag(eoWarrantyChecker);
        if(eoWarrantyChecker!=null && eoWarrantyChecker.serviceCategory!=null) {
            checkbox.setEnabled(eoWarrantyChecker.serviceCategory.equalsIgnoreCase("Spare Parts") ? false : true);
        }
        if(this.eoSparePartWarrantyChecker.parents!=null && this.eoSparePartWarrantyChecker.parents.size()>0){
            checkbox.setEnabled(false);
        }
        if(eoWarrantyChecker.serviceCategory!=null && eoWarrantyChecker.serviceCategory.equalsIgnoreCase("Repeat Booking") ){
            if(this.eoSparePartWarrantyChecker.parents!=null && this.eoSparePartWarrantyChecker.parents.size()==0 ){
                checkbox.setEnabled(false);
            }else{
                checkbox.setEnabled(true);
            }
        }
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                EOWarrantyChecker selectObj = (EOWarrantyChecker) checkBox.getTag();
                selectObj.isCheckBoxChecked = checkBox.isChecked();
                //  checkBox.setChecked(checkBox.isChecked());
            }
        });
        if(this.eoSparePartWarrantyChecker.eoWarrantyCheckerBookingDetail.repeat_booking_flag){
            checkbox.setEnabled(false);
        }


    }

    HashMap<String, ArrayList<EOWarrantyChecker>> unitDetailCallTypeMap;//=new HashMap<>();

    private void bindDataTOArray() {
        callTypeList = new ArrayList<>();
        if (this.eoSparePartWarrantyChecker.eoSpareParts != null && this.eoSparePartWarrantyChecker.eoSpareParts.model_number != null) {
            selectModel.setEnabled(false);
            selectPurchaseDateLayout.setEnabled(false);
            this.modelDropDownIcon.setVisibility(View.GONE);
            selectModel.setText(this.eoSparePartWarrantyChecker.eoSpareParts.model_number);
            EOModelNumber eoModelNumber = new EOModelNumber();
            eoModelNumber.modelNumber = this.eoSparePartWarrantyChecker.eoSpareParts.model_number;
            eoModelNumber.id = "";
            selectModel.setTag(eoModelNumber);
            selectPurchaseDate.setText(this.eoSparePartWarrantyChecker.eoSpareParts.date_of_purchase);
        } else if (selectedEOModelNumber != null) {
            selectModel.setText(selectedEOModelNumber.modelNumber);
            selectModel.setTag(selectedEOModelNumber);
            // selectModel.setEnabled(false);
            // this.modelDropDownIcon.setVisibility(View.GONE);
            if (selectPurchaseDate != null) {
                selectPurchaseDate.setText(purchaseDate);
                // selectPurchaseDateLayout.setEnabled(false);
            }

        } else if (this.eoSparePartWarrantyChecker.modelList == null || this.eoSparePartWarrantyChecker.modelList.size() == 0) {
            selectModel.setEnabled(true);
            this.selectModel.setHint("Enter Model Number");
            this.modelDropDownIcon.setVisibility(View.GONE);
            this.selectModel.addTextChangedListener(new CustomeTextWatcher(this.selectModel));
        }
//        else {
//
//            selectModel.setEnabled(false);
//        }
        int counter = 0;
        unitDetailCallTypeMap = new HashMap<>();
        for (ArrayList<EOCompleteProductQuantity> priceArray : this.eoSparePartWarrantyChecker.eoWarrantyCheckerBookingDetail.prices) {
            ArrayList<EOWarrantyChecker> brandIDArray = new ArrayList<>();
            if (counter > 0) {
                EOWarrantyChecker eoWarrantyChecker1 = new EOWarrantyChecker();
                eoWarrantyChecker1.isHeader = true;
                callTypeList.add(eoWarrantyChecker1);
            }
            for (EOCompleteProductQuantity eoCompleteProductQuantity : priceArray) {


                EOWarrantyChecker eoWarrantyChecker = new EOWarrantyChecker();
                if (this.eoSparePartWarrantyChecker.eoWarrantyCheckerBookingDetail.isRepeat != null) {
                    eoWarrantyChecker.isCheckBoxChecked = true;
                }
                eoWarrantyChecker.serviceCategory = eoCompleteProductQuantity.serviceCategory;
                eoWarrantyChecker.id = eoCompleteProductQuantity.id;
                for (EOCompleteProductQuantity unitDetailQuantity : this.eoSparePartWarrantyChecker.eoWarrantyCheckerBookingDetail.unitDetailArray.get(counter).quantity) {

                    if (unitDetailQuantity.price_tags.equalsIgnoreCase(eoCompleteProductQuantity.serviceCategory)) {
                        eoWarrantyChecker.amount = unitDetailQuantity.amountDue;
                        eoWarrantyChecker.isCheckBoxChecked = true;

                        break;
                    }
                }
                if (eoWarrantyChecker.amount == null) {
                    eoWarrantyChecker.amount = eoCompleteProductQuantity.amountDue;
                }

                callTypeList.add(eoWarrantyChecker);
                //   brandIDArray=new ArrayList<>();
                brandIDArray = callTypeList;
            }

            unitDetailCallTypeMap.put(this.eoSparePartWarrantyChecker.eoWarrantyCheckerBookingDetail.unitDetailArray.get(counter).brand_id, brandIDArray);
            counter++;

        }
        dataToView();

    }

    private void openModelSelectionPopUp() {
        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getModelList(selectModel), true) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if (selectedItems != null) {
                    // partsObject.get(v.getTag()).put("model_number", selectedItems.getDetail1());
                    selectModel.setText(selectedItems.getDetail1());
                    selectModel.setTag(selectedItems.tag);
                    selectedEOModelNumber = (EOModelNumber) selectedItems.tag;

//                    selectedCompleteDetail.getbookingProductUnit().quantity.get(0).model_number = selectedItems.getDetail1();
//                    selectedCompleteDetail.getbookingProductUnit().quantity.get(0).model_number_id = ((EOModelNumber) selectedItems.tag).id;
                    //Log.d("aaaaa", "drop MODEL NMBR" +selectedItems.getDetail1()+"      selectedMODEL =  "+selectedCompleteDetail.eoCompleteProductQuantity().model_number);


//                     getPartTypeData();
                }

            }
        };
        bmaSelectionDialog.setTitle(getString(R.string.selectModelNumber));
        bmaSelectionDialog.show();
    }

    private ArrayList<BMAUiEntity> getModelList(TextView selectModel) {
        ArrayList<BMAUiEntity> bmaUiEntityArrayList = new ArrayList<>();
        if (this.eoSparePartWarrantyChecker == null || this.eoSparePartWarrantyChecker.modelList == null) {
            return bmaUiEntityArrayList;
        }
        for (EOModelNumber eoModelNumber : this.eoSparePartWarrantyChecker.modelList) {
            BMAUiEntity bmaUiEntity = new BMAUiEntity();
            bmaUiEntity.setDetail1(eoModelNumber.modelNumber);
            bmaUiEntity.tag = eoModelNumber;
            if (selectModel.getTag() != null && ((EOModelNumber) selectModel.getTag()).id.equalsIgnoreCase(eoModelNumber.id)) {
                bmaUiEntity.setChecked(true);
            }
            bmaUiEntityArrayList.add(bmaUiEntity);
        }
        return bmaUiEntityArrayList;
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

                selectPurchaseDate.setText(getDate(year, month + 1, dayOfMonth));
                purchaseDate = selectPurchaseDate.getText().toString().trim();
                //  selectedCompleteDetail.getbookingProductUnit().purchase_date = selectDate.getText().toString().trim();
            }

        };
        DatePickerDialog dpDialog = new DatePickerDialog(getContext(), listener, year, month, day);
        //dpDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dpDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dpDialog.show();

    }

    private String getDate(int year, int month, int dayOfMonth) {
        return year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
        // return (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth) + "-" + (month <= 9 ? "0" + month : month) + "-" + year;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modelDropDownIcon:
                openModelSelectionPopUp();
                break;
            case R.id.selectPurchaseDateLayout:
                openCalender();
                break;
            case R.id.submitButton:
                submitProcess();
        }
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

            if (eoSparePartWarrantyChecker.modelList == null || eoSparePartWarrantyChecker.modelList.size() == 0) {
                selectModel.setTag(new EOModelNumber());
            }
            //  int getKey = (int) editText.getTag();

//            EOCompleteProductQuantity selectCompleteProduct = selectedCompleteDetail.getbookingProductUnit().quantity.get(getKey);
//            if (key.equalsIgnoreCase("selectModel")) {
//                selectedCompleteDetail.getbookingProductUnit().quantity.get(0).model_number = value;
//            }
        }
    }


    private void submitProcess() {

        if (!isSelectAllField()) {
            return;
        }
        if (!checkCorrectSelection()) {
            Toast.makeText(getContext(), "Not Allow to select multiple different type of service category", Toast.LENGTH_SHORT).show();
            return;
        }
//        int counter = 0, brandUnitCounter = 0;
//
//        HashMap<String, Object> mainRequest = new HashMap<>();
//        ArrayList<Object> brandUnitList = new ArrayList<>();
//        HashMap<String, Object> AroundNetmainRequest = new HashMap<>();
//        ArrayList<Object> aroundNetBrandUnitList = new ArrayList<>();
//        // for(EOCompleteBookingProductUnit bookingUnitArray : this.eoSparePartWarrantyChecker.eoWarrantyCheckerBookingDetail.unitDetailArray) {
//        HashMap<String, Object> brandUnitMap = new HashMap<>();
//        HashMap<String, Object> aroundNetbrandUnitMap = new HashMap<>();
//
//        for (EOCompleteBookingProductUnit bookingUnitArray1 : this.eoSparePartWarrantyChecker.eoWarrantyCheckerBookingDetail.unitDetailArray) {
//
//            HashMap<String, Object> unitMap = new HashMap<>();
//            ArrayList<Object> unitList = new ArrayList<>();
//            HashMap<String, Object> aroundNetunitMap = new HashMap<>();
//            ArrayList<Object> aroundNetunitList = new ArrayList<>();
//
//
//            for (ArrayList<EOCompleteProductQuantity> priceArray : this.eoSparePartWarrantyChecker.eoWarrantyCheckerBookingDetail.prices) {
//                for (EOCompleteProductQuantity eoCompleteProductQuantity : priceArray) {
//                    ArrayList<String> pricesIDArray = new ArrayList<>();
//                    HashMap<String, ArrayList<String>> priceIDMap = new HashMap<>();
//                    ArrayList<String> aroundNetpricesIDArray = new ArrayList<>();
//                    HashMap<String, ArrayList<String>> aroundNetpriceIDMap = new HashMap<>();
//
//                    for (EOCompleteProductQuantity unitDetailQuantity : this.eoSparePartWarrantyChecker.eoWarrantyCheckerBookingDetail.unitDetailArray.get(counter).quantity) {
//
//                        if (unitDetailQuantity.price_tags.equalsIgnoreCase(eoCompleteProductQuantity.serviceCategory)) {
//                            pricesIDArray.add(unitDetailQuantity.amountDue);
//                            aroundNetpricesIDArray.add(unitDetailQuantity.around_net_payable);
//                            break;
//                        }
//                    }
//                    if (pricesIDArray.size() == 0) {
//                        pricesIDArray.add(eoCompleteProductQuantity.amountDue);
//                    }
//                    if (aroundNetpricesIDArray.size() == 0) {
//                        aroundNetpricesIDArray.add(eoCompleteProductQuantity.around_net_payable);
//                    }
//                    priceIDMap.put(eoCompleteProductQuantity.id, pricesIDArray);
//                    aroundNetpriceIDMap.put(eoCompleteProductQuantity.id, aroundNetpricesIDArray);
//                    unitList.add(priceIDMap);
//                    aroundNetunitList.add(aroundNetpriceIDMap);
//                }
//                counter++;
//            }
//            int bookkingUnitCounter = 1;
//            for (Object object : unitList) {
//                unitMap.put(bookkingUnitCounter + "", object);
//                bookkingUnitCounter++;
//            }
//            int bookkingAroundNetUnitCounter = 1;
//            for (Object object : aroundNetunitList) {
//                aroundNetunitMap.put(bookkingAroundNetUnitCounter + "", object);
//                bookkingAroundNetUnitCounter++;
//            }
//            brandUnitList.add(unitMap);
//            aroundNetBrandUnitList.add(aroundNetunitMap);
//
//
//        }
//        HashMap<String, Object> brandObj = new HashMap<>();
//        for (Object object : brandUnitList) {
//            HashMap<String, Object> convertedJson = BMAGson.store().getObject(HashMap.class, object);
//            brandObj.putAll(convertedJson);
//        }
//        HashMap<String, Object> aroundNetbrandObj = new HashMap<>();
//        for (Object object : aroundNetBrandUnitList) {
//            HashMap<String, Object> convertedJson = BMAGson.store().getObject(HashMap.class, object);
//            aroundNetbrandObj.putAll(convertedJson);
//        }
//        brandUnitMap.put(this.eoSparePartWarrantyChecker.eoWarrantyCheckerBookingDetail.unitDetailArray.get(0).brand_id, brandObj);
//        aroundNetbrandUnitMap.put(this.eoSparePartWarrantyChecker.eoWarrantyCheckerBookingDetail.unitDetailArray.get(0).brand_id, aroundNetbrandObj);
//        mainRequest.put("partner_paid_basic_charges", brandUnitMap);
//        AroundNetmainRequest.put("discount", aroundNetbrandUnitMap);
//
//        Log.d("aaaaa", "mainr = " + mainRequest.toString());
//        Log.d("aaaaa", "Discount = " + AroundNetmainRequest.toString());

//            brandUnitCounter++;
        // }


//        int checkBoxCounter=0;
//        aroundNetbrandUnitMap.put(this.eoSparePartWarrantyChecker.eoWarrantyCheckerBookingDetail.unitDetailArray.get(0).brand_id, aroundNetbrandObj);
//        HashMap<String,Object>brandMap=new HashMap<>();
//        int unitCounter=1;
//        for(EOCompleteBookingProductUnit eoCompleteBookingProductUnit:this.eoSparePartWarrantyChecker.eoWarrantyCheckerBookingDetail.unitDetailArray) {
//            ArrayList<String> priceList=new ArrayList<>();
//            for (EOWarrantyChecker eoWarrantyChecker : callTypeList) {
//                if (eoWarrantyChecker.isCheckBoxChecked) {
//                    ArrayList<EOCompleteProductQuantity> eoCompleteProductQuantitiesArray = this.eoSparePartWarrantyChecker.eoWarrantyCheckerBookingDetail.prices.get(0);
//                    EOCompleteProductQuantity eoCompleteProductQuantity = eoCompleteProductQuantitiesArray.get(checkBoxCounter);
//                    priceList.add(eoCompleteProductQuantity.id + eoWarrantyChecker.amount + checkBoxCounter + "1");
//                }
//                checkBoxCounter++;
//            }
//            brandMap.put(unitCounter+"",priceList);
//            unitCounter++;
//
//        }

//        ArrayList<Object> checkedPriceIdArray = new ArrayList<>();
//        ArrayList<Object> servicecategoryArray = new ArrayList<>();
//        for (EOWarrantyChecker eoWarrantyChecker : callTypeList) {
//            if (!eoWarrantyChecker.isHeader && eoWarrantyChecker.isCheckBoxChecked) {
//                checkedPriceIdArray.add(eoWarrantyChecker.id);
//                servicecategoryArray.add(eoWarrantyChecker.serviceCategory);
//
//            }
//        }
        //HashMap<String, HashMap<String, ArrayList<String>>> requestMap = new HashMap<>();
        HashMap<String, Object> requestMap = new HashMap<>();
////        Log.d("aaaaa", "checkservice = " + checkedPriceIdArray.toString());
////        Log.d("aaaaa", "checkservice = " + servicecategoryArray.toString());
//       // int counter=0;
//        HashMap<String, Object> brandMap = new HashMap<>();
//        for (EOCompleteBookingProductUnit eoCompleteBookingProductUnit : this.eoSparePartWarrantyChecker.eoWarrantyCheckerBookingDetail.unitDetailArray) {
//            HashMap<String,ArrayList<Object>>mapArray=new HashMap<>();
//
//            ArrayList<Object> priceIDArra=new ArrayList<>();
//            for (EOWarrantyChecker eoWarrantyChecker : unitDetailCallTypeMap.get(eoCompleteBookingProductUnit.brand_id)){
//
//                if(eoWarrantyChecker.isCheckBoxChecked) {
//                    priceIDArra.add(eoWarrantyChecker.id);
//                }
//            }
//
//            brandMap.put(eoCompleteBookingProductUnit.brand_id,mapArray.put(1+"",priceIDArra));
//
//        }
//
//
//        requestMap.put("prices", brandMap.toString());


        ArrayList<String> servicecategoryArray = new ArrayList<>();
        //  Log.d("aaaaa", "requestmap = " + BMAGson.store().toJson(requestMap));
        int counter = 0;
        ArrayList<String> selectedPriceIdArray = new ArrayList<>();
        HashMap<String, HashMap<String, ArrayList<String>>> brandMap1 = new HashMap<>();
        HashMap<String, ArrayList<String>> selectedPriceIDMap = new HashMap<>();

        HashMap<String, ArrayList<String>> serviceCategoryMap = new HashMap<>();
        for (EOWarrantyChecker eoWarrantyChecker : callTypeList) {

            if (eoWarrantyChecker.isCheckBoxChecked) {
                selectedPriceIdArray.add(eoWarrantyChecker.id + "_");
                servicecategoryArray.add(eoWarrantyChecker.serviceCategory);
            }
            if (eoWarrantyChecker.isHeader) {
                serviceCategoryMap.put(counter + "", servicecategoryArray);
                selectedPriceIDMap.put("1", selectedPriceIdArray);
                brandMap1.put(this.eoSparePartWarrantyChecker.eoWarrantyCheckerBookingDetail.unitDetailArray.get(counter).brand_id, selectedPriceIDMap);
                counter++;
                selectedPriceIdArray.clear();


            }

        }
        serviceCategoryMap.put(counter + "", servicecategoryArray);
        if (selectedPriceIdArray.size() == 0) {
            Toast.makeText(getContext(), "Please select atleast one call type", Toast.LENGTH_SHORT).show();
            return;
        }
        selectedPriceIDMap.put("1", selectedPriceIdArray);
        brandMap1.put(this.eoSparePartWarrantyChecker.eoWarrantyCheckerBookingDetail.unitDetailArray.get(counter).brand_id, selectedPriceIDMap);
        //        requestMap.put("prices", brandMap1);
//         requestMap.put("request_types", servicecategoryArray);
        //Log.d("aaaaa", "requestmap = " + BMAGson.store().toJson(requestMap));
        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = EditWarrantyBooking.this;
        this.actionID = "submitWarrantyCheckerAndEditCallType";

        httpRequest.execute(this.actionID, eoBooking.bookingID, selectModel.getText().toString(), selectPurchaseDate.getText().toString(), BMAGson.store().toJson(brandMap1), BMAGson.store().toJson(serviceCategoryMap), MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("scAgentID", null));


    }

    private boolean isSelectAllField() {
        if (selectModel.getText().toString().trim().length() == 0) {
            Toast.makeText(getContext(), "Model number can not blank", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectPurchaseDate.getText().toString().trim().length() == 0) {
            Toast.makeText(getContext(), "Purchase Date can not blank", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void openSparePartPage() {
        Bundle bundle = new Bundle();

        bundle.putString("pod", selectPurchaseDate.getText().toString().trim());
        //  bundle.putString("modelNo",modelNo);
        bundle.putParcelable("modelNumber", getModelNumber());
        bundle.putParcelable("eoBooking", eoBooking);
        bundle.putParcelable("eoSpareParts", eoSparePartWarrantyChecker.eoSpareParts);
        this.updateFragment(bundle, new SparePartsOrderFragment(), getString(R.string.spareParts));
    }

    private EOModelNumber getModelNumber() {
        EOModelNumber eoModelNumber = (EOModelNumber) selectModel.getTag();
        if(eoModelNumber==null){
            eoModelNumber=new EOModelNumber();
            selectModel.setTag(eoModelNumber);
        }
        String modelNo = selectModel.getText().toString().trim();
        eoModelNumber.modelNumber = modelNo;
        if (this.eoSparePartWarrantyChecker.modelList == null || this.eoSparePartWarrantyChecker.modelList.size() == 0) {
            // eoModelNumber=new EOModelNumber();
            eoModelNumber.id = "";
        }
        return eoModelNumber;
    }

    private void openProductDetialPage() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("eoBooking", eoBooking);

        if (selectedCompleteDetail != null) {
            bundle.putParcelable("productDetail", selectedCompleteDetail);
        }
        bundle.putString("pod", selectPurchaseDate.getText().toString().trim());
        bundle.putParcelable("modelNumber", getModelNumber());
        this.updateFragment(bundle, new ProductDetailFragment(), "Product Details");
    }


    public void updateFragment(Bundle bundle, Fragment fragment, String headerText) {
        this.updateFragment(bundle, fragment, headerText, null);
    }

    public void updateFragment(Bundle bundle, Fragment fragment, String headerText, Integer imageDrawable) {
        bundle.putString(BMAConstants.HEADER_TXT, headerText);
        fragment.setArguments(bundle);
        getMainActivity().updateFragment(fragment, true, imageDrawable);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        if (data.getBooleanExtra("isCancelled", false) || data.getBooleanExtra("completed", false)) {
            Intent intent = new Intent();
            intent.putExtra("isCancelled", true);
            getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, intent);
            getFragmentManager().popBackStack();
        } else {
            Intent intent = new Intent();
            EOCompleteProductdetail selectCompleteDetail = data.getParcelableExtra("productDetail");
            intent.putExtra("productDetail", selectCompleteDetail);
            EOSpareParts eoSpareParts = data.getParcelableExtra("eoSparePart");
            intent.putExtra("eoSparePart", eoSpareParts);

            EOModelNumber modelNumber = data.getParcelableExtra("modelNumber");
            String selectpurchadeDate = data.getStringExtra("pod");
            boolean isConsumptionrequired=data.getBooleanExtra("isConsumptionrequired",false);
            Log.d("aaaaaa","editwarranty = "+isConsumptionrequired);
            intent.putExtra("isConsumptionrequired",isConsumptionrequired);
            intent.putExtra("pod", selectpurchadeDate);
            intent.putExtra("modelNumber", modelNumber);
            intent.putExtra("completeCatogryPageName", "productDetail");
            getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, intent);
            getFragmentManager().popBackStack();
        }
    }

    private boolean checkCorrectSelection() {
        ArrayList<EOWarrantyChecker> arrayList = new ArrayList<>();
        ArrayList<ArrayList<EOWarrantyChecker>> groupArrayList = new ArrayList<>();

        for (EOWarrantyChecker eoWarrantyChecker : callTypeList) {

            if (eoWarrantyChecker.isHeader) {
                groupArrayList.add(arrayList);
                arrayList = new ArrayList<>();
            } else {
                arrayList.add(eoWarrantyChecker);
            }
        }
        groupArrayList.add(arrayList);
        for (int i = 0; i < groupArrayList.size(); i++) {
            if (!isCorrectSelectCheckBox(groupArrayList.get(i))) {
                return false;

            }
        }
        return true;
    }


    private boolean isCorrectSelectCheckBox(ArrayList<EOWarrantyChecker> delivered_price_tags) {
        boolean repair_flag = false;
        boolean repair_out_flag = false;
        boolean installation_flag = false;
        boolean pdi = false;
        boolean extended_warranty = false;
        boolean pre_sales = false;
        boolean amc = false;
        //$array =[];
        ArrayList<Object> objList = new ArrayList<>();
        if ((findInArray(delivered_price_tags, "Repair - In Warranty (Home Visit)") != -1 || this.findInArray(delivered_price_tags, "Repair - In Warranty (Service Center Visit)") != -1
                || this.findInArray(delivered_price_tags, "Repair - In Warranty (Customer Location)") != -1)) {

            repair_flag = true;
            objList.add(repair_flag);
        }
        if ((this.findInArray(delivered_price_tags, "Repair - Out Of Warranty (Home Visit)") != -1
                || this.findInArray(delivered_price_tags, "Repair - Out Of Warranty (Home Visit)") != -1
                || this.findInArray(delivered_price_tags, "Repair - Out Of Warranty (Customer Location)") != -1
                || this.findInArray(delivered_price_tags, "Repair - Out Of Warranty (Service Center Visit)") != -1)) {

            repair_out_flag = true;
            objList.add(repair_out_flag);
        }
        if (this.findInArray(delivered_price_tags, "Extended Warranty") != -1) {
            extended_warranty = true;
            objList.add(extended_warranty);
        }
        if (this.findInArray(delivered_price_tags, "AMC (Annual Maintenance Contract)") != -1) {
            amc = true;
            objList.add(amc);
        }

        if (this.findInArray(delivered_price_tags, "Presale Repair") != -1) {
            pre_sales = true;
            objList.add(pre_sales);
        }

        if (this.findInArray(delivered_price_tags, "Installation & Demo (Free)") != -1
                || this.findInArray(delivered_price_tags, "Installation & Demo (Paid)") != -1) {
            installation_flag = true;
            objList.add(installation_flag);
        }

        if (this.findInArray(delivered_price_tags, "Pre-Dispatch Inspection PDI - With Packing") != -1
                || this.findInArray(delivered_price_tags, "Pre-Dispatch Inspection PDI - With Packing") != -1
                || this.findInArray(delivered_price_tags, "Pre-Dispatch Inspection PDI - Without Packing") != -1
                || this.findInArray(delivered_price_tags, "Pre-Dispatch Inspection PDI - Without Packing") != -1) {
            pdi = true;
            objList.add(pdi);
        }

        if (objList.size() > 1) {
            return false;
        } else {
            return true;
        }


    }

    private int findInArray(ArrayList<EOWarrantyChecker> ar, String val) {
        for (int i = 0; i < ar.size(); i++) {
            EOWarrantyChecker eoWarrantyChecker = ar.get(i);
            if (eoWarrantyChecker.isCheckBoxChecked && eoWarrantyChecker.serviceCategory.equalsIgnoreCase(val)) { // strict equality test
                return i;
            }
        }
        return -1;
    }

}


//HashMap<"BrandID",HashMap<StringCounter,ArrayList<String>>>