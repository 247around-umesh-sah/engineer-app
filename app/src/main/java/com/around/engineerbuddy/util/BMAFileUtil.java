package com.around.engineerbuddy.util;

import com.around.engineerbuddy.BMAApplication;
import com.around.engineerbuddy.BMAGson;
import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.helper.ApplicationHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BMAFileUtil {


    public static <T> ArrayList<T> objectListFromFile(String fileName, Class<T> entity) {
        String result = streamToString(getFileFromAsset(fileName));
        if (result!=null && result.length()>0) {
            return BMAGson.store().getList(entity, result);
        }
        return new ArrayList<>();
    }
    public static <T> T objectFromFile(String fileName, Class<T> entity) {
        String result = streamToString(getFileFromAsset(fileName));
        if (result!=null && result.length()>0) {
            return BMAGson.store().getObject(entity, result);
        }
        return null;
    }
    public static InputStream getFileFromAsset(String fileName) {

        try {
            if (fileName!=null && fileName.length()>0) {
                return MainActivityHelper.applicationHelper().applicationContex.getAssets().open(fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String streamToString(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferReader = new BufferedReader(inputStreamReader);
            StringBuilder fileContent = new StringBuilder();
            String str;
            while ((str = bufferReader.readLine()) != null) {
                fileContent.append(str.trim());
            }
            return fileContent.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
