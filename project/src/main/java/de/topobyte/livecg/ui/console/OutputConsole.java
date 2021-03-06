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
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
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

	protected StyledDocument doc = new DefaultStyledDocument();

	private static final String STYLE_DEFAULT = "base";
	private static final String STYLE_EMPHASIS = "emph";

	public OutputConsole()
	{
		/*
		 * Layout
		 */
		setLayout(new BorderLayout());

		output = new JTextPane(doc);
		output.setEditable(false);

		JScrollPane jsp = new JScrollPane();
		jsp.setViewportView(output);

		add(jsp, BorderLayout.CENTER);

		/*
		 * Text styles
		 */
		StyleContext sc = StyleContext.getDefaultStyleContext();

		Style def = sc.getStyle(StyleContext.DEFAULT_STYLE);

		Style base = doc.addStyle(STYLE_DEFAULT, def);
		Style emphasis = doc.addStyle(STYLE_EMPHASIS, def);

		StyleConstants.setItalic(base, true);
		StyleConstants.setBold(emphasis, true);
	}

	protected void clearStyle()
	{
		doc.setCharacterAttributes(0, doc.getLength(),
				doc.getStyle(STYLE_DEFAULT), true);
	}

	protected Position createPositionAt(int offset)
	{
		try {
			return doc.createPosition(offset);
		} catch (BadLocationException e) {
			// This should be impossible
			return null;
		}
	}

	private int pos = 0;

	protected void gotoPosition(int pos)
	{
		this.pos = pos;
	}

	protected void insert(String text)
	{
		try {
			doc.insertString(pos, text, doc.getStyle(STYLE_EMPHASIS));
		} catch (BadLocationException e) {
			logger.error("Error during document text insertion: "
					+ e.getMessage());
		}
	}

	protected void setEmphasized(Position a, int length)
	{
		int offset = a.getOffset();
		doc.setCharacterAttributes(offset, length,
				doc.getStyle(STYLE_EMPHASIS), true);
	}

	protected void show(int position, int length)
	{
		try {
			final Rectangle r1 = output.modelToView(position);
			final Rectangle r2 = output.modelToView(position + length);
			if (r1 != null && r2 != null) {
				Rectangle.union(r1, r2, r1);
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run()
					{
						output.scrollRectToVisible(r1);
					}
				});
			}
		} catch (BadLocationException e) {
			// This should not happen
		}
	}
}
