/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.mycompany.chatapp;

import java.io.ObjectOutputStream;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author owent
 */
public class EditContact extends javax.swing.JDialog {
    private ObjectOutputStream out;
    private String Username;
    
    public EditContact(String Username, ObjectOutputStream out, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        this.out = out;
        this.Username = Username;
        
        WarningLabel.setVisible(false);
    }
    
    //Setter Getter
    public JButton getAddButton() {
        return AddButton;
    }

    public JTextField getUsernameField() {
        return UsernameField;
    }

    public JLabel getWarningLabel() {
        return WarningLabel;
    }

    public void setWarningLabel(JLabel WarningLabel) {
        this.WarningLabel = WarningLabel;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        UsernameField = new javax.swing.JTextField();
        AddButton = new javax.swing.JButton();
        WarningLabel = new javax.swing.JLabel();
        AddFriendLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        AddButton.setText("Add");
        AddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddButtonActionPerformed(evt);
            }
        });

        WarningLabel.setForeground(new java.awt.Color(255, 0, 0));
        WarningLabel.setText("User Not Found!");

        AddFriendLabel.setFont(new java.awt.Font("Segoe UI Black", 0, 18)); // NOI18N
        AddFriendLabel.setText("Add Friend");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(UsernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(98, 98, 98)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(AddButton))
                            .addComponent(WarningLabel)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(99, 99, 99)
                        .addComponent(AddFriendLabel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(AddFriendLabel)
                .addGap(13, 13, 13)
                .addComponent(UsernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(WarningLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(AddButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void AddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddButtonActionPerformed
        // TODO add your handling code here:
        String friend = getUsernameField().getText();
        
        try {
            if (friend.equals(Username)) {
                WarningLabel.setText("It is Your Username!");
                WarningLabel.setVisible(true);
            }
            else if (friend.equals("")) {
                WarningLabel.setText("Fill the Username!");
                WarningLabel.setVisible(true);
            }
            else {
                out.writeUTF("AF " + Username + " " + friend);
                out.flush();
            }
        }
        catch(IOException i) {
            System.out.println("AddContact AddButtonActionPerformed\n" + i);
        }
    }//GEN-LAST:event_AddButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddButton;
    private javax.swing.JLabel AddFriendLabel;
    private javax.swing.JTextField UsernameField;
    private javax.swing.JLabel WarningLabel;
    // End of variables declaration//GEN-END:variables
}
