package dotc.android.happybuy.push;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjun on 15/12/21.
 */
public class MessageManager {

    private static MessageManager mInstance;
    private Context mContext;
//    private MessageEntityDao mMessageEntityDao;
    private List<MessageMonitorListener> mMessageMonitorListeners;

//    private MessageManager(Context context) {
//        mContext = context.getApplicationContext();
//        DaoSession daoSeesion = ToolboxDaoProxy.getInstance(context).getDaoSession();
//        if (daoSeesion != null) {
//            mMessageEntityDao = daoSeesion.getMessageEntityDao();
//        }
//        mMessageMonitorListeners = new ArrayList<>();
//    }
//
//    public static MessageManager getInstance(Context context) {
//        if (mInstance == null) {
//            mInstance = new MessageManager(context);
//        }
//        return mInstance;
//    }
//
//    public long addMessage(MessageEntity messageEntity) {
//        long id = 0;
//        if (mMessageEntityDao != null) {
//            id = mMessageEntityDao.insert(messageEntity);
//        }
//        dispatchChangeToListener();
//        return id;
//    }
//
//    public void updateMessage(MessageEntity messageEntity) {
//        if (mMessageEntityDao != null) {
//            mMessageEntityDao.update(messageEntity);
//        }
//        dispatchChangeToListener();
//    }
//
//    public int queryUnreadMessageCount() {
//        int size = 0;
//        if (mMessageEntityDao != null) {
//            List<MessageEntity> list = mMessageEntityDao.queryBuilder()
//                    .where(MessageEntityDao.Properties.IsRead.eq(false), MessageEntityDao.Properties.Source.eq(PushConstance.getTopicGlobal())).list();
//            size = list.size();
//        }
//
//        return size;
//    }
//
//    public List<MessageEntity> queryMessage() {
//        List<MessageEntity> list = null;
//        if (mMessageEntityDao != null) {
//            list = mMessageEntityDao.queryBuilder()
//                    .where(MessageEntityDao.Properties.Source.eq(PushConstance.getTopicGlobal()))
//                    .orderAsc(MessageEntityDao.Properties.CreateTime).list();
//        }
//        return list;
//    }
//
//    public MessageEntity getMessage(long id) {
//        MessageEntity entity = null;
//        if (mMessageEntityDao != null) {
//            entity = mMessageEntityDao.load(id);
//        }
//        return entity;
//    }

    public boolean isInvalidMessage(String messageid) {
//        if (mMessageEntityDao != null && messageid != null) {
//            List<MessageEntity> list = mMessageEntityDao.queryBuilder().where(MessageEntityDao.Properties.MessageId.eq(messageid)).list();
//            return list.size() > 0;
//        }
        return false;
    }

    private void dispatchChangeToListener() {
        for (MessageMonitorListener listener : mMessageMonitorListeners) {
            listener.onMessageChanged();
        }
    }

    public void registerMessageMonitor(MessageMonitorListener listener) {
        if (!mMessageMonitorListeners.contains(listener)) {
            mMessageMonitorListeners.add(listener);
        }
    }

    public void unregisterMessageMonitor(MessageMonitorListener listener) {
        if (mMessageMonitorListeners.contains(listener)) {
            mMessageMonitorListeners.remove(listener);
        }
    }

    public interface MessageMonitorListener {
        void onMessageChanged();
    }


}
