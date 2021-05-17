package com.mydoomsite.astreaserver.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;

import com.mojang.brigadier.CommandDispatcher;
import com.mydoomsite.astreaserver.datatypes.ProtectedRegion;
import com.mydoomsite.astreaserver.helpers.CommandHelper;
import com.mydoomsite.astreaserver.helpers.WorldHelper;
import com.mydoomsite.astreaserver.main.RegionProtector;

public class UnprotectRegionCommand
{
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(
			Commands.literal("unprotectregion")
			.requires((cmdSource) -> {
				return cmdSource.hasPermission(2);
			}).executes((context) -> {
				CommandSource src = context.getSource();
				ServerWorld world = src.getLevel();
				ServerPlayerEntity caller = src.getPlayerOrException();
				
				if(!WorldHelper.IsOverworld(world))
					throw RegionProtector.ERROR_NOT_OVERWORLD.create();
				
				ProtectedRegion region = RegionProtector.GetProtectedRegion(world, caller.position());
				if(region != null)
				{
					if(!region.PlayerHasAdminAccess(caller))
						throw RegionProtector.ERROR_NO_ACCESS.create();
					
					try
					{
						RegionProtector.RemoveProtectedRegion(world, region);
						CommandHelper.LogCommandSuccess(src, "Successfully removed region \u00A7a" + region.Name, true, true);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						CommandHelper.LogCommandFailure(src, "Failed to remove region \u00A7a" + region.Name + "\u00A7r. See the server log for details");
					}
				}
				else
				{
					CommandHelper.LogCommandFailure(src, "Region is not protected");
				}
				
				return 1;
			})
		);
	}
}
