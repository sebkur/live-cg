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
package de.topobyte.livecg.algorithms.frechet.freespace;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.properties.BooleanCallback;
import de.topobyte.livecg.core.properties.PropertyParseHelper;

public class FreeSpacePropertyParser
{
	final static Logger logger = LoggerFactory
			.getLogger(FreeSpacePropertyParser.class);

	private FreeSpaceConfig config;

	public FreeSpacePropertyParser(FreeSpaceConfig config)
	{
		this.config = config;
	}

	public void parse(Properties properties)
	{
		PropertyParseHelper.parseBoolean(properties, "freespace-markers",
				new BooleanCallback() {

					@Override
					public void success(boolean value)
					{
						config.setDrawFreeSpaceMarkers(value);
					}
				});
		PropertyParseHelper.parseBoolean(properties, "reachable-markers",
				new BooleanCallback() {

					@Override
					public void success(boolean value)
					{
						config.setDrawReachableSpaceMarkers(value);
					}
				});
		PropertyParseHelper.parseBoolean(properties, "reachable-space",
				new BooleanCallback() {

					@Override
					public void success(boolean value)
					{
						config.setDrawReachableSpace(value);
					}
				});
	}

}
