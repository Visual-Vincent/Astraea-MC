package com.mydoomsite.astreaserver.events;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.mydoomsite.astreaserver.main.RegionProtector;

public final class ServerEvents
{
    @SubscribeEvent
    public void ServerStarted(final ServerStartedEvent event)
    {
        MinecraftServer server = event.getServer();
        ServerLevel world = server.getLevel(Level.OVERWORLD);
        
        try
        {
            RegionProtector.LoadProtectedRegions(world, true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
