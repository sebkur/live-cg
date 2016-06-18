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
package de.topobyte.livecg.core.geometry.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.CloseabilityException;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.ui.geometryeditor.SetOfGeometries;

public class SetOfGeometryReader extends DefaultHandler
{

	static final Logger logger = LoggerFactory
			.getLogger(SetOfGeometryReader.class);

	protected SetOfGeometries content;

	public SetOfGeometries read(File file) throws IOException,
			ParserConfigurationException, SAXException
	{
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		SetOfGeometries content = read(bis);
		bis.close();
		return content;
	}

	public SetOfGeometries read(InputStream input) throws IOException,
			ParserConfigurationException, SAXException
	{
		content = new SetOfGeometries();

		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.parse(input, this);

		return content;
	}

	protected enum Element {

		Data,
		Unknown,
		Node,
		Chain,
		Polygon,
		Shell,
		Hole

	}

	// Store the path within the XML document int this Stack
	protected Stack<Element> position = new Stack<Element>();
	// Collect text received via 'characters()' in this buffer
	private StringBuffer buffer = new StringBuffer();
	// Map ids of nodes to node instances
	private Map<Integer, Node> idToNode = new HashMap<Integer, Node>();
	// A temporary storage for a chain's node references
	private List<Integer> ids = new ArrayList<Integer>();
	// A temporary storage for chain's closed attribute
	private boolean closed = false;
	// Store polygon's shell here
	private Chain shell = null;
	// Store polygon's holes here;
	private List<Chain> holes = null;

	/*
	 * SAX callbacks
	 */

	@Override
	public void endDocument() throws SAXException
	{
		// Nothing to do at the moment
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException
	{
		int depth = position.size();
		buffer.setLength(0);
		if (position.isEmpty()) {
			if (qName.equals("data")) {
				position.push(Element.Data);
			}
		} else if (position.size() == 1 && position.peek() == Element.Data) {
			if (qName.equals("node")) {
				position.push(Element.Node);
				String sid = attributes.getValue("id");
				String sx = attributes.getValue("x");
				String sy = attributes.getValue("y");
				int id = Integer.valueOf(sid);
				double x = Double.valueOf(sx);
				double y = Double.valueOf(sy);
				addNode(id, x, y);
			} else if (qName.equals("chain")) {
				position.push(Element.Chain);
				parseChain(attributes);
			} else if (qName.equals("polygon")) {
				position.push(Element.Polygon);
				shell = null;
				holes = new ArrayList<Chain>();
			}
		} else if (position.peek() == Element.Polygon) {
			if (qName.equals("shell")) {
				position.push(Element.Shell);
				parseChain(attributes);
			} else if (qName.equals("hole")) {
				position.push(Element.Hole);
				parseChain(attributes);
			}
		}
		if (depth == position.size()) {
			logger.info("unknown tag in input file: '" + qName + "'");
			position.push(Element.Unknown);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException
	{
		Element element = position.pop();
		switch (element) {
		default:
		case Data:
		case Unknown:
		case Node:
			// Nothing to do for these
			break;
		case Chain:
		case Shell:
		case Hole:
			parseChainText();
			Chain chain;
			try {
				chain = buildChain();
			} catch (CloseabilityException e) {
				throw new SAXException(
						"A chain marked as closed was not closeable", e);
			}
			if (position.peek() == Element.Data) {
				content.addChain(chain);
			} else if (position.peek() == Element.Polygon) {
				try {
					chain.setClosed(true);
				} catch (CloseabilityException e) {
					throw new SAXException(
							"A ring of a polygon was not closeable", e);
				}
				if (element == Element.Shell) {
					shell = chain;
				} else if (element == Element.Hole) {
					holes.add(chain);
				}
			}
			break;
		case Polygon:
			Polygon polygon = new Polygon(shell, holes);
			content.addPolygon(polygon);
			break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException
	{
		buffer.append(ch, start, length);
	}

	/*
	 * Helper methods
	 */

	private void addNode(int id, double x, double y)
	{
		Coordinate coordinate = new Coordinate(x, y);
		Node node = new Node(coordinate);
		idToNode.put(id, node);
	}

	private void parseChain(Attributes attributes)
	{
		closed = false;
		String sClosed = attributes.getValue("closed");
		if (sClosed != null) {
			String lower = sClosed.toLowerCase();
			closed = lower.equals("true") || lower.equals("yes");
		}
	}

	private void parseChainText()
	{
		ids.clear();
		String text = buffer.toString();
		String[] parts = text.split(" ");
		for (String part : parts) {
			int n = Integer.valueOf(part);
			ids.add(n);
		}
	}

	private Chain buildChain() throws CloseabilityException
	{
		Chain chain = new Chain();
		for (int id : ids) {
			Node node = idToNode.get(id);
			chain.appendNode(node);
		}
		if (closed) {
			chain.setClosed(closed);
		}
		return chain;
	}
}
