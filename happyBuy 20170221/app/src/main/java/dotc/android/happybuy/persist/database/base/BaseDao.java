package dotc.android.happybuy.persist.database.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by wangjun on 16/2/26.
 */
public class BaseDao {

    protected String TAG = this.getClass().getSimpleName();
    protected SQLiteDatabase db;
    protected Context context;

    public BaseDao(Context context,SQLiteDatabase db){
        this.context = context;
        this.db = db;
    }



}
