package dotc.android.happybuy.persist.database.award;

import android.database.Cursor;

import dotc.android.happybuy.persist.database.location.LocationColumns;

/**
 * Created by wangjun on 16/2/26.
 */
public class AwardEntity {

    private String uid;
    private String productId;
    private String productItemId;
    private int period;
    private int isShow;
    private String awardNumber;
    private long createTime;
    private long updateTime;


    public AwardEntity() {}

    public AwardEntity(Cursor cursor) {
        uid = cursor.getString(cursor.getColumnIndex(AwardColumns.UID));
        productId = cursor.getString(cursor.getColumnIndex(AwardColumns.PRODUCT_ID));
        productItemId = cursor.getString(cursor.getColumnIndex(AwardColumns.PRODUCT_ITEM_ID));
        period = cursor.getInt(cursor.getColumnIndex(AwardColumns.PERIOD));
        isShow = cursor.getInt(cursor.getColumnIndex(AwardColumns.SHOW));
        awardNumber = cursor.getString(cursor.getColumnIndex(AwardColumns.AWARD_NUMBER));
        createTime = cursor.getLong(cursor.getColumnIndex(AwardColumns.CREATE_TIME));
        updateTime = cursor.getLong(cursor.getColumnIndex(AwardColumns.UPDATE_TIME));
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductItemId() {
        return productItemId;
    }

    public void setProductItemId(String productItemId) {
        this.productItemId = productItemId;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getIsShow() {
        return isShow;
    }

    public void setIsShow(int isShow) {
        this.isShow = isShow;
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
}
