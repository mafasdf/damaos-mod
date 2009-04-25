/*
 * TradingMarket.java
 *
 * Created on February 18, 2007, 2:29 PM
 *
 */

/**
 *
 * @author ynp, icg
 */
package TradingDemo;

import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

public class TradingMarket
{
    float bidPrice, askPrice, transactionPrice;
    private static final DecimalFormat TWO_DIGITS = new DecimalFormat( "0.00" );
    private static final DecimalFormat THREE_DIGITS = new DecimalFormat( "0.000" );
    
    // Number of trading trails in one trading round for Discrimatory-Price K double auction
    public static int maxNumberOfTrades = -1;        // default setting for Number of Trading trails        
    public static final boolean verboseOutput = false;      
    public int total_number = 0;                    // Total number of transactions
    public float average_price = (float)0.0;        // Average price of transactions
    public float stdDev = (float)0.0;               // Standard Deviation of Transaction Prices
    public float marketsurplus;                      
    private float marketEfficiency = -1;
    public float maxMarketSurplus = (float)0;       // Maximum Market surplus that can be extracted from under uniform pricing market
    
    int round = 0;
    int numMarkets;
    MarketStyle MS;
    Stat stats;
    
    public float[] buyerCMCSurplus;     // surplus that each buyer is going to get under the base case
    public float[] sellerCMCSurplus;    // surplus that each seller is going to get under the base case
    float buyerCMCSurplusSum = 0;       // surplus of buyer group under the base case
    float sellerCMCSurplusSum = 0;      // surplus of seller group under the base case
    float actBuyerSurplus = 0;          // Actural buyer surplus
    float actSellerSurplus = 0;         // Actural seller surplus
    
    BuyerAgent buyers[];
    ArrayList<BuyerAgent> buyerList = new ArrayList<BuyerAgent>();
    SellerAgent sellers[];
    ArrayList<SellerAgent> sellerList = new ArrayList<SellerAgent>();
    
    static private int ID_Number = 0;
    public final int marketID;          // ID for the markets
    
    private final Integer myTradingAgentSeed;   // used by tradingMarketAgentSeedGen to create seeds for agents
    private final Integer myMarketSeed;         // used by the random market 
    private RandomNumbers tradingMarketAgentSeedGen;    // create seeds for the market and its agents
    private RandomNumbers BuyerAgentSeedGen;            // create seeds for Buyers
    private RandomNumbers SellerAgentSeedGen;           // create seeds for Sellers
    
