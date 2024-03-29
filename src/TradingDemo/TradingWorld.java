/*
 * TradingWorld.java CSCI/ECON 0494 Middlebury College Fall 2004 Rob Axtell
 * Modified and updated by Yu Nanpeng and Ian Guffy
 */

package TradingDemo;

import java.util.*;
import java.io.*;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.ui.RefineryUtilities;

import uchicago.src.sim.analysis.Histogram;
import uchicago.src.sim.engine.AbstractGUIController;
import uchicago.src.sim.engine.ActionGroup;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.SimpleModel;

import java.awt.*;

public class TradingWorld extends SimpleModel
{
	public static final int NUMBER_OF_HISTOGRAMS = 4;
	
	private Histogram marketSurplusHist;
	private Histogram marketEfficiencyHist;
	private Histogram buyerMarketAdv;
	private Histogram sellerMarketAdv;
	
	public static String outputFileName = "Trader Demo Output" + ".csv";
	
	SimSpecs simSpec = null;
	public RandomNumbers masterSeedGen = null;
	
	DeviationRendererMarketSurplus MarketSurplusTimeSeries;
	DeviationRendererMarketEfficiency MarketEfficiencyTimeSeries;
	
	YIntervalSeries AvgMarketSurplusSeries = new YIntervalSeries("AvgMarketSupplusSeries");
	YIntervalSeries AvgMarketEfficiencySeries = new YIntervalSeries("AvgMarketEfficiencySeries");
	
	int clockTick = 1;
	
	public TradingWorld(SimSpecs ss)
	{
		simSpec = ss;
		masterSeedGen = new RandomNumbers(simSpec.getMarketSpecs().masterSeed);
		int s;
		for(int i = 0; i < simSpec.getMarketSpecs().marketQuantity; i++)
		{
			s = masterSeedGen.getRangedInt(0, Integer.MAX_VALUE);
			simSpec.addTradingMarketSeed(s, i);
		}
	}
	
	@Override
	public void setup()
	{
		super.setup();
		
		AbstractGUIController.UPDATE_PROBES = true; // remove?
		if(marketSurplusHist != null) marketSurplusHist.dispose();
		
		if(marketEfficiencyHist != null) marketEfficiencyHist.dispose();
		
		if(buyerMarketAdv != null) buyerMarketAdv.dispose();
		
		if(sellerMarketAdv != null) sellerMarketAdv.dispose();
	}
	
	@Override
	public void buildModel()
	{
		outputFileName = simSpec.getMarketSpecs().outputFile; // Assign output file name
		
		PrintStream jout = new PrintStream(System.out); //TODO = null instead?
		try
		{
			jout = new PrintStream(outputFileName);
		}
		catch(FileNotFoundException e)
		{
			System.err.print(e);
			System.exit(0);
		}
		
		System.setOut(jout);
		System.out.println(simSpec.getMarketSpecs().toStringCSV());
		System.out.println("\nMarket ID, Maximum Possible Market Suplus, CMC Price");
		
		for(int i = 0; i < simSpec.getMarketSpecs().marketQuantity; i++)
		{
			agentList.add(new TradingMarket(simSpec));
		}
		
		if(simSpec.isConciseMode())
		{
			System.out.println("\nPrimary Seller Profit");
		}
		else
		{
			System.out.println("\nTrade Round,Number of Transactions,Average Price,Market Surplus,Market Efficiency,"
				+ "Standard Deviation,Buyer Market Surplus,Seller Market Surplus,Buyer Market Advantages,Seller Market Advantages");
		}
		
		HistogramParameters[] hist = simSpec.getMarketSpecs().histParams;
		if(hist.length != NUMBER_OF_HISTOGRAMS)
		{
			throw new RuntimeException("Display options array is of the wrong size");
		}
		
		/*
		 * Attempt to use a loop for histogram creation String[] histNames =
		 * {"Market Surplus Histogram", "Market Efficiency Histogram", "Buyer
		 * Market Advantage Histogram", } for( int i = 0; i < 4; i++) { if(
		 * hist[i].isChecked() ) { marketSurplusHist = new Histogram("Market
		 * Surplus Histogram", hist[i].getNumBins(), hist[i].getLow(),
		 * hist[i].getHigh()) ; marketSurplusHist.createHistogramItem("Market
		 * surplus", agentList, "getSur"); marketSurplusHist.display(); } }
		 */

		if(hist[0].isChecked())
		{
			marketSurplusHist = new Histogram("Market Surplus Histogram", hist[0].getNumBins(), hist[0].getLow(), hist[0].getHigh());
			marketSurplusHist.createHistogramItem("Market surplus", agentList, "getSur");
			marketSurplusHist.display();
		}
		
		if(hist[1].isChecked())
		{
			marketEfficiencyHist = new Histogram("Market Efficiency Histogram", hist[1].getNumBins(), hist[1].getLow(), hist[1].getHigh());
			marketEfficiencyHist.createHistogramItem("Market Efficiency", agentList, "getMarkEff");
			marketEfficiencyHist.display();
		}
		if(hist[2].isChecked())
		{
			buyerMarketAdv = new Histogram("Buyer Market Advantage Histogram", hist[2].getNumBins(), hist[2].getLow(), hist[2].getHigh());
			buyerMarketAdv.createHistogramItem("Buyer Market Advantage", agentList, "getBuyerMarkAdv");
			buyerMarketAdv.display();
		}
		
		if(hist[3].isChecked())
		{
			sellerMarketAdv = new Histogram("Seller Market Advantage Histogram", hist[3].getNumBins(), hist[3].getLow(), hist[3].getHigh());
			sellerMarketAdv.createHistogramItem("Seller Market Advantage", agentList, "getSellerMarkAdv");
			sellerMarketAdv.display();
		}
	}
	
