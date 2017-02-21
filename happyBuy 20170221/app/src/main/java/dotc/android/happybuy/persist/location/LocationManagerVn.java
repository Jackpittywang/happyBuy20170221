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
public class LocationManagerVn {
    private final String TAG = this.getClass().getSimpleName();

    public static final String DB_NAME = "city_vn.s3db";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + GlobalContext.get().getPackageName();
    private SQLiteDatabase database;
    private Context context;
    private File file = null;

    public LocationManagerVn(Context context) {
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
            String sql = "select name,code from t_country_city where p_code='"+0+"'order by p_code";
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
            String sql = "select name,code from t_country_city where p_code='"+province.code+"' order by p_code";
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
            String sql = "select name from t_country_city where p_code='"+district.code+"' order by p_code";
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

    public Region newRegion(String dataString){
        Region region=null;
        SQLiteDatabase database = openDatabase();
        Cursor cursor = null;
        try{
            String sql = "select name,code from t_country_city where name='"+dataString+"' order by p_code";
            cursor = database.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                region=new Region(cursor.getString(0),cursor.getString(1));
            }
        } finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return region;

    }

    public static class Region {
        public String enValue;
        public String code;

        public Region(String enValue, String thValue) {
            this.enValue = enValue;
            this.code = thValue;
        }

        public Region(String dataString) {
            String[] array = dataString.split("/");
            enValue = array[0];
            code = array[1];
        }

        public static Region newRegion(String dataString){

                return new Region(dataString);
        }
    }

}
