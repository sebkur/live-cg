/* This file is part of LiveCG.
 *
 * Copyright (C) 2014  Sebastian Kuerten
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
package de.topobyte.livecg.util.swing;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class KeyValueComboBox<K, V> extends JComboBox
{

	private static final long serialVersionUID = -1L;

	public KeyValueComboBox(K[] names, V[] values, Integer selectedValue)
	{
		super(buildValues(names, values));

		setRenderer(new Renderer());
		setEditable(false);

		setSelectedIndex(-1);
		for (int i = 0; i < values.length; i++) {
			if (values[i] == selectedValue) {
				setSelectedIndex(i);
				break;
			}
		}
	}

	public void setMinPreferredWidth(int minWidth)
	{
		Dimension ps = getPreferredSize();
		if (ps.width < minWidth) {
			setPreferredSize(new Dimension(minWidth, ps.height));
		}
	}

	private static <K, V> Data<K, V>[] buildValues(K[] names, V[] values)
	{
		Data<K, V>[] data = new Data[names.length];
		for (int i = 0; i < names.length; i++) {
			data[i] = new Data<K, V>(names[i], values[i]);
		}
		return data;
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
				Data<K, V> item = (Data) value;
				setText(item.name.toString());
			} else {
				setText("default");
			}

			return this;
		}
	}

	public V getSelectedValue()
	{
		int index = getSelectedIndex();
		if (index < 0) {
			return null;
		}
		Data<K, V> data = (Data) getItemAt(index);
		if (data == null) {
			return null;
		}
		return data.value;
	}

	private static class Data<K, V>
	{
		K name;
		V value;

		public Data(K name, V value)
		{
			this.name = name;
			this.value = value;
		}

	}
}
