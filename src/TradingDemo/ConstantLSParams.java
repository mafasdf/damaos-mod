/*
 * ConstantLSParams.java
 *
 * Created on August 8, 2007, 10:22 AM
 *
 */

package TradingDemo;

/**
 *
 * @author Yu Nanpeng, Ian Guffy
 */
public class ConstantLSParams implements LearningStyleParameters 
{
    
    /** Creates a new instance of ConstantLSParams */
    private float offerPrice;
    
    public ConstantLSParams( float offer  ) 
    {
        setValue(offer);
    } 

    public float getValue() {
        return offerPrice;
    }

    public void setValue(float offer) {
        this.offerPrice = offer;
    }
    
    public String toString()
    {
        return "CP:" + String.valueOf( offerPrice );
    }
        
}
