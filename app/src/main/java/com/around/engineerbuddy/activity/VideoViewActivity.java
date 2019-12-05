package com.around.engineerbuddy.activity;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.VideoView;

import com.around.engineerbuddy.R;
import com.around.engineerbuddy.fragment.HelpingDocumentFragment;

public class VideoViewActivity extends AppCompatActivity {
    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getStringExtra("url");
        setContentView(R.layout.activity_video_view);
        videoView = findViewById(R.id.videoView);

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading please wait...");
        pd.show();
                DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) videoView.getLayoutParams();
        params.width = metrics.widthPixels;
        params.height = metrics.heightPixels;
        params.leftMargin = 0;
        videoView.setLayoutParams(params);


        String fullScreen = getIntent().getStringExtra("fullScreenInd");
        if ("y".equals(fullScreen)) {
        }

        if(url==null || url.length()==0){
            return;
        }
        Uri videoUri = Uri.parse(url);//("https://s3.amazonaws.com/bookings-collateral/vendor-partner-docs/Partner-Brand_Collateral_28_2019-09-10.sharp.mp4");

        videoView.setVideoURI(videoUri);

        FullScreenMediaController mediaController = new FullScreenMediaController(this);
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //close the progress dialog when buffering is done
                pd.dismiss();
            }
//

        });

    }
}
