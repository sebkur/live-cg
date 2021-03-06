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
package de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing.action;

import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing.FileFilterPointSet;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing.FortuneDialog;
import de.topobyte.livecg.core.geometry.io.PointSetWriter;
import de.topobyte.livecg.core.geometry.pointset.PointSet;

public class SaveAction extends FortuneAction
{

	final static Logger logger = LoggerFactory.getLogger(SaveAction.class);

	private static final long serialVersionUID = 1L;

	public SaveAction(FortuneDialog fortune)
	{
		super("Save", "Save the current point set", null, fortune);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(fortune.getLastActiveDirectory());
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileFilterPointSet());
		int returnVal = fc.showSaveDialog(fortune);

		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = fc.getSelectedFile();
		fortune.setLastActiveDirectory(file.getParentFile());

		List<Point> sites = fortune.getAlgorithm().getSites();
		PointSet pointSet = new PointSet();
		for (Point site : sites) {
			de.topobyte.livecg.core.geometry.pointset.Point point = new de.topobyte.livecg.core.geometry.pointset.Point(
					site.getX(), site.getY());
			pointSet.add(point);
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			PointSetWriter.write(pointSet, bos);

			bos.close();
		} catch (FileNotFoundException ex) {
			logger.error("file not found: '" + file + "'");
		} catch (IOException ex) {
			logger.error("unable to close output file");
		}
	}

}
