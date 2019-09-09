package com.around.engineerbuddy.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.BMAmplitude;
import com.around.engineerbuddy.HttpRequest;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.component.BMAAlertDialog;
import com.around.engineerbuddy.entity.EODocument;
import com.around.engineerbuddy.util.BMAConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class DocumentCategory extends BMAFragment implements View.OnClickListener {

    HttpRequest httpRequest;
    String bookingID;
    EODocument eoDocument;
    TextView noData;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.bookingID = this.getArguments().getString("bookingID");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BMAmplitude.saveUserAction("DocumentCategory", "DocumentCategory");
        this.view = inflater.inflate(R.layout.documentcategory, container, false);

        this.noData = this.view.findViewById(R.id.nodata);

        this.loadData();
        return this.view;
    }

    private void setView() {

        ImageView imageView = this.view.findViewById(R.id.documentImage);
        TextView documentname = this.view.findViewById(R.id.documentName);
        ImageView videoimage = this.view.findViewById(R.id.videoImage);
        TextView videoname = this.view.findViewById(R.id.videoName);
        ImageView softwareImage = this.view.findViewById(R.id.softwareImage);
        TextView softwareName = this.view.findViewById(R.id.softwareName);
        CardView documentcardview = this.view.findViewById(R.id.documentCardView);
        CardView videoCardView = this.view.findViewById(R.id.videoCardView);
        CardView softwareCardView = this.view.findViewById(R.id.softwareCardView);
        imageView.setBackground(getResources().getDrawable(R.drawable.file));
        documentname.setText(getString(R.string.document));
        videoimage.setBackground(getResources().getDrawable(R.drawable.video));
        videoname.setText(getString(R.string.video));
        softwareImage.setBackground(getResources().getDrawable(R.drawable.software));
        softwareName.setText(getString(R.string.software));
        softwareImage.setAlpha(.5f);
        documentcardview.setOnClickListener(this);
        videoCardView.setOnClickListener(this);
        softwareCardView.setOnClickListener(this);
    }

    private void loadData() {
        httpRequest = new HttpRequest(getContext(), true);
        httpRequest.delegate = DocumentCategory.this;
        httpRequest.execute("engineerHeplingDocuments", this.bookingID);


    }

    @Override
    public void processFinish(String response) {

        httpRequest.progress.dismiss();

        Log.d("response", " response = " + response);

        if (response.contains("data")) {
            JSONObject jsonObjectHttpReq;

            try {
                jsonObjectHttpReq = new JSONObject(response);

                final JSONObject jsonObject = jsonObjectHttpReq.getJSONObject("data");
                String statusCode = jsonObject.getString("code");
                if (statusCode.equals("0000")) {
                    String res = jsonObject.getString("response");
                    this.eoDocument = BMAGson.store().getObject(EODocument.class, res);
                    //  this.bookingInfo = BMAGson.store().getObject(BookingInfo.class, jsonObject);
                    if (this.eoDocument != null) {
                        this.setView();
                        noData.setVisibility(View.GONE);
                    }
                }
            } catch (JSONException e) {
                httpRequest.progress.dismiss();
                BMAAlertDialog bmaAlertDialog = new BMAAlertDialog(getContext(), false, true) {


                    @Override
                    public void onWarningDismiss() {
                        super.onWarningDismiss();
                    }
                };
                bmaAlertDialog.show(R.string.loginFailedMsg);
            }
        } else {
            httpRequest.progress.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Bundle bundle = new Bundle();
        int imageDrawable = R.drawable.pdf_white;
        ;
        switch (id) {
            case R.id.documentCardView:
                //Condition By Nitin Sir
                if( this.eoDocument.pdf.size()==0){
                    Toast.makeText(getContext(),getString(R.string.fileExistValidation),Toast.LENGTH_SHORT).show();
                    return;
                }
                bundle.putString("documentType", "document");

                bundle.putParcelableArrayList("documentList", this.eoDocument.pdf);
                imageDrawable = R.drawable.pdf_white;
                updateFragment(bundle,imageDrawable,"Documents");
                break;
            case R.id.videoCardView:
                if( this.eoDocument.video.size()==0){
                    Toast.makeText(getContext(),getString(R.string.videoExistValidation),Toast.LENGTH_SHORT).show();
                    return;
                }
                bundle.putParcelableArrayList("documentList", this.eoDocument.video);
                bundle.putString("documentType", "videos");
                imageDrawable = R.drawable.video;
                updateFragment(bundle,imageDrawable,"Videos");
                break;
//            case R.id.softwareCardView:
//                bundle.putString("documentType", "software");
//                imageDrawable = R.drawable.software;
//                break;
        }

    }
    private void updateFragment(Bundle bundle,int imageDrawable,String headerText){
        HelpingDocumentFragment helpingDocumentFragment = new HelpingDocumentFragment();
        bundle.putString(BMAConstants.HEADER_TXT, headerText);
        helpingDocumentFragment.setArguments(bundle);
        getMainActivity().updateFragment(helpingDocumentFragment, true, imageDrawable);
    }
}
