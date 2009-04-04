/*
 * ZI.java
 *
 * Created on June 8, 2007, 11:53 AM
 *
 */

package TradingDemo;

/**
 *
 * @author ynp, icg
 */
public class ZI implements TradingStyle 
{
    private float reservationValue;
    AgentType type;       
    RandomNumbers random;
    ActionDomainParameters myAdp;
    /** Creates a new instance of ZI */ 

    public ZI( float val, ActionDomainParameters adp, int seed, AgentType at  )
    {
        reservationValue = val;
        myAdp = adp;
        type = at;
        random = new RandomNumbers( seed );
    }
    
    public ZI( int seed, ActionDomainParameters adp, AgentType t )
    {
        this( -1, adp, seed, t );
    }
     
    public void setInfo(  TransInfo ti )
    {
        // ZI trader doesn'at care about the rest of the market
        // therefore, do nothing
    }
    
    public float getBidPrice( )
    {
        if( reservationValue < 0 )
        {
            throw new RuntimeException("Value cannot be "+ reservationValue + " . It must be positive." );
        }
             
        if( type == AgentType.BUYER )
        {
            //return (float) (random.next() * reservationValue); 
            return (float) (reservationValue/100) * ((myAdp.getUpperBound() 
            - myAdp.getLowerBound())*random.next() + myAdp.getLowerBound());
        }
        else
        {
            throw new RuntimeException("AgentType " + type + " cannot use method getBidPrice() ");
        }
    }  
    
    public float getBidPrice( int ID )
    {   // ZI trader does not care about the ID of who it trades with
        return this.getBidPrice();
    }
    
    public float getAskPrice()
    {
        if( reservationValue < 0 )
        {
            throw new RuntimeException("Value cannot be "+ reservationValue + " . It must be positive." );
        }
        
        if( type == AgentType.SELLER )
        {
            //return (float) ( reservationValue + (maxSellerValue - reservationValue)*random.next() );
            return (float) reservationValue + (reservationValue * (myAdp.getLowerBound() 
            + random.next() * (myAdp.getUpperBound() - myAdp.getLowerBound() ))) /100;
        }
        else
        {
            throw new RuntimeException("AgentType " + type + " cannot use method getAskPrice() ");
        } 
    }
    
    public float getAskPrice( int ID )
    {
        return this.getAskPrice();
    }
    
    public void setResPrice( float val )
    {
        reservationValue = val;
    }
    
    public void setAgentType( AgentType at )
    {
        type = at;
    }
}

