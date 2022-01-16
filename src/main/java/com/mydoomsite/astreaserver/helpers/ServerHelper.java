package com.mydoomsite.astreaserver.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import net.minecraft.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.server.ServerLifecycleHooks;

public final class ServerHelper
{
    public static void BroadcastMessage(LevelAccessor world, Component text, ChatType type)
    {
        BroadcastMessage(WorldHelper.GetWorldServer(world), text, type);
    }
    
    public static void BroadcastMessage(MinecraftServer server, Component text, ChatType type)
    {
        server.getPlayerList().broadcastMessage(text, type, Util.NIL_UUID);
    }
    
    public static File GetAstreaServerPath() throws IOException
    {
        return GetAstreaServerPath(ServerLifecycleHooks.getCurrentServer());
    }
    
    public static File GetAstreaServerPath(MinecraftServer server) throws IOException
    {
        File serverPath = server.getServerDirectory();
        File astreaDir = new File(serverPath, "astrea");
        
        if(!astreaDir.exists() && !astreaDir.mkdir())
            throw new IOException("Failed to create directory " + astreaDir.getCanonicalPath());
        
        return astreaDir;
    }
    
    public static File GetAstreaWorldPath() throws IOException
    {
        return GetAstreaWorldPath(ServerLifecycleHooks.getCurrentServer());
    }
    
    public static File GetAstreaWorldPath(MinecraftServer server) throws IOException
    {
        Path astreaPath = server.getWorldPath(new LevelResource("astrea"));
        File astreaDir = astreaPath.toFile();
        
        if(!astreaDir.exists() && !astreaDir.mkdir())
            throw new IOException("Failed to create directory " + astreaDir.getCanonicalPath());
        
        return astreaDir;
    }
    
    public static File GetProtectedRegionsPath() throws IOException
    {
        return GetProtectedRegionsPath(ServerLifecycleHooks.getCurrentServer());
    }
    
    public static File GetProtectedRegionsPath(MinecraftServer server) throws IOException
    {
        File astreaPath = GetAstreaWorldPath(server);
        File regionsPath = new File(astreaPath, "protectedregions");
        
        if(!regionsPath.exists() && !regionsPath.mkdir())
            throw new IOException("Failed to create directory " + regionsPath.getCanonicalPath());
        
        return regionsPath;
    }
}
