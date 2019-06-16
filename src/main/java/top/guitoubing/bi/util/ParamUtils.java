package top.guitoubing.bi.util;

public class ParamUtils {
    public static String paramsToString(Object... params){
        StringBuilder stringBuilder = new StringBuilder();
        for (Object param : params){
            stringBuilder.append(String.valueOf(param)).append("-");
        }
        System.out.println();
        return stringBuilder.toString().substring(0, stringBuilder.length()-1);
    }

    public static int checkStep(int step){
        if (step <= ConstantDefinition.MinStep)
            return ConstantDefinition.MinStep;
        if (step >= ConstantDefinition.MaxStep)
            return ConstantDefinition.MaxStep;
        return step;
    }

    public static int checkLimit(int limit){
        if (limit <= ConstantDefinition.MinLimit)
            return ConstantDefinition.MinLimit;
        if (limit >= ConstantDefinition.MaxLimit)
            return ConstantDefinition.MaxLimit;
        return limit;
    }
}
