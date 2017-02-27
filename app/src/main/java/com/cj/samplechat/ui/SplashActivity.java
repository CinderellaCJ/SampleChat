package com.cj.samplechat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.cj.samplechat.R;
import com.hyphenate.chat.EMClient;

public class SplashActivity extends Activity {

    private static final int sleepTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (EMClient.getInstance().isLoggedInBefore()){
                    long startTime = System.currentTimeMillis();
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    long costTime = System.currentTimeMillis() - startTime;
                    if (costTime < sleepTime){
                        try {
                            Thread.sleep(sleepTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                }else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                    finish();
                }
            }
        }).start();
    }
}
