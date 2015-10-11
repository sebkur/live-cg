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

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import de.topobyte.livecg.preferences.Configuration;

public class LAFSelector extends JComboBox
{

	private static final long serialVersionUID = 6856865390726849784L;

	public LAFSelector(Configuration configuration)
	{
		super(buildValues());

		setRenderer(new Renderer());
		setEditable(false);

		String lookAndFeel = configuration.getSelectedLookAndFeel();
		setSelectedIndex(-1);
		for (int i = 0; i < getModel().getSize(); i++) {
			LookAndFeelInfo info = (LookAndFeelInfo) getModel().getElementAt(i);
			if (info.getClassName().equals(lookAndFeel)) {
				setSelectedIndex(i);
				break;
			}
		}
	}

	private static LookAndFeelInfo[] buildValues()
	{
		LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
		return lafs;
	}

	private class Renderer extends BasicComboBoxRenderer
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
		{
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);

			if (value != null) {
				LookAndFeelInfo item = (LookAndFeelInfo) value;
				setText(item.getName());
			} else {
				setText("default");
			}

			return this;
		}
	}
}
