package org.lxb.spider.car.entity;

import java.util.List;

public class CarInfo {
    private String brandId;
    private String brandName;
    private String letter;
    private List<CarTypeDetailInfo> carTypeDetailInfos;

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

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public List<CarTypeDetailInfo> getCarTypeDetailInfos() {
        return carTypeDetailInfos;
    }

    public void setCarTypeDetailInfos(List<CarTypeDetailInfo> carTypeDetailInfos) {
        this.carTypeDetailInfos = carTypeDetailInfos;
    }
}
