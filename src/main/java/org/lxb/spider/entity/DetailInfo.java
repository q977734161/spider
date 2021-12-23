package org.lxb.spider.entity;

import com.alibaba.fastjson.JSONObject;

public class DetailInfo {
    private JSONObject baseInfo;
    private JSONObject transactionInfo;
    private JSONObject specialInfo;
    private JSONObject recommandInfo;
    private JSONObject communityInfo;

    @Override
    public String toString() {
        return "DetailInfo{" +
                "baseInfo=" + baseInfo +
                ", transactionInfo=" + transactionInfo +
                ", specialInfo=" + specialInfo +
                ", recommandInfo=" + recommandInfo +
                ", communityInfo=" + communityInfo +
                '}';
    }

    public JSONObject getCommunityInfo() {
        return communityInfo;
    }

    public void setCommunityInfo(JSONObject communityInfo) {
        this.communityInfo = communityInfo;
    }

    public JSONObject getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(JSONObject baseInfo) {
        this.baseInfo = baseInfo;
    }

    public JSONObject getTransactionInfo() {
        return transactionInfo;
    }

    public void setTransactionInfo(JSONObject transactionInfo) {
        this.transactionInfo = transactionInfo;
    }

    public JSONObject getSpecialInfo() {
        return specialInfo;
    }

    public void setSpecialInfo(JSONObject specialInfo) {
        this.specialInfo = specialInfo;
    }

    public JSONObject getRecommandInfo() {
        return recommandInfo;
    }

    public void setRecommandInfo(JSONObject recommandInfo) {
        this.recommandInfo = recommandInfo;
    }
}
