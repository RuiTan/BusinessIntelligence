package top.guitoubing.bi.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;
import top.guitoubing.bi.entity.CacheEntity;
import top.guitoubing.bi.entity.NodeEntity;
import top.guitoubing.bi.service.GraphService;
import top.guitoubing.bi.util.ConstantDefinition;
import top.guitoubing.bi.util.MongoDriverInitialize;
import top.guitoubing.bi.util.MysqlDriverInitialize;
import top.guitoubing.bi.util.ParamUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@CrossOrigin(maxAge = 3600, origins = "*")
public class IndexController {

    @RequestMapping(value = "index")
    @CrossOrigin(maxAge = 3600, origins = "*")
    public String index(){
        return "index";
    }

    /**
     * 通过mysql模糊匹配获取实体列表返回前端，供用户选择
     * @param type 实体种类
     * @param name 名称匹配
     * @return 查到的实体列表
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @RequestMapping("selectByTypeAndName")
    @ResponseBody
    @CrossOrigin(maxAge = 3600, origins = "*")
    public ArrayList<CacheEntity> selectByTypeAndName(@RequestParam("type") int type, @RequestParam("name") String name) throws SQLException, ClassNotFoundException {
        return MysqlDriverInitialize.selectByTypeAndName(type, name);
    }

    /**
     * 通过一个实体查询其关联的所有关系和实体(限定跳数和结果数量)
     * @param type 输入的实体种类
     * @param step 限定的跳数
     * @param id 实体ID
     * @param limit 限定结果数量
     * @return 结果集
     */
    @RequestMapping(value = "searchANode", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin(maxAge = 3600, origins = "*")
    public String searchANode(
            @RequestParam("type") int type, @RequestParam("step") int step, @RequestParam("id") int id, @RequestParam("limit") int limit){
        step = ParamUtils.checkStep(step);
        limit = ParamUtils.checkLimit(limit);
        String param = ParamUtils.paramsToString(type, step, id, limit);
        String results = MongoDriverInitialize.findOne(param, ConstantDefinition.mongoSingleCollectionType);
        if (results == null){
            HashMap<String, ArrayList<NodeEntity>> hashMap = new GraphService().searchByTypeAndId(type, step, limit, id);
            MongoDriverInitialize.addOne(param, hashMap, ConstantDefinition.mongoSingleCollectionType);
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(hashMap);
            results = jsonObject.toJSONString();
        }
        return results;
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
    @RequestMapping(value = "searchByTwoNodes", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin(maxAge = 3600, origins = "*")
    public String searchByTwoNodes(
            @RequestParam("sourceType") int sourceType, @RequestParam("targetType") int targetType, @RequestParam("step") int step,
            @RequestParam("limit") int limit, @RequestParam("sourceId") int sourceId, @RequestParam("targetId") int targetId){
        step = ParamUtils.checkStep(step);
        limit = ParamUtils.checkLimit(limit);
        String param = ParamUtils.paramsToString(sourceType, targetType, step, limit, sourceId, targetId);
        String results = MongoDriverInitialize.findOne(param, ConstantDefinition.mongoDoubleCollectionType);
        if (results == null){
            HashMap<String, ArrayList<NodeEntity>> hashMap = new GraphService().searchByTwoNodes(sourceType, targetType, step, limit, sourceId, targetId);
            MongoDriverInitialize.addOne(param, hashMap, ConstantDefinition.mongoDoubleCollectionType);
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(hashMap);
            results = jsonObject.toJSONString();
        }
        return results;
    }

    /**
     * 通过给定两个实体，返回两节点间最小路径
     * @param source 起始点
     * @param target 目标点
     * @param sourceType 起始节点种类
     * @param targetType 目标节点种类
     * @return 返回一条路径
     */
    @RequestMapping(value = "searchMinPath", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin(maxAge = 3600, origins = "*")
    public String searchMinPath(@RequestParam("source") int source, @RequestParam("target") int target,
                                                                @RequestParam("sourceType") int sourceType, @RequestParam("targetType") int targetType){
        String param = ParamUtils.paramsToString(source, target, sourceType, targetType);
        String results = MongoDriverInitialize.findOne(param,ConstantDefinition.mongoMinPathCollectionType);
        if (results == null){
            HashMap<String, ArrayList<NodeEntity>> hashMap = new GraphService().searchMinPath(source, target, sourceType, targetType);
            MongoDriverInitialize.addOne(param, hashMap, ConstantDefinition.mongoMinPathCollectionType);
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(hashMap);
            results = jsonObject.toJSONString();
        }
        return results;
    }

    @RequestMapping(value = "searchAllMinPaths", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin(maxAge = 3600, origins = "*")
    public String searchAllMinPaths(@RequestParam("source") int source, @RequestParam("target") int target,
                                    @RequestParam("sourceType") int sourceType, @RequestParam("targetType") int targetType) {
        String param = ParamUtils.paramsToString(source, target, sourceType, targetType);
        String results = MongoDriverInitialize.findOne(param,ConstantDefinition.mongoAllMinPathsCollectionType);
        if (results == null){
            HashMap<String, ArrayList<NodeEntity>> hashMap = new GraphService().searchAllMinPaths(source, target, sourceType, targetType);
            MongoDriverInitialize.addOne(param, hashMap, ConstantDefinition.mongoAllMinPathsCollectionType);
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(hashMap);
            results = jsonObject.toJSONString();
        }
        return results;
    }

    @RequestMapping(value = "getHistory", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin(maxAge = 3600, origins = "*")
    public ArrayList<HashMap<String, Object>> getHistory(@RequestParam("type") int type){
        return MongoDriverInitialize.getHistory(type);
    }

    /**
     * 收取由数据库服务器发送过来的最新IP，并设置为数据库连接URL部分
     * @param ip ip地址
     * @return ip地址
     */
    @RequestMapping(value = "saveIp", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin(maxAge = 3600, origins = "*")
    public String saveIp(@RequestParam("ip") String ip){
        ConstantDefinition.setUrl(ip);
        return ip;
    }

    /**
     * 获取数据库服务器IP地址（即宿舍路由器IP）
     * @return ip地址
     */
    @RequestMapping(value = "getIp", method = RequestMethod.GET)
    @ResponseBody
    @CrossOrigin(maxAge = 3600, origins = "*")
    public String getIp(){
        return ConstantDefinition.getUrl();
    }

    /**
     * query一条语句，测试用
     * @param query 查询语句
     * @return 结果集
     */
    @RequestMapping(value = "query", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin(maxAge = 3600, origins = "*")
    public HashMap<String, ArrayList<NodeEntity>> query(@RequestParam("query") String query){
        return new GraphService().query(query);
    }
}
