package TradingDemo;

public class ChangingZI extends ZI
{
	private int changeDay;
	private DayCounter dayCounter;
	private boolean changed = false;
	private ActionDomainParameters finalADP;
	
	public ChangingZI(float reservationValue, ActionDomainParameters startingADP, ActionDomainParameters finalADP, int seed, AgentType at,
			DayCounter dayCounter, int changeDay)
	{
		super(reservationValue, startingADP, seed, at);
		this.changeDay = changeDay;
		this.dayCounter = dayCounter;
		this.finalADP = finalADP;
	}
	
	@Override
	public float getBidPrice()
	{
		changeIfNeeded();
		return super.getBidPrice();
	}
	
	@Override
	public float getBidPrice(int id)
	{
		changeIfNeeded();
		return super.getBidPrice(id);
	}
	
	private void changeIfNeeded()
	{
		if(!changed && dayCounter.getValue() >= changeDay)
		{
			super.myAdp = finalADP;
			changed = true;
		}
	}
	
}
