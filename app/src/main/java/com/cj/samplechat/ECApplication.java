package com.cj.samplechat;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.cj.samplechat.bean.User;
import com.cj.samplechat.db.MyInfo;
import com.cj.samplechat.db.UserDao;
import com.cj.samplechat.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.hyphenate.chat.EMGCMListenerService.TAG;

/**
 * Created by Administrator on 2017/2/7.
 */

public class ECApplication extends Application {

    private static ECApplication applicationInstance;
    public static Context applicationContext;
    private String username = null;
    private Map<String, User> contactList;
    private UserDao userDao;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        applicationInstance = this;
        initChat(applicationContext);
    }


    public static ECApplication getInstance() {
        return applicationInstance;
    }


    private void initChat(Context context){
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回

        if (processAppName == null ||!processAppName.equalsIgnoreCase(this.getPackageName())) {
            Log.e(TAG, "enter the service process!");

            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

        options.setAcceptInvitationAlways(false);
        //初始化
        EMClient.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);

        initDbDao(context);
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    private void initDbDao(Context context) {
        userDao = new UserDao(context);
    }
    public void  setCurrentUserName(String username){
        this.username = username;
        MyInfo.getInstance(applicationInstance).setUserInfo(Constant.KEY_USERNAME,username);

    }

    public String getCurrentUserName() {
        if (TextUtils.isEmpty(username)) {
            username = MyInfo.getInstance(applicationInstance).getUserInfo(Constant.KEY_USERNAME);
        }
        return username;
    }

    public void setContactList(Map<String, User> contactList) {

        this.contactList = contactList;
        userDao.saveContactList(new ArrayList<User>(contactList.values()));

    }
    public Map<String, User> getContactList() {
        if (contactList == null) {
            contactList = userDao.getContactList();
        }
        return contactList;
    }

}
