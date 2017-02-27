package com.cj.samplechat.db;

import android.content.ContentValues;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2017/2/13.
 */

public class InviteMsgDao {
    static final String TABLE_NAME = "new_friends_msgs";
    static final String COLUMN_NAME_ID = "id";
    static final String COLUMN_NAME_FROM = "username";
    static final String COLUMN_NAME_GROUP_ID = "groupid";
    static final String COLUMN_NAME_GROUP_Name = "groupname";

    static final String COLUMN_NAME_TIME = "time";
    static final String COLUMN_NAME_REASON = "reason";
    public static final String COLUMN_NAME_STATUS = "status";
    static final String COLUMN_NAME_ISINVITEFROMME = "isInviteFromMe";
    static final String COLUMN_NAME_GROUPINVITER = "groupinviter";

    static final String COLUMN_NAME_UNREAD_MSG_COUNT = "unreadMsgCount";


    public InviteMsgDao(Context context){
    }

    /**
     * 保存message
     * @param message
     * @return  返回这条messaged在db中的id
     */
    public Integer saveMessage(InviteMsg message){
        return DBManager.getInstance().saveMessage(message);
    }

    /**
     * 更新message
     * @param msgId
     * @param values
     */
    public void updateMessage(int msgId,ContentValues values){
        DBManager.getInstance().updateMessage(msgId, values);
    }

    /**
     * 获取messges
     * @return
     */
    public List<InviteMsg> getMessagesList(){
        return DBManager.getInstance().getMessagesList();
    }

    public void deleteMessage(String from){
        DBManager.getInstance().deleteMessage(from);
    }

    public int getUnreadMessagesCount(){
        return DBManager.getInstance().getUnreadNotifyCount();
    }

    public void saveUnreadMessageCount(int count){
        DBManager.getInstance().setUnreadNotifyCount(count);
    }
}
