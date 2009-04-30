package TradingDemo;

public class MultiLayerMREParams implements LearningStyleParameters
{	
	private ModRothErevParams[] arr;
	
	public MultiLayerMREParams(ModRothErevParams ... values)
	{
		arr = java.util.Arrays.copyOf(values, values.length);		
	}
	
	public int size()
	{
		return arr.length;
	}
	
	public ModRothErevParams get(int i)
	{
		return arr[i];
	}
	
}
