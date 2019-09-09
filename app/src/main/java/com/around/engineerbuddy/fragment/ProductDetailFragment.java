package com.around.engineerbuddy.fragment;

import android.app.DatePickerDialog;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.component.BMAFontViewField;
import com.around.engineerbuddy.component.BMASelectionDialog;
import com.around.engineerbuddy.entity.BMAUiEntity;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.entity.EOCompleteProductQuantity;
import com.around.engineerbuddy.entity.EOCompleteProductdetail;
import com.around.engineerbuddy.entity.EOModelNumber;
import com.around.engineerbuddy.util.BMAConstants;
import com.around.engineerbuddy.util.BMAUIUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ProductDetailFragment extends BMAFragment implements View.OnClickListener {

    Button submit, addMoreProduct;
    ;
    TextView noDataToDisplay, categoryName, amountDue, applianceRequesttype, brandName, serviceCapacityType, selectDate, selectServiceCatg, name, applianceName, address;
    CheckBox productBorkenStatus;
    LinearLayout productHeaderLayout, addServicePartLayout;
    // BMAFontViewField modelDropDownIcon;
    ImageView serialNoPic;
    ScrollView scrollView;
    EOBooking eoBooking;
    int counter = 0;
    EOCompleteProductdetail eoCompleteProductdetail;
    private String actionID;

    HashMap<String, Object> partsMap = new HashMap<>();
    HashMap<Integer, HashMap<String, Object>> selectedPartsObject = new HashMap<>();
    // HashMap<Integer, HashMap<String, Object>> partsObject = new HashMap<>();
    //String selectedObject;
    //JSONObject selectJsonObject;
    EOCompleteProductdetail selectedCompleteDetail;//= new EOCompleteProductdetail();


    LayoutInflater inflater;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.eoBooking = getArguments().getParcelable("eoBooking");
        //selectedObject = getArguments().getString("productDetail");
        selectedCompleteDetail = getArguments().getParcelable("productDetail");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.product_detail_fragment, container, false);
        this.inflater = inflater;
        this.counter = 0;
