/*
 * MonitorJTable.java
 *
 * Created on Jul 30, 2011, 12:13:54 PM
 */
package SwingBeans;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author adamsaladino
 */
public class MonitorJTable extends JTable {

    public MonitorJTable() {
        initComponents();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int colIndex) {
        return false; //Disallow the editing of any cell
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
