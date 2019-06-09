package top.guitoubing.bi.controller;

import org.springframework.web.bind.annotation.*;
import top.guitoubing.bi.service.GraphService;

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
     * @param searchText 实体名称
     * @param limit 限定结果数量
     * @return 查询的节点和关系集合
     */
    @RequestMapping(value = "searchANode", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, ArrayList<HashMap<String, Object>>> searchANode(@RequestParam("type") int type, @RequestParam("step") int step, @RequestParam("searchText") String searchText,@RequestParam("limit") int limit){
        return new GraphService().searchByTypeAndSearchingText(type, step, limit, searchText);
    }


}
