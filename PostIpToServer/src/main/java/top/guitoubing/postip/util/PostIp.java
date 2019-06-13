package top.guitoubing.postip.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostIp {
    private static final String url = "http://www.ip138.com/ips138.asp";
    private static final String postServerIpPort = "http://218.78.28.138:9900/saveIp";

    /**
     * 由于路由器的不可控重启，导致路由器IP时常变动；通过爬取上述网站，可实时获取路由器IP，并返回使用
     * @return 路由器当前IP
     */
    public static String getNewIP(){
        String ip = null;
        try
        {
            Document document = Jsoup.connect(url).get();
            Elements elements = document.getElementsByTag("table");
            String ipText = elements.get(2).text();
            ip = ipText.split("\\[")[1].split("]")[0];
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // 写入日志文件
        try {
            FileWriter fileWriter = new FileWriter(new File("ip.log"), true);
            fileWriter.append("[").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("]").append(":").append(ip).append("\r\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ip;
    }

    /**
     * 向服务器发送该ip交由服务器上处理，设置为数据库服务器新IP
     * @param ip ip地址
     * @return ip地址
     */
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
