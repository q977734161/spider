package org.lxb.spider.car.entity;


import java.util.HashMap;
import java.util.Map;

public class CarConfigParams {
    private Map<String, String> baseInfo = new HashMap<>(); //基本信息
    private Map<String, String> carBodyInfo = new HashMap<>(); //        车身
    private Map<String, String> engineInfo = new HashMap<>(); //发动机
    private Map<String, String> gearboxInfo = new HashMap<>(); //        变速箱
    private Map<String, String> chassisSteeringInfo = new HashMap<>(); //底盘转向
    private Map<String, String> wheelBrakeInfo = new HashMap<>(); //        车轮制动
    private Map<String, String> activeSafetyInfo  = new HashMap<>(); //主动安全
    private Map<String, String> passiveSafetyInfo = new HashMap<>(); //        被动安全
    private Map<String, String> assistOperateInfo = new HashMap<>(); //辅助/操控配置
    private Map<String, String> assistDriveInfo = new HashMap<>(); //        辅助驾驶功能
    private Map<String, String> assistDriveHardWareInfo = new HashMap<>(); //辅助驾驶硬件
    private Map<String, String> outConfigInfo = new HashMap<>(); //        外部配置
    private Map<String, String> innerConfigInfo = new HashMap<>(); //内部配置
    private Map<String, String> seatConfigInfo = new HashMap<>(); //        座椅配置
    private Map<String, String> internetOfVehiclesInfo = new HashMap<>(); //车机/互联
    private Map<String, String> videoEntertainmentInfo = new HashMap<>(); //        影音娱乐
    private Map<String, String> lightFuncInfo = new HashMap<>(); //灯光功能
    private Map<String, String> grassRearviewMirrorInfo = new HashMap<>(); //玻璃/后视镜
    private Map<String, String> airConditionInfo = new HashMap<>(); //空调/制冷
    private Map<String, String> optionalInfo = new HashMap<>(); //        选配包

    public Map<String, String> getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(Map<String, String> baseInfo) {
        this.baseInfo = baseInfo;
    }

    public Map<String, String> getCarBodyInfo() {
        return carBodyInfo;
    }

    public void setCarBodyInfo(Map<String, String> carBodyInfo) {
        this.carBodyInfo = carBodyInfo;
    }

    public Map<String, String> getEngineInfo() {
        return engineInfo;
    }

    public void setEngineInfo(Map<String, String> engineInfo) {
        this.engineInfo = engineInfo;
    }

    public Map<String, String> getGearboxInfo() {
        return gearboxInfo;
    }

    public void setGearboxInfo(Map<String, String> gearboxInfo) {
        this.gearboxInfo = gearboxInfo;
    }

    public Map<String, String> getChassisSteeringInfo() {
        return chassisSteeringInfo;
    }

    public void setChassisSteeringInfo(Map<String, String> chassisSteeringInfo) {
        this.chassisSteeringInfo = chassisSteeringInfo;
    }

    public Map<String, String> getWheelBrakeInfo() {
        return wheelBrakeInfo;
    }

    public void setWheelBrakeInfo(Map<String, String> wheelBrakeInfo) {
        this.wheelBrakeInfo = wheelBrakeInfo;
    }

    public Map<String, String> getActiveSafetyInfo() {
        return activeSafetyInfo;
    }

    public void setActiveSafetyInfo(Map<String, String> activeSafetyInfo) {
        this.activeSafetyInfo = activeSafetyInfo;
    }

    public Map<String, String> getPassiveSafetyInfo() {
        return passiveSafetyInfo;
    }

    public void setPassiveSafetyInfo(Map<String, String> passiveSafetyInfo) {
        this.passiveSafetyInfo = passiveSafetyInfo;
    }

    public Map<String, String> getAssistOperateInfo() {
        return assistOperateInfo;
    }

    public void setAssistOperateInfo(Map<String, String> assistOperateInfo) {
        this.assistOperateInfo = assistOperateInfo;
    }

    public Map<String, String> getAssistDriveInfo() {
        return assistDriveInfo;
    }

    public void setAssistDriveInfo(Map<String, String> assistDriveInfo) {
        this.assistDriveInfo = assistDriveInfo;
    }

    public Map<String, String> getAssistDriveHardWareInfo() {
        return assistDriveHardWareInfo;
    }

    public void setAssistDriveHardWareInfo(Map<String, String> assistDriveHardWareInfo) {
        this.assistDriveHardWareInfo = assistDriveHardWareInfo;
    }

    public Map<String, String> getOutConfigInfo() {
        return outConfigInfo;
    }

    public void setOutConfigInfo(Map<String, String> outConfigInfo) {
        this.outConfigInfo = outConfigInfo;
    }

    public Map<String, String> getInnerConfigInfo() {
        return innerConfigInfo;
    }

    public void setInnerConfigInfo(Map<String, String> innerConfigInfo) {
        this.innerConfigInfo = innerConfigInfo;
    }

    public Map<String, String> getSeatConfigInfo() {
        return seatConfigInfo;
    }

    public void setSeatConfigInfo(Map<String, String> siteConfigInfo) {
        this.seatConfigInfo = siteConfigInfo;
    }

    public Map<String, String> getInternetOfVehiclesInfo() {
        return internetOfVehiclesInfo;
    }

    public void setInternetOfVehiclesInfo(Map<String, String> internetOfVehiclesInfo) {
        this.internetOfVehiclesInfo = internetOfVehiclesInfo;
    }

    public Map<String, String> getVideoEntertainmentInfo() {
        return videoEntertainmentInfo;
    }

    public void setVideoEntertainmentInfo(Map<String, String> videoEntertainmentInfo) {
        this.videoEntertainmentInfo = videoEntertainmentInfo;
    }

    public Map<String, String> getLightFuncInfo() {
        return lightFuncInfo;
    }

    public void setLightFuncInfo(Map<String, String> lightFuncInfo) {
        this.lightFuncInfo = lightFuncInfo;
    }

    public Map<String, String> getGrassRearviewMirrorInfo() {
        return grassRearviewMirrorInfo;
    }

    public void setGrassRearviewMirrorInfo(Map<String, String> grassRearviewMirrorInfo) {
        this.grassRearviewMirrorInfo = grassRearviewMirrorInfo;
    }

    public Map<String, String> getAirConditionInfo() {
        return airConditionInfo;
    }

    public void setAirConditionInfo(Map<String, String> airConditionInfo) {
        this.airConditionInfo = airConditionInfo;
    }

    public Map<String, String> getOptionalInfo() {
        return optionalInfo;
    }

    public void setOptionalInfo(Map<String, String> optionalInfo) {
        this.optionalInfo = optionalInfo;
    }
}
