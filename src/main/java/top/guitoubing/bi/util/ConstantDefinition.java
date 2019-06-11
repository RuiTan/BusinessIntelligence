package top.guitoubing.bi.util;

public class ConstantDefinition {
    // 连接字符串
    private static String url = "bolt://58.41.200.10:7687";
    static final String username = "neo4j";
    static final String password = "tanrui";

    static final String mongoUser = "testAdmin";
    static final String mongoDatabase = "admin";
    static final String mongoPassword = "123456";
    static final String mongoIp = "47.101.148.55";
    static final int mongoPort = 27017;
    static final String mongoCollection = "record";

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
