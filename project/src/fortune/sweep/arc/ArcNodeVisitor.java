package fortune.sweep.arc;

public interface ArcNodeVisitor
{

	public void spike(ArcNode current, ArcNode next, double y1, double y2,
			double sweepX);
	
	public void arc(ArcNode current, ArcNode next, double y1, double y2,
			double sweepX);

}
