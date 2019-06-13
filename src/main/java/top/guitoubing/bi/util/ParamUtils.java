package top.guitoubing.bi.util;

public class ParamUtils {
    public static String paramsToString(Object... params){
        StringBuilder stringBuilder = new StringBuilder();
        for (Object param : params){
            stringBuilder.append(String.valueOf(param)).append("-");
        }
        return stringBuilder.toString().substring(0, stringBuilder.length()-1);
    }
}
