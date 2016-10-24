package com.bfd.process;

import com.bfd.Tasks;
import com.bfd.WorkCache;
import com.bfd.utils.HttpUtils;
import com.bfd.utils.StringUtils;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by BFD_303 on 2016/10/20.
 */
public class ListProcess extends Processor {

    @Override
    public void setType(Tasks.Type type) {
        super.setType(Tasks.Type.LIST);
    }

    @Override
    public void process(Tasks task) throws InterruptedException {
        // TODO 解析博文搜索列表页
        packHtml(task);

        String html = task.getHtml();
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("div.WB_cardwrap");

        for (Element e : elements) {
            try{
                String content = e.select("div.content p.comment_txt").text();
                String name = e.select("div.content div.feed_content a.name_txt").text();
                String usercard = e.select("div.content div.feed_content a[href]").attr("href");
                //获取fans数量
                int fans = getFansNum(usercard);

                String time = e.select("div.feed_from a.W_textb").text();

                String feedStr = e.select("div.feed_action").text();
                int forward = getCount(feedStr,"转发");
                int collect = getCount(feedStr,"收藏");
                int comment_count = getCount(feedStr,"评论");

                Map<String,Object> field = new HashMap<String,Object>();
                field.put("usercard",usercard);
                field.put("content", content);
                field.put("name", name);
                field.put("time", time);
                field.put("fans",fans);
                field.put("forward", forward);
                field.put("collect", collect);
                field.put("comment_count", comment_count);

                if (task.getUrl().contains(URLEncoder.encode("光大银行"))){
                    //TODO 获取评论人昵称
                    List<String> commentors = new ArrayList<String>();
                    String mid = e.select("div[mid]").attr("mid");
                    String commentUrl = "http://weibo.com/aj/v6/comment/big?ajwvr=6&id=" + mid;

                    content = HttpUtils.httpGet(commentUrl,WorkCache.cookie,WorkCache.charset);
                    Map<String,Object> comm = gson.fromJson(content,Map.class);
                    html = (String) ((Map<String,Object>)comm.get("data")).get("html");
                    html = StringUtils.unicodeToString(html);

                    doc = Jsoup.parse(html);
                    Elements elements1 = doc.select("div.list_li");
                    for (Element e1:elements1) {
                        String commentor = e1.select("div.list_con div.WB_text a").text();
                        commentors.add(commentor);
                    }
                    field.put("commentors",commentors);
                }

                //数据保存
                save(field);

            }catch(IndexOutOfBoundsException e1){
                continue;
            }catch(Exception e1){
                continue;
            }

        }


        String nextpage = elements.select("div.W_pages span.list div.layer_menu_list ul").select("a[href]").text();
        //TODO  扩散翻页任务
        int pageSize = nextpage.split(" ").length;
        if (task.isFirst) {
            for (int i = 2; i <= pageSize; i++) {
                String url = task.getUrl();
                url = url + "&page=" + i;
                WorkCache.tasks.put(new Tasks(url, Tasks.Type.LIST, false));
                System.out.println("add one task , url is : " + url);
            }
        }
    }

    private void save(Map<String,Object> map) {
        //TODO  解析结果保存
        try {
            String resultData = gson.toJson(map);
            FileUtils.writeStringToFile(new File(WorkCache.target),resultData + "\n",true);
            System.out.println(resultData);
        } catch (IOException e) {
            System.out.println("save exception. data is " + map.toString());
            e.printStackTrace();
        }
    }

    /**
     * 获取转发数量
     * @param content
     * @param tag
     * @return
     */
    private int getCount(String content, String tag) {
        Pattern p = Pattern.compile(tag + "(\\d+)");
        Matcher m = p.matcher(content);
        if (m.find()){
            System.out.println(m.group(1));
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    /**
     * 根据usercard 发送请求获取用户粉丝数量
     * @param usercard
     * @return
     */
    private int getFansNum(String usercard) throws Exception{
        //TODO 扩散usercard任务，下载并解析，获取粉丝数量
        String url = "http://weibo.com/aj/user/newcard?@@@usercard@@@";
        url = url.replace("@@@usercard@@@", usercard);

        url = usercard;
        System.out.println(usercard);

        try {
            String content = HttpUtils.httpGet(url, WorkCache.cookie, WorkCache.charset);
            content = StringUtils.unicodeToString(content);

            Pattern p = Pattern.compile("\\{\"ns\":\"\",\"domid\":\"Pl_Core_T8CustomTriColumn__3\"[^\\}]+}");
            Matcher m = p.matcher(content);
            if (m.find()){
                content = m.group(0);
                Map<String,Object> map = gson.fromJson(content,Map.class);
                content = (String) map.get("html");
                content = StringUtils.unicodeToString(content);
                Document doc = Jsoup.parse(content);
                String s = doc.select("div.WB_cardwrap div.PCD_counter div.WB_innerwrap table.tb_counter").text();
                System.out.println(s);

                p = Pattern.compile("([\\d万]+)粉丝");
                m = p.matcher(s);
                if (m.find()){
                    System.out.println(m.group(1));
                    return Integer.parseInt(m.group(1));
                }
            }
        } catch (Exception e) {
            System.out.println("get fans num exception .. usercard is " + usercard);
            e.printStackTrace();
            throw new Exception();
        }
        return 0;
    }

    /**
     * 获取html源码
     * @param task
     */
    public void packHtml(Tasks task){
        String html = task.getHtml();
        Pattern p = Pattern.compile("\\{\"pid\":\"pl_weibo_direct\"[^\\}]+\\}");
        Matcher m = p.matcher(html);
        if (m.find()){
            Map<String,Object> data = gson.fromJson(m.group(0),Map.class);
            html = (String) data.get("html");
            html = StringUtils.unicodeToString(html);
            task.setHtml(html);
        }
    }

    public static void main(String[] args) throws Exception {
        ListProcess p = new ListProcess();
        Gson gson = new Gson();

//        try {
//            Properties pro = new Properties();
//            pro.load(FileUtils.openInputStream(new File("etc/crawl-config.properties")));
//            WorkCache.charset = (String) pro.get("charset");
//            WorkCache.cookie = (String) pro.get("cookie");
//            WorkCache.target = (String) pro.get("target");
//            WorkCache.keywords = gson.fromJson("[\"光大银行\",\"黄金邮票\",\"理财早夜市\",\"阳光银行\",\"光大+信用卡\",\"光大+借记卡\",\"光大+理财\"]", List.class);
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        String url = "http://weibo.com/aj/v6/comment/big?ajwvr=6&id=4032420431067235";
        String content = HttpUtils.httpGet(url,WorkCache.cookie,WorkCache.charset);

        System.out.println(url);
        System.out.println(content);

    }

}