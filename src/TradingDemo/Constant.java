/*
 * Constant.java
 *
 * Created on June 19, 2007, 4:35 PM
 *
 */

package TradingDemo;

/**
 *
 * @author Yu Nanpeng, Ian Guffy
 */

// Used to test the performance other trade styles
// Always uses the same Bid or Ask price
public class Constant implements TradingStyle 
{
    private float myReservationPrice;
    private float bidAskPrice;
    AgentType type; 
            
    /** Creates a new instance of Constant */

    public Constant( float resPrice, ConstantLSParams clsp, AgentType t  )
    {
        myReservationPrice = resPrice;
        if( null != clsp )
        {
            bidAskPrice = clsp.getValue();
        }
        else
        {
            bidAskPrice = myReservationPrice;
        }
    }
    
    public void setInfo(TransInfo ti)
    {
        // Const trader doesn't care about the rest of the market
        // therefore, do nothing
    }    
    
    public float getBidPrice()
    {
        return bidAskPrice;
    }  
    
    public float getAskPrice()
    {
        return bidAskPrice;
    } 
        
    public float getBidPrice( int id )
    {
        return this.getBidPrice();
    }    
    
    public float getAskPrice( int id )
    {
       return this.getAskPrice();
    }
    
    public void setResPrice( float val )
    {
        myReservationPrice = val;
    }
    
    public void setAgentType( AgentType at )
    {
        type = at;
    }
    
}

