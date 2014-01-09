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
package de.topobyte.livecg.algorithms.jts.buffer;

import de.topobyte.livecg.algorithms.frechet.distanceterrain.ConfigChangedListener;
import de.topobyte.livecg.core.export.SizeProvider;
import de.topobyte.livecg.core.scrolling.ScenePanel;

public class BufferPanel extends ScenePanel implements SizeProvider,
		ConfigChangedListener
{

	private static final long serialVersionUID = 479795971427192954L;

	private BufferPainter algorithmPainter;

	private BufferAlgorithm algorithm;

	public BufferPanel(BufferAlgorithm algorithm, BufferConfig config)
	{
		super(algorithm.getScene());
		this.algorithm = algorithm;
		algorithmPainter = new BufferPainter(algorithm, config, painter);
		super.algorithmPainter = algorithmPainter;
		config.addConfigChangedListener(this);
	}

	@Override
	public void configChanged()
	{
		this.scene = algorithm.getScene();
		algorithmPainter.setScene(algorithm.getScene());
		checkBounds();
		repaint();
	}
}
