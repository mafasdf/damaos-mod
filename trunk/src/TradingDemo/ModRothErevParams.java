/*
 * ModRothErevParams.java
 *
 * Created on August 8, 2007, 10:15 AM
 *
 */

package TradingDemo;

/**
 *
 * @author Yu Nanpeng, Ian Guffy
 */
public class ModRothErevParams implements LearningStyleParameters 
{
    // initial propensity, exp, recency
    private float initProp;
    private float experimentation;
    private float recency;
    
    public ModRothErevParams( float iProp, float exp, float rec ) 
    {
        setInitProp(iProp);
        setExperimentation(exp);
        setRecency(rec);
    }

    public float getInitProp() {
        return initProp;
    }

    public void setInitProp(float initProp) {
        this.initProp = initProp;
    }

    public float getExperimentation() {
        return experimentation;
    }

    public void setExperimentation(float exp) {
        this.experimentation = exp;
    }

    public float getRecency() {
        return recency;
    }

    public void setRecency(float recency) {
        this.recency = recency;
    } 
    
    @Override
    public String toString()
    {
        return "EP: " + String.valueOf( initProp ) + ", EE: " + String.valueOf( experimentation ) + ", EF: " + String.valueOf( recency );
    }
}
