package com.around.engineerbuddy.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.component.BMAFontViewField;
import com.around.engineerbuddy.component.BMASelectionDialog;
import com.around.engineerbuddy.entity.BMAUiEntity;
import com.around.engineerbuddy.entity.EOAllBookingTask;
import com.around.engineerbuddy.entity.EOAppliance;
import com.around.engineerbuddy.entity.EOApplianceModel;
import com.around.engineerbuddy.entity.EOCheckSparePartPrice;
import com.around.engineerbuddy.entity.EOInventoryParts;
import com.around.engineerbuddy.entity.EOModelNumber;
import com.around.engineerbuddy.entity.EOPartType;
import com.around.engineerbuddy.entity.EOPartners;
import com.around.engineerbuddy.entity.EoWrongPart;
import com.around.engineerbuddy.util.BMAConstants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
 // This class is used to check spare price
public class CheckSparePartPrice extends BMAFragment implements View.OnClickListener {

    HttpRequest httpRequest;
    EOCheckSparePartPrice eoCheckSparePartPrice;
    ArrayList<EOAppliance>partnerAppliances=new ArrayList<>();
    ArrayList<EOApplianceModel>partnerAppliancesModels=new ArrayList<>();
    ArrayList<EOPartType>partTypes=new ArrayList<>();
    ArrayList<EOInventoryParts>parts=new ArrayList<>();
    EOApplianceModel selectedEoApplianceModel;

    String actionId;
    //BMAFontViewField brandFontIcon,ProductDropDownIcon,modelDropDownIcon,partTypeDropDownIcon,sparePartDropDownIcon;
    TextView brandTextview,selectProduct,selectModel,selectpartType,selectsparePart,partPrice,partNumber,gstpartPrice;
    LinearLayout selectBrandLayout,selectProductLayout,selectModelLayout,selectPartTypeLayout,selectsparePartLayout;

    // this callback method is used to bind view with fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.check_spare_price, container, false);
//        this.brandFontIcon = this.view.findViewById(R.id.brandDropDownIcon);
//        this.ProductDropDownIcon = this.view.findViewById(R.id.ProductDropDownIcon);
//        this.modelDropDownIcon = this.view.findViewById(R.id.modelDropDownIcon);
//        this.partTypeDropDownIcon = this.view.findViewById(R.id.partTypeDropDownIcon);
//        this.sparePartDropDownIcon = this.view.findViewById(R.id.sparePartDropDownIcon);
        brandTextview = this.view.findViewById(R.id.selectBrand);
        selectProduct = this.view.findViewById(R.id.selectProduct);
        selectModel = this.view.findViewById(R.id.selectModel);
        selectpartType = this.view.findViewById(R.id.selectpartType);
        selectsparePart = this.view.findViewById(R.id.selectsparePart);
        partPrice = this.view.findViewById(R.id.partPrice);
        partNumber = this.view.findViewById(R.id.partNumber);
        this.gstpartPrice=this.view.findViewById(R.id.gstpartPrice);
        this.selectBrandLayout=this.view.findViewById(R.id.selectBrandLayout);
        this.selectProductLayout=this.view.findViewById(R.id.selectProductLayout);
        this.selectModelLayout=this.view.findViewById(R.id.selectModelLayout);
        this.selectPartTypeLayout=this.view.findViewById(R.id.selectPartTypeLayout);
        this.selectsparePartLayout=this.view.findViewById(R.id.selectsparePartLayout);
        this.selectBrandLayout.setOnClickListener(this);
        this.selectProductLayout.setOnClickListener(this);
        this.selectModelLayout.setOnClickListener(this);
        this.selectPartTypeLayout.setOnClickListener(this);
        this.selectsparePartLayout.setOnClickListener(this);

//        this.brandFontIcon.setOnClickListener(this);
//        this.ProductDropDownIcon.setOnClickListener(this);
//        this.modelDropDownIcon.setOnClickListener(this);
//        this.partTypeDropDownIcon.setOnClickListener(this);
//        this.sparePartDropDownIcon.setOnClickListener(this);
        getRequest();
        return this.view;
    }

    //This method is used to bind request data to call partner API
    private void getRequest() {
        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = CheckSparePartPrice.this;
        String mobile = MainActivityHelper.applicationHelper().getSharedPrefrences(BMAConstants.LOGIN_INFO).getString("phoneNumber", null);

        this.actionId = "partners";
        httpRequest.execute(actionId, mobile);

    }

    // This method is used to bind request data to call partner Appliance API

    private void getpartnerAppliances(EOPartners eoPartners) {

        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = CheckSparePartPrice.this;
        this.actionId = "partnerAppliances";
        httpRequest.execute(actionId, eoPartners.id);
    }

