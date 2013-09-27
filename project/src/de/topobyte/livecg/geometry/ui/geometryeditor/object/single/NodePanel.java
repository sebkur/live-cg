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
package de.topobyte.livecg.geometry.ui.geometryeditor.object.single;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.Coordinate;
import de.topobyte.livecg.geometry.geom.Node;
import de.topobyte.livecg.geometry.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.geometry.ui.misc.Borders;
import de.topobyte.livecg.geometry.ui.misc.Borders.BorderState;
import de.topobyte.swing.DocumentAdapter;
import de.topobyte.swing.JPanelTextField;
import de.topobyte.swing.layout.GridBagHelper;

public class NodePanel extends JPanel
{

	private static final long serialVersionUID = 5640771403274002420L;

	private Node node;
	private JLabel label;
	private JPanelTextField inputX, inputY;
	private JLabel labelInfo;

	private GeometryEditPane editPane;

	public NodePanel(GeometryEditPane editPane, Node node)
	{
		this.editPane = editPane;
		this.node = node;
		setLayout(new GridBagLayout());
		label = new JLabel();
		labelInfo = new JLabel();

		inputX = new JPanelTextField();
		inputY = new JPanelTextField();
		
		NodeActionPanel nodeActionPanel = new NodeActionPanel(editPane, node);

		setNoPreferredWidth(inputX.getTextField());
		setNoPreferredWidth(inputY.getTextField());

		GridBagConstraints c = new GridBagConstraints();

		int y = 0;
		
		GridBagHelper.setGxGyGwGh(c, 0, y, 2, 1);
		c.anchor = GridBagConstraints.LINE_START;
		add(label, c);
		
		GridBagHelper.setGxGyGwGh(c, 0, ++y, 2, 1);
		add(nodeActionPanel, c);

		GridBagHelper.setGxGyGwGh(c, 0, ++y, 1, 1);
		GridBagHelper.setWxWyF(c, 1.0, 0.0, GridBagConstraints.HORIZONTAL);

		c.gridx = 0;
		add(inputX, c);
		c.gridx = 1;
		add(inputY, c);
		
		GridBagHelper.setGxGyGwGh(c, 0, ++y, 2, 1);
		add(labelInfo, c);

		GridBagHelper.setGxGyGwGh(c, 0, ++y, 2, 1);
		GridBagHelper.setWxWyF(c, 1.0, 1.0, GridBagConstraints.BOTH);
		add(new JPanel(), c);

		update();

		inputX.setBorder(Borders.createBorder(BorderState.NORMAL));
		inputY.setBorder(Borders.createBorder(BorderState.NORMAL));

		inputX.getTextField().getDocument()
				.addDocumentListener(new DocumentAdapter() {

					@Override
					public void update(DocumentEvent event)
					{
						textfieldChanged(inputX);
					}
				});
		inputY.getTextField().getDocument()
				.addDocumentListener(new DocumentAdapter() {

					@Override
					public void update(DocumentEvent event)
					{
						textfieldChanged(inputY);
					}
				});

		inputX.getTextField().addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e)
			{
				lostFocus(inputX);
			}
		});
		inputY.getTextField().addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e)
			{
				lostFocus(inputY);
			}
		});
	}

	private void setNoPreferredWidth(JTextField textField)
	{
		Dimension dimension = textField.getPreferredSize();
		dimension.width = 0;
		textField.setPreferredSize(dimension);
	}

	public void update()
	{
		Coordinate c = node.getCoordinate();
		label.setText(getLabelText());
		inputX.setText("" + c.getX());
		inputY.setText("" + c.getY());
		labelInfo.setText(getLabelInfoText());
	}

	private String getLabelText()
	{
		return "Object: node";
	}
	
	private String getLabelInfoText()
	{
		List<Chain> chains = node.getChains();
		return String.format("Member of %d chains", chains.size());
	}

	protected void textfieldChanged(JPanelTextField input)
	{
		String text = input.getText();
		try {
			Double.valueOf(text);
			input.setBorder(Borders.createBorder(BorderState.NORMAL));
		} catch (NumberFormatException e) {
			input.setBorder(Borders.createBorder(BorderState.INVALID));
		}
	}

	protected void lostFocus(JPanelTextField input)
	{
		String text = input.getText();
		try {
			double value = Double.valueOf(text);
			setNodeValueFromTextfield(value, input);
		} catch (NumberFormatException e) {
			revertTextfieldToNodeValue(input);
		}
	}

	private void setNodeValueFromTextfield(double value, JPanelTextField input)
	{
		Coordinate oldCoord = node.getCoordinate();
		Coordinate newCoord = null;
		if (input == inputX) {
			newCoord = new Coordinate(value, oldCoord.getY());
		} else if (input == inputY) {
			newCoord = new Coordinate(oldCoord.getX(), value);
		}
		if (newCoord != null) {
			node.setCoordinate(newCoord);
			editPane.repaint();
		}
	}

	private void revertTextfieldToNodeValue(JPanelTextField input)
	{
		Coordinate coord = node.getCoordinate();
		if (input == inputX) {
			input.setText("" + coord.getX());
		} else if (input == inputY) {
			input.setText("" + coord.getY());
		}
	}

}
