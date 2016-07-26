package com.fiver.plus;

import java.math.BigDecimal;

public class PreferredGlobalBeverageStock extends GlobalBeverageStock {
	
	private BigDecimal fixedDividendPct;


	PreferredGlobalBeverageStock(String stockSymbol, int lastDividend, int parValue, String fixedDividendPct, String initialPrice) {
		super(stockSymbol, lastDividend, parValue, initialPrice);
		setFixedDividendPct(fixedDividendPct);
		// TODO Auto-generated constructor stub
	}

	@Override
	public BigDecimal getDividendYield() {
		BigDecimal dividend = getFixedDividendPct().multiply(getParValue());
		BigDecimal price = getStockPrice();
		BigDecimal lastDiv = dividend.divide(price,2, BigDecimal.ROUND_HALF_UP);
		return lastDiv;
	}
	
	public BigDecimal getFixedDividendPct() {
		return fixedDividendPct;
	}

	public void setFixedDividendPct(String fixedDividendPct) {
		if (fixedDividendPct == null){
			this.fixedDividendPct = BigDecimal.ZERO;
			return;
		}		
		this.fixedDividendPct = new BigDecimal(fixedDividendPct);
	}
}
