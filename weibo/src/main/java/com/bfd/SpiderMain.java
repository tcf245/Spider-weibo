package com.bfd;

import com.bfd.process.ListProcess;
import com.bfd.process.Processor;
import com.bfd.utils.HttpUtils;
import com.google.gson.Gson;
import org.apache.commons.codec.Encoder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Properties;

/**
 * Created by BFD_303 on 2016/10/20.
 */
public class SpiderMain {
//    private static final Log LOG = LogFactory.getLog(SpiderMain.class);
    private static Gson gson = new Gson();

    static {
        try {
            Properties pro = new Properties();
            pro.load(FileUtils.openInputStream(new File("etc/crawl-config.properties")));
            WorkCache.charset = (String) pro.get("charset");
            WorkCache.cookie = (String) pro.get("cookie");
            WorkCache.target = (String) pro.get("target");
            WorkCache.keywords = gson.fromJson("[\"光大银行\",\"黄金邮票\",\"理财早夜市\",\"阳光银行\",\"光大+信用卡\",\"光大+借记卡\",\"光大+理财\"]", List.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
//        PropertyConfigurator.configureAndWatch("etc/log4j.properties");
        System.out.println("WorkCache.keywords  size is : " + WorkCache.keywords.size());
        try {
            loadTask();

            while (WorkCache.tasks.size() > 0) {
                System.out.println("get task size is : " + WorkCache.tasks.size());

                Tasks t = WorkCache.tasks.take();
                String content = HttpUtils.httpGet(t.getUrl(), WorkCache.cookie, WorkCache.charset);
                t.setHtml(content);

                System.out.println("get content length is : " + content.length());

                Processor p = new ListProcess();
                p.process(t);

                Thread.sleep(1000 * 60);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void loadTask() {
        try {
            for (String s : WorkCache.keywords) {
                String url = "http://s.weibo.com/weibo/@@@keyword@@@&typeall=1&suball=1&timescope=custom:2016-10-17-0:2016-10-24-0&Refer=g?sudaref=s.weibo.com";
                url = url.replace("@@@keyword@@@", URLEncoder.encode(s));
                System.out.println(URLEncoder.encode(s));
                System.out.println("get task url is : " + url);
                WorkCache.tasks.put(new Tasks(url, Tasks.Type.LIST, true));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
