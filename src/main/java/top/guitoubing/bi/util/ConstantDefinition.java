package top.guitoubing.bi.util;

public class ConstantDefinition {
    // 连接字符串
//    private static String url = "bolt://58.41.200.10:7687";
    private static String url = "bolt://192.168.1.105:7687";
    static final String username = "neo4j";
    static final String password = "tanrui";

    static final String mongoUser = "testAdmin";
    static final String mongoDatabase = "admin";
    static final String mongoPassword = "123456";
    static final String mongoIp = "47.101.148.55";
    static final int mongoPort = 27017;
    static final String mongoSingleCollection = "single";
    static final String mongoDoubleCollection = "double";
    static final String mongoMinPathCollection = "minpath";
    static final String mongoAllMinPathCollection = "minpaths";
    public static final int mongoSingleCollectionType = 1;
    public static final int mongoDoubleCollectionType = 2;
    public static final int mongoMinPathCollectionType = 3;
    public static final int mongoAllMinPathsCollectionType = 4;
    public static final int mongoMaxHistories = 50;

    static final String mysqlUrl = "jdbc:mysql://47.101.148.55:3306/BI?useSSL=false&serverTimezone=UTC";
    static final String mysqlUser = "root";
    static final String mysqlPassword = "123IsYourNewPassword!";
    static final String mysqlDriver = "com.mysql.cj.jdbc.Driver";

    public static final String NODES = "nodes";
    public static final String RELATIONS = "relations";

    public static final int MaxLimit = 5000;
    public static final int MinLimit = 10;
    public static final int MaxStep = 15;
    public static final int MinStep = 2;

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String ip) {
        url = "bolt://" + ip + ":7687";
    }
}
