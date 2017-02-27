package com.cj.samplechat.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cj.samplechat.ECApplication;
import com.cj.samplechat.bean.User;
import com.cj.samplechat.db.InviteMsg.InviteMsgStatus;
import com.cj.samplechat.utils.CommonUtils;
import com.cj.samplechat.utils.Constant;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/10.
 */

public class DBManager {
    private static DBManager instance = new DBManager();
    private DBOpenHelper dbOpenHelper;

    private DBManager(){
        dbOpenHelper = DBOpenHelper.getInstance(ECApplication.getInstance().getApplicationContext());
    }

    public static synchronized DBManager getInstance(){
        if (instance == null){
            instance = new DBManager();
        }
        return instance;
    }

    /**
     * 保存好友list
     */
    synchronized public void saveContactList(List<User> contactList){
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        if (database.isOpen()){
            database.delete(UserDao.TABLE_NAME,null,null);
            for (User user:contactList){
                ContentValues values = new ContentValues();
                values.put(UserDao.COLUMN_NAME_ID,user.getUsername());
                if (user.getNick() != null){
                    values.put(UserDao.COLUMN_NAME_NICK,user.getNick());
                }
                if (user.getAvatar()!= null){
                    values.put(UserDao.COLUMN_NAME_AVATAR,user.getAvatar());
                }
                database.replace(UserDao.TABLE_NAME,null,values);
            }
        }
    }

