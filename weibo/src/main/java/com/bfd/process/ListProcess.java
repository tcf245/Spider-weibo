package com.bfd.process;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

/**
 * Created by BFD_303 on 2016/10/20.
 */
public class ListProcess {

    public static void main(String[] args) {
        try {
            String html = FileUtils.readFileToString(new File(ListProcess.class.getClassLoader().getResource("list.html").getFile()));
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select("div.WB_cardwrap");
            System.out.println(elements.size());
            for (Element e: elements) {
                System.out.println(e.text());
            }



        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
