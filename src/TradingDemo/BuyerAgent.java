/*
 * BuyerAgent.java
 *
 * Created on June 8, 2007, 10:57 AM
 *
 */

package TradingDemo;

/**
 *
 * @author ynp, icg
 */
public class BuyerAgent 
{
    public static int ID_Number = 0;        // This counter is incremented each time a BuyerAgent is created and provides a unique ID for each BuyerAgent
    public final int myID;                  // Stores the unique ID number of the buyer agent
    public boolean traded;                  // Keeps track if the BuyerAgent has been able to trade
    protected final float reservationPrice; // The reservation price of the buyer
    TradingStyle tradeStyle;                // The trading style determines the actual behavior of this agent
    public float offer;                     // Once the BuyerAgent has calculated its offer,
                                            // this variable allows the offer to be retrieved without making a new offer
    
    public final int mySeed;                // A seed for the BuyerAgent's random number generator
    public RandomNumbers rand;              // The BuyerAgent's random number generator
    
    public final AgentType agentType = AgentType.BUYER;   // AgentType set to BuyerAgent
    
    public BuyerAgent( float maxBuyerValue, float resPrice, TradingStyle ts, int seed )
    {
        mySeed = seed;
        rand = new RandomNumbers( mySeed );
        if( -1 >= resPrice )
        {
            reservationPrice = (float) ( rand.next() * maxBuyerValue );
        }
        else
        {
            reservationPrice = resPrice;
        }
        
        myID = ID_Number;   // Sets the unique id number of this agent
        ID_Number++;        // Increments the ID_Number counter for the next agent
        traded = false;
        
        tradeStyle = ts;
        tradeStyle.setResPrice( reservationPrice );
        tradeStyle.setAgentType( AgentType.BUYER );
    }
    
    // Called by TradingMarket to get a bid price from the buyer
    public float formBidPrice( int ID )
    {
        offer = tradeStyle.getBidPrice( ID ); // BuyerAgent defers to its tradeStyle to make bids
        return offer;
    }
    
    // Called by TradingMarket to inform buyer agent of the trade information
    public void updateMarketInfo( TransInfo transactionInfo )
    {
        tradeStyle.setInfo( transactionInfo );
    }
    
    // needed to compute max market surplus
    public float getValue()
    {
        return reservationPrice;
    }
    
    //public void setSeed()
    

}
