package com.minerival.develop.ospermrestrictions;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class EventPermMap {
    Material mat;
    String permission;
    String message;
    Boolean enabled;
    Set<EventTypes> events;
    public EventPermMap(){
        mat = null;
        permission = "not defined";
        message = "none";
        enabled = false;
        events = new HashSet<>();
    }

    public EventPermMap(Material mat, String permission, String message, Boolean enabled){
        this.mat = mat;
        this.permission = permission;
        if (message == null){
            this.message = ChatColor.RED+this.permission;
        }else{
            if (message.isEmpty()){
                this.message = ChatColor.RED+this.permission;
            }else{
                this.message = message;
            }
        }
        this.enabled = enabled;
        events = new HashSet<>();
    }

    public String getPermission(){
        return this.permission;
    }

    public String getMessage(){
        if(this.message.equals("none")){
            return getPermission();
        }
        return this.message;
    }

    public Boolean isEnabled(){
        return this.enabled;
    }

    public Set<EventTypes> getEvents(){
        return this.events;
    }

    public void addEvent(EventTypes event){
        this.events.add(event);
    }
    public void addEvents(Set<EventTypes> events){
        this.events.addAll(events);
    }
    public boolean hasEvent(EventTypes event){
        if (events.contains(event)){
            return true;
        }
        return false;
    }
}