//        if (selectedObject != null) {
//            try {
//                selectJsonObject = new JSONObject(selectedObject);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            // //////selectedPartsObject = BMAGson.store().getObject(HashMap.class, selectedObject);
//        }
        this.name = this.view.findViewById(R.id.name);
        this.applianceName = this.view.findViewById(R.id.applianceName);
        this.brandName = this.view.findViewById(R.id.brandName);
        this.address = this.view.findViewById(R.id.address);
        this.scrollView = this.view.findViewById(R.id.scroolView);
        this.view.findViewById(R.id.purchaseDate).setVisibility(View.VISIBLE);

        this.submit = this.view.findViewById(R.id.submitButton);
        this.addMoreProduct = this.view.findViewById(R.id.cancelButton);
        this.productHeaderLayout = this.view.findViewById(R.id.productHeaderLayout);
        this.categoryName = this.view.findViewById(R.id.categoryName);
        this.amountDue = this.view.findViewById(R.id.amountDue);
        this.applianceRequesttype = this.view.findViewById(R.id.appliancerequesttype);
        this.addServicePartLayout = this.view.findViewById(R.id.addServicePartLayout);
        this.serviceCapacityType = this.view.findViewById(R.id.serviceCapacityType);
        //this.selectDate = this.view.findViewById(R.id.selectpurchaseDate);
        this.productBorkenStatus = this.view.findViewById(R.id.productBorkencheckbox);

        //this.view.findViewById(R.id.dateLayout).setOnClickListener(this);

        // this.serialNoPic = this.view.findViewById(R.id.serialNoPic);
        this.submit.setText("Submit");
        this.submit.setVisibility(View.GONE);
       // this.addMoreProduct.setVisibility(View.GONE);
        this.addMoreProduct.setText("Next");
        //this.dataToView();
        this.name.setText(eoBooking.name);
        this.applianceName.setText(eoBooking.services);
        brandName.setText(eoBooking.applianceBrand);
        this.address.setText(eoBooking.bookingAddress);
        this.categoryName.setText(eoBooking.applianceCategory);
        this.serviceCapacityType.setText(eoBooking.applianceCapacity);
        this.noDataToDisplay=this.view.findViewById(R.id.noDataToDisplay);
        this.getProductRequest();

        //addMoreProductLayout(inflater);

        this.addMoreProduct.setOnClickListener(this);
        //this.submit.setOnClickListener(this);
        float dialogRadius = getResources().getDimension(R.dimen._20dp);
        BMAUIUtil.setBackgroundRound(this.submit, BMAUIUtil.getColor(R.color.missedBookingcolor), new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
        BMAUIUtil.setBackgroundRound(this.addMoreProduct, BMAUIUtil.getColor(R.color.missedBookingcolor), new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        this.submit.setOnClickListener(this);

        //BMAUIUtil.setBackgroundRound(this.productHeaderLayout,BMAUIUtil.getColor(R.color.darkNavy),new float[]{dialogRadius,dialogRadius,dialogRadius,dialogRadius,0,0,0,0});
        //   this.submit.setText("Submit");
        return this.view;
    }

    private void dataToView() throws JSONException {
        if (this.eoCompleteProductdetail == null || this.eoCompleteProductdetail.bookingUnitDetails == null) {
            return;
        }

     //   if (this.eoCompleteProductdetail.bookingUnitDetails.purchase_date != null)
            //  this.selectDate.setText(this.eoCompleteProductdetail.bookingUnitDetails.purchase_date);
            if (selectedCompleteDetail != null) {
                // this.selectDate.setText(this.selectedCompleteDetail.getbookingProductUnit().purchase_date);
                productBorkenStatus.setChecked(this.selectedCompleteDetail.getbookingProductUnit().isProductBroken);
            } else if (this.eoCompleteProductdetail.bookingUnitDetails.purchase_date != null) {
                this.selectedCompleteDetail = new EOCompleteProductdetail();
                // this.selectedCompleteDetail.getbookingProductUnit().purchase_date = this.eoCompleteProductdetail.bookingUnitDetails.purchase_date;
            }

        //this.categoryName.setText(this.eoCompleteProductdetail.bookingUnitDetails.category);
        //  this.amountDue.setText("100");
        // this.applianceRequesttype.setText(this.eoBooking.requestType);
        //   this.brandName.setText(this.eoCompleteProductdetail.bookingUnitDetails.brand);
        // this.serviceCapacityType.setText(this.eoCompleteProductdetail.bookingUnitDetails.capacity);
//        if(selectedPartsObject!=null) {
//            selectDate.setText(selectedPartsObject.get("selectDate").toString());
//        }


        for (EOCompleteProductQuantity eoCompleteProductQuantity : this.eoCompleteProductdetail.bookingUnitDetails.quantity) {
            // partsObject.put(counter, new HashMap<String, Object>());
            if (selectedCompleteDetail.getbookingProductUnit().quantity.size() < this.eoCompleteProductdetail.bookingUnitDetails.quantity.size()) {
                selectedCompleteDetail.getbookingProductUnit().quantity.add(new EOCompleteProductQuantity());
            }
            EOCompleteProductQuantity selectCompleteProduct = selectedCompleteDetail.getbookingProductUnit().quantity.get(counter);
            // partsObject.get(counter).put("unit_id", eoCompleteProductQuantity.unit_id);
            selectCompleteProduct.unit_id = eoCompleteProductQuantity.unit_id;
            selectCompleteProduct.selectedPOD = eoCompleteProductQuantity.pod;
            addMoreProductLayout(inflater, eoCompleteProductQuantity, selectCompleteProduct);

        }
    }

    private void addMoreProductLayout(LayoutInflater inflater, EOCompleteProductQuantity eoCompleteProductQuantity, EOCompleteProductQuantity eselectedCompleteProductQuantity) throws JSONException {
        View childView = inflater.inflate(R.layout.product_item_layout, null, false);

        EditText selectModel = childView.findViewById(R.id.selectModel);
        BMAFontViewField modelDropDownIcon = childView.findViewById(R.id.modeldropDownIcon);
        LinearLayout modelLayout = childView.findViewById(R.id.modelLayout);
        EditText enterSerialNumber = childView.findViewById(R.id.enterSerialNumber);
        enterSerialNumber.setTag(counter);
        LinearLayout enterSerialNoLayout = childView.findViewById(R.id.enterSerialNoLayout);
        LinearLayout serialNumberPhotoLayout = childView.findViewById(R.id.serialNumberPhotoLayout);
        LinearLayout datePurchaseLayout = childView.findViewById(R.id.dateLayout);
        childView.findViewById(R.id.modelLayout).setVisibility(counter == 0 ? View.VISIBLE : View.GONE);
        if (counter == 0) {
            this.selectDate = childView.findViewById(R.id.selectpurchaseDate);
            datePurchaseLayout.setVisibility(View.VISIBLE);
            if (selectedCompleteDetail != null && this.selectedCompleteDetail.getbookingProductUnit().purchase_date != null) {
                this.selectDate.setText(this.selectedCompleteDetail.getbookingProductUnit().purchase_date);
            }
            if(this.eoCompleteProductdetail!=null && this.eoCompleteProductdetail.eoSpareParts!=null && this.eoCompleteProductdetail.eoSpareParts.date_of_purchase!=null){
                selectDate.setEnabled(false);
                selectDate.setText(this.eoCompleteProductdetail.eoSpareParts.date_of_purchase);
                this.selectedCompleteDetail.getbookingProductUnit().purchase_date=this.eoCompleteProductdetail.eoSpareParts.date_of_purchase;

                datePurchaseLayout.setEnabled(false);
            }else {
                childView.findViewById(R.id.dateLayout).setOnClickListener(this);
            }
            if (this.eoCompleteProductdetail != null && this.eoCompleteProductdetail.modelList.size() == 0) {
                selectModel.setEnabled(true);
                selectModel.setTag(counter);
                selectModel.setHint("Enter model number");
                selectModel.addTextChangedListener(new CustomeTextWatcher(selectModel, "selectModel"));
                modelDropDownIcon.setVisibility(View.INVISIBLE);
            } else if (this.eoCompleteProductdetail != null) {
                modelLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openModelSelectionPopUp(selectModel, v);
                    }
                });
            }
            if(this.eoCompleteProductdetail!=null && this.eoCompleteProductdetail.eoSpareParts!=null && this.eoCompleteProductdetail.eoSpareParts.model_number!=null){

                        for(EOModelNumber eoModelNumber:this.eoCompleteProductdetail.modelList) {
                            if(eoModelNumber.modelNumber.equalsIgnoreCase( this.eoCompleteProductdetail.eoSpareParts.model_number)) {
                                selectedCompleteDetail.getbookingProductUnit().quantity.get(0).model_number =  this.eoCompleteProductdetail.eoSpareParts.model_number;
                                selectedCompleteDetail.getbookingProductUnit().quantity.get(0).model_number_id = eoModelNumber.id;
                                selectModel.setTag(eoModelNumber);
                                selectModel.setText( this.eoCompleteProductdetail.eoSpareParts.model_number);
                                selectModel.setEnabled(false);
                                modelDropDownIcon.setEnabled(false);
                                break;
                            }
                        }
            }
            enterSerialNoLayout.setVisibility(eoCompleteProductQuantity.pod.equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);
            serialNumberPhotoLayout.setVisibility(eoCompleteProductQuantity.pod.equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);


        } else {
            datePurchaseLayout.setVisibility(View.GONE);
            enterSerialNoLayout.setVisibility(View.GONE);
            serialNumberPhotoLayout.setVisibility(View.GONE);
        }
        //partsObject.get(counter).put("amount_paid", eoCompleteProductQuantity.amountDue);
        eselectedCompleteProductQuantity.amountDue = eoCompleteProductQuantity.amountDue;
        eselectedCompleteProductQuantity.pod = eoCompleteProductQuantity.pod;


        enterSerialNumber.addTextChangedListener(new CustomeTextWatcher(enterSerialNumber, "serial_number"));
        TextView serialNoPhoto = childView.findViewById(R.id.serialNoPhoto);
        TextView amountDue = childView.findViewById(R.id.amountDue);
        TextView serialNoPicLink = childView.findViewById(R.id.serialNoPicLink);
        if (counter == 0) {
            serialNoPic = childView.findViewById(R.id.serialNoPic);
//            Log.d("aaaaaa","serialNoPIC= "+eselectedCompleteProductQuantity.serial_number_pic);
            if (eselectedCompleteProductQuantity.serialNoPic != null) {
                serialNoPic.setImageBitmap(eselectedCompleteProductQuantity.serialNoPic);
            }
            childView.findViewById(R.id.serialPhotoLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPic(1, serialNoPic);
                }
            });
        }
        EditText basichChargeEdittext = childView.findViewById(R.id.basichChargeEdittext);
        basichChargeEdittext.setTag(counter);

        basichChargeEdittext.addTextChangedListener(new CustomeTextWatcher(basichChargeEdittext, "service_charge"));
        EditText additionalChargeEdittext = childView.findViewById(R.id.additionalChargeEdittext);
        additionalChargeEdittext.setTag(counter);

        additionalChargeEdittext.addTextChangedListener(new CustomeTextWatcher(additionalChargeEdittext, "additional_service_charge"));
        EditText partsCostEdittext = childView.findViewById(R.id.partsCostEdittext);
        partsCostEdittext.setTag(counter);

        partsCostEdittext.addTextChangedListener(new CustomeTextWatcher(partsCostEdittext, "parts_cost"));
        RadioGroup radioGroup = childView.findViewById(R.id.radioStatus);
        RadioButton complete = childView.findViewById(R.id.radioComplete);
        RadioButton notComplete = childView.findViewById(R.id.radioNotComplete);
        radioGroup.setTag(counter);
        complete.setChecked(eselectedCompleteProductQuantity.isComplete);
        notComplete.setChecked(eselectedCompleteProductQuantity.isNotComplete);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int partKey = (Integer) group.getTag();
                switch (checkedId) {
                    case R.id.radioComplete:
                        //partsObject.get(partKey).put("complete", true);
                        eselectedCompleteProductQuantity.isComplete = true;
                        eselectedCompleteProductQuantity.isNotComplete = false;
                        // partsMap.put(,);
                        // do operations specific to this selection
                        break;
                    case R.id.radioNotComplete:
                        // partsObject.get(partKey).put("complete", false);
                        eselectedCompleteProductQuantity.isNotComplete = true;
                        eselectedCompleteProductQuantity.isComplete = false;
                        // do operations specific to this selection
                        break;
                }
            }
        });

        //  EditText enterSerialNumber= childView.findViewById(R.id.enterSerialNumber);
        // childView.findViewById(R.id.productHeaderLayout).setVisibility(counter==0 ? View.VISIBLE : View.GONE);
        //childView.findViewById(R.id.footerLayout).setVisibility(counter==0 ? View.VISIBLE :View.GONE);
        childView.findViewById(R.id.symptonDeviderLayout).setVisibility(this.eoCompleteProductdetail.bookingUnitDetails.quantity.size() > 1 ? View.VISIBLE : View.GONE);
        enterSerialNumber.setTag(counter);
        selectServiceCatg = childView.findViewById(R.id.selectServiceCatg);
       // if (counter == 0) {
            if (eoCompleteProductQuantity.price_tags != null)
                selectServiceCatg.setText(eoCompleteProductQuantity.price_tags);
       // }
