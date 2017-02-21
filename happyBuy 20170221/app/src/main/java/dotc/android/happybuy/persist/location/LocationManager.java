package dotc.android.happybuy.persist.location;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.GlobalContext;

/**
 * Created by wangjun on 15-4-29.
 */
public class LocationManager {
    private final String TAG = this.getClass().getSimpleName();

    public static final String DB_NAME = "city_th.s3db";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + GlobalContext.get().getPackageName();
    private SQLiteDatabase database;
    private Context context;
    private File file = null;

    public LocationManager(Context context) {
        this.context = context;
    }

    public SQLiteDatabase openDatabase() {
        database = this.openDatabase(DB_PATH + "/" + DB_NAME);
        return database;
    }

//    public SQLiteDatabase getDatabase() {
//        return database;
//    }

    private SQLiteDatabase openDatabase(String dbfile) {
        file = new File(dbfile);
        if (file.exists()) {
            database = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
            return database;
        }
        return null;
    }

    public void closeDatabase() {
        if (this.database != null)
            this.database.close();
    }

    public List<Region> getProvince(){
        List<Region> provinces = new ArrayList<>();
        SQLiteDatabase database = openDatabase();
        Cursor cursor = null;
        try{
            String sql = "select province,province_th from t_location GROUP BY province order by province";
            cursor = database.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                provinces.add(new Region(cursor.getString(0),cursor.getString(1)));
            }
        } finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return provinces;
    }

    public List<Region> getDistrict(Region province){
        List<Region> districts = new ArrayList<>();
        SQLiteDatabase database = openDatabase();
        Cursor cursor = null;
        try{
            String sql = "select district,district_th from t_location where province='"+province.enValue+"' order by province";
            cursor = database.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                districts.add(new Region(cursor.getString(0),cursor.getString(1)));
            }
        } finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return districts;
    }

    public List<String> getPostcode(Region province,Region district){
        List<String> districts = new ArrayList<>();
        SQLiteDatabase database = openDatabase();
        Cursor cursor = null;
        try{
            String sql = "select postcode from t_location where province='"+province.enValue+"' and district='"+district.enValue+"' order by province";
            cursor = database.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                districts.add(cursor.getString(0));
            }
        } finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return districts;
    }

    public static class Region {
        public String enValue;
        public String thValue;

        public Region(String enValue, String thValue) {
            this.enValue = enValue;
            this.thValue = thValue;
        }

        public Region(String dataString) {
            String[] array = dataString.split("/");
            enValue = array[0];
            thValue = array[1];
        }

        public static Region newRegion(String dataString){
            String[] array = dataString.split("/");
            if (array.length == 2){
                return new Region(array[0],array[1]);
            }
            return null;
        }
    }

}
