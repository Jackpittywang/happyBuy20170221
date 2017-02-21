package dotc.android.happybuy.persist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import dotc.android.happybuy.persist.database.award.AwardDao;
import dotc.android.happybuy.persist.database.base.MyDatabaseHelper;
import dotc.android.happybuy.persist.database.location.LocationDao;
import dotc.android.happybuy.persist.database.message.MessageDao;


/**
 */
public class DaoProxy {

    private Context mContext;
    private static DaoProxy mInstance;
    private LocationDao mAppDao;
    private AwardDao mAwardDao;
    private MessageDao mMessageDao;
    private MyDatabaseHelper mDatabaseHelper;
    public final SQLiteDatabase db;

    private DaoProxy(Context context) {
        this.mContext = context;
        mDatabaseHelper = new MyDatabaseHelper(context);
        db = mDatabaseHelper.getWritableDatabase();
        mAppDao = new LocationDao(context, db);
        mAwardDao = new AwardDao(context,db);
        mMessageDao = new MessageDao(context,db);
    }

    public static DaoProxy getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DaoProxy(context.getApplicationContext());
        }
        return mInstance;
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (mDatabaseHelper != null) {
            mDatabaseHelper.close();
        }
    }

    public AwardDao getAwardDao() {
        return mAwardDao;
    }
    public MessageDao getMessageDao() {
        return mMessageDao;
    }
    public LocationDao getAppDao() {
        return mAppDao;
    }
}
