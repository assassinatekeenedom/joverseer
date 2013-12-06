package org.joverseer.ui.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.joverseer.game.Game;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationMapRange;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapOptions.MapOptionValuesEnum;
import org.joverseer.ui.domain.mapOptions.MapOptionsEnum;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * View for changing the map options
 * 
 * @author Marios Skounakis
 */
public class MapOptionsView extends AbstractView implements ApplicationListener {
	JComboBox cmbTurns;
	JComboBox cmbMaps;
	JComboBox zoom;
	JComboBox nationColors;
	JCheckBox drawOrders;
	JCheckBox drawNamesOnOrders;
	JCheckBox showClimate;
	JCheckBox popCenterNames;

	boolean fireEvents = true;

	@Override
	protected JComponent createControl() {
		HashMap mapOptions = (HashMap) Application.instance().getApplicationContext().getBean("mapOptions");
		mapOptions.put(MapOptionsEnum.DrawOrders, MapOptionValuesEnum.DrawOrdersOn);
		TableLayoutBuilder lb = new TableLayoutBuilder();
		JLabel label;
		lb.cell(label = new JLabel("Turn : "), "colspec=left:130px");
		label.setPreferredSize(new Dimension(100, 16));
		lb.cell(this.cmbTurns = new JComboBox(), "colspec=left:100px");
		lb.relatedGapRow();

		this.cmbTurns.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object obj = MapOptionsView.this.cmbTurns.getSelectedItem();
				if (obj == null)
					return;
				int turnNo = (Integer) obj;

				Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
				if (g.getCurrentTurn() == turnNo)
					return;
				g.setCurrentTurn(turnNo);
				if (!MapOptionsView.this.fireEvents)
					return;

				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), turnNo, this));
				if (MapPanel.instance().getSelectedHex() != null) {
					Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), MapPanel.instance().getSelectedHex(), this));
				}
			}
		});
		this.cmbTurns.setPreferredSize(new Dimension(100, 16));
		lb.row();

		// lb.append(new JLabel("  "));
		lb.cell(label = new JLabel("Map : "));
		lb.cell(this.cmbMaps = new JComboBox(), "align=left");
		lb.relatedGapRow();
		this.cmbMaps.setPreferredSize(new Dimension(100, 16));
		this.cmbMaps.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object obj = MapOptionsView.this.cmbMaps.getSelectedItem();
				if (obj == null)
					return;
				HashMap mapOptions1 = (HashMap) Application.instance().getApplicationContext().getBean("mapOptions");
				Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
				String str = obj.toString();
				if (str.equals("Current")) {
					mapOptions1.put(MapOptionsEnum.NationMap, null);
				} else if (str.equals("Dark Servants")) {
					mapOptions1.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapDarkServants);
				} else if (str.equals("Not Dark Servants")) {
					mapOptions1.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapNotDarkServants);
				} else if (str.equals("Free People")) {
					mapOptions1.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapFreePeople);
				} else if (str.equals("Not Free People")) {
					mapOptions1.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapNotFreePeople);
				} else if (str.equals("Neutrals")) {
					mapOptions1.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapNeutrals);
				} else if (str.equals("Not Neutrals")) {
					mapOptions1.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapNotNeutrals);
				} else if (str.equals("None")) {
					mapOptions1.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapNone);
				} else {
					int nationNo = g.getMetadata().getNationByName(str).getNumber();
					mapOptions1.put(MapOptionsEnum.NationMap, String.valueOf(nationNo));
				}
				int turnNo = g.getCurrentTurn();
				if (!MapOptionsView.this.fireEvents)
					return;

				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), turnNo, this));
			}

		});
		lb.row();

		// lb.append(new JLabel("  "));
		lb.cell(label = new JLabel("Draw orders : "));
		// label.setPreferredSize(new Dimension(100, 16));
		lb.cell(this.drawOrders = new JCheckBox(), "align=left");
		this.drawOrders.setSelected(mapOptions.get(MapOptionsEnum.DrawOrders) != null && mapOptions.get(MapOptionsEnum.DrawOrders) == MapOptionValuesEnum.DrawOrdersOn);
		lb.relatedGapRow();
		this.drawOrders.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				HashMap mapOptions1 = (HashMap) Application.instance().getApplicationContext().getBean("mapOptions");
				if (MapOptionsView.this.drawOrders.getModel().isSelected()) {
					mapOptions1.put(MapOptionsEnum.DrawOrders, MapOptionValuesEnum.DrawOrdersOn);
				} else {
					mapOptions1.put(MapOptionsEnum.DrawOrders, MapOptionValuesEnum.DrawOrdersOff);
				}
				Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
				if (!Game.isInitialized(g))
					return;
				int turnNo = g.getCurrentTurn();
				if (!MapOptionsView.this.fireEvents)
					return;
				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), turnNo, this));

			}

		});
		lb.row();

		lb.cell(label = new JLabel("Draw names on orders : "));
		// label.setPreferredSize(new Dimension(100, 16));
		lb.cell(this.drawNamesOnOrders = new JCheckBox(), "align=left");
		lb.relatedGapRow();
		this.drawNamesOnOrders.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				HashMap mapOptions1 = (HashMap) Application.instance().getApplicationContext().getBean("mapOptions");
				if (MapOptionsView.this.drawNamesOnOrders.getModel().isSelected()) {
					mapOptions1.put(MapOptionsEnum.DrawNamesOnOrders, MapOptionValuesEnum.DrawNamesOnOrdersOn);
				} else {
					mapOptions1.put(MapOptionsEnum.DrawNamesOnOrders, MapOptionValuesEnum.DrawNamesOnOrdersOff);
				}
				Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
				if (!Game.isInitialized(g))
					return;
				int turnNo = g.getCurrentTurn();
				if (!MapOptionsView.this.fireEvents)
					return;

				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), turnNo, this));

			}

		});
		lb.row();

		lb.cell(label = new JLabel("Show PC names : "));
		// label.setPreferredSize(new Dimension(100, 16));
		lb.cell(this.popCenterNames = new JCheckBox(), "align=left");
		lb.relatedGapRow();
		this.popCenterNames.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				HashMap mapOptions1 = (HashMap) Application.instance().getApplicationContext().getBean("mapOptions");
				if (MapOptionsView.this.popCenterNames.getModel().isSelected()) {
					mapOptions1.put(MapOptionsEnum.PopCenterNames, MapOptionValuesEnum.PopCenterNamesOn);
				} else {
					mapOptions1.put(MapOptionsEnum.PopCenterNames, MapOptionValuesEnum.PopCenterNamesOff);
				}
				Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
				if (!Game.isInitialized(g))
					return;
				int turnNo = g.getCurrentTurn();
				if (!MapOptionsView.this.fireEvents)
					return;

				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), turnNo, this));

			}

		});
		lb.row();

		// lb.append(new JLabel("  "));
		lb.cell(label = new JLabel("Show climate : "));
		// label.setPreferredSize(new Dimension(100, 16));
		lb.cell(this.showClimate = new JCheckBox(), "align=left");
		lb.relatedGapRow();
		this.showClimate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				HashMap mapOptions1 = (HashMap) Application.instance().getApplicationContext().getBean("mapOptions");
				if (MapOptionsView.this.showClimate.getModel().isSelected()) {
					mapOptions1.put(MapOptionsEnum.ShowClimate, MapOptionValuesEnum.ShowClimateOn);
				} else {
					mapOptions1.put(MapOptionsEnum.ShowClimate, MapOptionValuesEnum.ShowClimateOff);
				}
				Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
				if (!Game.isInitialized(g))
					return;
				int turnNo = g.getCurrentTurn();
				if (!MapOptionsView.this.fireEvents)
					return;

				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), turnNo, this));

			}

		});
		lb.row();
		lb.cell(label = new JLabel("Zoom level : "));
		ZoomOption[] zoomOptions = new ZoomOption[] { new ZoomOption("s1", 6, 6), new ZoomOption("s2", 7, 7), new ZoomOption("s3", 9, 9), new ZoomOption("s4", 11, 11), new ZoomOption("1", 13, 13), new ZoomOption("2", 15, 15), new ZoomOption("3", 17, 17), new ZoomOption("4", 19, 19), };
		lb.cell(this.zoom = new JComboBox(zoomOptions), "align=left");
		lb.relatedGapRow();
		this.zoom.setPreferredSize(new Dimension(100, 16));
		this.zoom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ZoomOption opt = (ZoomOption) MapOptionsView.this.zoom.getSelectedItem();

				if (opt == null)
					return;
				MapMetadata metadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
				metadata.setGridCellHeight(opt.getHeight());
				metadata.setGridCellWidth(opt.getWidth());
				if (!MapOptionsView.this.fireEvents)
					return;

				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.MapMetadataChangedEvent.toString(), this, this));
			}
		});
		this.zoom.setSelectedIndex(4);
		lb.row();
		lb.cell(label = new JLabel("Nation colors : "));
		lb.cell(this.nationColors = new JComboBox(new String[] { "Color/Nation", "Color/Allegiance" }), "align=left");
		this.nationColors.setSelectedIndex(0);
		lb.relatedGapRow();
		this.nationColors.setPreferredSize(new Dimension(100, 16));
		this.nationColors.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String opt = (String) MapOptionsView.this.nationColors.getSelectedItem();
				HashMap mapOptions1 = (HashMap) Application.instance().getApplicationContext().getBean("mapOptions");
				if (opt == null)
					return;
				if (opt.equals("Color/Nation")) {
					mapOptions1.put(MapOptionsEnum.NationColors, MapOptionValuesEnum.NationColorsNation);
				} else if (opt.equals("Color/Allegiance")) {
					mapOptions1.put(MapOptionsEnum.NationColors, MapOptionValuesEnum.NationColorsAllegiance);
				}
				;
				if (!MapOptionsView.this.fireEvents)
					return;

				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.MapMetadataChangedEvent.toString(), this, this));
			}
		});

		resetGame();
		JPanel panel = lb.getPanel();
		panel.setPreferredSize(new Dimension(130, 200));
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));

		return new JScrollPane(panel);
	}

	public void resetGame() {
		this.fireEvents = false;
		this.cmbTurns.removeAllItems();
		this.cmbMaps.removeAllItems();
		Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		if (g != null) {
			ActionListener[] als = this.cmbTurns.getActionListeners();
			for (ActionListener al : als) {
				this.cmbTurns.removeActionListener(al);
			}
			for (int i = 0; i <= g.getMaxTurn(); i++) {
				if (g.getTurn(i) != null) {
					this.cmbTurns.addItem(g.getTurn(i).getTurnNo());
				}
			}
			for (ActionListener al : als) {
				this.cmbTurns.addActionListener(al);
			}
			this.cmbTurns.setSelectedItem(g.getCurrentTurn());
			this.cmbMaps.addItem("Current");
			this.cmbMaps.addItem("Free People");
			this.cmbMaps.addItem("Dark Servants");
			this.cmbMaps.addItem("Neutrals");
			for (NationMapRange nmr : g.getMetadata().getNationMapRanges().getItems()) {
				Nation n = g.getMetadata().getNationByNum(nmr.getNationNo());
				this.cmbMaps.addItem(n.getName());
			}
			this.cmbMaps.addItem("None");
			this.cmbMaps.addItem("Not Free People");
			this.cmbMaps.addItem("Not Dark Servants");
			this.cmbMaps.addItem("Not Neutrals");
		}
		this.fireEvents = true;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
			JOverseerEvent e = (JOverseerEvent) applicationEvent;
			if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
				this.fireEvents = false;
				resetGame();
				this.fireEvents = true;
			}
			if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
				this.fireEvents = false;
				Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
				if (Game.isInitialized(g)) {
					if (!this.cmbTurns.getSelectedItem().equals(g.getCurrentTurn())) {
						this.cmbTurns.setSelectedItem(g.getCurrentTurn());
					}
				}
				this.fireEvents = true;
			}
			if (e.getEventType().equals(LifecycleEventsEnum.SetPalantirMapStyleEvent.toString())) {
				this.fireEvents = false;

				this.zoom.setSelectedIndex(2);
				this.nationColors.setSelectedIndex(0);
				this.showClimate.setSelected(false);
				PreferenceRegistry.instance().setPreferenceValue("map.terrainGraphics", "simple");
				PreferenceRegistry.instance().setPreferenceValue("map.fogOfWarStyle", "xs");
				PreferenceRegistry.instance().setPreferenceValue("map.charsAndArmies", "simplified");
				PreferenceRegistry.instance().setPreferenceValue("map.deadCharacters", "no");
				PreferenceRegistry.instance().setPreferenceValue("map.showArmyType", "no");

				this.fireEvents = true;
				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.MapMetadataChangedEvent.toString(), this, this));

			}
			if (e.getEventType().equals(LifecycleEventsEnum.ZoomIncreaseEvent.toString())) {
				if (this.zoom.getSelectedIndex() < this.zoom.getItemCount() - 1) {
					this.zoom.setSelectedIndex(this.zoom.getSelectedIndex() + 1);
				}
			}
			if (e.getEventType().equals(LifecycleEventsEnum.ZoomDecreaseEvent.toString())) {
				if (this.zoom.getSelectedIndex() > 0) {
					this.zoom.setSelectedIndex(this.zoom.getSelectedIndex() - 1);
				}
			}
		}
	}

	class ZoomOption {
		String description;
		int width;
		int height;

		public ZoomOption(String description, int width, int height) {
			super();
			this.description = description;
			this.width = width;
			this.height = height;
		}

		@Override
		public String toString() {
			return this.description;
		}

		public String getDescription() {
			return this.description;
		}

		public int getHeight() {
			return this.height;
		}

		public int getWidth() {
			return this.width;
		}

	}
}
