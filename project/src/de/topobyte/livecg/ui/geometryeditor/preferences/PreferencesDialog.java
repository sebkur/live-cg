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
package de.topobyte.livecg.ui.geometryeditor.preferences;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.LiveCG;
import de.topobyte.livecg.preferences.Configuration;
import de.topobyte.livecg.preferences.PreferenceManager;
import de.topobyte.swing.ButtonPane;

public class PreferencesDialog extends JDialog
{

	private static final long serialVersionUID = 7480440856413783370L;

	final static Logger logger = LoggerFactory
			.getLogger(PreferencesDialog.class);

	private LiveCG liveCG;

	private LAFSelector lafSelector;

	public PreferencesDialog(Window owner, LiveCG liveCG)
	{
		super(owner, "Preferences");
		this.liveCG = liveCG;

		JPanel content = new JPanel(new BorderLayout());
		setContentPane(content);

		Configuration c = PreferenceManager.getConfiguration();

		/*
		 * Buttons
		 */

		JButton buttonOk = new JButton("OK");
		JButton buttonCancel = new JButton("Cancel");

		List<JButton> buttons = new ArrayList<JButton>();
		buttons.add(buttonOk);
		buttons.add(buttonCancel);

		ButtonPane buttonPane = new ButtonPane(buttons);
		content.add(buttonPane, BorderLayout.SOUTH);

		/*
		 * Preferences
		 */

		JPanel prefs = new JPanel(new GridLayout(0, 2));
		content.add(prefs, BorderLayout.CENTER);

		lafSelector = new LAFSelector(c);

		lafSelector.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				setLookAndFeel((LookAndFeelInfo) lafSelector.getSelectedItem());
			}
		});

		prefs.add(new JLabel("Look and Feel"));
		prefs.add(lafSelector);

		/*
		 * Button actions
		 */

		buttonCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				PreferencesDialog.this.dispose();
			}
		});

		buttonOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event)
			{
				PreferencesDialog.this.dispose();
				ok();
			}
		});
	}

	private void setLookAndFeel(LookAndFeelInfo info)
	{
		String lookAndFeel = info.getClassName();
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) {
			logger.error("error while setting look and feel '" + lookAndFeel
					+ "': " + e.getClass().getSimpleName() + ", message: "
					+ e.getMessage());
		}
		SwingUtilities.updateComponentTreeUI(this);
		this.pack();
	}

	protected void ok()
	{
		Configuration configuration = PreferenceManager.getConfiguration();

		LookAndFeelInfo info = (LookAndFeelInfo) lafSelector.getSelectedItem();
		configuration.setSelectedLookAndFeel(info.getClassName());

		liveCG.applyConfiguration(configuration);
	}
}
