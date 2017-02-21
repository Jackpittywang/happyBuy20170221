package dotc.android.happybuy.persist.database.award;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.persist.database.base.BaseDao;
import dotc.android.happybuy.persist.database.location.LocationColumns;
import dotc.android.happybuy.persist.database.location.LocationEntity;


/**
 * Created by wangjun on 16/2/26.
 */
public class AwardDao extends BaseDao {

    public AwardDao(Context context, SQLiteDatabase db) {
        super(context, db);
    }

    public boolean saveOneAward(String uid,String productId,String productItemId,int period,String awardNumber){
        ContentValues values = new ContentValues();
        values.put(AwardColumns.UID, uid);
        values.put(AwardColumns.PRODUCT_ID, productId);
        values.put(AwardColumns.PRODUCT_ITEM_ID, productItemId);
        values.put(AwardColumns.PERIOD, period);
        values.put(AwardColumns.AWARD_NUMBER, awardNumber);
        values.put(AwardColumns.SHOW, 1);
        values.put(AwardColumns.CREATE_TIME, System.currentTimeMillis());
        values.put(AwardColumns.UPDATE_TIME, System.currentTimeMillis());
        long id = db.insert(AwardColumns.TABLE_NAME, null, values);
        return id > -1;
    }

    public boolean isExistAward(String uid,String productId,String productItemId,int period){
        String selection = AwardColumns.UID+"=? and "+AwardColumns.PRODUCT_ITEM_ID+"=? ";
        String[] selectionArgs = new String[]{String.valueOf(uid),productItemId};
        Cursor cursor = null;
        try{
            cursor = db.query(AwardColumns.TABLE_NAME, AwardColumns.ALL_COLUMNS,selection,selectionArgs,null,null,null);
            return cursor.getCount()>0;
        } finally {
            if(cursor!=null){
                cursor.close();
            }
        }
    }

}
