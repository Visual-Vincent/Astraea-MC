package com.mydoomsite.astreaserver.events;

import com.mydoomsite.astreaserver.helpers.EntityHelper;
import com.mydoomsite.astreaserver.helpers.WorldHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class EntityEvents
{
    @SubscribeEvent
    public void OnAttack(AttackEntityEvent event)
    {
        Entity entity = event.getTarget();
        if(!(
            entity instanceof HangingEntity ||
            entity instanceof ArmorStand
        ))
            return;
        
        Level world = entity.level;
        if(!WorldHelper.IsServerWorld(world))
            return;
        
        Player player = event.getPlayer();
        BlockPos pos = entity.blockPosition();
        
        String logMessage = String.format(
            "%s hit entity \"%s\" at (%d, %d, %d)",
            player.getName().getContents(), entity.getType().getRegistryName().toString(),
            pos.getX(), pos.getY(), pos.getZ()
        );
            
        RegionEventCallbacks.RegionDefaultEventCallback(event, world, player, pos, logMessage);
    }
    
    @SubscribeEvent
    public void OnInteract(EntityInteract event)
    {
        Level world = event.getWorld();
        if(!WorldHelper.IsServerWorld(world))
            return;
        
        Player player = event.getPlayer();
        Entity target = event.getTarget();
        BlockPos pos = event.getPos();
        
        if(target instanceof Player)
            return;
        
        // TODO: Owned horses shouldn't be mountable, etc. Write special cases for such mobs
        if(target instanceof Mob)
            return;
        
        if(!(
            target instanceof HangingEntity ||
            target instanceof ArmorStand
        ))
            return;
        
        if(!EntityHelper.IsInteractable(target))
            return;
        
        String logMessage = String.format(
            "%s interacted with entity \"%s\" at (%d, %d, %d)",
            player.getName().getContents(), target.getType().getRegistryName().toString(),
            pos.getX(), pos.getY(), pos.getZ()
        );
            
        RegionEventCallbacks.RegionDefaultEventCallback(event, world, player, pos, logMessage);
    }
    
    @SubscribeEvent
    public void OnInteractSpecific(EntityInteractSpecific event)
    {
        Level world = event.getWorld();
        if(!WorldHelper.IsServerWorld(world))
            return;
        
        Player player = event.getPlayer();
        Entity target = event.getTarget();
        BlockPos pos = event.getPos();
        
        if(target instanceof Player)
            return;
        
        if(target instanceof Mob)
            return;
        
        if(!(
            target instanceof HangingEntity ||
            target instanceof ArmorStand
        ))
            return;
        
        if(!EntityHelper.IsInteractableSpecific(target))
            return;
        
        String logMessage = String.format(
            "%s interacted with entity \"%s\" at (%d, %d, %d)",
            player.getName().getContents(), target.getType().getRegistryName().toString(),
            pos.getX(), pos.getY(), pos.getZ()
        );
            
        RegionEventCallbacks.RegionDefaultEventCallback(event, world, player, pos, logMessage);
    }
}
