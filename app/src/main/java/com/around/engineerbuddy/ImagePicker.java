package com.around.engineerbuddy;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.around.engineerbuddy.util.BMAConstants;

import java.io.File;

public class ImagePicker {

    File file;
    Context context;
    Activity activity;
    int requestCodeForResult;
    public ImagePicker(Context context,Activity activity){
//        this.context=context;
//        this.activity=activity;
//        this.init();;
        this(context, activity, 0);

    }
    public ImagePicker(Context context,Activity activity,int requestCodeForResult){
        this.context=context;
        this.activity=activity;
        this.requestCodeForResult=requestCodeForResult;
        this.init();;

    }
    private void init(){
        this.selectImage();
        }


    String userChoosenTask="";
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                // boolean result = checkPermission(MainActivity.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    checkPermissonCW();
//                    int checkPermisson=ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                    if(checkPermisson!=PackageManager.PERMISSION_GRANTED){
//                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},myReqWriteCamera);
//
//                    }else {
//
//                    }
                    /////////////
//                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.CAMERA}, 1);
//                    } else {
////                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
////                        startActivityForResult(galleryIntent, 0);
//                        cameraIntent();
//                    }
//                        //cameraIntent();
                    /////////
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    checkPermissonRG();
                    //////////////////
                    //if (result)
//                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//                    } else {
//                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//                        startActivityForResult(galleryIntent, 0);
//                        //galleryIntent();
//                    }
                    ///////////////
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void checkPermissonRG(){
        int checkPermisson= ContextCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE);
        if(checkPermisson!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, BMAConstants.myreqReadGallary);

        }else {

            checkPermissonWG();
        }
    }
    private void checkPermissonWG(){
        int checkPermisson=ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(checkPermisson!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},BMAConstants.myreqWriteGallary);

        }else {

            getPhoto();
        }
    }
    private void getPhoto(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent,this.requestCodeForResult);


    }
    private void checkPermissonCA(){
        int checkPermisson=ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA);
        if(checkPermisson!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.CAMERA},BMAConstants.myreqCamera);

        }else {

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
           activity. startActivityForResult(cameraIntent, requestCodeForResult);
            //catchPhoto();
        }
    }
    private void checkPermissonCW(){
        int checkPermisson=ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(checkPermisson!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},BMAConstants.myReqWriteCamera);

        }else {

            checkPermissonCA();
        }
    }
    public void catchPhoto(){
        this.file=getFile();
        Log.d("aaaaaa","getFilecatchPic");
        if(this.file!=null){
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Uri photoUri = Uri.fromFile(this.file);//FileProvider.getUriForFile(getApplicationContext(), getPackageName()+".fileprovider", this.file);//
                // Uri.fromFile(this.file);
                Log.d("aaaaaa","catchPhoto");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                activity.startActivityForResult(intent, this.requestCodeForResult);//BMAConstants.captureCamera);
            }catch (ActivityNotFoundException e){

            }
        }else{
            Toast.makeText(context,"Check Your SD card Status ",Toast.LENGTH_SHORT).show();
        }
    }
    private File getFile(){
        File fileDir=new File(Environment.getExternalStorageDirectory()+"/Android/data"+context.getPackageName()+"/File");
        if(!fileDir.exists()){
            if(!fileDir.mkdirs()){
                return null;
            }
        }
        File mediaFile= new File(fileDir.getPath()+File.separator+"temp.jpg");
        return mediaFile;
    }



    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults){

        switch (requestCode){
            case BMAConstants.myreqCamera:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                activity.startActivityForResult(cameraIntent, requestCodeForResult);
                //catchPhoto();
                break;
            case BMAConstants.myReqWriteCamera:
                checkPermissonCA();
                break;
            case BMAConstants.myreqReadGallary:
                checkPermissonWG();
                break;
            case BMAConstants.myreqWriteGallary:
                getPhoto();
                break;
        }
    }






}
