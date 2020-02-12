package com.around.engineerbuddy.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.component.BMAFontViewField;
import com.around.engineerbuddy.component.BMASelectionDialog;
import com.around.engineerbuddy.entity.BMAUiEntity;
import com.around.engineerbuddy.entity.EOAllBookingTask;
import com.around.engineerbuddy.entity.EOCheckSparePartPrice;
import com.around.engineerbuddy.entity.EOModelNumber;
import com.around.engineerbuddy.entity.EOPartners;
import com.around.engineerbuddy.util.BMAConstants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CheckSparePartPrice extends BMAFragment implements View.OnClickListener {

    HttpRequest httpRequest;
    EOCheckSparePartPrice eoCheckSparePartPrice;
    String actionId;
    BMAFontViewField brandFontIcon;
    TextView brandTextview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.check_spare_price, container, false);
        this.brandFontIcon = this.view.findViewById(R.id.brandDropDownIcon);
        brandTextview = this.view.findViewById(R.id.selectBrand);
        this.brandFontIcon.setOnClickListener(this);
        getRequest();
        return this.view;
    }

    private void getRequest() {
        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = CheckSparePartPrice.this;
        String mobile = MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("phoneNumber", null);
        Log.d("aaaaa", "mobile no = " + mobile);
        this.actionId = "partners";
        httpRequest.execute(actionId, mobile);

    }

    @Override
    public void processFinish(String response) {
        this.httpRequest.progress.dismiss();
        if (response.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(response);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                if (statusCode.equals("0000")) {

                    if (actionId.equalsIgnoreCase("partners")) {
                        String res = jsonObject.getString("response");
                        eoCheckSparePartPrice = BMAGson.store().getObject(EOCheckSparePartPrice.class, res);
                        Log.d("aaaa","size = "+eoCheckSparePartPrice);
                    }

                }
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.brandDropDownIcon) {
            showBrandPopup();
        }
    }

    private void showBrandPopup() {
        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getBrandList(), true) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if (selectedItems != null) {
                    // partsObject.get(v.getTag()).put("model_number", selectedItems.getDetail1());
                    brandTextview.setText(selectedItems.getDetail1());
                    brandTextview.setTag(selectedItems.tag);
                    getpartnerAppliances((EOPartners) selectedItems.tag);
                    // selectedEOModelNumber = (EOModelNumber) selectedItems.tag;

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

    private ArrayList<BMAUiEntity> getBrandList() {
        ArrayList<BMAUiEntity> brandList = new ArrayList<>();
        if (this.eoCheckSparePartPrice == null || eoCheckSparePartPrice.partners == null) {
            return brandList;
        }
        for (EOPartners eoPartners : this.eoCheckSparePartPrice.partners) {
            BMAUiEntity bmaUiEntity = new BMAUiEntity();
            bmaUiEntity.setDetail1(eoPartners.public_name);
            bmaUiEntity.tag = eoPartners;
            brandList.add(bmaUiEntity);
        }
        return brandList;

    }

    private void getpartnerAppliances(EOPartners eoPartners) {

        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = CheckSparePartPrice.this;
        String mobile = MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("phoneNumber", null);
        Log.d("aaaaa", "mobile no = " + mobile);
        this.actionId = "partnerAppliances";
        httpRequest.execute(actionId, eoPartners.id);
    }
}
