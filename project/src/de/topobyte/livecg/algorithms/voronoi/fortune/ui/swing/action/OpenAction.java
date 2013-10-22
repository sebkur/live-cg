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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing.FileFilterPointSet;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing.FortuneDialog;
import de.topobyte.livecg.core.geometry.io.PointSetReader;
import de.topobyte.livecg.core.geometry.pointset.PointSet;
import de.topobyte.livecg.util.exception.ParseException;

public class OpenAction extends FortuneAction
{
	final static Logger logger = LoggerFactory.getLogger(OpenAction.class);

	private static final long serialVersionUID = 1L;

	public OpenAction(FortuneDialog fortune)
	{
		super("Open", "Open a point set", null, fortune);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(fortune.getLastActiveDirectory());
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileFilterPointSet());
		int returnVal = fc.showOpenDialog(fortune);

		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = fc.getSelectedFile();
		fortune.setLastActiveDirectory(file.getParentFile());

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
			fortune.getAlgorithm().setSites(sites);
		} catch (FileNotFoundException ex) {
			logger.error("file not found: '" + file + "'");
		} catch (IOException ex) {
			logger.error("IOException while reading point set: "
					+ ex.getMessage());
		} catch (ParseException ex) {
			logger.error("ParseException while reading point set: "
					+ ex.getMessage());
		}
	}
}
