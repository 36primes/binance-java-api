package com.binance.api.client.domain.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DribbletHistory {
    private int total;
    private List<DribbletHistoryRecord> userAssetDribblets;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DribbletHistoryRecord> getUserAssetDribblets() {
        return userAssetDribblets;
    }

    public void setUserAssetDribblets(List<DribbletHistoryRecord> userAssetDribblets) {
        this.userAssetDribblets = userAssetDribblets;
    }

    @Override
    public String toString() {
        return "DribbletHistory{" +
                "total=" + total +
                ", userAssetDribblets=" + userAssetDribblets +
                '}';
    }
}
