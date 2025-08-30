/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.chatapp;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

public class AppContact extends javax.swing.JFrame { //As Client
    // Client Server Communication
    private Socket s;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    //User Properties
    private User CurrentUser = null;
    private DefaultListModel<String> CurrentFriendRequestListToJList = new DefaultListModel<>();;
    private DefaultListModel<String> CurrentFriendListToJList;
    private DefaultListModel<String> CurrentGroupListToJList;
    private DefaultListModel<String> MemberListToJList = new DefaultListModel<>();
    
    //Display Frame
    private EditContact EditContact;
    private GroupInfoJDialog GroupInfoJDialog;
    private FriendRequestJDialog FriendRequestJDialog;
    
    public AppContact() {
        initComponents();
        
        // Accept message from server for UI
        Thread AppOpenThread = new Thread (() -> {
            try {
                s = new Socket("localhost", 12345);
                System.out.println("Server Connected");

                out = new ObjectOutputStream(s.getOutputStream());
                in = new ObjectInputStream(s.getInputStream());

                // Login OR Register JDialog
                /*
                    The JDialog was put in this thread because several users 
                    will affect the null on out that cause by unfinished thread
                */
                LoginJDialog LoginJDialog = new LoginJDialog(in, out, this, true);

                //Display LoginJDialog
                LoginJDialog.setVisible(true);
                this.setVisible(true);
                                
                CurrentUser = LoginJDialog.getCurrentUser();
                
                //Fill The GroupList
                CurrentGroupListToJList = new DefaultListModel<>();
                GroupList.setModel(CurrentGroupListToJList);

                Map<String, String> sortedGroups = new TreeMap<>(CurrentUser.getGroupsList());

                for (Map.Entry<String, String> g : sortedGroups.entrySet()) {
                    CurrentGroupListToJList.addElement(g.getKey());
                }
                
                //Fill The FriendList
                CurrentFriendListToJList = new DefaultListModel<>();
                FriendList.setModel(CurrentFriendListToJList);

                Map<String, String> sortedFriends = new TreeMap<>(CurrentUser.getFriendsList());

                for (Map.Entry<String, String> f : sortedFriends.entrySet()) {
                    CurrentFriendListToJList.addElement(f.getKey());
                }
            } catch (IOException ex) {
                Logger.getLogger(AppContact.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        AppOpenThread.start();
        
        try {
            AppOpenThread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(AppContact.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Di sini kamu bisa kirim pesan ke server
                try {
                    out.writeUTF("LO " + CurrentUser.getUsername());
                    out.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        new Thread(() -> {
            try {
                while (true) {
                    String ServerMessage = in.readUTF();
                    System.out.println(ServerMessage);
                    commandList(ServerMessage);
                }
            }
            catch (IOException ex) {
                Logger.getLogger(AppContact.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally {
                try {
                    if (in != null) in.close();
                    if (out != null) out.close();
                    if (s != null) s.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    public void commandList(String ServerMessage) {
        String[] command = ServerMessage.split(" ", 2);
        
        if (command[0].equals("AF")) {
            addFriendToList(command[1]);
        }
        else if (command[0].equals("MM")) {
            command = ServerMessage.split(" ", 3);
            MessageMeFromFriend(command[1], command[2]);
        }
        else if (command[0].equals("AG")) {
            addGroupToList(command[1]);
        }
        else if (command[0].equals("GM")) {
            MemberListSetToJDialog(command[1]);
        }
        else if (command[0].equals("NM")) {
            command = ServerMessage.split(" ", 3);
            BeNewMemberOnGroup(command[1], command[2]);
        }
        else if (command[0].equals("CM")) {
            ClearMember();
        }
        else if (command[0].equals("MG")) {
            command = ServerMessage.split(" ", 4);
            MessageMeFromGroup(command[1], command[2], command[3]);
        }
        else if (command[0].equals("KM")) {
            GetKicked(command[1]);
        }
        else if (command[0].equals("W")) {
            NotLeaderKickWarning(command[1]);
        }
        else if (command[0].equals("DM")) {
            DeleteMemberOnTheJDialogList(command[1]);
        }
        else if (command[0].equals("FR")) {
            FriendRequestMessage(command[1]);
        }
        else if (command[0].equals("FRA")) {
            FriendRequestMessageAdd(command[1]);
        }
        else if (command[0].equals("EC")) {
            EditContactSetting();
        }
        else if (command[0].equals("DF")) {
            DeleteFriendAfterBeDeleted(command[1]);
        }
    }
    
    //All User Interaction Methods
    public void DeleteFriendAfterBeDeleted(String friend) {
        DisplayMessageArea.setText("");
        CurrentFriendListToJList.removeElement(friend);
    }
    
    public void EditContactSetting() {
        EditContact.dispose();
    }
    
    public void FriendRequestMessageAdd(String friend) {
        CurrentUser.getFriendRequest().add(friend);
    }
    
    public void FriendRequestMessage(String friend) {
        CurrentFriendRequestListToJList.addElement(friend);
    }
    
    public void DeleteMemberOnTheJDialogList(String member) {
        DefaultListModel<String> MemberJList = (DefaultListModel<String>) GroupInfoJDialog.getMemberJList().getModel();
        
        MemberJList.removeElement(member);
    }
    
    public void NotLeaderKickWarning(String warningMessage) {
        WarningJDialog WarningJDialog = new WarningJDialog(this, true);
        WarningJDialog.getWarningLabel().setText(warningMessage);
        WarningJDialog.setVisible(true);
    }
    
    public void GetKicked(String groupName) {
        CurrentUser.getGroupsList().remove(groupName);
        CurrentGroupListToJList.removeElement(groupName);
        
        DisplayMessageArea.setText("");
    }
    
    public void ClearMember() {
        MemberListToJList.clear();
    }
    
    public void BeNewMemberOnGroup(String groupName, String chatHistory) {
        //Add new group into user account
        CurrentUser.getGroupsList().put(groupName, chatHistory);
                
        //add new group into jlist
        CurrentGroupListToJList.clear();

        Map<String, String> sortedGroups = new TreeMap<>(CurrentUser.getGroupsList());

        for (Map.Entry<String, String> g : sortedGroups.entrySet()) {
            CurrentGroupListToJList.addElement(g.getKey());
        }
    }
    
    public void MemberListSetToJDialog(String m) {
        MemberListToJList.addElement(m);
    }
            
    public void addFriendToList(String friendName) {
        if (friendName.equals("")) {
            EditContact.getWarningLabel().setText("User is not Found!");
            EditContact.getWarningLabel().setVisible(true);
        }
        else if (CurrentUser.getFriendsList().containsKey(friendName)) {
            EditContact.getWarningLabel().setText("User Added!");
            EditContact.getWarningLabel().setVisible(true);
        }
        else {
            CurrentUser.getFriendsList().put(friendName, "");
            CurrentFriendListToJList.clear();
            
            //Use TreeMap for ordering the name based on alphabeth
            Map<String, String> sortedFriends = new TreeMap<>(CurrentUser.getFriendsList());

            for (Map.Entry<String, String> f : sortedFriends.entrySet()) {
                CurrentFriendListToJList.addElement(f.getKey());
            }
        }
    }
    
    public void addGroupToList(String groupName) {
        CurrentUser.getGroupsList().put(groupName, "");
        CurrentGroupListToJList.clear();
        
        Map<String, String> sortedGroups = new TreeMap<>(CurrentUser.getGroupsList());

        for (Map.Entry<String, String> g : sortedGroups.entrySet()) {
            CurrentGroupListToJList.addElement(g.getKey());
        }
    }
    
    public void MessageMeFromFriend(String Sender, String Message) {
        String MessageHistory = CurrentUser.getFriendsList().get(Sender) + "\n[" + Sender + "]: "  + Message;
        
        if (FriendList.getSelectedValue() != null && FriendList.getSelectedValue().equals(Sender)) {
            DisplayMessageArea.setText(MessageHistory);
        }
        
        CurrentUser.getFriendsList().put(Sender, MessageHistory);
    }
    
    public void MessageMeFromGroup(String Sender, String targetGroup, String Message) {
        String MessageHistory = CurrentUser.getGroupsList().get(targetGroup) + "\n[" + Sender + "]: "  + Message;

        if (GroupList.getSelectedValue() != null && GroupList.getSelectedValue().equals(targetGroup)) {
            DisplayMessageArea.setText(MessageHistory);
        }
        
        CurrentUser.getGroupsList().put(targetGroup, MessageHistory);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField2 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        InputMessageField1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        InputMessageField = new javax.swing.JTextArea();
        UserNameField = new javax.swing.JTextField();
        InfoButton = new javax.swing.JButton();
        ProfileButton = new javax.swing.JButton();
        GroupLabel = new javax.swing.JLabel();
        GroupListDropButton = new javax.swing.JToggleButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        GroupList = new javax.swing.JList<>();
        FriendsLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        FriendList = new javax.swing.JList<>();
        FriendListDropButton = new javax.swing.JButton();
        SendMessageButton = new javax.swing.JButton();
        AddGroupButton = new javax.swing.JButton();
        DeleteGroupButton = new javax.swing.JButton();
        AddFriendButton = new javax.swing.JButton();
        DeleteFriendButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        DisplayMessageArea = new javax.swing.JTextArea();
        FriendRequestButton = new javax.swing.JButton();

        jTextField2.setText("jTextField2");

        jButton2.setText("jButton2");

        InputMessageField1.setColumns(20);
        InputMessageField1.setRows(5);
        InputMessageField1.setMaximumSize(new java.awt.Dimension(224, 84));
        InputMessageField1.setMinimumSize(new java.awt.Dimension(224, 84));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1920, 1080));

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setToolTipText("");
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        InputMessageField.setColumns(20);
        InputMessageField.setRows(5);
        InputMessageField.setMargin(new java.awt.Insets(5, 5, 5, 5));
        InputMessageField.setMaximumSize(new java.awt.Dimension(224, 84));
        InputMessageField.setMinimumSize(new java.awt.Dimension(224, 84));
        jScrollPane2.setViewportView(InputMessageField);

        UserNameField.setEditable(false);
        UserNameField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        UserNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UserNameFieldActionPerformed(evt);
            }
        });

        InfoButton.setText("Info");
        InfoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InfoButtonActionPerformed(evt);
            }
        });

        ProfileButton.setText("Profile");
        ProfileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ProfileButtonActionPerformed(evt);
            }
        });

        GroupLabel.setText("Group");

        GroupListDropButton.setText("v");
        GroupListDropButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GroupListDropButtonActionPerformed(evt);
            }
        });

        GroupList.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        GroupList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                GroupListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(GroupList);

        FriendsLabel.setText("Friends");

        FriendList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                FriendListValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(FriendList);

        FriendListDropButton.setText("v");
        FriendListDropButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FriendListDropButtonActionPerformed(evt);
            }
        });

        SendMessageButton.setText("Send");
        SendMessageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendMessageButtonActionPerformed(evt);
            }
        });

        AddGroupButton.setText("Add Group");
        AddGroupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddGroupButtonActionPerformed(evt);
            }
        });

        DeleteGroupButton.setText("Delete Group");
        DeleteGroupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteGroupButtonActionPerformed(evt);
            }
        });

        AddFriendButton.setText("Add Friend");
        AddFriendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddFriendButtonActionPerformed(evt);
            }
        });

        DeleteFriendButton.setText("Delete Friend");
        DeleteFriendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteFriendButtonActionPerformed(evt);
            }
        });

        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        DisplayMessageArea.setEditable(false);
        DisplayMessageArea.setColumns(20);
        DisplayMessageArea.setLineWrap(true);
        DisplayMessageArea.setRows(5);
        DisplayMessageArea.setWrapStyleWord(true);
        jScrollPane4.setViewportView(DisplayMessageArea);

        FriendRequestButton.setText("R");
        FriendRequestButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FriendRequestButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(FriendsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(FriendListDropButton))
                    .addComponent(jScrollPane3)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(AddGroupButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DeleteGroupButton))
                            .addComponent(ProfileButton))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(GroupLabel)
                                .addGap(186, 186, 186)
                                .addComponent(GroupListDropButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(FriendRequestButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(AddFriendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DeleteFriendButton)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(UserNameField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(InfoButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1553, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SendMessageButton))
                    .addComponent(jScrollPane4))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(InfoButton, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
                    .addComponent(UserNameField)
                    .addComponent(ProfileButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(GroupListDropButton)
                            .addComponent(GroupLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(AddGroupButton)
                            .addComponent(DeleteGroupButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(FriendsLabel)
                            .addComponent(FriendListDropButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(DeleteFriendButton)
                            .addComponent(FriendRequestButton)
                            .addComponent(AddFriendButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                            .addComponent(SendMessageButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void UserNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UserNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_UserNameFieldActionPerformed

    private void InfoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InfoButtonActionPerformed
        if (FriendList.getSelectedValue() != null) {
            ProfileFrame ProfileFrame = new ProfileFrame();
            ProfileFrame.setVisible(true);
            ProfileFrame.UsernameField.setText(FriendList.getSelectedValue());
        }
        else if (GroupList.getSelectedValue() != null) {
            MemberListToJList.clear();
            GroupInfoJDialog = new GroupInfoJDialog(this, true, this.out, CurrentFriendListToJList, GroupList.getSelectedValue(), CurrentUser.getUsername());
            GroupInfoJDialog.getMemberJList().setModel(MemberListToJList);

            //Get current group member            
            try {
                out.writeUTF("GM " + GroupList.getSelectedValue());
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(AppContact.class.getName()).log(Level.SEVERE, null, ex);
            }

            GroupInfoJDialog.setVisible(true);
        }
        else {
            WarningJDialog WarningJDialog = new WarningJDialog(this, true);
            WarningJDialog.setVisible(true);
        }
    }//GEN-LAST:event_InfoButtonActionPerformed

    private void ProfileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ProfileButtonActionPerformed
        // TODO add your handling code here:
        ProfileFrame ProfileFrame = new ProfileFrame();
        ProfileFrame.setVisible(true);
        ProfileFrame.UsernameField.setText(CurrentUser.getUsername());
    }//GEN-LAST:event_ProfileButtonActionPerformed

    private void GroupListDropButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GroupListDropButtonActionPerformed
        GroupList.setVisible(!GroupList.isVisible());
        
        if (GroupList.isVisible()) {
            GroupListDropButton.setText("v");
        }
        else {
            GroupListDropButton.setText(">");
        }
    }//GEN-LAST:event_GroupListDropButtonActionPerformed

    private void FriendListDropButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FriendListDropButtonActionPerformed
        FriendList.setVisible(!FriendList.isVisible());
        
        if (FriendList.isVisible()) {
            FriendListDropButton.setText("v");
        }
        else {
            FriendListDropButton.setText(">");
        }
    }//GEN-LAST:event_FriendListDropButtonActionPerformed

    private void AddFriendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddFriendButtonActionPerformed
        // TODO add your handling code here:
        EditContact = new EditContact(this.CurrentUser.getUsername(), this.out, this, false);
        EditContact.setVisible(true);
    }//GEN-LAST:event_AddFriendButtonActionPerformed

    private void DeleteFriendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteFriendButtonActionPerformed
        String friendDelete = FriendList.getSelectedValue();
        int friendDeleteIndex = FriendList.getSelectedIndex();
        
        CurrentFriendListToJList.remove(friendDeleteIndex);
        CurrentUser.getFriendsList().remove(friendDelete);
        
        DisplayMessageArea.setText("");
        
        try {
            out.writeUTF("DF " + CurrentUser.getUsername() + " " + friendDelete);
            out.flush();
        }
        catch(IOException e) {
            System.out.println("AppContact DeleteFriendButtonActionPerformed\n" + e);
        }
    }//GEN-LAST:event_DeleteFriendButtonActionPerformed

    private void SendMessageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendMessageButtonActionPerformed
        String Message = InputMessageField.getText();
        
        if (Message.equals("") || FriendList.getSelectedValue() == null && GroupList.getSelectedValue() == null) {
            return;
        }
        
        if (FriendList.getSelectedValue() != null) { //send to friend
            String friend = FriendList.getSelectedValue();
            
            try {
                out.writeUTF("MF " + CurrentUser.getUsername() + " " + friend + " " + Message);
                out.flush();
            }
            catch(IOException e) {
                System.out.println("AppContact SendMessageButtonActionPerformed\n" + e);
            }
            
            String CurrentMessageDisplay = CurrentUser.getFriendsList().get(friend) + "\n[" + CurrentUser.getUsername() + "]: "  + Message;

            //Send Message to Yourself
            DisplayMessageArea.setText(CurrentMessageDisplay);
            
            //Save new chat history
            CurrentUser.getFriendsList().put(friend, CurrentMessageDisplay);

            InputMessageField.setText("");
        }
        else { //send to group
            String group = GroupList.getSelectedValue();
            
            try {
                out.writeUTF("MG " + CurrentUser.getUsername() + " " + group + " " + Message);
                out.flush();
            }
            catch(IOException e) {
                System.out.println("AppContact SendMessageButtonActionPerformed\n" + e);
            }
            
            //No need to send it to yourself because server will do it
            //Save new chat history later after user send it (the method for saving chat group history
            // is on MessageMeFromGroup)

            InputMessageField.setText("");
        }
    }//GEN-LAST:event_SendMessageButtonActionPerformed

    private void FriendListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_FriendListValueChanged
        // TODO add your handling code here:if (!evt.getValueIsAdjusting()) {
        String selected = FriendList.getSelectedValue();
        if (selected != null) {
            UserNameField.setText(selected);
            GroupList.clearSelection();
            
            //Message display is change when selected value on JList changes
            DisplayMessageArea.setText(CurrentUser.getFriendsList().get(selected));
        }
        else {
            UserNameField.setText("");
        }
    }//GEN-LAST:event_FriendListValueChanged

    private void FriendRequestButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FriendRequestButtonActionPerformed
        //Fill the list first
        CurrentFriendRequestListToJList.clear();
        
        FriendRequestJDialog = new FriendRequestJDialog(this, true, this.out, this.CurrentUser.getUsername());
        FriendRequestJDialog.getRequestFriendJList().setModel(CurrentFriendRequestListToJList);
        
        for (String r: CurrentUser.getFriendRequest()) {
            CurrentFriendRequestListToJList.addElement(r);
        }
        
        FriendRequestJDialog.setVisible(true);
    }//GEN-LAST:event_FriendRequestButtonActionPerformed

    private void DeleteGroupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteGroupButtonActionPerformed
        try {
            out.writeUTF("DG " + GroupList.getSelectedValue() + " " + CurrentUser.getUsername());
           out.flush();
        } catch (IOException ex) {
            Logger.getLogger(AppContact.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_DeleteGroupButtonActionPerformed

    private void AddGroupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddGroupButtonActionPerformed
        // TODO add your handling code here:
        EditGroupJDialog EditGroupJDialog = new EditGroupJDialog(this, false, out, CurrentUser.getUsername());
        EditGroupJDialog.setVisible(true);        
    }//GEN-LAST:event_AddGroupButtonActionPerformed

    private void GroupListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_GroupListValueChanged
        // TODO add your handling code here:
        String selected = GroupList.getSelectedValue();
        
        if (selected != null) {
            UserNameField.setText(selected);
            FriendList.clearSelection();
            
            //Message display is change when selected value on JList changes
            DisplayMessageArea.setText(CurrentUser.getGroupsList().get(selected));
        }
        else {
            UserNameField.setText("");
        }
    }//GEN-LAST:event_GroupListValueChanged

    public static void main(String args[]) {
        AppContact AppContact = new AppContact();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddFriendButton;
    private javax.swing.JButton AddGroupButton;
    private javax.swing.JButton DeleteFriendButton;
    private javax.swing.JButton DeleteGroupButton;
    private javax.swing.JTextArea DisplayMessageArea;
    private javax.swing.JList<String> FriendList;
    private javax.swing.JButton FriendListDropButton;
    private javax.swing.JButton FriendRequestButton;
    private javax.swing.JLabel FriendsLabel;
    private javax.swing.JLabel GroupLabel;
    private javax.swing.JList<String> GroupList;
    private javax.swing.JToggleButton GroupListDropButton;
    private javax.swing.JButton InfoButton;
    private javax.swing.JTextArea InputMessageField;
    private javax.swing.JTextArea InputMessageField1;
    private javax.swing.JButton ProfileButton;
    private javax.swing.JButton SendMessageButton;
    private javax.swing.JTextField UserNameField;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
