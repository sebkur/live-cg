package fortune.sweep.gui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import fortune.sweep.gui.core.Config;

public class Settings extends JToolBar implements ItemListener
{

	private static final long serialVersionUID = -6537449209660520005L;

	private Canvas canvas;
	private Config config;

	private JToggleButton[] buttons;

	private static final String TEXT_CIRCLES = "Circles";
	private static final String TEXT_BEACHLINE = "Beachline";
	private static final String TEXT_VORONOI = "Voronoi diagram";
	private static final String TEXT_DELAUNAY = "Delaunay triangulation";
	
	private static final String TEXT_ADD_RANDOM = "Add random points";

	public Settings(Canvas canvas, Config config)
	{
		this.canvas = canvas;
		this.config = config;
		
		setFloatable(false);
		
		String as[] = { TEXT_CIRCLES, TEXT_BEACHLINE, TEXT_VORONOI,
				TEXT_DELAUNAY };

		buttons = new JToggleButton[as.length];
		for (int i = 0; i < as.length; i++) {
			buttons[i] = new JToggleButton(as[i]);
			buttons[i].addItemListener(this);
			add(buttons[i]);
		}

		buttons[0].setSelected(config.isDrawCircles());
		buttons[1].setSelected(config.isDrawBeach());
		buttons[2].setSelected(config.isDrawVoronoiLines());
		buttons[3].setSelected(config.isDrawDelaunay());
		
		JButton buttonRandom = new JButton(TEXT_ADD_RANDOM);
		add(buttonRandom);
		
		buttonRandom.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Settings.this.canvas.addRandomPoints();
			}
		});
	}

	public void itemStateChanged(ItemEvent e)
	{
		JToggleButton button = (JToggleButton) e.getItem();
		String s = button.getText();
		boolean flag = button.isSelected();
		if (s.equals(TEXT_CIRCLES)) {
			config.setDrawCircles(flag);
		} else if (s.equals(TEXT_BEACHLINE)) {
			config.setDrawBeach(flag);
		} else if (s.equals(TEXT_VORONOI)) {
			config.setDrawVoronoiLines(flag);
		} else if (s.equals(TEXT_DELAUNAY)) {
			config.setDrawDelaunay(flag);
		}
		canvas.repaint();
	}

}
