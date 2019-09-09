package com.around.engineerbuddy.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.around.engineerbuddy.R;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.entity.EOBooking;
import com.around.engineerbuddy.entity.EOCompleteProductQuantity;
import com.around.engineerbuddy.entity.EOCompleteProductdetail;
import com.around.engineerbuddy.util.BMAConstants;
import com.around.engineerbuddy.util.BMAUIUtil;

import java.util.HashMap;

public class CheckoutFragment extends BMAFragment {

    LinearLayout productHeaderLayout, chargeLayout;
    Button cashButton, paytmButtom;
    EOBooking eoBooking;
    TextView applianceName, brandName, serviceType,name;
    EOCompleteProductdetail selectedProductDetail;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.eoBooking = getArguments().getParcelable("eoBooking");
        this.selectedProductDetail = this.getArguments().getParcelable("productDetail");
    }

    LinearLayout addserviceLayout;
    HashMap<Integer, HashMap<String, Object>> partsObject = new HashMap<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.product_detail_fragment, container, false);
        productHeaderLayout = this.view.findViewById(R.id.productHeaderLayout);
        this.productHeaderLayout.setBackgroundColor(BMAUIUtil.getColor(R.color.darkBlueGreen));
        this.addserviceLayout=this.view.findViewById(R.id.addServicePartLayout);
        this.view.findViewById(R.id.productBorkencheckbox).setVisibility(View.GONE);
        this.view.findViewById(R.id.dateLayout).setVisibility(View.GONE);
        this.cashButton = this.view.findViewById(R.id.cancelButton);
        this.paytmButtom = this.view.findViewById(R.id.submitButton);
        applianceName = this.view.findViewById(R.id.applianceName);
        applianceName.setText(eoBooking.services);
        brandName = this.view.findViewById(R.id.brandName);
        brandName.setText(eoBooking.applianceBrand);
        serviceType = this.view.findViewById(R.id.serviceCapacityType);
        this.name=this.view.findViewById(R.id.name);
        this.name.setText(eoBooking.name);
        serviceType.setText(eoBooking.services);
        float dialogRadius = getResources().getDimension(R.dimen._20dp);
        this.paytmButtom.setText("Paytm");
        this.cashButton.setText("Cash");
        this.view.findViewById(R.id.serviceCategoryCapacityLayout).setVisibility(View.GONE);
        this.view.findViewById(R.id.addressLayout).setVisibility(View.GONE);
        BMAUIUtil.setBackgroundRound(this.cashButton, BMAUIUtil.getColor(R.color.missedBookingcolor), new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        this.paytmButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaytmQRFragment paytmQRFragment=new PaytmQRFragment();
                Bundle bundle=new Bundle();
                bundle.putString("bookingID",eoBooking.bookingID);
                paytmQRFragment.setArguments(bundle);
                getMainActivity().updateFragment(paytmQRFragment,true);
            }
        });
        cashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(),true,false) {

                    @Override
                    public void onConfirmation(String inputValue) {
                        super.onConfirmation(inputValue);
                        if(inputValue.length()==0){
                            Toast.makeText(getContext(),"Please enter amount",Toast.LENGTH_SHORT).show();
                            return;
                        }else {
                            Intent intent = new Intent();
                            intent.putExtra("completeCatogryPageName", "paymentDetail");
                            intent.putExtra("paymentsAmount", inputValue);
                            intent.putExtra("isCash", selectedProductDetail != null);
                            getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, intent);
                            getFragmentManager().popBackStack();
                        }
                    }
                };
                bmaAlertDialog.show();
                bmaAlertDialog.showInputField();

            }
        });
        if(selectedProductDetail!=null) {
            for (EOCompleteProductQuantity eoCompleteProductQuantity:selectedProductDetail.getbookingProductUnit().quantity){
                if(eoCompleteProductQuantity.isComplete) {
                    View childView = inflater.inflate(R.layout.checkout_item, null, false);
                    TextView serviceCatg = childView.findViewById(R.id.serviceName);
                    serviceCatg.setText(eoCompleteProductQuantity.price_tags);
                    TextView basicCharge = childView.findViewById(R.id.basicCharge);
                    basicCharge.setText("₹ " + eoCompleteProductQuantity.customerBasicharge);
                    TextView additionalCharge = childView.findViewById(R.id.additionalCharge);
                    additionalCharge.setText("₹ " + eoCompleteProductQuantity.customerExtraharge);
                    TextView partsCharge = childView.findViewById(R.id.partsCharge);
                    partsCharge.setText("₹ " + eoCompleteProductQuantity.customerPartharge);
                    addserviceLayout.addView(childView);
                }
            }
            for (EOCompleteProductQuantity eoCompleteProductQuantity:selectedProductDetail.prices){
                if(eoCompleteProductQuantity.isComplete) {
                    View childView = inflater.inflate(R.layout.checkout_item, null, false);
                    TextView serviceCatg = childView.findViewById(R.id.serviceName);
                    serviceCatg.setText(eoCompleteProductQuantity.price_tags);
                    TextView basicCharge = childView.findViewById(R.id.basicCharge);
                    basicCharge.setText("₹ " + eoCompleteProductQuantity.customerBasicharge);
                    TextView additionalCharge = childView.findViewById(R.id.additionalCharge);
                    additionalCharge.setText("₹ " + eoCompleteProductQuantity.customerExtraharge);
                    TextView partsCharge = childView.findViewById(R.id.partsCharge);
                    partsCharge.setText("₹ " + eoCompleteProductQuantity.customerPartharge);
                    addserviceLayout.addView(childView);
                }
            }
        }
        return this.view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent=new Intent();
        intent.putExtra("paymentsAmount", data.getStringExtra("paymentsAmount"));
        getTargetFragment().onActivityResult(getTargetRequestCode(), BMAConstants.requestCode, intent);
        getFragmentManager().popBackStack();
    }
}
