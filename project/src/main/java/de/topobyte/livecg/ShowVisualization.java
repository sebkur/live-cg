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
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.algorithms.convexhull.chan.ChanLauncher;
import de.topobyte.livecg.algorithms.frechet.distanceterrain.DistanceTerrainChainsLauncher;
import de.topobyte.livecg.algorithms.frechet.freespace.FreeSpaceChainsLauncher;
import de.topobyte.livecg.algorithms.jts.buffer.PolygonBufferLauncher;
import de.topobyte.livecg.algorithms.polygon.monotone.MonotoneTriangulationLauncher;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesLauncher;
import de.topobyte.livecg.algorithms.polygon.shortestpath.ShortestPathInPolygonLauncher;
import de.topobyte.livecg.algorithms.polygon.triangulation.viamonotonepieces.MonotonePiecesTriangulationLauncher;
import de.topobyte.livecg.algorithms.voronoi.fortune.FortunesSweepLauncher;
import de.topobyte.livecg.core.config.LiveConfig;
import de.topobyte.livecg.core.geometry.io.ContentReader;
import de.topobyte.livecg.datastructures.content.ContentDisplayLauncher;
import de.topobyte.livecg.datastructures.dcel.DcelLauncher;
import de.topobyte.livecg.ui.ContentLauncher;
import de.topobyte.livecg.ui.LaunchException;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.parsing.ArgumentHelper;
import de.topobyte.utilities.apache.commons.cli.parsing.EnumArgument;
import de.topobyte.utilities.apache.commons.cli.parsing.StringOption;

public class ShowVisualization
{
	static final Logger logger = LoggerFactory
			.getLogger(ShowVisualization.class);

	private static final String HELP_MESSAGE = ShowVisualization.class
			.getSimpleName() + " [options] <file>";

	private static final String OPTION_CONFIG = "config";
	private static final String OPTION_VISUALIZATION = "visualization";
	private static final String OPTION_STATUS = "status";
	private static final String OPTION_PROPERTIES = "D";

	public static void main(String[] args)
	{
		EnumArgument<Visualization> visualizationSwitch = new EnumArgument<>(
				Visualization.class);

		// @formatter:off
		Options options = new Options();
		OptionHelper.addL(options, OPTION_CONFIG, true, false, "path", "config file");
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

		String[] extra = line.getArgs();
		if (extra.length == 0) {
			System.out.println("Missing file argument");
			new HelpFormatter().printHelp(HELP_MESSAGE, options);
			System.exit(1);
		}
		String input = extra[0];

		StringOption argConfig = ArgumentHelper.getString(line, OPTION_CONFIG);
		if (argConfig.hasValue()) {
			String configPath = argConfig.getValue();
			LiveConfig.setPath(configPath);
		}

		StringOption argVisualization = ArgumentHelper.getString(line,
				OPTION_VISUALIZATION);
		StringOption argStatus = ArgumentHelper.getString(line, OPTION_STATUS);

		Visualization visualization = visualizationSwitch
				.parse(argVisualization.getValue());
		if (visualization == null) {
			System.err.println("Unsupported visualization '"
					+ argVisualization.getValue() + "'");
			System.exit(1);
		}

		System.out.println("Visualization: " + visualization);

		ContentReader contentReader = new ContentReader();
		Content content = null;
		try {
			content = contentReader.read(new File(input));
		} catch (Exception e) {
			System.out.println("Error while reading input file '" + input
					+ "'. Exception type: " + e.getClass().getSimpleName()
					+ ", message: " + e.getMessage());
			System.exit(1);
		}

		Properties properties = line.getOptionProperties(OPTION_PROPERTIES);

		ContentLauncher launcher = null;

		switch (visualization) {
		case GEOMETRY: {
			launcher = new ContentDisplayLauncher();
			break;
		}
		case DCEL: {
			launcher = new DcelLauncher();
			break;
		}
		case FREESPACE: {
			launcher = new FreeSpaceChainsLauncher();
			break;
		}
		case DISTANCETERRAIN: {
			launcher = new DistanceTerrainChainsLauncher();
			break;
		}
		case CHAN: {
			launcher = new ChanLauncher();
			break;
		}
		case FORTUNE: {
			launcher = new FortunesSweepLauncher();
			break;
		}
		case MONOTONE_PIECES: {
			launcher = new MonotonePiecesLauncher();
			break;
		}
		case MONOTONE_TRIANGULATION: {
			launcher = new MonotoneTriangulationLauncher();
			break;
		}
		case TRIANGULATION: {
			launcher = new MonotonePiecesTriangulationLauncher();
			break;
		}
		case SPIP: {
			launcher = new ShortestPathInPolygonLauncher();
			break;
		}
		case BUFFER: {
			launcher = new PolygonBufferLauncher();
			break;
		}
		}

		try {
			launcher.launch(content, true);
		} catch (LaunchException e) {
			System.err.println("Unable to start visualization");
			System.err.println("Error message: " + e.getMessage());
			System.exit(1);
		}
	}
}
