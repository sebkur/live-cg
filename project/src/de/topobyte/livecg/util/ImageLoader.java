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

package de.topobyte.livecg.util;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.Icon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 *
 */
public class ImageLoader
{

	static final Logger logger = LoggerFactory.getLogger(ImageLoader.class);

	/**
	 * Load an image from the given filename. The file-resource will be resolved
	 * by using the classloader. ImageIO will be tried first, then SVG via
	 * batik.
	 *
	 * @param filename
	 *            the resource to load.
	 * @return the Icon loaded or null.
	 */
	public static Icon load(String filename)
	{
		if (filename == null) {
			return null;
		}

		// first try ImageIO
		BufferedImage bi = null;
		try {
			InputStream is = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(filename);
			bi = ImageIO.read(is);
			is.close();
		} catch (Exception e) {
			logger.debug("unable to load icon: " + filename
					+ " exception message: " + e.getMessage());
			e.printStackTrace();
		}
		if (bi != null) {
			return new BufferedImageIcon(bi);
		}

		// unable to load image
		return null;
	}
}
