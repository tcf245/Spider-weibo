package com.bfd.process;

import com.bfd.Tasks;
import com.bfd.WorkCache;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by BFD_303 on 2016/10/20.
 */
public class ListProcess extends Processor {

    @Override
    public void setType(Tasks.Type type) {
        super.setType(Tasks.Type.LIST);
    }

    @Override
    void process(Tasks task) throws InterruptedException {

        String html = task.getHtml();
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("div.WB_cardwrap");

        for (Element e : elements) {
            String content = e.select("div.content p.comment_txt").text();
            String name = e.select("div.content div.feed_content a.name_txt").text();

            String time = e.select("div.feed_from a.W_textb").text();

            Element feed = e.select("div.feed_action ul.feed_action_info").get(0);
            String forward = feed.select("li:contains(转发)").text().replace("转发","");
            String collect = feed.select("li:contains(收藏)").text().replace("收藏","");
            String comment_count = feed.select("li:contains(评论)").text().replace("评论","");

            task.put("content", content);
            task.put("name", name);
            task.put("time", time);
            task.put("forward", forward);
            task.put("collect", collect);
            task.put("comment_count", comment_count);

            String resultData = gson.toJson(task);

            WorkCache.results.put(resultData);
        }

        String nextpage = elements.select("div.W_pages span.list div.layer_menu_list ul").select("a[href]").text();
        int pageSize = nextpage.split(" ").length;
        if (task.isFirst) {
            for (int i = 0; i < pageSize; i++) {
                String url = "";
                WorkCache.tasks.put(new Tasks(url, Tasks.Type.LIST, false));
            }
        }

    }
}