// This method is used to bind request to call ApplinaceModel API
    private void getRequestForApplianceModels(EOAppliance eoAppliance){
        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = CheckSparePartPrice.this;
        this.actionId = "partnerappliancesModels";
        httpRequest.execute(actionId,eoAppliance.id,eoAppliance.partner_id);


    }
     //This method is used to bind request data to call partnerappliancesModelsPartTypes API
    private void partnerappliancesModelsPartTypes(EOApplianceModel eoApplianceModel){
        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = CheckSparePartPrice.this;

        this.actionId = "partnerappliancesModelsPartTypes";
        this.selectedEoApplianceModel=eoApplianceModel;
        httpRequest.execute(actionId, eoApplianceModel.service_id,eoApplianceModel.entity_id);


    }
     //This method is used to bind request data to call partnerappliancesModelsPartTypesInventory API
    private void partnerappliancesModelsPartTypesInventory(EOPartType partType){
        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = CheckSparePartPrice.this;
        this.actionId = "partnerappliancesModelsPartTypesInventory";
        if(this.selectedEoApplianceModel!=null)
        httpRequest.execute(actionId,selectedEoApplianceModel.service_id,selectedEoApplianceModel.entity_id, partType.type);


    }

    // This is callback method and recive response,this method will called after request complete
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
                    String res = jsonObject.getString("response");
                    if (actionId.equalsIgnoreCase("partners")) {

                        eoCheckSparePartPrice = BMAGson.store().getObject(EOCheckSparePartPrice.class, res);
                        Log.d("aaaa","size = "+eoCheckSparePartPrice);
                    }else if(actionId.equalsIgnoreCase("partnerAppliances")){
                         JSONObject applianceJsonObject=jsonObject.getJSONObject("response");
                         if(applianceJsonObject!=null)
                        partnerAppliances=BMAGson.store().getList(EOAppliance.class,applianceJsonObject.getString("partner_appliances"));
                    }else if(actionId.equalsIgnoreCase ("partnerappliancesModels")){
                        JSONObject applianceJsonObject=jsonObject.getJSONObject("response");
                        if(applianceJsonObject!=null)
                            partnerAppliancesModels=BMAGson.store().getList(EOApplianceModel.class,applianceJsonObject.getString("partner_appliances_models"));

                    }else if(actionId.equalsIgnoreCase("partnerappliancesModelsPartTypes")){
                        JSONObject applianceJsonObject=jsonObject.getJSONObject("response");
                        if(applianceJsonObject!=null) {
                            partTypes = BMAGson.store().getList(EOPartType.class,  applianceJsonObject.getString("part_types"));
                        }


                    }else if(this.actionId.equalsIgnoreCase("partnerappliancesModelsPartTypesInventory")){
                        JSONObject applianceJsonObject=jsonObject.getJSONObject("response");
                        if(applianceJsonObject!=null) {
                            parts = BMAGson.store().getList(EOInventoryParts.class,  applianceJsonObject.getString("parts"));
                        }

                    }

                }
            } catch (Exception e) {

            }
        }
    }

    //this is call back method when we click on any button then this method be called
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.selectBrandLayout) {
            showBrandPopup();
        }
        if (v.getId() == R.id.selectProductLayout) {
            showPartnerAppliancesPopup();
        }
        if (v.getId() == R.id.selectModelLayout) {
            showpartnerappliancesModelsPopup();
        }
        if (v.getId() == R.id.selectPartTypeLayout) {
            showpartnerappliancesModelsPartTypesPopup();
        }
        if (v.getId() == R.id.selectsparePartLayout) {
            showPartnerappliancesModelsPartTypesInventoryPopup();
        }
    }

    // To show popup selection for brand list
    private void showBrandPopup() {
        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getBrandList(), true) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if (selectedItems != null) {
                    // partsObject.get(v.getTag()).put("model_number", selectedItems.getDetail1());
                   EOPartners eoPartners= (EOPartners) brandTextview.getTag();
                   EOPartners selectedEoPartner= (EOPartners) selectedItems.tag;
                    brandTextview.setText(selectedItems.getDetail1());
                    brandTextview.setTag(selectedItems.tag);
                    if(selectedEoPartner!=null && eoPartners!=null && selectedEoPartner.id.equalsIgnoreCase(eoPartners.id) ){
                        return;
                    }
                    getpartnerAppliances(selectedEoPartner);
                    selectProduct.setText("");
                    selectProduct.setTag(null);
                    selectModel.setText("");
                    selectModel.setTag(null);
                    selectpartType.setText("");
                    selectpartType.setTag(null);
                    selectsparePart.setText("");
                    selectsparePart.setTag(null);
                    partPrice.setText("");
                    partNumber.setText("");

                }

            }
        };
        bmaSelectionDialog.setTitle(getString(R.string.selectModelNumber));
        bmaSelectionDialog.show();
    }

    // To bind brand list with UiEntity object
    private ArrayList<BMAUiEntity> getBrandList() {
        ArrayList<BMAUiEntity> brandList = new ArrayList<>();
        if (this.eoCheckSparePartPrice == null || eoCheckSparePartPrice.partners == null) {
            return brandList;
        }
        EOPartners eoSelectedPartners= (EOPartners) brandTextview.getTag();
        for (EOPartners eoPartners : this.eoCheckSparePartPrice.partners) {
            BMAUiEntity bmaUiEntity = new BMAUiEntity();
            bmaUiEntity.setDetail1(eoPartners.public_name);
            bmaUiEntity.tag = eoPartners;
            if(eoSelectedPartners!=null && eoSelectedPartners.id.equalsIgnoreCase(eoPartners.id)){
                bmaUiEntity.setChecked(true);
            }
            brandList.add(bmaUiEntity);
        }
        return brandList;

    }

