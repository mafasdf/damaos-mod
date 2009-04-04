/*
 * SellerOfferAscending.java
 *
 * Created on June 22, 2007, 4:09 PM
 */

package TradingDemo;

import java.util.Comparator;

/**
 *
 * @author Yu Nanpeng, Ian Guffy
 */

// This Comparator is implemented to allow SellerOffers to be sorted in ascending order (low to high)
public class SellerOfferAscending implements Comparator
{
    
    /** Creates a new instance of BuyerOfferDescending */
    public SellerOfferAscending() 
    {}

    public int compare(Object o1, Object o2) 
    {
       SellerAgent s1 = (SellerAgent) o1;
       SellerAgent s2 = (SellerAgent) o2;

       return Float.compare( s1.offer, s2.offer ); 
    }
    
}

    

