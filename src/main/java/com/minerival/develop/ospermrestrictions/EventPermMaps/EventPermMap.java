package com.minerival.develop.ospermrestrictions.EventPermMaps;

import com.minerival.develop.ospermrestrictions.EventTypes;
import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.Set;

public abstract class EventPermMap <T> implements IEventPermMap <T> {
    private T type;
    private String permission;
    private String message;
    private Boolean enabled;
    private Set<EventTypes> events;

    public EventPermMap(){
        type = null;
        permission = "not defined";
        message = "none";
        enabled = false;
        events = new HashSet<>();
    }

    public EventPermMap(T type, String permission, String message, Boolean enabled){
        this.type = type;
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


    @Override
    public String getPermission(){
        return this.permission;
    }

    @Override
    public String getMessage(){
        if(this.message.equals("none")){
            return getPermission();
        }
        return this.message;
    }

    @Override
    public Boolean isEnabled(){
        return this.enabled;
    }

    @Override
    public Set<EventTypes> getEvents(){
        return this.events;
    }

    @Override
    public void addEvent(EventTypes event){
        this.events.add(event);
    }

    @Override
    public void addEvents(Set<EventTypes> events){
        this.events.addAll(events);
    }

    @Override
    public T getType(){
        return this.type;
    }
    @Override
    public boolean hasEvent(EventTypes event){
        if (events.contains(event)){
            return true;
        }
        return false;
    }

}
