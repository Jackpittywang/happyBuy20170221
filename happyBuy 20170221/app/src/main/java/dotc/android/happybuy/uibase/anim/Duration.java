package dotc.android.happybuy.uibase.anim;

/**
 * Created by wangjun on 16/9/27.
 */
public class Duration {

    public static long UNIT = 1000;
    public static int FACTOR = 1;
    public static long FRAME = 40*FACTOR;//40

    public static long value(float value){
        return (long) (value*UNIT);
    }

    public static long value(int value){
        return value*UNIT;
    }

    public static long ofTime(int frameCount){
        return FRAME*frameCount;
    }

}
