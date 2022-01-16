package com.mydoomsite.astreaserver.commands;

import net.minecraft.commands.*;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

import com.mojang.brigadier.CommandDispatcher;
import com.mydoomsite.astreaserver.datatypes.ProtectedRegion;
import com.mydoomsite.astreaserver.helpers.CommandHelper;
import com.mydoomsite.astreaserver.helpers.WorldHelper;
import com.mydoomsite.astreaserver.main.RegionProtector;

public class RegionTrustCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
            Commands.literal("regiontrust")
            .requires((cmdSource) -> {
                return cmdSource.hasPermission(2);
            })
            .then(Commands.argument("player", EntityArgument.player())
            .executes((context) -> {
                CommandSourceStack src = context.getSource();
                ServerLevel world = src.getLevel();
                ServerPlayer caller = src.getPlayerOrException();
                ServerPlayer player = EntityArgument.getPlayer(context, "player");
                
                if(!WorldHelper.IsOverworld(world))
                    throw RegionProtector.ERROR_NOT_OVERWORLD.create();
                
                ProtectedRegion region = RegionProtector.GetProtectedRegion(world, caller.position());
                if(region != null)
                {
                    if(!region.PlayerHasAdminAccess(caller))
                        throw RegionProtector.ERROR_NO_ACCESS.create();
                    
                    if(region.TrustedPlayers.add(player.getUUID()))
                    {
                        try
                        {
                            RegionProtector.WriteProtectedRegion(world, region);
                            CommandHelper.LogCommandSuccess(src, "Entrusted \u00A7a" + player.getName().getContents() + "\u00A7r with access to region \u00A7a" + region.Name, true, true);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            CommandHelper.LogCommandFailure(src, "Failed to update region \u00A7a" + region.Name + "\u00A7r. See the server log for details");
                        }
                    }
                    else
                    {
                        CommandHelper.LogCommandFailure(src, "Player \u00A7a" + player.getName().getContents() + "\u00A7r already has access to region \u00A7a" + region.Name);
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
