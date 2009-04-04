/*
 * SimpBeliefBasedID.java
 *
 * Created on June 8, 2007, 11:57 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TradingDemo;

/**
 *
 * @author ynp, icg
 */
public class SimpBeliefBasedID implements TradingStyle {
    
    // This algorithm has not yet been implemented completely.
            
    public SimpBeliefBasedID( float val, AgentType at )
    {
        //value = val;
        //type = at;
    }
       
    public SimpBeliefBasedID( AgentType at )
    {
        this( -1, at );
    }
    
    public void setInfo( TransInfo ti){}
    
    public float getBidPrice( int ID ){ return -1; }
    
    public float getAskPrice( float f, int ID ){ return -1; }
    
    public void setResPrice(float val)
    {
        //value = val;
    }

    public void setAgentType(AgentType a)
    {
    }

    public float getBidPrice()
    {
        return -1;
    }

    public float getAskPrice()
    {
        return -1;
    }

    public float getAskPrice(int i)
    {
        return -1;
    }
    
}
