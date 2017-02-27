package com.cj.samplechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cj.samplechat.R;
import com.cj.samplechat.bean.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/2/10.
 */

public class ContactAdapter extends BaseAdapter {
    private Context context;
    private List<User> contactList;

    public ContactAdapter(Context context, List<User> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public User getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ContactHolder contactHolder;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
            contactHolder = new ContactHolder(view);
            view.setTag(contactHolder);
        } else {
            view = convertView;
            contactHolder = (ContactHolder) view.getTag();
            contactHolder.contactName.setText(getItem(position).getUsername());
        }
        return view;
    }

    static class ContactHolder {
        @BindView(R.id.img_contact_avatar)
        ImageView imgContactAvatar;
        @BindView(R.id.contact_name)
        TextView contactName;
        @BindView(R.id.contact_msg)
        TextView contactMsg;

        ContactHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
