package com.minerival.develop.ospermrestrictions.EventPermManagers;

import com.minerival.develop.ospermrestrictions.EventPermMaps.EventPermMap;
import com.minerival.develop.ospermrestrictions.EventTypes;
import com.minerival.develop.ospermrestrictions.main;
import org.bukkit.event.EventHandler;

import java.util.HashSet;
import java.util.Set;

public abstract class EventPermManager<T> implements IEventPermManager<T> {
    final main plugin;

    Set<EventPermMap> materials;

    public EventPermManager(main plugin){
        this.plugin = plugin;
        materials = new HashSet<>();
    }


    @EventHandler
    public Set<EventPermMap> getAllMaterials(){
        return this.materials;
    }

    @Override
    public void clearEventPermMaps(){
        this.materials = new HashSet<>();
    }

    @Override
    public Set<EventPermMap> getItemsFromEvent(EventTypes event){
        Set<EventPermMap> eventItems = new HashSet<>();
        for(EventPermMap i : materials){
            if (i.hasEvent(event)){
                eventItems.add(i);
            }
        }
        return eventItems;
    }

    @Override
    public EventPermMap getItem(T item){
        for(EventPermMap i : materials){
            if (i.getType().equals(item)){
                return i;
            }
        }
        return null;
    }



    @Override
    public Boolean isValid(EventPermMap item){
        if (item == null){
            return false;
        }
        if (item.getType() == null){
            return false;
        }
        return true;
    }

    @Override
    public void addItem(EventPermMap item){
        EventPermMap it = getItem((T) item.getType());
        if (isValid(it)){
            it.addEvents(item.getEvents());
            materials.add(it);
        }else{
            materials.add(item);
        }
    }
    public T getType() {
        return getType();
    }

}
