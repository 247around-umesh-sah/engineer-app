package com.around.engineerbuddy.filepicker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.around.engineerbuddy.entity.EODocumentType;

import java.io.File;
import java.io.IOException;

public class BMAFilePicker  {
    public static void openDownLoadFile(Context context, EODocumentType eoDocumentType){

//        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"Downloads");
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        context.startActivity(intent);
            //First check if SD Card is present or not
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                //Get Download Directory File
                File apkStorage = new File(
                        Environment.getExternalStorageDirectory() + "/"
                                + "downloads");///"+eoDocumentType.documentDescription+eoDocumentType.documentType);

                //If file is not present then display Toast
                if (!apkStorage.exists())
                   Toast.makeText(context, "Right now there is no directory. Please download some file first.", Toast.LENGTH_SHORT).show();

                else {

                    //If directory is present Open Folder

                    /** Note: Directory will open only if there is a app to open directory like File Manager, etc.  **/

//                    File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Downloads/" + "demo.pdf");  // -> filename = maven.pdf
//                    Uri path = Uri.fromFile(pdfFile);
//                    Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
//                    pdfIntent.setDataAndType(path, "application/pdf");
//                    pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                  //  Intent intent = new Intent(Intent.ACTION_VIEW);
                    Log.d("aaaaaa","absolute path = "+Environment.getExternalStorageDirectory().getAbsolutePath());
                    Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath());
                          //  + "/" +"downloads");// +
                            //"/"+eoDocumentType.documentDescription+eoDocumentType.documentType);
                    Log.d("aaaaa","view  = "+uri.getPath());
                    intent.setDataAndType(uri, "application/pdf");
                    //intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);//(Intent.createChooser(intent, "Open Downloads Folder"));
                }

            } else
                Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

      //  }
    }


    public static void openFile(Context context, File file) throws IOException {
       // File file = new File(urlPath);//"your any type of file url"
        // Create URI
       // File file=url;
        Uri uri = Uri.fromFile(file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (file.toString().contains(".doc") || file.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if(file.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if(file.toString().contains(".ppt") || file.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if(file.toString().contains(".xls") || file.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if(file.toString().contains(".zip") || file.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if(file.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if(file.toString().contains(".wav") || file.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if(file.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if(file.toString().contains(".jpg") || file.toString().contains(".jpeg") || file.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if(file.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if(file.toString().contains(".3gp") || file.toString().contains(".mpg") || file.toString().contains(".mpeg") || file.toString().contains(".mpe") || file.toString().contains(".mp4") || file.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}
