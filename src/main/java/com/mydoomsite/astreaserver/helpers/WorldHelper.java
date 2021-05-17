package com.mydoomsite.astreaserver.helpers;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Dimension;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public final class WorldHelper
{
	@Nullable
	public static MinecraftServer GetWorldServer(IWorld world)
	{
		if(world == null || !(world instanceof ServerWorld))
			return null;
		
		return ((ServerWorld)world).getServer();
	}
	
	public static File GetServerPath(IWorld world)
	{
		if(world == null)
			throw new NullPointerException("Argument 'world' cannot be null");
		
		MinecraftServer server = GetWorldServer(world);
		
		if(server == null)
			throw new NullPointerException("No MinecraftServer instance found in " + world.getClass().getName());
		
		return server.getServerDirectory();
	}
	
	public static File GetAstreaServerPath(IWorld world) throws IOException
	{
		return ServerHelper.GetAstreaServerPath(WorldHelper.GetWorldServer(world));
	}
	
	public static File GetAstreaWorldPath(IWorld world) throws IOException
	{
		return ServerHelper.GetAstreaWorldPath(WorldHelper.GetWorldServer(world));
	}
	
	public static File GetProtectedRegionsPath(IWorld world) throws IOException
	{
		return ServerHelper.GetProtectedRegionsPath(WorldHelper.GetWorldServer(world));
	}
	
	public static boolean IsOverworld(World world)
	{
		return world.dimension().location().getPath() == Dimension.OVERWORLD.location().getPath();
	}
}
