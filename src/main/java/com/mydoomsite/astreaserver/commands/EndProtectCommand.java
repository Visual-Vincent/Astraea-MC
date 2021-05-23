package com.mydoomsite.astreaserver.commands;

import java.util.UUID;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mydoomsite.astreaserver.datatypes.ProtectedRegion;
import com.mydoomsite.astreaserver.helpers.CommandHelper;
import com.mydoomsite.astreaserver.helpers.RegionHelper;

public class EndProtectCommand
{
    private static final SimpleCommandExceptionType ERROR_NOT_DRAWING = new SimpleCommandExceptionType(new StringTextComponent("You are not drawing a region. Type /protectregion to start"));
    
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(
            Commands.literal("endprotect")
            .requires((cmdSource) -> {
                return cmdSource.hasPermission(2);
            }).executes((context) -> {
                CommandSource src = context.getSource();
                ServerWorld world = src.getLevel();
                ServerPlayerEntity protector = src.getPlayerOrException();
                
                UUID protectorUuid = protector.getUUID();
                Vector3d end = protector.position();
                
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
