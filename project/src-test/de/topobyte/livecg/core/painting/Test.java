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
package de.topobyte.livecg.core.painting;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import de.topobyte.livecg.algorithms.convexhull.chan.ChansAlgorithm;
import de.topobyte.livecg.algorithms.convexhull.chan.ChansAlgorithmPainter;
import de.topobyte.livecg.algorithms.frechet.distanceterrain.DistanceTerrainPainterChains;
import de.topobyte.livecg.algorithms.frechet.freespace.FreeSpacePainterChains;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesConfig;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesAlgorithm;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesPainter;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesTriangulationAlgorithm;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesTriangulationPainter;
import de.topobyte.livecg.algorithms.polygon.shortestpath.PairOfNodes;
import de.topobyte.livecg.algorithms.polygon.shortestpath.ShortestPathAlgorithm;
import de.topobyte.livecg.algorithms.polygon.shortestpath.ShortestPathHelper;
import de.topobyte.livecg.algorithms.polygon.shortestpath.ShortestPathPainter;
import de.topobyte.livecg.algorithms.voronoi.fortune.FortunesSweep;
import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.core.FortunePainter;
import de.topobyte.livecg.core.export.SvgExporter;
import de.topobyte.livecg.core.export.TikzExporter;
import de.topobyte.livecg.core.geometry.dcel.DCEL;
import de.topobyte.livecg.core.geometry.dcel.DcelConverter;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.CopyUtil;
import de.topobyte.livecg.core.geometry.geom.CopyUtil.PolygonMode;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.io.ContentReader;
import de.topobyte.livecg.datastructures.content.ContentConfig;
import de.topobyte.livecg.datastructures.content.ContentPainter;
import de.topobyte.livecg.datastructures.dcel.DcelConfig;
import de.topobyte.livecg.datastructures.dcel.DcelPainter;
import de.topobyte.livecg.datastructures.dcel.InstanceDcelPainter;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.ui.geometryeditor.ContentHelper;
import de.topobyte.livecg.util.coloring.ColorMapBuilder;

public class Test
{
	private static String patternSvg = ("/tmp/test%d.svg");
	private static String patternTikz = ("/tmp/test%d.tikz");

	private static int counter = 1;

	private static File svg()
	{
		return new File(String.format(patternSvg, counter));
	}

	private static File tikz()
	{
		return new File(String.format(patternTikz, counter));
	}

	private static void next()
	{
		counter += 1;
	}

	public static void main(String[] args) throws IOException,
			ParserConfigurationException, SAXException, TransformerException
	{
		ContentReader contentReader = new ContentReader();

		// Geometery, DCEL

		String path1 = "res/presets/Startup.geom";
		Content content1 = contentReader.read(new File(path1));

		geometry(svg(), tikz(), content1);

		next();
		dcel(svg(), tikz(), content1);

		// Triangulations

		String path2 = "res/presets/polygons/Big.geom";
		Content content2 = contentReader.read(new File(path2));
		Polygon polygon = content2.getPolygons().get(0);

		next();
		monotone(svg(), tikz(),
				CopyUtil.copy(polygon, PolygonMode.REUSE_NOTHING));

		next();
		triangulation(svg(), tikz(),
				CopyUtil.copy(polygon, PolygonMode.REUSE_NOTHING));

		// Frechet

		String path3 = "res/presets/frechet/Paper.geom";
		Content content3 = contentReader.read(new File(path3));

		List<Chain> chains = content3.getChains();
		Chain chain1 = chains.get(0);
		Chain chain2 = chains.get(1);

		next();
		freeSpace(svg(), tikz(), chain1, chain2);

		next();
		distanceTerrain(svg(), tikz(), chain1, chain2);

		// Shortest path in polygon

		next();
		spip(svg(), tikz(), CopyUtil.copy(polygon, PolygonMode.REUSE_NOTHING));

		// Chan's Algorithm

		String path4 = "res/presets/chan/Chan1.geom";
		Content content4 = contentReader.read(new File(path4));

		List<Polygon> polygons = content4.getPolygons();

		next();
		chan(svg(), tikz(), polygons);

		// Fortune's Sweep

		String path5 = "res/presets/voronoi/Points1.geom";
		Content content5 = contentReader.read(new File(path5));

		next();
		fortune(svg(), tikz(), content5);
	}

	private static void freeSpace(File svg, File tikz, Chain chain1,
			Chain chain2) throws TransformerException, IOException
	{
		de.topobyte.livecg.algorithms.frechet.freespace.FreeSpaceConfig config = new de.topobyte.livecg.algorithms.frechet.freespace.FreeSpaceConfig();
		FreeSpacePainterChains freeSpacePainter = new FreeSpacePainterChains(
				config, 100, chain1, chain2, null);

		int width = 400;
		int height = 400;
		SvgExporter.exportSVG(svg, freeSpacePainter, width, height);
		TikzExporter.exportTikz(tikz, freeSpacePainter, width, height);
	}

	private static void distanceTerrain(File svg, File tikz, Chain chain1,
			Chain chain2) throws TransformerException, IOException
	{
		de.topobyte.livecg.algorithms.frechet.distanceterrain.DistanceTerrainConfig config = new de.topobyte.livecg.algorithms.frechet.distanceterrain.DistanceTerrainConfig();
		DistanceTerrainPainterChains terrainPainter = new DistanceTerrainPainterChains(
				config, chain1, chain2, null);

		int width = 400;
		int height = 400;
		SvgExporter.exportSVG(svg, terrainPainter, width, height);
		TikzExporter.exportTikz(tikz, terrainPainter, width, height);
	}

