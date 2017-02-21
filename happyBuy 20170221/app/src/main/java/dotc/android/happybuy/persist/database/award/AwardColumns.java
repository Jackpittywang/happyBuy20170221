package dotc.android.happybuy.persist.database.award;

import android.provider.BaseColumns;

public class AwardColumns implements BaseColumns {

	public static final String TABLE_NAME = "tb_awards";

	public static final String UID = "uid";
	public static final String PRODUCT_ID = "product_id";
	public static final String PRODUCT_ITEM_ID = "product_item_id";
	public static final String PERIOD = "period";
	public static final String SHOW = "is_show";//0 display ,1 undisplay
	public static final String AWARD_NUMBER = "award_number";
	public static final String CREATE_TIME = "create_time";
	public static final String UPDATE_TIME = "update_time";
//	public static final String DATA0 = "data0";
//	public static final String DATA1 = "data1";
//	public static final String DATA2 = "data2";

	public static final String CREATE_SQL =
			"CREATE TABLE IF NOT EXISTS " + AwardColumns.TABLE_NAME
					+" ( _id INTEGER PRIMARY KEY, "
					+ AwardColumns.UID + " TEXT NOT NULL ,"
					+ AwardColumns.PRODUCT_ID + " TEXT NOT NULL ,"
					+ AwardColumns.PRODUCT_ITEM_ID + " TEXT NOT NULL ,"
					+ AwardColumns.PERIOD + " INTEGER ,"
					+ AwardColumns.SHOW + "  INTEGER default 0 ,"
					+ AwardColumns.AWARD_NUMBER + " TEXT NOT NULL ,"
					+ AwardColumns.CREATE_TIME + " long ,"
					+ AwardColumns.UPDATE_TIME + " long "
					+");";

	public static final String[] ALL_COLUMNS = new String[]{
			_ID,PRODUCT_ID,PRODUCT_ITEM_ID,PERIOD,SHOW,CREATE_TIME,UPDATE_TIME
	};

}
