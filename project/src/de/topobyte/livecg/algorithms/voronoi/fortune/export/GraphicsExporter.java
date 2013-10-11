package de.topobyte.livecg.algorithms.voronoi.fortune.export;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.topobyte.livecg.algorithms.voronoi.fortune.Algorithm;
import de.topobyte.livecg.algorithms.voronoi.fortune.gui.core.FortunePainter;
import de.topobyte.livecg.algorithms.voronoi.fortune.gui.core.Config;
import de.topobyte.livecg.core.painting.AwtPainter;

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

		FortunePainter algorithmPainter = new FortunePainter(algorithm,
				config, painter);

		algorithmPainter.setWidth(width);
		algorithmPainter.setHeight(height);
		algorithmPainter.paint();

		ImageIO.write(image, "png", file);
	}

}
