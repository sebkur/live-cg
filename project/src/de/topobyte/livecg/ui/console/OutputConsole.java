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
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Position;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputConsole extends JPanel
{

	private static final long serialVersionUID = 5922951928480514060L;

	final static Logger logger = LoggerFactory.getLogger(OutputConsole.class);

	private JTextPane output = new JTextPane();

	private StyledDocument doc = new DefaultStyledDocument();

	private static final String styleDefault = "base";
	private static final String styleEmphasis = "emph";

	public OutputConsole()
	{
		setLayout(new BorderLayout());

		output = new JTextPane(doc);
		output.setEditable(false);
		// output.setDocument(doc);

		JScrollPane jsp = new JScrollPane();
		jsp.setViewportView(output);

		add(jsp, BorderLayout.CENTER);

		StyleContext sc = StyleContext.getDefaultStyleContext();

		Style def = sc.getStyle(StyleContext.DEFAULT_STYLE);

		Style base = doc.addStyle(styleDefault, def);
		Style emphasis = doc.addStyle(styleEmphasis, def);

		StyleConstants.setItalic(base, true);
		StyleConstants.setBold(emphasis, true);
	}

	protected void append(String text)
	{
		try {
			doc.insertString(doc.getLength(), text, doc.getStyle(styleEmphasis));
		} catch (BadLocationException e) {
			logger.error("Error during document text insertion: "
					+ e.getMessage());
		}
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

		Position end = doc.getEndPosition();
		output.setCaretPosition(end.getOffset() - 1);
	}

	protected void clearStyle()
	{
		doc.setCharacterAttributes(0, doc.getLength(),
				doc.getStyle(styleDefault), true);
	}
}
