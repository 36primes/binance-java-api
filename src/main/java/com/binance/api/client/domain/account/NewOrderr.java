package com.binance.api.client.domain.account;

import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A trade order to enter or exit a position.
 */
@JsonIgnoreProperties(ignoreUnknown = true) 
public class NewOrderr {

  /**
   * Symbol to place the order on.
   */
  private String symbol;

  /**
   * Buy/Sell order side.
   */
  private OrderSide side;

  /**
   * Type of order.
   */
  private OrderType type;

  /**
   * Time in force to indicate how long will the order remain active.
   */
  private TimeInForce timeInForce;

  /**
   * Quantity.
   */
  private String quantity;

  /**
   * Quote quantity.
   */
  private String quoteOrderQty;

  /**
   * Price.
   */
  private String price;

  /**
   * A unique id for the order. Automatically generated if not sent.
   */
  private String newClientOrderId;

  /**
   * Used with stop orders.
   */
  private String stopPrice;

  /**
   * Used with stop orders.
   */
  private String stopLimitPrice;

  /**
   * Used with iceberg orders.
   */
  private String icebergQty;

  /**
   * Set the response JSON. ACK, RESULT, or FULL; default: RESULT.
   */
  private NewOrderResponseType newOrderRespType;

  /**
   * Receiving window.
   */
  private Long recvWindow;

  /**
   * Order timestamp.
   */
  private long timestamp;

  /**
   * Creates a new order with all required parameters.
   */
  public NewOrderr(String symbol, OrderSide side, OrderType type, TimeInForce timeInForce, String quantity) {
    this.symbol = symbol;
    this.side = side;
    this.type = type;
    this.timeInForce = timeInForce;
    this.quantity = quantity;
    this.newOrderRespType = NewOrderResponseType.RESULT;
    this.timestamp = System.currentTimeMillis();
    this.recvWindow = BinanceApiConstants.DEFAULT_RECEIVING_WINDOW;
  }

  /**
   * Creates a new order with all required parameters plus price, which is optional for MARKET orders.
   */
  public NewOrderr(String symbol, OrderSide side, OrderType type, TimeInForce timeInForce, String quantity, String price) {
    this(symbol, side, type, timeInForce, quantity);
    this.price = price;
  }

  public String getSymbol() {
    return symbol;
  }

  public NewOrderr symbol(String symbol) {
    this.symbol = symbol;
    return this;
  }

  public OrderSide getSide() {
    return side;
  }

  public NewOrderr side(OrderSide side) {
    this.side = side;
    return this;
  }

  public OrderType getType() {
    return type;
  }

  public NewOrderr type(OrderType type) {
    this.type = type;
    return this;
  }

  public TimeInForce getTimeInForce() {
    return timeInForce;
  }

  public NewOrderr timeInForce(TimeInForce timeInForce) {
    this.timeInForce = timeInForce;
    return this;
  }

  public String getQuantity() {
    return quantity;
  }

  public NewOrderr quantity(String quantity) {
    this.quantity = quantity;
    return this;
  }

  public String getQuoteOrderQty() {
    return quoteOrderQty;
  }

  public NewOrderr quoteOrderQty(String quoteOrderQty) {
    this.quoteOrderQty = quoteOrderQty;
    return this;
  }

  public String getPrice() {
    return price;
  }

  public NewOrderr price(String price) {
    this.price = price;
    return this;
  }

  public String getNewClientOrderId() {
    return newClientOrderId;
  }

  public NewOrderr newClientOrderId(String newClientOrderId) {
    this.newClientOrderId = newClientOrderId;
    return this;
  }

  public String getStopPrice() {
    return stopPrice;
  }

  public NewOrderr stopPrice(String stopPrice) {
    this.stopPrice = stopPrice;
    return this;
  }

  public String getStopLimitPrice() {
    return stopLimitPrice;
  }

  public NewOrderr stopLimitPrice(String stopLimitPrice) {
    this.stopLimitPrice = stopLimitPrice;
    return this;
  }

  public String getIcebergQty() {
    return icebergQty;
  }

  public NewOrderr icebergQty(String icebergQty) {
    this.icebergQty = icebergQty;
    return this;
  }

  public NewOrderResponseType getNewOrderRespType() {
    return newOrderRespType;
  }

  public NewOrderr newOrderRespType(NewOrderResponseType newOrderRespType) {
    this.newOrderRespType = newOrderRespType;
    return this;
  }

  public Long getRecvWindow() {
    return recvWindow;
  }

  public NewOrderr recvWindow(Long recvWindow) {
    this.recvWindow = recvWindow;
    return this;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public NewOrderr timestamp(long timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Places a MARKET buy order for the given <code>quantity</code>.
   *
   * @return a new order which is pre-configured with MARKET as the order type and BUY as the order side.
   */
  public static NewOrderr marketBuy(String symbol, String quantity) {
    return new NewOrderr(symbol, OrderSide.BUY, OrderType.MARKET, null, quantity);
  }

  /**
   * Places a MARKET sell order for the given <code>quantity</code>.
   *
   * @return a new order which is pre-configured with MARKET as the order type and SELL as the order side.
   */
  public static NewOrderr marketSell(String symbol, String quantity) {
    return new NewOrderr(symbol, OrderSide.SELL, OrderType.MARKET, null, quantity);
  }

  /**
   * Places a LIMIT buy order for the given <code>quantity</code> and <code>price</code>.
   *
   * @return a new order which is pre-configured with LIMIT as the order type and BUY as the order side.
   */
  public static NewOrderr limitBuy(String symbol, TimeInForce timeInForce, String quantity, String price) {
    return new NewOrderr(symbol, OrderSide.BUY, OrderType.LIMIT, timeInForce, quantity, price);
  }

  /**
   * Places a LIMIT sell order for the given <code>quantity</code> and <code>price</code>.
   *
   * @return a new order which is pre-configured with LIMIT as the order type and SELL as the order side.
   */
  public static NewOrderr limitSell(String symbol, TimeInForce timeInForce, String quantity, String price) {
    return new NewOrderr(symbol, OrderSide.SELL, OrderType.LIMIT, timeInForce, quantity, price);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)
            .append("symbol", symbol)
            .append("side", side)
            .append("type", type)
            .append("timeInForce", timeInForce)
            .append("quantity", quantity)
            .append("quoteOrderQty", quoteOrderQty)
            .append("price", price)
            .append("newClientOrderId", newClientOrderId)
            .append("stopPrice", stopPrice)
            .append("stopLimitPrice", stopLimitPrice)
            .append("icebergQty", icebergQty)
            .append("newOrderRespType", newOrderRespType)
            .append("recvWindow", recvWindow)
            .append("timestamp", timestamp)
            .toString();
  }
}
