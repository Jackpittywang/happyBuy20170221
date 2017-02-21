package dotc.android.happybuy.persist.database.location;

import android.provider.BaseColumns;

public class LocationColumns implements BaseColumns {

	public static final String TABLE_NAME = "tb_apps";

	public static final String PKG_NAME = "pkg_name";
//	public static final String TITLE = "title";
	public static final String SORT = "sort";
	public static final String CREATE_TIME = "create_time";
	public static final String UPDATE_TIME = "update_time";
	public static final String KEY = "key";
//	public static final String DATA0 = "data0";
//	public static final String DATA1 = "data1";
//	public static final String DATA2 = "data2";

	public static final String CREATE_SQL =
			"CREATE TABLE IF NOT EXISTS " + LocationColumns.TABLE_NAME
					+" ( _id INTEGER PRIMARY KEY, "
					+ LocationColumns.PKG_NAME + " TEXT NOT NULL ,"
					+ LocationColumns.SORT + "  INTEGER default 0 ,"
					+ LocationColumns.CREATE_TIME + " long ,"
					+ LocationColumns.UPDATE_TIME + " long ,"
					+ LocationColumns.KEY + " TEXT"
					+");";

	public static final String[] ALL_COLUMNS = new String[]{
			_ID,PKG_NAME,SORT,CREATE_TIME,UPDATE_TIME,KEY,
	};

}
