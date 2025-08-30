/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapp;

import java.util.TreeSet;

public class Group {
    private String leader, groupName;
    private TreeSet<String> Members;
    
    public Group(String groupName, String leader) {
        this.groupName = groupName;
        this.leader = leader;
        this.Members = new TreeSet<>();
        
        this.Members.add(leader);
    }
    
    public String getLeader() {
        return this.leader;
    }
    
    public String getGroupName() {
        return this.leader;
    }

    public TreeSet<String> getMembers() {
        return Members;
    }
}
