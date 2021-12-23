package org.lxb.spider.entity;

import java.util.List;

public class RegionInfo {
    private String href;
    private String regionName;
    private List<AreaInfo> areaInfoList;

    public RegionInfo() {
    }

    public RegionInfo(String href, String regionName) {
        this.href = href;
        this.regionName = regionName;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public List<AreaInfo> getAreaInfoList() {
        return areaInfoList;
    }

    public void setAreaInfoList(List<AreaInfo> areaInfoList) {
        this.areaInfoList = areaInfoList;
    }
}
