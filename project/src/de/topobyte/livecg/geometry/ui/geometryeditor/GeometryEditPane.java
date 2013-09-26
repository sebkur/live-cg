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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import de.topobyte.livecg.geometry.geom.AwtHelper;
import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.Coordinate;
import de.topobyte.livecg.geometry.geom.Line;
import de.topobyte.livecg.geometry.geom.Node;
import de.topobyte.livecg.geometry.geom.Polygon;
import de.topobyte.livecg.geometry.geom.Rectangle;
import de.topobyte.livecg.geometry.ui.geometryeditor.action.OpenCloseRingAction;
import de.topobyte.livecg.geometry.ui.geometryeditor.mousemode.MouseMode;
import de.topobyte.livecg.geometry.ui.geometryeditor.mousemode.MouseModeListener;
import de.topobyte.livecg.geometry.ui.geometryeditor.mousemode.MouseModeProvider;

public class GeometryEditPane extends JPanel implements MouseModeProvider,
		ContentChangedListener
{

	private static final long serialVersionUID = -8078013859398953550L;

	private MouseMode mouseMode = MouseMode.EDIT;

	private Content content;

	private List<Node> currentNodes = new ArrayList<Node>();
	private List<Chain> currentChains = new ArrayList<Chain>();
	private List<Polygon> currentPolygons = new ArrayList<Polygon>();

	public GeometryEditPane()
	{
		content = new Content();

		setBackground(new Color(0xFAFAFA));

		EditorMouseListener mouseListener = new EditorMouseListener(this);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);

		setupKeys();

		initForContent();
	}

	private void initForContent()
	{
		content.addContentChangedListener(this);
		setTransferHandler(new EditPaneTransferHandler(content));
	}

	private void setupKeys()
	{
		InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = getActionMap();

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_DOWN_MASK), "c-o");

		OpenCloseRingAction openCloseRingAction = new OpenCloseRingAction(this);

		actionMap.put("c-o", openCloseRingAction);
	}

	@Override
	public MouseMode getMouseMode()
	{
		return mouseMode;
	}

	@Override
	public void setMouseMode(MouseMode mouseMode)
	{
		MouseMode old = this.mouseMode;
		this.mouseMode = mouseMode;
		for (MouseModeListener listener : listeners) {
			listener.mouseModeChanged(mouseMode);
		}
		if (mouseMode != MouseMode.SELECT_MOVE) {
			setMouseHighlight((Node) null);
			setMouseHighlight((Chain) null);
			repaint();
		}
		if (old == MouseMode.EDIT) {
			setProspectNode(null);
			setProspectLine(null);
			repaint();
		}
	}

	private List<MouseModeListener> listeners = new ArrayList<MouseModeListener>();

	@Override
	public void addMouseModeListener(MouseModeListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public void removeMouseModeListener(MouseModeListener listener)
	{
		listeners.remove(listener);
	}

	/*
	 * content
	 */

	public Content getContent()
	{
		return content;
	}

	public void setContent(Content content)
	{
		this.content = content;
		initForContent();

		fireContentReferenceChanged();

		clearCurrentNodes();
		clearCurrentChains();
		clearCurrentPolygons();
		setSnapHighlight(null);
	}

	public List<Node> getCurrentNodes()
	{
		return currentNodes;
	}

	public List<Chain> getCurrentChains()
	{
		return currentChains;
	}

	public List<Polygon> getCurrentPolygons()
	{
		return currentPolygons;
	}

	public boolean addCurrentNode(Node node)
	{
		currentNodes.add(node);
		fireSelectionChanged();
		return true;
	}

	public boolean addCurrentChain(Chain chain)
	{
		currentChains.add(chain);
		fireSelectionChanged();
		return true;
	}

	public boolean addCurrentPolygon(Polygon polygon)
	{
		currentPolygons.add(polygon);
		fireSelectionChanged();
		return true;
	}

	public boolean removeCurrentNode(Node node)
	{
		boolean changed = currentNodes.remove(node);
		if (changed) {
			fireSelectionChanged();
		}
		return changed;
	}

	public boolean removeCurrentChain(Chain chain)
	{
		boolean changed = currentChains.remove(chain);
		if (changed) {
			fireSelectionChanged();
		}
		return changed;
	}

	public boolean removeCurrentPolygon(Polygon polygon)
	{
		boolean changed = currentPolygons.remove(polygon);
		if (changed) {
			fireSelectionChanged();
		}
		return changed;
	}

	public boolean clearCurrentNodes()
	{
		boolean changed = currentNodes.size() != 0;
		if (changed) {
			currentNodes.clear();
			fireSelectionChanged();
		}
		return changed;
	}

	public boolean clearCurrentChains()
	{
		boolean changed = currentChains.size() != 0;
		if (changed) {
			currentChains.clear();
			fireSelectionChanged();
		}
		return changed;
	}

	public boolean clearCurrentPolygons()
	{
		boolean changed = currentPolygons.size() != 0;
		if (changed) {
			currentPolygons.clear();
			fireSelectionChanged();
		}
		return changed;
	}

	/*
	 * drawing
	 */

	private Color colorChainLines = Color.BLACK;
	private Color colorChainPoints = Color.BLACK;
	private Color colorEditingChainLines = Color.BLUE;
	private Color colorEditingChainPoints = Color.BLUE;
	private Color colorFirstEditingLinePoints = Color.RED;
	private Color colorLastEditingLinePoints = Color.RED;

	private Color colorMouseHighightNode = Color.CYAN;
	private Color colorMouseHighlightChain = Color.CYAN;
	private Color colorSnapHighlightNode = Color.CYAN.darker();
	private Color colorSelectedNodes = Color.CYAN;

	public void paint(Graphics graphics)
	{
		super.paint(graphics);
		Graphics2D g = (Graphics2D) graphics;

		useAntialiasing(g, true);
		List<Polygon> polygons = content.getPolygons();
		for (int i = 0; i < polygons.size(); i++) {
			Polygon polygon = polygons.get(i);
			drawInterior(g, polygon);
		}
		useAntialiasing(g, false);

		if (mouseHighlightChain != null) {
			drawHighlight(g, mouseHighlightChain, colorMouseHighlightChain);
		}

		if (mouseHighlightPolygon != null) {
			drawHighlight(g, mouseHighlightPolygon.getShell(),
					colorMouseHighlightChain);
		}

		List<Chain> chains = content.getChains();
		for (int i = 0; i < chains.size(); i++) {
			Chain chain = chains.get(i);
			if (currentChains.contains(chain)) {
				continue;
			}
			draw(g, chain, colorChainLines, colorChainPoints, getName(i));
		}

		useAntialiasing(g, true);
		for (int i = 0; i < polygons.size(); i++) {
			Polygon polygon = polygons.get(i);
			drawExterior(g, polygon);
		}
		useAntialiasing(g, false);

		if (currentChains.size() > 0) {
			for (Chain chain : currentChains) {
				draw(g, chain, colorEditingChainLines, colorEditingChainPoints,
						null);

				g.setColor(colorLastEditingLinePoints);
				Coordinate c = chain.getLastCoordinate();
				g.drawRect((int) Math.round(c.getX() - 3),
						(int) Math.round(c.getY() - 3), 6, 6);

				if (chain.getNumberOfNodes() > 1) {
					g.setColor(colorFirstEditingLinePoints);
					c = chain.getFirstCoordinate();
					g.drawRect((int) Math.round(c.getX() - 3),
							(int) Math.round(c.getY() - 3), 6, 6);
				}
			}
		}

		if (currentPolygons.size() > 0) {
			for (Polygon polygon : currentPolygons) {
				draw(g, polygon.getShell(), colorEditingChainLines,
						colorEditingChainPoints, null);
			}
		}

		if (snapHighlightNode != null) {
			g.setColor(colorSnapHighlightNode);
			Coordinate c = snapHighlightNode.getCoordinate();
			g.fillRect((int) Math.round(c.getX() - 5),
					(int) Math.round(c.getY() - 5), 10, 10);
		}

		if (mouseHighlightNode != null) {
			g.setColor(colorMouseHighightNode);
			Coordinate c = mouseHighlightNode.getCoordinate();
			g.drawRect((int) Math.round(c.getX() - 3),
					(int) Math.round(c.getY() - 3), 6, 6);
		}

		if (currentNodes.size() > 0) {
			for (Node node : currentNodes) {
				g.setColor(colorSelectedNodes);
				Coordinate c = node.getCoordinate();
				g.fillRect((int) Math.round(c.getX() - 2),
						(int) Math.round(c.getY() - 2), 4 + 1, 4 + 1);
			}
		}

		if (prospectLine != null) {
			useAntialiasing(g, true);
			g.setColor(Color.BLACK);
			Coordinate c1 = prospectLine.getC1();
			Coordinate c2 = prospectLine.getC2();
			g.drawLine((int) Math.round(c1.getX()),
					(int) Math.round(c1.getY()), (int) Math.round(c2.getX()),
					(int) Math.round(c2.getY()));
			useAntialiasing(g, false);
		}

		if (prospectNode != null) {
			g.setColor(Color.BLACK);
			Coordinate c = prospectNode.getCoordinate();
			int x = (int) Math.round(c.getX());
			int y = (int) Math.round(c.getY());
			g.drawRect(x - 2, y - 2, 4, 4);
		}

		if (selectionRectangle != null) {
			int x = Math.min(selectionRectangle.getX1(),
					selectionRectangle.getX2());
			int y = Math.min(selectionRectangle.getY1(),
					selectionRectangle.getY2());
			int width = Math.abs(selectionRectangle.getX2()
					- selectionRectangle.getX1());
			int height = Math.abs(selectionRectangle.getY2()
					- selectionRectangle.getY1());
			g.setColor(Color.BLACK);
			g.drawRect(x, y, width, height);
		}
	}

	private String getName(int i)
	{
		return new Character((char) ('A' + i)).toString();
	}

	private void draw(Graphics2D g, Chain chain, Color colorLine,
			Color colorPoints, String name)
	{
		int n = chain.getNumberOfNodes();
		if (n == 0) {
			return;
		}

		// line segments
		useAntialiasing(g, true);
		g.setColor(colorLine);
		g.setStroke(new BasicStroke(1.0f));
		Coordinate last = chain.getCoordinate(0);
		for (int i = 1; i < n; i++) {
			Coordinate current = chain.getCoordinate(i);
			int x1 = (int) Math.round(last.getX());
			int y1 = (int) Math.round(last.getY());
			int x2 = (int) Math.round(current.getX());
			int y2 = (int) Math.round(current.getY());
			g.drawLine(x1, y1, x2, y2);
			last = current;
		}
		if (chain.isClosed()) {
			Coordinate first = chain.getCoordinate(0);
			int x1 = (int) Math.round(last.getX());
			int y1 = (int) Math.round(last.getY());
			int x2 = (int) Math.round(first.getX());
			int y2 = (int) Math.round(first.getY());
			g.drawLine(x1, y1, x2, y2);
		}
		useAntialiasing(g, false);
		// points
		g.setColor(colorPoints);
		g.setStroke(new BasicStroke(1.0f));
		for (int i = 0; i < n; i++) {
			Coordinate current = chain.getCoordinate(i);
			int x = (int) Math.round(current.getX());
			int y = (int) Math.round(current.getY());
			g.drawRect(x - 2, y - 2, 4, 4);
		}
		// label
		if (name != null) {
			Coordinate first = chain.getFirstCoordinate();
			g.drawString(name, (int) Math.round(first.getX()) - 2,
					(int) Math.round(first.getY()) - 4);
		}
	}

	private void drawHighlight(Graphics2D g, Chain chain, Color color)
	{
		int n = chain.getNumberOfNodes();
		if (n == 0) {
			return;
		}

		// line segments
		useAntialiasing(g, true);
		g.setColor(color);
		g.setStroke(new BasicStroke(3.0f));
		Coordinate last = chain.getCoordinate(0);
		for (int i = 1; i < n; i++) {
			Coordinate current = chain.getCoordinate(i);
			int x1 = (int) Math.round(last.getX());
			int y1 = (int) Math.round(last.getY());
			int x2 = (int) Math.round(current.getX());
			int y2 = (int) Math.round(current.getY());
			g.drawLine(x1, y1, x2, y2);
			last = current;
		}
		if (chain.isClosed()) {
			Coordinate first = chain.getCoordinate(0);
			int x1 = (int) Math.round(last.getX());
			int y1 = (int) Math.round(last.getY());
			int x2 = (int) Math.round(first.getX());
			int y2 = (int) Math.round(first.getY());
			g.drawLine(x1, y1, x2, y2);
		}
	}

	private void drawInterior(Graphics2D g, Polygon polygon)
	{
		Area area = AwtHelper.toShape(polygon);
		g.setColor(new Color(0x33ff0000, true));
		g.fill(area);
	}

	private void drawExterior(Graphics2D g, Polygon polygon)
	{
		if (currentChains.contains(polygon.getShell())) {
			return;
		}
		draw(g, polygon.getShell(), colorChainLines, colorChainPoints, null);
		for (Chain hole : polygon.getHoles()) {
			draw(g, hole, colorChainLines, colorChainPoints, null);
		}
	}

	private void useAntialiasing(Graphics2D g, boolean b)
	{
		if (b) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
		}
	}

	@Override
	public void contentChanged()
	{
		repaint();
	}

	private Node prospectNode = null;

	public boolean setProspectNode(Node prospectNode)
	{
		boolean changed = this.prospectNode != prospectNode;
		this.prospectNode = prospectNode;
		return changed;
	}

	private Line prospectLine = null;

	public boolean setProspectLine(Line prospectLine)
	{
		boolean changed = this.prospectLine != prospectLine;
		this.prospectLine = prospectLine;
		return changed;
	}

	private Node mouseHighlightNode = null;
	private Chain mouseHighlightChain = null;
	private Polygon mouseHighlightPolygon = null;
	private Node snapHighlightNode = null;

	public boolean setMouseHighlight(Node node)
	{
		if (mouseHighlightNode != node) {
			mouseHighlightNode = node;
			mouseHighlightChain = null;
			mouseHighlightPolygon = null;
			return true;
		}
		return false;
	}

	public boolean setSnapHighlight(Node node)
	{
		if (snapHighlightNode != node) {
			snapHighlightNode = node;
			return true;
		}
		return false;
	}

	public boolean setMouseHighlight(Chain chain)
	{
		if (mouseHighlightChain != chain) {
			mouseHighlightNode = null;
			mouseHighlightChain = chain;
			mouseHighlightPolygon = null;
			return true;
		}
		return false;
	}

	public boolean setMouseHighlight(Polygon polygon)
	{
		if (mouseHighlightPolygon != polygon) {
			mouseHighlightNode = null;
			mouseHighlightChain = null;
			mouseHighlightPolygon = polygon;
			return true;
		}
		return false;
	}

	private List<SelectionChangedListener> selectionListenerns = new ArrayList<SelectionChangedListener>();

	public void addSelectionChangedListener(SelectionChangedListener l)
	{
		selectionListenerns.add(l);
	}

	public void removeSelectionChangedListener(SelectionChangedListener l)
	{
		selectionListenerns.remove(l);
	}

	private void fireSelectionChanged()
	{
		for (SelectionChangedListener l : selectionListenerns) {
			l.selectionChanged();
		}
	}

	private List<ContentReferenceChangedListener> contentReferenceChangedListeners = new ArrayList<ContentReferenceChangedListener>();

	public void addContentReferenceChangedListener(
			ContentReferenceChangedListener l)
	{
		contentReferenceChangedListeners.add(l);
	}

	public void removeContentReferenceChangedListener(
			ContentReferenceChangedListener l)
	{
		contentReferenceChangedListeners.remove(l);
	}

	private void fireContentReferenceChanged()
	{
		for (ContentReferenceChangedListener l : contentReferenceChangedListeners) {
			l.contentReferenceChanged();
		}
	}

	public void removeChain(Chain chain)
	{
		if (currentChains.contains(chain)) {
			removeCurrentChain(chain);
		}
		content.removeChain(chain);
	}

	public void removePolygon(Polygon polygon)
	{
		if (currentPolygons.contains(polygon)) {
			removeCurrentPolygon(polygon);
		}
		content.removePolygon(polygon);
	}

	private Rectangle selectionRectangle = null;

	public Rectangle getSelectionRectangle()
	{
		return selectionRectangle;
	}

	public void setSelectionRectangle(Rectangle rectangle)
	{
		this.selectionRectangle = rectangle;
	}
}
