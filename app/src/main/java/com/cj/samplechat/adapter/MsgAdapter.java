package com.cj.samplechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cj.samplechat.R;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/2/9.
 */

public class MsgAdapter extends BaseAdapter {
    private Context context;
    private List<EMMessage> msgList;

    public MsgAdapter(Context context, List<EMMessage> msgList) {
        this.context = context;
        this.msgList = msgList;
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public EMMessage getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = getItem(position);
        return message.direct() == EMMessage.Direct.RECEIVE ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EMMessage message = getItem(position);
        int viewType = getItemViewType(position);
        View view;
        ChatViewHolder chatViewHolder;
        if (convertView == null) {
            if (viewType == 0) {
                view = LayoutInflater.from(context).inflate(R.layout.chat_item_receiver, parent, false);
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.chat_item_send, parent, false);
            }
            chatViewHolder = new ChatViewHolder(view);
            view.setTag(chatViewHolder);
        } else {
            view = convertView;
            chatViewHolder = (ChatViewHolder) view.getTag();

        }

        String msg  = ((EMTextMessageBody)message.getBody()).getMessage();

        chatViewHolder.tvMsgContent.setText(msg);
        return view;
    }

    static class ChatViewHolder {
        @BindView(R.id.tv_msg_content)
        TextView tvMsgContent;

        ChatViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
