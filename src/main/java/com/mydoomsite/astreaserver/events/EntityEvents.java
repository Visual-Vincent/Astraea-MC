package com.mydoomsite.astreaserver.events;

import com.mydoomsite.astreaserver.helpers.EntityHelper;
import com.mydoomsite.astreaserver.helpers.WorldHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class EntityEvents
{
    @SubscribeEvent
    public void OnInteract(EntityInteract event)
    {
        World world = event.getWorld();
        if(!WorldHelper.IsServerWorld(world))
            return;
        
        PlayerEntity player = event.getPlayer();
        Entity target = event.getTarget();
        BlockPos pos = event.getPos();
        
        if(target instanceof PlayerEntity)
            return;
        
        // TODO: Owned horses shouldn't be mountable, etc. Write special cases for such mobs
        if(target instanceof MobEntity)
            return;
        
        if(!(
            target instanceof HangingEntity ||
            target instanceof ArmorStandEntity
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
        World world = event.getWorld();
        if(!WorldHelper.IsServerWorld(world))
            return;
        
        PlayerEntity player = event.getPlayer();
        Entity target = event.getTarget();
        BlockPos pos = event.getPos();
        
        if(target instanceof PlayerEntity)
            return;
        
        if(target instanceof MobEntity)
            return;
        
        if(!(
            target instanceof HangingEntity ||
            target instanceof ArmorStandEntity
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
