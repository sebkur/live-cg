package fortune.sweep.gui.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

public class BufferedImageIcon implements Icon
{

	private BufferedImage bi;

	/**
	 * Create a new icon from this buffered image.
	 * 
	 * @param bi
	 *            the image to wrap.
	 */
	public BufferedImageIcon(BufferedImage bi)
	{
		this.bi = bi;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		g.drawImage(bi, x, y, null);
	}

	@Override
	public int getIconWidth()
	{
		return bi.getWidth();
	}

	@Override
	public int getIconHeight()
	{
		return bi.getHeight();
	}

}