	@Override
	public void buildSchedule()
	{
		// The action to draw time series data of daily average market efficiency and average market surplus
		class DrawTimeSeries extends BasicAction
		{
			public void execute()
			{
				
				double sumSur = 0; // sum of daily market surplus 
				double sumOfSquares = 0; // sum of squares of daily market surplus
				double sumEff = 0; // sum of daily market efficiency
				double sumOfSquaresEff = 0; // sum of squares of daily market efficiency
				
				for(int i = 0; i < agentList.size(); i++)
				{
					TradingMarket tradingMarket = (TradingMarket) agentList.get(i);
					sumSur += tradingMarket.getSur();
					sumEff += tradingMarket.getMarkEff();
					sumOfSquares += tradingMarket.getSur() * tradingMarket.getSur();
					sumOfSquaresEff += tradingMarket.getMarkEff() * tradingMarket.getMarkEff();
				}
				double avgSur = sumSur / agentList.size(); // computes average daily market surplus of the simulation runs
				double avgEff = sumEff / agentList.size(); // computes average daily market efficiency of the simulation runs
				
				double devSurp = 0; // standard deviation of daily market surplus among the simulation runs
				double devEff = 0; // standard deviation of daily market efficiency among the simulation runs
				
				if(clockTick == 1)
				{
					devSurp = 0;
					devEff = 0;
				}
				else
				{
					double tempSur = sumOfSquares - agentList.size() * avgSur * avgSur;
					double tempEff = sumOfSquaresEff - agentList.size() * avgEff * avgEff;
					
					tempSur = tempSur / (agentList.size() - 1);
					tempEff = tempEff / (agentList.size() - 1);
					
					devSurp = Math.sqrt(tempSur);
					devEff = Math.sqrt(tempEff);
				}
				
				// add data point to average market surplus and market efficiency series
				AvgMarketSurplusSeries.add(clockTick, avgSur, avgSur - devSurp, avgSur + devSurp);
				AvgMarketEfficiencySeries.add(clockTick, avgEff, avgEff - devEff, avgEff + devEff);
				
				// If it is the first round, creat the two time series graphs
				if(clockTick == 1)
				{
					if(simSpec.isMarketSurplusGraphEnabled())
					{
						MarketSurplusTimeSeries = new DeviationRendererMarketSurplus("JFreeChart : DeviationRendererMarketSurplus",
								AvgMarketSurplusSeries);
						MarketSurplusTimeSeries.pack();
						RefineryUtilities.centerFrameOnScreen(MarketSurplusTimeSeries);
						MarketSurplusTimeSeries.setVisible(true);
					}
					
					if(simSpec.isMarketEfficiencyGraphEnabled())
					{
						MarketEfficiencyTimeSeries = new DeviationRendererMarketEfficiency("JFreeChart : DeviationRendererMarketEfficiency",
								AvgMarketEfficiencySeries);
						MarketEfficiencyTimeSeries.pack();
						RefineryUtilities.centerFrameOnScreen(MarketEfficiencyTimeSeries);
						MarketEfficiencyTimeSeries.setVisible(true);
					}
				}
				clockTick++;
				
			}
		}
		
		DrawTimeSeries drawTimeSeries = new DrawTimeSeries();
		
		ActionGroup TraderActions = new ActionGroup();
		try
		{
			TraderActions.createActionForEach(agentList, "trade");
		}
		catch(NoSuchMethodException e)
		{
			
			e.printStackTrace();
		}
		
		ActionGroup displayActions = new ActionGroup();
		
		// the Histogram class has a step method to update its display
		HistogramParameters[] disp = simSpec.getMarketSpecs().histParams;
		if(disp.length != NUMBER_OF_HISTOGRAMS)
		{
			throw new RuntimeException("Display options array is of the wrong size");
		}
		
		if(disp[0].isChecked())
		{
			displayActions.createActionFor(marketSurplusHist, "step");
		}
		if(disp[1].isChecked())
		{
			displayActions.createActionFor(marketEfficiencyHist, "step");
		}
		if(disp[2].isChecked())
		{
			displayActions.createActionFor(buyerMarketAdv, "step");
		}
		if(disp[3].isChecked())
		{
			displayActions.createActionFor(sellerMarketAdv, "step");
		}
		
		ActionGroup allActions = new ActionGroup();
		
		allActions.addAction(TraderActions); // add trading actions   
		allActions.addAction(drawTimeSeries); // add draw time series actions
		
		boolean[] allFalse = new boolean[disp.length];
		Arrays.fill(allFalse, false);
		if(!disp.equals(allFalse))
		{
			allActions.addAction(displayActions); // add display histogram actions
		}
		
		//add action to end after configurable number of rounds
		allActions.createActionFor(this, "checkEndCondition");
		
		schedule.scheduleActionAtInterval(1.0, allActions);
	}
	
	public void checkEndCondition()
	{
		//-1 is considered "infinity" (don't ever stop)
		if(simSpec.getNumRounds() != -1 && clockTick > simSpec.getNumRounds()) this.stop();
	}
}