//         else {
//            selectServiceCatg.setVisibility(View.GONE);
//        }
        eselectedCompleteProductQuantity.price_tags = eoCompleteProductQuantity.price_tags;

        if (eselectedCompleteProductQuantity.serialNumber != null) {
            enterSerialNumber.setText(eselectedCompleteProductQuantity.serialNumber);
        } else {
            enterSerialNumber.setText(eoCompleteProductQuantity.serial_number);
        }
        if (eselectedCompleteProductQuantity.customerBasicharge != null) {
            basichChargeEdittext.setText(eselectedCompleteProductQuantity.customerBasicharge);
        } else {
            basichChargeEdittext.setText(eoCompleteProductQuantity.customer_paid_basic_charges);
        }
        if (eselectedCompleteProductQuantity.customerExtraharge != null) {
            additionalChargeEdittext.setText(eselectedCompleteProductQuantity.customerExtraharge);
        } else {
            additionalChargeEdittext.setText(eoCompleteProductQuantity.customer_paid_extra_charges);
        }
        if (eselectedCompleteProductQuantity.customerPartharge != null) {
            partsCostEdittext.setText(eselectedCompleteProductQuantity.customerPartharge);
        } else {
            partsCostEdittext.setText(eoCompleteProductQuantity.customer_paid_parts);
        }
        amountDue.setText("" + eoCompleteProductQuantity.amountDue);
        //serialNoPicLink.setText(eoCompleteProductQuantity.serial_number_pic);
        enterSerialNumber.setSelection(enterSerialNumber.getText().toString().trim().length());
        modelLayout.setTag(counter);
        if (eselectedCompleteProductQuantity != null && eselectedCompleteProductQuantity.model_number != null) {
            selectModel.setText(eselectedCompleteProductQuantity.model_number);
        }


        childView.findViewById(R.id.basicChargeLayout).setVisibility(this.isShowBasicCharge(eoCompleteProductQuantity) ? View.VISIBLE : View.GONE);
        childView.findViewById(R.id.partsChargeLayout).setVisibility(this.isShowAdditionalCharge(eoCompleteProductQuantity) ? View.VISIBLE : View.GONE);
        childView.findViewById(R.id.additionalChargeLayout).setVisibility(this.isShowPartsCharge(eoCompleteProductQuantity) ? View.VISIBLE : View.GONE);


        int dialogRadius = 40;
