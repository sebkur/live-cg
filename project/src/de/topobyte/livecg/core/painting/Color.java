package de.topobyte.livecg.core.painting;

public class Color
{
	private int argb;

	public Color(int rgb)
	{
		this.argb = setFullAlpha(rgb);
	}

	public Color(int rgb, boolean hasAlpha)
	{
		if (hasAlpha) {
			this.argb = rgb;
		} else {
			this.argb = setFullAlpha(rgb);
		}
	}

	public Color(int r, int g, int b)
	{
		this(r, g, b, 255);
	}

	public Color(int r, int g, int b, int a)
	{
		argb = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8)
				| ((b & 0xFF) << 0);
	}

	private int setFullAlpha(int rgb)
	{
		return 0xff000000 | rgb;
	}

	public int getARGB()
	{
		return argb;
	}

	public int getRGB()
	{
		return argb & 0xFFFFFF;
	}

	public double getAlpha()
	{
		int a = (argb & 0xff000000) >>> 24;
		return a / 255.0;
	}
}
