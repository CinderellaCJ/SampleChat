package com.cj.samplechat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cj.samplechat.R;
import com.cj.samplechat.db.InviteMsg;
import com.cj.samplechat.db.InviteMsgDao;
import com.cj.samplechat.db.UserDao;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {


    @BindView(R.id.btn_chat)
    Button btnChat;
    @BindView(R.id.btn_contact)
    Button btnContact;
    @BindView(R.id.btn_invite)
    Button btnInvite;

    private InviteMsgDao inviteMsgDao;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        inviteMsgDao = new InviteMsgDao(this);
        userDao = new UserDao(this);
    }

    @OnClick({R.id.btn_chat, R.id.btn_contact, R.id.btn_invite})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_chat:
                startActivity(new Intent(this,ChatActivity.class));
                break;
            case R.id.btn_contact:
                startActivity(new Intent(this,ContactActivity.class));
                break;
            case R.id.btn_invite:
                startActivity(new Intent(this,NewFriendActivity.class));
                break;
        }
    }

    /**
     * 保存并提示消息的邀请消息
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMsg msg){
        if(inviteMsgDao == null){
            inviteMsgDao = new InviteMsgDao(MainActivity.this);
        }
        inviteMsgDao.saveMessage(msg);
        //保存未读数，这里没有精确计算
        inviteMsgDao.saveUnreadMessageCount(1);
        // 提示有新消息
        //响铃或其他操作
    }


}
