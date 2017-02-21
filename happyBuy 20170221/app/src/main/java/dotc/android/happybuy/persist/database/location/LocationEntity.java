package dotc.android.happybuy.persist.database.location;

import android.database.Cursor;

/**
 * Created by wangjun on 16/2/26.
 */
public class LocationEntity {

    private Long id;
    private String pkgName;
//    private String title;
    private int sort;
    private long createTime;
    private long updateTime;
    private String key;
//    private String data0;
//    private String data1;
//    private String data2;

    public LocationEntity() {}


    public LocationEntity(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(LocationColumns._ID));
        pkgName = cursor.getString(cursor.getColumnIndex(LocationColumns.PKG_NAME));
        sort = cursor.getInt(cursor.getColumnIndex(LocationColumns.SORT));
        createTime = cursor.getLong(cursor.getColumnIndex(LocationColumns.CREATE_TIME));
        updateTime = cursor.getLong(cursor.getColumnIndex(LocationColumns.UPDATE_TIME));
        key = cursor.getString(cursor.getColumnIndex(LocationColumns.KEY));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
