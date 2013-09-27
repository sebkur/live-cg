package data.pointset;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Locale;

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
