/*
 * ModRothErev.java
 *
 * Created on June 8, 2007, 11:54 AM
 */

package TradingDemo;

/**
 *
 * @author ynp, icgs
 */
public class ModRothErev implements TradingStyle 
{   
        public int domainSize = -1;     // Domain ranges from 0.0 to 1.0, in increments of 0.1, thus its size = 11
        private float INITIAL_PROPENSITY;      // The propensity to initially choose each reservationValue
        public float epsilon = (float) -1;   // ok not static          // Epsilon reservationValue 
        public float previousReward = -1;                       // The reservationValue of the reward of the previous trade
        public float phi = (float) -1;                          // Phi, the recency parameter in the Roth-Evev algorithm
        public int indexOfLastAction = -1;                      // The index of the action that was most recently chosen
        public float actionDomain[];                            // The action domain, which ranges from 0.0 to 1.0, in increments of 0.1 
        
        // A propensity reservationValue is stored that corresponds to each action in the actionDomain. 
        // The propensity values for actions that have yeilded profitable results are increased proportionally to the reward received. 
        // The probability and accumulated probabilities are recalculated each round to reflect 
        // the new propensity values.
        public float propensity[];                              // The propensity of choosing the corresponding action in the actionDomain
        public float probability[];                             // The probability of choosing the corresponding action in the actionDomain
        public float accProp[];                                 // The probability of the corresponding action plus the sum 
                                                                // of the probabilities of the preceeding actions

        ActionDomainParameters myAdp;
        
        public float reservationValue;                                     // The reservationValue that the Agent using this TradingStyle, the reservation price 
        private AgentType type;
        RandomNumbers random;
        
    // AgentType is only used to double check that a Buyer is using the getBidPrice
    // and a seller is using getAskPrice
    public ModRothErev( float val, ActionDomainParameters adp, int seed, ModRothErevParams mrep, AgentType at )
    {
        type = at;   
        myAdp = adp;
        
        if( null != mrep )
        {
            epsilon = mrep.getExperimentation();
            phi = mrep.getRecency();
            INITIAL_PROPENSITY = mrep.getInitProp();
        }
        
        domainSize = adp.getDomainSize();
        
        actionDomain = new float[domainSize];
        probability = new float[domainSize];
        propensity = new float[domainSize];
        accProp = new float[domainSize];
        
        for( int i = 0; i < domainSize; i++ )
        {
            //actionDomain[i] = ((float)i) / (domainSize - 1);
            actionDomain[i] = i*( (adp.getUpperBound() - adp.getLowerBound()) )/ (domainSize - 1) + adp.getLowerBound();
            actionDomain[i] = actionDomain[i] / 100; // the action domain is a percentage, so it must be converted from 75% format to 0.75 format
            probability[i] =  1/domainSize;//TODO: is this needed?
            propensity[i] = INITIAL_PROPENSITY;
            accProp[i] = ((float)i) / domainSize;
        }
        reservationValue = val;
        previousReward = -1;
    
        random = new RandomNumbers( seed );      
    }
    
    // Updates the propensities using the modified Roth-Erev algorithm
    // transPrice is the transaction price of the last trade
    public void updatePropensity( )
    {
        // If there was a successful trade in the previous round,
        // then the index of the last round will not be -1 ( or some other negative error reservationValue )
        if( 0 <= indexOfLastAction )
        {
            for( int i = 0; i < domainSize; i++ )
            {            
                // This if statement picks out the previous action so that its propensity reservationValue 
                // can be increased
                if( indexOfLastAction == i )
                {
                    propensity[i] = ( (1 - phi) * propensity[i] ) + ( previousReward*(1-epsilon) );
                }
                
                // All other actions have their propensities updated as well to make it less likely that the algorithm will get
                // stuck choosing a single action
                else
                {
                    propensity[i] = ( (1 - phi) * propensity[i] ) + ( (propensity[i]*epsilon)/(domainSize-1) );
                }
            }       
        } 
    }
        
