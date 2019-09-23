package com.around.engineerbuddy.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.component.BMASelectionDialog;
import com.around.engineerbuddy.entity.BMAUiEntity;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.entity.EOSymptomDefect;
import com.around.engineerbuddy.entity.EOSymtoms;
import com.around.engineerbuddy.util.BMAConstants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SymptomFragment extends BMAFragment implements View.OnClickListener {
    Button submitButton;
    private HttpRequest httpRequest;
    String actionID;
    private EOBooking eoBooking;
    EditText selectSympton,selectDefect,selectSolution,closingRemarks;
   // BMAFontViewField symptomDropDownIcon,defectDropDownIcon,solutionDropDownIcon;
    ArrayList<EOSymptomDefect> symptomsList=new ArrayList<>();
    ArrayList<EOSymptomDefect> solutionList=new ArrayList<>();
    EOSymtoms eoSymptoms;
    EOSymptomDefect selectedEOSymptomDefect;
    LinearLayout modelLayout,defectLayout,solutionLayout;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.eoBooking = getArguments().getParcelable("eoBooking");
        this.selectedEOSymptomDefect=getArguments().getParcelable("eoSelectedSymptomDefect");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.sympton_layout, container, false);
        this.view.findViewById(R.id.cancelButton).setVisibility(View.GONE);
        this.submitButton=this.view.findViewById(R.id.submitButton);
