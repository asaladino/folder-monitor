/*
 * PreferencesJDialog.java
 *
 * Created on Aug 7, 2011, 11:07:12 AM
 */
package Dialogs;

import Models.Options;
import Utilities.Utilities;
import Utilities.Validation;
import javax.swing.JDialog;

/**
 * @author adamsaladino
 */
public class PreferencesJDialog extends JDialog {
    
    private Options options = Options.getInstance();
    private Validation validation = new Validation();

    public PreferencesJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setRootPane(Utilities.enableEscapeHideEventForJDialogs(getDialog()));
        initComponents();
        loadOptions();
        loadValidation();
    }
    
    private JDialog getDialog() {
        return this;
    }

    private void loadValidation() {
        validation.addObjectToValidate(jTextFieldNumberRecentlyOpened, Validation.INTEGER);
    }
    
    private void loadOptions() {
        jCheckBoxMinimizeSystemTray.setSelected(options.isMinimizeToSystemTray());
        jCheckBoxStartSystemTray.setSelected(options.isStartToSystemTray());
        jCheckBoxStartWithMostRecent.setSelected(options.isStartWithPreviousWatchList());
        jTextFieldNumberRecentlyOpened.setText(options.getMaxRecentlyOpened() + "");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonClose = new javax.swing.JButton();
        jButtonSubmit = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jCheckBoxStartSystemTray = new javax.swing.JCheckBox();
        jCheckBoxMinimizeSystemTray = new javax.swing.JCheckBox();
        jCheckBoxStartWithMostRecent = new javax.swing.JCheckBox();
        jLabelNumberRecentlyOpened = new javax.swing.JLabel();
        jTextFieldNumberRecentlyOpened = new javax.swing.JTextField();

        setTitle("Preferences");
        setMaximumSize(new java.awt.Dimension(441, 270));
        setMinimumSize(new java.awt.Dimension(441, 270));
        setResizable(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jButtonClose.setText("Cancel");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });

        jButtonSubmit.setText("OK");
        jButtonSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSubmitActionPerformed(evt);
            }
        });

        jCheckBoxStartSystemTray.setText("Start in System Tray");

        jCheckBoxMinimizeSystemTray.setText("Minimize to System Tray");

        jCheckBoxStartWithMostRecent.setText("Start with Previous Watch List");

        jLabelNumberRecentlyOpened.setText("Number of Recently Opened");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jCheckBoxStartSystemTray, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                    .add(jCheckBoxStartWithMostRecent, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jCheckBoxMinimizeSystemTray, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabelNumberRecentlyOpened, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 217, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextFieldNumberRecentlyOpened, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jCheckBoxStartSystemTray, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jCheckBoxMinimizeSystemTray, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jCheckBoxStartWithMostRecent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelNumberRecentlyOpened, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jTextFieldNumberRecentlyOpened, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("General", jPanel1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jButtonClose)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jButtonSubmit)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonSubmit)
                    .add(jButtonClose))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jButtonCloseActionPerformed
private void jButtonSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSubmitActionPerformed
    if(validation.validate()) {
        options.setMinimizeToSystemTray(jCheckBoxMinimizeSystemTray.isSelected());
        options.setStartToSystemTray(jCheckBoxStartSystemTray.isSelected());
        options.setStartWithPreviousWatchList(jCheckBoxStartWithMostRecent.isSelected());
        options.setMaxRecentlyOpened(Integer.parseInt(jTextFieldNumberRecentlyOpened.getText()));
        options.save();
        setVisible(false);
    }
}//GEN-LAST:event_jButtonSubmitActionPerformed
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
    loadOptions();
}//GEN-LAST:event_formComponentShown
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonSubmit;
    private javax.swing.JCheckBox jCheckBoxMinimizeSystemTray;
    private javax.swing.JCheckBox jCheckBoxStartSystemTray;
    private javax.swing.JCheckBox jCheckBoxStartWithMostRecent;
    private javax.swing.JLabel jLabelNumberRecentlyOpened;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextFieldNumberRecentlyOpened;
    // End of variables declaration//GEN-END:variables
}
