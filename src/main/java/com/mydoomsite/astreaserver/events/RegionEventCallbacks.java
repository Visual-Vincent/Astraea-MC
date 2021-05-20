package com.mydoomsite.astreaserver.events;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;

import com.mydoomsite.astreaserver.datatypes.ProtectedRegion;
import com.mydoomsite.astreaserver.helpers.ServerHelper;
import com.mydoomsite.astreaserver.helpers.WorldHelper;
import com.mydoomsite.astreaserver.lib.Constants;
import com.mydoomsite.astreaserver.main.MainRegistry;
import com.mydoomsite.astreaserver.main.RegionProtector;

public class RegionEventCallbacks
{
	public static void RegionDefaultEventCallback(Event event, ProtectedRegion region, String logMessage)
	{
		switch(region.ProtectionLevel)
		{
			case ProtectedRegion.PROTECTION_GRIEFING:
				if(event.isCancelable())
				{
					event.setCanceled(true);
					break;
				}
				// If canceling is not possible, fall-through to logging
				
			case ProtectedRegion.PROTECTION_LOGGING_ONLY:
				try
				{
					File regionsPath = ServerHelper.GetProtectedRegionsPath();
					File logFile = new File(regionsPath, region.Name + ".log");
					
					String log = String.format("[%s] %s", LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER), logMessage);
					
					try(FileWriter fw = new FileWriter(logFile, true))
					{
						fw.write(log + System.lineSeparator());
					}
				}
				catch (Exception ex)
				{
					String log = String.format("[%s] %s", region.Name, logMessage);
					MainRegistry.Logger.info(log);
					ex.printStackTrace();
				}
				break;
		}
	}
	
	public static void RegionBlockEventCallback(BlockEvent event, IWorld world, PlayerEntity player, BlockPos blockPosition, String logMessage)
	{
		if(world.isClientSide())
			return;
		
		MinecraftServer server = WorldHelper.GetWorldServer(world);
		if(server == null)
			return;
		
		ProtectedRegion region = RegionProtector.GetProtectedRegion((ServerWorld)world, blockPosition);
		
		if(region == null || region.PlayerHasAccess(player))
			return;
		
		RegionDefaultEventCallback(event, region, logMessage);
	}
}
