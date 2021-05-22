package com.mydoomsite.astreaserver.datatypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import com.mydoomsite.astreaserver.helpers.PlayerHelper;
import com.mydoomsite.astreaserver.lib.Constants;
import com.mydoomsite.astreaserver.main.MainRegistry;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector3i;

public class ProtectedRegion
{
    public static final int VERSION = 1;
    
    public static final int PROTECTION_NONE = 0;
    public static final int PROTECTION_LOGGING_ONLY = 1;
    public static final int PROTECTION_GRIEFING = 2;
    
    @SuppressWarnings("serial")
    public static final Map<String, Integer> ProtectionLevels = Collections.unmodifiableMap(new HashMap<String, Integer>() {
        {
            put("logging", PROTECTION_LOGGING_ONLY);
            put("griefing", PROTECTION_GRIEFING);
        }
    });
    
    public static String GetProtectionLevelName(int protectionLevel)
    {
        switch(protectionLevel)
        {
            case PROTECTION_NONE:           return "None";
            case PROTECTION_LOGGING_ONLY:	return "Logging only";
            case PROTECTION_GRIEFING:       return "Grief protection";
        }
        
        return "<unknown>";
    }
    
    public String Name;
    public UUID Owner;
    public UUID Protector;
    public int ProtectionLevel;
    public final Vector3i Start;
    public final Vector3i End;
    
    public final HashSet<UUID> TrustedPlayers = new HashSet<>();
    
    public ProtectedRegion(String name, Vector3i start, Vector3i end, int protectionLevel, UUID owner, UUID protector)
    {
        if(name == null || name.length() <= 0) throw new IllegalArgumentException("Name cannot be null");
        if(start == null) throw new IllegalArgumentException("Start position cannot be null");
        if(end == null)   throw new IllegalArgumentException("End position cannot be null");
        
        if(protectionLevel < PROTECTION_NONE || protectionLevel > PROTECTION_GRIEFING)
            throw new IllegalArgumentException("Invalid region protection level");
        
        if(owner == null || protector == null)
            throw new IllegalArgumentException("Player UUID cannot be null");
        
        if(owner == Util.NIL_UUID || protector == Util.NIL_UUID)
            throw new IllegalArgumentException("Player UUID cannot be nil");
        
        this.Name = name;
        this.Start = new Vector3i(Math.min(start.getX(), end.getX()), Math.min(start.getY(), end.getY()), Math.min(start.getZ(), end.getZ()));
        this.End   = new Vector3i(Math.max(start.getX(), end.getX()), Math.max(start.getY(), end.getY()), Math.max(start.getZ(), end.getZ()));
        this.ProtectionLevel = protectionLevel;
        this.Owner     = owner;
        this.Protector = protector;
    }
    
    public boolean Contains(Vector3i position)
    {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();

        return (
            x >= this.Start.getX() && x <= this.End.getX() &&
            y >= this.Start.getY() && y <= this.End.getY() &&
            z >= this.Start.getZ() && z <= this.End.getZ()
        );
    }
    
    public boolean PlayerHasAccess(PlayerEntity player)
    {
        if(Constants.DEBUG)
            return false;
        
        UUID uuid = player.getUUID();
        return this.Owner.equals(uuid) || PlayerHelper.IsSuperAdmin(uuid) || this.TrustedPlayers.contains(uuid);
    }
    
    public boolean PlayerHasAdminAccess(PlayerEntity player)
    {
        UUID uuid = player.getUUID();
        return this.Owner.equals(uuid) || PlayerHelper.IsSuperAdmin(uuid) || (this.Protector.equals(uuid) && PlayerHelper.IsOp(player));
    }
    
    private static boolean ValidateNBTPos(CompoundNBT compound)
    {
        return compound != null &&
            compound.contains("x", NBTTagType.Int.getId()) &&
            compound.contains("y", NBTTagType.Int.getId()) &&
            compound.contains("z", NBTTagType.Int.getId());
    }
    
