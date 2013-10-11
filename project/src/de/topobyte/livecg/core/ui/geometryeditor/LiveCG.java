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

package de.topobyte.livecg.core.ui.geometryeditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.geometry.io.ContentReader;
import de.topobyte.livecg.core.ui.geometryeditor.debug.ContentDialog;
import de.topobyte.livecg.core.ui.geometryeditor.object.ObjectDialog;

public class LiveCG
{

	static final Logger logger = LoggerFactory.getLogger(LiveCG.class);

	public static void main(String[] args)
	{
		final LiveCG runner = new LiveCG();

		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				runner.setup(true);
			}
		});
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				runner.frame.requestFocus();
			}
		});
	}

	private JFrame frame;
	private ObjectDialog objectDialog;
	private ContentDialog contentDialog;

	public LiveCG()
	{
		BasicConfigurator.configure();

		frame = new JFrame();
	}

	public void setup(boolean exitOnClose)
	{
		frame.setSize(800, 600);
		if (exitOnClose) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

		frame.setTitle("Live CG");

		GeometryEditor lineEditor = new GeometryEditor();
		Menu menu = new Menu(this, lineEditor.getEditPane(),
				lineEditor.getEditPane());
		Toolbar toolbar = new Toolbar(lineEditor.getEditPane(),
				lineEditor.getEditPane());

		toolbar.setFloatable(false);

		StatusBar statusBar = new StatusBar();
		StatusBarMouseListener statusBarMouseListener = new StatusBarMouseListener(
				statusBar);
		lineEditor.getEditPane().addMouseListener(statusBarMouseListener);
		lineEditor.getEditPane().addMouseMotionListener(statusBarMouseListener);

		GridBagConstraints c = new GridBagConstraints();

		JPanel mainPanel = new JPanel(new GridBagLayout());
		frame.setJMenuBar(menu);
		frame.setContentPane(mainPanel);

		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;

		c.gridy = 0;
		c.weighty = 0.0;
		mainPanel.add(toolbar, c);

		c.gridy = 1;
		c.weighty = 1.0;
		mainPanel.add(lineEditor, c);

		c.gridy = 2;
		c.weighty = 0.0;
		mainPanel.add(statusBar, c);

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		objectDialog = new ObjectDialog(frame, lineEditor.getEditPane());
		objectDialog.setSize(300, 300);
		objectDialog.setLocation(frame.getX() + frame.getWidth(), frame.getY());
		objectDialog.setVisible(true);
		
		contentDialog = new ContentDialog(frame, lineEditor.getEditPane());
		contentDialog.setSize(300, 300);
		contentDialog.setLocation(frame.getX() + frame.getWidth(), frame.getY());

		ContentReader reader = new ContentReader();
		String filename = "res/geom/some.geom";
		InputStream input = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(filename);
		try {
			Content content = reader.read(input);
			lineEditor.getEditPane().setContent(content);
		} catch (Exception e) {
			logger.debug("unable to load startup geometry file", e);
			logger.debug("Exception: " + e.getClass().getSimpleName());
			logger.debug("Message: " + e.getMessage());
		}

	}

	public void showObjectDialog()
	{
		objectDialog.setVisible(true);
	}

	public void showContentDialog()
	{
		contentDialog.setVisible(true);
	}
}
