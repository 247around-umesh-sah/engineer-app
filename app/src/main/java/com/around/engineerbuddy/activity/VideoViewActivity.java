package com.around.engineerbuddy.activity;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import com.around.engineerbuddy.R;
import com.around.engineerbuddy.fragment.HelpingDocumentFragment;

public class VideoViewActivity extends AppCompatActivity {
    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        String url=getIntent().getStringExtra("url");
        setContentView(R.layout.activity_video_view);
         videoView = findViewById(R.id.videoView);
        videoView.setVisibility(View.VISIBLE);
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading please wait...");
        pd.show();
        Uri uri = Uri.parse(url);//(eoDocumentType.file);//("https://s3.amazonaws.com/bookings-collateral/vendor-partner-docs/Partner-Brand_Collateral_21_2019-05-15.Lpg To Png_E.m4v");
        videoView.setRotation(90f);

        videoView.setVideoURI(uri);
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //close the progress dialog when buffering is done
                pd.dismiss();
            }
        });
    }


}
