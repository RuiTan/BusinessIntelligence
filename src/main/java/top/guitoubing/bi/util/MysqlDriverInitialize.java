package top.guitoubing.bi.util;

import top.guitoubing.bi.entity.CacheEntity;

import java.sql.*;
import java.util.ArrayList;

public class MysqlDriverInitialize {
    private static Connection connection;

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        if (connection == null || connection.isClosed()){
            Class.forName(ConstantDefinition.mysqlDriver);
            connection = DriverManager.getConnection(ConstantDefinition.mysqlUrl, ConstantDefinition.mysqlUser, ConstantDefinition.mysqlPassword);
        }
        return connection;
    }

    public static ArrayList<CacheEntity> selectByTypeAndName(int type, String name) throws SQLException, ClassNotFoundException {
        System.out.println("开始从Mysql中查[type:" + type + ",name:" + name + "]");
        String sql = "select * from NeoCache where _label=? and _name like ? limit 50";
        PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
        preparedStatement.setString(1, NodeUtils.getTypeFromKey(type).getKey());
        preparedStatement.setString(2, name+"%");
        ResultSet resultSet = preparedStatement.executeQuery();
        System.out.println("从Mysql中query结束");
        ArrayList<CacheEntity> cacheEntities = new ArrayList<>();
        while (resultSet.next()){
            cacheEntities.add(new CacheEntity(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4)));
        }
        preparedStatement.close();
        System.out.println("结束从Mysql中查[type:" + type + ",name:" + name + "]");
        if (!connection.isClosed())
            connection.close();
        return cacheEntities;
    }
}
