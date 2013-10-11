package de.topobyte.livecg.algorithms.voronoi.fortune.gui.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JPanel;

import de.topobyte.livecg.algorithms.voronoi.fortune.Algorithm;
import de.topobyte.livecg.algorithms.voronoi.fortune.AlgorithmWatcher;
import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;
import de.topobyte.livecg.algorithms.voronoi.fortune.gui.core.FortunePainter;
import de.topobyte.livecg.algorithms.voronoi.fortune.gui.core.Config;
import de.topobyte.livecg.core.painting.AwtPainter;

public class Canvas extends JPanel implements AlgorithmWatcher
{

	private static final long serialVersionUID = 461591430129084653L;

	private Algorithm algorithm;
	private FortunePainter algorithmPainter;
	private AwtPainter painter;

	public Canvas(Algorithm algorithm, Config config, int width, int height)
	{
		this.algorithm = algorithm;

		painter = new AwtPainter(null);
		algorithmPainter = new FortunePainter(algorithm, config, painter);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e)
			{
				Point point = new Point(e.getPoint().x, e.getPoint().y);
				if (point.getX() > Canvas.this.algorithm.getSweepX()) {
					Canvas.this.algorithm.addSite(point);
					repaint();
				}
			}

		});
	}

	@Override
	public void update()
	{
		repaint();
	}

	public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		painter.setGraphics(g2d);
		algorithmPainter.setWidth(getWidth());
		algorithmPainter.setHeight(getHeight());
		algorithmPainter.paint();
	}

	public void addRandomPoints()
	{
		int MARGIN = 20;
		int MINSIZE = 20;

		int sx = (int) Math.ceil(algorithm.getSweepX());
		int width = getWidth() - sx;
		int marginX = 0;
		int marginY = 0;
		if (width >= MARGIN * 2 + MINSIZE) {
			marginX = MARGIN;
		}
		if (getHeight() >= MARGIN * 2 + MINSIZE) {
			marginY = MARGIN;
		}
		if (width <= 0) {
			return;
		}
		Random random = new Random();
		for (int i = 0; i < 16; i++) {
			int x = random.nextInt(width - marginX * 2 - 1) + sx + marginX + 1;
			int y = random.nextInt(getHeight() - marginY * 2) + marginY;
			algorithm.addSite(new Point(x, y));
		}
		update();
	}

}
