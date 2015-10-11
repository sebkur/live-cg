/* This file is part of LiveCG.
 *
 * Copyright (C) 2014  Sebastian Kuerten
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
package de.topobyte.livecg.algorithms.polygon.monotone;

import javax.swing.JToolBar;

import de.topobyte.livecg.util.ZoomInput;
import de.topobyte.livecg.util.controls.ControlManager;
import de.topobyte.livecg.util.controls.Icons;

public class Settings extends JToolBar
{

	private static final long serialVersionUID = 1L;

	private static final String NEXT1 = "next1";
	private static final String NEXT2 = "next2";
	private static final String PREV1 = "prev1";
	private static final String PREV2 = "prev2";

	private static final String TEXT_PREV1 = "Previous node";
	private static final String TEXT_NEXT1 = "Next node";
	private static final String TEXT_PREV2 = "Previous step";
	private static final String TEXT_NEXT2 = "Next step";

	private MonotoneTriangulationAlgorithm algorithm;
	private MonotoneTriangulationPanel mtp;

	private ControlManager controlManager;

	public Settings(MonotoneTriangulationAlgorithm algorithm,
			MonotoneTriangulationPanel mtp, MonotoneTriangulationConfig config)
	{
		this.algorithm = algorithm;
		this.mtp = mtp;
		ZoomInput zoomInput = new ZoomInput(mtp);
		add(zoomInput);

		controlManager = new ControlManager() {

			@Override
			protected void control(String key)
			{
				Settings.this.control(key);
			}
		};

		add(controlManager.add(PREV1, Icons.SKIP_BACKWARD, TEXT_PREV1));
		add(controlManager.add(NEXT1, Icons.SKIP_FORWARD, TEXT_NEXT1));
		add(controlManager.add(PREV2, Icons.SEEK_BACKWARD, TEXT_PREV2));
		add(controlManager.add(NEXT2, Icons.SEEK_FORWARD, TEXT_NEXT2));
	}

	protected void control(String id)
	{
		if (id.equals(PREV1)) {
			tryPreviousNode(0);
		} else if (id.equals(NEXT1)) {
			tryNextNode();
		} else if (id.equals(PREV2)) {
			int subStatus = algorithm.getSubStatus();
			if (subStatus > 0) {
				algorithm.setSubStatus(subStatus - 1);
				mtp.repaint();
			} else if (subStatus == 0) {
				tryPreviousNode(-1);
			}
		} else if (id.equals(NEXT2)) {
			int subStatus = algorithm.getSubStatus();
			int nos = algorithm.numberOfMinorSteps();
			if (subStatus < nos) {
				algorithm.setSubStatus(subStatus + 1);
				mtp.repaint();
			} else if (subStatus == nos) {
				tryNextNode();
			}
		}
	}

	private void tryPreviousNode(int subStatus)
	{
		int status = algorithm.getStatus();
		if (status > 0) {
			algorithm.setStatus(status - 1, subStatus);
			mtp.repaint();
		}
	}

	private void tryNextNode()
	{
		int status = algorithm.getStatus();
		if (status < algorithm.getNumberOfSteps()) {
			algorithm.setStatus(status + 1, 0);
			mtp.repaint();
		}
	}
}
