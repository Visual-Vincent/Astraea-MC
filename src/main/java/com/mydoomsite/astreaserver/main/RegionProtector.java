package com.mydoomsite.astreaserver.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mydoomsite.astreaserver.datatypes.ProtectedRegion;
import com.mydoomsite.astreaserver.datatypes.SafeFile;
import com.mydoomsite.astreaserver.helpers.PathHelper;
import com.mydoomsite.astreaserver.helpers.WorldHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

public final class RegionProtector
{
    public static final SimpleCommandExceptionType ERROR_NOT_OVERWORLD = new SimpleCommandExceptionType(new TextComponent("Region protection is only possible in the Overworld"));
    public static final SimpleCommandExceptionType ERROR_ALREADY_EXISTS = new SimpleCommandExceptionType(new TextComponent("A region with that name already exists"));
    public static final SimpleCommandExceptionType ERROR_NO_ACCESS = new SimpleCommandExceptionType(new TextComponent("You do not have permission to modify this region"));
    public static final SimpleCommandExceptionType ERROR_INVALID_PROTECTION_LEVEL = new SimpleCommandExceptionType(new TextComponent("Invalid protection level"));
    public static final SimpleCommandExceptionType ERROR_INVALID_NAME = new SimpleCommandExceptionType(new TextComponent("Invalid region name"));
    public static final SimpleCommandExceptionType ERROR_UNKNOWN = new SimpleCommandExceptionType(new TextComponent("An unknown error occurred. Please check the server log for details"));
    
    // Possible TODO: Implement QuadTree to minimize iterations?
    //                Alternatively just add chunks to a nested Map<> (will increase memory usage, but this is server so it should be fine)

    //             WorldRegionCoords2D
    private static final Map<Vec3i, List<ProtectedRegion>> protectedRegions = new ConcurrentHashMap<>();
    private static final HashSet<String> loadedRegions = new HashSet<>(); // Not thread-safe!
    
    private static Collection<Vec3i> GetRegionsForChunks(LevelChunk start, LevelChunk end)
    {
        ArrayList<Vec3i> regions = new ArrayList<>();
        
        int startRegionX = start.getPos().getRegionX();
        int startRegionZ = start.getPos().getRegionZ();
        
        if(start == end)
        {
            regions.add(new Vec3i(startRegionX, 0, startRegionZ));
            return regions;
        }
        
        int endRegionX = end.getPos().getRegionX();
        int endRegionZ = end.getPos().getRegionZ();
        
        for(int x = Math.min(startRegionX, endRegionX); x <= Math.max(startRegionX, endRegionX); x++)
        {
            for(int z = Math.min(startRegionZ, endRegionZ); z <= Math.max(startRegionZ, endRegionZ); z++)
            {
                regions.add(new Vec3i(x, 0, z));
            }
        }
        
        return regions;
    }
    
    public static void LoadProtectedRegions(ServerLevel world, boolean ignoreErrors) throws CommandSyntaxException, IOException
    {
        if(!WorldHelper.IsOverworld(world))
            throw ERROR_NOT_OVERWORLD.create();
        
        MainRegistry.Logger.info("Reloading protected regions...");
        
        File regionsPath = WorldHelper.GetProtectedRegionsPath(world);
        
        protectedRegions.clear();
        loadedRegions.clear();
        
        int c = 0;
        
        for(File file : regionsPath.listFiles((dir, name) -> name.toLowerCase().endsWith(".bin")))
        {
            if(!file.isFile())
                continue;
            
            try
            {
                ProtectedRegion region = ProtectedRegion.Load(file);
                LoadProtectedRegion(world, region);
                c++;
            }
            catch (Throwable ex)
            {
                if(!ignoreErrors)
                    throw ex;
                
                ex.printStackTrace();
            }
        }
        
        MainRegistry.Logger.info("Loaded " + c + " protected regions.");
    }
    
