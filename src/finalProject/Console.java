package finalProject;

import java.util.ArrayList;

import uchicago.src.sim.engine.SimInit;
import TradingDemo.*;

public class Console
{
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String prefix = args[0];
		int x = Integer.parseInt(args[1]);
		int y = Integer.parseInt(args[2]);
		int z = Integer.parseInt(args[3]);//just used in filename
		
		float topInitialPropensity = Float.parseFloat(args[4]);
		int choiceWidth = Integer.parseInt(args[5]);
		int daysBetweenChange = Integer.parseInt(args[6]);
		float minRecency = Float.parseFloat(args[7]);
		float maxRecency = Float.parseFloat(args[8]);
		float minExp = Float.parseFloat(args[9]);
		float maxExp = Float.parseFloat(args[10]);

		float recency = x / 16.0f;//top
		float experimentation = y / 16.0f;//top
		float bottomInitialPropensity = 100;
		
		String fileName = String.format("%s_%d_%d_%d_.txt", prefix, x, y, z);
		
		HistogramParameters[] fourDisabledHistograms = new HistogramParameters[4];
		java.util.Arrays.fill(fourDisabledHistograms, new HistogramParameters(false, 0, 0, 0.0f, '\0'));
		
		SimSpecs specs = new SimSpecs();
		specs.addAgentSpecs(new AgentSpecs(AgentType.SELLER.toString(), "TwoLayerModRothErev", 1, //only one learner
				0, //max not used
				new MultiLayerMREParams(
							new ModRothErevParams( topInitialPropensity, experimentation, recency),
							new ModRothErevParams( bottomInitialPropensity, Float.NaN, Float.NaN)
						),
				50, //reserve price
				new ActionDomainParameters(6, 0, 200),//divide region [$50, $150] into 6 choices
				new ActionDomainParameters(choiceWidth, minRecency, maxRecency),//both recency and exp have same number of choices
				new ActionDomainParameters(choiceWidth, minExp, maxExp),//both recency and exp have same number of choices
				daysBetweenChange//from args
		));
		
		specs.addAgentSpecs(new AgentSpecs(AgentType.SELLER.toString(), "ZI", 4, //number of ZI sellers
				0, //not used
				null,//no learning params
				50,//reserve of 50
				new ActionDomainParameters(0, 0, 20)));//range: [$50, $60]

		
		specs.addAgentSpecs(new AgentSpecs(AgentType.BUYER.toString(), "ChangingZI", 5,0,null,100,new ActionDomainParameters(10, 90, 100)
		,new ActionDomainParameters(10, 70,80), 1250));//lower bids at day 1250
		
		
		
		
		specs.addMarketSpecs(new MarketSpecs(0x5f3759d5, //random seed (used to grow random fruit to get more random seeds)
				"DiscriminatoryPriceKDoubleAuction",//market type
				100, //number of batched runs for this setup (each with different RND seed)
				fileName,//filename generated above 
				100, //max number of trade attempts per day
				0.5f, //k value
				fourDisabledHistograms)); //disable histograms
		specs.setNumRounds(2500);
		//disable additional graphs to completely disable GUI
		specs.setMarketSurplusGraphEnabled(false);
		specs.setMarketEfficiencyGraphEnabled(false);
		specs.setConciseMode(true);
		
		TradingWorld trader = new TradingWorld(specs);
		
		SimInit init = new SimInit();
		init.setExitOnExit(false);
		init.loadModel(trader, null, true);//true: run in non-GUI mode
		
	}
}
