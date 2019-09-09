package com.around.engineerbuddy.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.around.engineerbuddy.R;
import com.around.engineerbuddy.component.BMATileView;
import com.around.engineerbuddy.util.BMAConstants;

public class CancelBookingTypeFragment extends BMAFragment implements View.OnClickListener {
    BMATileView tile1, tile2, tile3;
    String bookingID;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.bookingID=getArguments().getString("bookingID");
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.cancel_booking_type, container, false);
        this.tile1 = this.view.findViewById(R.id.tile1);
        this.tile2 = this.view.findViewById(R.id.tile2);
        this.tile3 = this.view.findViewById(R.id.tile3);
        this.setTileValue(this.tile1, R.string.crm, R.drawable.crm_cancel_type, "CRMCancel", true);
        this.setTileValue(this.tile2, R.string.partner, R.drawable.partner_cancel_type, "PartnerCancel", true);
        this.setTileValue(this.tile3, R.string.sf, R.drawable.sf_cancel_type, "SFCancel", true);
        this.tile1.setText("Cancellation \n Reasons");
        this.tile2.setText("Cancellation \n Reasons");
        this.tile3.setText("Cancellation \n Reasons");
        return this.view;
    }
    private void setTileValue(BMATileView fnTileView, @StringRes int title, @DrawableRes int imgRes, String tag, boolean isVisible) {
        fnTileView.setTitle(title);
        fnTileView.setImageResource(imgRes);
        fnTileView.setTag(tag);
        fnTileView.setVisibility(isVisible);
        //fnTileView.setTitleColor(BMAUIUtil.getColor(R.color.tile_title_color));
        // fnTileView.setTitleBgColor(R.color.appTheamColor);
        fnTileView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.tile1:
                bundle.putString("bookingID", bookingID);
                this.updateFragment(bundle, new CancelBookingFragment(), "CRM Cancellation");
                break;
            case R.id.tile2:
               // bundle.putString("bookingID", bookingID);
                //this.updateFragment(bundle, new CancelBookingFragment(), "Partner Cancellation");
                break;
            case R.id.tile3:
               // bundle.putString("bookingID", bookingID);
              //  this.updateFragment(bundle, new CancelBookingFragment(), "SF Cancellation");
                break;
        }
    }
    public void updateFragment(Bundle bundle, Fragment fragment, String headerText) {
        this.updateFragment(bundle,fragment,headerText,null);
    }
    public void updateFragment(Bundle bundle, Fragment fragment, String headerText, Integer imageDrawable) {
        bundle.putString(BMAConstants.HEADER_TXT, headerText);
        fragment.setArguments(bundle);
        getMainActivity().updateFragment(fragment, true, imageDrawable);
    }
}