// To show popup selection for partner appliance data

    private void showPartnerAppliancesPopup() {
        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getPartnerAppliancesPopupList(), true) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if (selectedItems != null) {
                    // partsObject.get(v.getTag()).put("model_number", selectedItems.getDetail1());

                    EOAppliance eoAppliance= (EOAppliance) selectProduct.getTag();
                    EOAppliance eoSelectedAppliance= (EOAppliance) selectedItems.tag;
                    selectProduct.setText(selectedItems.getDetail1());
                    selectProduct.setTag(selectedItems.tag);
                    if(eoSelectedAppliance!=null && eoAppliance!=null && eoSelectedAppliance.id.equalsIgnoreCase(eoAppliance.id) ){
                        return;
                    }
                    getRequestForApplianceModels((EOAppliance) selectedItems.tag);
                    selectModel.setText("");
                    selectModel.setTag(null);
                    selectpartType.setText("");
                    selectpartType.setTag(null);
                    selectsparePart.setText("");
                    selectsparePart.setTag(null);
                    partPrice.setText("");
                    partNumber.setText("");
                }

            }
        };
        bmaSelectionDialog.setTitle(getString(R.string.selectModelNumber));
        bmaSelectionDialog.show();
    }

    //   To bind partner appliance list with UiEntity object

    private ArrayList<BMAUiEntity> getPartnerAppliancesPopupList() {
        ArrayList<BMAUiEntity> brandList = new ArrayList<>();
        if (this.partnerAppliances == null) {
            return brandList;
        }
        EOAppliance eoSelectedAppliance= (EOAppliance) selectProduct.getTag();
        for (EOAppliance eoAppliance : partnerAppliances) {
            BMAUiEntity bmaUiEntity = new BMAUiEntity();
            bmaUiEntity.setDetail1(eoAppliance.services);
            bmaUiEntity.tag = eoAppliance;
            if(eoSelectedAppliance!=null && eoSelectedAppliance.id.equalsIgnoreCase(eoAppliance.id)){
                bmaUiEntity.setChecked(true);
            }
            brandList.add(bmaUiEntity);
        }
        return brandList;

    }
