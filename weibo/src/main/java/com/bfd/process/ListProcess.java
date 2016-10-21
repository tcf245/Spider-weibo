package com.bfd.process;

import com.bfd.Tasks;
import com.bfd.WorkCache;
import com.bfd.utils.HttpUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.Map;
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
                String usercard = e.select("div.content div.feed_content").attr("a[usercard]");

                String time = e.select("div.feed_from a.W_textb").text();

                Element feed = e.select("div.feed_action ul.feed_action_info").get(0);
                String forward = feed.select("li:contains(转发)").text().replace("转发","");
                String collect = feed.select("li:contains(收藏)").text().replace("收藏","");
                String comment_count = feed.select("li:contains(评论)").text().replace("评论","");

                task.put("usercard",usercard);
                task.put("content", content);
                task.put("name", name);
                task.put("time", time);
                task.put("forward", forward);
                task.put("collect", collect);
                task.put("comment_count", comment_count);

            }catch(NullPointerException e1){
                continue;
            }

        }
        if (task.get("usercard") != null){
            //TODO 扩散usercard任务，下载并解析，获取粉丝数量
            String url = "http://weibo.com/aj/user/newcard?@@@usercard@@@&type=1";
            url = url.replace("@@@usercard@@@",(String)task.get("usercard"));

            try {
                String content = HttpUtils.httpGet(url,WorkCache.cookie,WorkCache.charset);
                content = content.replace(")}catch(e){};","");
                content = content.substring(23);
                Map<String,Object> user = gson.fromJson(content,Map.class);
                String userHtml = (String) user.get("data");

                Document d = Jsoup.parse(userHtml);
                String fans = d.select("div.related_info div.name dd div li:contains(收藏)").text().replace("收藏","");
                task.put("fans",Integer.valueOf(fans));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String nextpage = elements.select("div.W_pages span.list div.layer_menu_list ul").select("a[href]").text();
            //TODO  扩散翻页任务
        int pageSize = nextpage.split(" ").length;
        if (task.isFirst) {
            for (int i = 2; i <= pageSize; i++) {
                String url = "";
                WorkCache.tasks.put(new Tasks(url, Tasks.Type.LIST, false));
            }
        }

        //TODO  解析结果保存
        try {
            String resultData = gson.toJson(task.getField());
            FileUtils.writeStringToFile(new File(WorkCache.target),resultData,true);
            System.out.println(resultData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void packHtml(Tasks task){
        String html = task.getHtml();
        Pattern p = Pattern.compile("\\{\"pid\":\"pl_weibo_direct\"[^\\}]+\\}");
        Matcher m = p.matcher(html);
        if (m.find()){
            Map<String,Object> data = gson.fromJson(m.group(0),Map.class);
            html = (String) data.get("html");
            task.setHtml(html);
        }
    }
}
