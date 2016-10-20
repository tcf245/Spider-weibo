package com.bfd;

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
    private static final Log LOG = LogFactory.getLog(SpiderMain.class);

    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();
        PropertyConfigurator.configureAndWatch(SpiderMain.class.getClassLoader().getResource("log4j.properties").getFile());

        Properties pro = new Properties();
        pro.load(FileUtils.openInputStream(new File(SpiderMain.class.getClassLoader().getResource("crawl-config.properties").getFile())));

        WorkCache.keywords = gson.fromJson((String) pro.get("keyword"),List.class);

        URLEncoder.encode("");


    }

}
