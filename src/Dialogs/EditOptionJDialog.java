package Dialogs;

import Models.FolderOptions;
import FolderMonitor.MonitorFrame;
import Utilities.Utilities;
import Utilities.Validation;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * @author adamsaladino
 */
public class EditOptionJDialog extends JDialog {

    private String[] options = {"Empty", "File Field", "File Location", "Folder Location"};
    private MonitorFrame monitorFrame;
    private Validation validation = new Validation();
    /**
     * Index of the folder currently being edited.
     */
    private int folderWatchIndex;
    private ArrayList<String> arguments = new ArrayList<String>();
    private int editItemIndex = -1;

    public EditOptionJDialog(MonitorFrame monitorFrame, boolean modal) {
        super(monitorFrame, modal);
        this.monitorFrame = monitorFrame;
        setRootPane(Utilities.enableEscapeHideEventForJDialogs(getDialog()));
        initComponents();
        jList1.addMouseListener(new EditArgumentJList());
        loadFieldOptions();
        loadValidation();
    }

    private JDialog getDialog() {
        return this;
    }

    private void loadValidation() {
        validation.addObjectToValidate(jTextFieldSeconds, Validation.INTEGER);
        validation.addObjectToValidate(jTextFieldCommand, Validation.NOT_EMPTY);
    }

    private void loadFieldOptions() {
        jComboBox1.setModel(new DefaultComboBoxModel(options));
    }

    public void clearOptionFields() {
        jTextFieldName.setText("");
        jTextFieldCommand.setText("");
        jTextFieldFolder.setText("");
        jTextFieldFileTypes.setText("");
        jTextFieldSeconds.setText("");
        jCheckBoxDelete.setSelected(false);
        ((DefaultListModel) jList1.getModel()).clear();
    }

    public void setOptionFields(FolderOptions folderOptions) {
        jTextFieldName.setText(folderOptions.getName());
        jTextFieldCommand.setText(folderOptions.getCommandToRun());
        jTextFieldFolder.setText(folderOptions.getFolderToWatch().getAbsolutePath());
        jTextFieldFileTypes.setText(folderOptions.getFileTypes());
        jTextFieldSeconds.setText(folderOptions.getSecondsBetweenCheck() + "");
        jCheckBoxDelete.setSelected(folderOptions.isDeleteAfterCommand());
        arguments = folderOptions.getArguments();
        loadList();
    }

    private void loadList() {
        ((DefaultListModel) jList1.getModel()).clear();
        for (int i = 0; i < arguments.size(); i++) {
            ((DefaultListModel) jList1.getModel()).addElement(arguments.get(i));
        }
    }

    private void addOptionFields() {
        monitorFrame.addFolder(
                jTextFieldName.getText(),
                jTextFieldCommand.getText(),
                jTextFieldFolder.getText(),
                jTextFieldFileTypes.getText(),
                jCheckBoxDelete.isSelected(),
                Integer.parseInt(jTextFieldSeconds.getText()),
                arguments);

    }

    private void updateOptionFields() {
        monitorFrame.updateFolder(
                getFolderWatchIndex(),
                jTextFieldName.getText(),
                jTextFieldCommand.getText(),
                jTextFieldFolder.getText(),
                jTextFieldFileTypes.getText(),
                jCheckBoxDelete.isSelected(),
                Integer.parseInt(jTextFieldSeconds.getText()),
                arguments);
    }

    /**
     * Index of the folder currently being edited.
     * @return the folderWatchIndex
     */
    public int getFolderWatchIndex() {
        return folderWatchIndex;
    }

    /**
     * Index of the folder currently being edited.
     * @param folderWatchIndex the folderWatchIndex to set
     */
    public void setFolderWatchIndex(int folderWatchIndex) {
        this.folderWatchIndex = folderWatchIndex;
    }

    public void editAddParam() {
        String s = "";
        if (editItemIndex == -1) {
            s = (String) JOptionPane.showInputDialog(this, "Parameter", "Parameter", JOptionPane.PLAIN_MESSAGE, null, null, "");
        } else {
            s = (String) JOptionPane.showInputDialog(this, "Parameter", "Parameter", JOptionPane.PLAIN_MESSAGE, null, null, arguments.get(editItemIndex));
        }
        if (s != null && !s.isEmpty()) {
            if (editItemIndex == -1) {
                arguments.add(s);
            } else {
                arguments.set(editItemIndex, s);
            }
            loadList();
        }
    }

    public void addFileFieldParam() {
        arguments.add(FolderOptions.FILENAME);
        loadList();
    }

    public void addFileLocationParam() {
        File file = Utilities.getFileSaveOnly(this, "Select File...", null);
        if (file != null) {
            arguments.add(file.getAbsolutePath());
            loadList();
        }
    }

