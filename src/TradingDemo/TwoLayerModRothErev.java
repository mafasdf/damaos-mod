package TradingDemo;

public class TwoLayerModRothErev implements TradingStyle
{
	//The MRE logic for choosing parameters for the bottom layer
	private ModRothErev topMRELayer;
	
	//The MRE logic that actually chooses prices
	private ModRothErev bottomMRELayer;
	
	//The top layer is not used every turn, but waits for a certain interval period to change the parameters of the bottom layer
	private int daysBetweenChange;
	//to keep track of when to change bottom layer parameters
	private DayCounter dayCounter;
	private int dayLastChanged;
	//metric to use as "reward" for top layer
	private float profitSinceLastChange = 0;
	
	private ActionDomainParameters recencyDomainParams;
	private ActionDomainParameters experimentationDomainParams;
	
	public TwoLayerModRothErev(float reservationValue, ActionDomainParameters bottomDomainParams, int seed, MultiLayerMREParams theMREParams, AgentType at, ActionDomainParameters recencyDomain, ActionDomainParameters experimentationDomain,
			int daysBetweenChange, DayCounter dc)
	{
		//get two seeds
		RandomNumbers random = new RandomNumbers(seed);
		int topSeed = random.getRangedInt(0, Integer.MAX_VALUE);
		int bottomSeed = random.getRangedInt(0, Integer.MAX_VALUE);
		
		//Set up top layer
		//NaN: min & max not used in top layer, only index
		ActionDomainParameters topLayerDomain = new ActionDomainParameters(recencyDomain.getDomainSize() * experimentationDomain.getDomainSize(),
				Float.NaN, Float.NaN);
		//reservation value and agent type not used in top layer
		topMRELayer = new ModRothErev(Float.NaN, topLayerDomain, topSeed, theMREParams.get(0), null);
		
		//only initial propensity is needed from bottomMREParams
		bottomMRELayer = new ModRothErev(reservationValue, bottomDomainParams, bottomSeed, theMREParams.get(1), at);
		
		recencyDomainParams = recencyDomain;
		experimentationDomainParams = experimentationDomain;
		
		dayCounter = dc;
		this.daysBetweenChange = daysBetweenChange;
		dayLastChanged = -daysBetweenChange - 1;//set to value such that change is guaranteed on first round. 
	}
	
	public float getAskPrice()
	{
		changeIfNeeded();
		return bottomMRELayer.getAskPrice();
	}
	
	public float getAskPrice(int i)
	{
		return getAskPrice();
	}
	
	public float getBidPrice()
	{
		changeIfNeeded();
		return bottomMRELayer.getBidPrice();
	}
	
	public float getBidPrice(int i)
	{
		return getBidPrice();
	}
	
	public void setAgentType(AgentType a)
	{
		bottomMRELayer.setAgentType(a);
	}
	
	public void setInfo(TransInfo ti)
	{
		changeIfNeeded();
		bottomMRELayer.setInfo(ti);
		profitSinceLastChange += bottomMRELayer.previousReward;//assumes bottomMRELayer.setInfo will update reward 
	}
	
	public void setResPrice(float val)
	{
		bottomMRELayer.reservationValue = val;
	}
	
	private void changeIfNeeded()
	{
		if(dayCounter.getValue() - dayLastChanged < daysBetweenChange) return;//nothing to do
			
		//update top layer (like setInfo), ONLY IF NOT FIRST TIME
		if(dayLastChanged != Integer.MIN_VALUE)
		{
			topMRELayer.previousReward = profitSinceLastChange;
			topMRELayer.updatePropensity();
		}
		
		int topAction = topMRELayer.pickActionIndex();
		
		//1:1 mapping from action value to experimentation & recency values:
		int recencyAction = topAction % recencyDomainParams.getDomainSize();
		int experimentationAction = topAction / recencyDomainParams.getDomainSize();
		
		//change discrete value to main continuous ones
		bottomMRELayer.phi = recencyAction * (recencyDomainParams.getUpperBound() - recencyDomainParams.getLowerBound())
				/ (recencyDomainParams.getDomainSize() - 1) + recencyDomainParams.getLowerBound();
		bottomMRELayer.epsilon = experimentationAction * (experimentationDomainParams.getUpperBound() - experimentationDomainParams.getLowerBound())
				/ (experimentationDomainParams.getDomainSize() - 1) + experimentationDomainParams.getLowerBound();
		
		//reset
		dayLastChanged = dayCounter.getValue();
		profitSinceLastChange = 0;
	}
}
