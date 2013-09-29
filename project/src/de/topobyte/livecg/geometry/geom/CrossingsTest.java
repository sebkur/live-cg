package de.topobyte.livecg.geometry.geom;

import java.util.ArrayList;
import java.util.List;

public class CrossingsTest
{

	private List<Chain> chains;

	public CrossingsTest(Chain chain)
	{
		this.chains = new ArrayList<Chain>();
		chains.add(chain);
	}

	public CrossingsTest(List<Chain> chains)
	{
		this.chains = chains;
	}

	public boolean covers(Coordinate coordinate)
	{
		double tx = coordinate.getX();
		double ty = coordinate.getY();

		int crossings = 0;
		for (Chain chain : chains) {
			crossings += check(chain, tx, ty);
		}
		return crossings != 0;
	}

	private int check(Chain ring, double tx, double ty)
	{
		int crossings = 0;

		int n = ring.getNumberOfNodes();

		Coordinate c0 = ring.getCoordinate(n - 1);
		Coordinate c1 = ring.getCoordinate(0);

		boolean yflag0 = (c0.y >= ty);

		for (int j = 0; j < n; j++) {
			boolean yflag1 = (c1.y >= ty);
			if (yflag0 != yflag1) {
				boolean xflag0 = (c0.x >= tx);
				boolean xflag1 = (c1.x >= tx);
				if (xflag0 == xflag1) {
					if (xflag0) {
						crossings += (yflag0 ? -1 : 1);
					}
				} else {
					if ((c1.x - (c1.y - ty) * (c0.x - c1.x) / (c0.y - c1.y)) >= tx) {
						crossings += (yflag0 ? -1 : 1);
					}
				}
			}
			yflag0 = yflag1;
			c0 = c1;
			if (j + 1 < n) {
				c1 = ring.getCoordinate(j + 1);
			} else {
				c1 = ring.getCoordinate(0);
			}
		}
		return crossings;
	}

}
