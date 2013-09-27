package data.pointset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.Locale;

public class PointSetReader
{

	public static PointSet read(InputStream in) throws IOException,
			ParseException
	{
		PointSet pointSet = new PointSet();

		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		for (String line = reader.readLine(); line != null; line = reader
				.readLine()) {
			String[] parts = line.split(",");
			if (parts.length != 2) {
				throw new ParseException("number of fields is not 2");
			}
			String sx = parts[0].trim();
			String sy = parts[1].trim();
			double x, y;
			try {
				x = numberFormat.parse(sx).doubleValue();
			} catch (java.text.ParseException e) {
				throw new ParseException("unable to parse x value: '" + sx
						+ "'");
			}
			try {
				y = numberFormat.parse(sy).doubleValue();
			} catch (java.text.ParseException e) {
				throw new ParseException("unable to parse y value: '" + sy
						+ "'");
			}
			pointSet.add(new Point(x, y));
		}

		return pointSet;
	}
}
