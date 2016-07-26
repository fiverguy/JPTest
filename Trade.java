package com.fiver.plus;

import java.math.BigDecimal;
import java.util.Calendar;

public class Trade {
	
	private boolean bSale;
	private String stockCode;
	private BigDecimal tradedPrice;
	private long tradeTime;
	private BigDecimal shareQuantity;
	
	Trade(boolean isSale, String stockCode, String quantity, BigDecimal currentPrice){
		this.bSale = isSale;
		this.stockCode = stockCode;
		setShareQuantity(quantity);
		this.tradedPrice = currentPrice;
		setTradeTime();
	}
	
	public String getStockCode(){
		return stockCode;
	}
	
	public BigDecimal getShareQuantity() {
		return shareQuantity;
	}

	public void setShareQuantity(String shareQuantity) {
		if (shareQuantity == null){
			this.shareQuantity = BigDecimal.ZERO;
			return;
		}
		this.shareQuantity = new BigDecimal(shareQuantity);
	}

	public BigDecimal getTradedPrice() {
		return tradedPrice;
	}

	public long geTradeTime() {
		return tradeTime;
	}

	public void setTradeTime() {
		Calendar cal = Calendar.getInstance();
		this.tradeTime = cal.getTime().getTime();  // milliseconds since the epoch		
	}

	public boolean isSale() {
		return bSale;
	}

	
}
