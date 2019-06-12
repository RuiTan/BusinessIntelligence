package top.guitoubing.bi.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    public static String getCurrentTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }
}
