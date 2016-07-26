package com.fiver.plus;


import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Scanner;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;


public final class SuperSimpleStockMarketMain {

	public static final BigDecimal SCALE_FACTOR_98  = new BigDecimal("0.98");
	public static final BigDecimal SCALE_FACTOR_99  = new BigDecimal("0.99");	
	public static final BigDecimal SCALE_FACTOR_101 = new BigDecimal("1.01");	
	public static final BigDecimal SCALE_FACTOR_102 = new BigDecimal("1.02");
	
	public static GlobalBeverageStock[] stocks = new GlobalBeverageStock[5];

	public static void main(String[] args) {
		
		// so we can read values from keyboard
		Scanner scan = new Scanner(System.in);
		
		// Initialise
		TreeSet<GlobalBeverageStock> stockSet = new TreeSet<GlobalBeverageStock>();
		stockSet.add(new CommonGlobalBeverageStock("TEA", 0, 100, "157.91"));
		stockSet.add(new CommonGlobalBeverageStock("POP", 8, 100, "23.32"));
		stockSet.add(new CommonGlobalBeverageStock("ALE", 23, 60, "108.18"));
		stockSet.add(new PreferredGlobalBeverageStock("GIN", 8, 100, "2", "270.19"));
		stockSet.add(new CommonGlobalBeverageStock("JOE", 13, 250, "87.77"));
		
		HashSet<Trade> trades = new HashSet<Trade>();
		
		while(true){
		    GlobalBeverageStock stock = null;
		    String stockCode=null;
		    
			showActions();
			
			int choice = getAction(scan, 5);
			
			if (choice == 0){
				scan.close();
				System.out.println("Bye");
				System.exit(0);
			}
			
			// Get the stock code for the first 4 options only
			if (choice != 5){
			    showStocks(stockSet);
			    stockCode = getStockChoice(scan, stockSet);
			    stock = getStock(stockSet, stockCode);
			}
			
			if (choice == 1){
				BigDecimal pr = getPrice(scan);
				stock.setStockPrice(pr);
				BigDecimal divYield = stock.getDividendYield();
				System.out.println("Yield for stock " + stockCode + " is " + divYield.toPlainString());
			}
			else if (choice == 2){
				BigDecimal pr = getPrice(scan);
				stock.setStockPrice(pr);
				BigDecimal peRatio = stock.getPERatio();
				System.out.println("P/E Ratio for stock " + stockCode + " is " + peRatio.toPlainString());				
			}
			else if (choice == 3){
				boolean sell = getBuyOrSell(scan);
				Trade t = new Trade(sell,stockCode,"11", makeStockPrice(stock.getStockPrice(),	(int) (System.currentTimeMillis() % 1000)));
				trades.add(t);
			}
			else if (choice == 4){
				// Cleanup anything older than 15 minutes before doing the weighted calc
				cleanupTrades(trades);
				
				// check if any trades actually done
				BigDecimal weightedPrice = calculateVolumeWeightedStockPrice(stockCode, trades);
				if (weightedPrice != null){
					System.out.println("Volume Weighted Stock Price for stock code " + stockCode + " is " + weightedPrice);
				}
				else{
					System.out.println("Unable to calculate Volumue Weighted Stock Price for stock code " + stockCode + ".");
					System.out.println("Are you sure there were any trades for it in the last 15 minutes?");
				}
			}
			else if (choice == 5){
				BigDecimal shareIndex = calculateGBCEShareIndex(stockSet);
				System.out.println("The GBCE All Share Index is " + shareIndex);
			}
		}
	}

	private static BigDecimal calculateGBCEShareIndex(TreeSet<GlobalBeverageStock> stocks){
		BigDecimal shareIndex = new BigDecimal(0);
		for (GlobalBeverageStock stock : stocks) {
			shareIndex.add(stock.getStockPrice());
		}
		shareIndex = shareIndex.divide(new BigDecimal(stocks.size()));
		return shareIndex;
	}
	