//        if (counter > 0)
//            BMAUIUtil.setBackgroundRound(spareNumberLayout, R.color.colorPrimary, new float[]{0, 0, dialogRadius, dialogRadius, dialogRadius, dialogRadius, 0, 0});
        // setSelectedDataToView(selectedPartsObject.get(counter));

//
//        if (selectJsonObject != null) {
//            String key = counter + "";
//
//            // HashMap<String, Object> hm = selectedPartsObject.get(counter);
//            JSONObject hm = selectJsonObject.getJSONObject(key);
//            if (!(hm.isNull("model_number"))) {
//                Object modelNumber = hm.get("model_number");
//
//                Log.d("aaaaaaa", "modelnumber");
//                if (modelNumber != null) {
//                    selectModel.setText(modelNumber.toString());
//                }
//            }
//            if (!(hm.isNull("serial_number"))) {
//                Object serialNo = hm.get("serial_number");
//                Log.d("aaaaaaa", "serialNo");
//                if (serialNo != null && ((String) serialNo).length() != 0) {
//                    enterSerialNumber.setText(serialNo.toString());
//                }
//            }
//            //  Bitmap serialNmbrPic= hm.get("serialNoPic");
//            if (!(hm.isNull("serialNoPic"))) {
//                if (hm.get("serialNoPic") != null && hm.get("serialNoPic") instanceof Bitmap) {
//                    serialNoPic.setImageBitmap((Bitmap) hm.get("serialNoPic"));
//                }
//            }
//            Object serviceCharge = hm.get("service_charge");
//            Log.d("aaaaaaa", "servicecharge");
//            if (serviceCharge != null) {
//                basichChargeEdittext.setText(serviceCharge.toString());
//            }
//            Object additionalServiceCharge = hm.get("additional_service_charge");
//            Log.d("aaaaaaa", "additionalcharge");
//            if (additionalServiceCharge != null) {
//                additionalChargeEdittext.setText(additionalServiceCharge.toString());
//            }
//            Object partsCost = hm.get("parts_cost");
//            Log.d("aaaaaaa", "partsCost");
//            if (partsCost != null) {
//                partsCostEdittext.setText(partsCost.toString());
//            }
//            Boolean isComplete = (Boolean) hm.get("complete");
//            Log.d("aaaaaaa", "complete");
//            if (isComplete != null) {
//                complete.setChecked(isComplete);
//            }
//            Object not_Complete = hm.get("complete");
//            if (not_Complete != null) {
//                notComplete.setChecked((Boolean) not_Complete);
//            }
//        }
        // radioGroup.setText(hm.get("basichCharge"));

        this.addServicePartLayout.addView(childView);
        this.scrollView.fullScroll(View.FOCUS_DOWN);

        counter++;
    }

    private void setSelectedDataToView(HashMap<String, Object> selectedMap) {

    }


    private HttpRequest httpRequest;

    private void getProductRequest() {
        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = ProductDetailFragment.this;
        this.actionID = "bookingProductDetails";
        httpRequest.execute(this.actionID, eoBooking.bookingID, eoBooking.applianceBrand, eoBooking.partnerID, eoBooking.serviceID, getMainActivity().getServiceCenterId());

    }

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
                    this.noDataToDisplay.setVisibility(View.GONE);
                    // Log.d("aaaaaaa","response "+jsonObject.get("response"));
                    //    JSONObject jsonObject1=jsonObject.getJSONObject("response");//new JSONObject(jsonObject.get("response").toString());

                    //  EOCompleteProductdetail eoCompleteProductdetail=new EOCompleteProductdetail();

                    if (this.actionID.equalsIgnoreCase("validateSerialNumber")) {
                        if (this.clickView.getId() == R.id.cancelButton) {
                            addMoreProduct();
                        } else {
                            submit();
                        }
                    } else {
                        this.eoCompleteProductdetail = BMAGson.store().getObject(EOCompleteProductdetail.class, jsonObject.get("response"));
                        this.dataToView();
                    }
