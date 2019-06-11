package top.guitoubing.bi.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Neo4j中的节点类型，可序列化
 */
public class NodeEntity implements Serializable {
    public HashMap<String, Object> properties;

    public NodeEntity() {
        properties = new HashMap<>();
    }

    public NodeEntity(HashMap<String, Object> properties) {
        this.properties = properties;
    }

    public HashMap<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, Object> properties) {
        this.properties = properties;
    }

    public Object get(String key){
        return properties.get(key);
    }

    public void put(String key, Object o){
        properties.put(key, o);
    }

    public void putAll(Map<String, Object> map){
        properties.putAll(map);
    }

    @Override
    public String toString(){
        return getProperties().toString();
    }

    /**
     * NodeEntity的相等需要Node的id相等即可，用于在List中添加时过滤相同节点
     * @param object 比较对象
     * @return 是否相等
     */
    @Override
    public boolean equals(Object object){
        if (object == null)
            return false;
        else if (!(object instanceof NodeEntity))
            return false;
        return getProperties().get("id").equals(((NodeEntity) object).getProperties().get("id"));
    }
}
