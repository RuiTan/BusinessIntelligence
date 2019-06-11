package top.guitoubing.bi.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;
import top.guitoubing.bi.entity.NodeEntity;
import top.guitoubing.bi.service.GraphService;
import top.guitoubing.bi.util.ConstantDefinition;
import top.guitoubing.bi.util.MongoDriverInitialize;
import top.guitoubing.bi.util.ParamUtils;

import javax.xml.soap.Node;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
public class IndexController {

    @RequestMapping(value = "index")
    public String index(){
        return "index";
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
    public String searchANode(
            @RequestParam("type") int type, @RequestParam("step") int step, @RequestParam("id") int id, @RequestParam("limit") int limit){
        String param = ParamUtils.paramsToString(type, step, id, limit);
        String results = MongoDriverInitialize.findOne(param);
        if (results == null){
            HashMap<String, ArrayList<NodeEntity>> hashMap = new GraphService().searchByTypeAndId(type, step, limit, id);
            MongoDriverInitialize.addOne(param, hashMap);
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
    public String searchByTwoNodes(
            @RequestParam("sourceType") int sourceType, @RequestParam("targetType") int targetType, @RequestParam("step") int step,
            @RequestParam("limit") int limit, @RequestParam("sourceId") int sourceId, @RequestParam("targetId") int targetId){
        String param = ParamUtils.paramsToString(sourceType, targetType, step, limit, sourceId, targetId);
        String results = MongoDriverInitialize.findOne(param);
        if (results == null){
            HashMap<String, ArrayList<NodeEntity>> hashMap = new GraphService().searchByTwoNodes(sourceType, targetType, step, limit, sourceId, targetId);
            MongoDriverInitialize.addOne(param, hashMap);
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(hashMap);
            results = jsonObject.toJSONString();
        }
        return results;
    }

    @RequestMapping(value = "searchMinPath", method = RequestMethod.POST)
    @ResponseBody
    public String searchMinPath(@RequestParam("source") int source, @RequestParam("target") int target,
                                                                @RequestParam("sourceType") int sourceType, @RequestParam("targetType") int targetType){
        String param = ParamUtils.paramsToString(source, target, sourceType, targetType);
        String results = MongoDriverInitialize.findOne(param);
        if (results == null){
            HashMap<String, ArrayList<NodeEntity>> hashMap = new GraphService().searchMinPath(source, target, sourceType, targetType);
            MongoDriverInitialize.addOne(param, hashMap);
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(hashMap);
            results = jsonObject.toJSONString();
        }
        return results;
    }

    /**
     * 收取由数据库服务器发送过来的最新IP，并设置为数据库连接URL部分
     * @param ip ip地址
     * @return ip地址
     */
    @RequestMapping(value = "saveIp", method = RequestMethod.POST)
    @ResponseBody
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
    public String getIp(){
        return ConstantDefinition.getUrl();
    }

    @RequestMapping(value = "query", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, ArrayList<NodeEntity>> query(@RequestParam("query") String query){
        return new GraphService().query(query);
    }
}
