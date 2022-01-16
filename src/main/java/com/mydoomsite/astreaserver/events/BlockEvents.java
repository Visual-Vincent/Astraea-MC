package com.mydoomsite.astreaserver.events;

import com.mydoomsite.astreaserver.helpers.BlockHelper;
import com.mydoomsite.astreaserver.helpers.WorldHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class BlockEvents
{
    @SubscribeEvent
    public void OnBreak(BlockEvent.BreakEvent event)
    {
        if(!WorldHelper.IsServerWorld(event.getWorld()))
            return;
        
        Player player = event.getPlayer();
        BlockPos pos = event.getPos();
        
        String logMessage = String.format(
            "%s broke block \"%s\" at (%d, %d, %d)",
            player.getName().getContents(), event.getState().getBlock().getRegistryName().toString(),
            pos.getX(), pos.getY(), pos.getZ()
        );
        
        RegionEventCallbacks.RegionDefaultEventCallback(event, event.getWorld(), player, pos, logMessage);
    }
    
    @SubscribeEvent
    public void OnPlace(BlockEvent.EntityPlaceEvent event)
    {
        if(!WorldHelper.IsServerWorld(event.getWorld()))
            return;
        
        Entity entity = event.getEntity();
        if(!(entity instanceof Player))
            return;
        
        Player player = (Player)entity;
        BlockPos pos = event.getPos();
        
        String logMessage = String.format(
            "%s placed block \"%s\" at (%d, %d, %d)",
            player.getName().getContents(), event.getPlacedBlock().getBlock().getRegistryName().toString(),
            pos.getX(), pos.getY(), pos.getZ()
        );
        
        RegionEventCallbacks.RegionDefaultEventCallback(event, event.getWorld(), player, pos, logMessage);
    }
    
    @SubscribeEvent
    public void OnRightClick(RightClickBlock event)
    {
        Level world = event.getWorld();
        if(!WorldHelper.IsServerWorld(world))
            return;
        
        Player player = event.getPlayer();
        BlockPos pos = event.getPos();
        Block block = world.getBlockState(pos).getBlock();
        
        // Exception for special blocks
        if(
            block instanceof DoorBlock ||
            block instanceof TrapDoorBlock ||
            block instanceof FenceGateBlock ||
            //block instanceof BedBlock ||
            block instanceof CraftingTableBlock ||
            block instanceof ButtonBlock ||
            block instanceof LeverBlock ||
            block instanceof StonecutterBlock ||
            block instanceof BellBlock ||
            block instanceof EnderChestBlock
        )
            return;
        
        ItemStack itemStack = event.getItemStack();
        
        // TODO: Check ItemStack and handle placement of entities such as Paintings, Armor Stand, Item Frame, etc.
        
        if(!BlockHelper.IsInteractable(block))
            return;
        
        String logMessage = String.format(
            "%s interacted with block \"%s\" at (%d, %d, %d)",
            player.getName().getContents(), block.getRegistryName().toString(),
            pos.getX(), pos.getY(), pos.getZ()
        );
        
        if(itemStack != null && itemStack != ItemStack.EMPTY)
        {
            Item item = itemStack.getItem();
            
            if(!(
                item instanceof BucketItem ||
                item instanceof BlockItem
            ))
            {
                logMessage = String.format("%s using \"%s\"", logMessage, item.getRegistryName().toString());
            }
        }
        
        RegionEventCallbacks.RegionDefaultEventCallback(event, world, player, pos, logMessage);
    }
}
