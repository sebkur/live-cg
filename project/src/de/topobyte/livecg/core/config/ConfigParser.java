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
package de.topobyte.livecg.core.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.util.Stack;
import de.topobyte.livecg.util.resources.ResourceFile;
import de.topobyte.livecg.util.resources.ResourceLoader;

public class ConfigParser
{
	final static Logger logger = LoggerFactory.getLogger(ConfigParser.class);

	private Configuration config;

	private ConfigParser()
	{
		config = new Configuration();
	}

	public static Configuration parse(String path) throws IOException
	{
		ConfigParser parser = new ConfigParser();
		ResourceFile file = ResourceLoader.open(path);
		InputStream input = file.open();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		logger.debug("PARSING: " + path);
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				break;
			}
			parser.parseLine(line);
		}

		return parser.config;
	}

	private Stack<String> prefixes = new Stack<String>();

	private void parseLine(String line)
	{
		String trimmedLine = line.trim();
		if (trimmedLine.endsWith("{")) {
			String prefix = trimmedLine.substring(0, trimmedLine.length() - 1);
			prefix = prefix.trim();
			logger.debug("PREFIX: " + prefix);
			prefixes.push(prefix);
			return;
		} else if (trimmedLine.startsWith("}")) {
			prefixes.pop();
			return;
		}
		String[] parts = line.split("=");
		if (parts.length != 2) {
			return;
		}
		String name = parts[0].trim();
		String value = parts[1].trim();

		String fullName = buildName(name);

		if (value.startsWith("#")) {
			Color color = parseColor(value, null);
			config.colors.put(fullName, color);
		}
	}

	private String buildName(String name)
	{
		StringBuilder buffer = new StringBuilder();
		for (String prefix : prefixes) {
			buffer.append(prefix);
			buffer.append(".");
		}
		buffer.append(name);
		logger.debug("FULL: " + buffer.toString());
		return buffer.toString();
	}

	private static Pattern patternColor = Pattern
			.compile("#([0-9a-fA-F]{6}|[0-9a-fA-F]{8})");

	public static Color parseColor(String value, Color defaultValue)
	{
		if (value == null) {
			return defaultValue;
		}
		Matcher matcher = patternColor.matcher(value);
		if (!matcher.matches()) {
			System.out.println(value);
		}
		if (value.length() == 7) {
			String r = value.substring(1, 3);
			String g = value.substring(3, 5);
			String b = value.substring(5, 7);
			int ir = Integer.parseInt(r, 16);
			int ig = Integer.parseInt(g, 16);
			int ib = Integer.parseInt(b, 16);
			return new Color(ir, ig, ib);
		} else if (value.length() == 9) {
			String a = value.substring(1, 3);
			String r = value.substring(3, 5);
			String g = value.substring(5, 7);
			String b = value.substring(7, 9);
			int ia = Integer.parseInt(a, 16);
			int ir = Integer.parseInt(r, 16);
			int ig = Integer.parseInt(g, 16);
			int ib = Integer.parseInt(b, 16);
			return new Color(ir, ig, ib, ia);
		}
		return null;
	}

}
