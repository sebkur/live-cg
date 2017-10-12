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

package de.topobyte.livecg.ui.geometryeditor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.geometry.Rectangle;

public class Content extends SetOfGeometries
{

	private Rectangle scene;

	public Content()
	{
		this(600, 400);
	}

	public Content(double w, double h)
	{
		scene = new Rectangle(0, 0, w, h);
	}

	public Rectangle getScene()
	{
		return scene;
	}

	public void setScene(Rectangle scene)
	{
		this.scene = scene;
	}

	public Set<Chain> getChainsNear(Coordinate coordinate)
	{
		Set<Chain> results = new HashSet<>();
		for (Chain chain : chains) {
			if (chain.hasPointWithinThreshold(coordinate, 4)) {
				results.add(chain);
			}
		}
		return results;
	}

	private List<ContentChangedListener> contentListenerns = new ArrayList<>();

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

	public void fireDimensionChanged()
	{
		for (ContentChangedListener l : contentListenerns) {
			l.dimensionChanged();
		}
	}

	public Node getNearestNode(Coordinate coordinate)
	{
		double distance = Double.MAX_VALUE;
		Node nearestNode = null;
		for (Chain chain : chains) {
			Node n = chain.getNearestPoint(coordinate);
			double d = n.getCoordinate().distance(coordinate);
			if (d < distance) {
				distance = d;
				nearestNode = n;
			}
		}
		for (Polygon polygon : polygons) {
			Chain shell = polygon.getShell();
			Node n = shell.getNearestPoint(coordinate);
			double d = n.getCoordinate().distance(coordinate);
			if (d < distance) {
				distance = d;
				nearestNode = n;
			}
			for (Chain hole : polygon.getHoles()) {
				n = hole.getNearestPoint(coordinate);
				d = n.getCoordinate().distance(coordinate);
				if (d < distance) {
					distance = d;
					nearestNode = n;
				}
			}
		}
		return nearestNode;
	}

	public Node getNearestDifferentNode(Coordinate coordinate, Node node)
	{
		double distance = Double.MAX_VALUE;
		Node nearestNode = null;
		for (Chain chain : chains) {
			Node n = chain.getNearestDifferentNode(node);
			if (n == null || n == node) {
				continue;
			}
			double d = n.getCoordinate().distance(coordinate);
			if (d < distance) {
				distance = d;
				nearestNode = n;
			}
		}
		for (Polygon polygon : polygons) {
			Chain shell = polygon.getShell();
			Node n = shell.getNearestDifferentNode(node);
			if (n == node) {
				continue;
			}
			double d = n.getCoordinate().distance(coordinate);
			if (d < distance) {
				distance = d;
				nearestNode = n;
			}
			for (Chain hole : polygon.getHoles()) {
				n = hole.getNearestDifferentNode(node);
				if (n == node) {
					continue;
				}
				d = n.getCoordinate().distance(coordinate);
				if (d < distance) {
					distance = d;
					nearestNode = n;
				}
			}
		}
		return nearestNode;
	}

	public Chain getNearestChain(Coordinate coordinate)
	{
		double distance = Double.MAX_VALUE;
		Chain nearest = null;
		for (Chain chain : chains) {
			double d = chain.distance(coordinate);
			if (d < distance) {
				distance = d;
				nearest = chain;
			}
		}
		return nearest;
	}

	public Polygon getNearestPolygon(Coordinate coordinate)
	{
		double distance = Double.MAX_VALUE;
		Polygon nearest = null;
		for (Polygon polygon : polygons) {
			Chain shell = polygon.getShell();
			double d = shell.distance(coordinate);
			if (d < distance) {
				distance = d;
				nearest = polygon;
			}
		}
		return nearest;
	}

}
