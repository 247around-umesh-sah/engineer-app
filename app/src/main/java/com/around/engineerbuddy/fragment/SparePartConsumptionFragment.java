package com.around.engineerbuddy.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.around.engineerbuddy.entity.EOSpareConsumptionStatus;
import com.around.engineerbuddy.entity.EOSpareCosumptionRequest;
import com.around.engineerbuddy.entity.EOSparePartConsumption;
import com.around.engineerbuddy.entity.EoWrongPart;
import com.around.engineerbuddy.util.BMAConstants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SparePartConsumptionFragment extends BMAFragment {

    RecyclerView recyclerView;
    HttpRequest httpRequest;
    EOBooking eoBooking;
    EOConsumption eoConsumption;
    String actionID;
    boolean isSpareConsumptionRequired;
    Button submitButton;
    TextView noDataToDisplay;
    ArrayList<EOSpareCosumptionRequest> cosumptionArray = new ArrayList<>();
    // ArrayList<EOSpareCosumptionRequest> selectedCosumptionList=new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.eoBooking = getArguments().getParcelable("eoBooking");
        cosumptionArray = getArguments().getParcelableArrayList("selectedCosumptionList");
    }

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
                submitProcess();
            }
        });
        loadData();
        return this.view;
    }

    private void loadData() {
        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = SparePartConsumptionFragment.this;
        this.actionID = "spareConsumptionData";
        httpRequest.execute(actionID, this.eoBooking.bookingID);
    }

    private void getWrongPart() {
        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = SparePartConsumptionFragment.this;
        this.actionID = "wrongSparePartsName";
        httpRequest.execute(actionID, this.eoBooking.serviceID, eoBooking.partnerID);
    }

    ArrayList<EoWrongPart> wrongPartList = new ArrayList<>();

    @Override
    public void processFinish(String response) {
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
                        isSpareConsumptionRequired = true;

                        this.eoConsumption = BMAGson.store().getObject(EOConsumption.class, res);
                        this.dataToView();
                    } else if (this.actionID.equalsIgnoreCase("wrongSparePartsName")) {
                        // BMAGson.store().getList(EoWrongPart.class, jsonObject.getString("response"));
                        selectedItemView.findViewById(R.id.wrongPartLayout).setVisibility(View.VISIBLE);
                        wrongPartList = BMAGson.store().getList(EoWrongPart.class, jsonObject.getJSONObject("response").getString("wrong_part_list"));

                        if (wrongPartList != null && wrongPartList.size() > 0) {
                            selectedItemView.setTag(wrongPartList);

                        } else {
                            selectedItemView.findViewById(R.id.wrongPartDropDownIcon).setVisibility(View.GONE);
                            selectedItemView.findViewById(R.id.wrongPartNumberlayout).setVisibility(View.GONE);
                            EditText selectWrongpart = selectedItemView.findViewById(R.id.selectwrongtpart);
                            selectWrongpart.setEnabled(true);
                            selectWrongpart.addTextChangedListener(new CustomeTextWatcher(selectedItemView, selectedPosition));
                        }
                    }
                }
            } catch (Exception e) {
                this.noDataToDisplay.setVisibility(View.VISIBLE);
            }
        } else {
            this.noDataToDisplay.setVisibility(View.VISIBLE);
            // httpRequest.progress.dismiss();
            BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {


                @Override
                public void onWarningDismiss() {
                    super.onWarningDismiss();
                }
            };
            bmaAlertDialog.show("Server Error");
        }


    }

    private void dataToView() {
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
        BMARecyclerAdapter bmaRecyclerAdapter = new BMARecyclerAdapter(getContext(), this.eoConsumption.sparePartCosumption, recyclerView, this, R.layout.spare_part_consumption_row);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bmaRecyclerAdapter);
    }

    @Override
    public <T> void createRow(RecyclerView.ViewHolder viewHolder, View itemView, T rowObject, int position) {
        EOSparePartConsumption eoSparePartConsumption = (EOSparePartConsumption) rowObject;
        TextView partNumber = itemView.findViewById(R.id.partNumber);
        TextView partName = itemView.findViewById(R.id.partName);
        TextView partType = itemView.findViewById(R.id.partType);
        TextView status = itemView.findViewById(R.id.status);
        TextView selectConsumptionReason = itemView.findViewById(R.id.selectConsumptionReason);
        EditText partNameEdittext = itemView.findViewById(R.id.selectwrongtpart);
        EditText reasonDescription = itemView.findViewById(R.id.Problemdescriptionedittext);

        BMAFontViewField wrongPartDropDownIcon = itemView.findViewById(R.id.wrongPartDropDownIcon);
        LinearLayout selectReasonLayout = itemView.findViewById(R.id.selectreasonLayout);
        partNumber.setText(eoSparePartConsumption.part_number);
        partName.setText(eoSparePartConsumption.parts_requested);
        partType.setText(eoSparePartConsumption.parts_requested_type);
        status.setText(eoSparePartConsumption.status);
        selectReasonLayout.setTag(position);

        selectReasonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int positionTag = (int) v.getTag();
                View clickedItemView = recyclerView.findViewHolderForAdapterPosition(positionTag).itemView;
                selectConsumptionReason(clickedItemView, positionTag);
            }
        });
        wrongPartDropDownIcon.setTag(position);
        wrongPartDropDownIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionTag = (int) v.getTag();
                View clickedItemView = recyclerView.findViewHolderForAdapterPosition(positionTag).itemView;
                selectWrongPart(clickedItemView, positionTag);
            }
        });
        if (cosumptionArray != null && cosumptionArray.size() > 0 && position < cosumptionArray.size()) {
            EOSpareCosumptionRequest selectedCosumption = cosumptionArray.get(position);
            if (selectedCosumption.spareConsumptionReason != null) {
                selectConsumptionReason.setText(selectedCosumption.spareConsumptionReason);
                if (selectedCosumption.consumed_spare_status_id != null) {
                    selectConsumptionReason.setTag(selectedCosumption.consumed_spare_status_id);
                }
            }
            if (selectedCosumption.consumed_spare_tag != null && selectedCosumption.consumed_spare_tag.equalsIgnoreCase("wrong_part_received")) {
                itemView.findViewById(R.id.wrongPartLayout).setVisibility(View.VISIBLE);
                if (selectedCosumption.part_name != null) {
                    partNameEdittext.setText(selectedCosumption.part_name);
                }
                if (selectedCosumption.remarks != null) {
                    reasonDescription.setText(selectedCosumption.remarks);
                }

            }
        }
        if (cosumptionArray.size() >= position) {
            cosumptionArray.get(position).position = position;
        }


    }

    View selectedItemView;
    int selectedPosition = -1;

    private void selectConsumptionReason(View itemView, int position) {
//        wrongPartLayout
        selectedItemView = itemView;
        selectedPosition = position;
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
                    if (eoSpareConsumptionStatus.tag.equalsIgnoreCase("wrong_part_received")) {
                        EOSpareCosumptionRequest eoSpareCosumptionRequest = cosumptionArray.get(position);
                        eoSpareCosumptionRequest.consumed_spare_tag = eoSpareConsumptionStatus.tag;
                        eoSpareCosumptionRequest.spareConsumptionReason = eoSpareConsumptionStatus.consumed_status;
                        eoSpareCosumptionRequest.consumed_spare_status_id = eoSpareConsumptionStatus.id;
                        eoSpareCosumptionRequest.spare_id = eoConsumption.sparePartCosumption.get(position).id;
                        getWrongPart();
                    } else {
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

            }
        };
        bmaSelectionDialog.setTitle(getString(R.string.selectModelNumber));
        bmaSelectionDialog.show();
    }


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

    private void selectWrongPart(View itemView, int position) {
        selectedItemView = itemView;
        EditText selectWrong = itemView.findViewById(R.id.selectwrongtpart);

        TextView wrongPartNumber = itemView.findViewById(R.id.wrongPartNumber);
        BMAFontViewField wrongPartDropDownIcon = itemView.findViewById(R.id.wrongPartDropDownIcon);
        BMASelectionDialog bmaSelectionDialog = new BMASelectionDialog(getContext(), getWrongPartList(selectWrong, (ArrayList<EoWrongPart>) itemView.getTag()), true) {
            @Override
            public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
                super.onDismiss(selectedItems, deletedItems, updatedItems);
                if (selectedItems != null) {

                    selectWrong.setText(selectedItems.getDetail1());
                    EoWrongPart eoWrongPart = (EoWrongPart) selectedItems.tag;
                    selectWrong.setTag(eoWrongPart);
                    wrongPartNumber.setText(eoWrongPart.part_number);
                    EOSpareCosumptionRequest eoSpareCosumptionRequest = cosumptionArray.get(position);
                    eoSpareCosumptionRequest.part_name = eoWrongPart.part_name;
                    if (eoConsumption != null && eoConsumption.sparePartCosumption.get(position).shipped_inventory_id != null) {
                        eoSpareCosumptionRequest.inventory_id = eoWrongPart.inventory_id;
                    }
//                     getPartTypeData();
                }

            }
        };
        bmaSelectionDialog.setTitle(getString(R.string.selectModelNumber));
        bmaSelectionDialog.show();
    }


    private ArrayList<BMAUiEntity> getWrongPartList(EditText selectModel, ArrayList<EoWrongPart> wrongPartList) {
        ArrayList<BMAUiEntity> bmaUiEntityArrayList = new ArrayList<>();
        if (this.wrongPartList == null || this.wrongPartList.size() == 0) {
            return bmaUiEntityArrayList;
        }
        for (EoWrongPart eoWrongPart : wrongPartList) {
            BMAUiEntity bmaUiEntity = new BMAUiEntity();
            bmaUiEntity.setDetail1(eoWrongPart.part_name);
            bmaUiEntity.tag = eoWrongPart;
            if (selectModel.getTag() != null && ((EoWrongPart) selectModel.getTag()).inventory_id.equalsIgnoreCase(eoWrongPart.inventory_id)) {
                bmaUiEntity.setChecked(true);
            }
            bmaUiEntityArrayList.add(bmaUiEntity);
        }
        return bmaUiEntityArrayList;
    }

    private void submitProcess() {
        if (this.eoConsumption != null || this.eoConsumption.sparePartCosumption.size() >= 1 || this.eoConsumption.spareCosumptionStatus.size() >= 1) {

            for (EOSpareCosumptionRequest eoSpareCosumptionRequest : this.cosumptionArray) {

                if (eoSpareCosumptionRequest.consumed_spare_tag == null) {
                    if(this.eoConsumption!=null && this.eoConsumption.sparePartCosumption!=null && this.eoConsumption.sparePartCosumption.size()>2) {
                        this.recyclerView.scrollToPosition(eoSpareCosumptionRequest.position);

                    }
                    View errorItemView=this.recyclerView.findViewHolderForAdapterPosition(eoSpareCosumptionRequest.position).itemView;
                    TextView errorSelectReason = errorItemView.findViewById(R.id.selectConsumptionReason);
                    LinearLayout selecttReasonlayout=errorItemView.findViewById(R.id.selectreasonLayout);
                    errorSelectReason.setError("error");



                    Toast.makeText(getContext(), "Please select Consumption Reason ", Toast.LENGTH_SHORT).show();
                    return;
                } else if (eoSpareCosumptionRequest.consumed_spare_tag.equalsIgnoreCase("wrong_part_received")) {
                    if (eoSpareCosumptionRequest.part_name == null) {
                        Toast.makeText(getContext(), "Please fill part name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    View itemView = recyclerView.findViewHolderForAdapterPosition(eoSpareCosumptionRequest.position).itemView;
                    EditText reasonView = itemView.findViewById(R.id.Problemdescriptionedittext);
                    String reason = reasonView.getText().toString().trim();
                    if (reason == null || reason.length() == 0) {
                        reasonView.setError("Reason can not be blank");
                        return;
                    } else {
                        eoSpareCosumptionRequest.remarks = reason;
                    }
                }
            }
            //    Log.d("aaaaaa","spareConsumption = "+BMAGson.store().toJson(cosumptionArray));
            Intent intent = new Intent();
            intent.putExtra("spareConsumption", cosumptionArray);
            intent.putExtra("completeCatogryPageName", "spareConsumption");
            intent.putExtra("isSpareConsumptionRequired", isSpareConsumptionRequired);
            getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, intent);
            getFragmentManager().popBackStack();
        }
    }

    class CustomeTextWatcher implements TextWatcher {
        View itemView;
        int position;

        public CustomeTextWatcher(View itemView, int position) {
            this.itemView = itemView;
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String partname = s.toString();
            cosumptionArray.get(position).part_name = partname;

        }
    }


}
