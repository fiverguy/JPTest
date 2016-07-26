/**
 * 
 */
package com.fiver.plus;

import java.math.BigDecimal;

/**
 * @author craig
 *
 */
public abstract class GlobalBeverageStock implements Comparable<GlobalBeverageStock> {

	private String stockSymbol;
	private int lastDividend;					// last dividend (pence)
	private BigDecimal parValue;				// par value (pence)
	private BigDecimal stockPrice;


	GlobalBeverageStock(String stockSymbol){
		this.stockSymbol = stockSymbol;
	}

	GlobalBeverageStock(String stockSymbol, int lastDividend, int parValue, String initialPrice){
		this(stockSymbol);
		this.lastDividend = lastDividend;
		this.parValue = new BigDecimal(parValue);
		
		
		setStockPrice(new BigDecimal(initialPrice));
	}

	public BigDecimal getPERatio(){
		
		BigDecimal per = getStockPrice().divide(new BigDecimal(getLastDividend()),2,BigDecimal.ROUND_HALF_DOWN);
		return per;
		
	}

	public BigDecimal getParValue() {
		return parValue;
	}

	public void setParValue(int parValue) {
		this.parValue = new BigDecimal(parValue);
	}

	public int getLastDividend() {
		return lastDividend;
	}

	public void setLastDividend(int lastDividend) {
		this.lastDividend = lastDividend;
	}

	public String getStockSymbol(){
		return stockSymbol;
	}

	public void setStockSymbol(String stock){
		stockSymbol = stock;
	}

	public int compareTo(GlobalBeverageStock gbStock){
		return(this.getStockSymbol().compareTo(gbStock.getStockSymbol()));
	}


	public BigDecimal getStockPrice() {
		return stockPrice;
	}

	public void setStockPrice(BigDecimal price) {
		this.stockPrice = price;
	}
	
	public abstract BigDecimal getDividendYield();
}
