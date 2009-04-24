package finalProject;

import uchicago.src.sim.engine.SimInit;
import TradingDemo.*;

public class Console
{
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		SimSpecs specs = new SimSpecs();
		specs.addAgentSpecs(new AgentSpecs(AgentType.SELLER.toString(), "ZI", 5, 0, null, 7, new ActionDomainParameters(10,0, 100)));
		specs.addAgentSpecs(new AgentSpecs(AgentType.BUYER.toString(), "ZI", 5, 0, null, 14, new ActionDomainParameters(10,0, 100)));		
		specs.addMarketSpecs(new MarketSpecs(777, "DiscriminatoryPriceKDoubleAuction", 200, "hullowurld.txt", 5000, 0.5f, new HistogramParameters[]{
				new HistogramParameters(false, 0, 0, 0.0f, '\0'),
				new HistogramParameters(false, 0, 0, 0.0f, '\0'),
				new HistogramParameters(false, 0, 0, 0.0f, '\0'),
				new HistogramParameters(false, 0, 0, 0.0f, '\0'),
		}));
		specs.setNumRounds(100);
		//disable additional graphs
		specs.setMarketSurplusGraphEnabled(false);
		specs.setMarketEfficiencyGraphEnabled(false);
		
		TradingWorld trader = new TradingWorld(specs); 
        
        SimInit init = new SimInit();
        init.loadModel(trader, null, true);//true: run in non-GUI mode
        trader.begin();
	}
	
}
