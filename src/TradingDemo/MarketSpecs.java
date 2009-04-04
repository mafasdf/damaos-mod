/*
 * MarketSpecs.java
 *
 * Created on June 27, 2007, 4:02 Pm
 */
package TradingDemo;

import java.util.*;

/**
 * MarketSpecs is a container class that holds 
 * all of the specifications for creating a trading market.
 * This class also contains methods used to output these specifications.
 *
 * @author ynp, icg
 */
public class MarketSpecs 
{
    public int masterSeed;
    public String marketType;
    public int marketQuantity;
    public String outputFile;
    public int maxTradesPerRound;
    public HistogramParameters[] histParams;
    public float kParam;
    
    /** Creates a new instance of MarketSpecs */    
    public MarketSpecs( int theRandomSeed, String theMarketType, int quantityOfMarkets, String outputFileName, 
            int maxTPR, float KParameter, HistogramParameters[] histParameters )
    {
        masterSeed = theRandomSeed;
        marketType = theMarketType;
        marketQuantity = quantityOfMarkets;
        outputFile = outputFileName;
        maxTradesPerRound = maxTPR;
        histParams = histParameters;
        kParam = KParameter;
    }
    
    // This string outputs the parameters in a Comma Separated format for the data output file
    public String toStringCSV()
    {
        String labels = "Random Seed,Market Type,Number of Markets,Max Number of Trades Per Round,K-Param";
        String values = "" + masterSeed + "," + marketType + "," + marketQuantity + "," + maxTradesPerRound+ "," + kParam;
        return labels + "\n" + values;
    }
     
    // This toString method is formatted for the saved parameters file
    @Override
    public String toString()
    {
        return "#RA:" + masterSeed + ",MT:" + marketType + ",OF:" + outputFile + ",QM:" + marketQuantity 
                +",NT:" + maxTradesPerRound +",KP:" + kParam + histString(); //UPDATE
    }
    
    // used to store the results of the displayOptions array
    public String histString()
    {
        String s = "";
        for( HistogramParameters h : histParams )
        {
            s += h.toString();
        }
        return s;
    }
    
}
