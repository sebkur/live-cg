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

package de.topobyte.livecg.geometryeditor.geometryeditor;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import de.topobyte.livecg.LiveCG;
import de.topobyte.livecg.core.scrolling.ZoomAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.CopyAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.FilePropertiesAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.MouseAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.NewAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.OpenAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.PasteAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.SaveAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.SelectAllAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.SelectNothingAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.ShowContentDialogAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.ShowObjectDialogAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.visualizations.DcelAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.visualizations.DistanceTerrainChainsAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.visualizations.DistanceTerrainSegmentsAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.visualizations.FortunesSweepAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.visualizations.FreeSpaceChainsAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.visualizations.FreeSpaceSegmentsAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.visualizations.MonotonePiecesAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.visualizations.ShortestPathInPolygonAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.visualizations.TriangulationAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.visualizations.TriangulationDualGraphAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.mousemode.MouseMode;
import de.topobyte.livecg.geometryeditor.geometryeditor.mousemode.MouseModeDescriptions;
import de.topobyte.livecg.geometryeditor.geometryeditor.mousemode.MouseModeProvider;
import de.topobyte.livecg.geometryeditor.geometryeditor.presets.PresetMenu;
import de.topobyte.livecg.geometryeditor.misc.AboutAction;
import de.topobyte.livecg.geometryeditor.misc.ExitAction;
import de.topobyte.livecg.geometryeditor.misc.LicenseAction;
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
		JMenu view = new JMenu("View");
		JMenu window = new JMenu("Window");
		JMenu help = new JMenu("Help");
		add(file);
		add(tools);
		add(edit);
		add(presets);
		add(visualizations);
		add(view);
		add(window);
		add(help);

		file.setMnemonic('F');
		tools.setMnemonic('T');
		edit.setMnemonic('E');
		presets.setMnemonic('P');
		visualizations.setMnemonic('V');
		view.setMnemonic('I');
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
		JMenuItem properties = new JMenuItem(new FilePropertiesAction(this,
				editPane));
		file.add(newDocuemnt);
		file.add(openDocuemnt);
		file.add(saveDocuemnt);
		file.add(properties);
		file.add(exit);

		newDocuemnt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				KeyEvent.CTRL_DOWN_MASK));
		openDocuemnt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				KeyEvent.CTRL_DOWN_MASK));
		saveDocuemnt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				KeyEvent.CTRL_DOWN_MASK));
		properties.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
				KeyEvent.ALT_DOWN_MASK));
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				KeyEvent.CTRL_DOWN_MASK));

		/*
		 * Tools
		 */

		Map<MouseMode, Integer> mouseModeAccelerators = new HashMap<MouseMode, Integer>();
		mouseModeAccelerators.put(MouseMode.SELECT_MOVE, KeyEvent.VK_Q);
		mouseModeAccelerators.put(MouseMode.ROTATE, KeyEvent.VK_W);
		mouseModeAccelerators.put(MouseMode.SCALE, KeyEvent.VK_E);
		mouseModeAccelerators.put(MouseMode.SELECT_RECTANGULAR, KeyEvent.VK_A);
		mouseModeAccelerators.put(MouseMode.EDIT, KeyEvent.VK_S);
		mouseModeAccelerators.put(MouseMode.DELETE, KeyEvent.VK_D);

		for (MouseMode mode : new MouseMode[] { MouseMode.SELECT_MOVE,
				MouseMode.ROTATE, MouseMode.SCALE,
				MouseMode.SELECT_RECTANGULAR, MouseMode.EDIT, MouseMode.DELETE }) {
			MouseAction mouseAction = new MouseAction(
					MouseModeDescriptions.getShort(mode),
					MouseModeDescriptions.getLong(mode), mode,
					mouseModeProvider);
			JMenuItem mouseItem = new JMenuItem(mouseAction);
			tools.add(mouseItem);

			if (mouseModeAccelerators.get(mode) != null) {
				mouseItem.setAccelerator((KeyStroke.getKeyStroke(
						mouseModeAccelerators.get(mode), 0)));
			}
		}

		/*
		 * Edit
		 */

		JMenuItem copy = new JMenuItem(new CopyAction(editPane));
		JMenuItem paste = new JMenuItem(new PasteAction(editPane));
		JMenuItem selectNothing = new JMenuItem(new SelectNothingAction(
				editPane));
		JMenuItem selectEverything = new JMenuItem(
				new SelectAllAction(editPane));

		edit.add(copy);
		edit.add(paste);
		edit.add(selectNothing);
		edit.add(selectEverything);

		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				KeyEvent.CTRL_DOWN_MASK));
		paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
				KeyEvent.CTRL_DOWN_MASK));
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

		JMenu arrangements = new JMenu("Arrangements");
		arrangements.setIcon(folder);

		JMenu frechet = new JMenu("Fréchet distance");
		frechet.setIcon(folder);

		JMenu polygons = new JMenu("Polygons");
		polygons.setIcon(folder);

		JMenuItem dcel = new JMenuItem(new DcelAction(editPane));

		JMenuItem freeSpaceSegments = new JMenuItem(
				new FreeSpaceSegmentsAction());
		JMenuItem freeSpaceChains = new JMenuItem(new FreeSpaceChainsAction(
				editPane));
		JMenuItem distanceTerrainSegments = new JMenuItem(
				new DistanceTerrainSegmentsAction());
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
		visualizations.add(arrangements);
		visualizations.add(frechet);
		visualizations.add(polygons);

		arrangements.add(dcel);

		frechet.add(freeSpaceSegments);
		frechet.add(freeSpaceChains);
		frechet.add(distanceTerrainSegments);
		frechet.add(distanceTerrainChains);

		polygons.add(monotonePieces);
		polygons.add(triangulation);
		polygons.add(triangulationWithDualGraph);
		polygons.add(shortestPathInPolygon);

		/*
		 * View
		 */

		JMenuItem itemZoomIn = new JMenuItem(new ZoomAction<GeometryEditPane>(
				editPane, ZoomAction.Type.IN));
		JMenuItem itemZoomOut = new JMenuItem(new ZoomAction<GeometryEditPane>(
				editPane, ZoomAction.Type.OUT));
		JMenuItem itemZoom100 = new JMenuItem(new ZoomAction<GeometryEditPane>(
				editPane, ZoomAction.Type.IDENTITY));
		view.add(itemZoomIn);
		view.add(itemZoomOut);
		view.add(itemZoom100);
		itemZoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS,
				KeyEvent.CTRL_DOWN_MASK));
		itemZoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS,
				KeyEvent.CTRL_DOWN_MASK));
		itemZoom100.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				KeyEvent.CTRL_DOWN_MASK));

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
