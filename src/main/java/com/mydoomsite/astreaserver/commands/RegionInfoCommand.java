package com.mydoomsite.astreaserver.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;

import com.mojang.brigadier.CommandDispatcher;import com.mydoomsite.astreaserver.datatypes.ProtectedRegion;
import com.mydoomsite.astreaserver.helpers.CommandHelper;
import com.mydoomsite.astreaserver.helpers.PlayerHelper;
import com.mydoomsite.astreaserver.helpers.WorldHelper;
import com.mydoomsite.astreaserver.main.RegionProtector;

public class RegionInfoCommand
{
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(
			Commands.literal("regioninfo")
			.requires((cmdSource) -> {
				return cmdSource.hasPermission(2);
			}).executes((context) -> {
				CommandSource src = context.getSource();
				ServerWorld world = src.getLevel();
				
				if(!WorldHelper.IsOverworld(world))
					throw RegionProtector.ERROR_NOT_OVERWORLD.create();
				
				ServerPlayerEntity caller = src.getPlayerOrException();
				
				ProtectedRegion region = RegionProtector.GetProtectedRegion(world, caller.position());
				if(region != null)
				{
					String owner = "<error>";
					try { owner = PlayerHelper.GetPlayerName(region.Owner); } catch (Exception e) { e.printStackTrace(); }
					
					String protector = "<error>";
					try { protector = PlayerHelper.GetPlayerName(region.Protector); } catch (Exception e) { e.printStackTrace(); }
					
					CommandHelper.LogCommandSuccess(src, 
						"Region \"\u00A7a" + region.Name + "\u00A7r\"\n" +
						"\u00A7eOwner: \u00A7a" + owner + "\n" +
						"\u00A7eProtector: \u00A7a" + protector + "\n" +
						"\u00A7eProtection: \u00A7a" + ProtectedRegion.GetProtectionLevelName(region.ProtectionLevel) + "\n" +
						"\u00A7fType /regionplayers to see who has access to this region",
					false, false);
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
