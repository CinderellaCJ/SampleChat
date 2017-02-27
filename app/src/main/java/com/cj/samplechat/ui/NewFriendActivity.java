package com.cj.samplechat.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.cj.samplechat.R;
import com.cj.samplechat.adapter.NewFriendAdapter;
import com.cj.samplechat.db.InviteMsg;
import com.cj.samplechat.db.InviteMsgDao;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewFriendActivity extends Activity {

    @BindView(R.id.lv_new_friend)
    ListView lvNewFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
        ButterKnife.bind(this);

        InviteMsgDao inviteMsgDao = new InviteMsgDao(this);
        List<InviteMsg> inviteMsgList = inviteMsgDao.getMessagesList();
        NewFriendAdapter newFriendAdapter = new NewFriendAdapter(this,1,inviteMsgList);
        lvNewFriend.setAdapter(newFriendAdapter);
        inviteMsgDao.saveUnreadMessageCount(0);
    }
}