    public void addFolderLocationParam() {
        File file = Utilities.getFileSaveDirectoryOnly(this, "Select Folder...");
        if (file != null) {
            arguments.add(file.getAbsolutePath());
            loadList();
        }
    }

    class EditArgumentJList extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                editItemIndex = jList1.locationToIndex(e.getPoint());
                editAddParam();
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonCancel = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel = new javax.swing.JPanel();
        jCheckBoxDelete = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jTextFieldFileTypes = new javax.swing.JTextField();
        jTextFieldFolder = new javax.swing.JTextField();
        jTextFieldName = new javax.swing.JTextField();
        jTextFieldSeconds = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jTextFieldCommand = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();
        jButtonRemove = new javax.swing.JButton();
        jButtonAdd = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(240, 240, 240));
        setResizable(false);

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jButtonSave.setText("OK");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });

        jTabbedPane1.setMaximumSize(new java.awt.Dimension(436, 270));

        jPanel.setMaximumSize(new java.awt.Dimension(415, 224));
        jPanel.setPreferredSize(new java.awt.Dimension(415, 224));

        jCheckBoxDelete.setText("Delete After Command Finishes");
        jCheckBoxDelete.setAlignmentY(0.0F);
        jCheckBoxDelete.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        jLabel3.setText("File Types");

        jLabel6.setText("Folder");

        jLabel1.setText("Name");

        jLabel4.setText("Inverval (sec)");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
            .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
            .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(20, 20, 20))
        );

        jTextFieldFolder.setEditable(false);
        jTextFieldFolder.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTextFieldFolderMouseReleased(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTextFieldName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                    .add(jTextFieldFileTypes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                    .add(jTextFieldFolder, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                    .add(jTextFieldSeconds, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(0, 0, 0))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jTextFieldName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextFieldFolder, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextFieldFileTypes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextFieldSeconds, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanelLayout = new org.jdesktop.layout.GroupLayout(jPanel);
        jPanel.setLayout(jPanelLayout);
        jPanelLayout.setHorizontalGroup(
            jPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelLayout.createSequentialGroup()
                .add(jPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanelLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jCheckBoxDelete, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jCheckBoxDelete, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(217, 217, 217))
        );

        jTabbedPane1.addTab("Options", jPanel);

        jLabel2.setText("Command");

        jList1.setModel(new DefaultListModel());
        jScrollPane1.setViewportView(jList1);

        jLabel5.setText("Arguments");

        jButtonRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/delete_16x16.png"))); // NOI18N
        jButtonRemove.setBorder(null);
        jButtonRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveActionPerformed(evt);
            }
        });

        jButtonAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/add_16x16.png"))); // NOI18N
        jButtonAdd.setBorder(null);
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jTextFieldCommand, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(jComboBox1, 0, 196, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButtonAdd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jButtonRemove, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextFieldCommand, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButtonAdd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButtonRemove, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Command", jPanel1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jButtonCancel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonSave)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 270, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonSave)
                    .add(jButtonCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed
    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        if(validation.validate()) {
            if (folderWatchIndex != -1) {
                updateOptionFields();
            } else {
                addOptionFields();
            }
            setVisible(false);
        }
    }//GEN-LAST:event_jButtonSaveActionPerformed
    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        if (jComboBox1.getSelectedIndex() == 1) {
            addFileFieldParam();
        } else if (jComboBox1.getSelectedIndex() == 2) {
            addFileLocationParam();
        } else if (jComboBox1.getSelectedIndex() == 3){
            addFolderLocationParam();
        } else {
            editItemIndex = -1;
            editAddParam();
        }
    }//GEN-LAST:event_jButtonAddActionPerformed
    private void jButtonRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveActionPerformed
        if (jList1.getSelectedIndex() > -1) {
            arguments.remove(jList1.getSelectedIndex());
        }
        loadList();
    }//GEN-LAST:event_jButtonRemoveActionPerformed
private void jTextFieldFolderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldFolderMouseReleased
    File file = Utilities.getFileSaveDirectoryOnly(this, "Folder To Monitor..");
    if (file != null) {
        jTextFieldFolder.setText(file.getAbsolutePath());
    }
}//GEN-LAST:event_jTextFieldFolderMouseReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonRemove;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JCheckBox jCheckBoxDelete;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextFieldCommand;
    private javax.swing.JTextField jTextFieldFileTypes;
    private javax.swing.JTextField jTextFieldFolder;
    private javax.swing.JTextField jTextFieldName;
    private javax.swing.JTextField jTextFieldSeconds;
    // End of variables declaration//GEN-END:variables
}
