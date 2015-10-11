/* This file is part of LiveCG.
 *
 * Copyright (C) 2014  Sebastian Kuerten
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
package de.topobyte.livecg.util.controls;

public enum Icons {
	SKIP_FORWARD, SKIP_BACKWARD, SEEK_FORWARD, SEEK_BACKWARD;

	public static String getPath(Icons icon)
	{
		switch (icon) {
		case SEEK_BACKWARD:
			return "res/images/24x24/media-seek-backward.png";
		case SEEK_FORWARD:
			return "res/images/24x24/media-seek-forward.png";
		case SKIP_BACKWARD:
			return "res/images/24x24/media-skip-backward.png";
		case SKIP_FORWARD:
			return "res/images/24x24/media-skip-forward.png";
		default:
			return null;
		}
	}
}
