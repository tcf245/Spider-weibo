package com.bfd;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by BFD_303 on 2016/10/20.
 */
public class WorkCache {
    public static List<String> keywords ;
    public static int retry_times = 3;
    public static BlockingQueue<Tasks> tasks = new LinkedBlockingQueue<>();
    public static BlockingQueue<String> results = new LinkedBlockingQueue<>();
    public static String charset = "utf-8";
    public static String cookie;
    public static String target = "etc/target";
}
