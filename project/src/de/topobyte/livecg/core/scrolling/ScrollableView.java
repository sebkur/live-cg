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
package de.topobyte.livecg.core.scrolling;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import de.topobyte.swing.layout.GridBagHelper;

public class ScrollableView<T extends JComponent & Viewport & HasScene & HasMargin>
		extends JPanel
{

	private static final long serialVersionUID = 1729551468089935167L;

	public ScrollableView(T view)
	{
		super(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		JScrollBar scrollerH = new JScrollBar(JScrollBar.HORIZONTAL);
		JScrollBar scrollerV = new JScrollBar(JScrollBar.VERTICAL);

		SceneBoundedRangeModel<T> rangeH = new SceneBoundedRangeModel<T>(view,
				true);
		scrollerH.setModel(rangeH);

		SceneBoundedRangeModel<T> rangeV = new SceneBoundedRangeModel<T>(view,
				false);
		scrollerV.setModel(rangeV);

		GridBagHelper.setGxGy(c, 0, 0);
		GridBagHelper.setWxWyF(c, 1.0, 1.0, GridBagConstraints.BOTH);
		add(view, c);

		GridBagHelper.setGxGy(c, 0, 1);
		GridBagHelper.setWxWyF(c, 1.0, 0.0, GridBagConstraints.BOTH);
		add(scrollerH, c);

		GridBagHelper.setGxGy(c, 1, 0);
		GridBagHelper.setWxWyF(c, 0.0, 1.0, GridBagConstraints.BOTH);
		add(scrollerV, c);
	}

}
