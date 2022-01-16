package com.mydoomsite.astreaserver.helpers;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;

public final class WorldHelper
{
    @Nullable
    public static MinecraftServer GetWorldServer(LevelAccessor world)
    {
        if(world == null || !(world instanceof ServerLevel))
            return null;
        
        return ((ServerLevel)world).getServer();
    }
    
    public static boolean IsServerWorld(LevelAccessor world)
    {
        return world != null && !world.isClientSide() && world instanceof ServerLevel;
    }
    
    public static boolean IsOverworld(Level world)
    {
        return world.dimension().location().getPath() == DimensionType.OVERWORLD_LOCATION.location().getPath();
    }
    
    public static File GetServerPath(LevelAccessor world)
    {
        if(world == null)
            throw new NullPointerException("Argument 'world' cannot be null");
        
        MinecraftServer server = GetWorldServer(world);
        
        if(server == null)
            throw new NullPointerException("No MinecraftServer instance found in " + world.getClass().getName());
        
        return server.getServerDirectory();
    }
    
    public static File GetAstreaServerPath(LevelAccessor world) throws IOException
    {
        return ServerHelper.GetAstreaServerPath(WorldHelper.GetWorldServer(world));
    }
    
    public static File GetAstreaWorldPath(LevelAccessor world) throws IOException
    {
        return ServerHelper.GetAstreaWorldPath(WorldHelper.GetWorldServer(world));
    }
    
    public static File GetProtectedRegionsPath(LevelAccessor world) throws IOException
    {
        return ServerHelper.GetProtectedRegionsPath(WorldHelper.GetWorldServer(world));
    }
}
