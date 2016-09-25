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

package de.topobyte.livecg;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.config.LiveConfig;
import de.topobyte.livecg.core.geometry.io.ContentReader;
import de.topobyte.livecg.preferences.Configuration;
import de.topobyte.livecg.preferences.PreferenceManager;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditor;
import de.topobyte.livecg.ui.geometryeditor.Menu;
import de.topobyte.livecg.ui.geometryeditor.StatusBar;
import de.topobyte.livecg.ui.geometryeditor.Toolbar;
import de.topobyte.livecg.ui.geometryeditor.debug.ContentDialog;
import de.topobyte.livecg.ui.geometryeditor.mouse.StatusBarMouseListener;
import de.topobyte.livecg.ui.geometryeditor.object.ObjectDialog;
import de.topobyte.livecg.util.LocationUtil;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.parsing.ArgumentHelper;
import de.topobyte.utilities.apache.commons.cli.parsing.StringOption;

public class LiveCG
{

	static final Logger logger = LoggerFactory.getLogger(LiveCG.class);

	private static final String HELP_MESSAGE = LiveCG.class.getSimpleName()
			+ "[options] [file]";

	private static final String OPTION_CONFIG = "config";

	public static void main(String[] args)
	{
		// @formatter:off
		Options options = new Options();
		OptionHelper.addL(options, OPTION_CONFIG, true, false, "path", "config file");
		// @formatter:on

		CommandLineParser clp = new DefaultParser();

		CommandLine line = null;
		try {
			line = clp.parse(options, args);
		} catch (ParseException e) {
			System.err
					.println("Parsing command line failed: " + e.getMessage());
			new HelpFormatter().printHelp(HELP_MESSAGE, options);
			System.exit(1);
		}

		StringOption config = ArgumentHelper.getString(line, OPTION_CONFIG);
		if (config.hasValue()) {
			String configPath = config.getValue();
			LiveConfig.setPath(configPath);
		}

		Configuration configuration = PreferenceManager.getConfiguration();
		String lookAndFeel = configuration.getSelectedLookAndFeel();
		if (lookAndFeel == null) {
			lookAndFeel = UIManager.getSystemLookAndFeelClassName();
		}
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) {
			logger.error("error while setting look and feel '" + lookAndFeel
					+ "': " + e.getClass().getSimpleName() + ", message: "
					+ e.getMessage());
		}

		Content content = null;
		String filename = "res/presets/Startup.geom";

		String[] extra = line.getArgs();
		if (extra.length > 0) {
			filename = extra[0];
		}

		ContentReader reader = new ContentReader();
		InputStream input = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(filename);
		try {
			content = reader.read(input);
		} catch (Exception e) {
			logger.info("unable to load startup geometry file", e);
			logger.info("Exception: " + e.getClass().getSimpleName());
			logger.info("Message: " + e.getMessage());
		}

		final LiveCG runner = new LiveCG();
		final Content c = content;

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				runner.setup(true, c);
			}
		});
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				runner.frame.requestFocus();
			}
		});
	}

	private JFrame frame;
	private ObjectDialog objectDialog;
	private ContentDialog contentDialog;

	private boolean locationSetObjectDialog = false;
	private boolean locationSetContentDialog = false;

	private boolean showObjectDialog = false;
	private boolean showContentDialog = false;

	public LiveCG()
	{
		frame = new JFrame();
	}

	public JFrame getFrame()
	{
		return frame;
	}

	public void setup(boolean exitOnClose, Content content)
	{
		frame.setSize(800, 600);
		if (exitOnClose) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

		frame.setTitle("Live CG");

		GeometryEditor geometryEditor = new GeometryEditor();
		Menu menu = new Menu(this, geometryEditor.getEditPane(),
				geometryEditor.getEditPane());
		Toolbar toolbar = new Toolbar(geometryEditor.getEditPane(),
				geometryEditor.getEditPane());

		toolbar.setFloatable(false);

		StatusBar statusBar = new StatusBar();
		StatusBarMouseListener statusBarMouseListener = new StatusBarMouseListener(
				geometryEditor.getEditPane(), statusBar);
		geometryEditor.getEditPane().addMouseListener(statusBarMouseListener);
		geometryEditor.getEditPane().addMouseMotionListener(
				statusBarMouseListener);

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
		mainPanel.add(geometryEditor, c);

		c.gridy = 2;
		c.weighty = 0.0;
		mainPanel.add(statusBar, c);

		frame.setLocationByPlatform(true);
		frame.setVisible(true);

		objectDialog = new ObjectDialog(frame, geometryEditor.getEditPane());
		objectDialog.setSize(300, 300);

		contentDialog = new ContentDialog(frame, geometryEditor.getEditPane());
		contentDialog.setSize(300, 300);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				if (showObjectDialog) {
					showObjectDialog();
				}
				if (showContentDialog) {
					showContentDialog();
				}
			}
		});

		if (content != null) {
			geometryEditor.getEditPane().setContent(content);
		}
	}

	public void showObjectDialog()
	{
		if (!locationSetObjectDialog) {
			locationSetObjectDialog = true;
			LocationUtil.positionTopAlignedToTheRightTo(frame, objectDialog);
		}
		objectDialog.setVisible(true);
	}

	public void showContentDialog()
	{
		if (!locationSetContentDialog) {
			locationSetContentDialog = true;
			LocationUtil.positionTopAlignedToTheRightTo(frame, contentDialog);
		}
		contentDialog.setVisible(true);
	}

	public void applyConfiguration(Configuration configuration)
	{
		String lookAndFeel = configuration.getSelectedLookAndFeel();
		if (lookAndFeel == null) {
			lookAndFeel = UIManager.getSystemLookAndFeelClassName();
		}
		setLookAndFeel(configuration.getSelectedLookAndFeel());

		PreferenceManager.store(configuration);
	}

	private void setLookAndFeel(String lookAndFeel)
	{
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) {
			logger.error("error while setting look and feel '" + lookAndFeel
					+ "': " + e.getClass().getSimpleName() + ", message: "
					+ e.getMessage());
		}
		for (Window window : JFrame.getWindows()) {
			SwingUtilities.updateComponentTreeUI(window);
			// window.pack();
		}
	}
}