    /**
     * 获取好友list
     *
     * @return
     */
    synchronized public Map<String, User> getContactList() {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Map<String, User> users = new Hashtable<String, User>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
                User user = new User(username);
                user.setNick(nick);
                user.setAvatar(avatar);
                if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)
                        || username.equals(Constant.CHAT_ROOM)|| username.equals(Constant.CHAT_ROBOT)) {
                    user.setInitialLetter("");
                } else {
                   CommonUtils.setUserInitialLetter(user);
                }
                users.put(username, user);
            }
            cursor.close();
        }
        return users;
    }

    /**
     * 删除一个联系人
     * @param username
     */
    synchronized public void deleteContact(String username){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if(db.isOpen()){
            db.delete(UserDao.TABLE_NAME, UserDao.COLUMN_NAME_ID + " = ?", new String[]{username});
        }
    }

    /**
     * 保存一个联系人
     * @param user
     */
    synchronized public void saveContact(User user){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
        if(user.getNick() != null)
            values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
        if(user.getAvatar() != null)
            values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
        if(db.isOpen()){
            db.replace(UserDao.TABLE_NAME, null, values);
        }
    }

    public void setDisabledGroups(List<String> groups){
        setList(UserDao.COLUMN_NAME_DISABLED_GROUPS, groups);
    }

    public List<String>  getDisabledGroups(){
        return getList(UserDao.COLUMN_NAME_DISABLED_GROUPS);
    }

    public void setDisabledIds(List<String> ids){
        setList(UserDao.COLUMN_NAME_DISABLED_IDS, ids);
    }

    public List<String> getDisabledIds(){
        return getList(UserDao.COLUMN_NAME_DISABLED_IDS);
    }

    synchronized private void setList(String column, List<String> strList){
        StringBuilder strBuilder = new StringBuilder();

        for(String hxid:strList){
            strBuilder.append(hxid).append("$");
        }

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(column, strBuilder.toString());

            db.update(UserDao.PREF_TABLE_NAME, values, null,null);
        }
    }

    synchronized private List<String> getList(String column){
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + column + " from " + UserDao.PREF_TABLE_NAME,null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        String strVal = cursor.getString(0);
        if (strVal == null || strVal.equals("")) {
            return null;
        }

        cursor.close();

        String[] array = strVal.split("$");

        if(array != null && array.length > 0){
            List<String> list = new ArrayList<String>();
            for(String str:array){
                list.add(str);
            }

            return list;
        }

        return null;
    }

    /**
     * 保存message
     * @param message
     * @return  返回这条messaged在db中的id
     */
    public synchronized Integer saveMessage(InviteMsg message){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int id = -1;
        if(db.isOpen()){
            ContentValues values = new ContentValues();
            values.put(InviteMsgDao.COLUMN_NAME_FROM, message.getFrom());
            values.put(InviteMsgDao.COLUMN_NAME_GROUP_ID, message.getGroupId());
            values.put(InviteMsgDao.COLUMN_NAME_GROUP_Name, message.getGroupName());
            values.put(InviteMsgDao.COLUMN_NAME_REASON, message.getReason());
            values.put(InviteMsgDao.COLUMN_NAME_TIME, message.getTime());
            values.put(InviteMsgDao.COLUMN_NAME_STATUS, message.getStatus().ordinal());
            values.put(InviteMsgDao.COLUMN_NAME_GROUPINVITER, message.getGroupInviter());
            db.insert(InviteMsgDao.TABLE_NAME, null, values);

            Cursor cursor = db.rawQuery("select last_insert_rowid() from " + InviteMsgDao.TABLE_NAME,null);
            if(cursor.moveToFirst()){
                id = cursor.getInt(0);
            }

            cursor.close();
        }
        return id;
    }

    /**
     * 更新message
     * @param msgId
     * @param values
     */
    synchronized public void updateMessage(int msgId,ContentValues values){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if(db.isOpen()){
            db.update(InviteMsgDao.TABLE_NAME, values, InviteMsgDao.COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(msgId)});
        }
    }

    /**
     * 获取messges
     * @return
     */
    synchronized public List<InviteMsg> getMessagesList(){
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        List<InviteMsg> msgs = new ArrayList<InviteMsg>();
        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select * from " + InviteMsgDao.TABLE_NAME + " desc",null);
            while(cursor.moveToNext()){
                InviteMsg msg = new InviteMsg();
                int id = cursor.getInt(cursor.getColumnIndex(InviteMsgDao.COLUMN_NAME_ID));
                String from = cursor.getString(cursor.getColumnIndex(InviteMsgDao.COLUMN_NAME_FROM));
                String groupid = cursor.getString(cursor.getColumnIndex(InviteMsgDao.COLUMN_NAME_GROUP_ID));
                String groupname = cursor.getString(cursor.getColumnIndex(InviteMsgDao.COLUMN_NAME_GROUP_Name));
                String reason = cursor.getString(cursor.getColumnIndex(InviteMsgDao.COLUMN_NAME_REASON));
                long time = cursor.getLong(cursor.getColumnIndex(InviteMsgDao.COLUMN_NAME_TIME));
                int status = cursor.getInt(cursor.getColumnIndex(InviteMsgDao.COLUMN_NAME_STATUS));
                String groupInviter = cursor.getString(cursor.getColumnIndex(InviteMsgDao.COLUMN_NAME_GROUPINVITER));

                msg.setId(id);
                msg.setFrom(from);
                msg.setGroupId(groupid);
                msg.setGroupName(groupname);
                msg.setReason(reason);
                msg.setTime(time);
                msg.setGroupInviter(groupInviter);

                if(status == InviteMsgStatus.BEINVITEED.ordinal())
                    msg.setStatus(InviteMsgStatus.BEINVITEED);
                else if(status == InviteMsgStatus.BEAGREED.ordinal())
                    msg.setStatus(InviteMsgStatus.BEAGREED);
                else if(status == InviteMsgStatus.BEREFUSED.ordinal())
                    msg.setStatus(InviteMsgStatus.BEREFUSED);
                else if(status == InviteMsgStatus.AGREED.ordinal())
                    msg.setStatus(InviteMsgStatus.AGREED);
                else if(status == InviteMsgStatus.REFUSED.ordinal())
                    msg.setStatus(InviteMsgStatus.REFUSED);
                else if(status == InviteMsgStatus.BEAPPLYED.ordinal())
                    msg.setStatus(InviteMsgStatus.BEAPPLYED);
                else if(status == InviteMsgStatus.GROUPINVITATION.ordinal())
                    msg.setStatus(InviteMsgStatus.GROUPINVITATION);
                else if(status == InviteMsgStatus.GROUPINVITATION_ACCEPTED.ordinal())
                    msg.setStatus(InviteMsgStatus.GROUPINVITATION_ACCEPTED);
                else if(status == InviteMsgStatus.GROUPINVITATION_DECLINED.ordinal())
                    msg.setStatus(InviteMsgStatus.GROUPINVITATION_DECLINED);

                msgs.add(msg);
            }
            cursor.close();
        }
        return msgs;
    }

    /**
     * 删除要求消息
     * @param from
     */
    synchronized public void deleteMessage(String from){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if(db.isOpen()){
            db.delete(InviteMsgDao.TABLE_NAME, InviteMsgDao.COLUMN_NAME_FROM + " = ?", new String[]{from});
        }
    }

    synchronized int getUnreadNotifyCount(){
        int count = 0;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select " + InviteMsgDao.COLUMN_NAME_UNREAD_MSG_COUNT + " from " + InviteMsgDao.TABLE_NAME, null);
            if(cursor.moveToFirst()){
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    synchronized void setUnreadNotifyCount(int count){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if(db.isOpen()){
            ContentValues values = new ContentValues();
            values.put(InviteMsgDao.COLUMN_NAME_UNREAD_MSG_COUNT, count);

            db.update(InviteMsgDao.TABLE_NAME, values, null,null);
        }
    }

    synchronized public void closeDB(){
        if(dbOpenHelper != null){
            dbOpenHelper.closeDB();
        }
        instance = null;
    }

}
