package com.around.engineerbuddy.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.around.engineerbuddy.SplashActivity;

public class ApplicationHelper {

    public  SharedPreferences sharedprefrence;
    SharedPreferences.Editor editor;
    public Context applicationContex;
   public ApplicationHelper(Context applicationContex){
        this.applicationContex=applicationContex;
    }
//    public  SharedPreferences getSharedPrefrences() {
//        if(sharedprefrence==null){
//            sharedprefrence=this.applicationContex.getSharedPreferences("myPrefrence", Context.MODE_PRIVATE);
//            //this.editor=sharedprefrence.edit();
//        }
//        return this.sharedprefrence;
//    }
    public  SharedPreferences getSharedPrefrences(String myprencesName) {
        if(sharedprefrence==null){
            if(this.applicationContex!=null) {
                sharedprefrence = this.applicationContex.getSharedPreferences(myprencesName, Context.MODE_PRIVATE);
            }
            //this.editor=sharedprefrence.edit();
        }
        return this.sharedprefrence;
    }

}
