/*
 * MarketStyle.java
 *
 * Created on June 22, 2007, 11:57 AM
 *
 */

package TradingDemo;

/**
 *
 * @author Yu Nanpeng, Ian Guffy
 */

// The MarketStyle interface is used by the different market styles, and require
// a single Trade function to be implemented.
public interface MarketStyle 
{
    
    /** Creates a new instance of MarketStyle */
    
    public Stat Trade( Stat s, BuyerAgent[] buyers, SellerAgent[] sellers, boolean verb );
    
}
