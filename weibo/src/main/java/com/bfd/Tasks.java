package com.bfd;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tcf24 on 2016/10/21.
 */
public class Tasks {

    private Page page;
    private Type type;
//  private String keyword;
    private int retryTimes = WorkCache.retry_times;
    private Map<String,Object> field = new HashMap<String,Object>();
    public boolean isFirst = false;

    public Tasks(String url, Type type,boolean isFirst) {
        page = new Page(url);
        this.type = type;
        this.isFirst = isFirst;
    }

    public Map<String, Object> getField() {
        return field;
    }

    public void setField(Map<String, Object> field) {
        this.field = field;
    }

    public void put(String key, Object value){
        field.put(key,value);
    }

    public Object get(String key){
        return field.get(key);
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getHtml() {
        return page.getHtml();
    }

    public void setHtml(String html) {
        this.page.setHtml(html);
    }

    public String getUrl() {
        return page.getUrl();
    }

    public void setUrl(String url) {
        this.page.setUrl(url);
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type{
        LIST,COMMENT,USER;
    }
}
