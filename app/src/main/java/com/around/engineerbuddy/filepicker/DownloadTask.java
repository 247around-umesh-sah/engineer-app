package com.around.engineerbuddy.filepicker;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask {
    private static final String TAG = "Download Task";
    private Context context;
    //private Button buttonText;
    private String downloadUrl = "", downloadFileName = "";

    public DownloadTask(Context context, String downloadUrl) {
        this.context = context;
       // this.buttonText = buttonText;
        this.downloadUrl = downloadUrl;
         downloadFileName =("pdf");//downloadUrl.replace("http://androhub.com/demo/", "");//Create file name by picking download file name from URL
        Log.e(TAG, downloadFileName);

        //Start Downloading Task
        new DownloadingTask().execute();
    }

    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            buttonText.setEnabled(false);
//            buttonText.setText("DownLoad Started...");//Set Button Text when download started
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                    Toast.makeText(context, "Download Complete.", Toast.LENGTH_SHORT).show();
                    BMAFilePicker.openDownLoadFile(context);
                    //BMAFilePicker.openFilePicker(context,outputFile);
                   // BMAFilePicker.openFile(context,outputFile);

//                    buttonText.setEnabled(true);
//                    buttonText.setText("Download Completed");//If Download completed then change button text
                } else {
//                    buttonText.setText("Download Failed");//If download failed change button text
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            buttonText.setEnabled(true);
//                            buttonText.setText("DownloadA Again");//Change button text again after 3sec
//                        }
//                    }, 3000);

                    Log.e(TAG, "Download Failed");
                    Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show();

                }
            } catch (Exception e) {
                e.printStackTrace();

                //Change button text if exception occurs
//                buttonText.setText("Download Failed");
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        buttonText.setEnabled(true);
//                        buttonText.setText("Download Again");
//                    }
//                }, 3000);
//                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());

            }


            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl   ("http://maven.apache.org/maven-1.x/maven.pdf");//
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.setDoOutput(true);
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());

                }


                //Get File if SD card is present
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    apkStorage = new File(
                            Environment.getExternalStorageDirectory().toString() + "/"
                                    + "downloads");
                } else
                    Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

                //If File is not present create directory
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e(TAG, "Directory Created.");
                }

                outputFile = new File(apkStorage, downloadFileName);//Create Output file in Main File

                //Create New File if not present
                Log.d("aaaaa","size = "+c.getContentLength()+"     .. output = "+outputFile.getAbsolutePath()+"    apkfile = "+apkStorage.getAbsolutePath()+"     downloadfile = "+downloadFileName);
                if (outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                }


                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024*1024*100];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }

            return null;
        }
    }






}
