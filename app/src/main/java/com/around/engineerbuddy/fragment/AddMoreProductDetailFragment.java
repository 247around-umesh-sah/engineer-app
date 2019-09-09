package com.around.engineerbuddy.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.around.engineerbuddy.R;
import com.around.engineerbuddy.component.BMAFontViewField;
import com.around.engineerbuddy.component.BMASelectionDialog;
import com.around.engineerbuddy.entity.BMAUiEntity;
import com.around.engineerbuddy.entity.EOAddMoreProduct;
import com.around.engineerbuddy.entity.EOCompleteProductQuantity;
import com.around.engineerbuddy.entity.EOCompleteProductdetail;
import com.around.engineerbuddy.entity.EOModelNumber;
import com.around.engineerbuddy.util.BMAConstants;
import com.around.engineerbuddy.util.BMAUIUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddMoreProductDetailFragment extends BMAFragment implements View.OnClickListener {
    Button submit, addMoreProduct;
    LinearLayout addMorePartParentLayout;
    ScrollView addMorePartScrollView;
    LayoutInflater inflater;
    int counter;
    EOAddMoreProduct eoAddMoreProduct = new EOAddMoreProduct();
    //ArrayList<HashMap<String,String>>
    HashMap<String, Object> partsMap = new HashMap<>();
    HashMap<Integer, HashMap<String, Object>> partsObject = new HashMap<>();
    public ArrayList<EOCompleteProductQuantity> prices = new ArrayList<>();
    EOCompleteProductdetail selectedCompleteDetail;

    // String productDetail, selectDate;
    // boolean isBroken;
    EOModelNumber eoModelNumber;
    public String unitDetailKey;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.selectedCompleteDetail = this.getArguments().getParcelable("productDetail");
        this.eoModelNumber = this.getArguments().getParcelable("eoModel");
        this.prices = this.getArguments().getParcelableArrayList("prices");
        //  this.productDetail = this.getArguments().getString("productDetail");
        //this.selectDate = this.getArguments().getString("selectDate");
        // this.isBroken = this.getArguments().getBoolean("isBrokenProduct");
        this.unitDetailKey = this.getArguments().getString("unitDetailKey");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.add_more_part_layout, container, false);
        this.inflater = inflater;
        //this.view.findViewById(R.id.footerLayout).setVisibility(View.VISIBLE);
        this.submit = this.view.findViewById(R.id.submitButton);
        this.addMoreProduct = this.view.findViewById(R.id.cancelButton);
        this.addMorePartParentLayout = this.view.findViewById(R.id.addMorePartLayout);
        this.addMorePartScrollView = this.view.findViewById(R.id.addMorePartScrollView);
        this.submit.setText(getString(R.string.hint_save));
        this.addMoreProduct.setText(getString(R.string.addMore));
        this.submit.setOnClickListener(this);
        // this.addMoreProduct.setOnClickListener(this);
        int dialogRadius = R.dimen._20dp;
        this.addMoreProduct.setVisibility(View.GONE);
        BMAUIUtil.setBackgroundRound(this.submit, BMAUIUtil.getColor(R.color.missedBookingcolor), new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
        //BMAUIUtil.setBackgroundRound(this.addMoreProduct, BMAUIUtil.getColor(R.color.missedBookingcolor), new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

//        if(this.selectedCompleteDetail.prices.size()==0){
//            this.selectedCompleteDetail.prices.add(new EOCompleteProductQuantity());
//            this.addMoreProductLayout(inflater, prices.get(0),this.selectedCompleteDetail.prices.get(counter));
//        }else{
//            for(EOCompleteProductQuantity eoCompleteProductQuantity:this.selectedCompleteDetail.prices){
//                this.addMoreProductLayout(inflater, prices.get(0),eoCompleteProductQuantity);
//            }
//        }

        for (EOCompleteProductQuantity eoCompleteProductQuantity : this.prices) {
            EOCompleteProductQuantity selectedProductQuantity = new EOCompleteProductQuantity();
            if (selectedCompleteDetail.prices.size() != this.prices.size()) {
                selectedCompleteDetail.prices.add(selectedProductQuantity);
            }
            if (selectedCompleteDetail != null && selectedCompleteDetail.prices.size() > counter) {
                selectedProductQuantity = selectedCompleteDetail.prices.get(counter);
            }
            addMoreProductLayout(inflater, eoCompleteProductQuantity, selectedProductQuantity);
        }

        return this.view;
    }

    private void addMoreProductLayout(LayoutInflater inflater, EOCompleteProductQuantity eoCompleteProductQuantity, EOCompleteProductQuantity selectedProductQuantity) {
        View childView = inflater.inflate(R.layout.product_item_layout, null, false);
        partsObject.put(counter, new HashMap<>());
        partsObject.get(counter).put("unit_id", unitDetailKey + "new" + eoCompleteProductQuantity.id);
        partsObject.get(counter).put("amount_paid", eoCompleteProductQuantity.amountDue);

        LinearLayout productLayout = childView.findViewById(R.id.productLayout);
        productLayout.setBackgroundColor(BMAUIUtil.getColor(R.color.lightSkyColor));
        TextView selectServiceCatg = childView.findViewById(R.id.selectServiceCatg);
       // TextView selectModel = childView.findViewById(R.id.selectModel);
        BMAFontViewField serviceCatgdropDownIcon = childView.findViewById(R.id.ServiceCatgdropDownIcon);
        BMAFontViewField modeldropDownIcon = childView.findViewById(R.id.modeldropDownIcon);

        EditText enterSerialNumber = childView.findViewById(R.id.enterSerialNumber);
        TextView serialNoPhoto = childView.findViewById(R.id.serialNoPhoto);
        TextView amountDue = childView.findViewById(R.id.amountDue);
        TextView serialNoPicLink = childView.findViewById(R.id.serialNoPicLink);
        ImageView serialNoPicImage = childView.findViewById(R.id.serialNoPic);
        EditText basichChargeEdittext = childView.findViewById(R.id.basichChargeEdittext);
        EditText additionalChargeEdittext = childView.findViewById(R.id.additionalChargeEdittext);
        EditText partsCostEdittext = childView.findViewById(R.id.partsCostEdittext);
        //  serviceCatgdropDownIcon.setVisibility(View.VISIBLE);
        serviceCatgdropDownIcon.setTag(counter);
        childView.findViewById(R.id.modelLayout).setVisibility(View.GONE);
        childView.findViewById(R.id.enterSerialNoLayout).setVisibility(View.GONE);
        childView.findViewById(R.id.serialNumberPhotoLayout).setVisibility(View.GONE);
        childView.findViewById(R.id.basicChargeLayout).setVisibility(this.isShowBasicCharge(eoCompleteProductQuantity) ? View.VISIBLE : View.GONE);
        childView.findViewById(R.id.additionalChargeLayout).setVisibility(this.isShowAdditionalCharge(eoCompleteProductQuantity) ? View.VISIBLE : View.GONE);
        childView.findViewById(R.id.partsChargeLayout).setVisibility(this.isShowPartsCharge(eoCompleteProductQuantity) ? View.VISIBLE : View.GONE);
        amountDue.setText("₹ " + eoCompleteProductQuantity.amountDue);
        selectServiceCatg.setText(eoCompleteProductQuantity.serviceCategory);
        basichChargeEdittext.setTag(counter);
        basichChargeEdittext.addTextChangedListener(new CustomeTextWatcher(basichChargeEdittext, "service_charge"));
        additionalChargeEdittext.setTag(counter);
        additionalChargeEdittext.addTextChangedListener(new CustomeTextWatcher(additionalChargeEdittext, "additional_service_charge"));
        partsCostEdittext.setTag(counter);
        partsCostEdittext.addTextChangedListener(new CustomeTextWatcher(partsCostEdittext, "parts_cost"));


        RadioGroup radioGroup = childView.findViewById(R.id.radioStatus);
        radioGroup.setTag(counter);
        RadioButton complete = childView.findViewById(R.id.radioComplete);
        RadioButton notComplete = childView.findViewById(R.id.radioNotComplete);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int partKey = (Integer) group.getTag();
                switch (checkedId) {
                    case R.id.radioComplete:
                        partsObject.get(partKey).put("complete", true);
                        selectedCompleteDetail.prices.get(partKey).isComplete = true;
                        selectedCompleteDetail.prices.get(partKey).isNotComplete = false;
                        basichChargeEdittext.setEnabled(true);
                        additionalChargeEdittext.setEnabled(true);
                        partsCostEdittext.setEnabled(true);
                        // partsMap.put(,);
                        // do operations specific to this selection
                        break;
                    case R.id.radioNotComplete:
                        selectedCompleteDetail.prices.get(partKey).isComplete = false;
                        selectedCompleteDetail.prices.get(partKey).isNotComplete = true;
                        basichChargeEdittext.setEnabled(false);
                        additionalChargeEdittext.setEnabled(false);
                        partsCostEdittext.setEnabled(false);
                        partsObject.get(partKey).put("complete", false);
                        basichChargeEdittext.setText("0");
                        additionalChargeEdittext.setText("0");
                        partsCostEdittext.setText("0");
                        // do operations specific to this selection
                        break;
                }
            }
        });

        childView.findViewById(R.id.numberHeaderLayout).setVisibility(View.VISIBLE);//(counter == 0 ? View.GONE : View.VISIBLE);


        TextView rowPartNumber = childView.findViewById(R.id.rowPartNumber);
        LinearLayout productPartLayout = childView.findViewById(R.id.productPartLayout);
        BMAFontViewField expandArrowIcon = childView.findViewById(R.id.expandArrowIcon);
        expandArrowIcon.setVisibility(View.GONE);
        ///// childView.findViewById(R.id.serialPhotoLayout).setOnClickListener(this);
        rowPartNumber.setText(counter + 1 + "");
