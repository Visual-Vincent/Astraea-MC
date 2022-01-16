package com.mydoomsite.astreaserver.helpers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;

import com.google.gson.*;
import com.mydoomsite.astreaserver.datatypes.NBTTagType;
import com.mydoomsite.astreaserver.datatypes.SafeFile;
import com.mydoomsite.astreaserver.main.MainRegistry;

import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class PlayerHelper
{
    private static final String PLAYERNAME_API_URL = "https://api.mojang.com/user/profiles/%s/names";
    
    private static final Map<UUID, String> playerNameCache = new ConcurrentHashMap<>();
    private static final HashSet<UUID> superAdmins = new HashSet<>();
    
    private static final int SUPERADMINFORMAT_VERSION = 1;
    
    @SubscribeEvent
    public void OnPlayerLoggedIn(PlayerLoggedInEvent event)
    {
        Player player = event.getPlayer();
        PlayerHelper.playerNameCache.put(player.getUUID(), player.getName().getContents());
    }
    
    @SubscribeEvent
    public void OnServerStarting(ServerStartingEvent event)
    {
        try
        {
            LoadSuperAdmins();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public static boolean IsOp(Player player)
    {
        Level world = player.level;
        
        if(!(world instanceof ServerLevel))
            return false;
        
        ServerLevel serverWorld = (ServerLevel)world;
        
        return serverWorld.getServer().getPlayerList().isOp(player.getGameProfile());
    }
    
    public static boolean IsSuperAdmin(UUID uuid)
    {
        return superAdmins.contains(uuid);
    }
    
    public static void LoadSuperAdmins() throws IOException
    {
        File astreaPath = ServerHelper.GetAstreaServerPath();
        SafeFile file = new SafeFile(astreaPath.getCanonicalPath(), "superadmins.bin");
        
        if(!file.Exists())
            return;
        
        CompoundTag root;
        try (InputStream stream = file.OpenRead())
        {
            root = NbtIo.readCompressed(stream);
        }
        
        if(root == null)
            throw new IOException("An unknown error occurred while reading NBT file '" + file.getFullPath() + "'");
        
        if(
            !root.contains("VERSION", NBTTagType.Int.getId()) ||
            !root.contains("players", NBTTagType.List.getId())
        )
            throw new InvalidObjectException("File '" + file.getFullPath() + "' is not a valid ProtectedRegion");
        
        int version = root.getInt("VERSION");
        
        if(version < 1)
            throw new InvalidObjectException("Invalid version " + version + " in file '" + file.getFullPath() + "'");
        else if(version > SUPERADMINFORMAT_VERSION)
            throw new InvalidObjectException("File '" + file.getFullPath() + "' is of version " + version + ", only version " + SUPERADMINFORMAT_VERSION + " or lower is supported.");
        
        ListTag players = root.getList("players", NBTTagType.String.getId());
        for(Tag entry : players)
        {
            StringTag uuid = (StringTag)entry;
            try
            {
                superAdmins.add(UUID.fromString(uuid.getAsString()));
            }
            catch (IllegalArgumentException ex)
            {
                MainRegistry.Logger.error("'" + file.getFullPath() + "': Bad UUID '" + uuid.getAsString() + "'; Ignoring.");
            }
        }
    }
    
    private static void SaveSuperAdmins() throws IOException
    {
        CompoundTag root = new CompoundTag();
        root.putInt("VERSION", SUPERADMINFORMAT_VERSION);
        
        ListTag players = new ListTag();
        for(UUID uuid : superAdmins)
        {
            players.add(StringTag.valueOf(uuid.toString()));
        }
        root.put("players", players);
        
        File astreaPath = ServerHelper.GetAstreaServerPath();
        SafeFile file = new SafeFile(astreaPath.getCanonicalPath(), "superadmins.bin", true);
        try (OutputStream stream = file.OpenWrite())
        {
            NbtIo.writeCompressed(root, stream);
        }
    }
    
    public static boolean MakeSuperAdmin(UUID uuid) throws IOException
    {
        if(!superAdmins.add(uuid))
            return false;
        
        SaveSuperAdmins();
        return true;
    }
    
    public static boolean RemoveSuperAdmin(UUID uuid) throws IOException
    {
        if(!superAdmins.remove(uuid))
            return false;
        
        SaveSuperAdmins();
        return true;
    }
    
    public static String GetPlayerName(UUID uuid) throws Exception
    {
        if(uuid == null || uuid == Util.NIL_UUID)
            throw new Exception("UUID cannot be null");
        
        String name = playerNameCache.get(uuid);
        if(name != null)
            return name;
        
        name = "<error>";
        
        URL url = new URL(String.format(PLAYERNAME_API_URL, uuid.toString().replaceAll("-", "")));
        String json = IOUtils.toString(url, StandardCharsets.UTF_8);
        JsonElement root = JsonParser.parseString(json);
        
        if(!root.isJsonArray())
            throw new Exception("Invalid response from server");
        
        long changedAt = 0;
        
        JsonArray names = root.getAsJsonArray();
        for(JsonElement element : names)
        {
            if(!element.isJsonObject())
                continue;
            
            JsonObject object = element.getAsJsonObject();
            
            JsonElement nameProp = object.get("name");
            JsonElement changedAtProp = object.get("changedToAt");
            
            if(nameProp == null || nameProp.isJsonNull() || (nameProp.isJsonPrimitive() && !((JsonPrimitive)nameProp).isString()))
                continue;
            
            long currentChangedAt = 0;
            if(changedAtProp != null && changedAtProp.isJsonPrimitive())
            {
                JsonPrimitive primitive = changedAtProp.getAsJsonPrimitive();
                if(primitive.isNumber())
                {
                    currentChangedAt = primitive.getAsLong();
                }
            }
            
            if(currentChangedAt < changedAt)
                continue;
            
            changedAt = currentChangedAt;
            name = nameProp.getAsString();
        }
        
        playerNameCache.put(uuid, name);
        
        return name;
    }
}
