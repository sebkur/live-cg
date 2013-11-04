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
package de.topobyte.livecg.geometryeditor.geometryeditor.clipboard;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.geometryeditor.geometryeditor.SetOfGeometries;

public class GeometryTransferable implements Transferable
{

	final static Logger logger = LoggerFactory
			.getLogger(GeometryTransferable.class);

	public static DataFlavor flavorPlainText = DataFlavor
			.getTextPlainUnicodeFlavor();
	public static DataFlavor flavorGeometries = new GeometryDataFlavor();

	private SetOfGeometries geometries;

	public GeometryTransferable(SetOfGeometries geometries)
	{
		this.geometries = geometries;
		System.out.println("Create GeometryTransferable");
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		System.out.println("Get Flavors");
		return new DataFlavor[] { flavorGeometries, flavorPlainText };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		System.out.println("isDataFlavorSupported");
		return flavor.equals(flavorPlainText)
				|| flavor.equals(flavorGeometries);
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException
	{
		System.out.println("Get data: " + flavor);
		if (flavor.equals(flavorPlainText)) {
			return buildStringInputStream();
		}
		if (flavor.equals(flavorGeometries)) {
			return geometries;
		}
		return null;
	}

	private InputStream buildStringInputStream()
	{
		String text = buildText();
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(text.getBytes("unicode"));
		} catch (UnsupportedEncodingException e) {
			logger.debug("unable to create string: " + e.getMessage());
		}
		return bais;
	}

	private String buildText()
	{
		// TODO Auto-generated method stub
		return "";
	}
}