    private static void LoadProtectedRegion(ServerLevel world, ProtectedRegion region) throws CommandSyntaxException
    {
        if(!WorldHelper.IsOverworld(world))
            throw ERROR_NOT_OVERWORLD.create();
        
        if(RegionExists(region.Name))
            throw ERROR_ALREADY_EXISTS.create();
        
        BlockPos startPos = new BlockPos(region.Start);
        BlockPos endPos   = new BlockPos(region.End);
        
        LevelChunk startChunk = world.getChunkAt(startPos);
        LevelChunk endChunk   = world.getChunkAt(endPos);
        
        Collection<Vec3i> worldRegions = GetRegionsForChunks(startChunk, endChunk);
        
        loadedRegions.add(region.Name.toLowerCase());
        
        for(Vec3i worldRegion : worldRegions)
        {
            List<ProtectedRegion> regions = protectedRegions.get(worldRegion);
            
            if(regions == null)
            {
                regions = new ArrayList<>();
                protectedRegions.put(worldRegion, regions);
            }
            
            regions.add(region);
        }
    }
    
    public static void WriteProtectedRegion(ServerLevel world, ProtectedRegion region) throws CommandSyntaxException, IOException
    {
        if(!WorldHelper.IsOverworld(world))
            throw ERROR_NOT_OVERWORLD.create();
        
        if(!PathHelper.IsFileNameValid(region.Name))
            throw ERROR_INVALID_NAME.create();
        
        File regionsPath = WorldHelper.GetProtectedRegionsPath(world);
        SafeFile file = new SafeFile(regionsPath.getCanonicalPath(), region.Name + ".bin", true);
        
        region.Save(file);
    }
    
    public static void RemoveProtectedRegion(ServerLevel world, ProtectedRegion region) throws CommandSyntaxException, IOException
    {
        if(!WorldHelper.IsOverworld(world))
            throw ERROR_NOT_OVERWORLD.create();
        
        File regionsPath = WorldHelper.GetProtectedRegionsPath(world);
        File path = new File(regionsPath, region.Name + ".bin");
        File logFile = new File(regionsPath, region.Name + ".log");
        File backupFile = new File(regionsPath, region.Name + ".bin.old");
        
        if(path.exists() && !path.delete())
            throw new IOException("Failed to delete file '" + path.getCanonicalPath() + "'");
        
        if(logFile.exists() && !logFile.delete())
            throw new IOException("Failed to delete file '" + logFile.getCanonicalPath() + "'");
        
        if(backupFile.exists() && !backupFile.delete())
            throw new IOException("Failed to delete file '" + backupFile.getCanonicalPath() + "'");
        
        LoadProtectedRegions(world, true);
    }
    
    public static ProtectedRegion GetProtectedRegion(ServerLevel world, Vec3 position)
    {
        return GetProtectedRegion(world, new BlockPos(position));
    }
    
    public static ProtectedRegion GetProtectedRegion(ServerLevel world, BlockPos position)
    {
        if(!WorldHelper.IsOverworld(world))
            return null;
        
        LevelChunk chunk = world.getChunkAt(position);
        
        int worldRegionX = chunk.getPos().getRegionX();
        int worldRegionZ = chunk.getPos().getRegionZ();
        
        Vec3i worldRegion = new Vec3i(worldRegionX, 0, worldRegionZ);
        
        List<ProtectedRegion> regions = protectedRegions.get(worldRegion);
        
        if(regions == null)
            return null;
        
        for(ProtectedRegion region : regions)
        {
            Vec3i pos = new Vec3i(position.getX(), position.getY(), position.getZ());
            if(region.Contains(pos))
            {
                return region;
            }
        }
        
        return null;
    }
    
    public static ProtectedRegion ProtectRegion(ServerLevel world, String name, Vec3i start, Vec3i end, int protectionLevel, UUID owner, UUID protector) throws CommandSyntaxException
    {
        if(!WorldHelper.IsOverworld(world))
            throw ERROR_NOT_OVERWORLD.create();
        
        if(RegionExists(name))
            throw ERROR_ALREADY_EXISTS.create();
        
        Vec3i startCoord = new Vec3i(Math.min(start.getX(), end.getX()), Math.min(start.getY(), end.getY()), Math.min(start.getZ(), end.getZ()));
        Vec3i endCoord   = new Vec3i(Math.max(start.getX(), end.getX()), Math.max(start.getY(), end.getY()), Math.max(start.getZ(), end.getZ()));
        
        ProtectedRegion region = new ProtectedRegion(name, startCoord, endCoord, protectionLevel, owner, protector);
        
        try
        {
            WriteProtectedRegion(world, region);
        }
        catch (CommandSyntaxException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw ERROR_UNKNOWN.create();
        }
        
        LoadProtectedRegion(world, region);
        
        return region;
    }
    
    public static boolean RegionExists(String name)
    {
        return loadedRegions.contains(name.toLowerCase());
    }
}