// To Show popup selection for appliance model
    private void showpartnerappliancesModelsPopup() {
        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getpartnerappliancesModelsList(), true) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if (selectedItems != null) {
                    // partsObject.get(v.getTag()).put("model_number", selectedItems.getDetail1());
                    EOApplianceModel eoApplianceModel= (EOApplianceModel) selectedItems.tag;
                    EOApplianceModel eoselectedApplianceModel= (EOApplianceModel) selectModel.getTag();
                    selectModel.setText(selectedItems.getDetail1());
                    selectModel.setTag(selectedItems.tag);
                    if(eoselectedApplianceModel!=null && eoApplianceModel!=null && eoselectedApplianceModel.id.equalsIgnoreCase(eoApplianceModel.id) ){
                        return;
                    }
                    partnerappliancesModelsPartTypes(eoApplianceModel);
                    selectpartType.setText("");
                    selectpartType.setTag(null);
                    selectsparePart.setText("");
                    selectsparePart.setTag(null);
                    partPrice.setText("");
                    partNumber.setText("");
                }

            }
        };
        bmaSelectionDialog.setTitle(getString(R.string.selectModelNumber));
        bmaSelectionDialog.show();
    }
    // To bind appliance model list with UiEntity object

    private ArrayList<BMAUiEntity> getpartnerappliancesModelsList() {
        ArrayList<BMAUiEntity> brandList = new ArrayList<>();
        if (this.partnerAppliancesModels == null) {
            return brandList;
        }
        EOApplianceModel eoselectedApplianceModel= (EOApplianceModel) selectModel.getTag();
        for (EOApplianceModel eoApplianceModel : partnerAppliancesModels) {
            BMAUiEntity bmaUiEntity = new BMAUiEntity();
            bmaUiEntity.setDetail1(eoApplianceModel.model_number);
            bmaUiEntity.tag = eoApplianceModel;
            if(eoselectedApplianceModel!=null && eoselectedApplianceModel.id.equalsIgnoreCase(eoApplianceModel.id)){
                bmaUiEntity.setChecked(true);
            }
            brandList.add(bmaUiEntity);
        }
        return brandList;

    }

    // To show popUp selection for part type data
    private void showpartnerappliancesModelsPartTypesPopup() {
        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getpartnerappliancesModelsPartTypesList(), true) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if (selectedItems != null) {
                    // partsObject.get(v.getTag()).put("model_number", selectedItems.getDetail1());
                    EOPartType eoPartType= (EOPartType) selectedItems.tag;
                    EOPartType eoSelectedPartType= (EOPartType) selectpartType.getTag();

                    selectpartType.setText(selectedItems.getDetail1());
                    selectpartType.setTag(selectedItems.tag);
                    if(eoSelectedPartType!=null && eoPartType!=null && eoSelectedPartType.type.equalsIgnoreCase(eoPartType.type) ){
                        return;
                    }
                    partnerappliancesModelsPartTypesInventory( (EOPartType) selectedItems.tag);
                    selectsparePart.setText("");
                    selectsparePart.setTag(null);
                    partPrice.setText("");
                    partNumber.setText("");
                }

            }
        };
        bmaSelectionDialog.setTitle(getString(R.string.selectModelNumber));
        bmaSelectionDialog.show();
    }

    // To bind parttype list with UiEntity object
    private ArrayList<BMAUiEntity> getpartnerappliancesModelsPartTypesList() {
        ArrayList<BMAUiEntity> brandList = new ArrayList<>();
        if (this.partTypes == null ) {
            return brandList;
        }
        EOPartType eoSelectedPartType= (EOPartType) selectpartType.getTag();
        for (EOPartType eoPartType : this.partTypes) {
            BMAUiEntity bmaUiEntity = new BMAUiEntity();
            bmaUiEntity.setDetail1(eoPartType.type);
            bmaUiEntity.tag = eoPartType;
            if(eoSelectedPartType!=null && eoSelectedPartType.type.equalsIgnoreCase(eoPartType.type)){
                bmaUiEntity.setChecked(true);
            }
            brandList.add(bmaUiEntity);
        }
        return brandList;

    }
    //To show popup with inventorty spare data
    private void showPartnerappliancesModelsPartTypesInventoryPopup() {
        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getPartnerappliancesModelsPartTypesInventoryList(), true) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if (selectedItems != null) {
                    // partsObject.get(v.getTag()).put("model_number", selectedItems.getDetail1());
                    selectsparePart.setText(selectedItems.getDetail1());
                    selectsparePart.setTag(selectedItems.tag);
                    EOInventoryParts eoInventoryParts= (EOInventoryParts) selectedItems.tag;
                    partPrice.setText("₹  "+eoInventoryParts.customer_price);
                    partNumber.setText(eoInventoryParts.part_number);
                    gstpartPrice.setVisibility(View.VISIBLE);

                    //getpartnerAppliances((EOInventoryParts) selectedItems.tag);
                }

            }
        };
        bmaSelectionDialog.setTitle(getString(R.string.selectModelNumber));
        bmaSelectionDialog.show();
    }
//this method is used to bind inventory part list with UIEntity object

    private ArrayList<BMAUiEntity> getPartnerappliancesModelsPartTypesInventoryList() {
        ArrayList<BMAUiEntity> brandList = new ArrayList<>();
        if (this.parts==null) {
            return brandList;
        }
        EOInventoryParts eoSelctedinventory= (EOInventoryParts) selectsparePart.getTag();
        for (EOInventoryParts eoInventoryParts : this.parts) {
            BMAUiEntity bmaUiEntity = new BMAUiEntity();
            bmaUiEntity.setDetail1(eoInventoryParts.part_name);
            bmaUiEntity.tag = eoInventoryParts;
            if(eoSelctedinventory!=null && eoSelctedinventory.part_number.equalsIgnoreCase(eoInventoryParts.part_number)){
                bmaUiEntity.setChecked(true);
            }
            brandList.add(bmaUiEntity);
        }
        return brandList;

    }


}
