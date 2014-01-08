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
package de.topobyte.livecg.core.properties;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyParseHelper
{
	final static Logger logger = LoggerFactory
			.getLogger(PropertyParseHelper.class);

	public static void parseBoolean(Properties properties, String name,
			BooleanCallback callback)
	{
		try {
			String property = properties.getProperty(name);
			if (property == null) {
				return;
			}
			boolean value = PropertyParser.parseBooleanProperty(property);
			callback.success(value);
		} catch (PropertyParseException e) {
			logger.warn("Problem while parsing property '" + name + "': "
					+ e.getMessage());
		}
	}

	public static void parseInteger(Properties properties, String name,
			IntegerCallback callback)
	{
		try {
			String property = properties.getProperty(name);
			if (property == null) {
				return;
			}
			int value = PropertyParser.parseIntegerProperty(property);
			callback.success(value);
		} catch (PropertyParseException e) {
			logger.warn("Problem while parsing property '" + name + "': "
					+ e.getMessage());
		}
	}
}
