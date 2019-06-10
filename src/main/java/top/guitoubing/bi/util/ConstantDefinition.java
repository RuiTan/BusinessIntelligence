package top.guitoubing.bi.util;

public class ConstantDefinition {
    // 连接字符串
    private static String url = "bolt://58.41.206.35:7687";
    static final String username = "neo4j";
    static final String password = "tanrui";

    public static final String NODES = "nodes";
    public static final String RELATIONS = "relations";

    public static final int NODESLIMIT = 5000;

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String ip) {
        url = "bolt://" + ip + ":7687";
    }
}
