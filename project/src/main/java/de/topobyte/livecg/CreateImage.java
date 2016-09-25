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

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.algorithms.convexhull.chan.ChanVisualizationSetup;
import de.topobyte.livecg.algorithms.frechet.distanceterrain.DistanceTerrainVisualizationSetup;
import de.topobyte.livecg.algorithms.frechet.freespace.FreeSpaceVisualizationSetup;
import de.topobyte.livecg.algorithms.jts.buffer.BufferVisualizationSetup;
import de.topobyte.livecg.algorithms.polygon.monotone.MonotoneTriangulationVisualizationSetup;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesVisualizationSetup;
import de.topobyte.livecg.algorithms.polygon.shortestpath.ShortestPathVisualizationSetup;
import de.topobyte.livecg.algorithms.polygon.triangulation.viamonotonepieces.MonotonePiecesTriangulationVisualizationSetup;
import de.topobyte.livecg.algorithms.voronoi.fortune.FortunesSweepVisualizationSetup;
import de.topobyte.livecg.core.SetupResult;
import de.topobyte.livecg.core.VisualizationSetup;
import de.topobyte.livecg.core.config.LiveConfig;
import de.topobyte.livecg.core.export.ExportFormat;
import de.topobyte.livecg.core.export.GraphicsExporter;
import de.topobyte.livecg.core.export.IpeExporter;
import de.topobyte.livecg.core.export.SvgExporter;
import de.topobyte.livecg.core.export.TikzExporter;
import de.topobyte.livecg.core.geometry.io.ContentReader;
import de.topobyte.livecg.core.painting.VisualizationPainter;
import de.topobyte.livecg.datastructures.content.ContentVisualizationSetup;
import de.topobyte.livecg.datastructures.dcel.DcelVisualizationSetup;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.parsing.ArgumentHelper;
import de.topobyte.utilities.apache.commons.cli.parsing.EnumArgument;
import de.topobyte.utilities.apache.commons.cli.parsing.StringOption;

public class CreateImage
{
	static final Logger logger = LoggerFactory.getLogger(CreateImage.class);

	private static final String HELP_MESSAGE = CreateImage.class
			.getSimpleName() + " [args]";

	private static final String OPTION_CONFIG = "config";
	private static final String OPTION_INPUT = "input";
	private static final String OPTION_OUTPUT = "output";
	private static final String OPTION_OUTPUT_FORMAT = "output-format";
	private static final String OPTION_VISUALIZATION = "visualization";
	private static final String OPTION_STATUS = "status";
	private static final String OPTION_PROPERTIES = "D";

	public static void main(String[] args)
	{
		EnumArgument<ExportFormat> exportSwitch = new EnumArgument<>(
				ExportFormat.class);

		EnumArgument<Visualization> visualizationSwitch = new EnumArgument<>(
				Visualization.class);

		// @formatter:off
		Options options = new Options();
		OptionHelper.addL(options, OPTION_CONFIG, true, false, "path", "config file");
		OptionHelper.addL(options, OPTION_INPUT, true, true, "file", "input geometry file");
		OptionHelper.addL(options, OPTION_OUTPUT, true, true, "file", "output file");
		OptionHelper.addL(options, OPTION_OUTPUT_FORMAT, true, true, "type",
					"type of output. one of <png,svg,tikz,ipe>");
		OptionHelper.addL(options, OPTION_VISUALIZATION, true, true, "type",
					"type of visualization. one of " + visualizationSwitch.getPossibleNames(true));
		OptionHelper.addL(options, OPTION_STATUS, true, false, "status to " +
				"set the algorithm to. The format depends on the algorithm");
		// @formatter:on

		Option propertyOption = new Option(OPTION_PROPERTIES,
				"set a special property");
		propertyOption.setArgName("property=value");
		propertyOption.setArgs(2);
		propertyOption.setValueSeparator('=');
		options.addOption(propertyOption);

		CommandLineParser clp = new DefaultParser();

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
		StringOption argStatus = ArgumentHelper.getString(line, OPTION_STATUS);

		ExportFormat exportFormat = exportSwitch.parse(argOutputFormat
				.getValue());
		if (exportFormat == null) {
			System.err.println("Unsupported output format '"
					+ argOutputFormat.getValue() + "'");
			System.exit(1);
		}

		Visualization visualization = visualizationSwitch
				.parse(argVisualization.getValue());
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

		Properties properties = line.getOptionProperties(OPTION_PROPERTIES);

		double zoom = 1;

		String statusArgument = null;
		if (argStatus.hasValue()) {
			statusArgument = argStatus.getValue();
		}

		VisualizationSetup setup = null;

		switch (visualization) {
		case GEOMETRY: {
			setup = new ContentVisualizationSetup();
			break;
		}
		case DCEL: {
			setup = new DcelVisualizationSetup();
			break;
		}
		case FREESPACE: {
			setup = new FreeSpaceVisualizationSetup();
			break;
		}
		case DISTANCETERRAIN: {
			setup = new DistanceTerrainVisualizationSetup();
			break;
		}
		case CHAN: {
			setup = new ChanVisualizationSetup();
			break;
		}
		case MONOTONE_PIECES: {
			setup = new MonotonePiecesVisualizationSetup();
			break;
		}
		case MONOTONE_TRIANGULATION: {
			setup = new MonotoneTriangulationVisualizationSetup();
			break;
		}
		case TRIANGULATION: {
			setup = new MonotonePiecesTriangulationVisualizationSetup();
			break;
		}
		case BUFFER: {
			setup = new BufferVisualizationSetup();
			break;
		}
		case FORTUNE: {
			setup = new FortunesSweepVisualizationSetup();
			break;
		}
		case SPIP: {
			setup = new ShortestPathVisualizationSetup();
			break;
		}
		}

		if (setup == null) {
			System.err.println("Not yet implemented");
			System.exit(1);
		}

		SetupResult setupResult = setup.setup(content, statusArgument,
				properties, zoom);

		int width = setupResult.getWidth();
		int height = setupResult.getHeight();

		VisualizationPainter visualizationPainter = setupResult
				.getVisualizationPainter();

		File output = new File(argOutput.getValue());

		visualizationPainter.setZoom(zoom);

		switch (exportFormat) {
		case IPE: {
			try {
				IpeExporter.exportIpe(output, visualizationPainter, width,
						height);
			} catch (Exception e) {
				System.err.println("Error while exporting. Exception type: "
						+ e.getClass().getSimpleName() + ", message: "
						+ e.getMessage());
				System.exit(1);
			}
			break;
		}
		case PNG: {
			try {
				GraphicsExporter.exportPNG(output, visualizationPainter, width,
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
			try {
				SvgExporter.exportSVG(output, visualizationPainter, width,
						height);
			} catch (Exception e) {
				System.err.println("Error while exporting. Exception type: "
						+ e.getClass().getSimpleName() + ", message: "
						+ e.getMessage());
				System.exit(1);
			}
			break;
		}
		case TIKZ: {
			try {
				TikzExporter.exportTikz(output, visualizationPainter, width,
						height);
			} catch (Exception e) {
				System.err.println("Error while exporting. Exception type: "
						+ e.getClass().getSimpleName() + ", message: "
						+ e.getMessage());
				System.exit(1);
			}
			break;
		}
		}
	}
}
