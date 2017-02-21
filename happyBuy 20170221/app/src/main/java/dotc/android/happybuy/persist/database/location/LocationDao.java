package dotc.android.happybuy.persist.database.location;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.persist.database.base.BaseDao;


/**
 * Created by wangjun on 16/2/26.
 */
public class LocationDao extends BaseDao {

    public LocationDao(Context context, SQLiteDatabase db) {
        super(context, db);
    }

    public List<LocationEntity> loadAll(){
        Cursor cursor = null;
        try{
            cursor = db.query(LocationColumns.TABLE_NAME, LocationColumns.ALL_COLUMNS,null,null,null,null, LocationColumns.CREATE_TIME+" desc");
            List<LocationEntity> appEntityList = new ArrayList<>();
            while (cursor.moveToNext()){
                appEntityList.add(new LocationEntity(cursor));
            }
            HBLog.d(TAG + " loadAll " + appEntityList.size());
            return appEntityList;
        } finally {
            if(cursor!=null){
                cursor.close();
            }
        }
    }

    public LocationEntity loadByRowId(long id) {
        String selection = LocationColumns._ID+"=?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        Cursor cursor = null;
        try{
            cursor = db.query(LocationColumns.TABLE_NAME, LocationColumns.ALL_COLUMNS,selection,selectionArgs,null,null,null);
            return new LocationEntity(cursor);
        } finally {
            if(cursor!=null){
                cursor.close();
            }
        }
    }

    public long[] saveAppEntities(List<LocationEntity> appEntityList){
        long[] ids = new long[appEntityList.size()];
        db.beginTransaction();
        try {
//            db.delete(LocationColumns.TABLE_NAME,null,null);
            for(int i=0;i<appEntityList.size();i++){
                LocationEntity appEntity = appEntityList.get(i);
                ContentValues values = buildContentValues(appEntity);
                long id = db.insert(LocationColumns.TABLE_NAME, null, values);
                ids[i] = id;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return ids;
    }

    public void deleteAppEntity(LocationEntity appEntity){
        List<LocationEntity> appEntityList = new ArrayList<>();
        appEntityList.add(appEntity);
        deleteAppEntities(appEntityList);
    }

    public int getMaxSort(){
        Cursor cursor = null;
        try{
            String sql = "select max("+ LocationColumns.SORT+") as maxSort from "+ LocationColumns.TABLE_NAME;
            cursor = db.rawQuery(sql,null);
            cursor.moveToFirst();
            return cursor.getInt(0);
        } finally {
            if(cursor!=null){
                cursor.close();
            }
        }
    }

    public void deleteAppEntities(List<LocationEntity> appEntityLists){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(LocationColumns._ID);
        stringBuilder.append(" in (");
        for(int i=0;i<appEntityLists.size();i++){
            stringBuilder.append(appEntityLists.get(i).getId());
            if(i!=appEntityLists.size()-1){
                stringBuilder.append(",");
            }
        }
        stringBuilder.append(")");
        db.delete(LocationColumns.TABLE_NAME,stringBuilder.toString(),null);
    }

    public void updateAppEntities(List<LocationEntity> appEntityLists){
        db.beginTransaction();
        try {
            for(int i=0;i<appEntityLists.size();i++){
                LocationEntity appEntity = appEntityLists.get(i);
                ContentValues values = buildContentValues(appEntity);
                int affectedNumber = db.update(LocationColumns.TABLE_NAME,values, null,null);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private ContentValues buildContentValues(LocationEntity appEntity){
        ContentValues values = new ContentValues();
        values.put(LocationColumns.PKG_NAME, appEntity.getPkgName());
        values.put(LocationColumns.SORT, appEntity.getSort());
        values.put(LocationColumns.UPDATE_TIME, System.currentTimeMillis());
        return values;
    }

}
