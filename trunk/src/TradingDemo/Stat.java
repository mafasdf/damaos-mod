/* * Stat class CSCI/ECON 0494 Middlebury College Fall 2004 Rob Axtell */package TradingDemo;public class Stat{	private int numTransactions; // The total number of transactions 	private float min = Float.MAX_VALUE, // Lowest transaction price			max = 0, // Highest transaction price 			sum = 0, // Sum of all of the transaction prices			sumOfSquares = 0, // Sum of squares of all of the transaction prices			marketSurplus = 0, // The Market Surplus			marketEfficiency = 0, // marketEfficiency is the Market Efficiency			buyerActualMarketSurplus = 0, sellerActualMarketSurplus = 0, maxNetSurplus = 0;	//to store individual surplus	private float[] buyersMarketSurplus;	private float[] sellersMarketSurplus;		public Stat(int numBuyers, int numSellers)	{		buyersMarketSurplus = new float[numBuyers];		sellersMarketSurplus = new float[numSellers];		numTransactions = 0;		min = Float.MAX_VALUE;		max = 0;		sum = 0;		sumOfSquares = 0;		marketSurplus = 0;		marketEfficiency = -1;	}		public void AddDatuum(float Datuum)	{		numTransactions++;				if(Datuum < min)		{			min = Datuum;		}				if(Datuum > max)		{			max = Datuum;		}				sum += Datuum;		sumOfSquares += Datuum * Datuum;	}		public void AddMarkSurp(float s)	{		marketSurplus += s;	}		public void AddBuyerActSurp(float s)	{		buyerActualMarketSurplus += s;	}		public void AddSellerActSurp(float s)	{		sellerActualMarketSurplus += s;	}		public void SetMaxNetSurp(float m)	{		maxNetSurplus = m;	}		public void SetMarkEff(float e)	{		marketEfficiency = e;	}		public int GetNumTrans()	{		return numTransactions;	}		public float GetMin()	{		return min;	}		public float GetMax()	{		return max;	}		public float GetAverage()	{				if(numTransactions > 0)		{			return sum / numTransactions;		}		else		{			return 0;		}	}		public float GetVariance()	{		float average, // Average transaction price		temp;				if(numTransactions > 1)		{			average = GetAverage();			temp = sumOfSquares - numTransactions * average * average;			return temp / (numTransactions - 1);		}		else		{			return 0;		}	}		public float GetStdDev()	{		float temp = GetVariance();		if(temp >= 0)		{			return (float) Math.sqrt(GetVariance());		}		else if((temp < 0) && (temp > -0.005))		{			return (float) 0;		}		else		{			throw new RuntimeException("The Variance of the Transaction price is" + GetVariance() + ". It might be calculated in the wrong way");		}	}		public float GetMarketSurplus()	{		return marketSurplus;	}		public float GetMarketEfficiency()	{		return marketEfficiency;	}		public float getBuyerActualSurplus()	{		return buyerActualMarketSurplus;	}		public float getSellerActualSurplus()	{		return sellerActualMarketSurplus;	}		public float getMaxNetSurplus()	{		return maxNetSurplus;	}		public void setIndividualBuyerMarketSurplus(int index, float amount)	{		buyersMarketSurplus[index] = amount;	}		public void setIndividualSellerMarketSurplus(int index, float amount)	{		sellersMarketSurplus[index] = amount;	}		//TODO: these could be made cleaner / less cohesive	public float[] getIndividualBuyersMarketSurplus()	{		return buyersMarketSurplus;	}		public float[] getIndividualSellersMarketSurplus()	{		return sellersMarketSurplus;	}}