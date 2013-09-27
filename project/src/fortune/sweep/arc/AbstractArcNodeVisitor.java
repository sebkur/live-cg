package fortune.sweep.arc;

public abstract class AbstractArcNodeVisitor implements ArcNodeVisitor
{

	@Override
	public void spike(ArcNode current, ArcNode next, double y1, double y2,
			double sweepX)
	{
		// ignore
	}

	@Override
	public void arc(ArcNode current, ArcNode next, double y1, double y2,
			double sweepX)
	{
		// ignore		
	}

}
