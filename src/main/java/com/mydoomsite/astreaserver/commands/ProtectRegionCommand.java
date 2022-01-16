package com.mydoomsite.astreaserver.commands;

import java.util.UUID;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mydoomsite.astreaserver.datatypes.ProtectedRegion;
import com.mydoomsite.astreaserver.helpers.CommandHelper;
import com.mydoomsite.astreaserver.helpers.RegionHelper;
import com.mydoomsite.astreaserver.main.RegionProtector;

import net.minecraft.commands.*;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;

public final class ProtectRegionCommand
{
    private static final SimpleCommandExceptionType ERROR_ALREADY_DRAWING = new SimpleCommandExceptionType(new TextComponent("You are already drawing a region. Type /cancelprotect to cancel."));
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
            Commands.literal("protectregion")
            .requires((cmdSource) -> {
                return cmdSource.hasPermission(2);
            })
            .then(Commands.argument("owner", EntityArgument.player())
            .then(Commands.argument("protection level", StringArgumentType.word())
                .suggests((context, builder) -> {
                    return SharedSuggestionProvider.suggest(ProtectedRegion.ProtectionLevels.keySet(), builder);
                })
            .then(Commands.argument("region name", StringArgumentType.greedyString()).executes((context) -> {
                CommandSourceStack src = context.getSource();
                ServerLevel world = src.getLevel();
                
                String name = StringArgumentType.getString(context, "region name");
                String protectionLevelStr = StringArgumentType.getString(context, "protection level");
                
                Integer protectionLevel = ProtectedRegion.ProtectionLevels.get(protectionLevelStr);
                if(protectionLevel == null)
                    throw RegionProtector.ERROR_INVALID_PROTECTION_LEVEL.create();
                
                ServerPlayer protector = src.getPlayerOrException();
                
                UUID ownerUuid = EntityArgument.getPlayer(context, "owner").getUUID();
                UUID protectorUuid = protector.getUUID();
                
                Vec3 start = protector.position();
                
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
