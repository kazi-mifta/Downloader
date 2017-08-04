package com.kazi.downloader;

import android.content.Context;
import android.content.SharedPreferences;


/*This Is Where Shared Preferences are Crated To manage And Create Variables That will Be Used later by Differnet
Portions Of the App.*/

public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "downloader-app";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";


    public PrefManager(Context context){
        this._context=context;
        pref = _context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);

        editor = pref.edit();


    }


    public void setIsFirstTimeLaunch(boolean isFirstTime){

        editor.putBoolean(IS_FIRST_TIME_LAUNCH,isFirstTime);
        editor.commit();
    }


    public boolean isFirstTimeLaunch(){

        return pref.getBoolean(IS_FIRST_TIME_LAUNCH,true);

    }



}
