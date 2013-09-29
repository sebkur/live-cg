package de.topobyte.fortune.sweep.export;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.topobyte.fortune.sweep.Algorithm;
import de.topobyte.fortune.sweep.gui.core.AlgorithmPainter;
import de.topobyte.fortune.sweep.gui.core.Config;
import de.topobyte.fortune.sweep.gui.swing.AwtPainter;

public class GraphicsExporter
{

	public static void exportPNG(File file, Algorithm algorithm, Config config,
			int width, int height) throws IOException
	{
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		AwtPainter painter = new AwtPainter(graphics);

		AlgorithmPainter algorithmPainter = new AlgorithmPainter(algorithm,
				config, painter);

		algorithmPainter.setWidth(width);
		algorithmPainter.setHeight(height);
		algorithmPainter.paint();

		ImageIO.write(image, "png", file);
	}

}
