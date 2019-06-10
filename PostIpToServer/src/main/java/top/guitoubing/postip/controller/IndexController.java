package top.guitoubing.postip.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import top.guitoubing.postip.util.PostIp;

@RestController
public class IndexController {
    @RequestMapping(value = "index")
    @ResponseBody
    public String index(){
        return PostIp.postIpToServer(PostIp.getNewIP());
    }
}
