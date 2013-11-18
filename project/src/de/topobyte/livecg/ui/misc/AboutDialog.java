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

import javax.swing.JDialog;
import javax.swing.JTabbedPane;

public class AboutDialog extends JDialog
{

	private static final long serialVersionUID = -4810886271113703904L;

	public final static int PAGE_ABOUT = 0;
	public final static int PAGE_LICENSE = 1;

	private AboutPanel aboutPanel;
	private LicensePanel licensePanel;

	public AboutDialog(int page)
	{
		setTitle("LiveCG");

		aboutPanel = new AboutPanel();
		licensePanel = new LicensePanel();

		JTabbedPane tabs = new JTabbedPane();
		tabs.add("About", aboutPanel);
		tabs.add("License", licensePanel);

		if (page == PAGE_ABOUT) {
			tabs.setSelectedIndex(0);
		} else if (page == PAGE_LICENSE) {
			tabs.setSelectedIndex(1);
		}

		setContentPane(tabs);
	}

	public static void showDialog(int page)
	{
		AboutDialog dialog = new AboutDialog(page);
		dialog.setLocationByPlatform(true);
		dialog.setSize(400, 400);
		dialog.setVisible(true);
	}
}
