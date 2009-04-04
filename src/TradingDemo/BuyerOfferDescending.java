/*
 * BuyerOfferDescending.java
 *
 * Created on June 22, 2007, 3:49 PM
 */

package TradingDemo;

import java.util.Comparator;

/**
 * This class is an instance of Comparator that is used to define a way of 
 * sorting BuyerAgent's offers in Descending (highest to lowest) order.
 * @author Yu Nanpeng, Ian Guffy
 */
public class BuyerOfferDescending implements Comparator{
    
    /** Creates a new instance of BuyerOfferDescending */
    public BuyerOfferDescending() {
    }

    public int compare(Object o1, Object o2) 
    {
       BuyerAgent b1 = (BuyerAgent) o1;
       BuyerAgent b2 = (BuyerAgent) o2;

       return (-1) * Float.compare( b1.offer, b2.offer ); // -1 used to reverse the ordering, 
       // so instead of sorting ascending, things will be sorted descending
    }
    
}
