/*
 * RandomNumbers.java
 *
 * Created on July 2, 2007, 11:21 AM
 *
 */

package TradingDemo;

import rngpack.src.edu.cornell.lassp.houle.RngPack.Ranmar;


/**
 * This class is a wrapper class for the 
 * Ranmar random number generation package.
 *
 * @author ynp, icg
 */
public class RandomNumbers
{
    private static Ranmar ranmar; 

    // create a rand number generator with an integer seed
    public RandomNumbers( int seed )
    {
        ranmar = new Ranmar( seed );
    }
    
    // returns the next random number,
    // between zero and one
    public float next()
    {
        return (float) ranmar.raw();
    }
    
    // returns a value between low and high, 
    // specifically [low, high]
    public int getRangedInt( int low, int high )
    {
        return ranmar.choose( low, high );
    }
}