//        expandArrowIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //  Log.d("aaaaaa", "counter  = " + counter);
//                productPartLayout.setVisibility(productPartLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
//            }
//        });
        LinearLayout spareNumberLayout = childView.findViewById(R.id.spareNumberLayout);
        childView.findViewById(R.id.basicChargeLayout).setVisibility(this.isShowBasicCharge(eoCompleteProductQuantity) ? View.VISIBLE : View.GONE);
        childView.findViewById(R.id.additionalChargeLayout).setVisibility(this.isShowAdditionalCharge(eoCompleteProductQuantity) ? View.VISIBLE : View.GONE);
        childView.findViewById(R.id.partsChargeLayout).setVisibility(this.isShowPartsCharge(eoCompleteProductQuantity) ? View.VISIBLE : View.GONE);

        int dialogRadius = 40;
        selectedProductQuantity.newUnitId = unitDetailKey + "new" + eoCompleteProductQuantity.id;
        if (counter > 0)
            BMAUIUtil.setBackgroundRound(spareNumberLayout, R.color.colorPrimary, new float[]{0, 0, dialogRadius, dialogRadius, dialogRadius, dialogRadius, 0, 0});


        if (selectedProductQuantity != null) {
            selectedProductQuantity.product_or_services = eoCompleteProductQuantity.product_or_services;
            selectedProductQuantity.amountDue = eoCompleteProductQuantity.amountDue;



            if (selectedProductQuantity.serviceCategory != null) {
                selectServiceCatg.setText(selectedProductQuantity.serviceCategory);
            }
            if (selectedProductQuantity.customerBasicharge != null) {
                basichChargeEdittext.setText(selectedProductQuantity.customerBasicharge);
            } else {
                basichChargeEdittext.setText(eoCompleteProductQuantity.customer_paid_basic_charges);
            }
            if (selectedProductQuantity.customerExtraharge != null) {
                additionalChargeEdittext.setText(selectedProductQuantity.customerExtraharge);
            } else {
                additionalChargeEdittext.setText(eoCompleteProductQuantity.customer_paid_extra_charges);
            }
            if (selectedProductQuantity.customerPartharge != null) {
                partsCostEdittext.setText(selectedProductQuantity.customerPartharge);
            } else {
                partsCostEdittext.setText(eoCompleteProductQuantity.customer_paid_parts);
            }
            complete.setChecked(selectedProductQuantity.isComplete);
            notComplete.setChecked(selectedProductQuantity.isNotComplete);
        }

        this.addMorePartParentLayout.addView(childView);
        this.addMorePartScrollView.fullScroll(View.FOCUS_DOWN);

        counter++;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitButton:
                if (isFillAllFeild()) {
                    boolean isQuantityStatus=false;
                    if(!isStatusSelecteOnce) {
                        for (EOCompleteProductQuantity selectCompleteProduct : this.selectedCompleteDetail.getbookingProductUnit().quantity) {
                            if (selectCompleteProduct.isComplete) {
                                isQuantityStatus = true;
                            }
                        }
                        if(!isQuantityStatus){
                            Toast.makeText(getContext(),getString(R.string.selectCompleteOnce),Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    Intent intent = new Intent();
                    //  intent.putExtra("AddMoreproductDetail", BMAGson.store().toJson(this.partsObject));
                    intent.putExtra("productDetail", selectedCompleteDetail);
//                    intent.putExtra("selectDate",selectDate);
//                    intent.putExtra("isBrokenProduct",isBroken);
                    intent.putExtra("addMorePart", "addMorePart");
                    // intent.putExtra("completeCatogryPageName","productDetail");
                    getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, intent);
                    getFragmentManager().popBackStack();
                }else {
                    Toast.makeText(getContext(),getString(R.string.checkSelectedFieldsValidation),Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cancelButton:
                if (isFillAllFeild()) {


//                if (prices != null && counter < prices.size()) {
                    //partsObject.put(counter, new HashMap<String, Object>());
                    if (this.selectedCompleteDetail.prices.size() < prices.size()) {
                        this.selectedCompleteDetail.prices.add(new EOCompleteProductQuantity());
                        this.addMoreProductLayout(this.inflater, this.prices.get(counter), this.selectedCompleteDetail.prices.get(counter));

                    }
                }else{

                    Toast.makeText(getContext(),getString(R.string.checkSelectedFieldsValidation),Toast.LENGTH_SHORT).show();
                }


                // }
                break;
        }
    }

    private void openServicePartDropDown(TextView selectServiceCatg, Object tag) {
        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getServiceCatgArray(selectServiceCatg)) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if (selectedItems != null) {
                    selectServiceCatg.setText(selectedItems.getDetail1());
                    selectServiceCatg.setTag(selectedItems.tag);
                    partsObject.get(tag).put("service_category", true);
                    selectedCompleteDetail.prices.get((int) tag).serviceCategory = selectedItems.getDetail1();
                    selectedCompleteDetail.prices.get((int) tag).id = ((EOCompleteProductQuantity) selectedItems.tag).id;

                }

            }
        };
        bmaSelectionDialog.setTitle(getString(R.string.selectModelNumber));
        bmaSelectionDialog.show();
    }

    private ArrayList<BMAUiEntity> getServiceCatgArray(TextView selectServiceCatg) {
        ArrayList<BMAUiEntity> bmaUiEntityArrayList = new ArrayList<>();
        if (this.prices == null) {
            return bmaUiEntityArrayList;
        }
        for (EOCompleteProductQuantity eoCompleteProductQuantity : this.prices) {
            BMAUiEntity bmaUiEntity = new BMAUiEntity();
            bmaUiEntity.setDetail1(eoCompleteProductQuantity.serviceCategory);
            if (selectServiceCatg.getTag() != null && ((EOCompleteProductQuantity) selectServiceCatg.getTag()).serviceCategory.equalsIgnoreCase(eoCompleteProductQuantity.serviceCategory)) {
                bmaUiEntity.setChecked(true);
            }
            bmaUiEntity.tag = eoCompleteProductQuantity;
            bmaUiEntityArrayList.add(bmaUiEntity);
        }
        return bmaUiEntityArrayList;
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
            partsObject.get(editText.getTag()).put(key, value);

            int getKey = (int) editText.getTag();
            EOCompleteProductQuantity selectedProductPrice = selectedCompleteDetail.prices.get(getKey);
            if (key.equalsIgnoreCase("service_charge")) {
                selectedProductPrice.customerBasicharge = value;
            }
//            if (key.equalsIgnoreCase("serial_number")) {
//                selectedProductPrice.serialNumber = value;
//            }
            if (key.equalsIgnoreCase("additional_service_charge")) {
                selectedProductPrice.customerExtraharge = value;
            }
            if (key.equalsIgnoreCase("parts_cost")) {
                selectedProductPrice.customerPartharge = value;
            }
        }
    }
    boolean isStatusSelecteOnce;
    private boolean isFillAllFeild() {

        isStatusSelecteOnce=false;
        int count = 0;

        for (EOCompleteProductQuantity eoCompleteProductQuantity : selectedCompleteDetail.prices) {

            EOCompleteProductQuantity eoCompletePrdctQnty = this.prices.get(count);
            if(!eoCompleteProductQuantity.isNotComplete && !eoCompleteProductQuantity.isComplete  ){

            }
            else if(eoCompleteProductQuantity.isNotComplete || (eoCompleteProductQuantity.serviceCategory != null && (eoCompleteProductQuantity.isComplete || eoCompleteProductQuantity.isNotComplete) &&
                    (!isShowBasicCharge(eoCompletePrdctQnty)) || (eoCompleteProductQuantity.customerBasicharge != null && eoCompleteProductQuantity.customerBasicharge.length() > 0) &&
                    (!isShowAdditionalCharge(eoCompletePrdctQnty)) || (eoCompleteProductQuantity.customerExtraharge != null && eoCompleteProductQuantity.customerExtraharge.length() > 0) &&
                    (!isShowPartsCharge(eoCompletePrdctQnty)) || (eoCompleteProductQuantity.customerPartharge != null && eoCompleteProductQuantity.customerPartharge.length() > 0)) ){


            } else {
                return false;
            }
            count++;

            if(eoCompleteProductQuantity.isComplete){
                isStatusSelecteOnce=true;
            }
        }
        return true;
    }


    private boolean isSelectAllField(HashMap<String, Object> selectedMap, int key) {
        EOCompleteProductQuantity eoCompleteProductQuantity = prices.get(key);
        return selectedMap != null && selectedMap.get("serviceCatg") != null && (!isShowBasicCharge(eoCompleteProductQuantity) || (selectedMap.get("service_charge") != null && ((String) selectedMap.get("service_charge")).length() > 0)) && (!isShowAdditionalCharge(eoCompleteProductQuantity) || (selectedMap.get("additional_service_charge") != null && ((String) selectedMap.get("additional_service_charge")).length() > 0)) && (!isShowPartsCharge(eoCompleteProductQuantity) || (selectedMap.get("parts_cost") != null && ((String) selectedMap.get("parts_cost")).length() > 0))
                && selectedMap.get("complete") != null;
    }

    private boolean isShowBasicCharge(EOCompleteProductQuantity eoCompleteProductQuantity) {

        return (eoCompleteProductQuantity.product_or_services.equalsIgnoreCase("product") && !eoCompleteProductQuantity.amountDue.equalsIgnoreCase("0.00"));

    }

    private boolean isShowAdditionalCharge(EOCompleteProductQuantity eoCompleteProductQuantity) {
        return (eoCompleteProductQuantity.product_or_services.equalsIgnoreCase("service"));
    }

    private boolean isShowPartsCharge(EOCompleteProductQuantity eoCompleteProductQuantity) {
        return !eoCompleteProductQuantity.product_or_services.equalsIgnoreCase("Service");//&& eoCompleteProductQuantity.amountDue.equalsIgnoreCase("0.00"));

    }

}
