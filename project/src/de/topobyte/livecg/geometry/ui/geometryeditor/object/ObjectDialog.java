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
package de.topobyte.livecg.geometry.ui.geometryeditor.object;

import java.awt.Window;
import java.util.List;

import javax.swing.JDialog;

import de.topobyte.livecg.geometry.ui.geom.Editable;
import de.topobyte.livecg.geometry.ui.geom.Node;
import de.topobyte.livecg.geometry.ui.geom.Polygon;
import de.topobyte.livecg.geometry.ui.geometryeditor.ContentChangedListener;
import de.topobyte.livecg.geometry.ui.geometryeditor.ContentReferenceChangedListener;
import de.topobyte.livecg.geometry.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.geometry.ui.geometryeditor.SelectionChangedListener;

public class ObjectDialog extends JDialog
{

	private static final long serialVersionUID = -9016694962587077670L;

	private GeometryEditPane editPane;

	public ObjectDialog(Window window, GeometryEditPane editPane)
	{
		super(window, "Object");
		this.editPane = editPane;

		setContentPane(new NothingPanel());

		editPane.addContentReferenceChangedListener(new ContentReferenceChangedListener() {

			@Override
			public void contentReferenceChanged()
			{
				initForContent();
			}
		});

		editPane.addSelectionChangedListener(new SelectionChangedListener() {

			@Override
			public void selectionChanged()
			{
				update();
			}
		});

		initForContent();
	}

	protected void initForContent()
	{
		editPane.getContent().addContentChangedListener(
				new ContentChangedListener() {

					@Override
					public void contentChanged()
					{
						update();
					}
				});
	}

	private Node currentNode = null;
	private Editable currentChain = null;
	private Polygon currentPolygon = null;

	private NodePanel np = null;
	private PolygonalChainPanel pcp = null;
	private PolygonPanel pp = null;

	protected void update()
	{
		List<Node> nodes = editPane.getCurrentNodes();
		List<Editable> chains = editPane.getCurrentChains();
		List<Polygon> polygons = editPane.getCurrentPolygons();

		int ns = nodes.size();
		int cs = chains.size();
		int ps = polygons.size();

		if (ns == 0 && cs == 0 && ps == 0) {
			if (currentNode != null || currentChain != null
					|| currentPolygon != null) {
				setContentPane(new NothingPanel());
			}
			currentNode = null;
			currentChain = null;
			currentPolygon = null;
		} else if (ns == 1 && cs == 0 && ps == 0) {
			Node node = nodes.iterator().next();
			if (currentNode != node) {
				currentNode = node;
				np = new NodePanel(editPane, node);
				setContentPane(np);
			} else {
				np.update();
			}
		} else if (ns == 0 && cs == 1 && ps == 0) {
			Editable chain = chains.iterator().next();
			if (currentChain != chain) {
				currentChain = chain;
				pcp = new PolygonalChainPanel(editPane, chain);
				setContentPane(pcp);
			} else {
				pcp.update();
			}
		} else if (ns == 0 && cs == 0 && ps == 1) {
			Polygon polygon = polygons.iterator().next();
			if (currentPolygon != polygon) {
				currentPolygon = polygon;
				pp = new PolygonPanel(editPane, polygon);
				setContentPane(pp);
			} else {
				pp.update();
			}
		} else {
			if (currentNode != null || currentChain != null || currentPolygon != null) {
				setContentPane(new MultiplePanel());
			}
			currentNode = null;
			currentChain = null;
			currentPolygon = null;
		}
		validate();
	}
}
