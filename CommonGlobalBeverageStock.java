package com.fiver.plus;

import java.math.BigDecimal;

public class CommonGlobalBeverageStock extends GlobalBeverageStock {

	CommonGlobalBeverageStock(String stockSymbol, int lastDividend, int parValue, String initialPrice) {
		super(stockSymbol, lastDividend, parValue, initialPrice);
	}

	@Override
	public BigDecimal getDividendYield() {
		BigDecimal lastDividend = new BigDecimal(getLastDividend());
		BigDecimal price = getStockPrice();
		BigDecimal divYield = lastDividend.divide(price,2, BigDecimal.ROUND_HALF_UP);
		return divYield;
	}


}
