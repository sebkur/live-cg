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

package de.topobyte.livecg.geometry.ui.geometryeditor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.topobyte.livecg.geometry.ui.geom.Coordinate;
import de.topobyte.livecg.geometry.ui.geom.Editable;
import de.topobyte.livecg.geometry.ui.geom.Node;
import de.topobyte.livecg.geometry.ui.geom.Polygon;

public class Content
{

	private List<Editable> editables = new ArrayList<Editable>();
	private List<Polygon> polygons = new ArrayList<Polygon>();

	public List<Editable> getChains()
	{
		return editables;
	}

	public void addChain(Editable line)
	{
		editables.add(line);
	}

	public void removeChain(Editable line)
	{
		editables.remove(line);
	}

	public List<Polygon> getPolygons()
	{
		return polygons;
	}

	public void addPolygon(Polygon polygon)
	{
		polygons.add(polygon);
	}

	public void removePolygon(Polygon polygon)
	{
		polygons.remove(polygon);
	}

	public Set<Editable> getEditablesNear(Coordinate coordinate)
	{
		Set<Editable> results = new HashSet<Editable>();
		for (Editable line : editables) {
			if (line.hasPointWithinThreshold(coordinate, 4)) {
				results.add(line);
			}
		}
		return results;
	}

	private List<ContentChangedListener> contentListenerns = new ArrayList<ContentChangedListener>();

	public void addContentChangedListener(ContentChangedListener l)
	{
		contentListenerns.add(l);
	}

	public void removeContentChangedListener(ContentChangedListener l)
	{
		contentListenerns.remove(l);
	}

	public void fireContentChanged()
	{
		for (ContentChangedListener l : contentListenerns) {
			l.contentChanged();
		}
	}

	public Node getNearestNode(Coordinate coordinate)
	{
		double distance = Double.MAX_VALUE;
		Node nearestNode = null;
		for (Editable editable : editables) {
			Node n = editable.getNearestPoint(coordinate);
			double d = n.getCoordinate().distance(coordinate);
			if (d < distance) {
				distance = d;
				nearestNode = n;
			}
		}
		for (Polygon polygon : polygons) {
			Editable shell = polygon.getShell();
			Node n = shell.getNearestPoint(coordinate);
			double d = n.getCoordinate().distance(coordinate);
			if (d < distance) {
				distance = d;
				nearestNode = n;
			}
		}
		return nearestNode;
	}

	public Node getNearestDifferentNode(Coordinate coordinate, Node node)
	{
		double distance = Double.MAX_VALUE;
		Node nearestNode = null;
		for (Editable editable : editables) {
			Node n = editable.getNearestDifferentNode(node);
			if (n == node) {
				continue;
			}
			double d = n.getCoordinate().distance(coordinate);
			if (d < distance) {
				distance = d;
				nearestNode = n;
			}
		}
		for (Polygon polygon : polygons) {
			Editable shell = polygon.getShell();
			Node n = shell.getNearestDifferentNode(node);
			if (n == node) {
				continue;
			}
			double d = n.getCoordinate().distance(coordinate);
			if (d < distance) {
				distance = d;
				nearestNode = n;
			}
		}
		return nearestNode;
	}

	public Editable getNearestChain(Coordinate coordinate)
	{
		double distance = Double.MAX_VALUE;
		Editable nearest = null;
		for (Editable editable : editables) {
			double d = editable.distance(coordinate);
			if (d < distance) {
				distance = d;
				nearest = editable;
			}
		}
		return nearest;
	}

	public Polygon getNearestPolygon(Coordinate coordinate)
	{
		double distance = Double.MAX_VALUE;
		Polygon nearest = null;
		for (Polygon polygon : polygons) {
			Editable shell = polygon.getShell();
			double d = shell.distance(coordinate);
			if (d < distance) {
				distance = d;
				nearest = polygon;
			}
		}
		return nearest;
	}

}
