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
package de.topobyte.livecg;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.algorithms.convexhull.chan.ChansAlgorithm;
import de.topobyte.livecg.algorithms.convexhull.chan.ChansAlgorithmPainter;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesAlgorithm;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesConfig;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesPainter;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesTriangulationAlgorithm;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesTriangulationPainter;
import de.topobyte.livecg.algorithms.polygon.shortestpath.PairOfNodes;
import de.topobyte.livecg.algorithms.polygon.shortestpath.ShortestPathAlgorithm;
import de.topobyte.livecg.algorithms.polygon.shortestpath.ShortestPathConfig;
import de.topobyte.livecg.algorithms.polygon.shortestpath.ShortestPathHelper;
import de.topobyte.livecg.algorithms.polygon.shortestpath.ShortestPathPainter;
import de.topobyte.livecg.algorithms.voronoi.fortune.FortunesSweep;
import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.core.FortuneConfig;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.core.FortunePainter;
import de.topobyte.livecg.core.algorithm.Algorithm;
import de.topobyte.livecg.core.algorithm.SceneAlgorithm;
import de.topobyte.livecg.core.config.LiveConfig;
import de.topobyte.livecg.core.export.ExportFormat;
import de.topobyte.livecg.core.export.GraphicsExporter;
import de.topobyte.livecg.core.geometry.dcel.DCEL;
import de.topobyte.livecg.core.geometry.dcel.DcelConverter;
import de.topobyte.livecg.core.geometry.dcel.DcelUtil;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.ChainHelper;
import de.topobyte.livecg.core.geometry.geom.CloseabilityException;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.CopyUtil;
import de.topobyte.livecg.core.geometry.geom.CopyUtil.PolygonMode;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.PolygonHelper;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.geom.Rectangles;
import de.topobyte.livecg.core.geometry.io.ContentReader;
import de.topobyte.livecg.core.painting.AlgorithmPainter;
import de.topobyte.livecg.datastructures.content.ContentConfig;
import de.topobyte.livecg.datastructures.content.ContentPainter;
import de.topobyte.livecg.datastructures.dcel.DcelConfig;
import de.topobyte.livecg.datastructures.dcel.InstanceDcelPainter;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.ui.geometryeditor.ContentHelper;
import de.topobyte.livecg.util.coloring.ColorMapBuilder;
import de.topobyte.misc.util.enums.EnumNameLookup;
import de.topobyte.utilities.apache.commons.cli.ArgumentHelper;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.StringOption;

public class CreateImage
{
	static final Logger logger = LoggerFactory.getLogger(CreateImage.class);

	private static final String HELP_MESSAGE = CreateImage.class
			.getSimpleName() + " [args]";

	private static final String OPTION_CONFIG = "config";
	private static final String OPTION_INPUT = "input";
	private static final String OPTION_OUTPUT = "output";
	private static final String OPTION_OUTPUT_FORMAT = "output_format";
	private static final String OPTION_VISUALIZATION = "visualization";

