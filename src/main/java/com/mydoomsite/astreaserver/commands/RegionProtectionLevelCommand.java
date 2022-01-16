package com.mydoomsite.astreaserver.commands;

import net.minecraft.commands.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mydoomsite.astreaserver.datatypes.ProtectedRegion;
import com.mydoomsite.astreaserver.helpers.CommandHelper;
import com.mydoomsite.astreaserver.helpers.WorldHelper;
import com.mydoomsite.astreaserver.main.RegionProtector;

public class RegionProtectionLevelCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
            Commands.literal("regionprotectionlevel")
            .requires((cmdSource) -> {
                return cmdSource.hasPermission(2);
            })
            .executes((context) -> {
                CommandSourceStack src = context.getSource();
                ServerLevel world = src.getLevel();
                ServerPlayer caller = src.getPlayerOrException();
                
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
                    return SharedSuggestionProvider.suggest(ProtectedRegion.ProtectionLevels.keySet(), builder);
                })
            .executes((context) -> {
                CommandSourceStack src = context.getSource();
                ServerLevel world = src.getLevel();
                ServerPlayer caller = src.getPlayerOrException();
                
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