//                    if (this.actionID.equalsIgnoreCase("engineerSparePartOrder")) {
//                        String responseData = (new JSONObject(jsonObject.getString("response")).getString("sparePartsOrder"));
//                        this.eoSparePartsOrder = BMAGson.store().getObject(EOSparePartsOrder.class, responseData);
//
//                    } else if (this.actionID.equalsIgnoreCase("partTypeOnModelNumber")) {
//                        String responseData = (new JSONObject(jsonObject.getString("response")).getString("partTypeList"));
//                        Log.d("aaaaa", "reponse = " + responseData);
//                        partTypeList = BMAGson.store().getList(EOPartType.class, responseData);
//                        Log.d("aaaaa", "size = " + partTypeList.get(0));
//                    } else if (this.actionID.equalsIgnoreCase("sparePartName")) {
//                        partNameList = BMAGson.store().getList(EOPartName.class, jsonObject.getString("response"));
//                        Log.d("bbbbbbb", "spareparts  = ");
//
//                        } else if (actionID.equalsIgnoreCase("submitSparePartsOrder")) {
//                            getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, new Intent());
//                            getFragmentManager().popBackStack();
//
//                        }
//                    }

                } else {

                    if(!this.actionID.equalsIgnoreCase("validateSerialNumber")) {
                        addMoreProduct.setEnabled(false);
                        this.noDataToDisplay.setVisibility(View.VISIBLE);
                    }

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

    ImageView serialNuberPic;

    private void selectPic(int requestCode, ImageView seraialNumPic) {
        serialNuberPic = seraialNumPic;
        Intent galleryIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, requestCode);
        // imagePicker=new ImagePicker(getContext(),getActivity(),requestCode);
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

                selectDate.setText(getDate(year, month + 1, dayOfMonth));
                selectedCompleteDetail.getbookingProductUnit().purchase_date = selectDate.getText().toString().trim();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        if (data.getStringExtra("addMorePart") != null) {
            Intent intent = new Intent();
            intent.putExtra("AddMoreproductDetail", data.getStringExtra("AddMoreproductDetail"));
            EOCompleteProductdetail selectCompleteDetail = data.getParcelableExtra("productDetail");
            intent.putExtra("productDetail", selectCompleteDetail);
//            intent.putExtra("selectDate", data.getStringExtra("selectDate"));
//            intent.putExtra("isBrokenProduct", data.getBooleanExtra("isBrokenProduct", false));
            intent.putExtra("completeCatogryPageName", "productDetail");
            getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, intent);
            getFragmentManager().popBackStack();
            return;
        }
