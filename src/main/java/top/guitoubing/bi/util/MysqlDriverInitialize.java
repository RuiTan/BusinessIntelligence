package top.guitoubing.bi.util;

import sun.misc.Cache;
import top.guitoubing.bi.entity.CacheEntity;

import java.sql.*;
import java.util.ArrayList;

public class MysqlDriverInitialize {
    private static Connection connection;

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        if (connection == null){
            Class.forName(ConstantDefinition.mysqlDriver);
            connection = DriverManager.getConnection(ConstantDefinition.mysqlUrl, ConstantDefinition.mysqlUser, ConstantDefinition.mysqlPassword);
        }
        return connection;
    }

    public static ArrayList<CacheEntity> selectByTypeAndName(int type, String name) throws SQLException, ClassNotFoundException {
        String sql = "select * from NeoCache where _label like ? and _name like ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
        preparedStatement.setString(1, "%"+NodeUtils.getTypeFromKey(type).getKey()+"%");
        preparedStatement.setString(2, "%"+name+"%");
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<CacheEntity> cacheEntities = new ArrayList<>();
        while (resultSet.next()){
            cacheEntities.add(new CacheEntity(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4)));
        }
        preparedStatement.close();
        return cacheEntities;
    }
}