    public TradingMarket( SimSpecs simSpec )
    {
        marketID = ID_Number;
        ID_Number ++;
        
        myTradingAgentSeed = simSpec.getTradingMarketSeed( marketID );
        
        // create random number generator for the trading market and its agents
        tradingMarketAgentSeedGen = new RandomNumbers( myTradingAgentSeed );
        
        BuyerAgentSeedGen = new RandomNumbers( tradingMarketAgentSeedGen.getRangedInt(0,Integer.MAX_VALUE) );
        SellerAgentSeedGen = new RandomNumbers( tradingMarketAgentSeedGen.getRangedInt(0, Integer.MAX_VALUE) );
        myMarketSeed = tradingMarketAgentSeedGen.getRangedInt(0, Integer.MAX_VALUE);
       
        MS = parseMarketSpecsToMarket( simSpec.getMarketSpecs(), myMarketSeed );
        
        AgentSpecs ac = null;
        AgentType at;
        MarketSpecs mc = simSpec.getMarketSpecs();
        
        for( int i = 0; i < simSpec.getAgentSpecsLength(); i++ )
        {
            ac = simSpec.getAgentSpecsAt(i);
            at = parseAgentType( ac.myAgentType );
            
            // add agents to buyer and seller list
            for( int j = 0; j < ac.myQuantity; j++ )
            {
                if( AgentType.BUYER == at )
                {
                    buyerList.add( parseAgentSpecsToBuyer( ac, mc ) );        
                }
                else if( AgentType.SELLER == at )
                {
                    sellerList.add( parseAgentSpecsToSeller( ac, mc ) );
                }
                else
                {
                    throw new RuntimeException("AgentType " +at+ " does not match a known agent type" );
                }               
            } 
        }

        float buyerReserve[] = new float[ buyerList.size() ];
        
        for( int j = 0; j < buyerList.size() ; j++ )
        {
            buyerReserve[j] = buyerList.get(j).getValue();      // Get the true reservation value of Buyers
        }
        
        float sellerReserve[] = new float[ sellerList.size() ];
        for( int k = 0; k < sellerList.size(); k++ )
        {
            sellerReserve[k] = sellerList.get(k).getValue();    // Get the true reservation value of sellers
        }
        
        Arrays.sort( buyerReserve );    // Sort Buyers' true reservation value in ascending order
        Arrays.sort( sellerReserve );   // Sort Sellers' true reservation vlaue in ascending order
        
        float cmcPrice = -1;             // CMC Price
        
        // loop through every buyer and seller that is inframarginal
        for(int m = 0; m < buyerReserve.length
                && m < sellerReserve.length
                && buyerReserve[ buyerReserve.length - 1 - m] >= sellerReserve[ m ]; m++ )
        {
            // calculates maximum market surplus that could be achieved if every trader is reporting their true reservation vale
            maxMarketSurplus += buyerReserve[ buyerReserve.length - 1 - m ] - sellerReserve[m];
        }
        
        //New CMC price calculation
        // which now utilizes the kParameter
        int r = buyerReserve.length -1;
        for(int i = 0; i < buyerReserve.length && i < sellerReserve.length && cmcPrice == -1; i++)
        {
            
            if(buyerReserve[r - i] <= sellerReserve[i] )
            {
                if(buyerReserve[r - i] == sellerReserve[i])
                {
                    cmcPrice = buyerReserve[r - i];
                }
                
                else if(i == 0)
                { cmcPrice = 0; }
                
                else
                {
                    if(buyerReserve[r - i + 1] == buyerReserve[r - i] )
                    { cmcPrice = buyerReserve[r - i]; }
                    
                    else if (sellerReserve[i - 1] == sellerReserve[i])
                    { cmcPrice = sellerReserve[i]; }
                    
                    else
                    { cmcPrice = (buyerReserve[r - i] + sellerReserve[i]) / 2; }
                }
            }
        
            if( (i == (buyerReserve.length - 1) || i == (sellerReserve.length -1) ) // if last iteration of the loop
                    && -1 == cmcPrice ) // if no cmcPoints
            {
                if( buyerReserve.length == sellerReserve.length)
                { cmcPrice = (buyerReserve[0] + sellerReserve[ sellerReserve.length - 1 ]) / 2; }

                else if( buyerReserve.length > sellerReserve.length)
                { 
                    if(buyerReserve[r - i] == buyerReserve[r - i - 1])
                    { cmcPrice = buyerReserve[r - i]; }
                    else 
                    { 
                        if( buyerReserve[r - i - 1] < sellerReserve[i]) // i+1 is safe for buyerReserve because buyerReserve > sellerReserve  
                        { cmcPrice = (buyerReserve[r - i] + sellerReserve[i])/2; }
                        // if buyer i+1's price is higher than the last seller's price
                        else
                        { cmcPrice = (buyerReserve[r - i] + buyerReserve[r - i - 1]) / 2; }
                    }
                }
                // more sellers than buyers
                else
                {    
                     if( sellerReserve[i] == sellerReserve[i + 1] )
                    { cmcPrice = sellerReserve[i]; }
                    else if( sellerReserve[ i + 1 ] > buyerReserve[r - i] )
                    { cmcPrice = (buyerReserve[r - i] + sellerReserve[i])/2; }
                    else // seller i + 1's price is loser than buyer i+1's price
                    { cmcPrice = (sellerReserve[ i ] + sellerReserve[i + 1]) / 2; }
                }
            }
        }
    
        System.out.println( marketID + "," + maxMarketSurplus + "," + cmcPrice );
        
        // Get the buyers from the buyerList
        buyers = new BuyerAgent[ buyerList.size() ];
        buyers = buyerList.toArray( buyers );
        buyerCMCSurplus = new float[buyerList.size()];
        for( int i = 0; i < buyerCMCSurplus.length; i++ )
        {
            if( buyers[i].reservationPrice > cmcPrice )
            {
                // calculates the surplus that each buyer is going to get under the base case
                buyerCMCSurplus[i] = buyers[i].reservationPrice - cmcPrice;
            }
            else
            {
                buyerCMCSurplus[i] = 0;
            }
            // calculates the surplus that buyer group is going to get under the base case
            buyerCMCSurplusSum += buyerCMCSurplus[i];
        }
        
        // Get the sellers from the sellerList
        sellers = new SellerAgent[ sellerList.size() ];
        sellers = sellerList.toArray( sellers );
        sellerCMCSurplus = new float[sellerList.size()];
        for( int i = 0; i < sellerCMCSurplus.length; i++ )
        {
            if( sellers[i].reservationPrice < cmcPrice )
            {
                // calculates the surplus that each buyer is going to get under the base case
                sellerCMCSurplus[i] = cmcPrice - sellers[i].reservationPrice;
            }
            else
            {
                sellerCMCSurplus[i] = 0;
            }
             // calculates the surplus that seller group is going to get under the base case
            sellerCMCSurplusSum += sellerCMCSurplus[i];
        }
    }
    
    
    public void trade()
    {
        round++;
        stats = new Stat(buyers.length, sellers.length);                                      // creates the instance of Stat to store and process data
        stats = MS.Trade( stats, buyers, sellers, verboseOutput  );
        stats.SetMaxNetSurp( maxMarketSurplus );                 // sets the maximum Market Surplus that could be achived under this market structure and agents setting
        
        total_number = stats.GetNumTrans();
        average_price = stats.GetAverage();
        stdDev = stats.GetStdDev();
        marketsurplus = stats.GetMarketSurplus();
        marketEfficiency = ( marketsurplus / maxMarketSurplus ) * 100;
        stats.SetMarkEff( marketEfficiency );
        actBuyerSurplus = stats.getBuyerActualSurplus();    
        actSellerSurplus = stats.getSellerActualSurplus();
        
        // Output to comma separated file
        // System.out is redirected to the output file in the TradingWorld class
        System.out.println( (round )+  "," 
                        + stats.GetNumTrans() + ","
                        + TWO_DIGITS.format(stats.GetAverage()) + "," 
                        + TWO_DIGITS.format(marketsurplus) + "," 
                        + THREE_DIGITS.format( stats.GetMarketEfficiency() ) + ","  
                        + TWO_DIGITS.format(stats.GetStdDev()) + "," 
                        + TWO_DIGITS.format(stats.getBuyerActualSurplus()) + ","
                        + TWO_DIGITS.format(stats.getSellerActualSurplus()) + ","
                        + "BuyerAdvantages:" + formatNumberArray(calculateAdvantages(stats.getIndividualBuyersMarketSurplus(), buyerCMCSurplus, maxMarketSurplus)) + ","
                        + "SellerAdvantages:" + formatNumberArray(calculateAdvantages(stats.getIndividualSellersMarketSurplus(), sellerCMCSurplus, maxMarketSurplus))
        );
        
    }


