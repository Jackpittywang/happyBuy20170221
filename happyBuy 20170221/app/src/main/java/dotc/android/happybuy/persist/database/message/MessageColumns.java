package dotc.android.happybuy.persist.database.message;

import android.provider.BaseColumns;

public class MessageColumns implements BaseColumns {

	public static final String TABLE_NAME = "tb_messages";

	public static final String UID = "uid";
	public static final String MESSAGE_ID = "message_id";
	public static final String MESSAGE_SOURCE = "message_source";
	public static final String ICON = "icon";
	public static final String TITLE = "title";
	public static final String MESSAGE = "message";
	public static final String DISPLAY_TITLE = "display_title";
	public static final String DISPLAY_MESSAGE = "display_message";
	public static final String TYPE = "type";//0  ,1
	public static final String DATA = "data";
	public static final String IS_READ = "is_read";//0 no,1 yes
	public static final String IS_SCAN = "is_scan";//0 no,1 yes
	public static final String CREATE_TIME = "create_time";
	public static final String UPDATE_TIME = "update_time";

	public static final String CREATE_SQL =
			"CREATE TABLE IF NOT EXISTS " + MessageColumns.TABLE_NAME
					+" ( _id INTEGER PRIMARY KEY, "
					+ MessageColumns.UID + " TEXT NOT NULL ,"
					+ MessageColumns.MESSAGE_ID + " TEXT NOT NULL ,"
					+ MessageColumns.MESSAGE_SOURCE + " TEXT NOT NULL ,"
					+ MessageColumns.ICON + " TEXT NOT NULL ,"
					+ MessageColumns.TITLE + " TEXT NOT NULL ,"
					+ MessageColumns.MESSAGE + " TEXT NOT NULL ,"
					+ MessageColumns.DISPLAY_TITLE + " TEXT NOT NULL ,"
					+ MessageColumns.DISPLAY_MESSAGE + " TEXT NOT NULL ,"
					+ MessageColumns.TYPE + "  INTEGER default 0 ,"
					+ MessageColumns.DATA + " TEXT NOT NULL ,"
					+ MessageColumns.IS_READ + "  INTEGER default 0 ,"
					+ MessageColumns.IS_SCAN + "  INTEGER default 0 ,"
					+ MessageColumns.CREATE_TIME + " long ,"
					+ MessageColumns.UPDATE_TIME + " long "
					+");";

	public static final String[] ALL_COLUMNS = new String[]{
			_ID,UID,MESSAGE_ID,MESSAGE_SOURCE,ICON,TITLE,MESSAGE,DISPLAY_TITLE,DISPLAY_MESSAGE,
			TYPE,DATA,IS_READ,IS_SCAN,CREATE_TIME,UPDATE_TIME
	};

}
