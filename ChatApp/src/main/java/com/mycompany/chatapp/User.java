/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapp;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.TreeSet;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String Username, Password;
    private boolean online;
    private TreeSet<String> friendRequest; //friend name request
    private HashMap<String, String> friends; //friend name & chat history
    private HashMap<String, String> groups; //group name & chat history
    private transient ObjectOutputStream out;
    
    public User(String Username, String Password) {
        this.online = false;
        this.Username = Username;
        this.Password = Password;
        this.friendRequest = new TreeSet<>();
        this.friends = new HashMap<>();
        this.groups = new HashMap<>();
    }

    //Getter Setter
    public boolean getOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
    
    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }
    
    public HashMap<String, String> getFriendsList() {
        return this.friends;
    }

    public TreeSet<String> getFriendRequest() {
        return friendRequest;
    }
    
    public HashMap<String, String> getGroupsList() {
        return this.groups;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }
}
