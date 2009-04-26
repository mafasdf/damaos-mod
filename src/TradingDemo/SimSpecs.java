/*
 * SimSpecs.java
 *
 * Created on June 27, 2007, 3:51 PM
 *
 */

package TradingDemo;

import java.util.ArrayList;

/**
 *
 * @author ynp, icg
 */
public class SimSpecs {
    
    private ArrayList<AgentSpecs> agentSpecList;
    private MarketSpecs marketSpecs;
    
    private Integer[] tradingMarketSeeds;
	private int numRounds;
	
	private boolean marketSurplusGraphEnabled, marketEfficiencyGraphEnabled;
	private boolean conciseMode;
    
    /** Creates a new instance of SimSpecs */
    public SimSpecs() 
    {
        agentSpecList = new ArrayList();
        marketSpecs = null;
        tradingMarketSeeds = null;
        numRounds = -1;
        marketSurplusGraphEnabled = true;
        marketEfficiencyGraphEnabled = true;
    }

    public void addAgentSpecs( ArrayList<AgentSpecs> agentList ) 
    {
        agentSpecList.addAll( agentList );
    }
    
    public void addAgentSpecs( AgentSpecs as )
    {
        agentSpecList.add( as );
    }

    public void addMarketSpecs( MarketSpecs theMarketSpecs ) 
    {
        marketSpecs = theMarketSpecs;
        setTradingMarketSeedCapacity( marketSpecs.marketQuantity );
    }
    
    public void changeMarketSpecs( MarketSpecs theMarketSpecs )
    {
        marketSpecs = theMarketSpecs;
    }
    
    // Uses tmsList as the tradingMarketSeeds
    public void addTradingMarketSeeds( Integer[] tmsList ) 
    {
        tradingMarketSeeds = tmsList;
    }
    
    // Sets the capacity of the tradingMarketSeeds array
    private void setTradingMarketSeedCapacity( int c )
    {
        tradingMarketSeeds = new Integer[c];
    }
    
    // Adds one seed at a specific index
    public void addTradingMarketSeed( Integer seed, int index )
    {
        tradingMarketSeeds[index] = seed;
    }
    
    public void removeAgentSpecs( AgentSpecs as )
    {
        agentSpecList.remove( as );
    }
    
    public void removeAgentSpecs( int index )
    {
        agentSpecList.remove( index );
    }
    
    public AgentSpecs getAgentSpecsAt( int index )
    {
        return agentSpecList.get( index ) ;
    }
    
    public MarketSpecs getMarketSpecs()
    {
        return marketSpecs;
    }
    
    public int getAgentSpecsLength()
    {
        return agentSpecList.size();
    }
    
    public int getTradingMarketSeed( int index )
    {
        return tradingMarketSeeds[index];
    }

	public int getNumRounds()
	{
		return numRounds;
	}
	
	public void setNumRounds(int numRounds)
	{
		this.numRounds = numRounds;
	}

	public boolean isMarketSurplusGraphEnabled()
	{
		return marketSurplusGraphEnabled;
	}

	public void setMarketSurplusGraphEnabled(boolean marketSurplusGraphEnabled)
	{
		this.marketSurplusGraphEnabled = marketSurplusGraphEnabled;
	}

	public boolean isMarketEfficiencyGraphEnabled()
	{
		return marketEfficiencyGraphEnabled;
	}

	public void setMarketEfficiencyGraphEnabled(boolean marketEfficiencyGraphEnabled)
	{
		this.marketEfficiencyGraphEnabled = marketEfficiencyGraphEnabled;
	}

	public boolean isConciseMode()
	{
		return conciseMode;
	}
	
	public void setConciseMode(boolean conciseMode)
	{
		this.conciseMode = conciseMode;
	}

    
}
