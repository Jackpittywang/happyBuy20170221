package dotc.android.happybuy.persist.database.message;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import dotc.android.happybuy.persist.database.award.AwardColumns;
import dotc.android.happybuy.persist.database.base.BaseDao;


/**
 * Created by wangjun on 16/2/26.
 */
public class MessageDao extends BaseDao {

    public MessageDao(Context context, SQLiteDatabase db) {
        super(context, db);
    }

    public boolean addMessage(String uid,String messageId,String source,String icon,String title,
            String message,int type,String data){
        ContentValues values = new ContentValues();
        values.put(MessageColumns.UID, uid);
        values.put(MessageColumns.MESSAGE_ID, messageId);
        values.put(MessageColumns.MESSAGE_SOURCE, source);
        values.put(MessageColumns.ICON, icon);
        values.put(MessageColumns.TITLE, title);
        values.put(MessageColumns.MESSAGE, message);
        values.put(MessageColumns.TYPE, type);
        values.put(MessageColumns.DATA, data);
        values.put(MessageColumns.IS_READ, MessageEntity.NO);
        values.put(MessageColumns.CREATE_TIME, System.currentTimeMillis());
        values.put(MessageColumns.UPDATE_TIME, System.currentTimeMillis());
        long id = db.insert(MessageColumns.TABLE_NAME, null, values);
        return id > -1;
    }

    public boolean isExistMessage(String uid,String messageId){
        String selection = MessageColumns.MESSAGE_ID+"=? ";//MessageColumns.UID+"=? and "+
        String[] selectionArgs = new String[]{messageId};
        Cursor cursor = null;
        try{
            cursor = db.query(MessageColumns.TABLE_NAME, MessageColumns.ALL_COLUMNS,selection,selectionArgs,null,null,null);
            return cursor.getCount()>0;
        } finally {
            if(cursor!=null){
                cursor.close();
            }
        }
//        return false;
    }

    public void setMessageRead(long id, boolean isread){
        ContentValues values = new ContentValues();
        values.put(MessageColumns.IS_READ, isread?MessageEntity.YES:MessageEntity.NO);
        String selection = MessageColumns._ID+"=? ";//MessageColumns.UID+"=? and "+
        String[] selectionArgs = new String[]{String.valueOf(id)};
        db.update(MessageColumns.TABLE_NAME,values,selection,selectionArgs);
    }

}
