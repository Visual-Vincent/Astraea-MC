package com.mydoomsite.astreaserver.commands;

import java.util.UUID;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mydoomsite.astreaserver.helpers.CommandHelper;
import com.mydoomsite.astreaserver.helpers.RegionHelper;

public class CancelProtectCommand
{
    private static final SimpleCommandExceptionType ERROR_NOT_DRAWING = new SimpleCommandExceptionType(new StringTextComponent("You are not drawing a region"));
    
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(
            Commands.literal("cancelprotect")
            .requires((cmdSource) -> {
                return cmdSource.hasPermission(2);
            }).executes((context) -> {
                CommandSource src = context.getSource();
                ServerPlayerEntity protector = src.getPlayerOrException();
                UUID protectorUuid = protector.getUUID();
                
                if(RegionHelper.CancelProtectRegion(protectorUuid))
                {
                    CommandHelper.LogCommandSuccess(src, "\u00A7aCancelled protected region drawing", false, false);
                }
                else
                {
                    throw ERROR_NOT_DRAWING.create();
                }
                
                return 1;
            })
        );
    }
}
