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

import java.util.prefs.Preferences;

public class Configuration
{

	static final String PROP_LAF = "look-and-feel";

	private String selectedLookAndFeel = null;

	public Configuration(Preferences preferences)
	{
		selectedLookAndFeel = preferences.get(PROP_LAF, null);
	}

	public void store(Preferences preferences)
	{
		removeOrStore(preferences, PROP_LAF, selectedLookAndFeel);
	}

	private void removeOrStore(Preferences preferences, String property,
			String value)
	{
		if (value == null) {
			preferences.remove(property);
		} else {
			preferences.put(property, value);
		}
	}

	/*
	 * Getters, Setters
	 */

	public String getSelectedLookAndFeel()
	{
		return selectedLookAndFeel;
	}

	public void setSelectedLookAndFeel(String selectedLookAndFeel)
	{
		this.selectedLookAndFeel = selectedLookAndFeel;
	}

}
