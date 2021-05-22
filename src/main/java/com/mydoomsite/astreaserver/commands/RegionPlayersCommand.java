package com.mydoomsite.astreaserver.commands;

import java.util.UUID;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;

import com.mojang.brigadier.CommandDispatcher;
import com.mydoomsite.astreaserver.datatypes.ProtectedRegion;
import com.mydoomsite.astreaserver.helpers.CommandHelper;
import com.mydoomsite.astreaserver.helpers.PlayerHelper;
import com.mydoomsite.astreaserver.helpers.WorldHelper;
import com.mydoomsite.astreaserver.main.RegionProtector;

public class RegionPlayersCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(
            Commands.literal("regionplayers")
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
                    int errors = 0;
                    String players = "";
                    
                    try
                    {
                        players = PlayerHelper.GetPlayerName(region.Owner);
                    }
                    catch (Exception e)
                    {
                        errors++;
                        e.printStackTrace();
                    }
                    
                    for(UUID uuid : region.TrustedPlayers)
                    {
                        try
                        {
                            players += ", " + PlayerHelper.GetPlayerName(uuid);
                        }
                        catch (Exception e)
                        {
                            errors++;
                            e.printStackTrace();
                        }
                    }
                    
                    if(errors > 0)
                        players += " \u00A7c(+" + errors + " more)";
                    
                    CommandHelper.LogCommandSuccess(src,
                        "Players with access to \"\u00A7a" + region.Name + "\u00A7r\":\n" +
                        "\u00A7e" + players,
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
