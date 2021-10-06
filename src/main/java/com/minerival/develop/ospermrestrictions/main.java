package com.minerival.develop.ospermrestrictions;

import com.minerival.develop.ospermrestrictions.EventPermManagers.EventPermManagerForMaterial;
import com.minerival.develop.ospermrestrictions.EventPermManagers.EventPermManagerForString;
import com.minerival.develop.ospermrestrictions.EventPermMaps.EventPermMap;
import com.minerival.develop.ospermrestrictions.EventPermMaps.EventPermMapForMaterial;
import com.minerival.develop.ospermrestrictions.EventPermMaps.EventPermMapForString;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import java.util.Set;


public final class main extends JavaPlugin implements Listener {
    Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 127, 255), 3);
    Particle.DustOptions dustOptions2 = new Particle.DustOptions(Color.fromRGB(0, 155, 197), 3);
    EventPermManagerForMaterial eventPermManagerForMaterial;
    EventPermManagerForString eventPermManagerForString;
    public static String bypassLore = "";
    public static boolean bypassLoreEnabled = true;
    public String prefix = "&1[&bO's &9PermRestrictions&1] ";
    public String noPermisisonMessage = "&fYou dont have permission.";
    public String reloadingMessage = "&e&lReloading..";
    public String reloadedMessage = "&a&lReloaded !";



    @Override
    public void onEnable() {
        eventPermManagerForMaterial = new EventPermManagerForMaterial(this);
        eventPermManagerForString = new EventPermManagerForString(this);
        getConfig().options().copyDefaults();
        loadConfig();
        getServer().getPluginManager().registerEvents(new ArmorListener(), this);
        try{
            //Better way to check for this? Only in 1.13.1+?
            Class.forName("org.bukkit.event.block.BlockDispenseArmorEvent");
            getServer().getPluginManager().registerEvents(new DispenserArmorListener(), this);
        }catch(Exception ignored){}
        getServer().getPluginManager().registerEvents(this, this);


    }

    public void loadConfig(){
        saveDefaultConfig();
        reloadConfig();
        eventPermManagerForMaterial.clearEventPermMaps();
        this.bypassLore = getConfig().getString("bypassLore");
        this.bypassLoreEnabled = getConfig().getBoolean("bypassLoreEnabled");

        getServer().getConsoleSender().sendMessage(colorize(prefix + "&9loaded successfully"));


        readEventPermMaps(EventTypes.values());
    }

    public void readEventPermMaps(EventTypes ... eventTypes){
        for (EventTypes type : eventTypes){
            readEventPermMaps(type);
        }
    }

    public void readEventPermMaps(EventTypes eventType){
        String event = eventType.name();
        Set<String> elements = getConfig().getConfigurationSection(event).getKeys(false);

        for (String element : elements){
            Boolean isEnabled = getConfig().getBoolean(event + "." + element + "." + "enabled");
            String permission = getConfig().getString(event + "." + element + "." + "permission");
            String message = getConfig().getString(event + "." + element + "." + "message");
            if (isEnabled){
                Boolean isString = false;
                switch(eventType){
                    case teleport:
                    case pvp:
                        isString = true;
                        break;
                }
                if (isString){
                    EventPermMapForString tempEPM = new EventPermMapForString(element, permission, message, isEnabled);
                    tempEPM.addEvent(EventTypes.valueOf(event));
                    eventPermManagerForString.addItem(tempEPM);
                }else{
                    EventPermMapForMaterial tempEPM = new EventPermMapForMaterial(Material.valueOf(element), permission, message, isEnabled);
                    tempEPM.addEvent(EventTypes.valueOf(event));
                    eventPermManagerForMaterial.addItem(tempEPM);
                }

            }

        }


    }

    public String colorize(String msg){
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent e){
        if (e.getNewArmorPiece() == null){
            return;
        }
        if (e.getNewArmorPiece().getType() == Material.AIR){
            return;
        }
        if (e.getPlayer().isOp()){
            return;
        }
        if (eventPermManagerForMaterial.usableWithoutPerm(e.getNewArmorPiece())){
            return;
        }

        Set<EventPermMap> oprMaps = eventPermManagerForMaterial.getItemsFromEvent(EventTypes.equip);
        Player p = e.getPlayer();

        for(EventPermMap i : oprMaps){
            if (i.getType().equals(e.getNewArmorPiece().getType())){
                if(!i.isEnabled()){
                    break;
                }
                if(!(p.hasPermission(i.getPermission()))){
                    p.sendMessage(colorize(i.getMessage()));
                    e.setCancelled(true);
                    denyVisualizer(e.getPlayer());
                }
            }
        }


        return;
    }



    @EventHandler
    public void onPortal(PlayerTeleportEvent e){
        if (e.getPlayer().isOp()){
            return;
        }

        Set<EventPermMap> oprMaps = eventPermManagerForMaterial.getItemsFromEvent(EventTypes.portal);
        Player p = e.getPlayer();
        for(EventPermMap i : oprMaps){
            if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.valueOf(i.getType().toString()))){
                if(p.hasPermission(i.getPermission())){
                    return;
                }else{
                    e.setCancelled(true);
                    p.sendMessage(colorize(i.getMessage()));
                    if (i.getType().equals(Material.END_PORTAL)){
                        pushUpPlayer(p, e.getFrom());
                    }
                }
            }
        }
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        if (!(p.getGameMode().equals(GameMode.SURVIVAL))){
            return;
        }
        if (p.isOp()){
            return;
        }
        if (eventPermManagerForMaterial.usableWithoutPerm(p.getItemInHand())){
            return;
        }

        Set<EventPermMap> oprMaps = eventPermManagerForMaterial.getItemsFromEvent(EventTypes.breakwith);
        for(EventPermMap i : oprMaps){
            if (i.getType().equals(p.getItemInHand().getType())){
                if (p.hasPermission(i.getPermission())){
                    return;
                }
                e.setCancelled(true);
                denyVisualizer(p);
                p.sendMessage(colorize(i.getMessage()));

            }
        }
    }


    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e){
        Player p = e.getPlayer();
        Location to = p.getLocation();
        if (!(p.getGameMode().equals(GameMode.SURVIVAL))){
            return;
        }
        if (p.isOp()){
            return;
        }

        Set<EventPermMap> oprMaps = eventPermManagerForString.getItemsFromEvent(EventTypes.teleport);
        for (EventPermMap i : oprMaps){
            if (i.getType().equals(to.getWorld().getName())){
                if (p.hasPermission(i.getPermission())){
                    return;
                }
                p.teleport(e.getFrom().getSpawnLocation());
                p.sendMessage(colorize(i.getMessage()));
            }
        }
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent e){
        if (!(e.getDamager().getType().equals(EntityType.PLAYER))){
            return;
        }
        if (!(e.getEntity().getType().equals(EntityType.PLAYER))){
            return;
        }

        Player victim = (Player)e.getEntity();
        Player attacker = (Player)e.getDamager();
        if (!(attacker.getGameMode().equals(GameMode.SURVIVAL))){
            return;
        }
        if (!(victim.getGameMode().equals(GameMode.SURVIVAL))){
            return;
        }
        if (attacker.isOp()){
            return;
        }

        Set<EventPermMap> oprMaps = eventPermManagerForString.getItemsFromEvent(EventTypes.pvp);
        for (EventPermMap i : oprMaps){
            if (i.getType().equals("allowPvp")){
                if (attacker.hasPermission(i.getPermission())){
                    return;
                }
                e.setCancelled(true);
                denyVisualizer(attacker);
                attacker.sendMessage(colorize(i.getMessage()));
            }else if (i.getType().equals("protectFromPvp")){
                if (victim.hasPermission(i.getPermission())){
                    e.setCancelled(true);
                    denyVisualizer(attacker);
                    attacker.sendMessage(colorize(i.getMessage()));
                }
                return;
            }
        }
    }


    @EventHandler
    public void onHit(EntityDamageByEntityEvent e){
        if (!(e.getDamager() instanceof Player)){
            return;
        }
        Player p = ((Player) e.getDamager()).getPlayer();
        if (p.isOp()){
            return;
        }
        if (p.getItemInHand() == null){
            return;
        }
        ItemStack item = p.getItemInHand();
        if (eventPermManagerForMaterial.usableWithoutPerm(item)){
            return;
        }


        Set<EventPermMap> oprMaps = eventPermManagerForMaterial.getItemsFromEvent(EventTypes.hit);
        for(EventPermMap i : oprMaps){
            if (i.getType().equals(item.getType())){
                if (p.hasPermission(i.getPermission())){
                    return;
                }
                e.setCancelled(true);
                denyVisualizer(p);
                p.sendMessage(colorize(i.getMessage()));
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        Set<EventPermMap> oprMaps = eventPermManagerForMaterial.getItemsFromEvent(EventTypes.use);

        Boolean isEnderPearl = false;
        if (e.getItem() != null) {
            if (eventPermManagerForMaterial.usableWithoutPerm(e.getItem())){
                return;
            }
            if (e.getItem().getType().equals(Material.ENDER_PEARL)) {
                isEnderPearl = true;
            }
        }
        Material block = null;
        Boolean isInteractedBlock = false;
        if (e.getClickedBlock() != null){
            block = e.getClickedBlock().getType();
            isInteractedBlock = true;
        }
        if (e.getAction() == Action.LEFT_CLICK_BLOCK){
            return;
        }


        for (EventPermMap i : oprMaps){
            if (i.getType().equals(Material.ENDER_PEARL) && isEnderPearl){
                if (p.hasPermission(i.getPermission())){
                    return;
                }else{
                    e.setCancelled(true);
                    denyVisualizer(p);
                    e.getPlayer().sendMessage(colorize(i.getMessage()));
                }
            }
            if (isInteractedBlock){
                if (i.getType().equals(block)){
                    if (p.hasPermission(i.getPermission())){
                        return;
                    }
                    e.setCancelled(true);
                    denyVisualizer(p);
                    p.sendMessage(colorize(i.getMessage()));
                }
            }
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e){

        if (e.getPlayer().isOp()){
            return;
        }
        if (eventPermManagerForMaterial.usableWithoutPerm(e.getItem())){
            return;
        }

        Set<EventPermMap> oprMaps = eventPermManagerForMaterial.getItemsFromEvent(EventTypes.consume);
        for(EventPermMap i : oprMaps){
            if (i.getType().equals(e.getItem().getType())){
                if (e.getPlayer().hasPermission(i.getPermission())){
                    return;
                }
                e.setCancelled(true);
                denyVisualizer(e.getPlayer());
                e.getPlayer().sendMessage(colorize(i.getMessage()));
            }
        }

    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if (!(p.getGameMode().equals(GameMode.SURVIVAL))){
            return;
        }
        if (p.isOp()){
            return;
        }
        if (p.getItemInHand() == null){
            return;
        }
        if (eventPermManagerForMaterial.usableWithoutPerm(e.getPlayer().getItemInHand())){
            return;
        }
        Set<EventPermMap> oprMaps = eventPermManagerForMaterial.getItemsFromEvent(EventTypes.breakwith);
        for(EventPermMap i : oprMaps){
            if (i.getType().equals(p.getItemInHand().getType())){
                if (p.hasPermission(i.getPermission())){
                    return;
                }
                e.setCancelled(true);
                denyVisualizer(p);
                p.sendMessage(colorize(i.getMessage()));

            }
        }
    }

    @EventHandler
    public void onResurrect(EntityResurrectEvent e){
        if (!(e.getEntity() instanceof Player)){
            return;
        }
        Player p = ((Player) e.getEntity()).getPlayer();
        if (p.isOp()){
            return;
        }
        /*

        if (eventPermManager.usableWithoutPerm(e.getNewArmorPiece())){
            return;
        }

         */

        EventPermMap i = eventPermManagerForMaterial.getItemsFromEvent(EventTypes.resurrect).iterator().next();

        if (i == null){
            return;
        }


        if(p.getItemInHand() != null){
            if (p.getItemInHand().getType().equals(i.getType())){
                if(eventPermManagerForMaterial.usableWithoutPerm(p.getItemInHand())){
                    return;
                }
            }
        }

        if(p.getInventory().getItemInOffHand() != null){
            if(p.getInventory().getItemInOffHand().equals(i.getType())){
                if(eventPermManagerForMaterial.usableWithoutPerm(p.getInventory().getItemInOffHand())){
                    return;
                }
            }
        }

        if (p.hasPermission(i.getPermission())){
            return;
        }
        e.setCancelled(true);
        denyVisualizer(p);
        p.sendMessage(colorize(i.getMessage()));
    }

    @EventHandler
    public void onItemSelected(PlayerItemHeldEvent e){
        if (e.getPlayer().isOp()){
            return;
        }
        Player p = e.getPlayer();
        if (p.getInventory().getItem(e.getNewSlot()) == null){
            return;
        }

        ItemStack item = p.getInventory().getItem(e.getNewSlot());

        if(eventPermManagerForMaterial.usableWithoutPerm(item)){
            return;
        }

        Set<EventPermMap> oprMaps = eventPermManagerForMaterial.getAllMaterials();
        for(EventPermMap i : oprMaps){
            if (i.getType().equals(item.getType())){
                if (p.hasPermission(i.getPermission())){
                    return;
                }
                p.sendMessage(colorize(i.getMessage()));
            }
        }
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent e){
        if (!(e.getEntity().getShooter() instanceof Player)){
            return;
        }

        ItemStack item = null;
        Player p = ((Player) e.getEntity().getShooter()).getPlayer();

        if(p.getItemInHand() != null){
            item = p.getItemInHand();
        }

        Set<EventPermMap> oprMaps = eventPermManagerForMaterial.getItemsFromEvent(EventTypes.throwing);

        for(EventPermMap i : oprMaps){
            if (((Material)i.getType()).name().equals(e.getEntityType().name())){
                if (p.hasPermission(i.getPermission())){
                    return;
                }
                if(item != null){
                    if (eventPermManagerForMaterial.usableWithoutPerm(item)){
                        return;
                    }
                }

                e.setCancelled(true);
                denyVisualizer(p);
                p.sendMessage(colorize(i.getMessage()));
            }


        }
    }


    public void pushBackPlayer(Player p, Location from){
        Vector portalPush = from.toVector();
        portalPush.subtract(p.getLocation().toVector());
        p.setVelocity(portalPush.multiply(3).setX(1).setZ(1));
    }
    public void pushUpPlayer(Player p, Location from){
        Vector portalPush = from.toVector();
        portalPush.subtract(p.getLocation().toVector());
        p.setVelocity(portalPush.multiply(1).setY(0.5));
    }
    public void denyVisualizer(Player p){
        Location loc = p.getLocation();
        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1);
        p.spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY()+1.15d, loc.getZ(), 3, dustOptions);
        p.spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY()+1.15d, loc.getZ(), 3, dustOptions2);
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (label.equalsIgnoreCase("ospermrestrictions") || label.equalsIgnoreCase("opr")){
            if (!sender.hasPermission("opr.reload") || !sender.isOp()){
                sender.sendMessage(colorize(prefix+ noPermisisonMessage));
                return true;
            }
            if (args.length == 0){
                sender.sendMessage(ChatColor.WHITE+"/ospermrestrictions reload");
                return true;
            }
            if (args.length > 0){
                if (args[0].equalsIgnoreCase("reload")){
                    sender.sendMessage(colorize(prefix+ reloadingMessage));;
                    loadConfig();
                    sender.sendMessage(colorize(prefix+ reloadedMessage));
                    return true;
                }
            }

        }
        return false;
    }


}
