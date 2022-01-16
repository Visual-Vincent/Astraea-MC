package com.mydoomsite.astreaserver.events;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.mydoomsite.astreaserver.helpers.WorldHelper;

public class ItemEvents
{
    @SubscribeEvent
    public void OnUseBucket(FillBucketEvent event)
    {
        Level world = event.getWorld();
        if(!WorldHelper.IsServerWorld(world))
            return;
        
        HitResult target = event.getTarget();
        if(target == null || target.getType() != Type.BLOCK)
            return;
        
        if(!(target instanceof BlockHitResult))
            return;
        
        BlockHitResult blockResult = (BlockHitResult)target;
        
        Player player = event.getPlayer();
        BlockPos pos = blockResult.getBlockPos();
        Block block = world.getBlockState(pos).getBlock();
        
        ItemStack itemStack = event.getEmptyBucket(); // Used even if the bucket is initially filled
        if(itemStack == null || itemStack == ItemStack.EMPTY)
            return;
        
        Item item = itemStack.getItem();
        if(!(item instanceof BucketItem))
            return;
        
        BucketItem bucket = (BucketItem)item;
        Fluid fluid = bucket.getFluid();
        
        String logMessage;
        
        if(fluid != null && fluid != Fluids.EMPTY)
        {
            logMessage = String.format(
                "%s poured fluid \"%s\" at (%d, %d, %d)",
                player.getName().getContents(), fluid.getRegistryName().toString(),
                pos.getX(), pos.getY(), pos.getZ()
            );
        }
        else
        {
            if(!(block instanceof BucketPickup) && !(block instanceof LiquidBlock))
                return;
            
            logMessage = String.format(
                "%s filled a bucket with block \"%s\" at (%d, %d, %d)",
                player.getName().getContents(), block.getRegistryName().toString(),
                pos.getX(), pos.getY(), pos.getZ()
            );
        }
        
        RegionEventCallbacks.RegionDefaultEventCallback(event, world, player, pos, logMessage);
    }
}
