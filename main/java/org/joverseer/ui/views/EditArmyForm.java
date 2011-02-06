package org.joverseer.ui.views;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.Character;
import org.joverseer.domain.NationRelations;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.controls.ResourceLabel;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.TableUtils;

/**
 * Forms that shows/edits an army
 * 
 * @author Marios Skounakis
 */

// TODO needs validation
public class EditArmyForm extends AbstractForm {
	class ArmyElementTableModel extends BeanTableModel {

		private static final long serialVersionUID = 1L;

		public ArmyElementTableModel(MessageSource ms) {
			super(ArmyElement.class, ms);
			setRowNumbers(false);
		}

		@Override
		protected Class[] createColumnClasses() {
			return new Class[] { String.class, Integer.class, Integer.class, Integer.class, Integer.class };
		}

		@Override
		protected String[] createColumnPropertyNames() {
			return new String[] { "armyElementType.type", "number", "training", "weapons", "armor" };
		}

	}

	public static String FORM_ID = "editArmyForm";
	JTextField commander;
	JTextField commandRank;
	JTextField morale;
	JTextField food;
	JTextField hexNo;
	JComboBox nation;
	JTable elements;
	BeanTableModel elementTableModel;

	ArrayList<ArmyElement> elementList;

	public EditArmyForm(FormModel arg0) {
		super(arg0, FORM_ID);
	}

	@Override
	public void commit() {
		super.commit();
		Army a = (Army) getFormObject();

		a.setCommanderName(commander.getText());
		try {
			a.setHexNo(String.valueOf(Integer.parseInt(hexNo.getText())));
		} catch (Exception exc) {
		}
		;

		try {
			a.setMorale(Integer.parseInt(morale.getText()));
		} catch (Exception exc) {
		}
		;
		try {
			a.setFood(Integer.parseInt(food.getText()));
		} catch (Exception exc) {
		}
		;
		try {
			Nation n = GameHolder.instance().getGame().getMetadata().getNationByName(nation.getSelectedItem().toString());
			a.setNationNo(n.getNumber());
			a.setNationAllegiance(n.getAllegiance());
		} catch (Exception exc) {
		}
		;
		a.getElements().clear();
		for (ArmyElement ae : elementList) {
			if (ae.getNumber() > 0) {
				a.getElements().add(ae);
			}
		}
	}

	@Override
	protected JComponent createFormControl() {
		GridBagLayoutBuilder lb = new GridBagLayoutBuilder();

		lb.append(new ResourceLabel("editArmyForm.Commander"));
		lb.append(commander = new JTextField());
		commander.setPreferredSize(new Dimension(120, 20));

		lb.nextLine();

		lb.append(new ResourceLabel("standardFields.Nation"));
		lb.append(nation = new JComboBox(getNationNames().toArray()));
		nation.setPreferredSize(new Dimension(120, 20));

		lb.nextLine();

		lb.append(new ResourceLabel("standardFields.HexNo"));
		lb.append(hexNo = new JTextField());
		nation.setPreferredSize(new Dimension(120, 20));

		lb.nextLine();

		lb.append(new ResourceLabel("editArmyForm.CommandRank"));
		lb.append(commandRank = new JTextField());
		commandRank.setPreferredSize(new Dimension(60, 20));

		lb.nextLine();

		lb.append(new ResourceLabel("editArmyForm.Morale"));
		lb.append(morale = new JTextField());
		morale.setPreferredSize(new Dimension(60, 20));

		lb.nextLine();

		lb.append(new ResourceLabel("editArmyForm.Food"));
		lb.append(food = new JTextField());
		food.setPreferredSize(new Dimension(60, 20));

		lb.nextLine();

		lb.append(new ResourceLabel("editArmyForm.Elements"));
		lb.nextLine();
		JComponent panel = lb.getPanel();

		TableLayoutBuilder tlb = new TableLayoutBuilder();
		JPanel pnl = new JPanel();
		pnl.add(panel);
		tlb.cell(pnl, "align=left");
		tlb.relatedGapRow();

		elements = new JTable(elementTableModel = new ArmyElementTableModel((MessageSource) Application.instance().getApplicationContext().getBean("messageSource")) {
			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				if (arg1 == 0)
					return false;
				return super.isCellEditable(arg0, arg1);
			}

			@Override
			public void setValueAt(Object arg0, int arg1, int arg2) {
				try {
					Integer.parseInt(arg0.toString());
				} catch (Exception exc) {
					arg0 = 0;
				}
				super.setValueAt(arg0, arg1, arg2);
			}
		});
		elements.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				if ((Integer) value == 0) {
					value = null;
				}
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				lbl.setHorizontalAlignment(JLabel.RIGHT);
				return lbl;
			}
		});
		JScrollPane scp = new JScrollPane(elements);
		scp.setPreferredSize(new Dimension(300, 180));
		tlb.cell(scp);

		return new JScrollPane(tlb.getPanel());
	}

	private ArrayList<String> getNationNames() {
		Game g = GameHolder.instance().getGame();
		ArrayList<NationRelations> nrs = g.getTurn().getContainer(TurnElementsEnum.NationRelation).getItems();
		ArrayList<String> ret = new ArrayList<String>();
		ret.add(g.getMetadata().getNationByNum(0).getName());
		for (NationRelations nr : nrs) {
			ret.add(g.getMetadata().getNationByNum(nr.getNationNo()).getName());
		}
		return ret;
	}

	@Override
	public void setFormObject(Object arg0) {
		super.setFormObject(arg0);
		if (arg0 == null)
			return;
		Army a = (Army) arg0;

		commander.setText(a.getCommanderName());

		nation.setSelectedItem(GameHolder.instance().getGame().getMetadata().getNationByNum(a.getNationNo()).getName());

		Character c = (Character) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("name", a.getCommanderName());
		if (c != null) {
			commandRank.setText(String.valueOf(c.getCommandTotal()));
		}
		commandRank.setEditable(false);
		morale.setText(String.valueOf(a.getMorale()));
		food.setText(String.valueOf(a.getFood()));
		hexNo.setText(a.getHexNo());
		elementList = new ArrayList<ArmyElement>();
		for (ArmyElementType aet : ArmyElementType.values()) {
			ArmyElement nae = new ArmyElement(aet, 0);
			ArmyElement ae = a.getElement(aet);
			if (ae != null) {
				nae.setNumber(ae.getNumber());
				nae.setTraining(ae.getTraining());
				nae.setWeapons(ae.getWeapons());
				nae.setArmor(ae.getArmor());
			}
			elementList.add(nae);
		}
		elementTableModel.setRows(elementList);
		TableUtils.sizeColumnsToFitRowData(elements);
	}

}
