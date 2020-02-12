package com.around.engineerbuddy.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.around.engineerbuddy.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ShowImageActivity extends AppCompatActivity {
 Bitmap bitmap;
 String bitmapUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bitmapUrl=getIntent().getStringExtra("imageBitmap");
        setContentView(R.layout.imagedialog);
        Log.d("aaaaa","activity imagebutmap = "+bitmapUrl);
        ImageView imageview=findViewById(R.id.showImage);
        if(bitmapUrl!=null) {
            bitmap=getBitmapFromURL(bitmapUrl);
            Log.d("aaaaa","activity imagebutmap = "+bitmap);
            imageview.setImageBitmap(bitmap);
        }
    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
