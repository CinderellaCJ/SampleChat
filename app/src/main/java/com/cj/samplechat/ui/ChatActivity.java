package com.cj.samplechat.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cj.samplechat.R;
import com.cj.samplechat.adapter.MsgAdapter;
import com.cj.samplechat.utils.LogUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cj.samplechat.utils.Constant.CHATTYPE_GROUP;

public class ChatActivity extends Activity implements EMMessageListener {

    @BindView(R.id.tv_sendUser)
    TextView tvSendUser;
    @BindView(R.id.lv_chat)
    ListView lvChat;
    @BindView(R.id.edt_sendMsg)
    EditText edtSendMsg;
    @BindView(R.id.btn_send)
    Button btnSend;

    private int chatType = 1;
    private List<EMMessage> msgList;
    private EMConversation conversation;
    private String toChatUsername = "ccc";
    protected int pageSize = 20;
    private MsgAdapter msgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername,EMConversation.EMConversationType.Chat,true);
        msgList = conversation.getAllMessages();
        getAllMessage();
        msgAdapter = new MsgAdapter(this,msgList);
        lvChat.setAdapter(msgAdapter);
        lvChat.setSelection(lvChat.getCount() - 1);

        EMClient.getInstance().chatManager().addMessageListener(this);

    }

    protected void getAllMessage(){
        conversation.markAllMessagesAsRead();
        int msgCount = msgList != null ? msgList.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pageSize){
            String msgId = null;
            if (msgList !=null && msgList.size() > 0){
                msgId = msgList.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId,pageSize - msgCount);
        }
    }

    @OnClick(R.id.btn_send)
    public void onClick() {
        String inputMsg = edtSendMsg.getText().toString();
        toChatUsername = "aaa";

        if (TextUtils.isEmpty(inputMsg)) {
            return;
        }

        EMMessage message = EMMessage.createTxtSendMessage(inputMsg, toChatUsername);
        if (chatType == CHATTYPE_GROUP){
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        EMClient.getInstance().chatManager().sendMessage(message);

        msgList.add(message);
        msgAdapter.notifyDataSetChanged();

        if (msgList.size() > 0){
            lvChat.setSelection(lvChat.getCount() - 1);
        }

        edtSendMsg.setText("");
        edtSendMsg.clearFocus();

        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                LogUtils.d("发送成功");
            }

            @Override
            public void onError(int code, String error) {
                LogUtils.d("发送失败" + code + ";" +error);
            }

            @Override
            public void onProgress(int progress, String status) {
            }
        });
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        for (EMMessage message : messages){
            String username = null;
            if (message.getChatType() == EMMessage.ChatType.Chat){
                username = message.getFrom();
            }else {
                username = message.getTo();
            }
            //当前对话则刷新聊天界面
            if (username.equals(toChatUsername)){

            }

            msgList.addAll(messages);
            msgAdapter.notifyDataSetChanged();
            if (msgList.size() > 0){
                lvChat.setSelection(lvChat.getCount() - 1);
            }
        }
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {

    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages) {

    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }
}