	private static void geometry(File svg, File tikz, Content content1)
			throws TransformerException, IOException
	{
		Rectangle scene = content1.getScene();
		int width = (int) Math.ceil(scene.getWidth());
		int height = (int) Math.ceil(scene.getHeight());

		ContentConfig contentConfig = new ContentConfig();
		ContentPainter contentPainter = new ContentPainter(scene, content1,
				contentConfig, null);

		SvgExporter.exportSVG(svg, contentPainter, width, height);
		TikzExporter.exportTikz(tikz, contentPainter, width, height);
	}

	private static void dcel(File svg, File tikz, Content content)
			throws TransformerException, IOException
	{
		Rectangle scene = content.getScene();
		int width = (int) Math.ceil(scene.getWidth());
		int height = (int) Math.ceil(scene.getHeight());

		DcelConfig dcelConfig = new DcelConfig();
		DCEL dcel = DcelConverter.convert(content);
		DcelPainter dcelPainter = new InstanceDcelPainter(scene, dcel,
				dcelConfig, null);

		SvgExporter.exportSVG(svg, dcelPainter, width, height);
		TikzExporter.exportTikz(tikz, dcelPainter, width, height);
	}

	private static void monotone(File svg, File tikz, Polygon polygon)
			throws TransformerException, IOException
	{
		MonotonePiecesAlgorithm algorithm = new MonotonePiecesAlgorithm(polygon);

		MonotonePiecesConfig polygonConfig = new MonotonePiecesConfig();
		Map<Polygon, Color> colorMap = ColorMapBuilder.buildColorMap(algorithm
				.getExtendedGraph());
		MonotonePiecesPainter triangulationPainter = new MonotonePiecesPainter(
				algorithm, polygonConfig, colorMap, null);

		Rectangle scene = algorithm.getScene();
		int width = (int) Math.ceil(scene.getWidth());
		int height = (int) Math.ceil(scene.getHeight());

		SvgExporter.exportSVG(svg, triangulationPainter, width, height);
		TikzExporter.exportTikz(tikz, triangulationPainter, width, height);
	}

	private static void triangulation(File svg, File tikz, Polygon polygon)
			throws TransformerException, IOException
	{
		MonotonePiecesTriangulationAlgorithm algorithm = new MonotonePiecesTriangulationAlgorithm(
				polygon);

		MonotonePiecesConfig polygonConfig = new MonotonePiecesConfig();
		Map<Polygon, Color> colorMap = ColorMapBuilder.buildColorMap(algorithm
				.getExtendedGraph());
		MonotonePiecesTriangulationPainter triangulationPainter = new MonotonePiecesTriangulationPainter(
				algorithm, polygonConfig, colorMap, null);

		Rectangle scene = algorithm.getScene();
		int width = (int) Math.ceil(scene.getWidth());
		int height = (int) Math.ceil(scene.getHeight());

		SvgExporter.exportSVG(svg, triangulationPainter, width, height);
		TikzExporter.exportTikz(tikz, triangulationPainter, width, height);
	}

	private static void spip(File svg, File tikz, Polygon polygon)
			throws TransformerException, IOException
	{
		PairOfNodes nodes = ShortestPathHelper.determineGoodNodes(polygon);
		Node start = nodes.getA();
		Node target = nodes.getB();
		ShortestPathAlgorithm algorithm = new ShortestPathAlgorithm(polygon,
				start, target);
		de.topobyte.livecg.algorithms.polygon.shortestpath.ShortestPathConfig config = new de.topobyte.livecg.algorithms.polygon.shortestpath.ShortestPathConfig();
		ShortestPathPainter shortestPathPainter = new ShortestPathPainter(
				algorithm, config, null);

		algorithm.setStatus(10, 4);

		Rectangle scene = algorithm.getScene();
		int width = (int) Math.ceil(scene.getWidth());
		int height = (int) Math.ceil(scene.getHeight());

		SvgExporter.exportSVG(svg, shortestPathPainter, width, height);
		TikzExporter.exportTikz(tikz, shortestPathPainter, width, height);
	}

	private static void chan(File svg, File tikz, List<Polygon> polygons)
			throws TransformerException, IOException
	{
		ChansAlgorithm chansAlgorithm = new ChansAlgorithm(polygons);
		ChansAlgorithmPainter algorithmPainter = new ChansAlgorithmPainter(
				chansAlgorithm, null);

		for (int i = 0; i < 50; i++) {
			chansAlgorithm.nextStep();
		}

		Rectangle scene = chansAlgorithm.getScene();
		int width = (int) Math.ceil(scene.getWidth());
		int height = (int) Math.ceil(scene.getHeight());

		SvgExporter.exportSVG(svg, algorithmPainter, width, height);
		TikzExporter.exportTikz(tikz, algorithmPainter, width, height);
	}

	private static void fortune(File svg, File tikz, Content content)
			throws TransformerException, IOException
	{
		List<Node> nodes = ContentHelper.collectNodes(content);
		FortunesSweep fortunesSweep = new FortunesSweep();
		for (Node node : nodes) {
			Coordinate c = node.getCoordinate();
			Point p = new Point(c.getX(), c.getY());
			fortunesSweep.addSite(p, false);
		}
		de.topobyte.livecg.algorithms.voronoi.fortune.ui.core.Config config = new de.topobyte.livecg.algorithms.voronoi.fortune.ui.core.Config();
		FortunePainter fortunePainter = new FortunePainter(fortunesSweep,
				config, null);

		fortunesSweep.setSweep(500);

		Rectangle scene = content.getScene();
		int width = (int) Math.ceil(scene.getWidth());
		int height = (int) Math.ceil(scene.getHeight());

		SvgExporter.exportSVG(svg, fortunePainter, width, height);
		TikzExporter.exportTikz(tikz, fortunePainter, width, height);
	}
}
