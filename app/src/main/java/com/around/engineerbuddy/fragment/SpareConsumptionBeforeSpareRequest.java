package com.around.engineerbuddy.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.around.engineerbuddy.adapters.BMARecyclerAdapter;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.component.BMAFontViewField;
import com.around.engineerbuddy.component.BMASelectionDialog;
import com.around.engineerbuddy.entity.BMAUiEntity;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.entity.EOConsumption;
import com.around.engineerbuddy.entity.EOModelNumber;
import com.around.engineerbuddy.entity.EOSpareConsumptionStatus;
import com.around.engineerbuddy.entity.EOSpareCosumptionRequest;
import com.around.engineerbuddy.entity.EOSparePartConsumption;
import com.around.engineerbuddy.util.BMAConstants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//This screen is used to show spare consumption related data if already requested before Spare request

public class SpareConsumptionBeforeSpareRequest extends BMAFragment {

    RecyclerView recyclerView;
    Button submitButton;
    TextView noDataToDisplay;
    EOConsumption eoConsumption;
    HttpRequest httpRequest;
    EOBooking eoBooking;
    String selectPOD;
    EOModelNumber modelNumber;

    public String actionID;
    ArrayList<EOSpareCosumptionRequest> cosumptionArray = new ArrayList<>();
    HashMap<String, Object> requestData = new HashMap<>();
    BMARecyclerAdapter bmaRecyclerAdapter;

    // ArrayList<Object>spareData=new ArrayList<>();

