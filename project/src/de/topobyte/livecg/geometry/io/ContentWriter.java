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
package de.topobyte.livecg.geometry.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.topobyte.livecg.geometry.ui.geom.Editable;
import de.topobyte.livecg.geometry.ui.geom.Node;
import de.topobyte.livecg.geometry.ui.geom.Polygon;
import de.topobyte.livecg.geometry.ui.geometryeditor.Content;

public class ContentWriter
{

	private List<Editable> chains;
	private List<Polygon> polygons;
	private OutputStream output;

	public void write(Content content, File file) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		write(content, bos);
		bos.close();
	}

	public void write(Content content, OutputStream output) throws IOException
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

	private void writeNodes() throws IOException
	{
		for (Editable chain : chains) {
			writeNodes(chain);
		}
		for (Polygon polygon : polygons) {
			Editable shell = polygon.getShell();
			writeNodes(shell);
		}
	}

	private Map<Node, Integer> nodeToId = new HashMap<Node, Integer>();

	private void writeNodes(Editable chain) throws IOException
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

	private void writeChains() throws IOException
	{
		for (Editable chain : chains) {
			writeChain(chain);
		}
	}

	private void writePolygons() throws IOException
	{
		for (Polygon polygon : polygons) {
			writePolygon(polygon);
		}
	}

	private void writeChain(Editable chain) throws IOException
	{
		StringBuilder buffer = buildChainBuffer(chain);
		String text = "\n  <chain>" + buffer + "</chain>";
		output.write(text.getBytes());
	}

	private void writePolygon(Polygon polygon) throws IOException
	{
		StringBuilder buffer = buildChainBuffer(polygon.getShell());
		output.write("\n  <polygon>".getBytes());
		String text = "\n    <chain>" + buffer + "</chain>";
		output.write(text.getBytes());
		output.write("\n  </polygon>".getBytes());
	}

	private StringBuilder buildChainBuffer(Editable chain)
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
