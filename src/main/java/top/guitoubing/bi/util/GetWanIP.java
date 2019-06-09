package top.guitoubing.bi.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GetWanIP {
    private static final String url = "http://20019.ip138.com/ic.asp";

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

    public static void main(String[] args){
        System.out.println(getNewIP());
    }
}
