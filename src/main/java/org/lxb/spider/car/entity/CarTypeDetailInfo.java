package org.lxb.spider.car.entity;

public class CarTypeDetailInfo {
    private String dataCkid;
    private String carName ;
    private String carStyleInfo;
    private String carPrice;
    private int index;
    private CarConfigParams carConfigParams;

    public String getDataCkid() {
        return dataCkid;
    }

    public void setDataCkid(String dataCkid) {
        this.dataCkid = dataCkid;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public void setCarStyleInfo(String carStyleInfo) {
        this.carStyleInfo = carStyleInfo;
    }

    public void setCarPrice(String carPrice) {
        this.carPrice = carPrice;
    }

    public void setCarConfigParams(CarConfigParams carConfigParams) {
        this.carConfigParams = carConfigParams;
    }

    public String getCarName() {
        return carName;
    }

    public String getCarStyleInfo() {
        return carStyleInfo;
    }

    public String getCarPrice() {
        return carPrice;
    }

    public CarConfigParams getCarConfigParams() {
        return carConfigParams;
    }
}
