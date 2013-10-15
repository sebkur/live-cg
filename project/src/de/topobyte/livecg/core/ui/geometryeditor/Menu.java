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

package de.topobyte.livecg.core.ui.geometryeditor;

import java.awt.event.KeyEvent;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import de.topobyte.livecg.LiveCG;
import de.topobyte.livecg.core.ui.geometryeditor.action.MouseAction;
import de.topobyte.livecg.core.ui.geometryeditor.action.NewAction;
import de.topobyte.livecg.core.ui.geometryeditor.action.OpenAction;
import de.topobyte.livecg.core.ui.geometryeditor.action.SaveAction;
import de.topobyte.livecg.core.ui.geometryeditor.action.SelectAllAction;
import de.topobyte.livecg.core.ui.geometryeditor.action.SelectNothingAction;
import de.topobyte.livecg.core.ui.geometryeditor.action.ShowContentDialogAction;
import de.topobyte.livecg.core.ui.geometryeditor.action.ShowObjectDialogAction;
import de.topobyte.livecg.core.ui.geometryeditor.action.visualizations.DistanceTerrainChainsAction;
import de.topobyte.livecg.core.ui.geometryeditor.action.visualizations.DistanceTerrainLinesAction;
import de.topobyte.livecg.core.ui.geometryeditor.action.visualizations.FortunesSweepAction;
import de.topobyte.livecg.core.ui.geometryeditor.action.visualizations.FreeSpaceChainsAction;
import de.topobyte.livecg.core.ui.geometryeditor.action.visualizations.FreeSpaceLinesAction;
import de.topobyte.livecg.core.ui.geometryeditor.action.visualizations.MonotonePiecesAction;
import de.topobyte.livecg.core.ui.geometryeditor.action.visualizations.ShortestPathInPolygonAction;
import de.topobyte.livecg.core.ui.geometryeditor.action.visualizations.TriangulationAction;
import de.topobyte.livecg.core.ui.geometryeditor.action.visualizations.TriangulationDualGraphAction;
import de.topobyte.livecg.core.ui.geometryeditor.mousemode.MouseMode;
import de.topobyte.livecg.core.ui.geometryeditor.mousemode.MouseModeDescriptions;
import de.topobyte.livecg.core.ui.geometryeditor.mousemode.MouseModeProvider;
import de.topobyte.livecg.core.ui.geometryeditor.presets.PresetMenu;
import de.topobyte.livecg.core.ui.misc.AboutAction;
import de.topobyte.livecg.core.ui.misc.ExitAction;
import de.topobyte.livecg.core.ui.misc.LicenseAction;
import de.topobyte.livecg.util.ImageLoader;

public class Menu extends JMenuBar
{

	private static final long serialVersionUID = -7983876851509766368L;

