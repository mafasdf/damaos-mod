/*
 * SellerAgent.java
 *
 * Created on June 18, 2007, 10:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TradingDemo;
/**
 *
 * @author Yu Nanpeng
 */
public class SellerAgent {
    
    public static int ID_Number = 0;        // This counter is incremented each time a SellerAgent is created
    public final int myID;                  // Stores the unique ID number of the seller agent
    public boolean traded;                  // Keeps track if the SellerAgent has been able to trade
    protected final float reservationPrice;            // The reservation price of the seller
    private final float maximumSellerValue; // The maximum reservationPrice a seller can have
    TradingStyle tradeStyle;                // The trading style determines the actual behavior of this agent
    public float offer;                     // Once the SellerAgent has calculated its offer,
                                            // this variable allows the offer to be retrieved without making a new offer
    
    public final AgentType agentType = AgentType.SELLER;   
    public final int mySeed;
    private RandomNumbers rand;
    
    public SellerAgent( float maxSellerCost, float resPrice, TradingStyle ts, int seed )
    {
        mySeed = seed;
        rand = new RandomNumbers( mySeed );
        if( -1 >= resPrice )
        {
            reservationPrice = (float) (rand.next() * maxSellerCost);
        }
        else
        {
            reservationPrice = resPrice;
        }
        
        myID = ID_Number;   // Sets the unique id number of this agent
        ID_Number++;        // Increments the ID_Number counter for the next agent
        traded = false;     
        maximumSellerValue = maxSellerCost;
        tradeStyle = ts; 
        tradeStyle.setResPrice( reservationPrice );
        tradeStyle.setAgentType( AgentType.SELLER );
    }    
    
    public float formAskPrice( int ID ) 
    {
        offer = tradeStyle.getAskPrice( ID );
        return offer;
    }
  
    public void updateMarketInfo( TransInfo transactionInfo )
    {  
        tradeStyle.setInfo( transactionInfo );
    }
  
    // This public getter is needed to be able to compute max market surplus
    public float getValue()
    {
        return reservationPrice;   
    }
    
}
