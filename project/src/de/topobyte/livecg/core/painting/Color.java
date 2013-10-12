package de.topobyte.livecg.core.painting;

public class Color
{
	private int rgb;

	public Color(int rgb)
	{
		this.rgb = setFullAlpha(rgb);
	}

	public Color(int rgb, boolean hasAlpha)
	{
		if (hasAlpha) {
			this.rgb = rgb;
		} else {
			this.rgb = setFullAlpha(rgb);
		}
	}

	private int setFullAlpha(int rgb)
	{
		return 0xff000000 | rgb;
	}

	public int getRGB()
	{
		return rgb;
	}

	public double getAlpha()
	{
		int a = (rgb & 0xff000000) >>> 24;
		return a / 255.0;
	}
}
