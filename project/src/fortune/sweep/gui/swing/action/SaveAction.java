package fortune.sweep.gui.swing.action;

import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;

import data.pointset.PointSet;
import data.pointset.PointSetWriter;

import fortune.sweep.geometry.Point;
import fortune.sweep.gui.swing.FileFilterPointSet;
import fortune.sweep.gui.swing.SwingFortune;

public class SaveAction extends SwingFortuneAction
{

	private static final long serialVersionUID = 1L;

	public SaveAction(SwingFortune swingFortune)
	{
		super("Save", "Save the current point set", null, swingFortune);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(swingFortune.getLastActiveDirectory());
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileFilterPointSet());
		int returnVal = fc.showSaveDialog(swingFortune);

		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = fc.getSelectedFile();
		swingFortune.setLastActiveDirectory(file.getParentFile());

		List<Point> sites = swingFortune.getAlgorithm().getSites();
		PointSet pointSet = new PointSet();
		for (Point site : sites) {
			data.pointset.Point point = new data.pointset.Point(site.getX(),
					site.getY());
			pointSet.add(point);
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			PointSetWriter.write(pointSet, bos);

			bos.close();
		} catch (FileNotFoundException ex) {
			System.out.println("file not found: '" + file + "'");
		} catch (IOException ex) {
			System.out.println("unable to close output file");
		}
	}

}
