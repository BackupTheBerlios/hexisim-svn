package hexapodsimulator;

import java.util.Date;

/**
 * Dialog for the properties of an interval in the timebar
 * @author peter
 */
public class SequenceTimebarDialog extends javax.swing.JDialog {

    private Date date;
    private boolean deleted;
    private boolean cancelled;

    /** Creates new form SequenceTimebarDialog */
    public SequenceTimebarDialog(java.awt.Frame parent, boolean showDate) {
        super(parent, true);
        initComponents();
        getRootPane().setDefaultButton(setTimeButton);
        if(!showDate) {
            minTextField.setEnabled(false);
            secTextField.setEnabled(false);
            msecTextField.setEnabled(false);
            setTimeButton.setEnabled(false);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        minTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        secTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        msecTextField = new javax.swing.JTextField();
        setTimeButton = new javax.swing.JButton();
        deleteSequenceButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Change the options of the selected sequence:");

        jLabel2.setText("Start time:");

        jLabel3.setText("min");

        jLabel4.setText("sec");

        jLabel5.setText("msec");

        setTimeButton.setText("Set time");
        setTimeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setTimeButtonActionPerformed(evt);
            }
        });

        deleteSequenceButton.setText("Delete Sequence");
        deleteSequenceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSequenceButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(minTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(1, 1, 1)
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(secTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(1, 1, 1)
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(msecTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(1, 1, 1)
                        .add(jLabel5))
                    .add(layout.createSequentialGroup()
                        .add(setTimeButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(deleteSequenceButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(cancelButton)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(minTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)
                    .add(secTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4)
                    .add(msecTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(setTimeButton)
                    .add(deleteSequenceButton)
                    .add(cancelButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void setTimeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setTimeButtonActionPerformed
        date = new Date(Integer.parseInt(msecTextField.getText())
                + Integer.parseInt(secTextField.getText()) * 1000
                + Integer.parseInt(minTextField.getText()) * 60000);
        dispose();
    }//GEN-LAST:event_setTimeButtonActionPerformed

    private void deleteSequenceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSequenceButtonActionPerformed
        deleted = true;
        dispose();
    }//GEN-LAST:event_deleteSequenceButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        cancelled = true;
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    public void setDate(Date date) {
        this.date = date;
        long milliSeconds = this.date.getTime();
        msecTextField.setText(Long.toString(milliSeconds%1000));
        secTextField.setText(Long.toString(milliSeconds/1000%60));
        minTextField.setText(Long.toString(milliSeconds/60000%60));
    }

    public Date getDate() {
        return date;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public boolean getCancelled() {
        return cancelled;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton deleteSequenceButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField minTextField;
    private javax.swing.JTextField msecTextField;
    private javax.swing.JTextField secTextField;
    private javax.swing.JButton setTimeButton;
    // End of variables declaration//GEN-END:variables

}
