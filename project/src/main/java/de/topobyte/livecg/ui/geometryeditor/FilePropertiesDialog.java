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
package de.topobyte.livecg.ui.geometryeditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.livecg.util.ButtonPane;
import de.topobyte.viewports.geometry.Rectangle;

public class FilePropertiesDialog extends JDialog
{

	private static final long serialVersionUID = 8139227323570997844L;

	private JTextField tWidth;
	private JTextField tHeight;

	public FilePropertiesDialog(Window parent, final Content content)
	{
		super(parent, "File Properties");

		Rectangle scene = content.getScene();
		double width = scene.getWidth();
		double height = scene.getHeight();

		JLabel lWidth = new JLabel("Width:");
		JLabel lHeight = new JLabel("Height:");

		tWidth = new JTextField(getText(width));
		tHeight = new JTextField(getText(height));

		GridBagConstraints c = new GridBagConstraints();
		GridBagConstraintsEditor editor = new GridBagConstraintsEditor(c);

		/*
		 * Table
		 */
		JPanel table = new JPanel();
		table.setLayout(new GridBagLayout());

		editor.gridPos(0, 0);
		editor.weight(0.0, 0.0).fill(GridBagConstraints.NONE);
		table.add(lWidth, c);

		editor.gridPos(0, 1);
		table.add(lHeight, c);

		editor.gridPos(1, 0);
		editor.weight(1.0, 0.0).fill(GridBagConstraints.BOTH);
		table.add(tWidth, c);

		editor.gridPos(1, 1);
		table.add(tHeight, c);

		/*
		 * Buttons
		 */

		JButton buttonOk = new JButton("OK");
		JButton buttonCancel = new JButton("Cancel");

		List<JButton> buttons = new ArrayList<>();
		buttons.add(buttonOk);
		buttons.add(buttonCancel);

		ButtonPane buttonPane = new ButtonPane(buttons);

		/*
		 * Main panel
		 */

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		setContentPane(panel);

		table.setBorder(BorderFactory.createTitledBorder("Scene dimension"));

		editor.gridPos(0, 0);
		editor.weight(1.0, 1.0).fill(GridBagConstraints.BOTH);
		panel.add(table, c);

		editor.gridPos(0, 1);
		editor.weight(1.0, 0.0).fill(GridBagConstraints.BOTH);
		panel.add(buttonPane, c);

		/*
		 * Button actions
		 */

		buttonCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				FilePropertiesDialog.this.dispose();
			}
		});

		buttonOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event)
			{
				String sWidth = tWidth.getText();
				String sHeight = tHeight.getText();

				double width = 0, height = 0;

				boolean ok = true;
				try {
					width = Double.parseDouble(sWidth);
				} catch (NumberFormatException e) {
					ok = false;
				}
				try {
					height = Double.parseDouble(sHeight);
				} catch (NumberFormatException e) {
					ok = false;
				}

				if (width <= 0 || height <= 0) {
					ok = false;
				}

				if (!ok) {
					return;
				}

				FilePropertiesDialog.this.dispose();
				content.setScene(new Rectangle(0, 0, width, height));
				content.fireDimensionChanged();
			}
		});
	}

	public String getText(double value)
	{
		int digits = 2;
		if (Math.abs(Math.round(value) - value) < 0.0001) {
			digits = 0;
		}
		return String.format("%." + digits + "f", value);
	}

}
