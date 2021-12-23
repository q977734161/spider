package org.lxb.spider.entity;

public class SiteInfo {
    private String href;
    private String siteName;

    public SiteInfo() {
    }

    public SiteInfo(String href, String siteName) {
        this.href = href;
        this.siteName = siteName;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
}
