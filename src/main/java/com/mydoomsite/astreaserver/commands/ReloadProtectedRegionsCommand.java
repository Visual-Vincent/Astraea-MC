package com.mydoomsite.astreaserver.commands;

import net.minecraft.commands.*;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mydoomsite.astreaserver.helpers.CommandHelper;
import com.mydoomsite.astreaserver.main.RegionProtector;

public class ReloadProtectedRegionsCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
            Commands.literal("reloadprotectedregions")
            .requires((cmdSource) -> {
                return cmdSource.hasPermission(2);
            }).executes((context) -> {
                CommandSourceStack src = context.getSource();
                
                try
                {
                    RegionProtector.LoadProtectedRegions(src.getLevel(), true);
                }
                catch (CommandSyntaxException e)
                {
                    throw e;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    throw RegionProtector.ERROR_UNKNOWN.create();
                }
                
                try
                {
                    CommandHelper.LogCommandSuccess(src, "\u00A7aSuccessfully reloaded protected regions", true, true);
                }
                catch (Exception ex) {}
                
                return 1;
            })
        );
    }
}
