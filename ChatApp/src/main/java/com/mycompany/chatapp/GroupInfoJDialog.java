/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.mycompany.chatapp;

import java.awt.Frame;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;

public class GroupInfoJDialog extends javax.swing.JDialog {
    private ObjectOutputStream out;

    private String groupName, CurrentUser;
    private DefaultListModel<String> FriendList;
    
    public GroupInfoJDialog(java.awt.Frame parent, boolean modal, ObjectOutputStream out, DefaultListModel<String> FriendList, String groupName, String CurrentUser) {
        super(parent, modal);
        initComponents();
        
        this.out = out;
        this.CurrentUser = CurrentUser;
        this.groupName = groupName;
        this.FriendList = FriendList;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        MemberJList = new javax.swing.JList<>();
        KickButton = new javax.swing.JButton();
        TitleLabel = new javax.swing.JLabel();
        AddButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPane1.setViewportView(MemberJList);

        KickButton.setText("Kick Member");
        KickButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KickButtonActionPerformed(evt);
            }
        });

        TitleLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        TitleLabel.setText("Group Member");

        AddButton.setText("Add Member");
        AddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(KickButton, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                            .addComponent(AddButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(TitleLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(TitleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(AddButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(KickButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void AddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddButtonActionPerformed
        // TODO add your handling code here:
        AddMemberJDialog AddMemberJDialog = new AddMemberJDialog(this, true, this.out, groupName);
        AddMemberJDialog.getFriendList().setModel(FriendList);
        AddMemberJDialog.setVisible(true);
    }//GEN-LAST:event_AddButtonActionPerformed

    private void KickButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KickButtonActionPerformed
        // TODO add your handling code here:
        if (CurrentUser.equals(MemberJList.getSelectedValue())) {
            Frame parent = (Frame) this.getParent();
            WarningJDialog WarningJDialog = new WarningJDialog(parent, true);
            WarningJDialog.getWarningLabel().setText("Cannot Kick Yourself!");
            WarningJDialog.setVisible(true);
            return;
        }
        
        String targetMember = MemberJList.getSelectedValue();
        
        try {
            out.writeUTF("KM " + this.groupName + " " + CurrentUser + " " + targetMember);
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(GroupInfoJDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_KickButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddButton;
    private javax.swing.JButton KickButton;
    private javax.swing.JList<String> MemberJList;
    private javax.swing.JLabel TitleLabel;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    public JList<String> getMemberJList() {
        return this.MemberJList;
    }
}