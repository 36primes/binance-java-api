package com.binance.api.client.domain.market;

import com.binance.api.client.constant.BinanceApiConstants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * An aggregated trade event for a symbol.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Trade {

  @JsonProperty("t")
  private long tradeId;

  @JsonProperty("p")
  private String price;

  @JsonProperty("q")
  private String quantity;

  @JsonProperty("b")
  private long buyerOrderId;

  @JsonProperty("a")
  private long sellerOrderId;

  @JsonProperty("T")
  private long tradeTime;

  @JsonProperty("m")
  private boolean isBuyerMaker;

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public String getQuantity() {
    return quantity;
  }

  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }

  public long getTradeTime() {
    return tradeTime;
  }

  public void setTradeTime(long tradeTime) {
    this.tradeTime = tradeTime;
  }

  public boolean isBuyerMaker() {
    return isBuyerMaker;
  }

  public void setBuyerMaker(boolean buyerMaker) {
    isBuyerMaker = buyerMaker;
  }

  public long getTradeId() {
    return tradeId;
  }

  public void setTradeId(long tradeId) {
    this.tradeId = tradeId;
  }

  public long getBuyerOrderId() {
    return buyerOrderId;
  }

  public void setBuyerOrderId(long buyerOrderId) {
    this.buyerOrderId = buyerOrderId;
  }

  public long getSellerOrderId() {
    return sellerOrderId;
  }

  public void setSellerOrderId(long sellerOrderId) {
    this.sellerOrderId = sellerOrderId;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)
            .append("tradeId", tradeId)
            .append("price", price)
            .append("quantity", quantity)
            .append("buyerOrderId", buyerOrderId)
            .append("sellerOrderId", sellerOrderId)
            .append("tradeTime", tradeTime)
            .append("isBuyerMaker", isBuyerMaker)
            .toString();
  }
}
