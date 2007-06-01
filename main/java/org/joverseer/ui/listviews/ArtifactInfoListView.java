package org.joverseer.ui.listviews;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.ui.support.transferHandlers.ParamTransferHandler;

/**
 * List view for ArtifactInfo objects
 * 
 * @author Marios Skounakis
 */
public class ArtifactInfoListView extends ItemListView {

    public ArtifactInfoListView() {
        super("artifacts", ArtifactInfoTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {32, 96, 96, 96, 96};
    }

    protected JComponent createControlImpl() {
        JComponent c = super.createControlImpl();

        //TODO does this drag and drop work? Needs refinement maybe?
        table.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent arg0) {
                ArtifactInfo a = (ArtifactInfo) tableModel.getRow(table.getSelectedRow());
                if (a == null)
                    return;
                TransferHandler handler = new ParamTransferHandler(a.getNo());
                table.setTransferHandler(handler);
                handler.exportAsDrag(table, arg0, TransferHandler.COPY);
            }
        });
        return c;
    }


}
