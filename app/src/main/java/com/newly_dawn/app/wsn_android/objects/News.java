package com.newly_dawn.app.wsn_android.objects;

/**
 * Created by dell on 2016/6/14.
 */
public class News {
    String title;
    String url;
    String source;
    String lmodify;
    String imgSrc;
    String subtitle;
    String ptime;

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getSource() {
        return source;
    }

    public String getLmodify() {
        return lmodify;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getPtime() {
        return ptime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setLmodify(String lmodify) {
        this.lmodify = lmodify;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setPtime(String ptime) {
        this.ptime = ptime;
    }
}

