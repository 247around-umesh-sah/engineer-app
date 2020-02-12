package com.around.engineerbuddy;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.around.engineerbuddy.activity.ShowImageActivity;
import com.around.engineerbuddy.fragment.BMAFragment;
import com.around.engineerbuddy.fragment.SparePartsOrderFragment;
import com.around.engineerbuddy.util.BMAConstants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImagePicker {

    File file;
    Context context;
    Activity activity;
    int requestCodeForResult;
    BMAFragment obj;
//    Bitmap imageBitmap;
    boolean isChoosefromLibrary;
    Uri imageUri;
    public ImagePicker(Context context,Activity activity){
//        this.context=context;
//        this.activity=activity;
//        this.init();;
        this(context, activity, 0,null,null,false);

    }
    public ImagePicker(Context context, Activity activity, int requestCodeForResult, BMAFragment obj, Uri imageUri,boolean isChoosefromLibrary){
        this.context=context;
        this.activity=activity;
        this.requestCodeForResult=requestCodeForResult;
        this.obj=obj;
        this.imageUri=imageUri;
        this.isChoosefromLibrary=isChoosefromLibrary;
        this.init();;

    }
    private void init(){
        this.selectImage();
        }


    String userChoosenTask="";
    private void selectImage() {
        String viewString="View";
        String takePhoto="Take Photo";

        String choosefromLibrary="Choose from Library";
        CharSequence[] items = {takePhoto,choosefromLibrary,viewString,
                "Cancel"};
        if(!isChoosefromLibrary){
            items = new CharSequence[]{takePhoto,viewString,
                    "Cancel" };
        }
        if(imageUri==null){

           items = new CharSequence[]{takePhoto, choosefromLibrary,
                   "Cancel" };
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        CharSequence[] finalItems = items;
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                // boolean result = checkPermission(MainActivity.this);
                if (finalItems[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    Log.d("aaaaaa","getPhoto");
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
                } else if (finalItems[item].equals("Choose from Library")) {
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
                } else if (finalItems[item].equals("Cancel")) {
                    dialog.dismiss();

                }else if(finalItems[item].equals("View")){
                    dialog.dismiss();
                    Log.d("aaaaa","imageuri imapikcer = "+imageUri);
                    if(imageUri!=null) {
                        showImageDialog(imageUri);
//                        Log.d("aaaaa","showimagebitmap = "+imageBitmap+"      activity = "+activity+"       context = "+context);
//                        Intent intent = new Intent(activity, ShowImageActivity.class);
//                        intent.putExtra("imageBitmap", imageBitmap);
//                        activity.startActivity(intent);
                    }
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

            //getPhoto();
            Intent takePictureIntent=new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            ((SparePartsOrderFragment)obj).dispatchTakePictureIntent(requestCodeForResult,takePictureIntent);
        }
    }
    public void dispatchTakePictureIntent(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent,this.requestCodeForResult);


    }
    private void checkPermissonCA(){
        int checkPermisson=ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA);
        if(checkPermisson!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.CAMERA},BMAConstants.myreqCamera);

        }else {
            Log.d("aaaaaa","checkpermission  CA");
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
             //activity. startActivityForResult(cameraIntent, requestCodeForResult);
            ((SparePartsOrderFragment)obj).dispatchTakePictureIntent(requestCodeForResult,cameraIntent);
            //catchPhoto();
        }
    }
    private void checkPermissonCW(){
        Log.d("aaaaaa","checkpermission ");
        int checkPermisson=ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(checkPermisson!=PackageManager.PERMISSION_GRANTED){
            Log.d("aaaaaa","imagepicker permissiongranted = "+activity.getClass().getSimpleName());
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},BMAConstants.myReqWriteCamera);

        }else {
            Log.d("aaaaaa","BEFORE checkpermission  CA");
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
        Log.d("aaaaaa","onrequest");
        switch (requestCode){
            case BMAConstants.myreqCamera:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //activity.startActivityForResult(cameraIntent, requestCodeForResult);
                ((SparePartsOrderFragment)obj).dispatchTakePictureIntent(requestCodeForResult,cameraIntent);
                //catchPhoto();
                break;
            case BMAConstants.myReqWriteCamera:
                checkPermissonCA();
                break;
            case BMAConstants.myreqReadGallary:
                checkPermissonWG();
                break;
            case BMAConstants.myreqWriteGallary:
               // getPhoto();
                Intent takePictureIntent=new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                ((SparePartsOrderFragment)obj).dispatchTakePictureIntent(requestCodeForResult,takePictureIntent);
                break;
        }
    }


private void showImageDialog(Uri imageUri){
    Bitmap imageBitmap= null;
    try {
        imageBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
    } catch (IOException e) {
        e.printStackTrace();
    }
    if(imageBitmap==null){
        imageBitmap=getBitmapFromURL(imageUri.toString());
        if(imageBitmap==null)
        return;
    }
    final Dialog d = new Dialog(context);
    d.requestWindowFeature(Window.FEATURE_NO_TITLE);
    d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    d.setContentView(R.layout.imagedialog);
    ImageView imageview= d.findViewById(R.id.showImage);
    imageview.setImageBitmap(imageBitmap);
   imageview.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            d.dismiss();
        }
    });

    d.show();
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
