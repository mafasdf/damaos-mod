/*
 * SimpBeliefBased.java
 *
 * Created on June 8, 2007, 11:56 AM
 *
 */

package TradingDemo;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author ynp, icg
 */
public class SimpBeliefBased implements TradingStyle
{
    public static int domainSize = -1;// from 0 to 1
    public int indexOfLastAction = -1;
    public float[] expectedReward = {};     // Array of Expected Rewards corresponding to each element in the actionDomain 
    public float reservationValue;          // Buyer or Seller's Reservation Price                                  
    public float maxValue;                  // Maximum ask price for Seller
    public AgentType type;                  // Records if the agent using this TradingStyle is a buyer or a seller
    public float actionDomain[];            // Contains the possible actions this TradingStyle can recommend
    public float k;
   
    RandomNumbers random;
         
    HashMap< Float, Integer > observationMap = new HashMap(); // float observed price, int # times observed
    
    ActionDomainParameters myAdp;
            
    public SimpBeliefBased( float val, ActionDomainParameters adp, int seed, AgentType at, float kParam )
    {               
        reservationValue = val;
        myAdp = adp;
        domainSize = adp.getDomainSize();
        type = at;
        actionDomain = new float[domainSize];
        expectedReward = new float[domainSize];
        k = kParam;
        
        for( int i = 0; i < domainSize; i++ )
        {
            //actionDomain[i] = ((float)i) / (domainSize - 1);     // Initialized the action domain for the agent
            actionDomain[i] = i*( (adp.getUpperBound() - adp.getLowerBound()) )/ (domainSize - 1) + adp.getLowerBound();
            actionDomain[i] = actionDomain[i] / 100;
        }
        
        random = new RandomNumbers( seed );
    }
    
    public SimpBeliefBased( int seed, ActionDomainParameters adp, AgentType at, float kParam )
    {
        this( -1, adp, seed, at, kParam );
    }
            
    public void calculateExpectedReward( )  // Calculates the Expected Reward for this agent
    {
        float profit;               // Profit to be received from one transaction
        
        if( reservationValue < 0 )
        {
            throw new RuntimeException("Value cannot be "+ reservationValue + " . It must be positive." );
        }
            
        // mapValues contains all the values of the HashMap which are the number of times each action was observed
        ArrayList<Integer> mapValues = new ArrayList(); 
        mapValues.addAll( observationMap.values() );
        
        // mapKeys contains all the keys of the HashMap which are the price of observed Action
        ArrayList<Float> mapKeys = new ArrayList();
        mapKeys.addAll( observationMap.keySet() );
       
        // The following loops calculate the profits for every combination of the actions in the action domain and 
        // the observed actions of the other agent, and record these profit values in the expectedReward array.      
        for( int i = 0; i < domainSize; i++ )
        {
            for( int j = 0; j < observationMap.size(); j++ )
            {
               if( AgentType.BUYER == type )    // For Buyer the profit from one transaction is the reservation price minus the transaction price
               {
                   // If the buyer's bid price is higher than the seller's ask price, then the trade will happen
                   // and there will be profit'
                   if( (actionDomain[i]*reservationValue) > mapKeys.get(j) )
                   {
                        profit = (float) ( reservationValue - k*( actionDomain[i]*reservationValue + mapKeys.get(j) ) );
                   }
                   else
                   {
                       profit = 0;
                   }
               }
               else if( AgentType.SELLER == type )  // For Seller the profit from one transaction is the transaction price minus the reservation price
               {
                   if( mapKeys.get(j) > (reservationValue + (maxValue - reservationValue)*actionDomain[i] )) 
                   {
                        profit = (float) ( k*( reservationValue + (maxValue - reservationValue)*actionDomain[i] + mapKeys.get(j) ) - reservationValue );
                   }
                   else
                   {
                       profit = 0;
                   }
               }
               else
               {
                   throw new RuntimeException("AgentType " + type + " behavior undefined");
               }
               
            // Safe because profits can only be positive
            expectedReward[i] += ( (mapValues.get(j) ) * profit );

            }
        }
    }
    
    
    // pickAction goes through the expectedReward array,
    // finds the index of the action that has the highest expected reward, 
    // and returns the action from the action domain
    public float pickAction()
    {
        // best is the index of the "best" action in the action domain
        // its initial reservationValue is randomized to a reservationValue >= 0 and <= size of the action domain.
        // Otherwise, in cases when the expected reward for all actions is zero, 
        // the action in the action domain at index zero would always be taken.
        // This way, each action has an opportunity to be selected in the case that
        // all expected rewards are zero.
        int best = (int)Math.round( random.next() * ( actionDomain.length - 1 ) );  
        int iterateUpOrDown = Math.round( random.next() );   //get a value between zero and one to determine if the array will 
                                                        // be iterated in ascending or descending order
        
        if( 0 == iterateUpOrDown )
        {
            for( int i = 0; i < expectedReward.length; i++ )
            {
                if ( expectedReward[ i ] > expectedReward[ best ])    // Search for the aciton with the highest expected reward
                {
                    best = i;
                }
            }
        }
        else
        {
            for ( int i = expectedReward.length -1; i > 0; i-- )
            {
               if ( expectedReward[ i ] > expectedReward[ best ])    // Search for the aciton with the highest expected reward
                {
                    best = i;
                }
            }
        }

        return actionDomain[ best ];
    }
    
