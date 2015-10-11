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
package de.topobyte.livecg.datastructures.dcel;

import de.topobyte.livecg.core.export.SizeProvider;
import de.topobyte.livecg.core.geometry.dcel.DCEL;
import de.topobyte.livecg.core.geometry.dcel.DcelUtil;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.geom.Rectangles;
import de.topobyte.livecg.core.scrolling.ScenePanel;

public class DcelPanel extends ScenePanel implements SizeProvider
{

	private static final long serialVersionUID = 8978186265217218174L;

	private DcelConfig config;

	public DcelPanel(DCEL dcel, DcelConfig config, int margin)
	{
		super(scene(dcel, margin));
		this.config = config;

		visualizationPainter = new InstanceDcelPainter(scene, dcel, config, painter);
	}

	private static Rectangle scene(DCEL dcel, int margin)
	{
		Rectangle bbox = DcelUtil.getBoundingBox(dcel);
		Rectangle scene = Rectangles.extend(bbox, margin);
		return scene;
	}

	public DcelConfig getConfig()
	{
		return config;
	}

	public void settingsUpdated()
	{
		repaint();
	}

}
