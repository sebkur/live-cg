package fortune.sweep.gui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import fortune.sweep.Algorithm;
import fortune.sweep.AlgorithmWatcher;

public class SweepControl extends JComponent implements AlgorithmWatcher
{

	private static final long serialVersionUID = 2560101765472976128L;

	private Algorithm algorithm;

	private int height = 30;
	private int ch = 20;
	private int cw = 20;

	public SweepControl(Algorithm algorithm)
	{
		this.algorithm = algorithm;
		setPreferredSize(new Dimension(getPreferredSize().width, height));
		ControlMouseAdapter controlMouseListener = new ControlMouseAdapter();
		addMouseListener(controlMouseListener);
		addMouseMotionListener(controlMouseListener);
	}

	@Override
	public void paintComponent(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setColor(Color.RED);
		g.fillRect((int) Math.round(algorithm.getSweepX()) - cw / 2,
				getHeight() / 2 - ch / 2, cw, ch);
	}

	@Override
	public void update()
	{
		repaint();
	}

	private class ControlMouseAdapter extends MouseAdapter
	{
		private boolean pressed = false;

		@Override
		public void mousePressed(MouseEvent e)
		{
			pressed = true;
			Point point = e.getPoint();
			algorithm.setSweep(point.x);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			pressed = false;
		}

		@Override
		public void mouseDragged(MouseEvent e)
		{
			if (pressed) {
				Point point = e.getPoint();
				int x = point.x;
				if (x < 0) {
					x = 0;
				}
				algorithm.setSweep(x);
			}
		}
	}
}