	public static void main(String[] args)
	{
		EnumNameLookup<ExportFormat> exportSwitch = new EnumNameLookup<ExportFormat>(
				ExportFormat.class, true);

		EnumNameLookup<Visualization> visualizationSwitch = new EnumNameLookup<Visualization>(
				Visualization.class, true);

		// @formatter:off
		Options options = new Options();
		OptionHelper.add(options, OPTION_CONFIG, true, false, "path", "config file");
		OptionHelper.add(options, OPTION_INPUT, true, true, "file", "input geometry file");
		OptionHelper.add(options, OPTION_OUTPUT, true, true, "file", "output file");
		OptionHelper.add(options, OPTION_OUTPUT_FORMAT, true, true, "type",
					"type of output. one of <png,svg,tikz,ipe>");
		OptionHelper.add(options, OPTION_VISUALIZATION, true, true, "type", 
					"type of visualization. one of " +
					"<geometry, dcel, fortune, monotone, triangulation, " +
					"spip, freespace, distanceterrain, chan>");
		// @formatter:on

		CommandLineParser clp = new GnuParser();

		CommandLine line = null;
		try {
			line = clp.parse(options, args);
		} catch (ParseException e) {
			System.err
					.println("Parsing command line failed: " + e.getMessage());
			new HelpFormatter().printHelp(HELP_MESSAGE, options);
			System.exit(1);
		}

		StringOption argConfig = ArgumentHelper.getString(line, OPTION_CONFIG);
		if (argConfig.hasValue()) {
			String configPath = argConfig.getValue();
			LiveConfig.setPath(configPath);
		}

		StringOption argInput = ArgumentHelper.getString(line, OPTION_INPUT);
		StringOption argOutput = ArgumentHelper.getString(line, OPTION_OUTPUT);
		StringOption argOutputFormat = ArgumentHelper.getString(line,
				OPTION_OUTPUT_FORMAT);
		StringOption argVisualization = ArgumentHelper.getString(line,
				OPTION_VISUALIZATION);

		ExportFormat exportFormat = exportSwitch.find(argOutputFormat
				.getValue());
		if (exportFormat == null) {
			System.err.println("Unsupported output format '"
					+ argOutputFormat.getValue() + "'");
			System.exit(1);
		}

		Visualization visualization = visualizationSwitch.find(argVisualization
				.getValue());
		if (visualization == null) {
			System.err.println("Unsupported visualization '"
					+ argVisualization.getValue() + "'");
			System.exit(1);
		}

		System.out.println("Visualization: " + visualization);
		System.out.println("Output format: " + exportFormat);

		ContentReader contentReader = new ContentReader();
		Content content = null;
		try {
			content = contentReader.read(new File(argInput.getValue()));
		} catch (Exception e) {
			System.out.println("Error while reading input file '"
					+ argInput.getValue() + "'. Exception type: "
					+ e.getClass().getSimpleName() + ", message: "
					+ e.getMessage());
			System.exit(1);
		}

		Algorithm algorithm = null;
		Rectangle scene = null;
		SceneAlgorithm sceneAlgorithm = null;
		AlgorithmPainter algorithmPainter = null;

		int margin = 15;
		double zoom = 2;

		switch (visualization) {
		case GEOMETRY: {
			scene = content.getScene();
			ContentConfig config = new ContentConfig();
			algorithmPainter = new ContentPainter(scene, content, config, null);
			break;
		}
		case DCEL: {
			DCEL dcel = DcelConverter.convert(content);
			Rectangle bbox = DcelUtil.getBoundingBox(dcel);
			scene = Rectangles.extend(bbox, margin);
			DcelConfig config = new DcelConfig();
			algorithmPainter = new InstanceDcelPainter(scene, dcel, config,
					null);
			break;
		}
		case FREESPACE: {
			break;
		}
		case DISTANCETERRAIN: {
			break;
		}
		case CHAN: {
			List<Polygon> viable = new ArrayList<Polygon>();
			for (Polygon polygon : content.getPolygons()) {
				if (polygon.getHoles().size() == 0) {
					viable.add(polygon);
				}
				// TODO: if polygon is convex
			}
			if (viable.size() < 2) {
				System.err.println("Not enough viable polygons");
				System.exit(1);
			}

			List<Polygon> polygons = new ArrayList<Polygon>();

			for (Polygon polygon : viable) {
				if (PolygonHelper.isCounterClockwiseOriented(polygon)) {
					polygons.add(CopyUtil.copy(polygon,
							PolygonMode.REUSE_NOTHING));
				} else {
					Chain shell = polygon.getShell();
					try {
						polygon = new Polygon(ChainHelper.invert(shell), null);
						polygons.add(CopyUtil.copy(polygon,
								PolygonMode.REUSE_NOTHING));
					} catch (CloseabilityException e) {
						// Should not happen
					}
				}
			}
			ChansAlgorithm alg = new ChansAlgorithm(polygons);
			algorithm = alg;
			sceneAlgorithm = alg;
			algorithmPainter = new ChansAlgorithmPainter(alg, null);
			break;
		}
		case FORTUNE: {
			List<Node> nodes = ContentHelper.collectNodes(content);

			FortunesSweep alg = new FortunesSweep();

			List<Point> sites = new ArrayList<Point>();
			for (Node node : nodes) {
				Coordinate c = node.getCoordinate();
				sites.add(new Point(c.getX() * zoom, c.getY() * zoom));
			}
			alg.setSites(sites);

			algorithm = alg;
			FortuneConfig config = new FortuneConfig();
			algorithmPainter = new FortunePainter(alg, config, null);
			break;
		}
		case MONOTONE: {
			if (content.getPolygons().size() < 1) {
				System.err.println("This visualization requires a polygon");
				System.exit(1);
			}
			Polygon polygon = content.getPolygons().get(0);
			MonotonePiecesAlgorithm alg = new MonotonePiecesAlgorithm(polygon);
			MonotonePiecesConfig config = new MonotonePiecesConfig();
			algorithm = alg;
			sceneAlgorithm = alg;
			Map<Polygon, Color> colorMap = ColorMapBuilder.buildColorMap(alg
					.getExtendedGraph());
			algorithmPainter = new MonotonePiecesPainter(alg, config, colorMap,
					null);
			break;
		}
		case TRIANGULATION: {
			if (content.getPolygons().size() < 1) {
				System.err.println("This visualization requires a polygon");
				System.exit(1);
			}
			Polygon polygon = content.getPolygons().get(0);
			MonotonePiecesTriangulationAlgorithm alg = new MonotonePiecesTriangulationAlgorithm(
					polygon);
			MonotonePiecesConfig config = new MonotonePiecesConfig();
			algorithm = alg;
			sceneAlgorithm = alg;
			Map<Polygon, Color> colorMap = ColorMapBuilder.buildColorMap(alg
					.getExtendedGraph());
			algorithmPainter = new MonotonePiecesTriangulationPainter(alg,
					config, colorMap, null);
			break;
		}
		case SPIP: {
			if (content.getPolygons().size() < 1) {
				System.err.println("This visualization requires a polygon");
				System.exit(1);
			}
			Polygon polygon = content.getPolygons().get(0);
			PairOfNodes nodes = ShortestPathHelper.determineGoodNodes(polygon);
			ShortestPathAlgorithm alg = new ShortestPathAlgorithm(polygon,
					nodes.getA(), nodes.getB());
			ShortestPathConfig config = new ShortestPathConfig();
			algorithm = alg;
			sceneAlgorithm = alg;
			algorithmPainter = new ShortestPathPainter(alg, config, null);
			break;
		}
		}

		if (algorithmPainter == null) {
			System.err.println("Not yet implemented");
			System.exit(1);
		}

		File output = new File(argOutput.getValue());

		// Default dimension from content scene
		Rectangle contentScene = content.getScene();
		int width = (int) Math.ceil(contentScene.getWidth() * zoom);
		int height = (int) Math.ceil(contentScene.getHeight() * zoom);

		if (sceneAlgorithm != null) {
			// If the algorithm provides a scene
			Rectangle algScene = sceneAlgorithm.getScene();
			width = (int) Math.ceil(algScene.getWidth() * zoom);
			height = (int) Math.ceil(algScene.getHeight() * zoom);
		} else if (scene != null) {
			// Otherwise, if the setup defined a scene
			width = (int) Math.ceil(scene.getWidth() * zoom);
			height = (int) Math.ceil(scene.getHeight() * zoom);
		}

		switch (exportFormat) {
		case IPE: {
			break;
		}
		case PNG: {
			try {
				algorithmPainter.setZoom(zoom);
				GraphicsExporter.exportPNG(output, algorithmPainter, width,
						height);
			} catch (IOException e) {
				System.err.println("Error while exporting. Exception type: "
						+ e.getClass().getSimpleName() + ", message: "
						+ e.getMessage());
				System.exit(1);
			}
			break;
		}
		case SVG: {
			break;
		}
		case TIKZ: {
			break;
		}
		}
	}
}