	private String formatNumberArray(float[] numbers)
	{
		StringBuilder toReturn = new StringBuilder(numbers.length * 4);
		
		for(int i=0; i < numbers.length; i++)
		{
			if(i!=0)
				toReturn.append(" ");
			toReturn.append(THREE_DIGITS.format(numbers[i]));
		}
		
		return toReturn.toString();
	}


	private float[] calculateAdvantages(float[] individualMarketSurpluses, float[] idealSurpluses, float maxSurplus)
	{
		if(individualMarketSurpluses.length != buyerCMCSurplus.length)
			throw new IllegalArgumentException("Mismatching array sizes");
		
		float[] advantages = new float[buyerCMCSurplus.length];
		for(int i=0; i < advantages.length; i++)
		{
			advantages[i] = (individualMarketSurpluses[i]-idealSurpluses[i]) / maxSurplus;
		}
		
		return advantages;
	}


	public int getN(){
        return total_number;
    }
    
    public double getA(){
        return average_price;
    }
    
    public double getS(){
        return stdDev;
    }
    
    public double getSur(){
        return marketsurplus;
    }
    
    public double getMarkEff()
    {
        return marketEfficiency;
    }
    
    public float getBuyerMarkAdv()
    {
        return 100 * ( actBuyerSurplus - buyerCMCSurplusSum ) / maxMarketSurplus;
    }
    
    public float getSellerMarkAdv()
    {
        return 100 * ( actSellerSurplus - sellerCMCSurplusSum ) / maxMarketSurplus;        
    }
    
    public AgentType parseAgentType( String s )
    {
        AgentType at;
        if( s.equalsIgnoreCase("BUYER") )
        {
            at = AgentType.BUYER;
        }
        else if( s.equalsIgnoreCase("SELLER") )
        {
            at = AgentType.SELLER;
        }
        else
            throw new RuntimeException("String does not match any known Agent Type" );
        
        return at;
    }
    
