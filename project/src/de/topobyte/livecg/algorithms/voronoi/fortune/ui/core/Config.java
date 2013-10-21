package de.topobyte.livecg.algorithms.voronoi.fortune.ui.core;

public class Config
{

	private boolean drawCircles, drawBeach, drawVoronoiLines, drawDelaunay,
			drawDcel;

	public Config()
	{
		drawCircles = false;
		drawBeach = true;
		drawVoronoiLines = true;
		drawDelaunay = false;
		drawDcel = true;
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

	public boolean isDrawDcel()
	{
		return drawDcel;
	}

	public void setDrawDcel(boolean drawDcel)
	{
		this.drawDcel = drawDcel;
	}

}
