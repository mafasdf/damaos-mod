/*
 * FileData.java
 *
 * Created on June 25, 2007, 3:11 PM
 *
 */

package TradingDemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * FileData is used to perform operations to read
 * and write to and from a file
 * the paramaters for the simulation
 *
 * @author Yu Nanpeng, Ian Guffy
 */
public class FileData 
{    
    int numberOfMarkets = -1;
    boolean b;
 
    // Create a SimSpecs object out of a DA-MAOS File
    public static SimSpecs parseInputFile( File file  ) throws IOException, Exception
    {
        SimSpecs simSpecs = new SimSpecs();
        Scanner input = null;
        try
        {
            input = new Scanner( file ).useDelimiter("[@#]");
        }
        catch( FileNotFoundException e )
        {
            System.err.print( e );
            System.err.print( "File \"" + file + "\" not found" );
            System.exit(0);
        }
        
        //read file and do stuff...
        
        // Check if the file is of the proper type by checking if
        // the first line of the file matches the fileType string
        String fileType = "DA-MAOS File";
        if( !input.hasNextLine() || !(input.next().trim().equalsIgnoreCase( fileType )) )
        {
            throw new RuntimeException(" File does not start with '" + fileType + "'" );
        }
              
        // Read through the input file, and call the appropriate 
        // method to parse the next line of the file.   
        String curr = null;
        char c;
        while( input.hasNextLine() )
        {
            curr = input.next().trim();
            c = curr.charAt(0);
            switch( Character.toUpperCase( c ) )
            {
                case 'A':
                    simSpecs.addAgentSpecs( parseAgents( curr ) );
                    break;
                case 'R':
                    simSpecs.addMarketSpecs( parseMarket( curr ));
                    break;
                default:
                    break;
            }
        }
        return simSpecs;
    }
    
    // Create a list of agents out of a line in a DA-MAOS File
    public static ArrayList<AgentSpecs> parseAgents( String agents ) throws Exception
    {
   
        String agentType = null;
        String tradingStyle = null;
        int quantity = -1;
        float reservationPrice = -1;
        float maxSellPriceOrBuyerCost = -1;
        
        // variables for the Tesfatsion Modified Roth Erev function
        float MREepsilon = -1;
        float MRphi = -1;
        float MREinitProp = -1;
        
        float COfferPrice = -1;
        
        int actDomainSize = -1;
        float lowerBound = -1;
        float upperBound = -1;
        
        ActionDomainParameters adp = null;   
        LearningStyleParameters lsp = null; 
        ArrayList<AgentSpecs> specList = new ArrayList();
      
        // break the line of text into substrings, storing them in "parsed"
        String[] parsed = agents.split(",");
         
        for( String s : parsed )
        {
            // remove extra whitespace
            s = s.trim();
            
            // the substring of s is taken at index 3 because of the format of the input file
            switch( Character.toUpperCase( s.charAt(0) ) )
            {
                case 'A':
                    agentType = s.substring(3).trim();
                    break;
                case 'T':
                    tradingStyle = s.substring(3).trim();
                    break;
                case 'Q':
                    quantity = Integer.valueOf( s.substring(3).trim() );
                    break;
                case 'R':
                    reservationPrice = Float.valueOf( s.substring(3).trim() );
                    break;
                case 'M':
                    maxSellPriceOrBuyerCost = Float.valueOf( s.substring(3).trim() );
                    break;
                case 'D':
                    actDomainSize = Integer.valueOf( s.substring(3).trim() );
                    break;
                case 'L':
                    lowerBound = Float.valueOf( s.substring(3).trim() );
                    break;
                case 'U':
                    upperBound = Float.valueOf( s.substring(3).trim() );
                    break;
                case 'C': // Constant LearningStyleParameters
                    if( 'P' == Character.toUpperCase( s.charAt(1) ) )
                    { 
                        COfferPrice = Float.valueOf( s.substring(3).trim() );
                    }                 
                    break;
                case 'E': // ModRothErev LearningStyleParameters
                    switch( Character.toUpperCase( s.charAt(1) ) )
                    {
                        case 'E':
                            MREepsilon = Float.valueOf( s.substring(3).trim() );
                            break;
                        case 'F':
                            MRphi = Float.valueOf( s.substring(3).trim() );
                            break;
                        case 'P':
                            MREinitProp = Float.valueOf( s.substring(3).trim() );
                            break;
                        default: 
                            break;
                    }
                    
                    break;
                    
                default:
                    break;
            }
        }
        
         // TODO: ensure that a ConstantLSParams will never be overwritten by a ModRothErevParams        
         if( -1 < COfferPrice )
         {
             lsp = new ConstantLSParams( COfferPrice );
         }
         else if( -1 < MREepsilon || -1 < MRphi || -1 < MREinitProp )
         {
             lsp = new ModRothErevParams( MREinitProp, MREepsilon, MRphi );
         }

        adp = new ActionDomainParameters( actDomainSize, lowerBound, upperBound );
        AgentSpecs as = new AgentSpecs(agentType, tradingStyle, quantity, maxSellPriceOrBuyerCost, lsp, reservationPrice, adp);
        specList.add( as );
        
        // This line is used primarily for debugging purposes
        System.out.println("AT =" + agentType + "  TS =" + tradingStyle + " Q = " + quantity + " R = " + reservationPrice 
                + " M = " + maxSellPriceOrBuyerCost + " lsp =" + lsp + " adp =" + adp );
        
        return specList;
    }
    
