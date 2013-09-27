package fortune.sweep.gui.swing;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FileFilterPointSet extends FileFilter
{

	@Override
	public boolean accept(File path)
	{
		return path.isDirectory() || path.getName().endsWith(".points");
	}

	@Override
	public String getDescription()
	{
		return "point sets (*.points)";
	}

}
