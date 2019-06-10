package top.guitoubing.postip.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class PostIp {
    private static final String url = "http://20019.ip138.com/ic.asp";
    private static final String postServerIpPort = "218.78.28.138:9900/saveIp";

    /**
     * 由于路由器的不可控重启，导致路由器IP时常变动；通过爬取上述网站，可实时获取路由器IP，并返回使用
     * @return 路由器当前IP
     */
    public static String getNewIP(){
        String ip = null;
        try
        {
            Document document = Jsoup.connect(url).get();
            Elements elements = document.getElementsByTag("center");
            String ipText = elements.get(0).text();
            ip = ipText.split("\\[")[1].split("]")[0];
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return ip;
    }

    public static String postIpToServer(String ip){
        Connection connection = Jsoup.connect(postServerIpPort);
        connection.data("ip", ip);
        Document document = null;
        try {
            document = connection.post();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document.toString();
    }
}