    // Information from the Agent (buyer or seller) about what just happened with the 
    // Agent's trade
    public void setInfo( TransInfo TI )  
    {
        //update 
        int count = 1;              // default is one because the price observed has occurred at least one time
        float offerPrice = -1;
        if( AgentType.BUYER == type )
        {
            offerPrice = TI.mySellOffer;
        }
        else if( AgentType.SELLER == type )
        {
            offerPrice = TI.myBuyOffer;
        }
        else
        {
            throw new RuntimeException("Agent type " + type + " not supported.");
        }
                
        // update observations
        if( observationMap.containsKey( offerPrice ) )
        {
            count += observationMap.get( offerPrice );
        }        
        // The put operation overrides the existing entry for that key if it exists,
        // otherwise it creates a new entry
        observationMap.put( offerPrice, count );
        
        //learn 
        calculateExpectedReward();
    }
    
    // Retuns the SimpBeliefBased trading style's reccomened bid price to the Buyer agent
    public float getBidPrice()
    {      
        if( reservationValue <= 0 )
        {
            throw new RuntimeException("Value cannot be "+ reservationValue + " . It must be positive." );
        }
        
        if( AgentType.BUYER == type )
        {
            return pickAction() * reservationValue;
            //return (float) (reservationValue/100) * ((myAdp.getUpperBound() - myAdp.getLowerBound()) 
            //* pickAction() + myAdp.getLowerBound());
        }
        else
        {
            throw new RuntimeException("Operation getBidPrice() not supported for AgentType " + type );
        }
    }
    
    public float getBidPrice( int ID )
    {
        return this.getBidPrice();
    }
    
    public float getAskPrice()
    { 
        if( reservationValue < 0 )
        {
            throw new RuntimeException("Value cannot be "+ reservationValue + " . It must be positive." );
        }
        
        if( AgentType.SELLER == type )
        {
           // maxValue = maxSellerValue;
           return pickAction() * reservationValue + reservationValue; 
           // return (float) reservationValue + (reservationValue * (myAdp.getLowerBound() 
           // + pickAction() * (myAdp.getUpperBound() - myAdp.getLowerBound() ))) /100;
        }
        else
        {
            throw new RuntimeException("Operation getAskPrice() not supported for AgentType " + type );
        }
    }
    
    public float getAskPrice( int ID )
    {
        return this.getAskPrice();
    }
    
    public void setResPrice(float val)
    {
        reservationValue = val;
    }
    
    public void setAgentType( AgentType at )
    {
        type = at;
    }
            
}
