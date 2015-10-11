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
package de.topobyte.livecg.core.painting.backend.ipe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;

import org.apache.commons.codec.binary.Base64;

import de.topobyte.livecg.core.painting.Image;

public class IpeImageEncoder
{
	public static IpeImage encode(Image image) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DeflaterOutputStream dos = new DeflaterOutputStream(baos);
		for (int j = 0; j < image.getHeight(); j++) {
			for (int i = 0; i < image.getWidth(); i++) {
				int rgb = image.getRGB(i, j);
				dos.write(rgb >> 16);
				dos.write(rgb >> 8);
				dos.write(rgb);
			}
		}
		dos.close();
		byte[] buffer = baos.toByteArray();
		int length = buffer.length;

		byte[] bbase64 = Base64.encodeBase64(buffer);
		String base64 = new String(bbase64);

		return new IpeImage(base64, length);
	}
}
