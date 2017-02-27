package com.cj.samplechat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cj.samplechat.ECApplication;

/**
 * Created by Administrator on 2017/2/10.
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;
    private static DBOpenHelper helperInstance;

    private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
            + UserDao.TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_NICK + " TEXT, "
            + UserDao.COLUMN_NAME_AVATAR + " TEXT, "
            + UserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";

    private static final String INIVTE_MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + InviteMsgDao.TABLE_NAME + " ("
            + InviteMsgDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + InviteMsgDao.COLUMN_NAME_FROM + " TEXT, "
            + InviteMsgDao.COLUMN_NAME_GROUP_ID + " TEXT, "
            + InviteMsgDao.COLUMN_NAME_GROUP_Name + " TEXT, "
            + InviteMsgDao.COLUMN_NAME_REASON + " TEXT, "
            + InviteMsgDao.COLUMN_NAME_STATUS + " INTEGER, "
            + InviteMsgDao.COLUMN_NAME_ISINVITEFROMME + " INTEGER, "
            + InviteMsgDao.COLUMN_NAME_UNREAD_MSG_COUNT + " INTEGER, "
            + InviteMsgDao.COLUMN_NAME_TIME + " TEXT, "
            + InviteMsgDao.COLUMN_NAME_GROUPINVITER + " TEXT); ";

    private static final String ROBOT_TABLE_CREATE = "CREATE TABLE "
            + UserDao.ROBOT_TABLE_NAME + " ("
            + UserDao.ROBOT_COLUMN_NAME_ID + " TEXT PRIMARY KEY, "
            + UserDao.ROBOT_COLUMN_NAME_NICK + " TEXT, "
            + UserDao.ROBOT_COLUMN_NAME_AVATAR + " TEXT);";

    private static final String CREATE_PREF_TABLE = "CREATE TABLE "
            + UserDao.PREF_TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_DISABLED_GROUPS + " TEXT, "
            + UserDao.COLUMN_NAME_DISABLED_IDS + " TEXT);";

    private DBOpenHelper(Context context) {
        super(context, getUserDatabaseName(), null, DATABASE_VERSION);
    }

    public static DBOpenHelper getInstance(Context context) {
        if (helperInstance == null) {
            helperInstance = new DBOpenHelper(context.getApplicationContext());
        }
        return helperInstance;
    }

    private static String getUserDatabaseName() {
        return  ECApplication.getInstance().getCurrentUserName() + "_demo.db";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERNAME_TABLE_CREATE);
        db.execSQL(INIVTE_MESSAGE_TABLE_CREATE);
        db.execSQL(CREATE_PREF_TABLE);
        db.execSQL(ROBOT_TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 2){
            db.execSQL("ALTER TABLE "+ UserDao.TABLE_NAME +" ADD COLUMN "+
                    UserDao.COLUMN_NAME_AVATAR + " TEXT ;");
        }

        if(oldVersion < 3){
            db.execSQL(CREATE_PREF_TABLE);
        }
        if(oldVersion < 4){
            db.execSQL(ROBOT_TABLE_CREATE);
        }
        if(oldVersion < 5){
            db.execSQL("ALTER TABLE " + InviteMsgDao.TABLE_NAME + " ADD COLUMN " +
                    InviteMsgDao.COLUMN_NAME_UNREAD_MSG_COUNT + " INTEGER ;");
        }
        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE " + InviteMsgDao.TABLE_NAME + " ADD COLUMN " +
                    InviteMsgDao.COLUMN_NAME_GROUPINVITER + " TEXT;");
        }
    }

    public void closeDB() {
        if (helperInstance != null) {
            try {
                SQLiteDatabase db = helperInstance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            helperInstance = null;
        }
    }
}
