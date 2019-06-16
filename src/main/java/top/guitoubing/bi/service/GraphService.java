package top.guitoubing.bi.service;

import javafx.util.Pair;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import top.guitoubing.bi.entity.NodeEntity;
import top.guitoubing.bi.entity.RelationEntity;
import top.guitoubing.bi.util.ConstantDefinition;
import top.guitoubing.bi.util.Neo4jDriverInitialize;
import top.guitoubing.bi.util.NodeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GraphService {

    private Neo4jDriverInitialize neo4jDriverInitialize = new Neo4jDriverInitialize();
    private Session session;

    private Session getSession(){
        if (session == null || !session.isOpen()){
            session = neo4jDriverInitialize.getSession();
        }
        return session;
    }

    /**
     * 通过一个实体查询其关联的所有关系和实体(限定跳数和结果数量)
     * @param type 输入的实体种类
     * @param step 限定的跳数
     * @param id 实体ID
     * @param limit 限定结果数量
     * @return 查询的节点和关系集合
     */
    public HashMap<String, ArrayList<NodeEntity>> searchByTypeAndId(int type, int step, int limit, int id){
        // 获取节点种类及其已定的查询字段
        Pair<String, String> nodeType = NodeUtils.getTypeFromKey(type);
        // 查询语句(使用parameters拼接字段会执行失败，原因待查)
        String query = "MATCH p=((n:"+nodeType.getKey()+")-[*"+step+"]-()) where id(n)="+id+" return p limit " + limit;
        return query(query);
    }

    /**
     * 通过一条语句查询并返回结果集（要求用p表示path，且结果只返回p）
     * @param query 语句
     * @return 结果集
     */
    public HashMap<String, ArrayList<NodeEntity>> query(String query){
        // 获取结果
        StatementResult result = getSession().run(query);
        System.out.println(query);
        HashMap<String,ArrayList<NodeEntity>> hashMap = new HashMap<>();
        // 节点集合
        ArrayList<NodeEntity> nodeList = new ArrayList<>();
        // 关系集合
        ArrayList<NodeEntity> relationList = new ArrayList<>();
        while (result.hasNext()){
            Record record = result.next();
            // 一个path相当于一条结果，这“一条结果”就是在查询语句中定义的'p'对应的表达式
            Path path = record.get("p").asPath();
            // 遍历节点添加
            Iterable<Node> nodes = path.nodes();
            for (Node node: nodes){
                NodeEntity nodeEntity = nodeToEntity(node);
                if (!nodeList.contains(nodeEntity))
                    nodeList.add(nodeEntity);
            }
            // 遍历关系添加
            Iterable<Relationship> relationships = path.relationships();
            for (Relationship relationship : relationships){
                RelationEntity relationEntity = relationToEntity(relationship);
                if (!relationList.contains(relationEntity))
                    relationList.add(relationEntity);
            }
        }
        hashMap.put(ConstantDefinition.NODES, nodeList);
        hashMap.put(ConstantDefinition.RELATIONS, relationList);
        if (session.isOpen())
            session.close();
//        System.err.println(hashMap);
        return hashMap;
    }


    public HashMap<String, ArrayList<NodeEntity>> searchMinPath(int source, int target, int sourceType, int targetType){
        String query = "MATCH (source:"+NodeUtils.getTypeFromKey(sourceType).getKey()+"),(target:"+NodeUtils.getTypeFromKey(targetType).getKey()+") WHERE id(source) = "
                + source +" AND id(target) = " + target +
                " MATCH p = shortestPath((source)-[*]-(target)) return p";
        return query(query);
    }

    public HashMap<String, ArrayList<NodeEntity>> searchAllMinPaths(int source, int target, int sourceType, int targetType){
        String query = "MATCH (source:"+NodeUtils.getTypeFromKey(sourceType).getKey()+"),(target:"+NodeUtils.getTypeFromKey(targetType).getKey()+") WHERE id(source) = "
                + source +" AND id(target) = " + target +
                " MATCH p = allShortestPaths((source)-[*]-(target)) return p";
        return query(query);
    }



    /**
     * 将数据库中Node节点转换为NodeEntity实体类型
     * @param node Node对象
     * @return NodeEntity对象
     */
    private NodeEntity nodeToEntity(Node node){
        NodeEntity nodeEntity = null;
        if (node != null){
            nodeEntity = new NodeEntity();
            nodeEntity.put("id", node.id());
            // 过滤节点标签
            nodeEntity.put("label", labelFilter(node.labels()));
            nodeEntity.putAll(node.asMap());
            return nodeEntity;
        }else {
            return null;
        }
    }

    /**
     * 将数据库中Relationship转换为RelationEntity实体类型
     * @param relationship Relationship对象
     * @return RelationEntity对象
     */
    private RelationEntity relationToEntity(Relationship relationship){
        RelationEntity relationEntity = null;
        if (relationship != null){
            relationEntity = new RelationEntity();
//            relationEntity.put("id", relationship.id());
            relationEntity.put("id", new Random().nextInt());
            relationEntity.put("source", relationship.startNodeId());
            relationEntity.put("target", relationship.endNodeId());
            relationEntity.put("label", relationship.type());
            relationEntity.putAll(relationship.asMap());
            return relationEntity;
        }else {
            return null;
        }
    }


    /**
     * 由于存在节点标签不唯一的情况，我们对节点标签进行了过滤，使其始终显示唯一一个标签；过滤遵循一个优先级规则：
     * 优先级顺序为Resource < ns3__BusinessClassification < 其他。
     * 即优先选择其他标签，当不存在其他标签时，再选择ns3__BusinessClassification，最后考虑Resource
     * @param ilabels 标签集合
     * @return 集合过滤后所剩的唯一标签
     */
    public String labelFilter(Iterable<String> ilabels){
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

    /**
     * 通过两个实体查询两个实体间可能存在的所有多跳关系(限定跳数和结果数量)
     * @param sourceType 起始节点类型
     * @param targetType 目标节点类型
     * @param step 跳数
     * @param limit 结果数量限制
     * @param sourceId 起始节点id
     * @param targetId 目标节点id
     * @return 结果集
     */
    public HashMap<String, ArrayList<NodeEntity>> searchByTwoNodes(int sourceType, int targetType, int step, int limit, int sourceId, int targetId){
        String sourceLabel = NodeUtils.getTypeFromKey(sourceType).getKey();
        String query = "match p=((n:"+sourceLabel+")-[*"+step+"]-()) where id(n)="+sourceId+" return p limit "+limit;
        System.out.println(query);
        StatementResult statementResult = getSession().run(query);
        HashMap<String, ArrayList<NodeEntity>> hashMap = new HashMap<>();
        ArrayList<NodeEntity> nodeList = new ArrayList<>();
        ArrayList<NodeEntity> relationList = new ArrayList<>();
        int pathCount = 0;
        int validCount = 0;
        while (statementResult.hasNext()){
            pathCount++;
            Record record = statementResult.next();
            Path path = record.get("p").asPath();
            if (containTarget(path, targetId)){
                validCount++;
                filterPath(path, nodeList, relationList, targetId);
            }
        }
        System.out.println("路径数量：" + pathCount);
        System.out.println("有效路径数量：" + validCount);
        hashMap.put(ConstantDefinition.NODES, nodeList);
        hashMap.put(ConstantDefinition.RELATIONS, deWeightRelation(relationList));
        if (session.isOpen())
            session.close();
        return hashMap;
    }

    private ArrayList<NodeEntity> deWeightRelation(ArrayList<NodeEntity> relationEntities){
        ArrayList<NodeEntity> newRelations = new ArrayList<>();
        for (NodeEntity relationEntity : relationEntities){
            if (!newRelations.contains(relationEntity)){
                newRelations.add(relationEntity);
            }
        }
        return newRelations;
    }

    /**
     * 过滤路径，目的有2：
     *  1. 找到存在目标节点的路径并将路径截断
     *  2. 对于上述找到的路径进行非实体节点过滤，定义了一个非实体节点集合，包含非实体节点a的关系()-[]-(a)-[]-()被认为是单跳关系而非多跳关系
     * @param path 路径
     * @param nodeList 返回给前端的节点集合
     * @param relationList 返回给前端的关系集合
     * @param targetId 目标节点ID
     */
    private void filterPath(Path path, ArrayList<NodeEntity> nodeList, ArrayList<NodeEntity> relationList, int targetId){
        // 获取路径上节点和关系的迭代器，并将其全部存入ArrayList中
        Iterable<Node> nodes = path.nodes();
        Iterable<Relationship> relationships = path.relationships();
        ArrayList<Node> nodeArrayList = new ArrayList<>();
        ArrayList<Relationship> relationshipArrayList = new ArrayList<>();
        nodes.forEach(nodeArrayList::add);
        relationships.forEach(relationshipArrayList::add);
        // 游标
        int flag = 0;
        // 先添加头节点
        NodeEntity first = nodeToEntity(nodeArrayList.get(0));
        if (!nodeList.contains(first))
            nodeList.add(nodeToEntity(nodeArrayList.get(0)));
        // 每次循环都会涉及到一个节点和其关联的两个关系
        while (flag < nodeArrayList.size()){
            // 首节点必然是起始节点，忽略，直接从第二个节点开始迭代
            Node node = nodeArrayList.get(flag+1);
            // 第二个节点的左边关系
            Relationship relationship = relationshipArrayList.get(flag);
            // 如果当前节点是目标节点，循环可以终止了，将当前节点和其左侧关系放入结果集中
            if (node.id() == targetId){
                NodeEntity nodeEntity = nodeToEntity(node);
                if (!nodeList.contains(nodeEntity))
                    nodeList.add(nodeEntity);
                addOrModifyRelationEntity(node, nodeArrayList.get(flag), node, relationList, relationship);
                break;
            }
            // isPropertyNode方法用于判断当前节点是不是非实体节点（即属性节点），若是则会将该节点转化成关系实体"relationEntity"
            // 这里有个假设，就是若当前节点是非实体节点，那么它肯定不是路径重点，且这个假设必然是成立的，因此这里没有做判断，直接传入flag+2参数
            boolean isPropertyNode = isPropertyNode(node, nodeArrayList.get(flag), nodeArrayList.get(flag+2), relationList, relationship);
            // 若是，则将该节点左右关系都删除（在上述假设的基础上，其左右关系必然存在），并将"relationEntity"放入结果集
            if (isPropertyNode){
                relationshipArrayList.set(flag, null);
                nodeArrayList.set(flag+1, null);
            }
            // 若不是，该节点需要被加入到结果集中，且其左右关系也需要加入到结果集
            else {
                NodeEntity nodeEntity = nodeToEntity(node);
                if (!nodeList.contains(nodeEntity)){
                    nodeList.add(nodeEntity);
                }
                if (nodeArrayList.get(flag) != null){
                    RelationEntity r = relationToEntity(relationship);
                    if (r != null && !relationList.contains(r))
                        relationList.add(r);
                }
            }
            flag++;
        }
    }

//    private Node getPrevValidNode(ArrayList<Node> nodes, int index){
//        for (int i = index - 1; i >= 0; i--){
//            if (nodes.get(i) != null)
//                return nodes.get(i);
//        }
//        return nodes.get(0);
//    }

    /**
     * 用于判断当前节点是不是非实体节点（即属性节点），若是则会将该节点转化成关系实体，关系的起始和终止节点即此节点的前第一个有效节点和后节点
     * 这里有个假设，就是若当前节点是非实体节点，那么它肯定不是路径重点，且这个假设必然是成立的
     * @param node 需要判断的节点
     * @param source 起始节点ID
     * @param target 目标节点ID
     * @return 转换后的关系实体
     */
    private boolean isPropertyNode(Node node, Node source, Node target, ArrayList<NodeEntity> relationEntities, Relationship relationship){
        String label = labelFilter(node.labels());
        if (NodeUtils.getPropertyNodes().contains(label)){
            addOrModifyRelationEntity(node, source, target, relationEntities, relationship);
            return true;
        }
        return false;
    }

    private void addOrModifyRelationEntity(Node node, Node source, Node target, ArrayList<NodeEntity> relationEntities, Relationship relationship){
        RelationEntity relationEntity;
        if (source == null){
            relationEntity = (RelationEntity) relationEntities.get(relationEntities.size() - 1);
            relationEntity.put("target", target.id());
//            Map<String, Object> nodeMap = new HashMap<>(node.asMap());
//            nodeMap.put("id", node.id());
//            nodeMap.put("label", labelFilter(node.labels()));
//            Map<String, Object> relationMap = new HashMap<>(relationship.asMap());
//            relationMap.put("id", relationship.id());
//            relationMap.put("start", relationship.startNodeId());
//            relationMap.put("end", relationship.endNodeId());
//            relationMap.put("type", relationship.type());
//            nodeMap.put("relation", relationMap);
//            relationEntity.put(String.valueOf(node.id()), nodeMap);
        }else {
            relationEntity = new RelationEntity();
            relationEntity.getProperties().put("id", new Random().nextInt());
            relationEntity.getProperties().put("source", source.id());
            relationEntity.getProperties().put("target", target.id());
//            relationEntity.getProperties().put(String.valueOf(node.id()), node.asMap());
            relationEntities.add(relationEntity);
        }
    }

    /**
     * 判断路径path上是否存在目标节点
     * @param path 路径
     * @param targetId 目标节点ID
     * @return 是否存在
     */
    private boolean containTarget(Path path, int targetId){
        Iterable<Node> nodes = path.nodes();
        for (Node node : nodes){
            if (node.id() == targetId){
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args){
        String query = "match p=((a:ns6__Currency)-[]-()) where id(a)=14066496 return p";
        System.out.println(new GraphService().query(query));
    }
}