//        frontDefectiveBitmap=null;
//        backDefectiveBitmap=null;
        if (data.getExtras() == null || data.getExtras().get("data") == null) {
            return;
        }
        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
        switch (requestCode) {
            case 1:
                //partsObject.get(0).put("serialNoPic", imageBitmap);
                selectedCompleteDetail.getbookingProductUnit().quantity.get(0).serialNoPic = imageBitmap;
                serialNuberPic.setImageBitmap(imageBitmap);

                break;

        }
    }


    private void openModelSelectionPopUp(TextView selectModel, View v) {
        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getModelList(selectModel), true) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if (selectedItems != null) {
                    // partsObject.get(v.getTag()).put("model_number", selectedItems.getDetail1());
                    selectModel.setText(selectedItems.getDetail1());
                    selectModel.setTag(selectedItems.tag);

                    selectedCompleteDetail.getbookingProductUnit().quantity.get(0).model_number = selectedItems.getDetail1();
                    selectedCompleteDetail.getbookingProductUnit().quantity.get(0).model_number_id = ((EOModelNumber) selectedItems.tag).id;
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
        if (this.eoCompleteProductdetail == null) {
            return bmaUiEntityArrayList;
        }
        for (EOModelNumber eoModelNumber : this.eoCompleteProductdetail.modelList) {
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


    View clickView;

    @Override
    public void onClick(View v) {
        //getChildFragmentManager().sta;
        this.clickView = v;
        switch (v.getId()) {
            case R.id.dateLayout:
                openCalender();
                break;
//            case R.id.serialPhotoLayout:
//                selectPic(1);
//                break;
            case R.id.cancelButton:

                if (this.selectedCompleteDetail.bookingUnitDetails.purchase_date != null && isFillAllFeild()) {
                    // if(checkSerialNumber(this.selectedCompleteDetail.bookingUnitDetails.quantity.get(0).serialNumber))
                    this.selectedCompleteDetail.bookingUnitDetails.isProductBroken = productBorkenStatus.isChecked();
//                    if (selectedCompleteDetail.getbookingProductUnit().quantity.get(0).pod.equalsIgnoreCase("1")) {
//                        checkSerialNumber(selectedCompleteDetail.getbookingProductUnit().quantity.get(0).serialNumber);
//                    } else
                        if (checkChargeAmount()) {
                            if (selectedCompleteDetail.getbookingProductUnit().quantity.get(0).pod.equalsIgnoreCase("1")) {
                                checkSerialNumber(selectedCompleteDetail.getbookingProductUnit().quantity.get(0).serialNumber);
                            }else {
                                addMoreProduct();
                            }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.customerChargesValidation), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), getString(R.string.selectAllFieldsValidation), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.submitButton:
                if (this.selectedCompleteDetail.bookingUnitDetails.purchase_date != null && isFillAllFeild()) {
                    this.selectedCompleteDetail.bookingUnitDetails.isProductBroken = productBorkenStatus.isChecked();
//                    if (selectedCompleteDetail.getbookingProductUnit().quantity.get(0).pod.equalsIgnoreCase("1")) {
//                        checkSerialNumber(selectedCompleteDetail.getbookingProductUnit().quantity.get(0).serialNumber);
//                    }else
                        if (checkChargeAmount()) {
                        boolean isQuantityStatus=false;
                        for (EOCompleteProductQuantity selectCompleteProduct : this.selectedCompleteDetail.getbookingProductUnit().quantity) {
                            if (selectCompleteProduct.isComplete) {
                                isQuantityStatus = true;
                            }
                        }
                        if(isQuantityStatus) {
                            if (selectedCompleteDetail.getbookingProductUnit().quantity.get(0).pod.equalsIgnoreCase("1")) {
                                checkSerialNumber(selectedCompleteDetail.getbookingProductUnit().quantity.get(0).serialNumber);
                            }else {
                                submit();
                            }
                        }else {
                            Toast.makeText(getContext(),getString(R.string.selectCompleteOnce),Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(getContext(), getString(R.string.customerChargesValidation), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.selectAllFieldsValidation), Toast.LENGTH_SHORT).show();
                }

                break;


        }

    }

    private void addMoreProduct() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("productDetail", this.selectedCompleteDetail);
        bundle.putString("selectDate", this.selectDate.getText().toString());
        bundle.putString("unitDetailKey", this.eoCompleteProductdetail.bookingUnitDetails.quantity.get(0).unit_id);
        //bundle.putBoolean("isBrokenProduct", this.productBorkenStatus.isChecked());
        bundle.putParcelableArrayList("prices", this.eoCompleteProductdetail.prices);
        this.updateFragment(bundle, new AddMoreProductDetailFragment(), getString(R.string.addProduct));
    }

    public void updateFragment(Bundle bundle, Fragment fragment, String headerText) {
        bundle.putString(BMAConstants.HEADER_TXT, headerText);
        fragment.setArguments(bundle);
        getMainActivity().updateFragment(fragment, true);
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
            EOCompleteProductQuantity selectCompleteProduct = selectedCompleteDetail.getbookingProductUnit().quantity.get(getKey);
            if (key.equalsIgnoreCase("selectModel")) {
                selectedCompleteDetail.getbookingProductUnit().quantity.get(0).model_number = value;
            }
            if (key.equalsIgnoreCase("service_charge")) {
                selectCompleteProduct.customerBasicharge = value;
            }
            if (key.equalsIgnoreCase("serial_number")) {
                selectCompleteProduct.serialNumber = value;
            }
            if (key.equalsIgnoreCase("additional_service_charge")) {
                selectCompleteProduct.customerExtraharge = value;
            }
            if (key.equalsIgnoreCase("parts_cost")) {
                selectCompleteProduct.customerPartharge = value;
            }
        }
    }

    private boolean isFillAllFeild() {

        int count = 0;
        for (EOCompleteProductQuantity eoCompleteProductQuantity : selectedCompleteDetail.getbookingProductUnit().quantity) {

            EOCompleteProductQuantity completeProductQnty = this.eoCompleteProductdetail.getbookingProductUnit().quantity.get(count);
            if ((count >= 1 || eoCompleteProductQuantity.model_number != null) &&
                    (eoCompleteProductQuantity.pod.equalsIgnoreCase("0") || ((counter >= 1 || eoCompleteProductQuantity.serialNumber != null && eoCompleteProductQuantity.serialNumber.length() > 0) &&
                            (count >= 1 || eoCompleteProductQuantity.serialNoPic != null))) &&
                    (!isShowBasicCharge(completeProductQnty) || (eoCompleteProductQuantity.customerBasicharge != null && eoCompleteProductQuantity.customerBasicharge.length() != 0)) &&

                    (!isShowAdditionalCharge(completeProductQnty) || (eoCompleteProductQuantity.customerExtraharge != null && eoCompleteProductQuantity.customerExtraharge.length() != 0))
                    && (!isShowPartsCharge(completeProductQnty) || (eoCompleteProductQuantity.customerPartharge != null && eoCompleteProductQuantity.customerPartharge.length() != 0))
            ){// && (eoCompleteProductQuantity.isComplete || eoCompleteProductQuantity.isNotComplete)) {

            } else {
                return false;
            }
            count++;
        }
        return true;

    }

    public boolean checkChargeAmount() {
        for (EOCompleteProductQuantity eoCompleteProductQuantity : selectedCompleteDetail.getbookingProductUnit().quantity) {
            Double charges = 0.00;
            if (eoCompleteProductQuantity.customerBasicharge != null && eoCompleteProductQuantity.customerBasicharge.length() != 0) {
                charges += Double.valueOf(eoCompleteProductQuantity.customerBasicharge);
            }
            if (eoCompleteProductQuantity.customerExtraharge != null && eoCompleteProductQuantity.customerExtraharge.length() != 0) {
                charges += Double.valueOf(eoCompleteProductQuantity.customerExtraharge);
            }
            if (eoCompleteProductQuantity.customerPartharge != null && eoCompleteProductQuantity.customerPartharge.length() != 0) {
                charges += Double.valueOf(eoCompleteProductQuantity.customerPartharge);
            }
            if (eoCompleteProductQuantity.amountDue != null) {

                if (charges < Double.valueOf(eoCompleteProductQuantity.amountDue)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isShowBasicCharge(EOCompleteProductQuantity eoCompleteProductQuantity) {

        return (eoCompleteProductQuantity.product_or_services.equalsIgnoreCase("service") && !eoCompleteProductQuantity.amountDue.equalsIgnoreCase("0.00"));
    }

    private boolean isShowAdditionalCharge(EOCompleteProductQuantity eoCompleteProductQuantity) {
        return !(eoCompleteProductQuantity.product_or_services.equalsIgnoreCase("product"));
    }

    private boolean isShowPartsCharge(EOCompleteProductQuantity eoCompleteProductQuantity) {
        return !eoCompleteProductQuantity.product_or_services.equalsIgnoreCase("service") ?
                eoCompleteProductQuantity.product_or_services.equalsIgnoreCase("product") && eoCompleteProductQuantity.amountDue.equalsIgnoreCase("0.00")
                : true;
        //  return (eoCompleteProductQuantity.product_or_services.equalsIgnoreCase("service") && eoCompleteProductQuantity.amountDue.equalsIgnoreCase("0.00"));

    }

    private void checkSerialNumber(String serialNumber) {
        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = ProductDetailFragment.this;
        this.actionID = "validateSerialNumber";
        httpRequest.execute(this.actionID, eoBooking.bookingID, serialNumber, selectedCompleteDetail.getbookingProductUnit().quantity.get(0).model_number, selectServiceCatg.getText().toString().trim());

    }

    private void submit() {
//        if (this.selectedCompleteDetail.bookingUnitDetails.purchase_date!=null && isFillAllFeild()) {
//            this.selectedCompleteDetail.bookingUnitDetails.isProductBroken=productBorkenStatus.isChecked();
        Intent intent = new Intent();
        intent.putExtra("productDetail", this.selectedCompleteDetail);//BMAGson.store().toJson(this.partsObject));
        //  intent.putExtra("purchaseDate", this.selectDate.getText().toString());
        // intent.putExtra("isBrokenProduct", this.productBorkenStatus.isChecked());
        intent.putExtra("completeCatogryPageName", "productDetail");
        getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, intent);
        getFragmentManager().popBackStack();
//        } else {
//            Toast.makeText(getContext(), "Please Select All Field", Toast.LENGTH_SHORT).show();
//        }
    }
}
