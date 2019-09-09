package com.around.engineerbuddy.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.around.engineerbuddy.R;

public class PaymentDetailFragment extends BMAFragment {
    Button submit;
    LinearLayout serialnumberLayout,symptomLayout,defectLayout,solutionLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.payments_detail_fragment, container, false);

        this.submit=this.view.findViewById(R.id.submitButton);

        //   this.submit.setText("Submit");
        return this.view;

    }
}
