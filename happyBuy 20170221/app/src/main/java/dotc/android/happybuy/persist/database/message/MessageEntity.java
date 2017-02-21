package dotc.android.happybuy.persist.database.message;

import android.database.Cursor;

import dotc.android.happybuy.persist.database.award.AwardColumns;

/**
 * Created by wangjun on 16/2/26.
 */
public class MessageEntity {

    public final static int NO = 0x00;
    public final static int YES = 0x01;

    private Long id;
    private String uid;
    private String messageId;
    private String messageSource;

    private String icon;
    private String title;
    private String message;
    private String displayTitle;
    private String displayMessage;
    private int isRead;
    private int isScan;
    private Integer type;
    private String data;

    private long createTime;
    private long updateTime;

    public MessageEntity() {}

    public MessageEntity(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(MessageColumns._ID));
        uid = cursor.getString(cursor.getColumnIndex(MessageColumns.UID));
        messageId = cursor.getString(cursor.getColumnIndex(MessageColumns.MESSAGE_ID));
        messageSource = cursor.getString(cursor.getColumnIndex(MessageColumns.MESSAGE_SOURCE));
        icon = cursor.getString(cursor.getColumnIndex(MessageColumns.ICON));
        title = cursor.getString(cursor.getColumnIndex(MessageColumns.TITLE));
        message = cursor.getString(cursor.getColumnIndex(MessageColumns.MESSAGE));
        displayTitle = cursor.getString(cursor.getColumnIndex(MessageColumns.DISPLAY_TITLE));
        displayMessage = cursor.getString(cursor.getColumnIndex(MessageColumns.DISPLAY_MESSAGE));
        isRead = cursor.getInt(cursor.getColumnIndex(MessageColumns.IS_READ));
        isScan = cursor.getInt(cursor.getColumnIndex(MessageColumns.IS_SCAN));
        type = cursor.getInt(cursor.getColumnIndex(MessageColumns.TYPE));
        data = cursor.getString(cursor.getColumnIndex(MessageColumns.DATA));
        createTime = cursor.getLong(cursor.getColumnIndex(MessageColumns.CREATE_TIME));
        updateTime = cursor.getLong(cursor.getColumnIndex(MessageColumns.UPDATE_TIME));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(String messageSource) {
        this.messageSource = messageSource;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDisplayTitle() {
        return displayTitle;
    }

    public void setDisplayTitle(String displayTitle) {
        this.displayTitle = displayTitle;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public boolean isRead() {
        return isRead==YES;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead?YES:NO;
    }

    public boolean getIsScan() {
        return isScan==YES;
    }

    public void setIsScan(boolean isScan) {
        this.isScan = isScan?YES:NO;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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
