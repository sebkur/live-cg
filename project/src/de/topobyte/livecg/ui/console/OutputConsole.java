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

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;
import javax.swing.text.Position;

public class OutputConsole extends JPanel
{

	private static final long serialVersionUID = 5922951928480514060L;

	protected JTextArea output = new JTextArea();

	public OutputConsole()
	{
		setLayout(new BorderLayout());

		output.setEditable(false);

		JScrollPane jsp = new JScrollPane();
		jsp.setViewportView(output);

		add(jsp, BorderLayout.CENTER);
	}

	protected void append(String text)
	{
		output.append(text);
	}

	private List<String> preBuffer = new ArrayList<String>();

	private void emptyPreBuffer()
	{
		for (String text : preBuffer) {
			append(text);
		}
		preBuffer.clear();
	}

	public void pushToPreBuffer(String text)
	{
		preBuffer.add(text);
	}

	public void push(String text)
	{
		emptyPreBuffer();
		append(text);

		Document document = output.getDocument();
		Position end = document.getEndPosition();
		output.setCaretPosition(end.getOffset() - 1);
	}
}
