package org.lxb.spider.car.entity;

public class AreaInfo {
    private String href;
    private String areaName;

    public AreaInfo() {
    }

    public AreaInfo(String href, String areaName) {
        this.href = href;
        this.areaName = areaName;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
