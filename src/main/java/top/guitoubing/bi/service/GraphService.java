package top.guitoubing.bi.service;

import javafx.util.Pair;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import top.guitoubing.bi.util.ConstantDefinition;
import top.guitoubing.bi.util.DriverInitialize;
import top.guitoubing.bi.util.NodeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.neo4j.driver.v1.Values.parameters;

public class GraphService {

    private DriverInitialize driverInitialize = new DriverInitialize();
    private Session session = driverInitialize.getSession();


    /**
     * 通过一个实体查询其关联的所有关系和实体(限定跳数和结果数量)
     * @param type 输入的实体种类
     * @param step 限定的跳数
     * @param text 实体名称
     * @param limit 限定结果数量
     * @return 查询的节点和关系集合
     */
    public HashMap<String, ArrayList<HashMap<String, Object>>> searchByTypeAndSearchingText(int type, int step, int limit, String text){
        // 获取节点种类及其已定的查询字段
        Pair<String, String> nodeType = NodeUtils.getTypeFromKey(type);
        // 查询语句(使用parameters拼接字段会执行失败，原因待查)
        String query = "MATCH p=((n:"+nodeType.getKey()+")-[*.."+step+"]-()) where n.ns1__hasName='"+text+"' return p limit " + (limit >= ConstantDefinition.NODESLIMIT ? ConstantDefinition.NODESLIMIT:limit);
        // 获取结果
        StatementResult result = session.run(query);
        System.out.println(query);
        HashMap<String,ArrayList<HashMap<String,Object>>> hashMap = new HashMap<>();
        // 节点集合
        ArrayList<HashMap<String, Object>> nodeList = new ArrayList<>();
        // 关系集合
        ArrayList<HashMap<String, Object>> relationList = new ArrayList<>();
        while (result.hasNext()){
            Record record = result.next();
            // 一个path相当于一条结果，这“一条结果”就是在查询语句中定义的'p'对应的表达式
            Path path = record.get("p").asPath();
            // 遍历节点添加
            Iterable<Node> nodes = path.nodes();
            for (Node node: nodes){
                HashMap<String, Object> nodeMap = new HashMap<>();
                nodeMap.put("id", node.id());
                // 过滤节点标签
                nodeMap.put("label", labelFilter(node.labels()));
                nodeMap.putAll(node.asMap());
                nodeList.add(nodeMap);
            }
            // 遍历关系添加
            Iterable<Relationship> relationships = path.relationships();
            for (Relationship relationship : relationships){
                HashMap<String, Object> relationMap = new HashMap<>();
                relationMap.put("source", relationship.startNodeId());
                relationMap.put("target", relationship.endNodeId());
                relationMap.put("label", relationship.type());
                relationMap.putAll(relationship.asMap());
                relationList.add(relationMap);
            }
        }
        hashMap.put(ConstantDefinition.NODES, nodeList);
        hashMap.put(ConstantDefinition.RELATIONS, relationList);
//        System.err.println(hashMap);
        return hashMap;
    }


    /**
     * 由于存在节点标签不唯一的情况，我们对节点标签进行了过滤，使其始终显示唯一一个标签；过滤遵循一个优先级规则：
     * 优先级顺序为Resource < ns3__BusinessClassification < 其他。
     * 即优先选择其他标签，当不存在其他标签时，再选择ns3__BusinessClassification，最后考虑Resource
     * @param ilabels 标签集合
     * @return 集合过滤后所剩的唯一标签
     */
    private String labelFilter(Iterable<String> ilabels){
        ArrayList<String> labels = new ArrayList<>();
        for (String label : ilabels){
            labels.add(label);
        }
        if (labels.size() == 1)
            return labels.get(0);
        else if (labels.size() == 2)
            return labels.get(0).equals("Resource") ? labels.get(1) : labels.get(0);
        else {
            for (String label : labels){
                if (!label.equals("Resource") && !label.equals("ns3__BusinessClassification"))
                    return label;
            }
            return "";
        }
    }

}
