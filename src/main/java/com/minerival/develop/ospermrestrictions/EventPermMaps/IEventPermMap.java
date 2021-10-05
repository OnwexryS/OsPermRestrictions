package com.minerival.develop.ospermrestrictions.EventPermMaps;

import com.minerival.develop.ospermrestrictions.EventTypes;

import java.util.Set;

public interface IEventPermMap <T>{

    public String getPermission();
    public String getMessage();
    public Boolean isEnabled();
    public Set<EventTypes> getEvents();
    public void addEvent(EventTypes event);
    public void addEvents(Set<EventTypes> events);
    public boolean hasEvent(EventTypes event);
    public T getType();
}
