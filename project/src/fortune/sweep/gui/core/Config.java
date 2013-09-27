package fortune.sweep.gui.core;

public class Config
{

	private boolean drawCircles, drawBeach, drawVoronoiLines, drawDelaunay;

	public Config()
	{
		drawCircles = false;
		drawBeach = true;
		drawVoronoiLines = true;
		drawDelaunay = false;
	}

	public boolean isDrawCircles()
	{
		return drawCircles;
	}

	public void setDrawCircles(boolean drawCircles)
	{
		this.drawCircles = drawCircles;
	}

	public boolean isDrawBeach()
	{
		return drawBeach;
	}

	public void setDrawBeach(boolean drawBeach)
	{
		this.drawBeach = drawBeach;
	}

	public boolean isDrawVoronoiLines()
	{
		return drawVoronoiLines;
	}

	public void setDrawVoronoiLines(boolean drawVoronoiLines)
	{
		this.drawVoronoiLines = drawVoronoiLines;
	}

	public boolean isDrawDelaunay()
	{
		return drawDelaunay;
	}

	public void setDrawDelaunay(boolean drawDelaunay)
	{
		this.drawDelaunay = drawDelaunay;
	}
}
