/*
 * UniformPriceAuction.java
 *
 * Created on June 22, 2007, 2:37 PM
 *
 */

package TradingDemo;

import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author Yu Nanpeng, Ian Guffy
 */
public class UniformPriceAuction implements MarketStyle
{
    float buyerOffers[];
    float sellerOffers[];
    float clearingPrice = -1;
    float kParameter = -1; // used to determine market clearing price
                            
    TransInfo TI;
    public final int BUYER_ID = -1;
    public final int SELLER_ID = -2;
    
    /** Creates a new instance of UniformPriceAuction */
    public UniformPriceAuction()
    {}
    
    public UniformPriceAuction( MarketSpecs marketSpecs, int seed )
    {
        // The value of the k-parameter, which is used to calculate the market clearing price, is the 
        // only user-selectable parameter in UniformPriceAuction. Random numbers are never used,
        // so seed is ignored.
        
        kParameter = marketSpecs.kParam; 
    }
    
    public UniformPriceAuction( float mult )
    {       
        kParameter = mult;  
        // K value currently not user-selectable, so currently unused
    }
    
    public Stat Trade( Stat s, BuyerAgent[] buyers, SellerAgent[] sellers, boolean verb ) 
    {
        // Because trades only occur if the BuyerAgent's offer is greater than the 
        // SellerAgent's, then we assume that the BuyerAgent's offer is always bigger
        
        clearingPrice = -1;
        buyerOffers = new float[ buyers.length ];
        sellerOffers = new float[ sellers.length ];
        
        // pick action
        for( int i = 0; i < buyers.length; i++ )
        {
            buyerOffers[i] = buyers[i].formBidPrice( SELLER_ID );
        }
        
        for( int i = 0; i < sellers.length; i++ )
        {
            sellerOffers[i] = sellers[i].formAskPrice( BUYER_ID );
        }
             
        // sort bids/asks
        Arrays.sort( buyerOffers );
        Arrays.sort( sellerOffers );
        
        int cmcIndex = 0;
        int r = buyerOffers.length -1;
        for(int i = 0; i < buyerOffers.length && i < sellerOffers.length && clearingPrice == -1; i++)
        {
            
            if(buyerOffers[r - i] <= sellerOffers[i] )
            {
                if(buyerOffers[r - i] == sellerOffers[i])
                {
                    clearingPrice = buyerOffers[r - i];
                }
                
                else if(i == 0)
                { clearingPrice = 0; }
                
                else
                {
                    if(buyerOffers[r - i + 1] == buyerOffers[r - i] )
                    { clearingPrice = buyerOffers[r - i]; }
                    
                    else if (sellerOffers[i - 1] == sellerOffers[i])
                    { clearingPrice = sellerOffers[i]; }
                    
                    else
                    { 
                        //clearingPrice = (buyerOffers[r - i] + sellerOffers[i]) /2; 
                        clearingPrice = sellerOffers[i] + (buyerOffers[r - i] - sellerOffers[i]) * kParameter; 
                    }
                }
            }
        
            if( (i == (buyerOffers.length - 1) || i == (sellerOffers.length - 1) ) // if last iteration of the loop
                    && -1 == clearingPrice ) // if no cmcPoints
            {
                if( buyerOffers.length == sellerOffers.length)
                { //clearingPrice = (buyerOffers[0] + sellerOffers[ sellerOffers.length - 1 ]) / 2; 
                  clearingPrice = sellerOffers[ sellerOffers.length - 1 ] 
                          + (buyerOffers[0] - sellerOffers[ sellerOffers.length - 1 ] ) * kParameter;
                }

                else if( buyerOffers.length > sellerOffers.length)
                { 
                    if(buyerOffers[r - i] == buyerOffers[r - i - 1])
                    { clearingPrice = buyerOffers[r - i]; }
                    else 
                    { 
                        if( buyerOffers[r - i - 1] < sellerOffers[i]) // i+1 is safe for buyerOffers because buyerOffers > sellerOffers  
                        {  // clearingPrice = (buyerOffers[r - i] + sellerOffers[i])/2; 
                           clearingPrice = sellerOffers[i] + (buyerOffers[r - i] - sellerOffers[i]) * kParameter;
                        }
                        // if buyer i+1's price is higher than the last seller's price
                        else
                        { // clearingPrice = (buyerOffers[r - i] + buyerOffers[r - i - 1]) / 2;
                            clearingPrice = buyerOffers[r - i - 1] 
                                    + ( buyerOffers[r - i] + buyerOffers[r - i - 1]) * kParameter; }
                    }
                }
                // more sellers than buyers
                else
                {    
                     if( sellerOffers[i] == sellerOffers[i + 1] )
                    { clearingPrice = sellerOffers[i]; }
                    else if( sellerOffers[ i + 1 ] > buyerOffers[r - i] )
                    {   //clearingPrice = (buyerOffers[r - i] + sellerOffers[i])/2;
                         clearingPrice = sellerOffers[i] +  (buyerOffers[r - i] - sellerOffers[i]) * kParameter; }
                    else // seller i + 1's price is loser than buyer i+1's price
                    {    //clearingPrice = (sellerOffers[ i ] + sellerOffers[i + 1]) / 2;
                         clearingPrice = sellerOffers[ i ] + (sellerOffers[i + 1] - sellerOffers[ i ]) * kParameter; }
                }
            }
            cmcIndex = i - 1;
        }
        // set info    
        for( int i = 0; i < buyerOffers.length; i++)
        {
            if( buyers[i].offer >= clearingPrice && clearingPrice > 0)
            {
                TI = new TransInfo( BUYER_ID, SELLER_ID, buyers[i].offer, clearingPrice, clearingPrice, true );
            }
            else
            {
                TI = new TransInfo( BUYER_ID, SELLER_ID, buyers[i].offer, clearingPrice, clearingPrice, false );
            }
            
            buyers[i].updateMarketInfo( TI );
        }
        
        for( int i = 0; i < sellerOffers.length; i++)
        {
            if( sellers[i].offer <= clearingPrice && clearingPrice > 0 )
            {
                TI = new TransInfo( BUYER_ID, SELLER_ID, clearingPrice, sellerOffers[i], clearingPrice, true );
            }
            else
            {
                TI = new TransInfo( BUYER_ID, SELLER_ID, clearingPrice, sellerOffers[i], clearingPrice, false );
            }
            
            sellers[i].updateMarketInfo( TI );
        }
        
        // TODO: figure out if this sorting is redundant
        Comparator c = new BuyerOfferDescending();
        Arrays.sort( buyers, c );
        c = new SellerOfferAscending(); 
        Arrays.sort( sellers, c );       
        
        if( cmcIndex >= 0 )
        {
            for( int i = 0; i < cmcIndex + 1 ; i++ )
            {
                s.AddDatuum(clearingPrice); 
                s.AddMarkSurp( buyers[i].getValue() - sellers[i].getValue() );
                s.AddBuyerActSurp( buyers[ i ].getValue() - clearingPrice );
                s.AddSellerActSurp( clearingPrice - sellers[i].getValue() );
            }
        }
        return s;
    }
}
