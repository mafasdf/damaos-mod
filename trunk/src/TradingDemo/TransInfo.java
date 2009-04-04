/*
 * TransInfo.java
 *
 * Created on June 8, 2007, 12:33 PM
 *
 */

package TradingDemo;

/**
 *
 * @author ynp, icg
 */
public class TransInfo {
    
    int myBuyerID;
    int mySellerID; 
    float myBuyOffer; 
    float mySellOffer;
    float myTransPrice; 
    boolean myTraded;
    
    /** Creates a new instance of TransInfo */
    public TransInfo( int buyerID, int sellerID, float buyOffer, float sellOffer, float transPrice, boolean traded ) 
    {
        myBuyerID = buyerID ;
        mySellerID = sellerID; 
        myBuyOffer = buyOffer; 
        mySellOffer = sellOffer;
        myTransPrice = transPrice; 
        myTraded = traded;
    }
    
}
