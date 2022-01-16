package com.mydoomsite.astreaserver.commands;

import net.minecraft.commands.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import com.mojang.brigadier.CommandDispatcher;
import com.mydoomsite.astreaserver.datatypes.ProtectedRegion;
import com.mydoomsite.astreaserver.helpers.CommandHelper;
import com.mydoomsite.astreaserver.helpers.WorldHelper;
import com.mydoomsite.astreaserver.main.RegionProtector;

public class UnprotectRegionCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
            Commands.literal("unprotectregion")
            .requires((cmdSource) -> {
                return cmdSource.hasPermission(2);
            }).executes((context) -> {
                CommandSourceStack src = context.getSource();
                ServerLevel world = src.getLevel();
                ServerPlayer caller = src.getPlayerOrException();
                
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