	private static BigDecimal calculateVolumeWeightedStockPrice(String stock, HashSet<Trade> trades){
		BigDecimal result=new BigDecimal(0);
		BigDecimal summedTPxQ = new BigDecimal(0); // the cumulative sum of (traded price * quantity)
		BigDecimal summedQuantity = new BigDecimal(0);
		
	     Iterator<Trade> it = trades.iterator();
	     while(it.hasNext()){
	    	Trade t = it.next();
	    	if (t.getStockCode().compareToIgnoreCase(stock) == 0){
	    		BigDecimal d = t.getTradedPrice().multiply(t.getShareQuantity()).setScale(2, RoundingMode.UP);
	    		summedTPxQ.add(d);
	    		summedQuantity.add(t.getShareQuantity());
	    	}

	     }		
		
	    if (summedQuantity.compareTo(BigDecimal.ZERO) == 0){
	    	return null;
	    }
	    result = summedTPxQ.divide(summedQuantity, RoundingMode.HALF_DOWN);
		return result;
	}
	
	private static void cleanupTrades(HashSet<Trade> trades)
	{
		Calendar cal = Calendar.getInstance();
		long now = cal.getTime().getTime();
		long fifteen_mins_msecs = 1 * 60 * 1000; // TODO Change back to 15

		Iterator<Trade> ti = trades.iterator();
		while (ti.hasNext()) {
		    Trade t = ti.next();
		    if (now - t.geTradeTime() > fifteen_mins_msecs){
		        ti.remove();
		    }
		}		
	}
	
	// Spec says nothing about asking for the price, so generate one that is the  
	// current price +/- some percentage based on when we happen to hit the method
	private static BigDecimal makeStockPrice(BigDecimal currentPrice, int offset){

		BigDecimal newPrice = BigDecimal.ZERO;
		newPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
		MathContext mc = new MathContext(2, RoundingMode.HALF_UP);

		
		if (offset < 250){
			newPrice = currentPrice.multiply(SCALE_FACTOR_101, mc);
		}
		else if (offset < 500){
			newPrice = currentPrice.multiply(SCALE_FACTOR_102, mc);			
		}
		else if (offset < 750){
			newPrice = currentPrice.multiply(SCALE_FACTOR_99, mc);			
		}
		else{
			newPrice = currentPrice.multiply(SCALE_FACTOR_98, mc);
		}

		System.out.println("currentPrice=" + currentPrice.toPlainString() + " newPrice=" + newPrice.toPlainString());
		return newPrice;

	}
	
	private static boolean getBuyOrSell(Scanner scan){
		System.out.println("Enter B)uy or S)ell:");
		
	    while (!scan.hasNext("[BbSs]")) {
	        System.out.println("Enter 'B' for Buy, 'S' for Sell");
	        scan.next();
	    }
	    String option = scan.next();
	    
		if (option.compareToIgnoreCase("S") == 0){
			return true;
		}
		return false;
	}
	
	private static int showStocks(TreeSet<GlobalBeverageStock> set){
		int stockNo=1;
		System.out.println("Please enter one of the following stock codes:");
		for (GlobalBeverageStock globalStock : set) {
			System.out.println(globalStock.getStockSymbol());
			stockNo++;
		}		
		return(stockNo-1);
	}
	
	private static GlobalBeverageStock getStock(TreeSet<GlobalBeverageStock> set, String stockCode){
		for (GlobalBeverageStock globalStock : set) {
			if (stockCode.compareToIgnoreCase(globalStock.getStockSymbol()) == 0){
				return globalStock;
			}
		}
		return null;
	}
	
	private static BigDecimal getPrice(Scanner scan){
		System.out.println("Enter Price: ");
		while(true){
			try{
				String price = scan.next();
				BigDecimal bdPrice = new BigDecimal(price);
				return(bdPrice);
			}
			catch (Exception e){
				System.out.println("Enter a valid price");
			}
				
		}
	}
	
	private static String getStockChoice(Scanner scan, TreeSet<GlobalBeverageStock> set){
		String stock;		
		stock=scan.next();
		return(stock);
	}
	
	private static int getAction(Scanner scan, int highestValid){
		
		int choice = -1;
		
		System.out.println("");
		System.out.println("Enter a number between 1 and " + highestValid + " (or 0 to quit)");

		while (choice < 0 || choice > highestValid){
			// We are after a number, so skip anything that isn't
			while (!scan.hasNextInt()) {
				scan.nextLine();
			}
			choice = scan.nextInt();
		}

		return choice;
	}
	
	private static void showActions(){
		System.out.println("");
		System.out.println("Please choose from one of the following options:");
		System.out.println("");
		System.out.println("1. Calculate dividend yield for a stock");
		System.out.println("2. Calculate P/E ratio for a stock");
		System.out.println("3. Trade (buy/sell) a stock");	
		System.out.println("4. Show Volume Weighted Price for a stock");
		System.out.println("5. Show All Share Index");
	}
}