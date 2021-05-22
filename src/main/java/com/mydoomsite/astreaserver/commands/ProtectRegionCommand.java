package com.mydoomsite.astreaserver.commands;

import java.util.UUID;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mydoomsite.astreaserver.datatypes.ProtectedRegion;
import com.mydoomsite.astreaserver.helpers.CommandHelper;
import com.mydoomsite.astreaserver.helpers.RegionHelper;
import com.mydoomsite.astreaserver.main.RegionProtector;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

public final class ProtectRegionCommand
{
    private static final SimpleCommandExceptionType ERROR_ALREADY_DRAWING = new SimpleCommandExceptionType(new StringTextComponent("You are already drawing a region. Type /cancelprotect to cancel."));
    
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(
            Commands.literal("protectregion")
            .requires((cmdSource) -> {
                return cmdSource.hasPermission(2);
            })
            .then(Commands.argument("owner", EntityArgument.player())
            .then(Commands.argument("protection level", StringArgumentType.word())
                .suggests((context, builder) -> {
                    return ISuggestionProvider.suggest(ProtectedRegion.ProtectionLevels.keySet(), builder);
                })
            .then(Commands.argument("region name", StringArgumentType.greedyString()).executes((context) -> {
                CommandSource src = context.getSource();
                ServerWorld world = src.getLevel();
                
                String name = StringArgumentType.getString(context, "region name");
                String protectionLevelStr = StringArgumentType.getString(context, "protection level");
                
                Integer protectionLevel = ProtectedRegion.ProtectionLevels.get(protectionLevelStr);
                if(protectionLevel == null)
                    throw RegionProtector.ERROR_INVALID_PROTECTION_LEVEL.create();
                
                ServerPlayerEntity protector = src.getPlayerOrException();
                
                UUID ownerUuid = EntityArgument.getPlayer(context, "owner").getUUID();
                UUID protectorUuid = protector.getUUID();
                
                Vector3d start = protector.position();
                
                if(RegionHelper.BeginProtectRegion(world, name, start, protectionLevel, ownerUuid, protectorUuid))
                {
                    // Begun drawing a new region
                    CommandHelper.LogCommandSuccess(src, "Started drawing region. Type /endprotect to complete, or /cancelprotect to cancel.", false, false);
                }
                else
                {
                    // A region drawing is already in progress by this player
                    throw ERROR_ALREADY_DRAWING.create();
                }
                
                return 1;
            }))))
        );
    }
}
