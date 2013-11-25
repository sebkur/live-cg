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
package de.topobyte.livecg.core.algorithm.steps;

import java.util.List;

public class StepUtil
{

	public static int totalNumberOfSteps(List<Step> steps)
	{
		int s = 0;
		for (Step step : steps) {
			if (step instanceof RepeatedStep) {
				RepeatedStep repeated = (RepeatedStep) step;
				s += repeated.howOften();
			} else {
				s += 1;
			}
		}
		return s;
	}
}
