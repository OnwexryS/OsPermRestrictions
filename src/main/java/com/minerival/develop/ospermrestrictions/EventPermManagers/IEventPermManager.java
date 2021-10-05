package com.minerival.develop.ospermrestrictions.EventPermManagers;

import com.minerival.develop.ospermrestrictions.EventPermMaps.EventPermMap;
import com.minerival.develop.ospermrestrictions.EventTypes;

import java.util.HashSet;
import java.util.Set;

public interface IEventPermManager<T> {

    public void clearEventPermMaps();
    public Set<EventPermMap> getItemsFromEvent(EventTypes event);
    public EventPermMap getItem(T item);
    public Set<EventPermMap> getAllMaterials();

    public Boolean isValid(EventPermMap item);
    public void addItem(EventPermMap item);

    //public T getType();


}
