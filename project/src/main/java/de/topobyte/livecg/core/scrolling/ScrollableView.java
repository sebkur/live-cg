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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.KeyStroke;

import de.topobyte.swing.layout.GridBagHelper;

public class ScrollableView<T extends JComponent & ViewportWithSignals & HasScene & HasMargin>
		extends JPanel
{

	private static final long serialVersionUID = 1729551468089935167L;

	public ScrollableView(T view)
	{
		super(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS,
				InputEvent.CTRL_DOWN_MASK), "Ctrl++");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS,
				InputEvent.CTRL_DOWN_MASK), "Ctrl+-");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				InputEvent.CTRL_DOWN_MASK), "Ctrl+1");

		ActionMap actionMap = getActionMap();
		actionMap.put("Ctrl++", new ZoomAction<>(view, ZoomAction.Type.IN));
		actionMap.put("Ctrl+-", new ZoomAction<>(view, ZoomAction.Type.OUT));
		actionMap.put("Ctrl+1",
				new ZoomAction<>(view, ZoomAction.Type.IDENTITY));

		JScrollBar scrollerH = new JScrollBar(JScrollBar.HORIZONTAL);
		JScrollBar scrollerV = new JScrollBar(JScrollBar.VERTICAL);

		SceneBoundedRangeModel<T> rangeH = new SceneBoundedRangeModel<>(view,
				true);
		scrollerH.setModel(rangeH);

		SceneBoundedRangeModel<T> rangeV = new SceneBoundedRangeModel<>(view,
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

		PanMouseAdapter<T> panAdapter = new PanMouseAdapter<>(view);
		addMouseListener(panAdapter);
		addMouseMotionListener(panAdapter);
	}
}
