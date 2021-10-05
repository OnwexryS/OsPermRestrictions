package com.minerival.develop.ospermrestrictions.EventPermManagers;

import com.minerival.develop.ospermrestrictions.main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Iterator;
import java.util.List;

public class EventPermManagerForMaterial extends EventPermManager<Material>{

    public EventPermManagerForMaterial(main plugin) {
        super(plugin);
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
                if (main.bypassLoreEnabled && ChatColor.stripColor(curr).contains(main.bypassLore)){
                    return true;
                }
            }
        }
        return false;
    }
}