//        this.symptomDropDownIcon=this.view.findViewById(R.id.symptomDropDownIcon);
//        this.defectDropDownIcon=this.view.findViewById(R.id.defectDropDownIcon);
//        this.solutionDropDownIcon=this.view.findViewById(R.id.solutionDropDownIcon);
        this.selectSympton=this.view.findViewById(R.id.selectSympton);
        this.selectDefect=this.view.findViewById(R.id.selectDefect);
        this.selectSolution=this.view.findViewById(R.id.selectSolution);
        TextView bookingRemarks=this.view.findViewById(R.id.booking_remarks);
        this.modelLayout=this.view.findViewById(R.id.modelLayout);
        this.defectLayout=this.view.findViewById(R.id.defectLayout);
        this.solutionLayout=this.view.findViewById(R.id.solutionLayout);
        if(this.eoBooking.bookingRemarks!=null)
        bookingRemarks.setText(this.eoBooking.bookingRemarks);
        this.closingRemarks=this.view.findViewById(R.id.Problemdescriptionedittext);
        this.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String remarks=closingRemarks.getText().toString().trim();
                if(remarks==null || remarks.length()==0){
                    Toast.makeText(getContext(), "Please enter closing remarks", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!isSelectAllField()){
                    return;
                }
                selectedEOSymptomDefect.remarks=remarks;
                Intent intent= new Intent();
                intent.putExtra("selectedEOSymptomDefect",selectedEOSymptomDefect);
                intent.putExtra("completeCatogryPageName","symptom");
                getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode,intent);
                getFragmentManager().popBackStack();
            }
        });
        this.modelLayout.setOnClickListener(this);
        this.defectLayout.setOnClickListener(this);
        this.solutionLayout.setOnClickListener(this);
        if(selectedEOSymptomDefect==null){
            selectedEOSymptomDefect=new EOSymptomDefect();
        }
        if(this.selectedEOSymptomDefect!=null){
            selectSympton.setText(this.selectedEOSymptomDefect.symptom);
            selectDefect.setText(this.selectedEOSymptomDefect.defect);
            selectSolution.setText(this.selectedEOSymptomDefect.technical_solution);
            closingRemarks.setText(this.selectedEOSymptomDefect.remarks);
        }
        this.getSymtomRequest();
        return this.view;
    }

    private void getSymtomRequest() {
        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = SymptomFragment.this;
        //Log.d("bbbbbbb", "partnerID =   = " + eoBooking.partnerID + "    serviceID =" + eoBooking.serviceID);
        this.actionID = "symptomCompleteBooking";
        httpRequest.execute(actionID, eoBooking.bookingID,eoBooking.serviceID,eoBooking.partnerID,eoBooking.requestType);
    }
    private void getdefectCompleteBookingRequest(int symptomID) {
        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = SymptomFragment.this;
        //Log.d("bbbbbbb", "partnerID =   = " + eoBooking.partnerID + "    serviceID =" + eoBooking.serviceID);
        this.actionID = "defectCompleteBooking";
        httpRequest.execute(actionID,symptomID+"");
    }
    private void getsolutionCompleteBookingRequest( int symptomID,int defectID ) {
        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = SymptomFragment.this;
        //Log.d("bbbbbbb", "partnerID =   = " + eoBooking.partnerID + "    serviceID =" + eoBooking.serviceID);
        this.actionID = "solutionCompleteBooking";
        httpRequest.execute(actionID,symptomID+"",defectID+"");
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
                    if (this.actionID.equalsIgnoreCase("symptomCompleteBooking") ) {
                      //  String responseData = (new JSONObject(jsonObject.getString("response")).getString("symptoms"));
                        this.eoSymptoms = BMAGson.store().getObject(EOSymtoms.class, jsonObject.getString("response"));
                       // String responseData = (new JSONObject(jsonObject.getString("response")).getString("sparePartsOrder"));
//                        this.eoSparePartsOrder = BMAGson.store().getObject(EOSparePartsOrder.class, responseData);
//                        if (this.eoSparePartsOrder != null && this.eoSparePartsOrder.modelNumberList.size() < 1) {
//                            selectModel.setEnabled(true);
//                            this.modelDropDownIcon.setEnabled(false);
//                        }
                    } else if (this.actionID.equalsIgnoreCase("defectCompleteBooking")) {
                        String responseData =jsonObject.getString("response");
                        symptomsList =  BMAGson.store().getList(EOSymptomDefect.class, responseData);

//                        partTypeList = BMAGson.store().getList(EOPartType.class, responseData);
//                        Log.d("aaaaa", "size = " + partTypeList.get(0));
                    } else if (this.actionID.equalsIgnoreCase("solutionCompleteBooking")) {
                        String responseData =jsonObject.getString("response");
                        solutionList =  BMAGson.store().getList(EOSymptomDefect.class, responseData);
//                        partNameList = BMAGson.store().getList(EOPartName.class, jsonObject.getString("response"));
//                        Log.d("bbbbbbb", "spareparts  = " );
//                        if(partNameList==null || partNameList.size()==0){
//                            selectedPartName.setEnabled(true);
//                            selectedPartNameDropDownIcon.setEnabled(false);
//
//                        }else{
//
//                        }
                    }
                    //  Log.d("aaaaa","eosapre = "+"        size = "+eoSparePartsOrder.modelNumberList.size());
//                    spareModelList = new ArrayList<EOSparePartsOrder>(responseData.getJSONArray("model_number"));
//                    spareModelList=jsonObject.getJSONArray("response");


                }
            } catch (Exception e) {

            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.modelLayout:
                openSymtonPopUp();
                break;
            case R.id.defectLayout:
                openDefectPartPopUp();
                break;
            case R.id.solutionLayout:
                openSolutionPartPopUp();
                break;
        }
    }

    private void openSymtonPopUp(){

        if(this.eoSymptoms ==null){
            return;
        }
        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getSymptonList()) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if(selectedItems!=null) {
                    selectSympton.setText(selectedItems.getDetail1());
                    EOSymptomDefect symptom= (EOSymptomDefect) selectedItems.tag;
                    selectSympton.setTag(symptom);
                    selectedEOSymptomDefect.symptom=selectedItems.getDetail1();
                    selectedEOSymptomDefect.id=symptom.id;
//                    selectPartType.setText(selectedItems.getDetail1());
//                    selectPartType.setTag(selectedItems.tag);
//                    selectedPartType = (EOPartType) selectedItems.tag;
//                    partsMap.put("parts_type",selectedItems.getDetail1());
                    getdefectCompleteBookingRequest(symptom.id);
                }else if(getSymptonList().size()==1){
                    BMAUiEntity bmaUiEntity=getSymptonList().get(0);
                            selectSympton.setText(bmaUiEntity.getDetail1());
                    EOSymptomDefect symptom= (EOSymptomDefect) bmaUiEntity.tag;
                    selectSympton.setTag(symptom);
                    selectedEOSymptomDefect.symptom=bmaUiEntity.getDetail1();
                    selectedEOSymptomDefect.id=symptom.id;
                    getdefectCompleteBookingRequest(symptom.id);
                }

            }
        };
        bmaSelectionDialog.setTitle(getString(R.string.selectModelNumber));
        bmaSelectionDialog.show();
    }
    private ArrayList<BMAUiEntity> getSymptonList(){
        ArrayList<BMAUiEntity> symptomList=new ArrayList<>();

        for(EOSymptomDefect eoSymptom:this.eoSymptoms.symptoms){
            BMAUiEntity bmaUiEntity=new BMAUiEntity();
            bmaUiEntity.setDetail1(eoSymptom.symptom);
            if(this.eoSymptoms.symptoms.size()==1){
                bmaUiEntity.setChecked(true);
            }
            bmaUiEntity.tag=eoSymptom;
            symptomList.add(bmaUiEntity);
        }
        return symptomList;
    }
    private void openDefectPartPopUp(){

        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getDefectPartList()) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if(selectedItems!=null) {
                    selectDefect.setText(selectedItems.getDetail1());
                    EOSymptomDefect eoSymptom=((EOSymptomDefect)selectedItems.tag);
                    selectDefect.setTag(eoSymptom);
                    selectedEOSymptomDefect.defect=selectedItems.getDetail1();
                    selectedEOSymptomDefect.defect_id=eoSymptom.defect_id;
                    getsolutionCompleteBookingRequest(((EOSymptomDefect)selectSympton.getTag()).id,eoSymptom.defect_id);
                } else if(getDefectPartList().size()==1){
                    BMAUiEntity bmaUiEntity=getDefectPartList().get(0);
                    selectDefect.setText(bmaUiEntity.getDetail1());
                    EOSymptomDefect symptom= (EOSymptomDefect) bmaUiEntity.tag;
                    selectDefect.setTag(symptom);
                    selectedEOSymptomDefect.defect_id=symptom.defect_id;
                    selectedEOSymptomDefect.defect=bmaUiEntity.getDetail1();
                    getsolutionCompleteBookingRequest(symptom.id,symptom.defect_id);
                }

            }
        };
        bmaSelectionDialog.setTitle(getString(R.string.selectDefective));
        bmaSelectionDialog.show();
    }
    private ArrayList<BMAUiEntity> getDefectPartList(){
        ArrayList<BMAUiEntity> defectPartList=new ArrayList<>();

        for(EOSymptomDefect eoSymptom:this.symptomsList){
            BMAUiEntity bmaUiEntity=new BMAUiEntity();
            bmaUiEntity.setDetail1(eoSymptom.defect);
            bmaUiEntity.tag=eoSymptom;
            if(this.symptomsList.size()==1){
                bmaUiEntity.setChecked(true);
            }
            defectPartList.add(bmaUiEntity);
        }
        return defectPartList;

    }
    private void openSolutionPartPopUp(){

        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getSolutionPartList()) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if(selectedItems!=null) {
                    selectSolution.setText(selectedItems.getDetail1());
                    selectSolution.setTag(selectedItems.tag);
                    selectedEOSymptomDefect.technical_solution=selectedItems.getDetail1();
                    selectedEOSymptomDefect.solution_id=((EOSymptomDefect)selectedItems.tag).solution_id;

//                    selectPartType.setText(selectedItems.getDetail1());
//                    selectPartType.setTag(selectedItems.tag);
//                    selectedPartType = (EOPartType) selectedItems.tag;
//                    partsMap.put("parts_type",selectedItems.getDetail1());
                    //getsolutionCompleteBookingRequest();
                }else if(getSolutionPartList().size()==1){
                    BMAUiEntity bmaUiEntity=getSolutionPartList().get(0);
                    selectSolution.setText(bmaUiEntity.getDetail1());
                    selectSolution.setTag(bmaUiEntity.tag);
                    selectedEOSymptomDefect.technical_solution=bmaUiEntity.getDetail1();
                    selectedEOSymptomDefect.solution_id=((EOSymptomDefect)bmaUiEntity.tag).solution_id;
                }


            }
        };
        bmaSelectionDialog.setTitle(getString(R.string.selectSolution));
        bmaSelectionDialog.show();
    }
    private ArrayList<BMAUiEntity> getSolutionPartList(){
        ArrayList<BMAUiEntity> solutionList=new ArrayList<>();

        for(EOSymptomDefect eoSymptom:this.solutionList){
            BMAUiEntity bmaUiEntity=new BMAUiEntity();
            bmaUiEntity.setDetail1(eoSymptom.technical_solution);
            bmaUiEntity.tag=eoSymptom;
            if(this.solutionList.size()==1){
                bmaUiEntity.setChecked(true);
            }
            solutionList.add(bmaUiEntity);
        }
        return solutionList;
    }
    private boolean isSelectAllField(){
        if(selectSympton.getText().toString().trim().length()==0){
            Toast.makeText(getContext(), "Please Select Symptom", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(selectDefect.getText().toString().trim().length()==0){
            Toast.makeText(getContext(), "Please Select Defect", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(selectSolution.getText().toString().trim().length()==0){
            Toast.makeText(getContext(), "Please Select solution", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
