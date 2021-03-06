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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.ui.geometryeditor.SetOfGeometries;

public class SetOfGeometryWriter
{

	protected List<Chain> chains;
	protected List<Polygon> polygons;
	protected OutputStream output;

	public void write(SetOfGeometries content, File file) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		write(content, bos);
		bos.close();
	}

	public void write(SetOfGeometries content, OutputStream output)
			throws IOException
	{
		this.output = output;
		chains = content.getChains();
		polygons = content.getPolygons();

		output.write("<data>".getBytes());
		writeNodes();
		writeChains();
		writePolygons();
		output.write("\n</data>\n".getBytes());
	}

	protected void writeNodes() throws IOException
	{
		for (Chain chain : chains) {
			writeNodes(chain);
		}
		for (Polygon polygon : polygons) {
			Chain shell = polygon.getShell();
			writeNodes(shell);
			for (Chain hole : polygon.getHoles()) {
				writeNodes(hole);
			}
		}
	}

	private Map<Node, Integer> nodeToId = new HashMap<>();

	private void writeNodes(Chain chain) throws IOException
	{
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			Node node = chain.getNode(i);
			if (nodeToId.containsKey(node)) {
				continue;
			}
			int id = nodeToId.size() + 1;
			nodeToId.put(node, id);
			writeNode(node, id);
		}
	}

	private void writeNode(Node node, int id) throws IOException
	{
		String text = String.format(Locale.US,
				"\n  <node id=\"%d\" x=\"%f\" y=\"%f\" />", id, node
						.getCoordinate().getX(), node.getCoordinate().getY());
		output.write(text.getBytes());
	}

	protected void writeChains() throws IOException
	{
		for (Chain chain : chains) {
			writeChain(chain);
		}
	}

	protected void writePolygons() throws IOException
	{
		for (Polygon polygon : polygons) {
			writePolygon(polygon);
		}
	}

	private void writeChain(Chain chain) throws IOException
	{
		StringBuilder buffer = buildChainBuffer(chain);
		String closed = chain.isClosed() ? "true" : "false";
		String text = "\n  <chain closed=\"" + closed + "\">" + buffer
				+ "</chain>";
		output.write(text.getBytes());
	}

	private void writePolygon(Polygon polygon) throws IOException
	{
		output.write("\n  <polygon>".getBytes());
		StringBuilder buffer = buildChainBuffer(polygon.getShell());
		String text = "\n    <shell>" + buffer + "</shell>";
		output.write(text.getBytes());
		for (Chain hole : polygon.getHoles()) {
			buffer = buildChainBuffer(hole);
			text = "\n    <hole>" + buffer + "</hole>";
			output.write(text.getBytes());
		}
		output.write("\n  </polygon>".getBytes());
	}

	private StringBuilder buildChainBuffer(Chain chain)
	{
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			Node node = chain.getNode(i);
			int id = nodeToId.get(node);
			if (i > 0) {
				buffer.append(" ");
			}
			buffer.append(id);
		}
		return buffer;
	}
}
