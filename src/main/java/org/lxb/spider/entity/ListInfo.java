package org.lxb.spider.entity;

public class ListInfo {

    private String houseId;
    private String community;
    private String detailUrl;
    private String address;
    private String houseInfo;
    private String followInfo;
    private String tag;
    private String totalPrice;
    private String unitPrice;
    private String detailInfo;

    @Override
    public String toString() {
        return "ListInfo{" +
                "community='" + community + '\'' +
                ", detailUrl='" + detailUrl + '\'' +
                ", address='" + address + '\'' +
                ", houseInfo='" + houseInfo + '\'' +
                ", followInfo='" + followInfo + '\'' +
                ", tag='" + tag + '\'' +
                ", totalPrice='" + totalPrice + '\'' +
                ", unitPrice='" + unitPrice + '\'' +
                ", detailInfo='" + detailInfo + '\'' +
                '}';
    }

    public ListInfo() {
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public ListInfo(String houseId,String community, String detailUrl, String address, String houseInfo, String followInfo, String tag, String totalPrice, String unitPrice) {
        this.houseId = houseId;
        this.community = community;
        this.detailUrl = detailUrl;
        this.address = address;
        this.houseInfo = houseInfo;
        this.followInfo = followInfo;
        this.tag = tag;
        this.totalPrice = totalPrice;
        this.unitPrice = unitPrice;
    }

    public String getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(String detailInfo) {
        this.detailInfo = detailInfo;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHouseInfo() {
        return houseInfo;
    }

    public void setHouseInfo(String houseInfo) {
        this.houseInfo = houseInfo;
    }

    public String getFollowInfo() {
        return followInfo;
    }

    public void setFollowInfo(String followInfo) {
        this.followInfo = followInfo;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }
}