    public static MarketSpecs parseMarket( String market ) throws IOException
    {
        int randSeed = -1;
        String marketType = null;
        String outputFileName = null;
        int quantityOfMarkets = -1;
        int maxTradesPerRound = -1;
        float kParameter = -1;
        // disp holds true or false for each of the four histograms,
        // to determine if that histogram will be displayed or not
        //boolean[] disp = new boolean[4];
        
        
        HistogramParameters[] histParams = new HistogramParameters[4]; 
        for( int i = 0; i < histParams.length; i++ )
        {
            histParams[i] = new HistogramParameters();
        }
        
        // break the line of text into substrings, storing them in "parsed"
        String[] parsed = market.split(",");
        
        for( String s : parsed )
        {
            // remove extra whitespace
            s = s.trim();
            
            // the substring of s is taken at index 3 because of the format of the input file
            switch( Character.toUpperCase( s.charAt(0) ) )
            {
                case 'R':
                    randSeed = Integer.valueOf( s.substring(3).trim() );
                    break;
                case 'M':
                    marketType = s.substring(3).trim();
                    System.err.println("MarketType: " + marketType);
                    break;
                case 'O':
                    outputFileName = s.substring(3).trim() ;
                    break;
                case 'Q':
                    quantityOfMarkets = Integer.valueOf( s.substring(3).trim() );
                    break;
                case 'N':
                    maxTradesPerRound = Integer.valueOf( s.substring(3).trim() );
                    break;
                case 'K':
                    kParameter = Float.valueOf( s.substring(3).trim() );
                    break;
  
                case 'H':                  
                    switch( Character.toUpperCase( s.charAt(2) ) )
                    {
                        case 'U':
                            if( histParams[0].getOutput() != 'U' )
                            {
                                histParams[0].setOutput( 'U' );
                            }       
        
                            switch( Character.toUpperCase( s.charAt(1) ) )
                            {    
                                case 'C':
                                    histParams[0].setChecked( Boolean.valueOf( s.substring(4).trim() ) );
                                    break;
                                case 'B':
                                    histParams[0].setNumBins( Integer.valueOf( s.substring(4).trim() ) );
                                    break;
                                case 'L':
                                    histParams[0].setLow( Float.valueOf( s.substring(4).trim() ) );
                                    break;
                                case 'I':
                                    histParams[0].setHigh( Float.valueOf( s.substring(4).trim() ) );
                                    break;
                                default:
                                    break;
                            }
                            break;
                            
                        case 'E':
                            if( histParams[1].getOutput() != 'E' )
                            {
                                histParams[1].setOutput( 'E' );
                            }       
                                    
                            switch( Character.toUpperCase( s.charAt(1) ) )
                            {    
                                case 'C':
                                    histParams[1].setChecked( Boolean.valueOf( s.substring(4).trim() ) );
                                    break;
                                case 'B':
                                    histParams[1].setNumBins( Integer.valueOf( s.substring(4).trim() ) );
                                    break;
                                case 'L':
                                    histParams[1].setLow( Float.valueOf( s.substring(4).trim() ) );
                                    break;
                                case 'I':
                                    histParams[1].setHigh( Float.valueOf( s.substring(4).trim() ) );
                                    break;
                                default:
                                    break;
                            }
                            break;
                            
                        case 'B':
                            
                            if( histParams[2].getOutput() != 'B' )
                            {
                                histParams[2].setOutput( 'B' );
                            }       
        
                            switch( Character.toUpperCase( s.charAt(1) ) )
                            {    
                                case 'C':
                                    histParams[2].setChecked( Boolean.valueOf( s.substring(4).trim() ) );
                                    break;
                                case 'B':
                                    histParams[2].setNumBins( Integer.valueOf( s.substring(4).trim() ) );
                                    break;
                                case 'L':
                                    histParams[2].setLow( Float.valueOf( s.substring(4).trim() ) );
                                    break;
                                case 'I':
                                    histParams[2].setHigh( Float.valueOf( s.substring(4).trim() ) );
                                    break;
                                default:
                                    break;
                            }
                            break;
                            
                        case 'S':
                            if( histParams[3].getOutput() != 'S' )
                            {
                                histParams[3].setOutput( 'S' );
                            }       
                            switch( Character.toUpperCase( s.charAt(1) ) )
                            {    
                                case 'C':
                                    histParams[3].setChecked( Boolean.valueOf( s.substring(4).trim() ) );
                                    break;
                                case 'B':
                                    histParams[3].setNumBins( Integer.valueOf( s.substring(4).trim() ) );
                                    break;
                                case 'L':
                                    histParams[3].setLow( Float.valueOf( s.substring(4).trim() ) );
                                    break;
                                case 'I':
                                    histParams[3].setHigh( Float.valueOf( s.substring(4).trim() ) );
                                    break;
                                default:
                                    break;
                            }
                            break;
                            
                        default:
                            break;
                    }
                       
                default:
                    break;
            }
        }
        
        // This line is primarily for debugging purposes
        System.out.println("RA = " +randSeed+ "  MT = " + marketType + " QM = " + quantityOfMarkets  + " OF = " + outputFileName
                + "MT = " + maxTradesPerRound  +" KP = " + kParameter + " DS = " + histParams[0] + " " + histParams[1] + " "
                + histParams[2] + " " + histParams[3] );
        
        return new MarketSpecs( randSeed, marketType, quantityOfMarkets, outputFileName, maxTradesPerRound, kParameter, histParams );
    }
    
    public static void saveSettings( SimSpecs s, String fileName )
    {
        PrintWriter out = null;
        
        // Note: All of the out.checkError() statements seem to be required to 
        //ensure that PrintWriter outputs things properly
        
        try
        {
            out = new PrintWriter( new FileWriter( fileName ) );
        }
        catch (IOException e)
        { e.printStackTrace(); }
        
        if( out.checkError() )
        { throw new RuntimeException("PrintWriter not working :-("); }
        
        // Create output file
        out.println("DA-MAOS File");
        if( out.checkError() )
        { throw new RuntimeException("PrintWriter not working :-("); }
        
        for( int i = 0; i < s.getAgentSpecsLength(); i++)
        {
            out.println( s.getAgentSpecsAt(i).toString() );
            
            if( out.checkError() )
            { throw new RuntimeException("PrintWriter not working :-("); }
            
        }
        out.println( s.getMarketSpecs().toString() );
        if( out.checkError() )
        { throw new RuntimeException("PrintWriter not working :-("); }
        
    }
  
}
