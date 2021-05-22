package com.mydoomsite.astreaserver.events;

import com.mydoomsite.astreaserver.helpers.BlockHelper;
import com.mydoomsite.astreaserver.helpers.WorldHelper;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BellBlock;
import net.minecraft.block.Block;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.StonecutterBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
        
        PlayerEntity player = event.getPlayer();
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
        if(!(entity instanceof PlayerEntity))
            return;
        
        PlayerEntity player = (PlayerEntity)entity;
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
        World world = event.getWorld();
        if(!WorldHelper.IsServerWorld(world))
            return;
        
        PlayerEntity player = event.getPlayer();
        BlockPos pos = event.getPos();
        Block block = world.getBlockState(pos).getBlock();
        
        // Exception for special blocks
        if(
            block instanceof DoorBlock ||
            block instanceof TrapDoorBlock ||
            block instanceof FenceGateBlock ||
            //block instanceof BedBlock ||
            block instanceof CraftingTableBlock ||
            block instanceof AbstractButtonBlock ||
            block instanceof LeverBlock ||
            block instanceof AnvilBlock ||
            block instanceof StonecutterBlock ||
            block instanceof BellBlock ||
            block instanceof EnderChestBlock
        )
            return;
        
        if(!BlockHelper.IsInteractable(block))
            return;
        
        String logMessage = String.format(
            "%s interacted with block \"%s\" at (%d, %d, %d)",
            player.getName().getContents(), block.getRegistryName().toString(),
            pos.getX(), pos.getY(), pos.getZ()
        );
        
        ItemStack itemStack = event.getItemStack();
        
        if(itemStack != null && itemStack != ItemStack.EMPTY)
        {
            Item item = itemStack.getItem();
            
            if(item instanceof BucketItem)
                return; // Handled by ItemEvents.OnUseBucket()
            
            if(item instanceof BlockItem)
                return;
            
            logMessage = String.format("%s using \"%s\"", logMessage, item.getRegistryName().toString());
        }
        
        RegionEventCallbacks.RegionDefaultEventCallback(event, world, player, pos, logMessage);
    }
}
