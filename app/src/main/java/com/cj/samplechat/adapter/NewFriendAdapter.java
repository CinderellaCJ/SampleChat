package com.cj.samplechat.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cj.samplechat.R;
import com.cj.samplechat.db.InviteMsg;
import com.cj.samplechat.db.InviteMsg.InviteMsgStatus;
import com.cj.samplechat.db.InviteMsgDao;
import com.hyphenate.chat.EMClient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/2/13.
 */

public class NewFriendAdapter extends ArrayAdapter<InviteMsg> {
    private Context context;
    private InviteMsgDao inviteMsgDao;


    public NewFriendAdapter(Context context, int textViewResourceId, List<InviteMsg> objects) {
        super(context,textViewResourceId, objects);
        this.context = context;
        inviteMsgDao = new InviteMsgDao(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        final NewFriendHolder newFriendHolder;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_new_friend, parent, false);
            newFriendHolder = new NewFriendHolder(view);
            convertView.setTag(view);
        } else {
            view = convertView;
            newFriendHolder = (NewFriendHolder) view.getTag();
        }
        final InviteMsg inviteMsg = getItem(position);
        if (inviteMsg != null){
            newFriendHolder.btnInviteAgree.setVisibility(View.INVISIBLE);
            newFriendHolder.tvInviteName.setText(inviteMsg.getFrom());
            newFriendHolder.tvInviteReason.setText(inviteMsg.getReason());
            if (inviteMsg.getStatus() == InviteMsgStatus.BEAGREED){
                newFriendHolder.tvInviteReason.setText("已同意你的好友请求");

            }else if (inviteMsg.getStatus() == InviteMsgStatus.BEINVITEED ||
                      inviteMsg.getStatus() == InviteMsgStatus.BEAPPLYED ||
                      inviteMsg.getStatus() == InviteMsgStatus.GROUPINVITATION){
                newFriendHolder.btnInviteAgree.setVisibility(View.VISIBLE);
                newFriendHolder.btnInviteAgree.setEnabled(true);
                newFriendHolder.btnInviteAgree.setText("同意");
                if (newFriendHolder.tvInviteReason == null){
                    newFriendHolder.tvInviteReason.setText("请求加你为好友");
                }
                newFriendHolder.btnInviteAgree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acceptInvitation(newFriendHolder.btnInviteAgree, inviteMsg);

                    }
                });
            }
        }
        return view;
    }

    private void acceptInvitation(final Button btnInviteAgree , final InviteMsg msg) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("正在同意...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {
                try {
                    if (msg.getStatus() == InviteMsgStatus.BEINVITEED) {//同意好友请求
                        EMClient.getInstance().contactManager().acceptInvitation(msg.getFrom());
                    } else if (msg.getStatus() == InviteMsgStatus.BEAPPLYED) { //同意加群申请
                        EMClient.getInstance().groupManager().acceptApplication(msg.getFrom(), msg.getGroupId());
                    } else if (msg.getStatus() == InviteMsgStatus.GROUPINVITATION) {
                        EMClient.getInstance().groupManager().acceptInvitation(msg.getGroupId(), msg.getGroupInviter());
                    }
                    msg.setStatus(InviteMsgStatus.AGREED);
                    // 更新db
                    ContentValues values = new ContentValues();
                    values.put(InviteMsgDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
                    inviteMsgDao.updateMessage(msg.getId(), values);
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @SuppressWarnings("deprecation")
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            btnInviteAgree.setText("同意");
                            btnInviteAgree.setBackgroundDrawable(null);
                            btnInviteAgree.setEnabled(false);


                        }
                    });
                } catch (final Exception e) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @SuppressLint("ShowToast")
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(context, "请求加你为好友" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }

    static class NewFriendHolder {
        @BindView(R.id.img_invite_avatar)
        ImageView imgInviteAvatar;
        @BindView(R.id.tv_invite_name)
        TextView tvInviteName;
        @BindView(R.id.tv_invite_reason)
        TextView tvInviteReason;
        @BindView(R.id.btn_invite_agree)
        Button btnInviteAgree;

        NewFriendHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
