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

package de.topobyte.livecg.geometryeditor.geometryeditor;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.config.LiveConfig;
import de.topobyte.livecg.core.geometry.geom.AwtHelper;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.GeometryTransformer;
import de.topobyte.livecg.core.geometry.geom.LineSegment;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.lina.AffineTransformUtil;
import de.topobyte.livecg.core.lina.Matrix;
import de.topobyte.livecg.core.painting.AwtPainter;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Painter;
import de.topobyte.livecg.core.scrolling.ViewportWithSignals;
import de.topobyte.livecg.core.scrolling.ViewportListener;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.OpenCloseRingAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.mouse.EditorMouseListener;
import de.topobyte.livecg.geometryeditor.geometryeditor.mousemode.MouseMode;
import de.topobyte.livecg.geometryeditor.geometryeditor.mousemode.MouseModeListener;
import de.topobyte.livecg.geometryeditor.geometryeditor.mousemode.MouseModeProvider;
import de.topobyte.livecg.util.SwingUtil;

public class GeometryEditPane extends JPanel implements MouseModeProvider,
		ContentChangedListener, ViewportWithSignals
{

	private static final long serialVersionUID = -8078013859398953550L;

	final static Logger logger = LoggerFactory
			.getLogger(GeometryEditPane.class);

	public static final int MARGIN = 200;

	private String q(String property)
	{
		return "geometryeditor.colors." + property;
	}

	private Color COLOR_BG1 = LiveConfig.getColor(q("background.scene"));
	private Color COLOR_BG2 = LiveConfig.getColor(q("background.nothing"));

	private MouseMode mouseMode = MouseMode.EDIT;

	private Content content;

	private List<Node> currentNodes = new ArrayList<Node>();
	private List<Chain> currentChains = new ArrayList<Chain>();
	private List<Polygon> currentPolygons = new ArrayList<Polygon>();

	private double positionX = 0;
	private double positionY = 0;
	private double zoom = 1;

	@Override
	public double getPositionX()
	{
		return positionX;
	}

	@Override
	public double getPositionY()
	{
		return positionY;
	}

	@Override
	public void setPositionX(double x)
	{
		positionX = x;
		fireViewportListenersViewportChanged();
	}

	@Override
	public void setPositionY(double y)
	{
		positionY = y;
		fireViewportListenersViewportChanged();
	}

	@Override
	public void setZoom(double zoom)
	{
		setZoomCentered(zoom);
	}

	public void setZoomCentered(double zoom)
	{
		double mx = -positionX + getWidth() / this.zoom / 2.0;
		double my = -positionY + getHeight() / this.zoom / 2.0;
		this.zoom = zoom;
		positionX = getWidth() / zoom / 2.0 - mx;
		positionY = getHeight() / zoom / 2.0 - my;
		checkBounds();
		fireViewportListenersZoomChanged();
	}

	@Override
	public double getZoom()
	{
		return zoom;
	}

	private List<ViewportListener> viewportListeners = new ArrayList<ViewportListener>();

	@Override
	public void addViewportListener(ViewportListener listener)
	{
		viewportListeners.add(listener);
	}

	@Override
	public void removeViewportListener(ViewportListener listener)
	{
		viewportListeners.remove(listener);
	}

	private void fireViewportListenersViewportChanged()
	{
		for (ViewportListener listener : viewportListeners) {
			listener.viewportChanged();
		}
	}

	private void fireViewportListenersZoomChanged()
	{
		for (ViewportListener listener : viewportListeners) {
			listener.zoomChanged();
		}
	}

	private void fireViewportListenersComplexChange()
	{
		for (ViewportListener listener : viewportListeners) {
			listener.complexChange();
		}
	}

	public GeometryEditPane()
	{
		content = new Content();

		setBackground(new java.awt.Color(COLOR_BG2.getRGB()));

		EditorMouseListener mouseListener = new EditorMouseListener(this);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);

		setupKeys();

		initForContent();

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e)
			{
				checkBounds();
			}

		});
	}

	private void checkBounds()
	{
		boolean update = false;
		if (-positionX + getWidth() / zoom > content.getScene().getWidth()
				+ MARGIN) {
			logger.debug("Moved out of viewport at right");
			positionX = getWidth() / zoom - content.getScene().getWidth()
					- MARGIN;
			update = true;
		}
		if (positionX > MARGIN) {
			logger.debug("Scrolled too much to the left");
			positionX = MARGIN;
			update = true;
		}
		if (-positionY + getHeight() / zoom > content.getScene().getHeight()
				+ MARGIN) {
			logger.debug("Moved out of viewport at bottom");
			positionY = getHeight() / zoom - content.getScene().getHeight()
					- MARGIN;
			update = true;
		}
		if (positionY > MARGIN) {
			logger.debug("Scrolled too much to the top");
			positionY = MARGIN;
			update = true;
		}
		if (update) {
			repaint();
		}
		fireViewportListenersViewportChanged();
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

		inputMap.put(
				KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK
						| InputEvent.SHIFT_MASK), "c-o");

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
		if (this.mouseMode == mouseMode) {
			return;
		}
		Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		setCursor(cursor);

		MouseMode old = this.mouseMode;
		this.mouseMode = mouseMode;
		for (MouseModeListener listener : listeners) {
			listener.mouseModeChanged(mouseMode);
		}
		if (mouseMode != MouseMode.SELECT_MOVE) {
			setMouseHighlight((Node) null);
			setMouseHighlight((Chain) null);
		}
		if (old == MouseMode.EDIT) {
			setProspectNode(null);
			setProspectSegment(null);
		}
		repaint();
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

	private Color colorChainLines = LiveConfig.getColor(q("chains.lines"));
	private Color colorChainPoints = LiveConfig.getColor(q("chains.points"));
	private Color colorEditingChainLines = LiveConfig
			.getColor(q("editing.chains.lines"));
	private Color colorEditingChainPoints = LiveConfig
			.getColor(q("editing.chains.points"));
	private Color colorFirstEditingLinePoints = LiveConfig
			.getColor(q("editing.chains.first"));
	private Color colorLastEditingLinePoints = LiveConfig
			.getColor(q("editing.chains.last"));

	private Color colorMouseHighightNode = LiveConfig
			.getColor(q("mousehighlight.node"));
	private Color colorMouseHighlightChain = LiveConfig
			.getColor(q("mousehighlight.chain"));
	private Color colorSnapHighlightNode = LiveConfig
			.getColor(q("nodes.snaphighlight"));
	private Color colorSelectedNodes = LiveConfig.getColor(q("nodes.selected"));
	private Color colorProspectNode = LiveConfig.getColor(q("prospect.node"));
	private Color colorProspectSegment = LiveConfig
			.getColor(q("prospect.segment"));
	private Color colorSelectionRectangle = LiveConfig
			.getColor(q("selection.rectangle"));
	private Color colorPolygonInterior = LiveConfig
			.getColor(q("polygon.interior"));

	private Color colorRotationRectangle = LiveConfig
			.getColor(q("rotation.rectangle"));

	@Override
	public void paint(Graphics graphics)
	{
		super.paint(graphics);
		Graphics2D g = (Graphics2D) graphics;
		SwingUtil.useAntialiasing(g, true);

		AwtPainter painter = new AwtPainter(g);
		paint(painter);
	}

	public void paint(Painter p)
	{
		Matrix translate = AffineTransformUtil.translate(positionX, positionY);
		Matrix scale = AffineTransformUtil.scale(zoom, zoom);
		Matrix matrix = scale.multiplyFromRight(translate);
		GeometryTransformer transformer = new GeometryTransformer(matrix);

		p.setColor(COLOR_BG2);
		p.fillRect(0, 0, getWidth(), getHeight());

		p.setColor(COLOR_BG1);
		Rectangle scene = content.getScene();
		Rectangle tscene = transformer.transform(scene);
		p.fillRect(tscene.getX1(), tscene.getY1(), tscene.getWidth(),
				tscene.getHeight());

		List<Polygon> polygons = content.getPolygons();
		for (int i = 0; i < polygons.size(); i++) {
			Polygon polygon = polygons.get(i);
			Polygon tpolygon = transformer.transform(polygon);
			drawInterior(p, tpolygon);
		}

		if (mouseHighlightChain != null) {
			Chain tchain = transformer.transform(mouseHighlightChain);
			drawHighlight(p, tchain, colorMouseHighlightChain);
		}

		if (mouseHighlightPolygon != null) {
			Chain tchain = transformer.transform(mouseHighlightPolygon
					.getShell());
			drawHighlight(p, tchain, colorMouseHighlightChain);
		}

		List<Chain> chains = content.getChains();
		for (int i = 0; i < chains.size(); i++) {
			Chain chain = chains.get(i);
			if (currentChains.contains(chain)) {
				continue;
			}
			Chain tchain = transformer.transform(chain);
			draw(p, tchain, colorChainLines, colorChainPoints, getName(i));
		}

		for (int i = 0; i < polygons.size(); i++) {
			Polygon polygon = polygons.get(i);
			if (currentPolygons.contains(polygon)) {
				continue;
			}
			Polygon tpolygon = transformer.transform(polygon);
			drawExterior(p, tpolygon, colorChainLines, colorChainPoints);
		}

		if (currentChains.size() > 0) {
			for (Chain chain : currentChains) {
				Chain tchain = transformer.transform(chain);
				draw(p, tchain, colorEditingChainLines,
						colorEditingChainPoints, null);

				p.setColor(colorLastEditingLinePoints);
				Coordinate c = chain.getLastCoordinate();
				Coordinate tc = transformer.transform(c);
				p.drawRect((int) Math.round(tc.getX() - 3),
						(int) Math.round(tc.getY() - 3), 6, 6);

				if (chain.getNumberOfNodes() > 1) {
					p.setColor(colorFirstEditingLinePoints);
					c = chain.getFirstCoordinate();
					tc = transformer.transform(c);
					p.drawRect((int) Math.round(tc.getX() - 3),
							(int) Math.round(tc.getY() - 3), 6, 6);
				}
			}
		}

		if (currentPolygons.size() > 0) {
			for (Polygon polygon : currentPolygons) {
				Polygon tpolygon = transformer.transform(polygon);
				drawExterior(p, tpolygon, colorEditingChainLines,
						colorEditingChainPoints);
			}
		}

		if (snapHighlightNode != null) {
			p.setColor(colorSnapHighlightNode);
			Coordinate c = snapHighlightNode.getCoordinate();
			Coordinate tc = transformer.transform(c);
			p.fillRect((int) Math.round(tc.getX() - 5),
					(int) Math.round(tc.getY() - 5), 10, 10);
		}

		if (mouseHighlightNode != null) {
			p.setColor(colorMouseHighightNode);
			Coordinate c = mouseHighlightNode.getCoordinate();
			Coordinate tc = transformer.transform(c);
			p.drawRect((int) Math.round(tc.getX() - 3),
					(int) Math.round(tc.getY() - 3), 6, 6);
		}

		if (currentNodes.size() > 0) {
			for (Node node : currentNodes) {
				p.setColor(colorSelectedNodes);
				Coordinate c = node.getCoordinate();
				Coordinate tc = transformer.transform(c);
				p.fillRect((int) Math.round(tc.getX() - 2),
						(int) Math.round(tc.getY() - 2), 4 + 1, 4 + 1);
			}
		}

		if (prospectLine != null) {
			p.setColor(colorProspectSegment);
			Coordinate c1 = prospectLine.getC1();
			Coordinate c2 = prospectLine.getC2();
			Coordinate tc1 = transformer.transform(c1);
			Coordinate tc2 = transformer.transform(c2);
			p.drawLine((int) Math.round(tc1.getX()),
					(int) Math.round(tc1.getY()), (int) Math.round(tc2.getX()),
					(int) Math.round(tc2.getY()));
		}

		if (prospectNode != null) {
			p.setColor(colorProspectNode);
			Coordinate c = prospectNode.getCoordinate();
			Coordinate tc = transformer.transform(c);
			int x = (int) Math.round(tc.getX());
			int y = (int) Math.round(tc.getY());
			p.drawRect(x - 2, y - 2, 4, 4);
		}

		if (selectionRectangle != null) {
			Rectangle tselectionRectangle = transformer
					.transform(selectionRectangle);
			double x = Math.min(tselectionRectangle.getX1(),
					tselectionRectangle.getX2());
			double y = Math.min(tselectionRectangle.getY1(),
					tselectionRectangle.getY2());
			double width = Math.abs(tselectionRectangle.getX2()
					- tselectionRectangle.getX1());
			double height = Math.abs(tselectionRectangle.getY2()
					- tselectionRectangle.getY1());
			p.setColor(colorSelectionRectangle);
			p.drawRect((int) Math.round(x), (int) Math.round(y),
					(int) Math.round(width), (int) Math.round(height));
		}

		if (mouseMode == MouseMode.SCALE) {
			Rectangle objects = getSelectedObjectsRectangle();
			Rectangle tobjects = transformer.transform(objects);
			double width = tobjects.getX2() - tobjects.getX1();
			double height = tobjects.getY2() - tobjects.getY1();
			p.setColor(colorRotationRectangle);
			p.drawRect((int) Math.round(tobjects.getX1()),
					(int) Math.round(tobjects.getY1()),
					(int) Math.round(width), (int) Math.round(height));

			double s = 6;
			// Corners
			p.drawRect(tobjects.getX1() - s / 2, tobjects.getY1() - s / 2, s, s);
			p.drawRect(tobjects.getX1() - s / 2, tobjects.getY2() - s / 2, s, s);
			p.drawRect(tobjects.getX2() - s / 2, tobjects.getY1() - s / 2, s, s);
			p.drawRect(tobjects.getX2() - s / 2, tobjects.getY2() - s / 2, s, s);
			// Sides
			p.drawRect(tobjects.getX1() - s / 2 + width / 2, tobjects.getY1()
					- s / 2, s, s);
			p.drawRect(tobjects.getX1() - s / 2 + width / 2, tobjects.getY2()
					- s / 2, s, s);
			p.drawRect(tobjects.getX1() - s / 2, tobjects.getY1() - s / 2
					+ height / 2, s, s);
			p.drawRect(tobjects.getX2() - s / 2, tobjects.getY1() - s / 2
					+ height / 2, s, s);
		}
	}

	private String getName(int i)
	{
		return new Character((char) ('A' + i)).toString();
	}

	private void draw(Painter p, Chain chain, Color colorLines,
			Color colorPoints, String name)
	{
		int n = chain.getNumberOfNodes();
		if (n == 0) {
			return;
		}

		// line segments
		p.setColor(colorLines);
		p.setStrokeWidth(1.0);
		Coordinate last = chain.getCoordinate(0);
		for (int i = 1; i < n; i++) {
			Coordinate current = chain.getCoordinate(i);
			int x1 = (int) Math.round(last.getX());
			int y1 = (int) Math.round(last.getY());
			int x2 = (int) Math.round(current.getX());
			int y2 = (int) Math.round(current.getY());
			p.drawLine(x1, y1, x2, y2);
			last = current;
		}
		if (chain.isClosed()) {
			Coordinate first = chain.getCoordinate(0);
			int x1 = (int) Math.round(last.getX());
			int y1 = (int) Math.round(last.getY());
			int x2 = (int) Math.round(first.getX());
			int y2 = (int) Math.round(first.getY());
			p.drawLine(x1, y1, x2, y2);
		}
		// points
		p.setColor(colorPoints);
		p.setStrokeWidth(1.0);
		for (int i = 0; i < n; i++) {
			Coordinate current = chain.getCoordinate(i);
			int x = (int) Math.round(current.getX());
			int y = (int) Math.round(current.getY());
			p.drawRect(x - 2, y - 2, 4, 4);
		}
		// label
		if (name != null) {
			Coordinate first = chain.getFirstCoordinate();
			p.drawString(name, (int) Math.round(first.getX()) - 2,
					(int) Math.round(first.getY()) - 4);
		}
	}

	private void drawHighlight(Painter p, Chain chain, Color color)
	{
		int n = chain.getNumberOfNodes();
		if (n == 0) {
			return;
		}

		// line segments
		p.setColor(color);
		p.setStrokeWidth(3.0);
		Coordinate last = chain.getCoordinate(0);
		for (int i = 1; i < n; i++) {
			Coordinate current = chain.getCoordinate(i);
			int x1 = (int) Math.round(last.getX());
			int y1 = (int) Math.round(last.getY());
			int x2 = (int) Math.round(current.getX());
			int y2 = (int) Math.round(current.getY());
			p.drawLine(x1, y1, x2, y2);
			last = current;
		}
		if (chain.isClosed()) {
			Coordinate first = chain.getCoordinate(0);
			int x1 = (int) Math.round(last.getX());
			int y1 = (int) Math.round(last.getY());
			int x2 = (int) Math.round(first.getX());
			int y2 = (int) Math.round(first.getY());
			p.drawLine(x1, y1, x2, y2);
		}
	}

	private void drawInterior(Painter p, Polygon polygon)
	{
		Area area = AwtHelper.toShape(polygon);
		p.setColor(colorPolygonInterior);
		p.fill(area);
	}

	private void drawExterior(Painter p, Polygon polygon, Color colorLines,
			Color colorPoints)
	{
		if (currentChains.contains(polygon.getShell())) {
			return;
		}
		draw(p, polygon.getShell(), colorLines, colorPoints, null);
		for (Chain hole : polygon.getHoles()) {
			draw(p, hole, colorLines, colorPoints, null);
		}
	}

	@Override
	public void contentChanged()
	{
		repaint();
	}

	@Override
	public void dimensionChanged()
	{
		checkBounds();
		repaint();
		fireViewportListenersComplexChange();
	}

	private Node prospectNode = null;

	public boolean setProspectNode(Node prospectNode)
	{
		boolean changed = this.prospectNode != prospectNode;
		this.prospectNode = prospectNode;
		return changed;
	}

	private LineSegment prospectLine = null;

	public boolean setProspectSegment(LineSegment prospectLine)
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

	public Node getMouseHighlightNode()
	{
		return mouseHighlightNode;
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
		// Remove from current-chains
		if (currentChains.contains(chain)) {
			removeCurrentChain(chain);
		}
		// Remove nodes from current-nodes that were only present in this object
		List<Node> gone = new ArrayList<Node>();
		for (Node node : currentNodes) {
			if (node.getChains().size() == 1
					&& node.getChains().get(0) == chain) {
				gone.add(node);
			}
		}
		for (Node node : gone) {
			removeCurrentNode(node);
		}
		// Eventually remove from content
		content.removeChain(chain);
	}

	public void removePolygon(Polygon polygon)
	{
		// Remove from current-polygons
		if (currentPolygons.contains(polygon)) {
			removeCurrentPolygon(polygon);
		}
		// Remove nodes from current-nodes that were only present in this object
		// -> This is a little more tricky than for chains, first find all
		// chains used by the polygon
		List<Chain> chains = new ArrayList<Chain>();
		chains.add(polygon.getShell());
		for (Chain hole : polygon.getHoles()) {
			chains.add(hole);
		}
		// -> Then look at nodes
		List<Node> gone = new ArrayList<Node>();
		for (Node node : currentNodes) {
			boolean keep = false;
			for (Chain c : node.getChains()) {
				if (!chains.contains(c)) {
					keep = true;
					break;
				}
			}
			if (!keep) {
				gone.add(node);
			}
		}
		for (Node node : gone) {
			removeCurrentNode(node);
		}
		// Eventually remove from content
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

	public boolean somethingSelected()
	{
		return currentChains.size() != 0 || currentPolygons.size() != 0
				|| currentNodes.size() != 0;
	}

	public boolean onlyOneNodeSelected()
	{
		return currentChains.size() == 0 && currentPolygons.size() == 0
				&& currentNodes.size() == 1;
	}

	public Set<Node> getSelectedNodes()
	{
		Set<Node> nodes = new HashSet<Node>();
		for (Node node : currentNodes) {
			nodes.add(node);
		}
		for (Chain chain : currentChains) {
			for (int i = 0; i < chain.getNumberOfNodes(); i++) {
				nodes.add(chain.getNode(i));
			}
		}
		for (Polygon polygon : currentPolygons) {
			Chain shell = polygon.getShell();
			for (int i = 0; i < shell.getNumberOfNodes(); i++) {
				nodes.add(shell.getNode(i));
			}
			for (Chain hole : polygon.getHoles()) {
				for (int i = 0; i < hole.getNumberOfNodes(); i++) {
					nodes.add(hole.getNode(i));
				}
			}
		}
		return nodes;
	}

	public Rectangle getSelectedObjectsRectangle()
	{
		double xmin = Double.POSITIVE_INFINITY;
		double xmax = Double.NEGATIVE_INFINITY;
		double ymin = Double.POSITIVE_INFINITY;
		double ymax = Double.NEGATIVE_INFINITY;
		Set<Node> nodes = getSelectedNodes();
		for (Node node : nodes) {
			Coordinate c = node.getCoordinate();
			if (c.getX() < xmin) {
				xmin = c.getX();
			}
			if (c.getX() > xmax) {
				xmax = c.getX();
			}
			if (c.getY() < ymin) {
				ymin = c.getY();
			}
			if (c.getY() > ymax) {
				ymax = c.getY();
			}
		}
		return new Rectangle(xmin, ymin, xmax, ymax);
	}

}