    private static Vector3i GetNBTPos(CompoundNBT compound)
    {
        if(!ValidateNBTPos(compound))
            return null;
        
        return new Vector3i(compound.getInt("x"), compound.getInt("y"), compound.getInt("z"));
    }
    
    public static ProtectedRegion Load(String file) throws IOException, InvalidObjectException, IllegalArgumentException
    {
        File f = new File(file);
        CompoundNBT root = CompressedStreamTools.readCompressed(f);
        
        if(root == null)
            throw new FileNotFoundException("File not found: " + f.getAbsolutePath());
        
        Vector3i start, end;
        
        if(
            !root.contains("VERSION", NBTTagType.Int.getId()) ||
            !root.contains("name", NBTTagType.String.getId()) ||
            !root.contains("owner", NBTTagType.String.getId()) ||
            !root.contains("protector", NBTTagType.String.getId()) ||
            !root.contains("protectionLevel", NBTTagType.Int.getId()) ||
            !root.contains("start", NBTTagType.Compound.getId()) || (start = GetNBTPos(root.getCompound("start"))) == null ||
            !root.contains("end",   NBTTagType.Compound.getId()) || (end = GetNBTPos(root.getCompound("end"))) == null ||
            !root.contains("trustedPlayers", NBTTagType.List.getId())
        )
            throw new InvalidObjectException("File \"" + f.getAbsolutePath() + "\" is not a valid ProtectedRegion");
        
        int version = root.getInt("VERSION");
        
        if(version < 1)
            throw new InvalidObjectException("Invalid version " + version + " in file \"" + f.getAbsolutePath() + "\"");
        else if(version > VERSION)
            throw new InvalidObjectException("File \"" + f.getAbsolutePath() + "\" is of version " + version + ", only version " + VERSION + " or lower is supported.");
        
        String name         = root.getString("name");
        UUID owner          = UUID.fromString(root.getString("owner"));
        UUID protector      = UUID.fromString(root.getString("protector"));
        int protectionLevel = root.getInt("protectionLevel");
        
        ProtectedRegion region = new ProtectedRegion(name, start, end, protectionLevel, owner, protector);
        
        ListNBT trustedPlayers = root.getList("trustedPlayers", NBTTagType.String.getId());
        for(INBT entry : trustedPlayers)
        {
            StringNBT uuid = (StringNBT)entry;
            try
            {
                region.TrustedPlayers.add(UUID.fromString(uuid.getAsString()));
            }
            catch (IllegalArgumentException ex)
            {
                MainRegistry.Logger.error(f.getAbsolutePath() + ": Bad UUID '" + uuid.getAsString() + "'; Ignoring.");
            }
        }
        
        return region;
    }
    
    public void Save(String file) throws IOException
    {
        CompoundNBT root = new CompoundNBT();
            root.putInt("VERSION", VERSION);
            root.putString("name", this.Name);
            root.putString("owner", this.Owner.toString());
            root.putString("protector", this.Protector.toString());
            root.putInt("protectionLevel", this.ProtectionLevel);
        
        CompoundNBT start = new CompoundNBT();
            start.putInt("x", this.Start.getX());
            start.putInt("y", this.Start.getY());
            start.putInt("z", this.Start.getZ());
        root.put("start", start);
        
        CompoundNBT end = new CompoundNBT();
            end.putInt("x", this.End.getX());
            end.putInt("y", this.End.getY());
            end.putInt("z", this.End.getZ());
        root.put("end", end);
        
        ListNBT trustedPlayers = new ListNBT();
        for(UUID uuid : this.TrustedPlayers)
        {
            trustedPlayers.add(StringNBT.valueOf(uuid.toString()));
        }
        root.put("trustedPlayers", trustedPlayers);
        
        File f = new File(file);
        try (OutputStream outputStream = new FileOutputStream(f, false))
        {
            CompressedStreamTools.writeCompressed(root, outputStream);
        }
    }
}