package de.topobyte.livecg.core.geometry.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Locale;

import de.topobyte.livecg.core.geometry.pointset.Point;
import de.topobyte.livecg.core.geometry.pointset.PointSet;

public class PointSetWriter
{

	public static void write(PointSet pointSet, OutputStream out)
			throws IOException
	{
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

		PrintWriter printer = new PrintWriter(out);
		for (Point point : pointSet.getPoints()) {
			printer.println(numberFormat.format(point.getX()) + ", "
					+ numberFormat.format(point.getY()));
		}

		printer.close();
	}
}
