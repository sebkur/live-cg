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
package de.topobyte.livecg.preferences;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreferenceManager
{

	private static final String PREFERNCE_NODE = "livecg";

	final static Logger logger = LoggerFactory
			.getLogger(PreferenceManager.class);

	public static Configuration getConfiguration()
	{
		Preferences preferences = Preferences.userRoot().node(PREFERNCE_NODE);

		Configuration configuration = new Configuration(preferences);

		return configuration;
	}

	public static void store(Configuration configuration)
	{
		Preferences preferences = Preferences.userRoot().node(PREFERNCE_NODE);
		configuration.store(preferences);

		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			logger.error("unable to store preferences: " + e.getMessage());
		}
	}

}
