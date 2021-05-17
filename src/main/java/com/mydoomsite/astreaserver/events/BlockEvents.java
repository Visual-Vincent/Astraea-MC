package com.mydoomsite.astreaserver.events;

import com.mydoomsite.astreaserver.datatypes.ProtectedRegion;
import com.mydoomsite.astreaserver.helpers.WorldHelper;
import com.mydoomsite.astreaserver.main.RegionProtector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class BlockEvents
{
	@SubscribeEvent
	public void Break(BlockEvent.BreakEvent event)
	{
		if(event.getWorld().isClientSide())
			return;
		
		MinecraftServer server = WorldHelper.GetWorldServer(event.getWorld());
		if(server == null)
			return;
		
		PlayerEntity player = event.getPlayer();
		ProtectedRegion region = RegionProtector.GetProtectedRegion((ServerWorld)event.getWorld(), event.getPos());
		
		if(region == null || region.PlayerHasAccess(player))
			return;
		
		String logMessage = String.format(
			"%s broke block \"%s\" at (%d, %d, %d)",
			player.getName().getContents(), event.getState().getBlock().getRegistryName().toString(),
			event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()
		);
		
		RegionEventCallbacks.RegionBlockEventCallback(event, event.getWorld(), player, event.getPos(), logMessage);
	}
	
	@SubscribeEvent
	public void Place(BlockEvent.EntityPlaceEvent event)
	{
		if(event.getWorld().isClientSide())
			return;
		
		MinecraftServer server = WorldHelper.GetWorldServer(event.getWorld());
		if(server == null)
			return;
		
		Entity entity = event.getEntity();
		if(!(entity instanceof PlayerEntity))
			return;
		
		PlayerEntity player = (PlayerEntity)entity;
		ProtectedRegion region = RegionProtector.GetProtectedRegion((ServerWorld)event.getWorld(), event.getPos());
		
		if(region == null || region.PlayerHasAccess(player))
			return;
		
		String logMessage = String.format(
			"%s placed block \"%s\" at (%d, %d, %d)",
			player.getName().getContents(), event.getPlacedBlock().getBlock().getRegistryName().toString(),
			event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()
		);
		
		RegionEventCallbacks.RegionBlockEventCallback(event, event.getWorld(), player, event.getPos(), logMessage);
	}
}
