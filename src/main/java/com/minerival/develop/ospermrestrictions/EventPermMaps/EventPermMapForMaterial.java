package com.minerival.develop.ospermrestrictions.EventPermMaps;

import org.bukkit.Material;

public class EventPermMapForMaterial extends EventPermMap<Material>{
    public EventPermMapForMaterial(Material type, String permission, String message, Boolean enabled){
        super(type, permission, message, enabled);
    }

}
