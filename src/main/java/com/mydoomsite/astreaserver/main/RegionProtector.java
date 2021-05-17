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
import com.mydoomsite.astreaserver.helpers.WorldHelper;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;

public final class RegionProtector
{
	public static final SimpleCommandExceptionType ERROR_NOT_OVERWORLD = new SimpleCommandExceptionType(new StringTextComponent("Region protection is only possible in the Overworld"));
	public static final SimpleCommandExceptionType ERROR_ALREADY_EXISTS = new SimpleCommandExceptionType(new StringTextComponent("A region with that name already exists"));
	public static final SimpleCommandExceptionType ERROR_NO_ACCESS = new SimpleCommandExceptionType(new StringTextComponent("You do not have permission to modify this region"));
	public static final SimpleCommandExceptionType ERROR_INVALID_PROTECTION_LEVEL = new SimpleCommandExceptionType(new StringTextComponent("Invalid protection level"));
	public static final SimpleCommandExceptionType ERROR_UNKNOWN = new SimpleCommandExceptionType(new StringTextComponent("An unknown error occurred. Please check the server log for details"));
	
	// Possible TODO: Implement QuadTree to minimize iterations?
	//                Alternatively just add chunks to a nested Map<> (will increase memory usage, but this is server so it should be fine)

	//             WorldRegionCoords2D
	private static final Map<Vector3i, List<ProtectedRegion>> protectedRegions = new ConcurrentHashMap<>();
	private static final HashSet<String> loadedRegions = new HashSet<>(); // Not thread-safe!
	
	private static Collection<Vector3i> GetRegionsForChunks(Chunk start, Chunk end)
	{
		ArrayList<Vector3i> regions = new ArrayList<>();
		
		int startRegionX = start.getPos().getRegionX();
		int startRegionZ = start.getPos().getRegionZ();
		
		if(start == end)
		{
			regions.add(new Vector3i(startRegionX, 0, startRegionZ));
			return regions;
		}
		
		int endRegionX = end.getPos().getRegionX();
		int endRegionZ = end.getPos().getRegionZ();
		
		for(int x = Math.min(startRegionX, endRegionX); x <= Math.max(startRegionX, endRegionX); x++)
		{
			for(int z = Math.min(startRegionZ, endRegionZ); z <= Math.max(startRegionZ, endRegionZ); z++)
			{
				regions.add(new Vector3i(x, 0, z));
			}
		}
		
		return regions;
	}
	
	public static void LoadProtectedRegions(ServerWorld world, boolean ignoreErrors) throws CommandSyntaxException, IOException
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
				ProtectedRegion region = ProtectedRegion.Load(file.getAbsolutePath());
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
	
	private static void LoadProtectedRegion(ServerWorld world, ProtectedRegion region) throws CommandSyntaxException
	{
		if(!WorldHelper.IsOverworld(world))
			throw ERROR_NOT_OVERWORLD.create();
		
		if(RegionExists(region.Name))
			throw ERROR_ALREADY_EXISTS.create();
		
		BlockPos startPos = new BlockPos(region.Start);
		BlockPos endPos   = new BlockPos(region.End);
		
		Chunk startChunk = world.getChunkAt(startPos);
		Chunk endChunk   = world.getChunkAt(endPos);
		
		Collection<Vector3i> worldRegions = GetRegionsForChunks(startChunk, endChunk);
		
		loadedRegions.add(region.Name.toLowerCase());
		
		for(Vector3i worldRegion : worldRegions)
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
	
	public static void WriteProtectedRegion(ServerWorld world, ProtectedRegion region) throws CommandSyntaxException, IOException
	{
		if(!WorldHelper.IsOverworld(world))
			throw ERROR_NOT_OVERWORLD.create();
		
		File regionsPath = WorldHelper.GetProtectedRegionsPath(world);
		File path = new File(regionsPath, region.Name + ".bin");
		region.Save(path.getAbsolutePath());
	}
	
	public static void RemoveProtectedRegion(ServerWorld world, ProtectedRegion region) throws CommandSyntaxException, IOException
	{
		if(!WorldHelper.IsOverworld(world))
			throw ERROR_NOT_OVERWORLD.create();
		
		File regionsPath = WorldHelper.GetProtectedRegionsPath(world);
		File path = new File(regionsPath, region.Name + ".bin");
		File logFile = new File(regionsPath, region.Name + ".log");
		
		if(path.exists() && !path.delete())
			throw new IOException("Failed to delete file " + path.getAbsolutePath());
		
		if(logFile.exists() && !logFile.delete())
			throw new IOException("Failed to delete file " + logFile.getAbsolutePath());
		
		LoadProtectedRegions(world, true);
	}
	
	public static ProtectedRegion GetProtectedRegion(ServerWorld world, Vector3d position)
	{
		return GetProtectedRegion(world, new BlockPos(position));
	}
	
	public static ProtectedRegion GetProtectedRegion(ServerWorld world, BlockPos position)
	{
		if(!WorldHelper.IsOverworld(world))
			return null;
		
		Chunk chunk = world.getChunkAt(position);
		
		int worldRegionX = chunk.getPos().getRegionX();
		int worldRegionZ = chunk.getPos().getRegionZ();
		
		Vector3i worldRegion = new Vector3i(worldRegionX, 0, worldRegionZ);
		
		List<ProtectedRegion> regions = protectedRegions.get(worldRegion);
		
		if(regions == null)
			return null;
		
		for(ProtectedRegion region : regions)
		{
			Vector3i pos = new Vector3i(position.getX(), position.getY(), position.getZ());
			if(region.Contains(pos))
			{
				return region;
			}
		}
		
		return null;
	}
	
	public static ProtectedRegion ProtectRegion(ServerWorld world, String name, Vector3i start, Vector3i end, int protectionLevel, UUID owner, UUID protector) throws CommandSyntaxException
	{
		if(!WorldHelper.IsOverworld(world))
			throw ERROR_NOT_OVERWORLD.create();
		
		if(RegionExists(name))
			throw ERROR_ALREADY_EXISTS.create();
		
		Vector3i startCoord = new Vector3i(Math.min(start.getX(), end.getX()), Math.min(start.getY(), end.getY()), Math.min(start.getZ(), end.getZ()));
		Vector3i endCoord   = new Vector3i(Math.max(start.getX(), end.getX()), Math.max(start.getY(), end.getY()), Math.max(start.getZ(), end.getZ()));
		
		ProtectedRegion region = new ProtectedRegion(name, startCoord, endCoord, protectionLevel, owner, protector);
		
		try
		{
			WriteProtectedRegion(world, region);
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
