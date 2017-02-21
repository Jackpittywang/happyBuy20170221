package dotc.android.happybuy.persist.database.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.persist.database.award.AwardColumns;
import dotc.android.happybuy.persist.database.location.LocationColumns;
import dotc.android.happybuy.persist.database.message.MessageColumns;

/**
 * Created by wangjun on 15/10/16.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private final String TAG = this.getClass().getSimpleName();

    private static final int VERSION = 3;

    public MyDatabaseHelper(Context context) {
        super(context, "gogobuy.db", null, VERSION);
    }

    public MyDatabaseHelper(Context context, String databaseName) {
        super(context, databaseName, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        HBLog.d(TAG + " onCreate ");
        db.execSQL(LocationColumns.CREATE_SQL);
        db.execSQL(AwardColumns.CREATE_SQL);
        db.execSQL(MessageColumns.CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        HBLog.d(TAG + " onUpgrade oldVersion:" + oldVersion + " newVersion:" + newVersion);
        db.execSQL("drop table if exists "+ LocationColumns.TABLE_NAME);
        db.execSQL("drop table if exists "+ AwardColumns.TABLE_NAME);
        db.execSQL("drop table if exists "+ MessageColumns.TABLE_NAME);
        onCreate(db);
    }


}
