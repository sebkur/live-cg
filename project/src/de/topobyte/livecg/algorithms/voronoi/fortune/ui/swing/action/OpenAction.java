package de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing.action;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing.FileFilterPointSet;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing.SwingFortune;
import de.topobyte.livecg.core.geometry.io.PointSetReader;
import de.topobyte.livecg.core.geometry.pointset.PointSet;
import de.topobyte.livecg.util.exception.ParseException;

public class OpenAction extends SwingFortuneAction
{

	private static final long serialVersionUID = 1L;

	public OpenAction(SwingFortune swingFortune)
	{
		super("Open", "Open a point set", null, swingFortune);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(swingFortune.getLastActiveDirectory());
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileFilterPointSet());
		int returnVal = fc.showOpenDialog(swingFortune);

		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = fc.getSelectedFile();
		swingFortune.setLastActiveDirectory(file.getParentFile());

		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);

			PointSet pointSet = PointSetReader.read(bis);

			try {
				fis.close();
			} catch (IOException ex) {
				// ignore
			}
			List<Point> sites = new ArrayList<Point>();
			for (de.topobyte.livecg.core.geometry.pointset.Point point : pointSet
					.getPoints()) {
				sites.add(new Point(point.getX(), point.getY()));
			}
			swingFortune.getAlgorithm().setSites(sites);
		} catch (FileNotFoundException ex) {
			System.out.println("file not found: '" + file + "'");
		} catch (IOException ex) {
			System.out.println("IOException while reading point set: "
					+ ex.getMessage());
		} catch (ParseException ex) {
			System.out.println("ParseException while reading point set: "
					+ ex.getMessage());
		}
	}
}