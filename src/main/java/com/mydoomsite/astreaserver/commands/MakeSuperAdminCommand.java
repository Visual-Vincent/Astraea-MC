package com.mydoomsite.astreaserver.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;

import com.mojang.brigadier.CommandDispatcher;
import com.mydoomsite.astreaserver.helpers.CommandHelper;
import com.mydoomsite.astreaserver.helpers.PlayerHelper;

public class MakeSuperAdminCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(
            Commands.literal("makesuperadmin")
            .requires((cmdSource) -> {
                if(cmdSource.getEntity() != null)
                {
                    try { return PlayerHelper.IsSuperAdmin(cmdSource.getPlayerOrException().getUUID()); } catch (Exception e) {}
                }
                
                return CommandHelper.IsExecutedByServer(cmdSource);
            })
            .then(Commands.argument("player", EntityArgument.player())
            .executes((context) -> {
                CommandSource src = context.getSource();
                ServerPlayerEntity player = EntityArgument.getPlayer(context, "player");
                
                try
                {
                    if(PlayerHelper.MakeSuperAdmin(player.getUUID()))
                    {
                        CommandHelper.LogCommandSuccess(src, "Made " + player.getName().getContents() + " Super Admin", true, true);
                    }
                    else
                    {
                        CommandHelper.LogCommandFailure(src, player.getName().getContents() + " is already a Super Admin");
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    CommandHelper.LogCommandFailure(src, "An error occurred while saving the super admins. See the server log for details");
                }
                
                return 1;
            })
        ));
    }
}
