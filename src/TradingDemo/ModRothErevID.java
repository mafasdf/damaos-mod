/*
 * ModRothErevID.java
 *
 * Created on June 8, 2007, 11:55 AM
 *
 */

package TradingDemo;

import java.util.*;
/**
 *
 * @author ynp, icg
 * */

// This algorithm is currently not fully implemented.

public class ModRothErevID implements TradingStyle
{ 
        public static final int domainSize = 11; // from 0 to 1
        public static final float INITIAL_PROPENSITY = 4;
        public static float epsilon = (float) 0.7; 
        public float previousReward = -1;
        public float fi = (float) 0.2;
        public int indexOfLastAction = -1;        
        public float actionDomain[];        

        //public float propensity[];
        public float pro[];
        //public float accProp[]; 
        //protected float[] prop = {-1};
               
        public float value;
        boolean untraded = true;
        AgentType type = AgentType.BUYER;
        
    HashMap< Integer, float[] > propensityMap = new HashMap();
    HashMap< Float, Integer > observationMap = new HashMap();
    /** Creates a new instance of ModRothErevID */
    
    public ModRothErevID( float val ) 
    {
        actionDomain = new float[domainSize];
        pro = new float[domainSize];
        //propensity = new float[domainSize];
        //accProp = new float[domainSize];
        
        for( int i = 0; i < domainSize; i++ )
        {
            actionDomain[i] = ((float)i) / (domainSize - 1);
            pro[i] =  1/domainSize;
            //propensity[i] = INITIAL_PROPENSITY;
            //accProp[i] = ((float)i) / domainSize;
        }
        value = val;
        previousReward = -1;              
    }
        
    public ModRothErevID( float val, AgentType a )
    {
        this( val );
        type = a;
    }      
    
    public ModRothErevID( AgentType at )
    {
        this( -1, at );
    }
   
    /**
    public float formBidPrice( int id ) 
        {
        float pickedAction = 0;
        pickedAction = pickAction( previousReward, id );
        if( pickedAction <= 1.0 )
        {
            return pickedAction * value;
        }
      
        else
            throw new RuntimeException();
        //return pickAction( previousReward, indexOfLastAction ) * value;
    }
    */
    
    public void updatePropensity ( int id )
    {
        if( propensityMap.containsKey( id ) )
        {
            pro = propensityMap.get(id);
        }
        else
        {
            throw new RuntimeException( "ID " + id + " should be in propensityMap!" );
        }
        
        int prevAct = indexOfLastAction;

        for( int i = 0; i < domainSize; i++ )
            {                
                if( prevAct == i )    
                    {
                        pro[i] = ( (1 - fi) * pro[i] ) + ( previousReward*(1-epsilon) );
                    }
                else
                    {
                        pro[i] = ( (1 - fi) * pro[i] ) + ( (pro[i]*epsilon)/(domainSize-1) );
                    }
            }            
        propensityMap.put( id, pro );
    }     
    
    public float pickAction( int id )
    {
   
        if( propensityMap.containsKey( id ) )
        {
            pro = propensityMap.get( id );
        }
        else
        {
            for( int i = 0; i < domainSize; i++ )
            {
                pro[i] = INITIAL_PROPENSITY;                
            }
            
           propensityMap.put( id, pro );
        }
        
        
       pro = propensityToProbability( pro );  
       pro = calcAccumProp( pro );
        
       int actionIndex = -1;                    
       //float rand =(float) Math.random(); 
       float rand = 7;//(float) RandomNumbers.next();
       for( int i = 0; i < domainSize; i++ )
       {
            // Select a random number, and then select the action according to the accumulated probability
            if( rand <= pro[i] )
                 {
                    actionIndex = i;
                    break;
                 }
          }
        
        indexOfLastAction = actionIndex;
        return actionDomain[ actionIndex ];       
    }  
    
    public float[] propensityToProbability( float prop[] )
   {
        float propensitySum = 0;
        
        for(int i = 0; i < domainSize; i++)
        {
            propensitySum += prop[i];
        }
               
        for( int i = 0; i < domainSize; i++)
        {
            prop[i] = prop[i]/propensitySum;
        }
        
        return prop;
    }
    
    public float[] fillPropensity( float[] d )
    {
       for( int i = 0; i < domainSize; i++ )
       {
           d[i] = INITIAL_PROPENSITY;
       }
        return d;
    }
    
    public float[] calcAccumProp( float prob[] )
    {
        for( int i = 1; i < domainSize - 1; i++ )
            {
                prob[i] = prob[ i-1 ] +  prob[ i ];
            }
        
        prob[ domainSize - 1 ] = (float) 1.00000; //Document 
        return prob;
    }
    
    
    public void setInfo(TransInfo ti)
    {
        int id = -1;
        
        if( AgentType.BUYER == type )
        {
            if( ti.myTraded )
            {
                previousReward = value - ti.myTransPrice;
            }
            else
            {
                previousReward = 0;
            }
            id = ti.mySellerID;
        }
        else if ( AgentType.SELLER == type )
        {
            if( ti.myTraded )
            {
                previousReward = ti.myTransPrice - value;
            }
            else
            {
                previousReward = 0;
            }
            id = ti.myBuyerID;
        }
        else
        {
            throw new RuntimeException(" Agent type " + type + " not supported" );
        }
        updatePropensity( id );
    }

    public float getBidPrice( int ID ) 
    { 
                   
        if( value < 0 )
        {
            throw new RuntimeException("Value cannot be "+ value + " . It must be positive." );
        }
        
        float pickedAction = 0;
        pickedAction = pickAction( ID );
        if( type == AgentType.BUYER )
        {
            if( pickedAction <= 1.0 && pickedAction >= 0 )
            {
                return pickedAction * value;
            }
      
            else
                throw new RuntimeException( " pickedAction = " + pickedAction + " which is not within the interval [ 0, 1 ] ");
        }
        else
        {
            throw new RuntimeException(" AgentType " + type + " not supported by getBidPrice");
        }
     
    }

    public float getAskPrice( float maxSellerValue, int ID ) 
    {
        float pickedAction = 0;
        pickedAction = pickAction( ID );
        
        if( value < 0 )
        {
            throw new RuntimeException("Value cannot be "+ value + " . It must be positive." );
        }

        if( type == AgentType.SELLER )
        {
            if( pickedAction <= 1.0 && pickedAction >= 0 )
            {
                return value + ( (maxSellerValue - value)* pickedAction );
            }
      
            else
                throw new RuntimeException( " pickedAction = " + pickedAction + " which is not within the interval [ 0, 1 ] ");
        }
        else
        {
            throw new RuntimeException(" AgentType " + type + " not supported by getBidPrice");
        }
           
    }
    
    public void setResPrice(float val)
    {
        value = val;
    }
    
    public void setAgentType( AgentType at )
    {
        type = at;
    }

    public float getBidPrice()
    {
        return -1;
    }

    public float getAskPrice()
    {
        return -1;
    }

    public float getAskPrice(int i)
    {
        return -1;
    }
}
