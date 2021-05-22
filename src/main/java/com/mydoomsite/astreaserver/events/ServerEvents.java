package com.mydoomsite.astreaserver.events;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

import com.mydoomsite.astreaserver.main.RegionProtector;

public final class ServerEvents
{
    @SubscribeEvent
    public void ServerStarted(final FMLServerStartedEvent event)
    {
        MinecraftServer server = event.getServer();
        ServerWorld world = server.getLevel(World.OVERWORLD);
        
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
