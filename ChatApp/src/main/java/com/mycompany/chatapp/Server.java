/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    //Client-server Communication
    private Socket s;
    private ServerSocket ss;
    
    //Attribute
    private HashMap<String, User> UserData = new HashMap<>(); //User data saved the chat history of friends & groups
    private HashMap<String, Group> GroupData = new HashMap<>(); //Group data saved the group name and the group object
    
    public static void main(String[] args) {
        Server server = new Server(12345);
    } 
    
    public Server(int port) {
        try {
            //Handshaking
            ss = new ServerSocket(port);
            System.out.println("Server started");
            
            while (true) {
                s = ss.accept();
                System.out.println("Client accepted");
                
                //Every new client connected, make new in & out
                ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                
                Thread handlingThread = new ClientHandler(s, this, in, out);
                
                handlingThread.start();
            }
        }
        catch (IOException e) {
            System.out.println("Server\n" + e);
        }
    }
    
    //Command List for All Methods at Below
    public void commandList(String cm, ClientHandler handler) {
        //The Limit is 4 for User Chatting Communication
        String[] command = cm.split(" ", 4);
        
        if (command[0].equals("RE")) {
            UserRegistration(command[1], command[2]);
        }
        else if (command[0].equals("LI")) {
            UserLogin(command[1], command[2], handler);
        }
        else if (command[0].equals("AF")) {
            AddFriend(command[1], command[2], handler);
        }
        else if (command[0].equals("DF")) {
            DeleteFriend(command[1], command[2]);
        }
        else if (command[0].equals("MF")) {
            MessageFriend(command[1], command[2], command[3]);
        }
        else if (command[0].equals("AG")) {
            AddGroup(command[1], command[2], handler);
        }
        else if (command[0].equals("GM")) {
            GroupMember(command[1], handler);
        }
        else if (command[0].equals("AM")) {
            AddMemberToTheGroup(command[1], command[2], handler);
        }
        else if (command[0].equals("MG")) {
            MessageGroup(command[1], command[2], command[3]);
        }
        else if (command[0].equals("KM")) {
            KickMember(command[1], command[2], command[3], handler);
        }
        else if (command[0].equals("DG")) {
            DeleteGroup(command[1], command[2], handler);
        }
        else if (command[0].equals("LO")) {
            LogOutUser(command[1]);
        }
    }
    
    //All Methods for Forming the Application
    public void LogOutUser(String user) {
        UserData.get(user).setOnline(false);
    }
    
    public void DeleteGroup(String group, String user, ClientHandler handler) {
        String groupLeader = GroupData.get(group).getLeader();
        
        try {
            if (groupLeader.equals(user)) {
                //delete the leader first on data structure
                UserData.get(user).getGroupsList().remove(group);
                
                //delete group from leader
                handler.getOut().writeUTF("KM " + group);
                handler.getOut().flush();
                
                TreeSet<String> groupMembers = GroupData.get(group).getMembers();
                
                //Delete group to all member using kick method
                for (String m: groupMembers) {
                    UserData.get(m).getGroupsList().remove(group);
                    
                    if (UserData.get(m).getOnline()) {
                        UserData.get(m).getOut().writeUTF("KM " + group);
                        UserData.get(m).getOut().flush();
                    }
                }
                
                GroupData.remove(group);
            }
            else {
                handler.getOut().writeUTF("W You Are Not Group Leader!");
                handler.getOut().flush();
            }
        }
        catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void KickMember(String groupName, String Kicker, String member, ClientHandler handler) {
        Group targetGroup = GroupData.get(groupName);
        User targetMember = UserData.get(member);
        
        if (!targetGroup.getLeader().equals(Kicker)) {
            try {
                handler.getOut().writeUTF("W You Are Not Group Leader!");
                handler.getOut().flush();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        
        if (targetGroup != null) {
            targetGroup.getMembers().remove(member);
        }
        
        if (targetMember != null) {
            targetMember.getGroupsList().remove(groupName);
        }
        
        try {
            if (targetMember.getOnline()) {
                targetMember.getOut().writeUTF("KM " + groupName);
                targetMember.getOut().flush();
                
                handler.getOut().writeUTF("DM " + member);
                handler.getOut().flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void AddMemberToTheGroup(String groupName, String newMember, ClientHandler handler) {
        //UserData modification
        String groupLeader = GroupData.get(groupName).getLeader();
        String chatHistory = UserData.get(groupLeader).getGroupsList().get(groupName);
        
        if (chatHistory == null) {
            chatHistory = "";
        }
        
        UserData.get(newMember).getGroupsList().put(groupName, chatHistory);

        //GroupData modification
        GroupData.get(groupName).getMembers().add(newMember);

        //Communication with client        
        try {
            if (UserData.get(newMember).getOnline()) { //If user online
                UserData.get(newMember).getOut().writeUTF("NM " + groupName + " " + chatHistory);
                UserData.get(newMember).getOut().flush();
            }
            
            GroupMember(groupName, handler);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
    public void UserRegistration(String Username, String Password) {
        UserData.put(Username, new User(Username, Password));
    }
    
    public void UserLogin(String Username, String Password, ClientHandler handler) {
        User CurrentUser = UserData.get(Username);
        
        try {
            //If User isn't on the list or the password isn't same
            if (CurrentUser == null || !CurrentUser.getPassword().equals(Password)) {
                handler.getOut().writeObject(null);
            }
            //If Login Success
            else {
                CurrentUser.setOnline(true);
                CurrentUser.setOut(handler.getOut());
                handler.getOut().writeObject(CurrentUser);
                handler.getOut().flush();
            }
        }
        catch(IOException e) {
            System.out.println("Server UserLogin\n" + e);
        }
    }
    
    public void AddFriend(String CurrentUser, String friendUsername, ClientHandler handler) {
        User user = UserData.get(CurrentUser);
        User friend = UserData.get(friendUsername);
                
        try {
            if (friend != null) {
                if (user.getFriendRequest().contains(friendUsername)) { //If request is appear in list
                    //Remove request process
                    user.getFriendRequest().remove(friendUsername);
                    
                    //Add process
                    user.getFriendsList().put(friendUsername, "");
                    friend.getFriendsList().put(CurrentUser, "");                    
                    
                    handler.getOut().writeUTF("AF " + friendUsername);
                    handler.getOut().flush();
                    
                    if (friend.getOnline()) {
                        friend.getOut().writeUTF("AF " + CurrentUser);
                        friend.getOut().flush();
                    }
                }
                else {
                    //request process
                    handler.getOut().writeUTF("EC");
                    handler.getOut().flush();
                    friend.getFriendRequest().add(CurrentUser);
                    
                    if (friend.getOnline()) { //If friend online
                        friend.getOut().writeUTF("FRA " + CurrentUser);
                        friend.getOut().flush();
                            
                        for (String r: friend.getFriendRequest()) { //Give the request list
                            friend.getOut().writeUTF("FR " + r);
                            friend.getOut().flush();
                        }
                    }
                }
            }
            else {
                handler.getOut().writeUTF("AF ");
                handler.getOut().flush();
            }
        }
        catch (IOException e) {
            System.out.println("Server AddFriend\n" + e);
        }
    }
    
    public void DeleteFriend(String CurrentUser, String friendUsername) {
        User friend = UserData.get(friendUsername);
        UserData.get(CurrentUser).getFriendsList().remove(friendUsername);
        friend.getFriendsList().remove(CurrentUser);
        
        try {
            if (friend.getOnline()) {
                friend.getOut().writeUTF("DF " + CurrentUser);
                friend.getOut().flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void MessageFriend (String Sender, String Receiver, String Message) {
        User target = UserData.get(Receiver);
        
        try {
            if (target != null) {
                if (target.getOnline()) {
                    //Send to Target
                    target.getOut().writeUTF("MM " + Sender + " " + Message);
                    target.getOut().flush();
                }
                
                //Set chat history in User friends HashMap
                User CurrentSender = UserData.get(Sender);
                User CurrentReceiver = UserData.get(Receiver);

                String MessageHistory = CurrentSender.getFriendsList().get(Receiver) + "\n[" + CurrentSender.getUsername() + "]: " + Message;
                UserData.get(Sender).getFriendsList().put(Receiver, MessageHistory);

                MessageHistory = CurrentReceiver.getFriendsList().get(Sender) + "\n[" + CurrentSender.getUsername() + "]: " + Message;
                UserData.get(Receiver).getFriendsList().put(Sender, MessageHistory);
            }
        }
        catch(IOException e) {
            System.out.println("Server MessageFriend\n" + e);
        }
    }
    
    public void MessageGroup(String sender, String targetGroup, String message) {
        Group group = GroupData.get(targetGroup);
        TreeSet<String> targetMember = group.getMembers();
        
        try {
            for (String m: targetMember) {
                if (UserData.get(m).getOnline()) {
                    UserData.get(m).getOut().writeUTF("MG " + sender + " " + targetGroup + " " + message);
                    UserData.get(m).getOut().flush();
                }
                
                //Set chat history in User groups HashMap
                User targetUser = UserData.get(m);
                String MessageHistory = targetUser.getGroupsList().get(targetGroup) + "\n[" + sender + "]: " + message;
                
                UserData.get(targetUser.getUsername()).getGroupsList().put(targetGroup, MessageHistory);
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void AddGroup(String user, String groupName, ClientHandler handler) {
        UserData.get(user).getGroupsList().put(groupName, "");
        GroupData.put(groupName, new Group(groupName, user));
        
        try {
            handler.getOut().writeUTF("AG " + groupName);
            handler.getOut().flush();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void GroupMember(String groupName, ClientHandler handler) {
        Group currentGroup = GroupData.get(groupName);
        
        try {
            //Clear member list
            handler.getOut().writeUTF("CM");
            
            //Give newer members list
            for (String m: currentGroup.getMembers()) {
                handler.getOut().writeUTF("GM " + m);
                handler.getOut().flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}