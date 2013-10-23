package de.topobyte.livecg.algorithms.voronoi.fortune.events;

public class EventQueueModification
{

	public enum Type {
		ADD, REMOVE
	}

	private double x;
	private Type type;
	private CirclePoint eventPoint;

	public EventQueueModification(double x, Type type, CirclePoint eventPoint)
	{
		this.x = x;
		this.type = type;
		this.eventPoint = eventPoint;
	}

	public double getX()
	{
		return x;
	}

	public Type getType()
	{
		return type;
	}

	public CirclePoint getEventPoint()
	{
		return eventPoint;
	}

	public String toString()
	{
		return String.format("sweep: %f, type: %s, %s, point: %f,%f", x,
				type.toString(), eventPoint.getClass(), eventPoint.getX(),
				eventPoint.getY());
	}
}