	public Menu(LiveCG liveCG, GeometryEditPane editPane,
			MouseModeProvider mouseModeProvider)
	{
		JMenu file = new JMenu("File");
		JMenu tools = new JMenu("Tools");
		JMenu edit = new JMenu("Edit");
		JMenu presets = new JMenu("Presets");
		JMenu visualizations = new JMenu("Visualizations");
		JMenu window = new JMenu("Window");
		JMenu help = new JMenu("Help");
		add(file);
		add(tools);
		add(edit);
		add(presets);
		add(visualizations);
		add(window);
		add(help);

		file.setMnemonic('F');
		tools.setMnemonic('T');
		edit.setMnemonic('E');
		presets.setMnemonic('P');
		visualizations.setMnemonic('V');
		window.setMnemonic('W');
		help.setMnemonic('H');

		Icon folder = ImageLoader.load("res/images/24x24/folder.png");

		/*
		 * File
		 */

		JMenuItem exit = new JMenuItem(new ExitAction());
		JMenuItem newDocuemnt = new JMenuItem(new NewAction(editPane));
		JMenuItem openDocuemnt = new JMenuItem(new OpenAction(this, editPane));
		JMenuItem saveDocuemnt = new JMenuItem(new SaveAction(this, editPane));
		file.add(newDocuemnt);
		file.add(openDocuemnt);
		file.add(saveDocuemnt);
		file.add(exit);

		newDocuemnt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				KeyEvent.CTRL_DOWN_MASK));
		openDocuemnt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				KeyEvent.CTRL_DOWN_MASK));
		saveDocuemnt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				KeyEvent.CTRL_DOWN_MASK));
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				KeyEvent.CTRL_DOWN_MASK));

		/*
		 * Tools
		 */

		for (MouseMode mode : new MouseMode[] { MouseMode.SELECT_MOVE,
				MouseMode.SELECT_RECTANGULAR, MouseMode.EDIT, MouseMode.DELETE }) {
			MouseAction mouseAction = new MouseAction(
					MouseModeDescriptions.getShort(mode),
					MouseModeDescriptions.getLong(mode), mode,
					mouseModeProvider);
			JMenuItem mouseItem = new JMenuItem(mouseAction);
			tools.add(mouseItem);

			switch (mode) {
			case SELECT_MOVE:
				mouseItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
						0));
				break;
			case SELECT_RECTANGULAR:
				mouseItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
						0));
				break;
			case EDIT:
				mouseItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
						0));
				break;
			case DELETE:
				mouseItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
						0));
				break;
			}
		}

		/*
		 * Edit
		 */

		JMenuItem selectNothing = new JMenuItem(new SelectNothingAction(
				editPane));
		JMenuItem selectEverything = new JMenuItem(
				new SelectAllAction(editPane));
		edit.add(selectNothing);
		edit.add(selectEverything);

		selectEverything.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				KeyEvent.CTRL_DOWN_MASK));
		selectNothing.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));

		/*
		 * Presets
		 */

		PresetMenu presetMenu = new PresetMenu(presets);
		presetMenu.build(editPane);

		/*
		 * Visualizations
		 */

		JMenuItem fortunesSweep = new JMenuItem(new FortunesSweepAction(
				editPane));

		JMenu frechet = new JMenu("Fréchet distance");
		frechet.setIcon(folder);

		JMenu polygons = new JMenu("Polygons");
		polygons.setIcon(folder);

		JMenuItem freeSpaceLines = new JMenuItem(new FreeSpaceLinesAction());
		JMenuItem freeSpaceChains = new JMenuItem(new FreeSpaceChainsAction(
				editPane));
		JMenuItem distanceTerrainLines = new JMenuItem(
				new DistanceTerrainLinesAction());
		JMenuItem distanceTerrainChains = new JMenuItem(
				new DistanceTerrainChainsAction(editPane));

		JMenuItem monotonePieces = new JMenuItem(new MonotonePiecesAction(
				editPane));
		JMenuItem triangulation = new JMenuItem(new TriangulationAction(
				editPane));
		JMenuItem triangulationWithDualGraph = new JMenuItem(
				new TriangulationDualGraphAction(editPane));
		JMenuItem shortestPathInPolygon = new JMenuItem(
				new ShortestPathInPolygonAction(editPane));

		visualizations.add(fortunesSweep);
		visualizations.add(frechet);
		visualizations.add(polygons);

		frechet.add(freeSpaceLines);
		frechet.add(freeSpaceChains);
		frechet.add(distanceTerrainLines);
		frechet.add(distanceTerrainChains);

		polygons.add(monotonePieces);
		polygons.add(triangulation);
		polygons.add(triangulationWithDualGraph);
		polygons.add(shortestPathInPolygon);

		/*
		 * Window
		 */

		JMenuItem showObjectDialog = new JMenuItem(new ShowObjectDialogAction(
				liveCG));
		JMenuItem showContentDialog = new JMenuItem(
				new ShowContentDialogAction(liveCG));
		window.add(showObjectDialog);
		window.add(showContentDialog);

		/*
		 * Help
		 */

		JMenuItem about = new JMenuItem(new AboutAction());
		JMenuItem license = new JMenuItem(new LicenseAction());
		help.add(about);
		help.add(license);
	}
}
