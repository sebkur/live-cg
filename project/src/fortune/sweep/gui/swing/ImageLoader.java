package fortune.sweep.gui.swing;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.Icon;

public class ImageLoader
{

	public static Icon load(String filename)
	{
		if (filename == null) {
			return null;
		}

		BufferedImage bi = null;
		try {
			InputStream is = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(filename);
			bi = ImageIO.read(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (bi != null) {
			return new BufferedImageIcon(bi);
		}

		// unable to load image
		return null;
	}
}
