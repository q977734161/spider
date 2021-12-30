package org.lxb.spider.car.entity;

import java.util.ArrayList;
import java.util.List;

public class ListInfo {

    private String nextPageUrl;
    private List<Item> item;


    public static class Item {
        private String dataId;
        private String brandName;
        private String priceRange;
        private String peizhiUrl;

        public Item(String dataId, String brandName, String priceRange, String peizhiUrl) {
            this.dataId = dataId;
            this.brandName = brandName;
            this.priceRange = priceRange;
            this.peizhiUrl = peizhiUrl;
        }

        public Item() {
        }

        public String getDataId() {
            return dataId;
        }

        public void setDataId(String dataId) {
            this.dataId = dataId;
        }

        public String getBrandName() {
            return brandName;
        }

        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }

        public String getPriceRange() {
            return priceRange;
        }

        public void setPriceRange(String priceRange) {
            this.priceRange = priceRange;
        }

        public String getPeizhiUrl() {
            return peizhiUrl;
        }

        public void setPeizhiUrl(String peizhiUrl) {
            this.peizhiUrl = peizhiUrl;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "dataId='" + dataId + '\'' +
                    ", brandName='" + brandName + '\'' +
                    ", priceRange='" + priceRange + '\'' +
                    ", peizhiUrl='" + peizhiUrl + '\'' +
                    '}';
        }
    }

    public ListInfo() {
    }

    public ListInfo(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public void setNextPageUrl(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }

    public List<Item> getItem() {
        return item;
    }

    public void setItem(List<Item> item) {
        this.item = item;
    }

    public void addItem(Item item) {
        if(this.item == null) {
            this.item = new ArrayList<>();
        }
        this.item.add(item);
    }

    @Override
    public String toString() {
        return "ListInfo{" +
                "nextPageUrl='" + nextPageUrl + '\'' +
                ", item=" + item +
                '}';
    }
}
