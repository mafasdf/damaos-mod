/*
 * DiscPriceKDoubleAuction.java Created on June 22, 2007, 12:05 PM
 */

package TradingDemo;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * @author Yu Nanpeng, Ian Guffy
 */
public class DiscPriceKDoubleAuction implements MarketStyle
{
	
	float bidPrice;
	float askPrice;
	float transactionPrice;
	int maxNumberOfTrades;
	int buyerIndex;
	int sellerIndex;
	float kParam;
	RandomNumbers rand;
	boolean verboseOutput;
	
	DecimalFormat twoDigits = new DecimalFormat("0.00");
	
	/**
	 * Creates a new instance of DiscPriceKDoubleAuction
	 */
	
	public DiscPriceKDoubleAuction(int seed)
	{
		rand = new RandomNumbers(seed);
	}
	
	public DiscPriceKDoubleAuction(int maxTrades, int seed)
	{
		maxNumberOfTrades = maxTrades;
		rand = new RandomNumbers(seed);
	}
	
	public DiscPriceKDoubleAuction(MarketSpecs ms, int seed)
	{
		maxNumberOfTrades = ms.maxTradesPerRound;
		rand = new RandomNumbers(seed);
		kParam = ms.kParam;
	}
	
	public Stat Trade(Stat stats, BuyerAgent[] buyers, SellerAgent[] sellers, boolean verbose)
	{
		verboseOutput = verbose;
		int numPossibleTrades = Math.min(buyers.length, sellers.length);
		int numTrades = 0;
		for(int counter = 1; counter < maxNumberOfTrades; counter++)
		{
			if(numTrades == maxNumberOfTrades) break;//nothing more to do
				
			bidPrice = 0;
			askPrice = 0;
			
			// Pick a buyer and seller at random
			buyerIndex = rand.getRangedInt(0, buyers.length);
			sellerIndex = rand.getRangedInt(0, sellers.length);
			
			//efficiency shortcut: check if either of these traders have already been in a transaction so we can skip asking them
			if(buyers[buyerIndex].traded || sellers[sellerIndex].traded)
			{
				if(verboseOutput)
				{
					System.out.println("Could not trade: " + "; buyer's bid was " + twoDigits.format(bidPrice) + " while seller's asking price was "
							+ twoDigits.format(askPrice));
				}
				continue;
			}
			
			// buyer forms a bid price and is told which seller it is attempting to trade with
			bidPrice = buyers[buyerIndex].formBidPrice(sellers[sellerIndex].myID);
			// seller forms ask price, and is told which buyer it is attempting to trade with
			askPrice = sellers[sellerIndex].formAskPrice(buyers[buyerIndex].myID);
			
			if(bidPrice >= askPrice)
			{
				transactionPrice = (float) (askPrice + kParam * (bidPrice - askPrice));
				TransInfo TI = new TransInfo(buyers[buyerIndex].myID, sellers[sellerIndex].myID, bidPrice, askPrice, transactionPrice, true);
				
				// Return the transaction info to the buyer
				buyers[buyerIndex].updateMarketInfo(TI);
				buyers[buyerIndex].traded = true;
				
				// Return the transaction info to the seller
				sellers[sellerIndex].updateMarketInfo(TI);
				sellers[sellerIndex].traded = true;
				
				// Update the statistics (stats is a Stat object)
				stats.AddDatuum(transactionPrice);
				stats.AddMarkSurp(buyers[buyerIndex].getValue() - sellers[sellerIndex].getValue());
				
				float buyerSurplus = buyers[buyerIndex].getValue() - transactionPrice;
				stats.AddBuyerActSurp(buyerSurplus);
				stats.setIndividualBuyerMarketSurplus(buyerIndex, buyerSurplus);
				
				float sellerSurplus = transactionPrice - sellers[sellerIndex].getValue();
				stats.AddSellerActSurp(sellerSurplus);
				stats.setIndividualSellerMarketSurplus(sellerIndex, sellerSurplus);
				
				if(verboseOutput)
				{
					System.out.print("Found two agents willing to trade: ");
					System.out.println("Price of " + twoDigits.format(transactionPrice) + " with buyer's bid of " + twoDigits.format(bidPrice)
							+ " and seller asking price of " + twoDigits.format(askPrice));
				}
				numTrades++;
			}
			else
			{
				transactionPrice = -2;
				TransInfo TI = new TransInfo(buyers[buyerIndex].myID, sellers[sellerIndex].myID, bidPrice, askPrice, transactionPrice, false);
				buyers[buyerIndex].updateMarketInfo(TI);
				sellers[sellerIndex].updateMarketInfo(TI);
				
				if(verboseOutput)
				{
					System.out.print("Found two agents willing to trade: ");
					System.out.println("No Transaction" + " with buyer's bid of " + twoDigits.format(bidPrice) + " and seller asking price of "
							+ twoDigits.format(askPrice));
				}
			}
		}
		
		// Reset all buyers and sellers so that they can trade again in the next round
		for(int i = 0; i < (buyers.length); i++)
		{
			buyers[i].traded = false;
		}
		for(int i = 0; i < (sellers.length); i++)
		{
			sellers[i].traded = false;
		}
		
		return stats;
	}
	
}
