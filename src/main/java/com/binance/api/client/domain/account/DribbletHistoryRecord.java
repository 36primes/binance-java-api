package com.binance.api.client.domain.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DribbletHistoryRecord {
    private long operateTime;
    private double totalTransferedAmount;
    private double totalServiceChargeAmount;
    private long transId;
    private List<DribbletHistoryRecordDetail> userAssetDribbletDetails;

    public long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(long operateTime) {
        this.operateTime = operateTime;
    }

    public double getTotalTransferedAmount() {
        return totalTransferedAmount;
    }

    public void setTotalTransferedAmount(double totalTransferedAmount) {
        this.totalTransferedAmount = totalTransferedAmount;
    }

    public double getTotalServiceChargeAmount() {
        return totalServiceChargeAmount;
    }

    public void setTotalServiceChargeAmount(double totalServiceChargeAmount) {
        this.totalServiceChargeAmount = totalServiceChargeAmount;
    }

    public long getTransId() {
        return transId;
    }

    public void setTransId(long transId) {
        this.transId = transId;
    }

    public List<DribbletHistoryRecordDetail> getUserAssetDribbletDetails() {
        return userAssetDribbletDetails;
    }

    public void setUserAssetDribbletDetails(List<DribbletHistoryRecordDetail> userAssetDribbletDetails) {
        this.userAssetDribbletDetails = userAssetDribbletDetails;
    }

    @Override
    public String toString() {
        return "DribbletHistoryRecord{" +
                "operateTime=" + operateTime +
                ", totalTransferedAmount=" + totalTransferedAmount +
                ", totalServiceChargeAmount=" + totalServiceChargeAmount +
                ", transId=" + transId +
                ", userAssetDribbletDetails=" + userAssetDribbletDetails +
                '}';
    }
}
