/*
 * AgentSpecs.java Created on June 26, 2007, 2:35 PM
 */

package TradingDemo;

/**
 * AgentSpecs is a container class that holds all of the specifications for
 * creating a group of agents.
 * 
 * @author ynp
 */
public class AgentSpecs
{
	String myAgentType;
	String myTradeStyleType;
	int myQuantity;
	float myReservationPrice = -1;
	float maxSellerPriceOrBuyerCost;
	LearningStyleParameters learningStyleParameters;
	ActionDomainParameters actionDomainParameters;
	
	//for changing ZI
	ActionDomainParameters actionDomainSecondaryParameters;
	public int changeDay = -1;
	
	//for 2-layer MRE
	ActionDomainParameters recencyDomainParameters;
	ActionDomainParameters experimentationDomainParameters;
	int daysBetweenChanges = -1;
	
	
	public AgentSpecs(String agentType, String tradeStyleType, int quantity, float maxBuySellPOrBuyerC, LearningStyleParameters lsp,
			float reservationPrice, ActionDomainParameters actDomParams)
	{
		myAgentType = agentType;
		myTradeStyleType = tradeStyleType;
		myQuantity = quantity;
		maxSellerPriceOrBuyerCost = maxBuySellPOrBuyerC;
		learningStyleParameters = lsp;
		
		myReservationPrice = reservationPrice;
		
		actionDomainParameters = actDomParams;
		
	}
	
	/**
	 * For ChangingZI.
	 */
	public AgentSpecs(String agentType, String tradeStyleType, int quantity, float maxBuySellPOrBuyerC, LearningStyleParameters lsp,
			float reservationPrice, ActionDomainParameters actDomParams, ActionDomainParameters secondaryADP, int dayToChange)
	{
		this(agentType, tradeStyleType, quantity, maxBuySellPOrBuyerC, lsp, reservationPrice, actDomParams);
		actionDomainSecondaryParameters = secondaryADP;
		changeDay = dayToChange;
	}
	
	/**
	 * For 2-layer MRE
	 */
	public AgentSpecs(String agentType, String tradeStyleType, int quantity, float maxBuySellPOrBuyerC, LearningStyleParameters lsp,
			float reservationPrice, ActionDomainParameters actDomParams, ActionDomainParameters recencyDomain, ActionDomainParameters experimentationDomain, int daysBetweenChanges)
	{
		this(agentType, tradeStyleType, quantity, maxBuySellPOrBuyerC, lsp, reservationPrice, actDomParams);
		recencyDomainParameters = recencyDomain;
		experimentationDomainParameters = experimentationDomain;
		this.daysBetweenChanges = daysBetweenChanges;
	}
	
	/*
	 * toString() is formatted this way because it is used when saving settings
	 * to a file
	 */
	public String toString()
	{
		String result = "@" + "AT:" + myAgentType + ",TS:" + myTradeStyleType + ",QT:" + myQuantity + ",RP:" + myReservationPrice + ",MX:"
				+ maxSellerPriceOrBuyerCost + ",DS:" + actionDomainParameters.getDomainSize() + ",LB:" + actionDomainParameters.getLowerBound()
				+ ",UB:" + actionDomainParameters.getUpperBound();
		
		if(null != learningStyleParameters)
		{
			result += "," + learningStyleParameters.toString();
		}
		return result;
	}
	
}
