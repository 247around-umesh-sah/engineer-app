package com.around.engineerbuddy.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.around.engineerbuddy.R;
import com.around.engineerbuddy.adapters.BMARecyclerAdapter;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.entity.EOSpareDetails;
import com.around.engineerbuddy.util.BMAConstants;
import com.around.engineerbuddy.util.BMAUIUtil;

public class SpareDetailsFragment extends BMAFragment {

    RecyclerView recyclerView;
    EOBooking eoBooking;
    Button addMorePart;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.eoBooking = getArguments().getParcelable("eoBooking");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.spare_details, container, false);
        this.recyclerView = this.view.findViewById(R.id.recyclerView);
        this.view.findViewById(R.id.cancelButton).setVisibility(View.GONE);
        this.addMorePart=this.view.findViewById(R.id.submitButton);
        this.addMorePart.setText("Add More Part");
        this.addMorePart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWarrantyPage();
            }
        });
        this.loadRecyclerViewData();
        return this.view;
    }
    private void  loadRecyclerViewData(){
        if(eoBooking!=null && eoBooking.spares!=null){
            BMARecyclerAdapter bmaRecyclerAdapter = new BMARecyclerAdapter(getContext(), this.eoBooking.spares, recyclerView, this, R.layout.spare_details_row);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(bmaRecyclerAdapter);
        }
    }

    @Override
    public <T> void createRow(RecyclerView.ViewHolder viewHolder, View itemView, T rowObject, int position) {
        EOSpareDetails eoSpareDetails= (EOSpareDetails) rowObject;
        TextView partName=itemView.findViewById(R.id.partName);
        TextView partType=itemView.findViewById(R.id.partType);
        TextView partStatus=itemView.findViewById(R.id.partStatus);

        TextView partQuantity=itemView.findViewById(R.id.partQuantity);
        TextView warrantyStatus=itemView.findViewById(R.id.warrantyStatus);

        partName.setText(eoSpareDetails.parts_shipped);
        partType.setText(eoSpareDetails.shipped_parts_type);
        partStatus.setText(eoSpareDetails.status);
        partQuantity.setText(eoSpareDetails.shipped_quantity);
        if(eoSpareDetails.part_warranty_status.equals("1")){
            warrantyStatus.setBackground(getResources().getDrawable(R.drawable.circle));
            warrantyStatus.setText("In");
            warrantyStatus.setTextColor(BMAUIUtil.getColor(R.color.green_color));
        }else{
            warrantyStatus.setBackground(getResources().getDrawable(R.drawable.red_circle));
            warrantyStatus.setText("Ow");
            warrantyStatus.setTextColor(BMAUIUtil.getColor(R.color.red_color));
        }
    }
    private void openWarrantyPage(){
        Bundle bundle=new Bundle();
        bundle.putParcelable("eoBooking", eoBooking);
        this.updateFragment(bundle, new EditWarrantyBooking(),"Check Warranty",null);
    }
    public void updateFragment(Bundle bundle, Fragment fragment, String headerText, Integer imageDrawable) {
        bundle.putString(BMAConstants.HEADER_TXT, headerText);
        fragment.setArguments(bundle);
        getMainActivity().updateFragment(fragment, true, imageDrawable);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getBooleanExtra("isCancelled", false) || data.getBooleanExtra("completed", false)) {
            Intent intent = new Intent();
            intent.putExtra("isCancelled", true);
            getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, intent);
            getFragmentManager().popBackStack();
        }
    }
}
