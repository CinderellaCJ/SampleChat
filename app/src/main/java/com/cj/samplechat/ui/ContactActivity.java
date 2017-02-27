package com.cj.samplechat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.cj.samplechat.ECApplication;
import com.cj.samplechat.R;
import com.cj.samplechat.adapter.ContactAdapter;
import com.cj.samplechat.bean.User;
import com.cj.samplechat.utils.CommonUtils;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactActivity extends Activity {

    @BindView(R.id.tv_contact_add)
    TextView tvContactAdd;
    @BindView(R.id.lv_contact)
    ListView lvContact;
    private List<User> contactList;
    private Map<String, User> contactsMap;
    private ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);

        contactList = new ArrayList<>();
      //  getContactList();
        contactAdapter = new ContactAdapter(this, contactList);
        lvContact.setAdapter(contactAdapter);
    }

    /**
     * 获取联系人列表，并过滤掉黑名单和排序
     */
    protected void getContactList() {
        contactList.clear();
        // 获取联系人列表
        contactsMap = ECApplication.getInstance().getContactList();
        if (contactsMap == null) {
            return;
        }
        synchronized (this.contactsMap) {
            Iterator<Map.Entry<String, User>> iterator = contactsMap.entrySet().iterator();
            List<String> blackList = EMClient.getInstance().contactManager().getBlackListUsernames();
            while (iterator.hasNext()) {
                Map.Entry<String, User> entry = iterator.next();
                // 兼容以前的通讯录里的已有的数据显示，加上此判断，如果是新集成的可以去掉此判断
                if (!entry.getKey().equals("item_new_friends") && !entry.getKey().equals("item_groups")
                        && !entry.getKey().equals("item_chatroom") && !entry.getKey().equals("item_robots")) {
                    if (!blackList.contains(entry.getKey())) {
                        // 不显示黑名单中的用户
                        User user = entry.getValue();
                        CommonUtils.setUserInitialLetter(user);
                        contactList.add(user);
                    }
                }
            }
        }

        // 排序
        Collections.sort(contactList, new Comparator<User>() {

            @Override
            public int compare(User lhs, User rhs) {
                if (lhs.getInitialLetter().equals(rhs.getInitialLetter())) {
                    return lhs.getNick().compareTo(rhs.getNick());
                } else {
                    if ("#".equals(lhs.getInitialLetter())) {
                        return 1;
                    } else if ("#".equals(rhs.getInitialLetter())) {
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }

            }
        });

    }

    @OnClick(R.id.tv_contact_add)
    public void onClick() {
        startActivity(new Intent(ContactActivity.this,AddContactActivity.class));
    }
}
