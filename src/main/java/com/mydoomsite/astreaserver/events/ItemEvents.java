package com.mydoomsite.astreaserver.events;

import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.mydoomsite.astreaserver.helpers.WorldHelper;

public class ItemEvents
{
    @SubscribeEvent
    public void OnUseBucket(FillBucketEvent event)
    {
        World world = event.getWorld();
        if(!WorldHelper.IsServerWorld(world))
            return;
        
        RayTraceResult target = event.getTarget();
        if(target == null || target.getType() != Type.BLOCK)
            return;
        
        if(!(target instanceof BlockRayTraceResult))
            return;
        
        BlockRayTraceResult blockResult = (BlockRayTraceResult)target;
        
        PlayerEntity player = event.getPlayer();
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
            if(!(block instanceof IBucketPickupHandler) && !(block instanceof FlowingFluidBlock))
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