    private BuyerAgent parseAgentSpecsToBuyer( AgentSpecs ac, MarketSpecs mc )
    {
        // BuyerAgent( TradingStyle ts, float val )
        String tradeStyleName = ac.myTradeStyleType.trim();
        float value = ac.myReservationPrice;
        float maxBuyerCost = ac.maxSellerPriceOrBuyerCost;
        TradingStyle tradingStyle = null;
     
        int buyerSeed = BuyerAgentSeedGen.getRangedInt( 0, Integer.MAX_VALUE );      
        int tsSeed = BuyerAgentSeedGen.getRangedInt( 0, Integer.MAX_VALUE );
        LearningStyleParameters lsp = null;
        
        if( tradeStyleName.equalsIgnoreCase("Constant") )
        {
            lsp = ac.learningStyleParameters;
            tradingStyle = new Constant( value, (ConstantLSParams) lsp, AgentType.BUYER );
        }
        else if( tradeStyleName.equalsIgnoreCase("ZI") )
        {
            tradingStyle = new ZI( value, ac.actionDomainParameters, tsSeed, AgentType.BUYER );
        }
        else if( tradeStyleName.equalsIgnoreCase("ModifiedRothErev") )
        {
            lsp = ac.learningStyleParameters;
            tradingStyle = new ModRothErev( value, ac.actionDomainParameters, tsSeed, (ModRothErevParams) lsp, AgentType.BUYER );
        }
        else if( tradeStyleName.equalsIgnoreCase("SimpleBeliefBased") )
        {
            tradingStyle = new SimpBeliefBased( value, ac.actionDomainParameters, tsSeed, AgentType.BUYER, mc.kParam );
        }
        else
        {
            throw new RuntimeException( "Trading Style " + tradeStyleName + " not supported!");
        }
        
        // if value == -1, then the value is set randomly at a value less than the max seller price
        return new BuyerAgent( maxBuyerCost, value, tradingStyle, buyerSeed );
    }

    private SellerAgent parseAgentSpecsToSeller( AgentSpecs ac, MarketSpecs mc )
    {
        String tradeStyleName = ac.myTradeStyleType.trim();
        float value = ac.myReservationPrice;
        float maxSellerPrice = ac.maxSellerPriceOrBuyerCost;
        TradingStyle tradingStyle = null;
        
        int sellerSeed = SellerAgentSeedGen.getRangedInt( 0, Integer.MAX_VALUE );      
        int tsSeed = SellerAgentSeedGen.getRangedInt( 0, Integer.MAX_VALUE );
         
        LearningStyleParameters  lsp = null;
        
        if( tradeStyleName.equalsIgnoreCase("Constant") )
        {
            lsp = ac.learningStyleParameters;
            tradingStyle = new Constant( value, (ConstantLSParams) lsp, AgentType.SELLER );
        }
        else if( tradeStyleName.equalsIgnoreCase("ZI") )
        {
            tradingStyle = new ZI( value, ac.actionDomainParameters, tsSeed, AgentType.SELLER );
        }
        else if( tradeStyleName.equalsIgnoreCase("ModifiedRothErev") )
        {
            lsp = ac.learningStyleParameters;
            tradingStyle = new ModRothErev( value, ac.actionDomainParameters, tsSeed, (ModRothErevParams) lsp, AgentType.SELLER );
        }
        else if( tradeStyleName.equalsIgnoreCase("SimpleBeliefBased") )
        {
            tradingStyle = new SimpBeliefBased( value, ac.actionDomainParameters, tsSeed, AgentType.SELLER, mc.kParam );
        }
        else
        {
            throw new RuntimeException( "Trading Style " + tradeStyleName + " not supported!");
        }
         // if value == -1, then the value is set randomly at a value less than the max seller price
      
         return new SellerAgent( maxSellerPrice, value, tradingStyle, sellerSeed );
    }

    
    private MarketStyle parseMarketSpecsToMarket( MarketSpecs marketSpecs, int marketSeed )
    {
        MarketStyle marketStyle = null;
        if( marketSpecs.marketType.equalsIgnoreCase( "DiscriminatoryPriceKDoubleAuction" ) )
        {
            marketStyle = new DiscPriceKDoubleAuction( marketSpecs, marketSeed );
        }
        else if ( marketSpecs.marketType.equalsIgnoreCase( "UniformPriceAuction" ) )
        {
            marketStyle = new UniformPriceAuction( marketSpecs, marketSeed );
        }
        else
        {
            throw new RuntimeException("Market Style " + marketSpecs.marketType + " not supported" );
        }
        
        return marketStyle;
    }
    
}
