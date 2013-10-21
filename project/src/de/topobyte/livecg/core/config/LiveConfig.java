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
package de.topobyte.livecg.core.config;

import java.io.IOException;

import de.topobyte.livecg.core.painting.Color;

public class LiveConfig
{
	private static final String DEFAULT_PATH = "res/config";

	private static Configuration config = null;
	private static String path = DEFAULT_PATH;

	public static void setPath(String path)
	{
		LiveConfig.path = path;
	}
	
	public static Color getColor(String key)
	{
		initialize();
		Color color = config.getColor(key);
		if (color != null) {
			return color;
		}
		return new Color(0x000000);
	}

	private static void initialize()
	{
		if (config == null) {
			try {
				config = ConfigParser.parse(path);
			} catch (IOException e) {
				System.out.println("unable to load configuration");
			}
		}
	}

}