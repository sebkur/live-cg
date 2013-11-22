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
package de.topobyte.livecg.ui.console;

import java.util.List;

import de.topobyte.livecg.core.algorithm.Algorithm;
import de.topobyte.livecg.core.algorithm.AlgorithmWatcher;
import de.topobyte.livecg.core.algorithm.Explainable;

public class AlgorithmOutputConsole extends OutputConsole implements
		AlgorithmWatcher
{

	private static final long serialVersionUID = -2733953495384438417L;

	private String newline = System.getProperty("line.separator");

	private Explainable explainable = null;

	public AlgorithmOutputConsole(Algorithm algorithm)
	{
		algorithm.addAlgorithmWatcher(this);
		if (algorithm instanceof Explainable) {
			explainable = (Explainable) algorithm;
			appendExplanation();
		}
	}

	private void appendExplanation()
	{
		List<String> messages = explainable.explain();
		for (String message : messages) {
			push(message);
			pushToPreBuffer(newline);
		}
	}

	@Override
	public void updateAlgorithmStatus()
	{
		if (explainable == null) {
			return;
		}
		appendExplanation();
	}

}
