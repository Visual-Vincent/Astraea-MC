package com.mydoomsite.astreaserver.commands;

import java.util.UUID;

import net.minecraft.commands.*;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mydoomsite.astreaserver.helpers.CommandHelper;
import com.mydoomsite.astreaserver.helpers.RegionHelper;

public class CancelProtectCommand
{
    private static final SimpleCommandExceptionType ERROR_NOT_DRAWING = new SimpleCommandExceptionType(new TextComponent("You are not drawing a region"));
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
            Commands.literal("cancelprotect")
            .requires((cmdSource) -> {
                return cmdSource.hasPermission(2);
            }).executes((context) -> {
                CommandSourceStack src = context.getSource();
                ServerPlayer protector = src.getPlayerOrException();
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
