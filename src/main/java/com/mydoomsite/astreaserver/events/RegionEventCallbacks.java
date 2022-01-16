package com.mydoomsite.astreaserver.events;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event;

import com.mydoomsite.astreaserver.datatypes.ProtectedRegion;
import com.mydoomsite.astreaserver.helpers.ServerHelper;
import com.mydoomsite.astreaserver.helpers.WorldHelper;
import com.mydoomsite.astreaserver.lib.Constants;
import com.mydoomsite.astreaserver.main.MainRegistry;
import com.mydoomsite.astreaserver.main.RegionProtector;

public class RegionEventCallbacks
{
    private static void RegionProcessEventCallback(Event event, ProtectedRegion region, String logMessage)
    {
        if(event == null) throw new IllegalArgumentException("'event' cannot be null");
        if(region == null) throw new IllegalArgumentException("'region' cannot be null");
        if(logMessage == null) throw new IllegalArgumentException("'logMessage' cannot be null");
        
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
    
    public static void RegionDefaultEventCallback(Event event, LevelAccessor world, Player player, BlockPos position, String logMessage)
    {
        if(event == null) throw new IllegalArgumentException("'event' cannot be null");
        if(world == null) throw new IllegalArgumentException("'world' cannot be null");
        if(player == null) throw new IllegalArgumentException("'player' cannot be null");
        if(position == null) throw new IllegalArgumentException("'position' cannot be null");
        if(logMessage == null) throw new IllegalArgumentException("'logMessage' cannot be null");
        
        if(!WorldHelper.IsServerWorld(world))
            return;
        
        ProtectedRegion region = RegionProtector.GetProtectedRegion((ServerLevel)world, position);
        
        if(region == null || region.PlayerHasAccess(player))
            return;
        
        RegionProcessEventCallback(event, region, logMessage);
    }
    
    public static void RegionDefaultEventCallback(Event event, LevelAccessor world, Player player, Vec3 position, String logMessage)
    {
        if(position == null)
            throw new IllegalArgumentException("'position' cannot be null");
        
        RegionDefaultEventCallback(event, world, player, new BlockPos(position), logMessage);
    }
}
