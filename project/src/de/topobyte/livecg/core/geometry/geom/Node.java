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
package de.topobyte.livecg.core.geometry.geom;

import java.util.ArrayList;
import java.util.List;

public class Node
{

	private Coordinate coordinate;
	private List<Chain> endpointChains = new ArrayList<Chain>();;
	private List<Chain> chains = new ArrayList<Chain>();;

	public Node(Coordinate coordinate)
	{
		this.coordinate = coordinate;
	}

	public Coordinate getCoordinate()
	{
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate)
	{
		this.coordinate = coordinate;
	}

	public List<Chain> getChains()
	{
		return chains;
	}

	public List<Chain> getEndpointChains()
	{
		return endpointChains;
	}

	public void addChain(Chain chain)
	{
		chains.add(chain);
	}

	public void removeChain(Chain chain)
	{
		chains.remove(chain);
	}

	public void addEndpointChain(Chain chain)
	{
		endpointChains.add(chain);
	}

	public void removeEndpointChain(Chain chain)
	{
		endpointChains.remove(chain);
	}
}
