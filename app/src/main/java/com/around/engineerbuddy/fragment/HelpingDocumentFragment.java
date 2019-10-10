package com.around.engineerbuddy.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.VideoView;

import com.around.engineerbuddy.BMAmplitude;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.adapters.BMARecyclerAdapter;
import com.around.engineerbuddy.component.BMAFontViewField;
import com.around.engineerbuddy.entity.EODocumentType;
import com.around.engineerbuddy.filepicker.DownloadTask;

import java.util.ArrayList;

public class HelpingDocumentFragment extends BMAFragment {
    RecyclerView recyclerView, videoRecyclerView;
    boolean isPdfDocument, isYoutube;
    String documentType;
    ArrayList<EODocumentType> documentList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.isPdfDocument = this.getArguments().getBoolean("isPdfDocument");
        this.isYoutube = this.getArguments().getBoolean("isYoutube");
        this.documentType = this.getArguments().getString("documentType");
        this.documentList = this.getArguments().getParcelableArrayList("documentList");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        this.view = inflater.inflate(R.layout.helping_document_fragment, container, false);
        BMAmplitude.saveUserAction("DocumentDetailFragment", "DocumentDetailFragment");
        this.recyclerView = this.view.findViewById(R.id.documentRecyclerView);
        //this.videoRecyclerView = this.view.findViewById(R.id.videoRecyclerView);
        //  TomorrowAdapter ta=new TomorrowAdapter(al,getMainActivity());
        this.view.findViewById(R.id.nodata).setVisibility(this.documentList.size() > 0 ? View.GONE : View.VISIBLE);
        BMARecyclerAdapter bmaRecyclerAdapter = new BMARecyclerAdapter(getContext(), this.documentList, recyclerView, this, R.layout.helper_document_row_item);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bmaRecyclerAdapter);
        return this.view;
    }

    @Override
    public <T> void createRow(RecyclerView.ViewHolder viewHolder, View itemView, T rowObject, int position) {
        // super.createRow(viewHolder, itemView, rowObject, position);
        EODocumentType eoDocumentType = (EODocumentType) rowObject;
        ImageView pdfImage = itemView.findViewById(R.id.pdfImage);
        pdfImage.setBackground(getResources().getDrawable(getDrawableImage(eoDocumentType.documentType)));
        BMAFontViewField optionIcon = itemView.findViewById(R.id.optionIcon);
        TextView documentDiscription=itemView.findViewById(R.id.documentDiscription);
        documentDiscription.setText(eoDocumentType.documentDescription);
        optionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.inflate(R.menu.option_menu);
                Menu menuitem = popupMenu.getMenu();
                if (documentType.equalsIgnoreCase("document")) {
                    menuitem.findItem(R.id.view).setVisible(false);
                    menuitem.findItem(R.id.share).setVisible(false);
                } else {
                    menuitem.findItem(R.id.download).setVisible(false);
                    menuitem.findItem(R.id.share).setVisible(true);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.download:

//                                WebView webView = HelpingDocumentFragment.this.view.findViewById(R.id.webView);
//                                webView.setVisibility(View.VISIBLE);
//                                webView.getSettings().setSupportZoom(true);
//                                webView.getSettings().setJavaScriptEnabled(true);
//                                webView.loadUrl("https://s3.amazonaws.com/bookings-collateral/invoices-excel/Around-1920-1396.pdf");
                                DownloadTask downloadTask = new DownloadTask(getContext(), eoDocumentType.file);//"http://androhub.com/demo/demo.pdf");
//                                //   downloadTask.downloadFile();
//
//                                //handle menu1 click
                                return true;
                            case R.id.share:
                                //handle menu2 click
                                sharedLink(eoDocumentType.file);
                                return true;
                            case R.id.view:

                                VideoView videoView = HelpingDocumentFragment.this.view.findViewById(R.id.videoView);
                                videoView.setVisibility(View.VISIBLE);
                                ProgressDialog pd = new ProgressDialog(getContext());
                                pd.setMessage("Loading please wait...");
                                pd.show();
                                Uri uri = Uri.parse(eoDocumentType.file);//("https://s3.amazonaws.com/bookings-collateral/vendor-partner-docs/Partner-Brand_Collateral_21_2019-05-15.Lpg To Png_E.m4v");
                                videoView.setVideoURI(uri);
                                videoView.start();

                                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        //close the progress dialog when buffering is done
                                        pd.dismiss();
                                    }
                                });


//                                WebView webView = HelpingDocumentFragment.this.view.findViewById(R.id.webView);
//                                webView.getSettings().setSupportZoom(true);
//                                webView.getSettings().setJavaScriptEnabled(true);
//                                webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + "https://androhub.com/demo/demo.pdf");


                                https:
//s3.amazonaws.com/bookings-collateral/vendor-partner-docs/Partner-Brand_Collateral_21_2019-05-15.Lpg To Png_E.m4v


//                                PDFViewrDialog pdfViewrDialog=new PDFViewrDialog(getContext(),"https://androhub.com/demo/demo.pdf");//Environment.getExternalStorageDirectory().getPath() + "/" +"downloads/demo.pdf");
//                                pdfViewrDialog.show();
//                                try {
//                                   // BMAFilePicker.openFile(getContext(),"http://androhub.com/demo/demo.pdf");
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
                                //handle menu3 click
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();

            }
        });


    }

    public int getDrawableImage(String documentType) {
        switch (documentType) {
            case "pdf":
                return R.drawable.pdf;
            case "video":
                return R.drawable.video;
            case "software":
                return R.drawable.software;
        }
        return R.drawable.pdf_white;
    }
    private void sharedLink(String link){
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Here is VideoCon TV repair document prepared by 247around");
        share.putExtra(Intent.EXTRA_TEXT, "Here is VideoCon TV repair document prepared by 247around"+"\n"+link);

        startActivity(Intent.createChooser(share, "Share link!"));

    }

}
