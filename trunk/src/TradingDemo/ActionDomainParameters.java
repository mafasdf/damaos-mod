/*
 * ActionDomainParameters.java
 *
 * Created on July 1, 2008, 10:54 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TradingDemo;

/**
 *
 * @author ynp
 */
public class ActionDomainParameters
{
    private int domainSize;
    private float upperBound,
                    lowerBound; 
    
    /** Creates a new instance of ActionDomainParameters */
    public ActionDomainParameters(int domain, float lower, float upper)
    {
        domainSize = domain;
        lowerBound = lower;
        upperBound = upper;
    }
    
    public int getDomainSize()
    {
        return domainSize;
    }

    public float getUpperBound()
    {
        return upperBound;
    }

    public float getLowerBound()
    {
        return lowerBound;
    }
    
    //for debugging
    public String toString()
    {
        String s = "Domain =" + domainSize + "Lower Bound =" + lowerBound + " Upper Bound =" + upperBound;
        return s;
    }
    
}
