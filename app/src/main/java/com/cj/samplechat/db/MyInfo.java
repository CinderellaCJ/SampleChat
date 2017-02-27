package com.cj.samplechat.db;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/2/10.
 */

public class MyInfo  {

    public static final String PREFERENCE_NAME = "local_userinfo";
    private static SharedPreferences sharedPreferences;
    private static MyInfo myInfo;
    private static SharedPreferences.Editor editor;

    private MyInfo(Context context){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);
    }

    public static MyInfo getInstance(Context context){
        if (myInfo == null){
            myInfo = new MyInfo(context);
        }
        editor = sharedPreferences.edit();
        return myInfo;
    }

    public void setUserInfo(String userKey , String userValue){
        editor.putString(userKey,userValue);
        editor.apply();
    }

    public String getUserInfo(String userKey){
        return sharedPreferences.getString(userKey,"");
    }
}
