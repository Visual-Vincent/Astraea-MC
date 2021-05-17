package com.mydoomsite.astreaserver.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mydoomsite.astreaserver.datatypes.ProtectedRegion;
import com.mydoomsite.astreaserver.helpers.CommandHelper;
import com.mydoomsite.astreaserver.helpers.WorldHelper;
import com.mydoomsite.astreaserver.main.RegionProtector;

public class RegionProtectionLevelCommand
{
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(
			Commands.literal("regionprotectionlevel")
			.requires((cmdSource) -> {
				return cmdSource.hasPermission(2);
			})
			.executes((context) -> {
				CommandSource src = context.getSource();
				ServerWorld world = src.getLevel();
				ServerPlayerEntity caller = src.getPlayerOrException();
				
				if(!WorldHelper.IsOverworld(world))
					throw RegionProtector.ERROR_NOT_OVERWORLD.create();
				
				ProtectedRegion region = RegionProtector.GetProtectedRegion(world, caller.position());
				if(region != null)
				{
					String message = 
						"Region \"\u00A7a" + region.Name + "\u00A7r\"\n" +
						"\u00A7eProtection: \u00A7a" + ProtectedRegion.GetProtectionLevelName(region.ProtectionLevel);
					CommandHelper.LogCommandSuccess(src, message, false, false);
				}
				else
				{
					CommandHelper.LogCommandFailure(src, "Region is not protected");
				}
				
				return 1;
			})
			.then(Commands.argument("new level", StringArgumentType.word())
				.suggests((context, builder) -> {
					return ISuggestionProvider.suggest(ProtectedRegion.ProtectionLevels.keySet(), builder);
				})
			.executes((context) -> {
				CommandSource src = context.getSource();
				ServerWorld world = src.getLevel();
				ServerPlayerEntity caller = src.getPlayerOrException();
				
				if(!WorldHelper.IsOverworld(world))
					throw RegionProtector.ERROR_NOT_OVERWORLD.create();
				
				String protectionLevelStr = StringArgumentType.getString(context, "new level");
				
				Integer protectionLevel = ProtectedRegion.ProtectionLevels.get(protectionLevelStr);
				if(protectionLevel == null)
					throw RegionProtector.ERROR_INVALID_PROTECTION_LEVEL.create();
				
				ProtectedRegion region = RegionProtector.GetProtectedRegion(world, caller.position());
				if(region != null)
				{
					if(!region.PlayerHasAdminAccess(caller))
						throw RegionProtector.ERROR_NO_ACCESS.create();
					
					region.ProtectionLevel = protectionLevel;
					try
					{
						RegionProtector.WriteProtectedRegion(world, region);
						CommandHelper.LogCommandSuccess(src, "Protection for region \u00A7a" + region.Name + "\u00A7r changed to: \u00A7e" + ProtectedRegion.GetProtectionLevelName(region.ProtectionLevel), true, true);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						CommandHelper.LogCommandFailure(src, "Failed to update region \u00A7a" + region.Name + "\u00A7r. See the server log for details");
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
