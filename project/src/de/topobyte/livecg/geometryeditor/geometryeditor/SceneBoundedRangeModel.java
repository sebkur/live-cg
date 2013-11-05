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
package de.topobyte.livecg.geometryeditor.geometryeditor;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SceneBoundedRangeModel implements BoundedRangeModel
{

	final static Logger logger = LoggerFactory
			.getLogger(SceneBoundedRangeModel.class);

	private GeometryEditPane editPane;

	private boolean horizontal;

	public SceneBoundedRangeModel(GeometryEditPane editPane, boolean horizontal)
	{
		this.editPane = editPane;
		this.horizontal = horizontal;
		editPane.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e)
			{
				editPaneResized();
			}

		});
		editPane.addViewportListener(new ViewportListener() {

			@Override
			public void zoomChanged()
			{
				editPaneZoomChanged();
			}

			@Override
			public void viewportChanged()
			{
				// ignore
			}

			@Override
			public void complexChange()
			{
				SceneBoundedRangeModel.this.complexChange();
			}
		});
	}

	protected void editPaneResized()
	{
		fireListeners();
	}

	protected void editPaneZoomChanged()
	{
		fireListeners();
	}

	protected void complexChange()
	{
		fireListeners();
	}

	@Override
	public int getMinimum()
	{
		// logger.debug("getMinimum()");
		return (int) Math.round(0 - GeometryEditPane.MARGIN
				* editPane.getZoom());
	}

	@Override
	public int getMaximum()
	{
		// logger.debug("getMaximum()");
		if (horizontal) {
			return (int) Math.round((editPane.getContent().getScene()
					.getWidth() + GeometryEditPane.MARGIN)
					* editPane.getZoom());
		} else {
			return (int) Math.round((editPane.getContent().getScene()
					.getHeight() + GeometryEditPane.MARGIN)
					* editPane.getZoom());
		}
	}

	@Override
	public int getExtent()
	{
		// logger.debug("getExtent()");
		if (horizontal) {
			return (int) Math.round(editPane.getWidth());
		} else {
			return (int) Math.round(editPane.getHeight());
		}
	}

	@Override
	public int getValue()
	{
		// logger.debug("getValue()");
		if (horizontal) {
			return (int) Math.round(-editPane.getPositionX()
					* editPane.getZoom());
		} else {
			return (int) Math.round(-editPane.getPositionY()
					* editPane.getZoom());
		}
	}

	@Override
	public void setValue(int newValue)
	{
		newValue = Math.min(newValue, getMaximum() - getExtent());
		newValue = Math.max(newValue, getMinimum());
		logger.debug("setValue(" + newValue + ")");
		if (horizontal) {
			editPane.setPositionX(-newValue / editPane.getZoom());
		} else {
			editPane.setPositionY(-newValue / editPane.getZoom());
		}
		editPane.repaint();
		fireListeners();
	}

	@Override
	public void setMinimum(int newMinimum)
	{
		// ignore
		logger.debug("setMinimum(" + newMinimum + ")");
	}

	@Override
	public void setMaximum(int newMaximum)
	{
		// ignore
		logger.debug("setMaximum(" + newMaximum + ")");
	}

	@Override
	public void setExtent(int newExtent)
	{
		// ignore
		logger.debug("setExtend(" + newExtent + ")");
	}

	@Override
	public void setRangeProperties(int value, int extent, int min, int max,
			boolean adjusting)
	{
		// ignore
		logger.debug(String.format("setRangeProperties(%d, %d, %d, %d, %b)",
				value, extent, min, max, adjusting));
	}

	private List<ChangeListener> listeners = new ArrayList<ChangeListener>();

	@Override
	public void addChangeListener(ChangeListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public void removeChangeListener(ChangeListener listener)
	{
		listeners.remove(listener);
	}

	private void fireListeners()
	{
		ChangeEvent event = new ChangeEvent(this);
		for (ChangeListener listener : listeners) {
			listener.stateChanged(event);
		}
	}

	private boolean adjusting = false;

	@Override
	public void setValueIsAdjusting(boolean b)
	{
		logger.debug("setValueIsAdjusting(" + b + ")");
		adjusting = b;
		fireListeners();
	}

	@Override
	public boolean getValueIsAdjusting()
	{
		return adjusting;
	}

}
