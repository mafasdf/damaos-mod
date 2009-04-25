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
		int z = Integer.parseInt(args[3]);
		
		float initialPropensity = z * 20;
		float recency = x / 40.0f;
		float experimentation = y / 40.0f;
		String fileName = String.format("%s_%d_%d_%d_.txt", prefix, x, y, z);
		
		SimSpecs specs = new SimSpecs();
		specs.addAgentSpecs(new AgentSpecs(AgentType.SELLER.toString(), "ModifiedRothErev", 1, 0, new ModRothErevParams(initialPropensity,
				experimentation, recency), 7, new ActionDomainParameters(10, 0, 200)));
		specs.addAgentSpecs(new AgentSpecs(AgentType.SELLER.toString(), "ZI", 5, 0, null, 7, new ActionDomainParameters(10, 0, 0)));
		specs.addAgentSpecs(new AgentSpecs(AgentType.BUYER.toString(), "ZI", 5, 0, null, 14, new ActionDomainParameters(10, 100, 100)));
		specs.addMarketSpecs(new MarketSpecs(777, //random seed
				"DiscriminatoryPriceKDoubleAuction",//market type
				5, //
				fileName, 50, 0.5f, //disable histograms
				new HistogramParameters[] {new HistogramParameters(false, 0, 0, 0.0f, '\0'), new HistogramParameters(false, 0, 0, 0.0f, '\0'),
						new HistogramParameters(false, 0, 0, 0.0f, '\0'), new HistogramParameters(false, 0, 0, 0.0f, '\0'),}));
		specs.setNumRounds(100);
		//disable additional graphs to completely disable GUI
		specs.setMarketSurplusGraphEnabled(false);
		specs.setMarketEfficiencyGraphEnabled(false);
		
		TradingWorld trader = new TradingWorld(specs);
		
		SimInit init = new SimInit();
		init.setExitOnExit(false);
		init.loadModel(trader, null, true);//true: run in non-GUI mode
		
	}
}
