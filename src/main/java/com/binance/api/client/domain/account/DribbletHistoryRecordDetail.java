package com.binance.api.client.domain.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DribbletHistoryRecordDetail {
    private long transId;
    private double serviceChargeAmount;
    private double amount;
    private long operateTime;
    private double transferedAmount;
    private String fromAsset;

    public long getTransId() {
        return transId;
    }

    public void setTransId(long transId) {
        this.transId = transId;
    }

    public double getServiceChargeAmount() {
        return serviceChargeAmount;
    }

    public void setServiceChargeAmount(double serviceChargeAmount) {
        this.serviceChargeAmount = serviceChargeAmount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(long operateTime) {
        this.operateTime = operateTime;
    }

    public double getTransferedAmount() {
        return transferedAmount;
    }

    public void setTransferedAmount(double transferedAmount) {
        this.transferedAmount = transferedAmount;
    }

    public String getFromAsset() {
        return fromAsset;
    }

    public void setFromAsset(String fromAsset) {
        this.fromAsset = fromAsset;
    }

    @Override
    public String toString() {
        return "DribbletHistoryRecordDetail{" +
                "transId=" + transId +
                ", serviceChargeAmount=" + serviceChargeAmount +
                ", amount=" + amount +
                ", operateTime=" + operateTime +
                ", transferedAmount=" + transferedAmount +
                ", fromAsset='" + fromAsset + '\'' +
                '}';
    }
}
