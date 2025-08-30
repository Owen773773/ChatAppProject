/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.chatapp;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditGroupJDialog extends javax.swing.JDialog {
    private ObjectOutputStream out;
            
    private String GroupName;
    private String CurrentUser;
    
    public EditGroupJDialog(java.awt.Frame parent, boolean modal, ObjectOutputStream out, String CurrentUser) {
        super(parent, modal);
        initComponents();
        
        this.out = out;
        this.GroupName = "";
        this.CurrentUser = CurrentUser;
        GroupNameNullWarningLabel.setVisible(false);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        GroupNameLabel = new javax.swing.JLabel();
        GroupNameField = new javax.swing.JTextField();
        OkButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();
        GroupNameNullWarningLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        GroupNameLabel.setText("Group Name");

        GroupNameField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        OkButton.setText("Ok");
        OkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkButtonActionPerformed(evt);
            }
        });

        CancelButton.setText("Cancel");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });

        GroupNameNullWarningLabel.setForeground(new java.awt.Color(255, 0, 51));
        GroupNameNullWarningLabel.setText("Group Name Should Not Be Empty!");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(GroupNameField)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(GroupNameLabel)
                        .addGap(0, 308, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(CancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(OkButton)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(99, 99, 99)
                .addComponent(GroupNameNullWarningLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(GroupNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(GroupNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(GroupNameNullWarningLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CancelButton)
                    .addComponent(OkButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_CancelButtonActionPerformed

    private void OkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkButtonActionPerformed
        GroupName = GroupNameField.getText();
        
        if (GroupName.equals("")) {
            GroupNameNullWarningLabel.setText("Fill the Group Name!");
            GroupNameNullWarningLabel.setVisible(true);
        }
        else {
            try {
                out.writeUTF("AG " + CurrentUser+ " " + GroupName);
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(EditGroupJDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            this.dispose();
        }
    }//GEN-LAST:event_OkButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CancelButton;
    private javax.swing.JTextField GroupNameField;
    private javax.swing.JLabel GroupNameLabel;
    private javax.swing.JLabel GroupNameNullWarningLabel;
    private javax.swing.JButton OkButton;
    // End of variables declaration//GEN-END:variables
}
