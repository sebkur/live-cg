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
package de.topobyte.livecg.algorithms.polygon.shortestpath;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.algorithms.frechet.distanceterrain.DistanceTerrainPropertyParser;
import de.topobyte.livecg.core.properties.BooleanCallback;
import de.topobyte.livecg.core.properties.PropertyParseHelper;

public class ShortestPathPropertyParser
{

	final static Logger logger = LoggerFactory
			.getLogger(DistanceTerrainPropertyParser.class);

	private ShortestPathConfig config;
	private Integer start = null;
	private Integer target = null;

	public ShortestPathPropertyParser(ShortestPathConfig config)
	{
		this.config = config;
	}

	public Integer getStart()
	{
		return start;
	}

	public Integer getTarget()
	{
		return target;
	}

	public void parse(Properties properties)
	{
		PropertyParseHelper.parseBoolean(properties, "dualgraph",
				new BooleanCallback() {

					@Override
					public void success(boolean value)
					{
						config.setDrawDualGraph(value);
					}
				});
		String nodes = properties.getProperty("nodes");
		if (nodes != null) {
			parseNodes(nodes);
		}
	}

	private void parseNodes(String nodes)
	{
		String[] parts = nodes.split(";");
		if (parts.length != 2) {
			return;
		}
		String n1 = parts[0];
		String n2 = parts[1];
		start = parseNode(n1);
		target = parseNode(n2);
	}

	private Integer parseNode(String n)
	{
		if (n.startsWith("n")) {
			String value = n.substring(1);
			return Integer.parseInt(value);
		}
		return null;
	}

}
