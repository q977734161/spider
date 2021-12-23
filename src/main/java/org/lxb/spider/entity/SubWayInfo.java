package org.lxb.spider.entity;

import java.util.List;

public class SubWayInfo {
    private String href;
    private String lineName;
    private List<SiteInfo> siteInfoList;

    public SubWayInfo(String href, String lineName) {
        this.href = href;
        this.lineName = lineName;
    }

    public SubWayInfo() {
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public List<SiteInfo> getSiteInfoList() {
        return siteInfoList;
    }

    public void setSiteInfoList(List<SiteInfo> siteInfoList) {
        this.siteInfoList = siteInfoList;
    }
}
