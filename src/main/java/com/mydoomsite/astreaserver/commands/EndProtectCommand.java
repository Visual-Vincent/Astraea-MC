package com.mydoomsite.astreaserver.commands;

import java.util.UUID;

import net.minecraft.commands.*;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mydoomsite.astreaserver.datatypes.ProtectedRegion;
import com.mydoomsite.astreaserver.helpers.CommandHelper;
import com.mydoomsite.astreaserver.helpers.RegionHelper;

public class EndProtectCommand
{
    private static final SimpleCommandExceptionType ERROR_NOT_DRAWING = new SimpleCommandExceptionType(new TextComponent("You are not drawing a region. Type /protectregion to start"));
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
            Commands.literal("endprotect")
            .requires((cmdSource) -> {
                return cmdSource.hasPermission(2);
            }).executes((context) -> {
                CommandSourceStack src = context.getSource();
                ServerLevel world = src.getLevel();
                ServerPlayer protector = src.getPlayerOrException();
                
                UUID protectorUuid = protector.getUUID();
                Vec3 end = protector.position();
                
                ProtectedRegion region = RegionHelper.EndProtectRegion(world, end, protectorUuid);
                if(region != null)
                {
                    CommandHelper.LogCommandSuccess(src, "Protected region \u00A7a\"" + region.Name + "\"\u00A7r created successfully", true, true);
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
