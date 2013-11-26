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
package de.topobyte.livecg.ui.console;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.text.Position;

import de.topobyte.livecg.core.algorithm.Algorithm;
import de.topobyte.livecg.core.algorithm.AlgorithmWatcher;
import de.topobyte.livecg.core.algorithm.Explainable;
import de.topobyte.livecg.core.algorithm.HasStatusMarker;

public class AlgorithmOutputConsole extends OutputConsole implements
		AlgorithmWatcher
{

	private static final long serialVersionUID = -2733953495384438417L;

	private String newline = System.getProperty("line.separator");

	private Explainable explainable = null;
	private HasStatusMarker statusMarker = null;

	public AlgorithmOutputConsole(Algorithm algorithm)
	{
		algorithm.addAlgorithmWatcher(this);
		if (algorithm instanceof HasStatusMarker) {
			statusMarker = (HasStatusMarker) algorithm;
		}
		if (algorithm instanceof Explainable) {
			explainable = (Explainable) algorithm;
			appendExplanation();
		}
	}

	private SortedMap<String, Entry> positions = new TreeMap<String, Entry>();

	private void appendExplanation()
	{
		if (explainable == null || statusMarker == null) {
			return;
		}
		String marker = statusMarker.getMarker();
		System.out.println(marker);

		Entry entry = positions.get(marker);

		clearStyle();

		if (entry != null) {
			// Text already there, just highlight
			setEmphasized(entry.position, entry.length);
			show(entry.position.getOffset(), entry.length - 1);
			// show(entry.position.getOffset());
			return;
		}

		List<String> messages = explainable.explain();

		int position = 0;
		if (positions.size() > 0) {
			SortedMap<String, Entry> headMap = positions.headMap(marker);
			if (headMap.size() != 0) {
				Entry e = headMap.get(headMap.lastKey());
				position = e.position.getOffset() + e.length;
			}
		}
		int length = 0;
		for (String message : messages) {
			gotoPosition(position + length);
			insert(message);
			length += message.length();

			gotoPosition(position + length);
			insert(newline);
			length += newline.length();
		}

		show(position, length - 1);
		// show(position);

		Position posBefore = createPositionAt(position);

		entry = new Entry(posBefore, length);
		positions.put(marker, entry);

		for (java.util.Map.Entry<String, Entry> me : positions.entrySet()) {
			int offset = me.getValue().position.getOffset();
			int l = me.getValue().length;
			System.out.println(String.format("%s: %d->%d", me.getKey(), offset,
					offset + l));
		}
	}

	@Override
	public void updateAlgorithmStatus()
	{
		appendExplanation();
	}

}
