/* This file is part of LiveCG. 
 * 
 * Copyright (C) 2013  Sebastian Kuerten
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.topobyte.polygon.shortestpath;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.topobyte.swing.layout.GridBagHelper;

public class ShortestPathDialog
{

	private JFrame frame;

	private JSlider slider;
	private ShortestPathPanel spp;

	public ShortestPathDialog(ShortestPathAlgorithm algorithm)
	{
		frame = new JFrame("Shortest Path in Polygons");

		JPanel panel = new JPanel(new GridBagLayout());
		frame.setContentPane(panel);

		spp = new ShortestPathPanel(algorithm);

		Sleeve sleeve = algorithm.getSleeve();
		int nDiagonals = sleeve.getDiagonals().size();

		int max = nDiagonals + 1;
		slider = new JSlider(0, max);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(1);
		slider.setValue(0);
		slider.setBorder(new TitledBorder("Diagonals"));

		GridBagConstraints c = new GridBagConstraints();

		GridBagHelper.setWxWyF(c, 1.0, 0.0, GridBagConstraints.HORIZONTAL);
		GridBagHelper.setGxGy(c, 0, 0);
		panel.add(slider, c);

		GridBagHelper.setWxWyF(c, 1.0, 1.0, GridBagConstraints.BOTH);
		GridBagHelper.setGxGy(c, 0, 1);
		panel.add(spp, c);

		frame.setLocationByPlatform(true);
		frame.setSize(800, 500);
		frame.setVisible(true);

		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e)
			{
				setDiagonal();
			}
		});
	}

	public JFrame getFrame()
	{
		return frame;
	}

	protected void setDiagonal()
	{
		int value = slider.getValue();
		System.out.println(value);
	}

}
