/*
 * HistogramParameters.java
 *
 * Created on July 3, 2008, 11:10 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TradingDemo;

/**
 *
 * @author ynp
 */
public class HistogramParameters
{
    private boolean checked;
    private int numBins;
    private float low;
    private float high;
    private char output;
    
    /** Creates a new instance of HistogramParameters */
    public HistogramParameters(boolean c, int bins, float l, float h, char o)
    {
        setChecked(c);
        setNumBins(bins);
        setLow(l);
        setHigh(h);
        setOutput(o);
    }
    
    public HistogramParameters()
    {
        setChecked(true);
        setNumBins(-1);
        setLow(-1);
        setHigh(-1);
        setOutput('x');
    }

    public boolean isChecked()
    {
        return checked;
    }

    public int getNumBins()
    {
        return numBins;
    }

    public float getLow()
    {
        return low;
    }

    public float getHigh()
    {
        return high;
    }
    
    // This toString method is formatted for the saved parameters file
    public String toString()
    {
        return ",HC" + getOutput() + ":" + isChecked() 
                + ",HB" + getOutput() + ":" + getNumBins() 
                + ",HL" + getOutput() + ":" + getLow()
                + ",HI" + getOutput() + ":" + getHigh();
    }

    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }

    public void setNumBins(int numBins)
    {
        this.numBins = numBins;
    }

    public void setLow(float low)
    {
        this.low = low;
    }

    public void setHigh(float high)
    {
        this.high = high;
    }

    public char getOutput()
    {
        return output;
    }

    public void setOutput(char output)
    {
        this.output = output;
    }
    
}
