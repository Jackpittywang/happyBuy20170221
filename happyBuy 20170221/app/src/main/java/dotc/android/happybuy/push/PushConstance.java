package dotc.android.happybuy.push;

/**
 */
public class PushConstance {

    public static boolean PUSH_DEBUG = false;

//    public static String TOPIC = "/topics/";//can't change
    public static String LONGLIVE_TOPIC_GLOBAL = "global";

    private static String SUB_GLOBAL = "global";//global:push to all user
    String ad = "{ \"data\": {\"score\": \"5x1\"},\"to\" : \"global\"}";

    public static String getSubGlobal(){
        return PushConstance.SUB_GLOBAL;
    }

    public static String getTopicGlobal(){
        return getSubGlobal();
    }



}
