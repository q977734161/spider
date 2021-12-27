package org.lxb.spider.car.entity;

public class BrandInfo {
    private String itemLetter;
    private String brandId;
    private String brandName;
    private String href;

    public BrandInfo() {
    }

    public BrandInfo(String itemLetter, String brandId, String brandName, String href) {
        this.itemLetter = itemLetter;
        this.brandId = brandId;
        this.brandName = brandName;
        this.href = href;
    }

    public String getItemLetter() {
        return itemLetter;
    }

    public void setItemLetter(String itemLetter) {
        this.itemLetter = itemLetter;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
