package TradingDemo;
/*
 * TradingStyle.java
 *
 * Created on June 8, 2007, 11:37 AM
 *
 */

/**
 *
 * @author ynp, icg
 */
public interface TradingStyle 
{
    float value = -1;
    
    float getBidPrice();
    float getAskPrice();

    float getBidPrice( int i );
    float getAskPrice( int i );
    
    void setInfo( TransInfo ti );
    
    void setResPrice( float val );
    void setAgentType( AgentType a );
    
}
