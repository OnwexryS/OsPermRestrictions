package com.minerival.develop.ospermrestrictions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class EventPermManager {
    final main plugin;


    Set<EventPermMap> materials;

    public EventPermManager(main plugin){
        this.plugin = plugin;
        materials = new HashSet<>();
    }

    public void clearEventPermMaps(){
        this.materials = new HashSet<>();
    }


    public Set<EventPermMap> getItemsFromEvent(EventTypes event){
        Set<EventPermMap> eventItems = new HashSet<>();
        for(EventPermMap i : materials){
            if (i.hasEvent(event)){
                eventItems.add(i);
            }
        }
        return eventItems;
    }

    public EventPermMap getItem(ItemStack item){
        for(EventPermMap i : materials){
            if (i.mat.equals(item.getType())){
                return i;
            }
        }
        return new EventPermMap();
    }


    public EventPermMap getItem(Material mat){
        for(EventPermMap i : materials){
            if (i.mat.equals(mat)){
                return i;
            }
        }
        return new EventPermMap();
    }

    public Boolean hasItem(ItemStack item){
        if (getItem(item) != null){
            return true;
        }
        return false;
    }


    public Boolean isValid(EventPermMap item){
        if (item.mat != null){
            return true;
        }
        return false;
    }

    public void addItem(EventPermMap item){
        EventPermMap it = getItem(item.mat);
        if (isValid(it)){
            it.addEvents(item.events);
            materials.add(it);
        }else{
            materials.add(item);
        }
    }

    public boolean usableWithoutPerm(ItemStack item){
        if (item.hasItemMeta()){
            ItemMeta itemMeta = item.getItemMeta();
            if (!(itemMeta.hasLore())){
                return false;
            }
            List<String> lores = itemMeta.getLore();
            Iterator<String> it = lores.iterator();
            String curr;
            while(it.hasNext()){
                curr = it.next();
                if (main.bypassLoreEnabled && curr.toLowerCase().contains(main.bypassLore)){
                    return true;
                }
            }
        }
        return false;
    }
}
