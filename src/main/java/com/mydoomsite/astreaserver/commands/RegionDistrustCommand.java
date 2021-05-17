package com.mydoomsite.astreaserver.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;

import com.mojang.brigadier.CommandDispatcher;
import com.mydoomsite.astreaserver.datatypes.ProtectedRegion;
import com.mydoomsite.astreaserver.helpers.CommandHelper;
import com.mydoomsite.astreaserver.helpers.WorldHelper;
import com.mydoomsite.astreaserver.main.RegionProtector;

public class RegionDistrustCommand
{
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(
			Commands.literal("regiondistrust")
			.requires((cmdSource) -> {
				return cmdSource.hasPermission(2);
			})
			.then(Commands.argument("player", EntityArgument.player())
			.executes((context) -> {
				CommandSource src = context.getSource();
				ServerWorld world = src.getLevel();
				ServerPlayerEntity caller = src.getPlayerOrException();
				ServerPlayerEntity player = EntityArgument.getPlayer(context, "player");
				
				if(!WorldHelper.IsOverworld(world))
					throw RegionProtector.ERROR_NOT_OVERWORLD.create();
				
				ProtectedRegion region = RegionProtector.GetProtectedRegion(world, caller.position());
				if(region != null)
				{
					if(!region.PlayerHasAdminAccess(caller))
						throw RegionProtector.ERROR_NO_ACCESS.create();
					
					if(region.TrustedPlayers.remove(player.getUUID()))
					{
						try
						{
							RegionProtector.WriteProtectedRegion(world, region);
							CommandHelper.LogCommandSuccess(src, "\u00A7a" + player.getName().getContents() + "\u00A7f no longer has access to region \u00A7a" + region.Name, true, true);
						}
						catch (Exception e)
						{
							e.printStackTrace();
							CommandHelper.LogCommandFailure(src, "Failed to update region \u00A7a" + region.Name + "\u00A7r. See the server log for details");
						}
					}
					else
					{
						CommandHelper.LogCommandFailure(src, "Player \u00A7a" + player.getName().getContents() + "\u00A7r does not have access to region \u00A7a" + region.Name);
					}
				}
				else
				{
					CommandHelper.LogCommandFailure(src, "Region is not protected");
				}
				
				return 1;
			}))
		);
	}
}
