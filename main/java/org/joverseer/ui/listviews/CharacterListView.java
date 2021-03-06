package org.joverseer.ui.listviews;

import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;

import org.joverseer.domain.Character;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.listviews.filters.AllegianceFilter;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.renderers.AllegianceColorCellRenderer;
import org.joverseer.ui.support.GraphicUtils;
import org.springframework.richclient.table.ColumnToSort;
import org.springframework.richclient.table.SortableTableModel;

/**
 * ListView for Character objects
 * 
 * @author Marios Skounakis
 */
public class CharacterListView extends ItemListView {
    int iHexNo = 0;
    int iOrderResults = 16;
    
    @Override
	protected AbstractListViewFilter[][] getFilters() {
        ArrayList<AbstractListViewFilter> filters1 = new ArrayList<AbstractListViewFilter>();
        filters1.addAll(Arrays.asList(NationFilter.createNationFilters()));
        filters1.addAll(Arrays.asList(AllegianceFilter.createAllegianceFilters()));
        return new AbstractListViewFilter[][]{filters1.toArray(new AbstractListViewFilter[]{})};
    }

    public CharacterListView() {
        super(TurnElementsEnum.Character, CharacterTableModel.class);
    }

    @Override
	protected int[] columnWidths() {
        return new int[] {40, 120, 
                            32, 32, 32, 32, 
                            32, 32, 32, 32, 
                            32, 32, 32, 32, 
                            32, 96, 96, 32};
    }


    @Override
	protected ColumnToSort[] getDefaultSort() {
        return new ColumnToSort[] {new ColumnToSort(0, 2), new ColumnToSort(1, 1)};
    }

    @Override
	protected JComponent createControlImpl() {
        JComponent c = super.createControlImpl();
        this.table.setDefaultRenderer(Integer.class, new AllegianceColorCellRenderer(this.tableModel) {

            @Override
			public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3,
                    int arg4, int arg5) {
                Component c1 = super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
                JLabel lbl = (JLabel) c1;
                Integer v = (Integer) arg1;
                if (v == null || v.equals(0)) {
                    lbl.setText("");
                }
                if (arg5 == CharacterListView.this.iHexNo) {
                    // render capital with bold
                    int idx = ((SortableTableModel)CharacterListView.this.table.getModel()).convertSortedIndexToDataIndex(arg4);
                    Object obj = CharacterListView.this.tableModel.getRow(idx);
                    Character ch = (Character)obj;
                    PopulationCenter capital = (PopulationCenter)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperties(new String[]{"nationNo", "capital"}, new Object[]{ch.getNationNo(), Boolean.TRUE});
                    if (capital != null && ch.getHexNo() == capital.getHexNo()) {
                        lbl.setFont(GraphicUtils.getFont(lbl.getFont().getName(), Font.BOLD, lbl.getFont().getSize()));
                    }

                }
                
                return c1;
            }

        });
        
        this.table.setDefaultRenderer(String.class, new AllegianceColorCellRenderer(this.tableModel) {
            @Override
			public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3,
                    int arg4, int arg5) {
                Component c1 = super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
                JLabel lbl = (JLabel) c1;
                
                if (arg5 == CharacterListView.this.iOrderResults) {
                    String results = (String)arg1;
                    if (results != null) {
                        results = "<html><body>" + results.replace("\n", "<br>") + "</body></html>";
                        lbl.setToolTipText(results);
                    }
                }
                return c1;
            } 
        });
        return c;
    }
   
}
