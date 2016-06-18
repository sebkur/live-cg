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
package de.topobyte.livecg.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CloneUtil
{
	public static <T> List<T> clone(List<T> input)
	{
		if (input == null) {
			return null;
		}
		List<T> copy = new ArrayList<>();
		for (T object : input) {
			copy.add(object);
		}
		return copy;
	}

	public static <K, V> Map<K, V> clone(Map<K, V> input)
	{
		if (input == null) {
			return null;
		}
		Map<K, V> copy = new HashMap<>();
		for (Entry<K, V> entry : input.entrySet()) {
			copy.put(entry.getKey(), entry.getValue());
		}
		return copy;
	}
}
