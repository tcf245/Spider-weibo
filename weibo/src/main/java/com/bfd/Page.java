package com.bfd;

/**
 * Created by tcf24 on 2016/10/21.
 */
public class Page {

    private String url;
    private String html;
    private String json;

    public Page(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
