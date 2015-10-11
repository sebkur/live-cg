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
package de.topobyte.livecg.core.algorithm;

import java.util.ArrayList;
import java.util.List;

public class DefaultAlgorithm implements Algorithm
{

	private List<AlgorithmWatcher> aws = new ArrayList<AlgorithmWatcher>();
	private List<AlgorithmChangedListener> acls = new ArrayList<AlgorithmChangedListener>();

	@Override
	public void addAlgorithmWatcher(AlgorithmWatcher listener)
	{
		aws.add(listener);
	}

	@Override
	public void removeAlgorithmWatcher(AlgorithmWatcher listener)
	{
		aws.remove(listener);
	}

	@Override
	public void addAlgorithmChangedListener(AlgorithmChangedListener listener)
	{
		acls.add(listener);
	}

	@Override
	public void remvoeAlgorithmChangedListener(AlgorithmChangedListener listener)
	{
		acls.remove(listener);
	}

	protected void fireAlgorithmStatusChanged()
	{
		for (AlgorithmWatcher listener : aws) {
			listener.updateAlgorithmStatus();
		}
	}

	protected void fireAlgorithmChanged()
	{
		for (AlgorithmChangedListener listener : acls) {
			listener.algorithmChanged();
		}
	}

}