    // This method is used to receive argument whatever passed from previous page
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.eoBooking = getArguments().getParcelable("eoBooking");
        //  selectModelNumberCallType = getArguments().getString("modelNo");
        selectPOD = getArguments().getString("pod");
        modelNumber = getArguments().getParcelable("modelNumber");
    }


    //  This method is used to inflate layout with fragment and initioalize all views;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.spare_consumption, container, false);

        this.recyclerView = this.view.findViewById(R.id.consumptionRecyclerView);
        this.submitButton = this.view.findViewById(R.id.submitButton);
        this.view.findViewById(R.id.cancelButton).setVisibility(View.GONE);
        this.noDataToDisplay = this.view.findViewById(R.id.noDataToDisplay);
        this.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAllFieldSelected();
            }
        });
        if(this.eoConsumption==null) {
            loadData();
        }else{
            dataToView();
        }
        return this.view;
    }

    //this method is used to hit request API
    private void loadData() {
        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = SpareConsumptionBeforeSpareRequest.this;
        this.actionID = "spareConsumptionData";
        httpRequest.execute(actionID, this.eoBooking.bookingID,this.eoBooking.pre_consume_req+"");
    }

    //this method is used to bind the data in recycler view
    private void dataToView(){
        if (this.eoConsumption == null || this.eoConsumption.sparePartCosumption.size() == 0 || this.eoConsumption.spareCosumptionStatus.size() == 0) {
            return;
        }
        if (cosumptionArray == null || cosumptionArray.size() == 0) {
            cosumptionArray = new ArrayList<>();
            int counter=0;
            for (int i = 0; i < this.eoConsumption.sparePartCosumption.size(); i++) {
                EOSpareCosumptionRequest eoSpareCosumptionRequest=new EOSpareCosumptionRequest();
                eoSpareCosumptionRequest.position=counter;
                cosumptionArray.add(eoSpareCosumptionRequest);
                counter++;
            }
        } else if (cosumptionArray != null && cosumptionArray.size() < this.eoConsumption.sparePartCosumption.size()) {
            int counter=0;
            for (int i = cosumptionArray.size(); i < this.eoConsumption.sparePartCosumption.size(); i++) {
                EOSpareCosumptionRequest eoSpareCosumptionRequest=new EOSpareCosumptionRequest();
                eoSpareCosumptionRequest.position=counter;
                cosumptionArray.add(eoSpareCosumptionRequest);
                counter++;
            }
        }
        this.bmaRecyclerAdapter = new BMARecyclerAdapter(getContext(), this.eoConsumption.sparePartCosumption, recyclerView, this, R.layout.spare_part_consumption_row);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bmaRecyclerAdapter);
    }


    //After receive response from server this call back method call
    @Override
    public void processFinish(String response) {
        super.processFinish(response);
        Log.d("aaaaaa", "responsssss = " + response);
        httpRequest.progress.dismiss();
        if (response.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(response);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                if (statusCode.equals("0000")) {
                    String res = jsonObject.getString("response");
                    if (this.actionID.equalsIgnoreCase("spareConsumptionData")) {

                        this.eoConsumption = BMAGson.store().getObject(EOConsumption.class, res);
                        this.dataToView();
                    }else if(this.actionID.equalsIgnoreCase("submitPrevPartsConsumption")){

                      // openSparePartOrderPage();
                    }
//                        if (this.actionID.equalsIgnoreCase("spareConsumptionData")) {
//                            isSpareConsumptionRequired = true;
//                        }
                }else if(statusCode.equals("0079")){
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

    // this method is used to set data to view in row wise
    @Override
    public <T> void createRow(RecyclerView.ViewHolder viewHolder, View itemView, T rowObject, int position) {
        EOSparePartConsumption eoSparePartConsumption = (EOSparePartConsumption) rowObject;
        TextView partNumber = itemView.findViewById(R.id.partNumber);
        TextView partName = itemView.findViewById(R.id.partName);
        TextView partType = itemView.findViewById(R.id.partType);
        TextView status = itemView.findViewById(R.id.status);
        itemView.findViewById(R.id.consumptionReasonlayout).setVisibility(View.VISIBLE);
        EditText consumptionRemarksdittext=itemView.findViewById(R.id.consumptionRemarksdittext);
        TextView selectReason = itemView.findViewById(R.id.selectConsumptionReason);
        LinearLayout selectReasonLayout = itemView.findViewById(R.id.selectreasonLayout);
        partNumber.setText(eoSparePartConsumption.part_number);
        partName.setText(eoSparePartConsumption.parts_shipped);
        partType.setText(eoSparePartConsumption.shipped_parts_type);
        status.setText(eoSparePartConsumption.status);
        selectReasonLayout.setTag(position);
        Log.d("aaaaaa"," position = = "+position);
        selectReasonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int positionTag = (int) v.getTag();
                View clickedItemView = recyclerView.findViewHolderForAdapterPosition(positionTag).itemView;
                Log.d("aaaaaa","view position = "+clickedItemView);
                selectConsumptionReason(clickedItemView, positionTag);
            }
        });
        if (cosumptionArray != null && cosumptionArray.size() > 0 && position < cosumptionArray.size()) {
            EOSpareCosumptionRequest selectedCosumption = cosumptionArray.get(position);
            if (selectedCosumption.spareConsumptionReason != null) {
                selectReason.setText(selectedCosumption.spareConsumptionReason);
                if (selectedCosumption.consumed_spare_status_id != null) {
                    selectReason.setTag(selectedCosumption.consumed_spare_status_id);
                }
                if(selectedCosumption.remarks!=null){
                    consumptionRemarksdittext.setText(selectedCosumption.remarks);
                }
            }
        }
        if (cosumptionArray.size() >= position) {
            cosumptionArray.get(position).position = position;
        }
    }
    //this method is used to open popup to show reason
    private void selectConsumptionReason(View itemView, int position) {

//        selectedItemView = itemView;
//        selectedPosition = position;
        TextView selectReason = itemView.findViewById(R.id.selectConsumptionReason);
        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getConsumptionList(selectReason), true) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if (selectedItems != null) {
                    selectReason.setText(selectedItems.getDetail1());
                    EOSpareConsumptionStatus eoSpareConsumptionStatus = (EOSpareConsumptionStatus) selectedItems.tag;
                    selectReason.setTag(eoSpareConsumptionStatus.id);
                    selectReason.setError(null);
                        itemView.findViewById(R.id.wrongPartLayout).setVisibility(View.GONE);
                        EOSpareCosumptionRequest eoSpareCosumptionRequest = cosumptionArray.get(position);
                        eoSpareCosumptionRequest.consumed_spare_tag = eoSpareConsumptionStatus.tag;
                        eoSpareCosumptionRequest.spareConsumptionReason = eoSpareConsumptionStatus.consumed_status;
                        eoSpareCosumptionRequest.consumed_spare_status_id = eoSpareConsumptionStatus.id;
                        eoSpareCosumptionRequest.spare_id = eoConsumption.sparePartCosumption.get(position).id;
                        eoSpareCosumptionRequest.remarks = null;
                        eoSpareCosumptionRequest.part_name = null;
                        eoSpareCosumptionRequest.inventory_id = null;




                }

            }
        };
        bmaSelectionDialog.setTitle(getString(R.string.selectModelNumber));
        bmaSelectionDialog.show();
    }


    // This method is used to bind the consumption list in a list with UIEntity class
    private ArrayList<BMAUiEntity> getConsumptionList(TextView selectModel) {
        ArrayList<BMAUiEntity> bmaUiEntityArrayList = new ArrayList<>();
        if (this.eoConsumption == null || this.eoConsumption.spareCosumptionStatus == null) {
            return bmaUiEntityArrayList;
        }
        for (EOSpareConsumptionStatus eoSpareConsumptionStatus : this.eoConsumption.spareCosumptionStatus) {
            BMAUiEntity bmaUiEntity = new BMAUiEntity();
            bmaUiEntity.setDetail1(eoSpareConsumptionStatus.consumed_status);
            bmaUiEntity.tag = eoSpareConsumptionStatus;
            if (selectModel.getTag() != null && ((String) selectModel.getTag()).equalsIgnoreCase(eoSpareConsumptionStatus.id)) {
                bmaUiEntity.setChecked(true);
            }
            bmaUiEntityArrayList.add(bmaUiEntity);
        }
        return bmaUiEntityArrayList;
    }



    // In this method we check all mandatory field
    private void checkAllFieldSelected(){
        if (this.eoConsumption != null || this.eoConsumption.sparePartCosumption.size() >= 1 || this.eoConsumption.spareCosumptionStatus.size() >= 1) {

            for (EOSpareCosumptionRequest eoSpareCosumptionRequest : this.cosumptionArray) {
                RecyclerView.ViewHolder viewholder=this.recyclerView.findViewHolderForAdapterPosition(eoSpareCosumptionRequest.position);
                View selectedItemView=null;
                if(viewholder==null){
                    viewholder=this.bmaRecyclerAdapter.holderHashMap.get(eoSpareCosumptionRequest.position);
                }
                if(viewholder!=null){
                    selectedItemView= viewholder.itemView;
                }

               // Log.d("aaaaaa","selectedItemview = "+selectedItemView);
                if (eoSpareCosumptionRequest.consumed_spare_tag == null) {
                    if (this.eoConsumption != null && this.eoConsumption.sparePartCosumption != null && this.eoConsumption.sparePartCosumption.size() > 2) {
                        this.recyclerView.scrollToPosition(eoSpareCosumptionRequest.position);

                    }

                    TextView errorSelectReason = selectedItemView.findViewById(R.id.selectConsumptionReason);
                    LinearLayout selecttReasonlayout = selectedItemView.findViewById(R.id.selectreasonLayout);
                    errorSelectReason.setError("error");


                    Toast.makeText(getContext(), "Please select Consumption Reason ", Toast.LENGTH_SHORT).show();
                    return;
                }
                   // View itemView =this.recyclerView.findViewHolderForAdapterPosition(eoSpareCosumptionRequest.position).itemView;;
                    EditText reasonView = selectedItemView.findViewById(R.id.consumptionRemarksdittext);
                    String reason = reasonView.getText().toString().trim();
                    if (reason == null || reason.length() == 0) {
                        eoSpareCosumptionRequest.remarks = "";
//                        reasonView.setError("Reason can not be blank");
//                        Toast.makeText(getContext(), "Please enter consumption remarks ", Toast.LENGTH_SHORT).show();
//                        return;
                    } else {
                        eoSpareCosumptionRequest.remarks = reason;
                    }
                }

        }

        submitRequestToServer(cosumptionArray);

    }
    //this method is used to submit request with selected data
    private void submitRequestToServer(ArrayList<EOSpareCosumptionRequest>cosumptionArray){
        requestData.put("spares_data",cosumptionArray);
        httpRequest = new HttpRequest(getMainActivity(), true);
        httpRequest.delegate = SpareConsumptionBeforeSpareRequest.this;
        this.actionID="submitPrevPartsConsumption";
        openSparePartOrderPage(cosumptionArray);
      //  httpRequest.execute(this.actionID, BMAGson.store().toJson(requestData));

    }
    // this method is used to opne spare part page with some bundle value
    private void openSparePartOrderPage(ArrayList<EOSpareCosumptionRequest>cosumptionArray){
        Bundle bundle = new Bundle();

        bundle.putString("pod",this.selectPOD);
        //  bundle.putString("modelNo",modelNo);
        bundle.putParcelable("modelNumber",this.modelNumber);
        bundle.putParcelable("eoBooking", eoBooking);
        bundle.putParcelableArrayList("consumptionArray",cosumptionArray);
      //  this.updateFragment(bundle, getString(R.string.spareParts));
        SparePartsOrderFragment sparePartsOrderFragment= new SparePartsOrderFragment();
        bundle.putString(BMAConstants.HEADER_TXT, getString(R.string.spareParts));
        sparePartsOrderFragment.setArguments(bundle);
        getMainActivity().updateFragment(sparePartsOrderFragment, true, null);

    }

    // this is callback method it call automatically after popBack
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data.getBooleanExtra("isCancelled", false) || data.getBooleanExtra("completed", false)) {
            Intent intent = new Intent();
            intent.putExtra("isCancelled", true);
            getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, intent);
            getFragmentManager().popBackStack();
        }
    }
}