    // pickAction uses the accumulated probability array to 
    // select an action. The action itself is returned, NOT just the index of the action
    public float pickAction()
    {
        int actionIndex = -1;
        float randNum =(float) random.next(); // Generate a random number
        propensityToProbability();
        calcAccumProp();
        
        for( int i = 0; i < domainSize; i++ )
        {
            // Using the previously generated random number,
            // select the action according to the accumulated probability
            if( randNum <= accProp[i] )
            {
                actionIndex = i;
                break;
            }
        }
        
        indexOfLastAction = actionIndex;
        return actionDomain[ actionIndex ];
   }
    
    // Calculates the probability array based on the propensity values
    public void propensityToProbability()
    {
        float propensitySum = 0;
        
        for(int i = 0; i < domainSize; i++)
        {
            propensitySum += propensity[i];
        }
               
        for( int i = 0; i < domainSize; i++)
        {
            probability[i] = propensity[i] / propensitySum;
        }
    }
    
    // Calculates the accumulated propensity values based on the probability array's values
    public void calcAccumProp()
    {
            accProp[0] = probability[0];
            for(int i = 1; i < domainSize - 1; i++)
            {
                accProp[i] = probability[i] +  accProp[i-1];
            }
            // Due to rounding, the reservationValue of this index could be be (erroneously) slightly higher or lower than 1.0
            accProp[ domainSize - 1 ] = (float) 1.00000; 
    }
    
    // Uses pickAction to return a bid price
    public float getBidPrice()
    {       
        if( reservationValue < 0 )
        {
            throw new RuntimeException("Value cannot be "+ reservationValue + " . It must be positive." );
        }
        
        float pickedAction = 0;
        pickedAction = pickAction();
        if( type == AgentType.BUYER )
        {
            if( pickedAction <= 1.0 && pickedAction >= 0 )
            {
                return pickedAction * reservationValue;
                //return (float) (reservationValue/100) * ((myAdp.getUpperBound() - myAdp.getLowerBound())
                //* pickAction() + myAdp.getLowerBound());
            }
      
            else
                throw new RuntimeException( " pickedAction = " + pickedAction +" which is not within the interval ( 0, 1 ) ");
        }
        else
        {
            throw new RuntimeException(" AgentType " + type + " not supported by getBidPrice");
        }
    }

    public float getBidPrice( int ID )  
    {
        // Curently no learning algorithm has been implemented
        // that can use the id's of the agents it has previously traded with or
        // attempted to trade with.
        return this.getBidPrice();
    }
    
    // Uses pickAction to return a bid price
    public float getAskPrice()
    { 
        float pickedAction = 0;
        pickedAction = pickAction();
        
        if( reservationValue < 0 )
        {
            throw new RuntimeException("Value cannot be "+ reservationValue + " . It must be positive." );
        }
        
        if( type == AgentType.SELLER )
        {
            if(  pickedAction <= 1.0 && pickedAction >= 0 )
            {
                //return reservationValue + ( (maxSellerValue - reservationValue)* pickedAction );
                // return (float) reservationValue + (reservationValue * (myAdp.getLowerBound() 
                //+ pickAction() * (myAdp.getUpperBound() - myAdp.getLowerBound() ))) /100;
                return pickedAction * reservationValue + reservationValue; 
            }
            else
                throw new RuntimeException( " pickedAction = " + pickedAction +" which is not within the interval ( 0, 1 )");
        }
        else
        {
            throw new RuntimeException(" AgentType " + type + " not supported by getAskPrice");
        }            
    }    
    
    public float getAskPrice( int ID )
    {
        return this.getAskPrice();
    }
        
    public void setInfo( TransInfo ti ) // previous reward      
    {
        // this method is called from BuyerAgent or SellerAgent, 
        // which is why it does not use the TransInfo object directly
        
        if( ti.myTraded )
        {
            if( AgentType.BUYER == type )
            {
                previousReward = reservationValue - ti.myTransPrice;
            }
            else if ( AgentType.SELLER == type )
            {
                previousReward = ti.myTransPrice - reservationValue;
            }
            else
            {
                throw new RuntimeException(" Agent type " + type + " not supported" );
            }
        }
        else
        {
            previousReward = 0;
        }
        
        updatePropensity();
    }   
    
    public void setResPrice( float val )
    {
        reservationValue = val;
    }
    
    public void setAgentType( AgentType at )
    {
        type = at;
    }
}
