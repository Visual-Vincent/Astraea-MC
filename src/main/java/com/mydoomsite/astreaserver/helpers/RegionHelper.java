package com.mydoomsite.astreaserver.helpers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.server.ServerWorld;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mydoomsite.astreaserver.datatypes.ProtectedRegion;
import com.mydoomsite.astreaserver.main.RegionProtector;

public final class RegionHelper
{
	private static Map<UUID, ProtectedRegion> wipRegions = new ConcurrentHashMap<>();
	
	public static boolean BeginProtectRegion(ServerWorld world, String name, Vector3d start, int protectionLevel, UUID owner, UUID protector) throws CommandSyntaxException
	{
		if(!WorldHelper.IsOverworld(world))
			throw RegionProtector.ERROR_NOT_OVERWORLD.create();
		
		if(wipRegions.containsKey(protector))
			return false;
		
		if(RegionProtector.RegionExists(name))
			throw RegionProtector.ERROR_ALREADY_EXISTS.create();
		
		ProtectedRegion region = new ProtectedRegion(name, new Vector3i(start.x, start.y, start.z), new Vector3i(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE), protectionLevel, owner, protector);
		wipRegions.put(protector, region);
		
		return true;
	}
	
	public static ProtectedRegion EndProtectRegion(ServerWorld world, Vector3d end, UUID protector) throws CommandSyntaxException
	{
		if(!WorldHelper.IsOverworld(world))
			throw RegionProtector.ERROR_NOT_OVERWORLD.create();
		
		if(!wipRegions.containsKey(protector))
			return null;
		
		ProtectedRegion wip = wipRegions.get(protector);
		CancelProtectRegion(protector);
		
		ProtectedRegion region = RegionProtector.ProtectRegion(world, wip.Name, wip.Start, new Vector3i(end.x, end.y, end.z), wip.ProtectionLevel, wip.Owner, wip.Protector);
		return region;
	}
	
	public static boolean CancelProtectRegion(UUID protector)
	{
		if(!wipRegions.containsKey(protector))
			return false;
		
		wipRegions.remove(protector);
		return true;
	}
}
