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
package de.topobyte.livecg.geometryeditor.geometryeditor.object;

import java.awt.Window;
import java.util.List;

import javax.swing.JDialog;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.geometryeditor.geometryeditor.ContentChangedListener;
import de.topobyte.livecg.geometryeditor.geometryeditor.ContentReferenceChangedListener;
import de.topobyte.livecg.geometryeditor.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.geometryeditor.geometryeditor.SelectionChangedListener;
import de.topobyte.livecg.geometryeditor.geometryeditor.object.multiple.MultiplePanel;
import de.topobyte.livecg.geometryeditor.geometryeditor.object.single.NodePanel;
import de.topobyte.livecg.geometryeditor.geometryeditor.object.single.PolygonPanel;
import de.topobyte.livecg.geometryeditor.geometryeditor.object.single.PolygonalChainPanel;

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

					@Override
					public void dimensionChanged()
					{
						// ignore
					}
				});
	}

	private Node currentNode = null;
	private Chain currentChain = null;
	private Polygon currentPolygon = null;

	private NodePanel np = null;
	private PolygonalChainPanel pcp = null;
	private PolygonPanel pp = null;
	private MultiplePanel mp = null;

	private Mode mode = Mode.NOTHING;

	protected void update()
	{
		List<Node> nodes = editPane.getCurrentNodes();
		List<Chain> chains = editPane.getCurrentChains();
		List<Polygon> polygons = editPane.getCurrentPolygons();

		int ns = nodes.size();
		int cs = chains.size();
		int ps = polygons.size();

		if (ns == 0 && cs == 0 && ps == 0) {
			if (mode != Mode.NOTHING) {
				setContentPane(new NothingPanel());
				mode = Mode.NOTHING;
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
			mode = Mode.NODE;
		} else if (ns == 0 && cs == 1 && ps == 0) {
			Chain chain = chains.iterator().next();
			if (currentChain != chain) {
				currentChain = chain;
				pcp = new PolygonalChainPanel(editPane, chain);
				setContentPane(pcp);
			} else {
				pcp.update();
			}
			mode = Mode.CHAIN;
		} else if (ns == 0 && cs == 0 && ps == 1) {
			Polygon polygon = polygons.iterator().next();
			if (currentPolygon != polygon) {
				currentPolygon = polygon;
				pp = new PolygonPanel(editPane, polygon);
				setContentPane(pp);
			} else {
				pp.update();
			}
			mode = Mode.POLYGON;
		} else {
			boolean create = mp == null;
			if (create) {
				mp = new MultiplePanel(editPane);
			} else {
				mp.update();
			}
			if (currentNode != null || currentChain != null
					|| currentPolygon != null) {
				currentNode = null;
				currentChain = null;
				currentPolygon = null;
				setContentPane(mp);
			}
			mode = Mode.MULTIPLE;
		}
		validate();
	}
}
