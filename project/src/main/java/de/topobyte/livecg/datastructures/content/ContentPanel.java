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
package de.topobyte.livecg.datastructures.content;

import de.topobyte.livecg.core.export.SizeProvider;
import de.topobyte.livecg.core.scrolling.ScenePanel;
import de.topobyte.livecg.ui.geometryeditor.Content;

public class ContentPanel extends ScenePanel implements SizeProvider
{

	private static final long serialVersionUID = -5359677679398120484L;

	private ContentConfig config;

	public ContentPanel(Content content, ContentConfig config, int margin)
	{
		super(content.getScene());
		this.config = config;

		visualizationPainter = new ContentPainter(scene, content, config,
				painter);
	}

	public ContentConfig getConfig()
	{
		return config;
	}

	public void settingsUpdated()
	{
		repaint();
	}

}
