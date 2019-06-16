package top.guitoubing.bi.util;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

public class Neo4jDriverInitialize {

    // neo4j连接的驱动Driver和会话Session
    private Driver driver;
    private Session session;

    // 获取driver
    private Driver getDriver(){
        driver = GraphDatabase.driver(ConstantDefinition.getUrl(), AuthTokens.basic(ConstantDefinition.username, ConstantDefinition.password));
        return driver;
    }

    // 关闭会话和驱动(一次连接)
    public void closeDriverAndSession(){
        session.close();
        driver.close();
    }

    // 获取会话session
    public Session getSession(){
        if (session == null || !session.isOpen())
            session = getDriver().session();
        return session;
    }

}
