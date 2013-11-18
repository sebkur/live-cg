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

package de.topobyte.livecg.ui.misc;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AboutPanel extends JPanel
{

	final static Logger logger = LoggerFactory.getLogger(AboutPanel.class);

	static final long serialVersionUID = -3966446075575003843L;

	public AboutPanel()
	{
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;

		JScrollPane jsp = new JScrollPane();
		add(jsp, c);

		JEditorPane pane = new JEditorPane();
		jsp.setViewportView(pane);
		pane.setEditable(false);

		HTMLEditorKit kit = new HTMLEditorKit();
		pane.setEditorKit(kit);

		String filename = "res/manual.html";
		URL url = Thread.currentThread().getContextClassLoader()
				.getResource(filename);
		try {
			logger.debug("url: " + url);
			pane.setPage(url);
		} catch (IOException e) {
			logger.debug("unable to set page: " + e.getMessage());
		}
	}
}
